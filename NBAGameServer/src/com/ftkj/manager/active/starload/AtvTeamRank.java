package com.ftkj.manager.active.starload;

import org.joda.time.DateTime;

/**
 * 活动排名通用对象
 * @author Jay
 * @time:2017年9月8日 下午4:35:06
 */
public class AtvTeamRank {

	private long teamId;
	/**
	 * 排名比较用
	 */
	private int value;
	/**
	 * 真是排名 ， 1开始
	 */
	private int rank;
	private DateTime updateTime;
	
	public AtvTeamRank(long teamId, DateTime updateTime) {
		super();
		this.teamId = teamId;
		this.updateTime = updateTime;
	}

	public AtvTeamRank(long teamId, int value, DateTime updateTime) {
		super();
		this.teamId = teamId;
		this.value = value;
		this.updateTime = updateTime;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public DateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(DateTime updateTime) {
		this.updateTime = updateTime;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	
	
}
