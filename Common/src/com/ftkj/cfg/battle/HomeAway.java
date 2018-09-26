package com.ftkj.cfg.battle;

/**
 *
 */
public enum HomeAway {
    /** 主场 */
    home,
    /** 客场 */
    away;

    public static HomeAway convert(String homeAway) {
        return "home".equalsIgnoreCase(homeAway) ? HomeAway.home :
                ("away".equalsIgnoreCase(homeAway) ? HomeAway.away : null);
    }
}
