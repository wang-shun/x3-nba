package com.ftkj.manager.league.groupwar;

import java.io.Serializable;

import com.ftkj.db.domain.group.LeagueGroupTeamPO;


public class GroupTeam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LeagueGroupTeamPO po;
	private boolean isReady = false; // 是否准备
	
	
	/**
	 * 队长必然是准备的
	 * @return
	 */
	public boolean isReady() {
		return po.getLevel() == 1 ? true : isReady;
	}


	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public GroupTeam(int leagueId, int groupId, long teamId, int position, int level) {
		super();
		this.po = new LeagueGroupTeamPO(leagueId, groupId, teamId, position, level);
	}

	public GroupTeam(LeagueGroupTeamPO po) {
		super();
		this.po = po;
	}


	public int getLevel() {
		return po.getLevel();
	}


	public long getTeamId() {
		return po.getTeamId();
	}

	public void save() {
		po.save();
	}
	
	public void del() {
		po.setStatus(-1);
		po.save();
	}


	public int getPrivity() {
		return po.getPrivity();
	}


	public void setPrivity(int privity) {
		po.setPrivity(privity);
	}

	
	public void setLevel(int level) {
		po.setLevel(level);
	}

	public int getPosition() {
		return po.getPosition();
	}


	public void setPosition(int position) {
		po.setPosition(position);
	}
	
	
	public long getLeagueGroupId() {
		return (this.po.getLeagueId()) * 1000  + this.po.getGroupId();
	}
	
}
