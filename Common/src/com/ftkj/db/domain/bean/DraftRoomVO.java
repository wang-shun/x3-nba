package com.ftkj.db.domain.bean;


public class DraftRoomVO {
	private int roomLevel;
	private int maxPlayerCount;
	private int limitMinLevel;
	private int limitMaxLevel;
	private int dropId; 
	private String props;
	private int maxRoom;
	private String time;
	private int cdTime;
	
	public int getRoomLevel() {
		return roomLevel;
	}
	public void setRoomLevel(int roomLevel) {
		this.roomLevel = roomLevel;
	}
	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}
	public void setMaxPlayerCount(int maxPlayerCount) {
		this.maxPlayerCount = maxPlayerCount;
	}
	public int getLimitMinLevel() {
		return limitMinLevel;
	}
	public void setLimitMinLevel(int limitMinLevel) {
		this.limitMinLevel = limitMinLevel;
	}
	public int getLimitMaxLevel() {
		return limitMaxLevel;
	}
	public void setLimitMaxLevel(int limitMaxLevel) {
		this.limitMaxLevel = limitMaxLevel;
	}
	public int getDropId() {
		return dropId;
	}
	public void setDropId(int dropId) {
		this.dropId = dropId;
	}
	public String getProps() {
		return props;
	}
	public void setProps(String props) {
		this.props = props;
	}
	public int getMaxRoom() {
		return maxRoom;
	}
	public void setMaxRoom(int maxRoom) {
		this.maxRoom = maxRoom;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getCdTime() {
		return cdTime;
	}
	public void setCdTime(int cdTime) {
		this.cdTime = cdTime;
	}
	
}
