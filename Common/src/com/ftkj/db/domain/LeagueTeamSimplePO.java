package com.ftkj.db.domain;

/**
 * @author tim.huang
 * 2017年5月31日
 *
 */
public class LeagueTeamSimplePO {
	private int leagueId;
	private long teamId;
	
	
	public LeagueTeamSimplePO() {
	}
	public int getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

}
