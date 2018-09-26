package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

public class StagePO extends AsynchronousBatchDB {

	/**
	 * 球队ID
	 */
	private long teamId;
	/**
	 * 当前大关器，赛季
	 */
	private int scene;
	/**
	 * 小关卡ID
	 */
	private int stageId;
	/**
	 * 季后赛比分
	 */
	private int step;
	/**
	 * 关卡比分记录
	 */
	private String score;
	
	public StagePO() {
	}
	
	public StagePO(long teamId) {
		super();
		this.teamId = teamId;
		this.stageId = 1;
	}



	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getScene() {
		return scene;
	}

	public void setScene(int scene) {
		this.scene = scene;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.scene,this.stageId,this.step,this.score);
	}

	@Override
	public String getRowNames() {
		return "team_id,scene,stage_id,step,score";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.scene,this.stageId,this.step,this.score);
    }

	@Override
	public String getTableName() {
		return "t_u_stage";
	}

	@Override
	public void del() {

	}


}
