package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.EquiPO;

public interface ITeamEquiAO {

	
	public List<EquiPO> getTeamEquiPOList(long teamId);
}
