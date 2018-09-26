package com.ftkj.manager.league;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年5月22日
 * 联盟动态日志
 */
public class LeagueLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String teamName;
	private String context;
	private String createTime;
	
	public LeagueLog() {
	}
	public LeagueLog(String teamName, String context, String createTime) {
		super();
		this.teamName = teamName;
		this.context = context;
		this.createTime = createTime;
	}
	public String getTeamName() {
		return teamName;
	}
	public String getContext() {
		return context;
	}
	public String getCreateTime() {
		return createTime;
	}
	
	
	
	
}
