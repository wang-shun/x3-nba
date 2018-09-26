package com.ftkj.manager.battle.model;

import com.ftkj.console.ConfigConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.coach.CoachBean;
import com.ftkj.manager.statistics.TeamDayStatistics;
import com.ftkj.server.GameSource;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.lambda.LBFloat;
import com.ftkj.util.tuple.Tuple2F;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author tim.huang
 * 2017年2月16日
 * 战斗球队数据
 */
public class BattleTeam implements Serializable {
    private static final long serialVersionUID = 2L;
    private static final Logger log = LoggerFactory.getLogger(BattleTeam.class);
    private static final Logger scorelog = LoggerFactory.getLogger("com.ftkj.manager.battle.model.BattleTeam_Score");
    /** 球队ID */
    private long teamId;
    /** 玩家节点名称(所在分区id) */
    private final String nodeName;
    /** 球队名字 */
    private final String name;
    /** 队徽 */
    private String logo;
    /** 球队级别 */
    private int level;
    /** 准备状态 */
    private Status ready;
    /** 分数 */
    private int score;
    /** 球队比赛中各项属性 */
    private BattleTeamAbility ability;
    /** 比赛中的所有球员数据 */
    private List<BattlePlayer> players;
    /** 首发阵容球员缓存 */
    private Map<EPlayerPosition, BattlePosition> lineupPlayers;
    /** 当前使用的战术， 长度为2的数组。 0为进攻战术，1为防守战术 */
    private BattleTactics[] pkTactics;
    /** 比赛中携带的道具 */
    //    private Map<Integer, BattleProp> props;
    /** 比赛中携带的战术 */
    private Map<TacticId, BattleTactics> tactics;
    /** 比赛中携带的教练 */
    private CoachBean coach;
    /** 每个回合分数 */
    private Map<EBattleStep, Integer> stepScore;
    /** buff列表 */
    private List<BattleBuffer> buffers;
    /** 比赛状态 */
    private BattleStat stat;
    /** AI 规则 */
    private BattleAI ai;
    /** 是否获胜 */
    private boolean win;
    /** 下回合强制命中行为的球员ID */
    private int nextPlayer;
    /** 下回合需要强制命中的子行为 */
    private EActionType nextPlayerAction;

    /** 本回合需要强制选择球员的子行为 */
    private Set<EActionType> forcePosActions;
    /** 本回合需要强制选择球员的位置 */
    private List<EPlayerPosition> forcePos;
    /** 从本回合开始需要强制选择球员的回合数 */
    private int forceActionRound;

    /** 初始士气 */
    private int initMorale;
    //====  连续得分
    /** 连续得分 */
    private int runPoint;
    /** 连续得分球员ID */
    private int runPointPlayerId;
    /** 连续得分球员的得分次数 */
    private int runPointNumWithPlayer;
    //====  连续失误
    /** 连续失误球员id. */
    private int turnoverStreakPlayerId;
    /**
     * 实时比赛行为统计. 只包括球队级别的统计信息.
     * 球员统计信息在球员 {@link BattlePlayer} 上,
     * 球队统计在球队 {@link BattleTeam} 上,
     * 比赛本身级别的在比赛 {@link BattleSource} 上.
     */
    private final ActionStatistics rtActionStats;
    /**
     * 实时比赛行为统计. 只包括球队级别的统计信息.
     * 球员统计信息在球员 {@link BattlePlayer} 上,
     * 球队统计在球队 {@link BattleTeam} 上,
     * 比赛本身级别的在比赛 {@link BattleSource} 上.
     */
    private final StepActionStatistics stepActionStats;
    /** 提示id和已触发次数 */
    private Map<Integer, Integer> hitNums;

    private TeamDayStatistics dayStatistics;

    /**
     * 本场比赛更换战术次数
     */
    private int updateTacticsNum;

