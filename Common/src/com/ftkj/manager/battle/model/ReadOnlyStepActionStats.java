package com.ftkj.manager.battle.model;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleStep;

import java.util.function.Predicate;

/**
 * 比赛小节数据
 */
public interface ReadOnlyStepActionStats {
    ReadOnlyActionStats getStepActions(EBattleStep step);

    int getStepAction(EBattleStep step, EActionType act);

    /** 获取满足条件的行为值的和 */
    int getStepActionSum(Predicate<EBattleStep> stepPredicate, EActionType act);
}
