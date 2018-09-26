package com.ftkj.manager.battle.model;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleStep;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 比赛小节数据
 */
public class StepActionStatistics implements ReadOnlyStepActionStats, Serializable {
    private static final long serialVersionUID = -6891575795725534488L;
    /** 每节行为数据 */
    private final Map<EBattleStep, ActionStatistics> stepActions;

    public StepActionStatistics() {
        this.stepActions = new EnumMap<>(EBattleStep.class);
        for (EBattleStep step : EBattleStep.values()) {
            stepActions.put(step, new ActionStatistics());
        }
    }

    /** 更新单个行为统计数据. 正加负减 */
    public void sum(EBattleStep step, EActionType type, float val) {
        stepActions.get(step).sum(type, val);
    }

    /** 设置单个行为统计数据 */
    public void set(EBattleStep step, EActionType type, float val) {
        stepActions.get(step).set(type, val);
    }

    public void sum(EBattleStep step, ActionStatistics other) {
        stepActions.get(step).sum(other);
    }

    public Map<EBattleStep, ActionStatistics> getStepActions() {
        return stepActions;
    }

    @Override
	public ActionStatistics getStepActions(EBattleStep step) {
        return stepActions.get(step);
    }

    @Override
	public int getStepAction(EBattleStep step, EActionType act) {
        ActionStatistics as = stepActions.get(step);
        return as == null ? 0 : as.getIntValue(act);
    }

    /** 获取满足条件的行为值的和 */
    @Override
	public int getStepActionSum(Predicate<EBattleStep> stepPredicate, EActionType act) {
        return stepActions.entrySet().stream()
                .filter(e -> stepPredicate.test(e.getKey()))
                .mapToInt(e -> e.getValue().getIntValue(act))
                .sum();
    }
}
