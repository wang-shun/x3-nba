package com.ftkj.manager.team;

import java.io.Serializable;

import com.ftkj.util.DateTimeUtil;

/**
 * @author tim.huang
 * 2017年5月18日 
 * 简易球队对象
 */
public class TeamSimple implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long teamId;
	private String teamName;
	private String logo;
	private int level;
	private String createTime;
	
	public TeamSimple(long teamId, String teamName, String logo,int level) {
		super();
		this.teamId = teamId;
		this.teamName = teamName;
		this.logo = logo;
		this.level = level;
		this.createTime = DateTimeUtil.getNowTimeDate();
	}
	
	public int getLevel() {
		return level;
	}

	public String getCreateTime() {
		return createTime;
	}
	public long getTeamId() {
		return teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public String getLogo() {
		return logo;
	}
	
	
}
