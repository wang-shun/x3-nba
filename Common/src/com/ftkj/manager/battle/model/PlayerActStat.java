package com.ftkj.manager.battle.model;

/**
 * @author tim.huang
 * 2017年2月16日
 * 球员比赛行为统计数据
 */
public final class PlayerActStat extends ActionStatistics {
    private static final long serialVersionUID = 1L;
    private int playerRid;

    public PlayerActStat(int playerRid) {
        this.playerRid = playerRid;
    }

    public int getPlayerRid() {
        return playerRid;
    }

}
