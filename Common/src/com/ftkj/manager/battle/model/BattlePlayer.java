package com.ftkj.manager.battle.model;

import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.util.lambda.LBFloat;
import com.ftkj.util.tuple.Tuple2F;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年2月16日
 * 战斗球员数据
 */
public class BattlePlayer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(BattlePlayer.class);
    private static final Logger scorelog = LoggerFactory.getLogger("com.ftkj.manager.battle.model.BattlePlayer_Score");
    private final EActionType OCAP = EActionType.ocap;
    private final EActionType DCAP = EActionType.dcap;
    /** tid */
    private long tid;
    /** 球员唯一ID */
    private int pid;
    /** 球员配置数据 */
    private PlayerBean player;
    /** 当前体力 */
    private float power;
    /** 各项属性加成,一般存放战斗临时buff之类的数据和最基础的球员核心属性 */
    private Map<AbilityType, PlayerAbility> abilities;
    /** 汇总战力，有变动时更新该属性. 体力对各项加成的影响在汇总时计算 */
    private PlayerAbility ability;
    /** 球员实时比赛行为统计数据 */
    private PlayerActStat realTimeActionStats;
    /** 球员小节行为统计数据 */
    private PlayerStepActStat stepActionStats;
    /** 球员技能 */
    private BattlePlayerSkill playerSkill;
    /** 是否最新数据 */
    private volatile boolean newestData;
    /** 球员位置 */
    private EPlayerPosition playerPosition;
    /** 场上位置 */
    private volatile EPlayerPosition lineupPos;
    //	private int startRound;
    /** 荣誉头像ID */
    private int honorLogo;
    private int honorLogoQuality;
    /** 反击? */
    private static final float fjrate = 80f;
    /**  */
    private static final float kdrate = 1.5f;
    /** 提示id和已触发次数 */
    private Map<Integer, Integer> hitNums;

    public BattlePlayer(long tid, int pid, PlayerBean player,
                        EPlayerPosition playerPosition, EPlayerPosition lineupPosition, int honorLogo,
                        int honorLogoQuality, Skill attackSkill, Skill defendSkill, PlayerAbility baseAbility, int maxSkillPower) {
        super();
        this.tid = tid;
        this.pid = pid;
        this.player = player;
        this.playerPosition = playerPosition;
        this.lineupPos = lineupPosition;
        this.honorLogo = honorLogo;
        this.honorLogoQuality = honorLogoQuality;
        this.abilities = Maps.newHashMap();
        this.abilities.put(baseAbility.getType(), baseAbility);
        this.ability = new PlayerAbility(AbilityType.Player, this.player.getPlayerRid());
        this.realTimeActionStats = new PlayerActStat(player.getPlayerRid());
        this.stepActionStats = new PlayerStepActStat(player.getPlayerRid());
        this.newestData = false;
        this.playerSkill = new BattlePlayerSkill(this.player.getPlayerRid(), attackSkill, defendSkill, maxSkillPower);
        sumAllAbilities();
        this.power = this.ability.getAttrData(EActionType.power);
        upRtAndStepActionStats(EBattleStep.First_Period, EActionType.update_power, (int) power);
        //		this.startRound = 0;
        //计算一次球员属性
        reloadAbility(lineupPosition);
    }

    private void sumAllAbilities() {
        this.abilities.values().forEach(ab ->
                ab.getAttrs().forEach((k, v) -> this.ability.addAttr(k, v)));
    }

    /** 重新计算一次球员数据 */
    public void reloadAbility(EPlayerPosition position) {
        if (this.newestData) {
            return;//已经是最新数据。无需计算
        }
        final Float srcOcap = ability.setAttr(OCAP, 0f);
        final Float srcDcap = ability.setAttr(DCAP, 0f);
        final float positionCap = PlayerConsole.getPositionAPI().getCap(position, playerPosition);
        final PlayerAbility ta = abilities.get(AbilityType.Tactic_Retrain);
        final float otCapRate = ta != null ? ta.get(OCAP) : 1f;
        final float dtCapRate = ta != null ? ta.get(DCAP) : 1f;
        final float power = getPower(this.power);

        LBFloat tempOCap = new LBFloat();
        LBFloat tempDCap = new LBFloat();
        abilities.values().stream()
                .filter(ab -> ab.getType() != AbilityType.Tactic_Retrain)
                .forEach(ab -> {
                    final float oocap = ab.getAttrData(OCAP);
                    final float odcap = ab.getAttrData(DCAP);
                    tempOCap.sum(oocap);
                    tempDCap.sum(odcap);
                    if (log.isTraceEnabled()) {
                        log.trace("calc cap pr sum module. tid {} prid {} m {} add {} {} -> {} {}",
                                tid, getRid(), ab.getType(), oocap, odcap, tempOCap.getVal(), tempDCap.getVal());
                    }
                });

        ability.setAttr(OCAP, tempOCap.getVal() * power * positionCap * (1 + otCapRate) / 10000.0f);
        ability.setAttr(DCAP, tempDCap.getVal() * power * positionCap * (1 + dtCapRate) / 10000.0f);

        if (log.isDebugEnabled()) {
            log.debug("calc cap pr. tid {} prid {} power {} poscap {} otr {} dtr {}. cap {} {} -> {} {}",
                    tid, getRid(), power, positionCap, otCapRate, dtCapRate,
                    srcOcap, srcDcap, ability.get(OCAP), ability.get(DCAP));
        }
        this.newestData = true;
    }

    private float getPower(float power) {
        if (power >= 60) {
            power = Math.round((100 + power) * 1.0f / 2.0f);
        } else {
            power = Math.round(4 * power * 1.0f / 3.0f);
        }
        if (power < 10) {
            power = 10;
        }
        return power;
    }

    /** 士气加成 */
    public Tuple2F reloadAbilityWithMorale(int morale, int initMorale, float moraleRate) {
        float srcacap = ability.getAttrData(OCAP);
        float srcdcap = ability.getAttrData(DCAP);
        float attack = getBattleMoraleCap(morale, initMorale, srcacap, moraleRate);
        float defend = getBattleMoraleCap(morale, initMorale, srcdcap, moraleRate);
        //        if (log.isDebugEnabled()) {
        //            log.debug("calc cap pr byMorale. tid {} pid {} morale {} initMorale {} acap {} dcap {}",
        //                    tid, getPlayerId(), morale, initMorale, attack, defend);
        //        }
        return new Tuple2F(srcacap + attack, srcdcap + defend);
    }

    public static float getBattleMoraleCap(int morale, int initMorale, float cap, float moraleRate) {
        return cap * ((morale - initMorale) * moraleRate);
    }

    public BattlePlayerSkill getPlayerSkill() {
        return playerSkill;
    }

    public int getSkillPower() {
        return playerSkill == null ? 0 : playerSkill.getSkillPower();
    }

    /** 获得除战术加成外的所有能力加成 */
    public int getBaseCap() {
        return abilities.values().stream()
                .filter(pb -> pb.getType() != AbilityType.Tactic_Retrain)
                //                .filter(pb -> pb.getType() != AbilityType.Tactic_Defense)
                //                .filter(pb -> pb.getType() != AbilityType.Tactic_Offense)
                .mapToInt(pb -> pb.getTotalCap())
                .sum();
    }

    public void updateTactics(AbilityType type, TacticsAbility ta) {
        this.abilities.remove(type);//移除旧的战术加成
        this.abilities.put(type, ta);
        this.newestData = false;
    }

    public void removeAbility(AbilityType type) {
        this.abilities.remove(type);
        this.newestData = false;
    }

    public EPlayerPosition getPlayerPosition() {
        return playerPosition;
    }

    protected void upRtAndStepActionStats(EBattleStep step, EActionType type, int val) {
        this.realTimeActionStats.sum(type, val);
        this.stepActionStats.sum(step, type, val);
        if (type == EActionType.pts) {
            scorelog.debug("btpr scoreup. tid {} pid {} val {} pts {}", tid, player.getPlayerRid(), val,
                    realTimeActionStats.getIntValue(EActionType.pts));
        }
    }

    protected void setRtAndStepActStats(EBattleStep step, EActionType type, int val) {
        this.realTimeActionStats.set(type, val);
        this.stepActionStats.set(step, type, val);
        if (type == EActionType.pts) {
            scorelog.debug("btpr scoreset. tid {} pid {} val {} pts {}", tid, player.getPlayerRid(), val,
                    realTimeActionStats.getIntValue(EActionType.pts));
        }
    }

    protected void upRtActStats(EActionType type, int val) {
        this.realTimeActionStats.sum(type, val);
        if (type == EActionType.pts) {
            scorelog.debug("btpr scoreup. tid {} pid {} val {} pts {}", tid, player.getPlayerRid(), val,
                    realTimeActionStats.getIntValue(EActionType.pts));
        }
    }

    protected void setRtActStats(EActionType type, int val) {
        this.realTimeActionStats.set(type, val);
        if (type == EActionType.pts) {
            scorelog.debug("btpr scoreset. tid {} pid {} val {} pts {}", tid, player.getPlayerRid(), val,
                    realTimeActionStats.getIntValue(EActionType.pts));
        }
    }

    public float setPower(float power) {
        this.power = power;
        this.newestData = false;
        if (this.power <= 0) {
            this.power = 1;
        }
        if (this.power > this.ability.getAttrData(EActionType.power)) {
            this.power = this.ability.getAttrData(EActionType.power);
        }
        return this.power;
    }

    public float getPower() {
        return power;
    }
    //    public int updatePower(long teamId, int val, BattlePKReport report) {
    //        float result = updatePower(val);
    //        report.addAction(teamId, getPlayerId(), val, EActionType.update_power, 0, 0, 0);
    //        return (int) result;
    //    }

    public int getHonorLogoQuality() {
        return honorLogoQuality;
    }

    public int getHonorLogo() {
        return honorLogo;
    }

    //	public void setStartRound(int startRound) {
    //		this.startRound = startRound;
    //	}
    //
    //	public int getStartRound() {
    //		return startRound;
    //	}

    public long getTid() {
        return tid;
    }

    public int getPid() {
        return pid;
    }

    public PlayerBean getPlayer() {
        return player;
    }

    public float calcPower(float power) {
        return (float) (power * Math.pow(fjrate / 100f, kdrate) / Math.pow(getPowerRate() / 100f, kdrate));
    }

    public static void main(String[] args) {
        float fjrate = 80f;
        float kdrate = 1.5f;
        float pospower = 60; //Tactics.xlsx -> Power
        float power = pospower / 100f;
        float getPowerRate = 75; // PlayerPhysical.xlsx

        double ret = (float) (power * Math.pow(fjrate / 100f, kdrate) / Math.pow(getPowerRate / 100f, kdrate));
        System.out.println(ret);
    }

    private float getPowerRate() {
        return getAbility().getAttrData(EActionType.power_rate);
    }

    public PlayerAbility getAbility() {
        return ability;
    }

    public PlayerActStat getRealTimeActionStats() {
        return realTimeActionStats;
    }

    public PlayerStepActStat getStepActionStats() {
        return stepActionStats;
    }

    public void updateLineupPosition(EPlayerPosition position) {
        lineupPos = position;
        if (!isLineupPos()) {//不在主力球员位置，移除战术加成
            abilities.remove(AbilityType.Tactic_Retrain);//移除旧的战术加成
            //            this.abilityMap.remove(AbilityType.Tactic_Offense);//移除旧的战术加成
            //            this.abilityMap.remove(AbilityType.Tactic_Defense);//移除旧的战术加成
        }
    }

    public void addPlayerAbility(PlayerAbility pa) {
        abilities.put(pa.getType(), pa);
        ability.clear();
        sumAllAbilities();
    }

    public void putPlayerAbility(Map<AbilityType, PlayerAbility> caps) {
        ability.clear();
        caps.forEach((k, v) ->
                abilities.put(v.getType(), v));
        sumAllAbilities();
    }

    /** 清空除了球员基础以外的所有加成 */
    public void clearCap() {
        PlayerAbility base = this.abilities.get(AbilityType.Player_Base);
        this.abilities.clear();
        this.abilities.put(base.getType(), base);
    }

    public EPlayerPosition getLineupPosition() {
        return lineupPos;
    }

    public EPlayerPosition getLpPos() {
        return lineupPos;
    }

    public boolean isLineupPos() {
        return lineupPos != EPlayerPosition.NULL;
    }

    /** 策划配置id */
    public int getPlayerId() {
        return getRid();
    }

    /** 策划配置id */
    public int getRid() {
        return this.player.getPlayerRid();
    }

    public Map<Integer, Integer> getHitNums() {
        return hitNums;
    }

    /** 增加提示的触发次数 */
    public void addHintNum(int hrid, int num) {
        if (hitNums == null) {
            hitNums = new HashMap<>();
        }
        hitNums.merge(hrid, num, (oldv, v) -> oldv + v);
    }

    @Override
    public String toString() {
        return "{" +
                "\"prid\":" + getRid() +
                '}';
    }
}
