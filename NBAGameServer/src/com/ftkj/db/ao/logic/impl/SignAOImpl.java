package com.ftkj.db.ao.logic.impl;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ISignAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.SignDAO;
import com.ftkj.db.domain.SignPO;

public class SignAOImpl extends BaseAO implements ISignAO {

	@IOC
	private SignDAO dao;
	
	@Override
	public SignPO getSignMonth(long teamId) {
		return dao.getSignMonth(teamId);
	}

	@Override
	public SignPO getSignPeriod(long teamId) {
		return dao.getSignPeriod(teamId);
	}

}