    public BattleTeam(long teamId, String name, String logo, int level, CoachBean coach, TeamAbility teamAbility) {
        super();
        this.teamId = teamId;
        this.name = name;
        this.logo = logo;
        this.level = level;
        this.ready = Status.Not_Ready;
        this.score = 0;
        this.players = Lists.newArrayList();
        this.lineupPlayers = new EnumMap<>(EPlayerPosition.class);
        this.tactics = Maps.newHashMap();
        this.pkTactics = new BattleTactics[2];
        for (EPlayerPosition position : EPlayerPosition.values()) {
            if (EPlayerPosition.NULL != position) {
                this.lineupPlayers.put(position, new BattlePosition(position));
            }
        }
        this.ability = new BattleTeamAbility(AbilityType.Team, teamAbility);

        this.stepScore = Maps.newHashMap();
        this.nodeName = GameSource.serverName;
        this.initMorale = 100;
        this.stat = new BattleStat();
        this.stat.setMorale(this.initMorale);
        this.coach = coach;
        this.buffers = Lists.newArrayList();
        rtActionStats = new ActionStatistics();
        stepActionStats = new StepActionStatistics();
        this.ai = new BattleAI();
    }

    public enum Status {
        Not_Ready(0),
        Ready(1);
        private final int status;

        Status(int status) {
            this.status = status;
        }
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public List<BattleBuffer> getBuffers() {
        return buffers;
    }

    public void setBuffers(List<BattleBuffer> buffers) {
        this.buffers = buffers;
    }

    public EActionType getNextPlayerAction() {
        return nextPlayerAction;
    }

    public void setNextPlayerAction(EActionType nextPlayerAction) {
        this.nextPlayerAction = nextPlayerAction;
    }

    public Set<EActionType> getForcePosActions() {
        return forcePosActions;
    }

    public void setForcePosActions(Set<EActionType> forcePosActions) {
        this.forcePosActions = forcePosActions;
    }

    public List<EPlayerPosition> getForcePos() {
        return forcePos;
    }

    public void setForcePos(List<EPlayerPosition> forcePos) {
        this.forcePos = forcePos;
    }

    public int getForceActionRound() {
        return forceActionRound;
    }

    public void setForceActionRound(int forceActionRound) {
        this.forceActionRound = forceActionRound;
    }

    public CoachBean getCoach() {
        return coach;
    }

    /** 更新并且返回是否连续失误 */
    public boolean updateTurnoverStreakPlayerId(int playerId) {
        boolean result = turnoverStreakPlayerId == playerId;
        turnoverStreakPlayerId = playerId;
        return result;
    }

