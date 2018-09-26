package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年5月25日
 * 联盟成就
 */
public class LeagueHonorPO extends AsynchronousBatchDB {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int leagueId;
	private int honorId;
	private int level;
	private DateTime endTime;
	
	public LeagueHonorPO() {
		super();
	}

	public LeagueHonorPO(int leagueId, int honorId, int level) {
		super();
		this.leagueId = leagueId;
		this.honorId = honorId;
		this.level = level;
		this.endTime = DateTime.now();
		this.save();
	}


	public int getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public int getHonorId() {
		return honorId;
	}

	public void setHonorId(int honorId) {
		this.honorId = honorId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.leagueId,this.honorId,this.level,this.endTime);
	}

	@Override
	public String getRowNames() {
		return "league_id, honor_id, `level`, end_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.leagueId,this.honorId,this.level,this.endTime);
    }

	@Override
	public String getTableName() {
		return "t_u_league_honor";
	}

	@Override
	public void del() {

	}


}
