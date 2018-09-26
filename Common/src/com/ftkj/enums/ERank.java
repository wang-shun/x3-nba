package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

public enum ERank {
    League("league"),
    Team_Lev("level"),
    Team_Cap("cap"),
    // 战队赛联盟排名
    League_Group_War("League_Group_War"),
    // 战队赛段位排名
    League_Group_Tier("League_Group_Tier_"),;

    private String type;

    ERank(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    //通过ID，取对应的战术枚举
    public static final Map<String, ERank> caches = new HashMap<>();

    static {
        for (ERank et : ERank.values()) {
            caches.put(et.getType(), et);
        }
    }

    public static ERank convert(String name) {
        return caches.get(name);
    }

}
