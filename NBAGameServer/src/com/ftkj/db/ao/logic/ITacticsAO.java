package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.TacticsPO;

public interface ITacticsAO {
	
	public List<TacticsPO> getTacticsList(long teamId);
	
	
	
}
