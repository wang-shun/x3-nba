package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeamScore implements Serializable{
	private static final long serialVersionUID = 6951894470023819034L;
	public String gameId;
	public long teamId;
	public String team;
	public List<Integer>scores;
	
	public TeamScore(){}
	
	public int getTotalScore(){
		return scores.get(scores.size()-1);
	}
	public TeamScore(String team) {
		this.team=team;
		this.scores=new ArrayList<Integer>();
	}
	
	
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public List<Integer> getScores() {
		return scores;
	}
	public void setScores(List<Integer> scores) {
		this.scores = scores;
	}
	@Override
	public String toString() {
		return "Score [team=" + team + ", scores=" + scores+ "]";
	}
}
