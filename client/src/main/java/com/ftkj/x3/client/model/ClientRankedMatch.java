package com.ftkj.x3.client.model;

import com.ftkj.manager.match.RankedMatch;

public class ClientRankedMatch extends RankedMatch {
    /** 每日奖励是否已经领取(true, 已经领取) */
    private boolean dailyReward;

    public boolean isDailyReward() {
        return dailyReward;
    }

    public void setDailyReward(boolean dailyReward) {
        this.dailyReward = dailyReward;
    }
}
