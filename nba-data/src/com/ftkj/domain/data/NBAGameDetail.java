package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Date;

public class NBAGameDetail implements Serializable{
	
	private static final long serialVersionUID = -1758576545604011023L;
	
	private String gameBoxId;
	private String homeTeamName;
	private String awayTeamName;
	
	private int homeTeamId;
	private int awayTeamId;
	
	private int homeTeamScore;
	private int awayTeamScore;
	
	private Date dateTime;
	
	private String status="";
		
	public NBAGameDetail() {}	
	
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
	public String getHomeTeamName() {
		return homeTeamName;
	}
	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}
	public String getAwayTeamName() {
		return awayTeamName;
	}
	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}
	public int getHomeTeamScore() {
		return homeTeamScore;
	}
	public void setHomeTeamScore(int homeTeamScore) {
		this.homeTeamScore = homeTeamScore;
	}
	public int getAwayTeamScore() {
		return awayTeamScore;
	}
	public void setAwayTeamScore(int awayTeamScore) {
		this.awayTeamScore = awayTeamScore;
	}	
	public String getStatus() {
		return status;
	}	
	public void setStatus(String status) {
		this.status = status;
	}		
	public String getGameBoxId() {
		return gameBoxId;
	}
	public void setGameBoxId(String gameBoxId) {
		this.gameBoxId = gameBoxId;
	}	
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public String toString() {
		return "NBAGameDetail [awayTeamId=" + awayTeamId + ", awayTeamName="
				+ awayTeamName + ", awayTeamScore=" + awayTeamScore
				+ ", dateTime=" + dateTime + ", gameBoxId=" + gameBoxId
				+ ", homeTeamId=" + homeTeamId + ", homeTeamName="
				+ homeTeamName + ", homeTeamScore=" + homeTeamScore
				+ ", status=" + status + "]";
	}
}
