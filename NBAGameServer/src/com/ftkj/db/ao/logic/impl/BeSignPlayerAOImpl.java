package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IBeSignPlayerAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.BeSignPlayerDAO;
import com.ftkj.db.domain.BeSignPlayerPO;

public class BeSignPlayerAOImpl extends BaseAO implements IBeSignPlayerAO {

	@IOC
	private BeSignPlayerDAO dao;
 	
	@Override
	public List<BeSignPlayerPO> getBeSignPlayerList(long teamId) {
		return dao.getBeSignPlayerList(teamId);
	}

}
