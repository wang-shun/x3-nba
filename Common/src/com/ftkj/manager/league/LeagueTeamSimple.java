package com.ftkj.manager.league;

import java.io.Serializable;

import com.ftkj.util.DateTimeUtil;

/**
 * @author tim.huang
 * 2017年5月23日
 * 联盟玩家简易数据
 */ 
public class LeagueTeamSimple implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long teamId;
	private String teamName;
	private int level;
	private String lastLoginTime;
	private int score;
	private int leagueLevel;
	
	public LeagueTeamSimple(long teamId, String teamName, int level,int leagueLevel) {
		this.teamId = teamId;
		this.teamName = teamName;
		this.level = level;
		this.leagueLevel = leagueLevel;
		this.score = 0;
		login();
	}
	
	public void updateLeagueLevel(int val){
		this.leagueLevel = val;
	}
	
	public void updateTeamName(String teamName){
        this.teamName = teamName;
    }
	
	public void updateLevel(int val){
		this.level = val;
	}
	
	public void updateScore(int val){
		this.score = this.score + val;
	}
	
	public void login(){
		this.lastLoginTime = DateTimeUtil.getNowTimeDate();
	}
	
	public int getLeagueLevel() {
		return leagueLevel;
	}

	public long getTeamId() {
		return teamId;
	}
	public String getTeamName() {
		return teamName == null ? "" : teamName;
	}
	public int getLevel() {
		return level;
	}
	public String getLastLoginTime() {
		return lastLoginTime == null ? "" : lastLoginTime;
	}
	public int getScore() {
		return score;
	}

	@Override
	public String toString() {
		return "LeagueTeamSimple [teamId=" + teamId + ", teamName=" + teamName + ", level=" + level + ", leagueLevel="
				+ leagueLevel + "]";
	}
	
}
