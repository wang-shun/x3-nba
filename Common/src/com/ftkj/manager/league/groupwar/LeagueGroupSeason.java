package com.ftkj.manager.league.groupwar;

import org.joda.time.DateTime;

import com.ftkj.db.domain.group.LeagueGroupSeasonPO;

public class LeagueGroupSeason {

	private LeagueGroupSeasonPO po;

	public LeagueGroupSeason(LeagueGroupSeasonPO po) {
		super();
		this.po = po;
	}
	
	public int getId() {
		return po.getId();
	}
	
	/**
	 * 更新状态
	 * @param status
	 */
	public void updateStatus(int status) {
		this.po.setStatus(status);
		this.po.save();
	}
	
	public int getStatus() {
		return po.getStatus();
	}
	
	public DateTime getEndTime() {
		return po.getEndTime();
	}
	
	public DateTime getStartTime() {
		return po.getStartTime();
	}

	public void save() {
		po.save();
	}
	
}
