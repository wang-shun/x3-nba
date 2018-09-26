package com.ftkj.manager.league.groupwar;

import java.io.Serializable;

public class LGroupApply implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private int leagueId;
	private int groupId;
	private long teamId;
	private float cap;
	
	public LGroupApply(long id, int leagueId, int groupId, long teamId, float cap) {
		super();
		this.id = id;
		this.leagueId = leagueId;
		this.groupId = groupId;
		this.teamId = teamId;
		this.cap = cap;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public float getCap() {
		return cap;
	}

	public void setCap(float cap) {
		this.cap = cap;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	
	
}
