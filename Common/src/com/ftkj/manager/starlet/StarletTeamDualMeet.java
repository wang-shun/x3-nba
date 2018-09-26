package com.ftkj.manager.starlet;

/**
 * 新秀对抗赛数据
 * @author qin.jiang
 */
public class StarletTeamDualMeet{

    /** 球队ID */
    private long teamId;
    
    /** 对抗赛ID */
    private long battleId;

    /** 对抗赛类型 */
    private int type;

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }  

    public long getBattleId() {
        return battleId;
    }

    public void setBattleId(long battleId) {
        this.battleId = battleId;
    }
}
