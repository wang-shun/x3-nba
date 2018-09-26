package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IBuffAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.BuffDAO;
import com.ftkj.db.domain.BuffPO;

public class BuffAOImpl extends BaseAO implements IBuffAO {

	@IOC
	private BuffDAO dao;
	
	@Override
	public List<BuffPO> getTeamBuffList(long teamId) {
		return dao.getTeamBuffList(teamId);
	}

}
