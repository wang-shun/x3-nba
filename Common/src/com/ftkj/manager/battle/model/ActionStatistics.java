package com.ftkj.manager.battle.model;

import com.ftkj.enums.EActionType;
import com.ftkj.util.lambda.LBFloat;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 * 比赛行为统计数据
 */
public class ActionStatistics implements ReadOnlyActionStats, Serializable {
    private static final long serialVersionUID = -759636642052553461L;
    /** 行为统计数据 */
    private final Map<EActionType, LBFloat> actionStatistics = new EnumMap<>(EActionType.class);

    /** 更新单个行为统计数据. 正加负减 */
    public void sum(EActionType type, float val) {
        actionStatistics.computeIfAbsent(type, (key) -> new LBFloat()).sum(val);
    }

    /** 设置单个行为统计数据 */
    public void set(EActionType type, float val) {
        actionStatistics.computeIfAbsent(type, (key) -> new LBFloat()).setVal(val);
    }

    /** 汇总另外一个统计数据到当前 */
    public ActionStatistics sum(ActionStatistics other) {
        if (other == null) {
            return this;
        }
        for (Map.Entry<EActionType, LBFloat> e : other.actionStatistics.entrySet()) {
            actionStatistics.merge(e.getKey(), e.getValue(), LBFloat::sum);
        }
        return this;
    }

    @Override
	public float getValue(EActionType type) {
        LBFloat lb = actionStatistics.get(type);
        return lb == null ? 0 : lb.getVal();
    }

    @Override
	public int getIntValue(EActionType type) {
        LBFloat lb = actionStatistics.get(type);
        return lb == null ? 0 : (int) lb.getVal();
    }

    @Override
	public float getActionValue(EActionType act) {
        switch (act) {
            case fgp:
                return getValue(EActionType.fgm) / Math.max(1, getValue(EActionType.fga));
            case ftp:
                return getValue(EActionType.ftm) / Math.max(1, getValue(EActionType.fta));
            case _3pp:
                return getValue(EActionType._3pm) / Math.max(1, getValue(EActionType._3pa));
            default:
                return getValue(act);
        }
    }

    public Map<EActionType, LBFloat> getActionStatistics() {
        return actionStatistics;
    }
}
