package com.ftkj.db.domain.match;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public class MatchBestPO extends AsynchronousBatchDB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int matchId;
	private long teamId;
	private int rank;
	private DateTime updateTime;
	
	public MatchBestPO() {
	}
	public MatchBestPO(int matchId, long teamId, int rank) {
		super();
		this.matchId = matchId;
		this.teamId = teamId;
		this.rank = rank;
		this.updateTime = DateTime.now();
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public DateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(DateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.matchId, this.teamId, this.rank, this.updateTime);
	}

	@Override
	public String getRowNames() {
		return "match_id, team_id, rank, update_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.matchId, this.teamId, this.rank, this.updateTime);
    }

	@Override
	public String getTableName() {
		return "t_u_match_best";
	}

	@Override
	public void del() {

	}


}
