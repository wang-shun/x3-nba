package com.ftkj.manager.battle.model;

import com.ftkj.enums.EActionType;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年2月28日
 * 比赛球员报表
 */
public class ActionReport implements Serializable {
    private static final long serialVersionUID = -8337864599871377320L;
    private long teamId;
    private EActionType type;
    private int playerId;
    //    private int power;//已废弃
    private int v1;
    private int v2;
    private int v3;
    private boolean isForce;

    public ActionReport(long teamId, int playerId, EActionType type,
                        int v1, int v2, int v3,boolean isForce) {
        super();
        this.teamId = teamId;
        this.playerId = playerId;
        this.type = type;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.isForce = isForce;
    }

    public long getTeamId() {
        return teamId;
    }

    public EActionType getType() {
        return type;
    }

    public int getV1() {
        return v1;
    }

    public int getV2() {
        return v2;
    }

    public int getV3() {
        return v3;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getPower() {
        return 0;
    }
    
    

    public boolean isForce() {
      return isForce;
    }

    public void setForce(boolean isForce) {
      this.isForce = isForce;
    }

    @Override
    public String toString() {
        return "{" +
                "\"tid\":" + teamId +
                ", \"type\":" + type.getConfigName() +
                ", \"pid\":" + playerId +
                ", \"v1\":" + v1 +
                ", \"v2\":" + v2 +
                ", \"v3\":" + v3 +
                '}';
    }

}
