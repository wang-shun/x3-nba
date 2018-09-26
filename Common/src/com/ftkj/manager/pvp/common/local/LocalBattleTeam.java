package com.ftkj.manager.pvp.common.local;

import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.battle.EBattleType;

/**
 * @author tim.huang
 * 2017年4月25日
 * 本地比赛数据
 */
public class LocalBattleTeam {
	private long teamId;
	private EBattleType battleType;
	private String nodeName;//比赛的服务器节点名称
	private long battleId;
	private EBattleRoomStatus status;
	
	public LocalBattleTeam(long teamId, EBattleType battleType) {
		this.teamId = teamId;
		this.battleType = battleType;
		this.status = EBattleRoomStatus.等待开启;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public long getTeamId() {
		return teamId;
	}
	public EBattleType getBattleType() {
		return battleType;
	}
	public String getNodeName() {
		return nodeName;
	}
	public long getBattleId() {
		return battleId;
	}
	public void setBattleId(long battleId) {
		this.battleId = battleId;
	}
	public EBattleRoomStatus getStatus() {
		return status;
	}
	public void setStatus(EBattleRoomStatus status) {
		this.status = status;
	}
	
	
	
	
}
