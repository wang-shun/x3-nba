package com.ftkj.manager.battle.model;

import com.ftkj.cfg.battle.BaseBattleBean;
import com.ftkj.cfg.battle.BattleActionsBean;
import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.cfg.battle.BattlePlayerPowerBean;
import com.ftkj.cfg.battle.BattleSkillPowerBean;
import com.ftkj.console.BattleConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.system.bean.DropBean;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Objects;

/**
 * 比赛配置信息
 *
 * @author tim.huang
 */
public class BattleInfo implements Serializable {
    private static final long serialVersionUID = 2L;
    private final long battleId;
    private final EBattleType battleType;
    /** 策划配置 */
    private final BattleBean battleBean;
    /** 小节延迟配置 */
    private ImmutableMap<EBattleStep, Integer> stepDelay;
    /** 技能能量每回合比率配置 */
    private BattleSkillPowerBean skillPower;
    /** 球员体力每回合比率配置 */
    private BattlePlayerPowerBean playerRoundPower;
    /** 比赛父行为配置 */
    private BattleActionsBean actions;
    /** 技能释放规则. 0或不填: 等待一段时间后释放. 1 : 立刻释放 */
    private int skillStrategy;
    /** 是否可以快速结束比赛 */
    private boolean quickEnd;
    /** 比赛添加时间 */
    private long addTime;
    /** 比赛开始时间 */
    private long startTime;
    //主场球队
    private long homeTeamId;
    /** 胜利时的奖励(动态) */
    private DropBean winDrop;
    /** 失败时的奖励(动态) */
    private DropBean lossDrop;
    /** 是否禁用消息推送. 快速结束比赛时需要禁止比赛过程中产生的消息推送给客户端 */
    private boolean disablePushMessage;

    public BattleInfo(long battleId,
                      EBattleType battleType,
                      BattleBean battleBean,
                      DropBean winDrop,
                      DropBean lossDrop,
                      long homeTeamId) {
        super();
        BaseBattleBean bbb = battleBean.getBaseBean();
        this.battleId = battleId;
        this.battleType = battleType;
        this.battleBean = battleBean;
        this.stepDelay = bbb.getStepDelay();
        this.quickEnd = bbb.isQuickEnd();
        this.skillPower = BattleConsole.getSkillPowers(bbb.getSkillPowerBean());
        this.playerRoundPower = BattleConsole.getPlayerRoundPowers(bbb.getPlayerPowerBean());
        this.actions = battleBean.getActions();
        this.skillStrategy = bbb.getSkillStrategy();
        this.winDrop = winDrop;
        this.lossDrop = lossDrop;
        this.homeTeamId = homeTeamId;
        Objects.requireNonNull(battleBean, "battleBean must not be null");
    }

    public long getBattleId() {
        return battleId;
    }

    public EBattleType getBattleType() {
        return battleType;
    }

    public BattleBean getBattleBean() {
        return battleBean;
    }

    public int getStepDelay(EBattleStep step, int defaultV) {
        return stepDelay.getOrDefault(step, defaultV);
    }

    public ImmutableMap<EBattleStep, Integer> getStepDelay() {
        return stepDelay;
    }

    public void setStepDelay(ImmutableMap<EBattleStep, Integer> stepDelay) {
        this.stepDelay = stepDelay;
    }

    public boolean isQuickEnd() {
        return quickEnd;
    }

    public void setQuickEnd(boolean quickEnd) {
        this.quickEnd = quickEnd;
    }

    public int skillPower(EActionType type) {
        return skillPower.getActPowers(type, 0);
    }

    public BattleSkillPowerBean getSkillPower() {
        return skillPower;
    }

    public void setSkillPower(BattleSkillPowerBean skillPower) {
        this.skillPower = skillPower;
    }

    public float roundPower(EActionType type) {
        return playerRoundPower.getActPowers(type, 0);
    }

    public BattlePlayerPowerBean getPlayerRoundPower() {
        return playerRoundPower;
    }

    public void setPlayerRoundPower(BattlePlayerPowerBean playerRoundPower) {
        this.playerRoundPower = playerRoundPower;
    }

    public void setActions(BattleActionsBean actions) {
        this.actions = actions;
    }

    public BattleActionsBean getActions() {
        return actions;
    }

    public int getSkillStrategy() {
        return skillStrategy;
    }

    public void setSkillStrategy(int skillStrategy) {
        this.skillStrategy = skillStrategy;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getAddTime() {
        return addTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(long homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public DropBean getWinDrop() {
        return winDrop;
    }

    public DropBean getLossDrop() {
        return lossDrop;
    }

    public void setWinDrop(DropBean winDrop) {
        this.winDrop = winDrop;
    }

    public void setLossDrop(DropBean lossDrop) {
        this.lossDrop = lossDrop;
    }

    public boolean isDisablePushMessage() {
        return disablePushMessage;
    }

    public void setDisablePushMessage(boolean disablePushMessage) {
        this.disablePushMessage = disablePushMessage;
    }
}
