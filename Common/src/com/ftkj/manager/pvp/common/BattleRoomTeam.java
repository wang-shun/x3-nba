package com.ftkj.manager.pvp.common;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年4月25日
 * 普通比赛房间玩家信息
 */
public class BattleRoomTeam implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long teamId;
	private String name;
	private String leagueName;
	private int level;
	private String teamNodeName;
	private String logo;
	public BattleRoomTeam(long teamId, String name, String leagueName,
			int level,  String teamNodeName, String logo) {
		super();
		this.teamId = teamId;
		this.name = name;
		this.leagueName = leagueName;
		this.level = level;
		this.teamNodeName = teamNodeName;
		this.logo = logo;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLeagueName() {
		return leagueName;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getTeamNodeName() {
		return teamNodeName;
	}
	public void setTeamNodeName(String teamNodeName) {
		this.teamNodeName = teamNodeName;
	}
	
}
