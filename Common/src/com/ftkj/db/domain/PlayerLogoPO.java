package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Description:荣誉头像
 * @author Jay
 * @time:2017年3月16日 上午10:08:36
 */
public class PlayerLogoPO extends AsynchronousBatchDB {

	/**
	 * 主键(teamId+playerId+logoId)
	 */
	private long teamId;
	/**
	 * 球员
	 */
	private int playerId;
	/**
	 * 当前装备头像ID, 0是没有装备任何头像，显示系统默认头像
	 */
	private int logoId;
	
	/**
	 * 荣誉等级
	 */
	private int lv;
	
	/**
	 * 大星脉等级
	 */
	private int starLv;
	
	/**
	 * 当前等级小星脉点亮数
	 */
	private int step;
	
	public PlayerLogoPO(){}
	public PlayerLogoPO(long teamId, int playerId, int lv, int starLv, int step) {
		super();
		this.teamId = teamId;
		this.playerId = playerId;
		this.lv = lv;
		this.starLv = starLv;
		this.step = step;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getLogoId() {
		return logoId;
	}

	public void setLogoId(int logoId) {
		this.logoId = logoId;
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public int getStarLv() {
		return starLv;
	}

	public void setStarLv(int starLv) {
		this.starLv = starLv;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.playerId,this.logoId,this.lv,this.starLv,this.step);
	}

	@Override
	public String getRowNames() {
		return "team_id,player_id,logo_id,lv,starLv,step";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.playerId,this.logoId,this.lv,this.starLv,this.step);
    }

	@Override
	public String getTableName() {
		return "t_u_logo_player";
	}

	@Override
	public void del() {

	}


}
