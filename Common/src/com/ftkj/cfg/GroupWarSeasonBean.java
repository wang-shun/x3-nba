package com.ftkj.cfg;

import org.joda.time.DateTime;

/**
 * 联盟组队赛，赛季配置
 * @author lin.lin
 *
 */
public class GroupWarSeasonBean {

	private int id;
	private String name;
	private DateTime startTime;
	private DateTime endTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public DateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}
	public DateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
