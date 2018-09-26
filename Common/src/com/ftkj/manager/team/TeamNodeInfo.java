package com.ftkj.manager.team;

import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2017年7月11日
 * 节点玩家详细信息对象
 */
public class TeamNodeInfo extends TeamNode {

	private String teamName;
	private String logo;
	private int level;
	//
	private int power;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TeamNodeInfo(long teamId, String nodeName, String teamName,
			String logo, int level) {
		super(teamId, nodeName);
		this.teamName = teamName;
		this.logo = logo;
		this.level = level;
	}
	
	public TeamNodeInfo(long teamId, String teamName,
			String logo, int level) {
		super(teamId, GameSource.serverName);
		this.teamName = teamName;
		this.logo = logo;
		this.level = level;
	}
	
	public TeamNodeInfo(long teamId, String teamName,
			String logo, int level,int power) {
		super(teamId, GameSource.serverName);
		this.teamName = teamName;
		this.logo = logo;
		this.level = level;
		this.power = power;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getPower() {
		return power;
	}

	public String getTeamName() {
		return teamName;
	}

	public String getLogo() {
		return logo;
	}

	public int getLevel() {
		return level;
	}

}
