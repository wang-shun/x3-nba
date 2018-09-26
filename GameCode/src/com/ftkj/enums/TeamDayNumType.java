package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

public enum TeamDayNumType {
    /** 竞技场. 每日已使用免费比赛次数 */
    Arena_Match_Free_Num(39000, "Arena_Match_Free_Num"),
    /** 新秀对抗赛. 每日已使用比赛次数 */
    Dual_Meet_Use_Num(50001, "Dual_Meet_Use_Num"),
    /** 新秀排位赛. 每日已使用比赛次数 */
    Dual_Meet_Rank_Use_Num(50002, "Dual_Meet_Rank_Use_Num"),

    //
    ;

    private final int type;
    private final String redisKey;

    TeamDayNumType(int type, String redisKey) {
        this.type = type;
        this.redisKey = redisKey;
    }

    public int getType() {
        return type;
    }

    public static final Map<String, TeamDayNumType> nameCaches = new HashMap<>();
    public static final Map<Integer, TeamDayNumType> typeCaches = new HashMap<>();

    public String getConfigName() {
        return redisKey;
    }

    static {
        for (TeamDayNumType t : values()) {
            if (nameCaches.containsKey(t.getConfigName())) {
                throw new Error("duplicate action cfg name :" + t.getConfigName());
            }
            nameCaches.put(t.getConfigName(), t);
            if (typeCaches.containsKey(t.getType())) {
                throw new Error("duplicate action cfg type :" + t.getType());
            }
            typeCaches.put(t.getType(), t);
        }
    }

    public static TeamDayNumType convertByName(String cfgName) {
        return nameCaches.get(cfgName);
    }

    public static TeamDayNumType convert(int type) {
        return typeCaches.get(type);
    }

}
