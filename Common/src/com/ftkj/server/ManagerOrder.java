package com.ftkj.server;

import com.ftkj.manager.BaseManager;

/**
 * @author tim.huang
 * 2016年8月8日
 * Manager类初始顺序管理
 */
public enum ManagerOrder {
    Cache(Holder.DEFAULT_ORDER - 12_0000),
    Team(Holder.DEFAULT_ORDER - 11_0000),
    Email(Holder.DEFAULT_ORDER - 10_0000),
    Battle(Holder.DEFAULT_ORDER - 9_0000),
    PVEBattle(Holder.DEFAULT_ORDER - 8_0000),
    PVPBattle(Holder.DEFAULT_ORDER - 7_0000),
    Buff(Holder.DEFAULT_ORDER - 6_0000),
    Active(Holder.DEFAULT_ORDER - 5_0000),
    Player(Holder.DEFAULT_ORDER - 4_0000),
    Node(Holder.DEFAULT_ORDER - 3_0000),
    LeagueHonor(Holder.DEFAULT_ORDER - 2_0000),
    League(Holder.DEFAULT_ORDER - 1_0000),
    KnockoutMatch(Holder.DEFAULT_ORDER - 1_0000),
    Default(Holder.DEFAULT_ORDER),
    /** 最后关闭系统 */
    Shutdown(Holder.DEFAULT_ORDER + 1_0000);

    private final int order;

    ManagerOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public static final class Holder {
        public static final int START_ORDER = 1;
        public static final int DEFAULT_ORDER = BaseManager.DEFAULT_INIT_AND_SHUTDOWN_ORDER;
    }
}
