package com.ftkj.manager.player;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author tim.huang
 * 2017年9月28日
 * 球员升级
 */
public class PlayerGrade extends AsynchronousBatchDB {
	
	private long teamId;
	private int playerId;
	private int grade;
	private int exp;
	private int starGrade;
	private int star;
	
	public void updateStar(int val){
		this.star+=val;
		this.save();
	}
	
	public int getStarGrade() {
		return starGrade;
	}

	public void setStarGrade(int starGrade) {
		this.starGrade = starGrade;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public PlayerGrade() {
	}
	
	public PlayerGrade(long teamId, int playerId) {
		super();
		this.teamId = teamId;
		this.playerId = playerId;
		this.grade = 1;
		this.exp =0;
		this.star =0;
		this.starGrade =0;
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

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.playerId,this.grade,this.exp,this.star,this.starGrade);
	}

	@Override
	public String getRowNames() {
		return "team_id, player_id, grade, `exp`,star,star_grade";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.playerId,this.grade,this.exp,this.star,this.starGrade);
    }

	@Override
	public String getTableName() {
		return "t_u_player_grade";
	}

	@Override
	public void del() {
		
	}


}
