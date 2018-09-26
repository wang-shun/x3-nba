package com.ftkj.manager.league;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年5月18日
 * 简易联盟对象
 */
public class LeagueSimple implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int leagueId;
	private String leagueName;
	private int level;
	
	public LeagueSimple(int leagueId, String leagueName, int level) {
		super();
		this.leagueId = leagueId;
		this.leagueName = leagueName;
		this.level = level;
	}
	public int getLeagueId() {
		return leagueId;
	}
	public String getLeagueName() {
		return leagueName;
	}
	public int getLevel() {
		return level;
	}
	
}
