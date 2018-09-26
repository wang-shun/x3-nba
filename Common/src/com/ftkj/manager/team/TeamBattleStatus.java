package com.ftkj.manager.team;

import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.util.DateTimeUtil;

import org.joda.time.DateTime;

import java.io.Serializable;

public class TeamBattleStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 比赛类型 */
    private volatile EBattleType type;
    /** 比赛ID */
    private volatile long battleId;
    /** 节点ip */
    private String nodeIp;
    /** 节点名称 */
    private String nodeName;
    /** 比赛创建时间 */
    private DateTime createTime;
    /** 比赛开始时间 */
    private DateTime startTime;
    /** 比赛房间状态 */
    private volatile EBattleRoomStatus status;

    private static final int TimeOut = 10 * 60;

    public TeamBattleStatus() {
    }

    public TeamBattleStatus(EBattleType type, long battleId, String nodeIp) {
        super();
        this.type = type;
        this.battleId = battleId;
        this.nodeIp = nodeIp;
        this.status = EBattleRoomStatus.等待开启;
        this.createTime = DateTime.now();
        this.startTime = DateTime.now();
    }

    public void reset() {
        this.battleId = 0;
        this.nodeIp = "";
        this.status = EBattleRoomStatus.默认未匹配;
    }

    public EBattleRoomStatus getStatus() {
        return status;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void updateStatus(EBattleRoomStatus status) {
        this.status = status;
        if (this.status == EBattleRoomStatus.比赛中) {
            this.startTime = DateTime.now();
        } else if (this.status == EBattleRoomStatus.等待开启) {
            this.createTime = DateTime.now();
        } else if (this.status == EBattleRoomStatus.比赛结束) {
            reset();
        }
    }

    /** 比赛是否已经超时结束. true 是 */
    public boolean isTimeOut() {
        if (this.status == EBattleRoomStatus.比赛结束) {
            return true;
        }
        if (this.status == EBattleRoomStatus.默认未匹配) {
            return false;
        }
        if (this.status == EBattleRoomStatus.比赛中 && DateTimeUtil.secondBetween(this.startTime, DateTime.now()) >= TimeOut) {
            return true;
        }
        if (this.status == EBattleRoomStatus.等待开启 && DateTimeUtil.secondBetween(this.createTime, DateTime.now()) >= TimeOut) {
            return true;
        }
        return false;
    }
    
    public boolean inBattle() {
        return battleId != 0 && !isTimeOut();
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public EBattleType getType() {
        return type;
    }

    public void setType(EBattleType type) {
        this.type = type;
    }

    public long getBattleId() {
        return battleId;
    }

    public void setBattleId(long battleId) {
        this.battleId = battleId;
    }

    public String jsonString() {
        return "{" +
            "\"type\":" + type +
            ", \"battleId\":" + battleId +
            ", \"node\":\"" + nodeName + "\"" +
            ", \"nodeip\":\"" + nodeIp + "\"" +
            ", \"createTime\":" + createTime +
            ", \"startTime\":" + startTime +
            ", \"status\":" + status +
            '}';
    }

    @Override
    public String toString() {
        return jsonString();
    }
}
