package com.ftkj.manager.custom;

import org.joda.time.DateTime;

import com.ftkj.db.domain.CustomTeamPO;

/**
 * @author tim.huang
 * 2017年8月3日
 *
 */
public class CustomTeam {
	
	private CustomTeamPO team;
	
	public CustomTeam(CustomTeamPO team) {
		super();
		this.team = team;
	}
	
	public static CustomTeam createCustomTeam(long teamId){
		CustomTeamPO po = new CustomTeamPO();
		po.setTeamId(teamId);
		po.setCreateTime(DateTime.now());
		po.setMoney(500);
		po.save();
		return new CustomTeam(po);
	}

	public void updateMoney(int val ){
		this.team.setMoney(this.team.getMoney()+val);
		this.team.save();
	}
	
	public long getTeamId() {
		return this.team.getTeamId();
	}

	public int getMoney() {
		return this.team.getMoney();
	}
}
