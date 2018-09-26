package com.ftkj.db.domain.group;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

public class LeagueGroupSeasonPO extends AsynchronousBatchDB {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private DateTime startTime;
	private DateTime endTime;
	private int status; //0，未开始 1，进行中（初始）  2，结束
	
	public LeagueGroupSeasonPO(int id, String name, DateTime startTime, DateTime endTime) {
		super();
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public LeagueGroupSeasonPO() {
		super();
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.id, this.name, this.startTime, this.endTime, this.status);
	}

	@Override
	public String getRowNames() {
		return "id, name, start_time, end_time, status";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.id, this.name, this.startTime, this.endTime, this.status);
    }

	@Override
	public String getTableName() {
		return "t_u_league_group_season";
	}

	@Override
	public void del() {
		// TODO Auto-generated method stub
		
	}


}
