package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年8月3日
 *
 */
public class CustomTeamPO extends AsynchronousBatchDB {

	
	private long teamId;
	private int money;
	private DateTime createTime;
	
	
	
	public CustomTeamPO() {
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.money,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, money, create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.money,this.createTime);
    }


	@Override
	public String getTableName() {
		return "t_u_custom_team";
	}

	@Override
	public void del() {

	}


}
