package com.ftkj.db.domain;

import org.joda.time.DateTime;

import com.ftkj.util.DateTimeUtil;

public class NBAPKSchedule {
	private int gameId;
	private int seasonId;
	private int status;
	private int gameType;
	private int homeTeamId;
	private int awayTeamId;
	private int homeTeamScore;
	private int awayTeamScore;
	private DateTime gameTime;
	
	public int getWinTeamId(){
		if(status == 0) {
			return 0;
		}
		return homeTeamScore>awayTeamScore?homeTeamId:awayTeamId;
	}
	
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public int getSeasonId() {
		return seasonId;
	}
	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getGameType() {
		return gameType;
	}
	public void setGameType(int gameType) {
		this.gameType = gameType;
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
	public DateTime getGameTime() {
		return gameTime;
	}
	public void setGameTime(DateTime gameTime) {
		this.gameTime = gameTime;
	}

	@Override
	public String toString() {
		return "\n NBAPKSchedule [gameId=" + gameId + ", seasonId=" + seasonId + ", status=" + status + ", gameType="
				+ gameType + ", homeTeamId=" + homeTeamId + ", awayTeamId=" + awayTeamId + ", homeTeamScore="
				+ homeTeamScore + ", awayTeamScore=" + awayTeamScore + ", gameTime=" + DateTimeUtil.getStringSql(gameTime) + "]";
	}
	
	
	
}
