package com.ftkj.manager.battle.model;

import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.cfg.battle.BattleStepBean;
import com.ftkj.util.BinarySearch;
import com.ftkj.util.IntervalInt;
import com.ftkj.util.concurrent.X3Collectors;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 小节配置
 *
 * @author tim.huang
 * 2017年2月21日
 * 默认比赛小节对象，其他类型比赛实现接口即可
 */
public class DefaultBattleStep implements com.ftkj.manager.battle.BattleStep, Serializable {
    private static final long serialVersionUID = -2796120565536194574L;
    private final Map<EBattleStep, BattleStepBean> steps;
    private final List<BattleStepBean> stepLists;

    public DefaultBattleStep(List<BattleStepBean> list) {
        if (list == null) {
            steps = Collections.emptyMap();
            stepLists = Collections.emptyList();
        } else {
            steps = list.stream().collect(Collectors.toMap(BattleStepBean::getStep, k -> k,
                    X3Collectors.throwingMerger(), LinkedHashMap::new));
            stepLists = ImmutableList.copyOf(list);
        }
    }

    public List<BattleStepBean> getSteps() {
        return stepLists;
    }

    @Override
    public BattleStepBean getBattleStep(EBattleStep step) {
        return steps.get(step);
    }

    @Override
    public BattleStepBean getStepByRound(int round) {
        int idx = BinarySearch.binarySearch(stepLists, round, (step, r) -> IntervalInt.compare(step.getRoundInterval(), r));
        if (idx < 0) {
            return null;
        }
        return stepLists.get(idx);
    }

    @Override
    public Iterator<BattleStepBean> iterator() {
        return stepLists.iterator();
    }

    @Override
    public int getBattleStepRound(EBattleStep step) {
        return steps.get(step).getRound();
    }

    @Override
    public boolean hasSteps() {
        return steps != null && !steps.isEmpty();
    }

    @Override
    public String toString() {
        return "{" +
                "\"steps\":" + steps +
                ", \"stepLists\":" + stepLists +
                '}';
    }
}
