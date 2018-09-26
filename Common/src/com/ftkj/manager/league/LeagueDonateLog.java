package com.ftkj.manager.league;

import com.ftkj.util.DateTimeUtil;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年5月22日
 * 联盟捐赠日志
 */
public class LeagueDonateLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long teamId;
	private String teamName;
	private String time;
	private int money;
	
	public LeagueDonateLog(long teamId, String teamName, int money) {
		super();
		this.teamId = teamId;
		this.teamName = teamName;
		this.time = DateTimeUtil.getNowTimeDateYmdhm();
		this.money = money;
	}
	public long getTeamId() {
		return teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public String getTime() {
		return time;
	}
	public int getMoney() {
		return money;
	}
	
	
	
}
