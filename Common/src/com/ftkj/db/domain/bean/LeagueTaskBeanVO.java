package com.ftkj.db.domain.bean;


/**
 * @author tim.huang
 * 2017年6月1日
 *
 */
public class LeagueTaskBeanVO {
	private int tid;
	private int type;//1 日常1，2日常2。。。
	private int limitLevel;//联盟开放等级
	private int startHour;
	private int endHour;//如果是-1则第二天0点结束
	private String taskProps;//当前任务对应的物品集合
	private String taskTotalProps;//
	private String gifts;
	private int limitHonor;//领取限制荣誉
	
	
	
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStartHour() {
		return startHour;
	}
	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}
	public int getEndHour() {
		return endHour;
	}
	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}
	public String getTaskProps() {
		return taskProps;
	}
	public void setTaskProps(String taskProps) {
		this.taskProps = taskProps;
	}
	public String getGifts() {
		return gifts;
	}
	
	public String getTaskTotalProps() {
		return taskTotalProps;
	}
	public void setTaskTotalProps(String taskTotalProps) {
		this.taskTotalProps = taskTotalProps;
	}
	public void setGifts(String gifts) {
		this.gifts = gifts;
	}
	public int getLimitHonor() {
		return limitHonor;
	}
	public void setLimitHonor(int limitHonor) {
		this.limitHonor = limitHonor;
	}
	public int getLimitLevel() {
		return limitLevel;
	}
	public void setLimitLevel(int limitLevel) {
		this.limitLevel = limitLevel;
	}
	
}
