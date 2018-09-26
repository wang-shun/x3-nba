package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年2月16日
 * 球员位置
 */
public enum EPlayerPosition {
    PG(1),
    SG(2),
    SF(3),
    PF(4),
    C(5),
    NULL(0);//板凳位

    private int id;

    private EPlayerPosition(int id) {
        this.id = id;

    }

    public int getId() {
        return id;
    }

    //通过ID，取对应的战术枚举
    public static final Map<Integer, EPlayerPosition> playerPositionEnumMap = new HashMap<>();

    static {
        for (EPlayerPosition et : EPlayerPosition.values()) {
            playerPositionEnumMap.put(et.getId(), et);
        }
    }

    public static EPlayerPosition getEPlayerPosition(int id) {
        return playerPositionEnumMap.get(id);
    }

    public static EPlayerPosition convertByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return valueOf(name.toUpperCase());
    }

    @Override
    public String toString() {
        return name();
    }
}
