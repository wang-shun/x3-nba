package com.ftkj.enums;

import com.google.common.collect.Maps;

import java.util.Map;

public enum EPropType {
    /** 常规物品 */
    Common(1),
    /** 常规礼包 */
    Package(2),
    /** 常规球员 */
    Player(3),
    /** 货币 */
    Money(4),
    /** 概率礼包 */
    Package_Random(5),
    /** 比赛用道具 */
    PK(6),
    /** 球员级别（随出市价球员） */
    PlayerGrade(7),
    /** 教练道具 */
    Coach(8),
    /** VIP经验 */
    VipExp(9),
    /** buff类型道具 */
    Buff(10),
    /** 球员包装道具 */
    Wrap_Player(11),
    /** 球员级别随出底薪球员 */
    PlayerBasePrice(12),
    //
    ;

    private int type;

    EPropType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private static Map<Integer, EPropType> map = Maps.newHashMap();

    static {
        for (EPropType et : EPropType.values()) {
            map.put(et.getType(), et);
        }
    }

    public static EPropType getEPropType(int id) {
        return map.get(id);
    }
}
