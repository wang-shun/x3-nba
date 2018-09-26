package com.ftkj.cfg.battle;

import com.ftkj.enums.battle.EBattleAction;
import com.ftkj.enums.battle.TacticZone;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 比赛父行为配置
 */
public final class BattleActionsBean implements Serializable {
    private static final long serialVersionUID = -8199203451862524553L;
    private final ImmutableMap<EBattleAction, BattleActionBean> actions;
    /** 战术成功总权重 */
    private final ImmutableMap<TacticZone, Integer> successWeight;
    /** 战术失败总权重 */
    private final ImmutableMap<TacticZone, Integer> failWeight;
    private final ImmutableList<BattleActionBean> successActions;
    private final ImmutableList<BattleActionBean> failActions;

    public BattleActionsBean(ImmutableMap<EBattleAction, BattleActionBean> actions) {
        this.actions = actions;
        BattleActionBeanStat stat = collectActionStat(actions);
        this.successWeight = stat.successWeight;
        this.failWeight = stat.failWeight;
        this.successActions = stat.successActions;
        this.failActions = stat.failActions;
    }

    public int getSucessWeight(TacticZone type) {
        return successWeight.getOrDefault(type, 500);
    }

    public int getFailWeight(TacticZone type) {
        return failWeight.getOrDefault(type, 500);
    }

    public BattleActionBean getActionBean(EBattleAction action) {
        return actions.get(action);
    }

    public BattleActionBean getActionBean(EBattleAction action, EBattleAction defaultAct) {
        BattleActionBean actb = actions.get(action);
        return actb != null ? actb : actions.get(defaultAct);
    }

    public ImmutableMap<EBattleAction, BattleActionBean> getActions() {
        return actions;
    }

    public ImmutableMap<TacticZone, Integer> getSuccessWeight() {
        return successWeight;
    }

    public ImmutableMap<TacticZone, Integer> getFailWeight() {
        return failWeight;
    }

    public ImmutableList<BattleActionBean> getSuccessActions() {
        return successActions;
    }

    public ImmutableList<BattleActionBean> getFailActions() {
        return failActions;
    }

    /** 汇总权重 */
    private BattleActionBeanStat collectActionStat(ImmutableMap<EBattleAction, BattleActionBean> actions) {
        Map<TacticZone, Integer> successWeight = new HashMap<>();
        Map<TacticZone, Integer> failtWeight = new HashMap<>();
        List<BattleActionBean> successActions = new ArrayList<>();
        List<BattleActionBean> failActions = new ArrayList<>();
        for (BattleActionBean acb : actions.values()) {
            if (acb.isSucessAction()) {
                successWeight.merge(TacticZone.normal, acb.getNormalTacticsWeight(), (oldv, v) -> oldv + v);
                successWeight.merge(TacticZone.inside, acb.getInsideTacticsWeight(), (oldv, v) -> oldv + v);
                successWeight.merge(TacticZone.outside, acb.getOuterTacticsWeight(), (oldv, v) -> oldv + v);
                successActions.add(acb);
            } else {
                failtWeight.merge(TacticZone.normal, acb.getNormalTacticsWeight(), (oldv, v) -> oldv + v);
                failtWeight.merge(TacticZone.inside, acb.getInsideTacticsWeight(), (oldv, v) -> oldv + v);
                failtWeight.merge(TacticZone.outside, acb.getOuterTacticsWeight(), (oldv, v) -> oldv + v);
                failActions.add(acb);
            }
        }
        return new BattleActionBeanStat(ImmutableMap.copyOf(successWeight),
                ImmutableMap.copyOf(failtWeight),
                ImmutableList.copyOf(successActions),
                ImmutableList.copyOf(failActions));
    }

    private static final class BattleActionBeanStat {
        /** 战术成功总权重 */
        private final ImmutableMap<TacticZone, Integer> successWeight;
        /** 战术失败总权重 */
        private final ImmutableMap<TacticZone, Integer> failWeight;
        private final ImmutableList<BattleActionBean> successActions;
        private final ImmutableList<BattleActionBean> failActions;

        BattleActionBeanStat(ImmutableMap<TacticZone, Integer> successWeight,
                             ImmutableMap<TacticZone, Integer> failWeight,
                             ImmutableList<BattleActionBean> successActions,
                             ImmutableList<BattleActionBean> failActions) {
            this.successWeight = successWeight;
            this.failWeight = failWeight;
            this.successActions = successActions;
            this.failActions = failActions;
        }
    }
}
