package com.ftkj.db.domain.bean;

public class TaskBeanVO {
	private int tid;
	private int type;
	private String name;
	private String props;
	private int limitLevel;
	private int showLevel;
	private String description;
	private int teamPurpose;
	/** 任务类型*/
	private int dayType;
	/** 任务星数*/
	private int star;
	private int pro;
	private int active;
	private int last_task;
	
	
	public int getLast_task() {
		return last_task;
	}
	public void setLast_task(int last_task) {
		this.last_task = last_task;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public int getShowLevel() {
		return showLevel;
	}
	public void setShowLevel(int showLevel) {
		this.showLevel = showLevel;
	}
	public int getDayType() {
		return dayType;
	}
	public void setDayType(int dayType) {
		this.dayType = dayType;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	public int getPro() {
		return pro;
	}
	public void setPro(int pro) {
		this.pro = pro;
	}
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
	public String getName() {
		return name;
	}
	public String getProps() {
		return props;
	}
	public void setProps(String props) {
		this.props = props;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLimitLevel() {
		return limitLevel;
	}
	public void setLimitLevel(int limitLevel) {
		this.limitLevel = limitLevel;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getTeamPurpose() {
		return teamPurpose;
	}
	public void setTeamPurpose(int teamPurpose) {
		this.teamPurpose = teamPurpose;
	}
}
