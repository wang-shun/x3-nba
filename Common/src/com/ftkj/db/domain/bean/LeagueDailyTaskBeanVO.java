package com.ftkj.db.domain.bean;


/**
 * 联盟成员的日常任务.
 * @author mr.lei
 * 2018年9月4日
 *
 */
public class LeagueDailyTaskBeanVO {
	/**任务唯一Id*/
	private int tid;
	/**任务类型*/
	private int type;
	/**同一类型任务刷新时随机到的概率（千分比）*/
	private int rate;
	/**捐献勋章id:捐献勋章数量*/
	private String taskProps;
	/**奖励道具id:数量,道具id:数量*/
	private String gifts;
	
	public LeagueDailyTaskBeanVO(){
		
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
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
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
	public void setGifts(String gifts) {
		this.gifts = gifts;
	}
	
}
