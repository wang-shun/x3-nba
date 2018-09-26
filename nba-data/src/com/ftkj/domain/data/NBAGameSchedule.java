package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Date;

public class NBAGameSchedule implements Serializable{

	private static final long serialVersionUID = -4026157164542999987L;
	public static final int GAME_TYPE_常规赛=0;
	public static final int GAME_TYPE_季后赛=1;
	public static final int GAME_TYPE_全明星赛=2;
	
	private int gameId;
	private int gameType;
	private int homeTeamId;
	private int awayTeamId;
	private int seasonId;
	private int homeScore;
	private int awayScore;
	private Date createTime;
	private Date gameTime;
	
	public NBAGameSchedule() {}

	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
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

	public int getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	public int getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getGameTime() {
		return gameTime;
	}

	public void setGameTime(Date gameTime) {
		this.gameTime = gameTime;
	}

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

}
