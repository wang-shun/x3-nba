package com.ftkj.manager.setting;

import java.io.Serializable;

public class TeamConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long teamId;
	
	/**
	 * 战术默认进攻
	 */
	private String tacticsJg;
	
	/**
	 * 战术默认防守
	 */
	private String tacticsFs;
	
	public TeamConfig(long teamId) {
		super();
		this.teamId = teamId;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public String getTacticsJg() {
		return tacticsJg;
	}

	public void setTacticsJg(String tacticsJg) {
		this.tacticsJg = tacticsJg;
	}

	public String getTacticsFs() {
		return tacticsFs;
	}

	public void setTacticsFs(String tacticsFs) {
		this.tacticsFs = tacticsFs;
	}

	
	
}
