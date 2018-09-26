package com.ftkj.x3.client.robot;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 机器人配置和状态
 *
 * @author luch
 */
public class RobotTeamConfig {
    private final int shardId;
    private final long accountId;
    private final String accountIdStr;

    /** 购买体力次数.0或不填:最大次数 >0 特定次数 */
    private final int exchangeEnergyNum;
    /** 购买商城道具.0或不填:购买所有; 1只购买经费道具 */
    private final int shop;
    /** 屏蔽聊天推送.0或不填屏蔽; 1不屏蔽 */
    private final int chatPush;
    /** 是否领取体力. 0或不填:不领取 1:领取; */
    private final int receiveEnergy;
    //统计信息
    private final AtomicLong loginCount = new AtomicLong();
    private final AtomicLong logoutCount = new AtomicLong();

    public RobotTeamConfig(int shardId,
                           long accountId,
                           int exchangeEnergyNum,
                           int shop,
                           int chatPush,
                           int receiveEnergy) {
        this.shardId = shardId;
        this.accountId = accountId;
        this.accountIdStr = String.valueOf(accountId);

        this.exchangeEnergyNum = exchangeEnergyNum;
        this.shop = shop;
        this.chatPush = chatPush;
        this.receiveEnergy = receiveEnergy;
    }

    public static RobotTeamConfig newDefault(int shardId, long accountId) {
        return new RobotTeamConfig(shardId, accountId, 0, 0, 0, 1);
    }

    public int getShardId() {
        return shardId;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getAccountIdStr() {
        return accountIdStr;
    }

    public int getExchangeEnergyNum() {
        return exchangeEnergyNum;
    }

    public boolean canExchangeEnergy() {
        return this.exchangeEnergyNum >= 0;
    }

    public int getShop() {
        return shop;
    }

    public int getChatPush() {
        return chatPush;
    }

    public boolean disableChatPush() {
        return chatPush == 0;
    }

    public int getReceiveEnergy() {
        return receiveEnergy;
    }

    public boolean canReceiveEnergy() {
        return receiveEnergy == 1;
    }

    @Override
    public String toString() {
        return "{" +
                "\"shardId\":" + shardId +
                ", \"accountId\":" + accountId +
                ", \"exchangeEnergyNum\":" + exchangeEnergyNum +
                ", \"shop\":" + shop +
                ", \"chatPush\":" + chatPush +
                ", \"receiveEnergy\":" + receiveEnergy +
                '}';
    }

}
