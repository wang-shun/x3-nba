package com.ftkj.db.domain;

import org.joda.time.DateTime;

public class NBAVS {
    /** 比赛ID*/
	private int gameId;
	/** 主场球队ID*/
	private String home;
	/** 客场球队ID*/
	private String away;
	/** 时间*/
	private DateTime dateTime;	
	
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String getAway() {
		return away;
	}
	public void setAway(String away) {
		this.away = away;
	}
	public DateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	
	
}