    public int getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(int nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    /** 跨服引用数据同步 */
    public void crossSourceSyn() {
        lineupPlayers.values().forEach(player -> player.initPlayer(getPlayer(player.getPlayer().getPlayerId())));
        buffers = Lists.newArrayList();
    }

    public void updateStepScore(EBattleStep step) {
        int total = stepScore.values().stream().mapToInt(score -> score).sum();
        stepScore.put(step, this.score - total);
    }
    
    /**
     * 修改每小节的分数,正加负减
     * @param step
     * @param score
     */
    public void updateStepScore(EBattleStep step, int score) {
        stepScore.put(step, stepScore.get(step) + score);
    }

    public int getStepScore(EBattleStep step) {
        return stepScore.getOrDefault(step, 0);
    }

    /** 当前小节得分 */
    public int getStepScoreNow() {
        int total = stepScore.values().stream().mapToInt(score -> score).sum();
        return score - total;
    }

    /** 更新连续得分的分数 */
    public void updateRunPoint(int val) {
        runPoint += val;
    }

    /** 更新并且返回是否连续得分 */
    public boolean upRunPointPlayerId(int playerId, int sc) {
        boolean result = runPointPlayerId == playerId;
        if (!result) {
            runPointNumWithPlayer = 0;
        }
        runPointPlayerId = playerId;
        runPointNumWithPlayer++;
        return result;
    }

    public void clearRunPoint() {
        runPoint = 0;
    }

    public int getRunPointNumWithPlayer() {
        return runPointNumWithPlayer;
    }

    public int getRunPointPlayerId() {
        return runPointPlayerId;
    }

    public int getRunPoint() {
        return runPoint;
    }

    public int getUpdateTacticsNum() {
        return updateTacticsNum;
    }

    public void addUpdateTacticsNum() {
        updateTacticsNum++;
    }

    /** 装备当前使用的战术 */
    public void updateTactics(TacticId otid, TacticId dtid, BattleTactics oot, BattleTactics odt) {
        pkTactics[0] = this.tactics.get(otid);
        pkTactics[1] = this.tactics.get(dtid);
        //装备战术，更新首发球员的战术加成
        lineupPlayers.values().forEach(bp ->
            bp.updateTactics(pkTactics[0], pkTactics[1], oot, odt, this.coach));
    }

    /** 装备当前使用的战术 */
    public void updateTactics(BattleTactics ot, BattleTactics dt, BattleTactics oot, BattleTactics odt) {
        pkTactics[0] = ot;
        pkTactics[1] = dt;
        //装备战术，更新首发球员的战术加成
        lineupPlayers.values().forEach(player ->
            player.updateTactics(pkTactics[0], pkTactics[1], oot, odt, this.coach));
    }

    /**
     * 球员位置交换
     *
     * @param ajg 对方进攻战术
     * @param afs 对方防守战术
     */
    public void updatePlayerPosition(int prida, int pridb, boolean update, BattleTactics ajg, BattleTactics afs) {
        if (prida == pridb) {
            return;
        }
        BattlePlayer pra = getPlayer(prida);
        BattlePlayer prb = getPlayer(pridb);
        if (pra == null || prb == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("btteam uppos before. tid {}. pra rid {} lppos {}. prb rid {} lppos {}", teamId,
                pra.getRid(), pra.getLpPos(), prb.getRid(), prb.getLpPos());
        }
        BattlePosition apos = getLineupPlayer(pra.getLineupPosition());
        BattlePosition bpos = getLineupPlayer(prb.getLineupPosition());
        //先全部更新到替补位
        pra.updateLineupPosition(EPlayerPosition.NULL);
        prb.updateLineupPosition(EPlayerPosition.NULL);

        if (apos != null) {//场上球员
            apos.updatePlayer(prb, this.getPkTactics(TacticType.Offense), this.getPkTactics(TacticType.Defense), ajg, afs, this.coach);
            prb.updateLineupPosition(apos.getPosition());
        }
        if (bpos != null) {
            bpos.updatePlayer(pra, this.getPkTactics(TacticType.Offense), this.getPkTactics(TacticType.Defense), ajg, afs, this.coach);
            pra.updateLineupPosition(bpos.getPosition());
        }
        if (update) {
            recalcPlayerAbility();
        }
        if (log.isDebugEnabled()) {
            log.debug("btteam uppos post. tid {}. pra rid {} lppos {}. prb rid {} lppos {}", teamId,
                pra.getRid(), pra.getLpPos(), prb.getRid(), prb.getLpPos());
        }
    }

    public BattleTactics getPkTactics(TacticType type) {
        return pkTactics[type.ordinal()];
    }

    public TacticId getPkTacticId(TacticType type) {
        BattleTactics bt = pkTactics[type.ordinal()];
        return bt != null ? bt.getTactics().getId() : null;
    }

    /** 添加战术到可使用列表中 */
    public void addTactics(BattleTactics bt) {
        tactics.put(bt.getTactics().getId(), bt);
    }

    /** 添加战术到可使用列表中 */
    public void replaceAllTactics(List<BattleTactics> bts) {
        tactics.clear();
        bts.forEach(bt -> tactics.put(bt.getTactics().getId(), bt));
    }

    public boolean hasTactics(TacticId id) {
        return tactics.get(id) != null;
    }

    public BattleTactics getTactic(TacticId et) {
        return tactics.get(et);
    }

    public TacticId getTacticId(TacticId et) {
        BattleTactics bt = tactics.get(et);
        return bt != null ? bt.getTactics().getId() : null;
    }

    public Map<TacticId, BattleTactics> getTactics() {
        return tactics;
    }

    public void updateMorale(int val) {
        int newVal = this.stat.getMorale() + val;
        newVal = newVal >= 200 ? 200 : newVal;
        this.stat.setMorale(newVal);
    }

    /** 添加球员到可使用列表中，并且将首发球员数据初始化 */
    public BattleTeam addPlayer(EPlayerPosition position, BattlePlayer player) {
        this.players.add(player);
        if (player.isLineupPos()) {
            lineupPlayers.get(position).initPlayer(player);
        }
        return this;
    }

    /** 重新计算球员战力 */
    public void recalcPlayerAbility() {
        PlayerAbility playerAbility = ability.clearPlayerAbility();
        float moraleRate = ConfigConsole.global().capMoraleRate;
        for (BattlePosition p : lineupPlayers.values()) {
            BattlePlayer pr = p.getPlayer();
            pr.reloadAbility(p.getPosition());//每个球员更新自己相关的加成属性
            Tuple2F capMoraleFinal = pr.reloadAbilityWithMorale(stat.getMorale(), initMorale, moraleRate);
            float attackCap = capMoraleFinal._1();
            float defendCap = capMoraleFinal._2();
            log.trace("calc cap team eachplayer. tid {} pid {} acap {} gcap {}", teamId, pr.getPlayerId(), attackCap, defendCap);
            playerAbility.sumCap(attackCap, defendCap);
        }
        log.debug("calc cap team allplayer. tid {} morale {}/{} r {} cap all pr {} team {}",
            teamId, stat.getMorale(), initMorale, moraleRate, playerAbility, ability);
    }

    public BattleTeamAbility getAbility() {
        return ability;
    }

    public void ready() {
        this.ready = Status.Ready;
    }

    public void updateScore(int val) {
        this.score += val;
        scorelog.debug("btteam upscore. tid {} val {} pts {}", teamId, val, score);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public long getTeamId() {
        return teamId;
    }

    public long getId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getName() {
        return name;
    }

    public BattleStat getStat() {
        return stat;
    }

    public String getLogo() {
        return logo;
    }

    public int getLevel() {
        return level;
    }

    public Status getReady() {
        return ready;
    }

    public List<BattlePlayer> getPlayers() {
        return players;
    }

    public BattlePlayer getPlayer(int playerRid) {
        return this.players.stream().filter(player -> player.getPlayerId() == playerRid).findFirst().orElse(null);
    }

    public BattlePlayer getRanPlayer() {
        return getLineupPlayer(EPlayerPosition.values()[RandomUtil.randInt(5)]).getPlayer();
    }

    public BattlePosition getLineupPlayer(EPlayerPosition position) {
        return this.lineupPlayers.get(position);
    }

    public int getLineupPrid(EPlayerPosition position) {
        BattlePosition bp = lineupPlayers.get(position);
        if (bp == null) {
            return 0;
        }
        return bp.getPlayer() != null ? bp.getPlayer().getRid() : 0;
    }

    public Map<EPlayerPosition, BattlePosition> getLineupPlayers() {
        return lineupPlayers;
    }

    public ActionStatistics sumActionStatistics(Predicate<EPlayerPosition> positionPredicate) {
        ActionStatistics ret = new ActionStatistics();
        for (Map.Entry<EPlayerPosition, BattlePosition> e : lineupPlayers.entrySet()) {
            BattlePosition pos = e.getValue();
            BattlePlayer pr = pos.getPlayer();
            if (pr != null && positionPredicate.test(e.getKey())) {
                ret.sum(pr.getRealTimeActionStats());
            }
        }
        return ret;
    }

    public int sumActionStatistics(Predicate<EPlayerPosition> positionPredicate, EActionType actionType) {
        LBFloat ret = new LBFloat();
        for (Map.Entry<EPlayerPosition, BattlePosition> e : lineupPlayers.entrySet()) {
            BattlePosition pos = e.getValue();
            BattlePlayer pr = pos.getPlayer();
            if (pr != null && positionPredicate.test(e.getKey())) {
                ret.sum(pr.getRealTimeActionStats().getValue(actionType));
            }
        }
        return ret.intValue();
    }

    public ActionStatistics getRtActionStats() {
        return rtActionStats;
    }

    public StepActionStatistics getStepActionStats() {
        return stepActionStats;
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

    public TeamDayStatistics getDayStatistics() {
        return dayStatistics;
    }

    public void setDayStatistics(TeamDayStatistics dayStatistics) {
        this.dayStatistics = dayStatistics;
    }

    public boolean randomSuccessAction(Random random) {
        BattleTeamAbility ha = getAbility();
        int r = random.nextInt(ha.getSuccWeight() + ha.getFailWeight());//命中一个成功或者失败的行为
        boolean succAction = false;
        if (r <= ha.getSuccWeight()) {//成功
            succAction = true;
        }
        return succAction;
    }

    public BattleAI getAi() {
        return ai;
    }

    public void setAi(BattleAI ai) {
        this.ai = ai;
    }
}
