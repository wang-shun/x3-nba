package com.ftkj.manager.battle.model;

/**
 * 球员小节数据
 */
public class PlayerStepActStat extends StepActionStatistics {
    private static final long serialVersionUID = -5991834210228565411L;
    private final int playerId;

    public PlayerStepActStat(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
