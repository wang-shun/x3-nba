package com.ftkj.manager.battle.model;

import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ConfigConsole.GlobalBean;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticId;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.coach.CoachBean;
import com.ftkj.manager.tactics.TacticsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 战术加成
 * <p>
 * 每回合计算球员战术加成：
 * 单个球员计算战术加成后攻防值 =
 * 计算战术之前的球员总攻防 +
 * 球员总进攻 * 进攻战术该球员所在位置上加成系数 * 进攻克制系数 +
 * 球员总防守 * 防守战术该球员所在位置上加成系数 * 防守克制系数
 * <p>
 * 说明：
 * 进攻战术该球员所在位置上加成：
 * 这个战术位置加成不是球员位置，而且球场位置，如强攻内线加的是PF和C，那么球场上C位上的球员就会获得加成，即使C位上的放的球员是个SG，也会获得加成。
 * <p>
 * 进攻克制系数：
 * 默认是1，进攻被克后是0.2，做成配置。当我方进攻战术的加成位置和对方防守战术的加成位置有相同时，则该进攻位置就会被克制。
 * 如果我方进攻战术为跑轰战术，系数恒定是1.
 * <p>
 * 防守克制系数：
 * 默认是1，防住对方后是4.6，做成配置。当我方防守战术的加成位置和对方进攻战术的加成位置有相同时，则该防守位置为防住对方。
 * 如果我方防守战术为全场紧逼或对方进攻战术是炮轰战术，系数恒定是1.
 * <p>
 * 配置表配的是千分比，配20，即加成2%
 *
 * @author tim.huang
 */
public class TacticsAbility extends PlayerAbility {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(TacticsAbility.class);

    public TacticsAbility(AbilityType type,
                          BattleTactics offense, BattleTactics defense,
                          BattleTactics otherOffense, BattleTactics otherDefense,
                          CoachBean coach, BattlePosition bp) {
        super(type, bp.getPlayer().getPlayerId());
        calcCapRate(offense, defense, otherOffense, otherDefense, coach, bp);
    }

    /**
     * @param otherOffense 对方进攻战术, 可能为null
     * @param otherDefense 对方防守战术, 可能为null
     */
    private void calcCapRate(BattleTactics offense, BattleTactics defense,
                             BattleTactics otherOffense, BattleTactics otherDefense,
                             CoachBean coach, BattlePosition bp) {
        //        final float ocap = bp.getPlayer().getAbility().getAttrData(EActionType.ocap);
        //        final float dcap = bp.getPlayer().getAbility().getAttrData(EActionType.dcap);
        final BattlePlayer pr = bp.getPlayer();
        final GlobalBean global = ConfigConsole.global();
        final EPlayerPosition pos = bp.getPosition();
        final float oRestrainRate = getOffenseRestrainRate(offense, otherDefense, global, pos);  //进攻被克制系数
        final float dRestrainRate = getDefenseRestrainRate(defense, otherOffense, global, pos);  //防守克制系数

        final float ocapRateCfg = getPosCapRate(offense, pos);
        final float dcapRateCfg = getPosCapRate(defense, pos);
        final float ocoachRateCfg = getCoachRate(offense, coach, pos);
        final float dcoachRateCfg = getCoachRate(defense, coach, pos);
        final float ocapRate = (ocapRateCfg + ocoachRateCfg) * oRestrainRate;
        final float dcapRate = (dcapRateCfg + dcoachRateCfg) * dRestrainRate;
        //球员位置加成
        if (log.isDebugEnabled()) {
            log.debug("cap tactics. tid {} prid {} pos {} restrainRate {} {} posRate {} {} coachRate {} {} final {} {} self {} {} target {} {}",
                    pr.getTid(), pr.getRid(), pos,
                    oRestrainRate, dRestrainRate, ocapRateCfg, dcapRateCfg, ocoachRateCfg, dcoachRateCfg,
                    ocapRate, dcapRate, offense, defense, otherOffense, otherDefense);
        }
        sumCap(ocapRate, dcapRate);
    }

    private float getCoachRate(BattleTactics tt, CoachBean coach, EPlayerPosition pos) {
        if (coach == null) {
            return 0f;
        }
        return tt == null ? 0f : coach.getTactics(tt.getTactics().getId(), pos, 0f);
    }

    private float getPosCapRate(BattleTactics tt, EPlayerPosition pos) {
        return tt == null ? 0f : tt.getTactics().getPosCapRate(tt.getLevel(), pos, 0f);
    }

    private boolean eq(BattleTactics bt, TacticId id) {
        return bt != null && bt.getTactics().getId() == id;
    }

    /** 进攻被克制系数 */
    private float getOffenseRestrainRate(BattleTactics offense, BattleTactics otherDefense, GlobalBean global, EPlayerPosition pos) {
        float offenseRestrainRate = 1;
        if (eq(offense, TacticId.跑轰战术)) {
            offenseRestrainRate = global.capTacticOffenseFullRate;
        } else if (restrainOffense(offense, otherDefense, pos)) {
            offenseRestrainRate = global.capTacticOffenseRestrainRate;
        }
        return offenseRestrainRate;
    }

    /** 防守克制系数 */
    private float getDefenseRestrainRate(BattleTactics defense, BattleTactics otherOffense, GlobalBean global, EPlayerPosition pos) {
        float defenseRestrainRate = 1;
        if (eq(defense, TacticId.全场紧逼) || eq(otherOffense, TacticId.跑轰战术)) {
            defenseRestrainRate = global.capTacticDefenseFullRate;
        } else if (restrainDefense(defense, otherOffense, pos)) {
            defenseRestrainRate = global.capTacticDefenseRestrainRate;
        }
        return defenseRestrainRate;
    }

    /** 防守克制. 当我方防守战术的加成位置和对方进攻战术的加成位置有相同时，则该防守位置为防住对方 */
    private boolean restrainDefense(BattleTactics defense, BattleTactics otherOffense, EPlayerPosition pos) {
        if (defense == null || otherOffense == null) {
            return false;
        }
        return isRestrain(defense, otherOffense, pos, defense.getTactics(), otherOffense.getTactics());
    }

    /** 进攻被克制. 当我方进攻战术的加成位置和对方防守战术的加成位置有相同时，则该进攻位置就会被克制。 */
    private boolean restrainOffense(BattleTactics offense, BattleTactics otherDefense, EPlayerPosition pos) {
        if (offense == null || otherDefense == null) {
            return false;
        }
        return isRestrain(offense, otherDefense, pos, otherDefense.getTactics(), offense.getTactics());
    }

    private boolean isRestrain(BattleTactics self, BattleTactics other, EPlayerPosition pos,
                               TacticsBean defenseB, TacticsBean offenseB) {
        if (self == null || other == null) {
            return false;
        }
        //        if (defenseB.getRestrain() != offenseB.getId()) {//忽略战术克制
        //            return false;
        //        }
        Float selfCapRate = self.getTactics().getPosCapRate(self.getLevel(), pos);
        Float oCapRate = other.getTactics().getPosCapRate(other.getLevel(), pos);
        if (log.isTraceEnabled()) {
            log.trace("cap tactics. pos {} self cap rate {}, other cap rate {}", pos, selfCapRate, oCapRate);
        }
        return (selfCapRate != null && selfCapRate > 0) && (oCapRate != null && oCapRate > 0);
    }
}
