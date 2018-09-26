package com.ftkj.manager.trade;

public interface OrderTrade {
    long getTeamId();
    boolean isStickTop();
    int getMoney();
    int getPrice();
    long getEndTimeMillis();
}
