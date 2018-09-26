package com.ftkj.manager.battle.model;

import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.enums.EActionType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** 比赛提示配置 */
public class BattleHint implements Serializable {
    private static final long serialVersionUID = -5544177161718120009L;
    /** 提示配置 {@link BattleBean#getHints()} */
    private final ImmutableMap<EActionType, ImmutableSet<Integer>> hintsCfg;
    /** 比赛全局提示id和已触发次数. 球队和球员的在 BattleTeam 和 BattlePlayer 上 */
    private final Map<Integer, Integer> matchHitNums = new HashMap<>();
    /** 所有提示已经触发 */
    private boolean allHintsTriggered;
    /** 完全触发的提示id. 配置触发次数 > -1 且 在目标上已触发次数 >= 配置次数 */
    private final Set<Integer> triggeredMaxNums = new HashSet<>();
    /** 比赛中每轮产生的 action 列表, 每轮结束时生成提示信息后清除 */
    private final EnumSet<EActionType> roundHintActions = EnumSet.noneOf(EActionType.class);

    public BattleHint(ImmutableMap<EActionType, ImmutableSet<Integer>> hints) {
        if (hints.isEmpty()) {
            allHintsTriggered = true;
        }
        hintsCfg = hints;
    }

    public boolean isAllHintsTriggered() {
        return allHintsTriggered;
    }

    public void setAllHintsTriggered(boolean allHintsTriggered) {
        this.allHintsTriggered = allHintsTriggered;
    }

    public void addActionTrigger(EActionType actionType) {
        if (allHintsTriggered) {
            return;
        }
        roundHintActions.add(actionType);
    }

    public EnumSet<EActionType> getRoundHintActions() {
        return roundHintActions;
    }

    public ImmutableSet<Integer> getIds(EActionType act) {
        return hintsCfg.get(act);
    }

    public void addMatchHintNum(int hrid, int num) {
        matchHitNums.merge(hrid, num, (oldv, v) -> oldv + v);
    }

    public Map<Integer, Integer> getMatchHitNums() {
        return matchHitNums;
    }

    public boolean isMatchHintTriggered(int hrid, int triggerNum) {
        return matchHitNums.getOrDefault(hrid, 0) >= triggerNum;
    }

    public Set<Integer> getTriggeredMaxNums() {
        return triggeredMaxNums;
    }

}
