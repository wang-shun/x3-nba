package com.ftkj.db.domain.group;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

public class LeagueGroupPO extends AsynchronousBatchDB {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int leagueId;
	private int groupId;
	private String name;
	private int score;
	private int winNum;
	private int lossNum;
	private int status; // 0 正常, 1匹配中，2比赛中， -1会被删除
	
	public LeagueGroupPO(int leagueId, int groupId, String name) {
		super();
		this.leagueId = leagueId;
		this.groupId = groupId;
		this.name = name;
	}

	public LeagueGroupPO() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getWinNum() {
		return winNum;
	}

	public void setWinNum(int winNum) {
		this.winNum = winNum;
	}

	public int getLossNum() {
		return lossNum;
	}

	public void setLossNum(int lossNum) {
		this.lossNum = lossNum;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public long getLeagueGroupId() {
		return (this.getLeagueId()) * 1000  + this.getGroupId();
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.leagueId, this.groupId, this.name, this.score, this.winNum, this.lossNum, this.status);
	}

	@Override
	public String getRowNames() {
		return "league_id, group_id, name, score, win_num, loss_num, status";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.leagueId, this.groupId, this.name, this.score, this.winNum, this.lossNum, this.status);
    }

	@Override
	public String getTableName() {
		return "t_u_league_group";
	}

	@Override
	public void del() {
		this.status = -1;
		this.save();
	}


}
