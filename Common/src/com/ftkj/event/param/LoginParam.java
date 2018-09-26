package com.ftkj.event.param;

import org.joda.time.DateTime;

public class LoginParam {

	public long teamId;
	public DateTime loginTime;
	/**
	 * 本次离线时间
	 * 秒数
	 */
	public long offlineSecond;
	public boolean isTodayFirst;
	
	public LoginParam(long teamId, DateTime loginTime, long offlineTime, boolean isTodayFirst) {
		super();
		this.teamId = teamId;
		this.loginTime = loginTime;
		this.offlineSecond = offlineTime;
		this.isTodayFirst = isTodayFirst;
	}

}
