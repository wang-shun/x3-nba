package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class NBAGameData implements Serializable{
	
	private static final long serialVersionUID = -682861345801884280L;
	private int gameId;
	private NBAGameSchedule gameSchedule;
	private NBATeamScore homeTeamScore;
	private NBATeamScore awayTeamScore;
	private List<NBAPlayerScore> homePlayerScores;
	private List<NBAPlayerScore> awayPlayerScores;
	
	
	public NBAGameData() {
		homePlayerScores=new ArrayList<NBAPlayerScore>();
		awayPlayerScores=new ArrayList<NBAPlayerScore>();
	}
	
	public int getGameId() {
		return gameId;
	}
	
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	
	public NBAGameSchedule getGameSchedule() {
		return gameSchedule;
	}
	
	public void setGameSchedule(NBAGameSchedule gameSchedule) {
		this.gameSchedule = gameSchedule;
	}
	
	public NBATeamScore getHomeTeamScore() {
		return homeTeamScore;
	}
	
	public void setHomeTeamScore(NBATeamScore homeTeamScore) {
		this.homeTeamScore = homeTeamScore;
	}
	
	public NBATeamScore getAwayTeamScore() {
		return awayTeamScore;
	}
	
	public void setAwayTeamScore(NBATeamScore awayTeamScore) {
		this.awayTeamScore = awayTeamScore;
	}
	
	public List<NBAPlayerScore> getHomePlayerScores() {
		return homePlayerScores;
	}
	
	public void setHomePlayerScores(List<NBAPlayerScore> homePlayerScores) {
		this.homePlayerScores = homePlayerScores;
	}
	
	public List<NBAPlayerScore> getAwayPlayerScores() {
		return awayPlayerScores;
	}
	
	public void setAwayPlayerScores(List<NBAPlayerScore> awayPlayerScores) {
		this.awayPlayerScores = awayPlayerScores;
	}
}
