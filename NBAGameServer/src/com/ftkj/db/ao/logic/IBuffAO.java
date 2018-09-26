package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.BuffPO;

public interface IBuffAO {
	
	public List<BuffPO> getTeamBuffList(long teamId);

}
