package com.ftkj.manager.league;

import java.util.List;

import com.ftkj.manager.prop.PropSimple;

/**
 * @author tim.huang
 * 2017年5月31日
 *
 */
public class LeagueTaskBean {
	
	private int tid;
	private int type;
	private int rate;
	private List<PropSimple> taskProps;//当前任务对应的物品集合
	private List<PropSimple> gifts;
	
	public LeagueTaskBean(int tid, int type, int rate,
			List<PropSimple> taskProps, List<PropSimple> gifts) {
		super();
		this.tid = tid;
		this.type = type;
		this.rate = rate;
		this.taskProps = taskProps;
		this.gifts = gifts;
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

	public List<PropSimple> getTaskProps() {
		return taskProps;
	}

	public void setTaskProps(List<PropSimple> taskProps) {
		this.taskProps = taskProps;
	}

	public List<PropSimple> getGifts() {
		return gifts;
	}

	public void setGifts(List<PropSimple> gifts) {
		this.gifts = gifts;
	}

	@Override
	public String toString() {
		return "LeagueDailyTaskBean [tid=" + tid + ", type=" + type + ", rate="
				+ rate + ", taskProps=" + taskProps
				+ ", gifts=" + gifts + "]";
	}
	
}
