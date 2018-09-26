package com.ftkj.db.domain.group;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

public class LeagueGroupTeamPO extends AsynchronousBatchDB {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int leagueId;
	private int groupId; 
	private long teamId;
	private int privity; //默契值
	private int position; //位置
	private int level; //职位, 1是队长， 0是普通队员
	private int status; // 0 正常 -1会被删掉
	
	public LeagueGroupTeamPO(int leagueId, int groupId, long teamId, int position, int level) {
		super();
		this.leagueId = leagueId;
		this.groupId = groupId;
		this.teamId = teamId;
		this.position = position;
		this.level = level;
	}

	public LeagueGroupTeamPO() {
	}

	public int getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getPrivity() {
		return privity;
	}

	public void setPrivity(int privity) {
		this.privity = privity;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.leagueId, this.groupId, this.teamId, this.privity, this.position, this.level, this.status);
	}

	@Override
	public String getRowNames() {
		return "league_id, group_id, team_id, privity, position, level, status";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.leagueId, this.groupId, this.teamId, this.privity, this.position, this.level, this.status);
    }

	@Override
	public String getTableName() {
		return "t_u_league_group_team";
	}

	@Override
	public void del() {
		this.status = -1;
		this.save();
	}


}
