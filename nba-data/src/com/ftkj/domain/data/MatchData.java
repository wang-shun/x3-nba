package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MatchData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7817775800967067153L;
	/**比赛结束*/
	public static final String GAME_FINAL = "Final";
	//
	private String gameBoxId;
	private int seasonId;
	private boolean isPlayoff;
	private int gameType;
	private Date gameDate;
	private int homeTeamId;
	private int awayTeamId;
	private String state;
	private TeamScore scoreHome;
	private TeamScore scoreAway;
	private List<PlayerStats>playerScoreHome;
	private List<PlayerStats>playerScoreAway;

	public MatchData() {
		playerScoreHome=new ArrayList<PlayerStats>();
		playerScoreAway=new ArrayList<PlayerStats>();
	}
	public boolean isPlayoff() {
		return isPlayoff;
	}
	public void setPlayoff(boolean isPlayoff) {
		this.isPlayoff = isPlayoff;
	}
	public int getHomeScore() {
		return scoreHome.getTotalScore();
	}
	public int getAwayScore(){
		return scoreAway.getTotalScore();
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
	public int getSeasonId() {
		return seasonId;
	}
	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}
	public String getGameBoxId() {
		return gameBoxId;
	}
	public void setGameBoxId(String gameBoxId) {
		this.gameBoxId = gameBoxId;
	}
	public Date getGameDate() {
		return gameDate;
	}
	public void setGameDate(Date gameDate) {
		this.gameDate = gameDate;
	}
	public TeamScore getScoreHome() {
		return scoreHome;
	}
	public void setScoreHome(TeamScore scoreHome) {
		this.scoreHome = scoreHome;
		scoreHome.gameId=gameBoxId;
	}
	public TeamScore getScoreAway() {
		return scoreAway;
	}
	public void setScoreAway(TeamScore scoreAway) {
		this.scoreAway = scoreAway;
		scoreAway.gameId=gameBoxId;
	}
	public List<PlayerStats> getPlayerScoreHome() {
		return playerScoreHome;
	}
	public void setPlayerScoreHome(List<PlayerStats> playerScoreHome) {
		this.playerScoreHome = playerScoreHome;
	}
	public List<PlayerStats> getPlayerScoreAway() {
		return playerScoreAway;
	}
	public void setPlayerScoreAway(List<PlayerStats> playerScoreAway) {
		this.playerScoreAway = playerScoreAway;
	}
	public int getGameType() {
		return gameType;
	}
	public void setGameType(int gameType) {
		this.gameType = gameType;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
