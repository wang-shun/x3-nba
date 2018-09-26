package com.ftkj.manager.pvp.common;

import java.io.Serializable;

import com.ftkj.enums.EBattleRoomStatus;

/**
 * @author tim.huang
 * 2017年4月25日
 *
 */
public class BattleRoom implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private int roomId;
	private long battleId;
	private BattleRoomTeam home;
	private BattleRoomTeam away;
	private EBattleRoomStatus status;
	private int level;
	//
	private String nodeName;
	
	public BattleRoom(int roomId, long battleId,BattleRoomTeam home, BattleRoomTeam away,
			EBattleRoomStatus status, int level, String nodeName) {
		super();
		this.roomId = roomId;
		this.home = home;
		this.away = away;
		this.status = status;
		this.level = level;
		this.nodeName = nodeName;
		this.battleId = battleId;
	}
	
	public long getBattleId() {
		return battleId;
	}

	public void setBattleId(long battleId) {
		this.battleId = battleId;
	}

	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public BattleRoomTeam getHome() {
		return home;
	}
	public void setHome(BattleRoomTeam home) {
		this.home = home;
	}
	public BattleRoomTeam getAway() {
		return away;
	}
	public void setAway(BattleRoomTeam away) {
		this.away = away;
	}
	public EBattleRoomStatus getStatus() {
		return status;
	}
	public void setStatus(EBattleRoomStatus status) {
		this.status = status;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
}
