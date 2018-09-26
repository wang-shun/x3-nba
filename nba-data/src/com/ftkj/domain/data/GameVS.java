package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Date;

public class GameVS implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5172899052104856457L;
	
	
	private int gameId;
	private int homeTeamId;
	private String home;
	private String homeName;
	private int awayTeamId;
	private String away;
	private String awayName;
	private Date time;
	
	public GameVS(){}
	
	public GameVS(String home,String homeName,String away,String awayName,String gameId,Date date){
		this.home = home;
		this.homeName = homeName;
		this.away = away;
		this.awayName = awayName;
		this.gameId = Integer.parseInt(gameId);
		this.time = date;
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
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public int getHomeTeamId() {
		return homeTeamId;
	}
	public void setHomeTeamId(int homeTeamId) {
		this.homeTeamId = homeTeamId;
	}
	public int getAwayTeamId() {
		return awayTeamId;
	}
	public void setAwayTeamId(int awayTeamId) {
		this.awayTeamId = awayTeamId;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	public String getHomeName() {
		return homeName;
	}

	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}

	public String getAwayName() {
		return awayName;
	}

	public void setAwayName(String awayName) {
		this.awayName = awayName;
	}

	@Override
	public String toString() {
		return "GameVS [away=" + away + ", awayTeamId=" + awayTeamId
				+ ", gameId=" + gameId + ", home=" + home + ", homeTeamId="
				+ homeTeamId + ", time=" + time + "]";
	}
	
}
