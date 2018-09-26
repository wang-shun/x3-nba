package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author tim.huang
 * 2017年5月25日
 * 联盟成就池
 */
public class LeagueHonorPoolPO extends AsynchronousBatchDB {

	
	private int leagueId;
	private int pid;
	private int num;
	
	public LeagueHonorPoolPO(int leagueId, int pid, int num) {
		super();
		this.leagueId = leagueId;
		this.pid = pid;
		this.num = num;
	}

	public LeagueHonorPoolPO() {
		super();
	}

	public int getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.leagueId,this.pid,this.num);
	}

	@Override
	public String getRowNames() {
		return "league_id, prop_id, num";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.leagueId,this.pid,this.num);
    }

	@Override
	public String getTableName() {
		return "t_u_league_honor_pool";
	}

	@Override
	public void del() {

	}


}
