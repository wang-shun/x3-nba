package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.StagePO;

public interface IStageAO {

	
	public List<StagePO> getStageList();

	public StagePO getStageByTeam(long teamId);
	
}
