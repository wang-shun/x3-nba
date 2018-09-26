package com.ftkj.db.domain;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

/**
 * @author tim.huang
 * 2017年6月26日
 * 玩家球馆建筑
 */
public class TeamArenaConstructionPO extends AsynchronousBatchDB implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long teamId;
	private int cId;
	private int curGold;
	private int maxGold;
	private int playerId;
	private int playerGrade;
	private DateTime updateTime;
	
	
	
	@Override
	public synchronized void save() {
		this.updateTime = DateTime.now();
		super.save();
	}

	public long getTeamId() {
		return teamId;
	}

	public int getcId() {
		return cId;
	}

	public int getCurGold() {
		return curGold;
	}

	public int getMaxGold() {
		return maxGold;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getPlayerGrade() {
		return playerGrade;
	}

	public DateTime getUpdateTime() {
		return updateTime;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public void setCurGold(int curGold) {
		this.curGold = curGold;
	}

	public void setMaxGold(int maxGold) {
		this.maxGold = maxGold;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public void setPlayerGrade(int playerGrade) {
		this.playerGrade = playerGrade;
	}

	public void setUpdateTime(DateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.cId,this.curGold,this.maxGold,this.playerId,this.playerGrade,this.updateTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, cid, cur_gold, max_gold, player_id, player_grade, update_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.cId,this.curGold,this.maxGold,this.playerId,this.playerGrade,this.updateTime);
    }

	@Override
	public String getTableName() {
		return "t_u_arena";
	}

	@Override
	public void del() {

	}


}
