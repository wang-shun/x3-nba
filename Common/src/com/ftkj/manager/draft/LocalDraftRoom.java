package com.ftkj.manager.draft;

import org.joda.time.DateTime;

/**
 * @author tim.huang
 * 2017年5月4日
 * 本地选秀房间
 */
public class LocalDraftRoom {
	private int roomId;
	private DateTime startTime;
	private int roomLevel;
	
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public DateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}
	public int getRoomLevel() {
		return roomLevel;
	}
	public void setRoomLevel(int roomLevel) {
		this.roomLevel = roomLevel;
	}
	
	
}
