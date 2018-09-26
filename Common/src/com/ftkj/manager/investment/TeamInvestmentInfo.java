package com.ftkj.manager.investment;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author tim.huang
 * 2017年12月29日
 * 玩家投资信息
 */
public class TeamInvestmentInfo extends AsynchronousBatchDB {
	private long teamId;
	private int maxTotal;
	private int money;
	
	
	public TeamInvestmentInfo(long teamId) {
		super();
		this.teamId = teamId;
		this.maxTotal = 10;
		this.money = 2550;
	}


	public TeamInvestmentInfo() {
	}


	public void updateMoney(int val){
		this.money+=val;
		this.save();
	}
	
	
	public void updateMaxTotal(int val){
		this.maxTotal += val;
		this.save();
	}
	
	
	
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	public int getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.money,this.maxTotal);
	}
	@Override
	public String getRowNames() {
		return "team_id, money, max_total";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.money,this.maxTotal);
    }

	@Override
	public String getTableName() {
		return "t_u_player_inv_team";
	}
	@Override
	public void del() {
		
	}


}
