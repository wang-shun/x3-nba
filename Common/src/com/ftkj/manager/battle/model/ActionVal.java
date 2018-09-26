package com.ftkj.manager.battle.model;

import com.ftkj.enums.EActionType;

public class ActionVal {
    private final EActionType act;
    private final int value;

    public ActionVal(EActionType act, int value) {
        this.act = act;
        this.value = value;
    }

    public EActionType getAct() {
        return act;
    }

    public int getValue() {
        return value;
    }
}
