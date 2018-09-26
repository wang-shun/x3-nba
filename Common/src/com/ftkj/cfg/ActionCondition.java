package com.ftkj.cfg;

import com.ftkj.enums.EActionType;

import java.io.Serializable;

/** 比赛数据要求 .数据对(类型和值) */
public final class ActionCondition implements Serializable {
    private static final long serialVersionUID = -2712335987379986246L;
    /** 比赛数据类型 */
    private final EActionType act;
    /** 要求 */
    private final CompareOp op;
    /** 值 */
    private final int value;

    public ActionCondition(EActionType act, CompareOp op, int value) {
        this.act = act;
        this.op = op;
        this.value = value;
    }

    public EActionType getAct() {
        return act;
    }

    public CompareOp getOp() {
        return op;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{" +
                "\"act\":" + act +
                ", \"op\":" + op +
                ", \"v\":" + value +
                '}';
    }

    /** 比赛数据是否达到配置要求 */
    public boolean match(float currVal) {
        return op.compare(currVal, value);
    }

    /** 比赛数据是否达到配置要求 */
    public boolean match(int currVal) {
        return op.compare(currVal, value);
    }
}
