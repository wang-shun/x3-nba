package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年6月26日
 * 球馆玩家数据
 */
public class TeamArenaPO extends AsynchronousBatchDB {

	private long teamId;
	private int gold;
	private int power;
	private int defend;
	private int level;
	private DateTime lastUpdateTime;
	
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getTeamId() {
		return teamId;
	}

	public int getGold() {
		return gold;
	}

	public int getPower() {
		return power;
	}

	public int getDefend() {
		return defend;
	}

	public DateTime getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public void setDefend(int defend) {
		this.defend = defend;
	}

	public void setLastUpdateTime(DateTime lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.gold,this.power,this.defend,this.level,this.lastUpdateTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, gold, `power`, defend, `level`, last_update_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.gold,this.power,this.defend,this.level,this.lastUpdateTime);
    }

	@Override
	public String getTableName() {
		return "t_u_arena_team";
	}

	@Override
	public void del() {

	}


}
