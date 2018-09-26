package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IStageAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.StageDAO;
import com.ftkj.db.domain.StagePO;

public class StageAOImpl extends BaseAO implements IStageAO {

	@IOC
	private StageDAO stageDAO;
	
	@Override
	public List<StagePO> getStageList() {
		return stageDAO.getStageList();
	}

	@Override
	public StagePO getStageByTeam(long teamId) {
		return stageDAO.getStageByTeam(teamId);
	}
}
