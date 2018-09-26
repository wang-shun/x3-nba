package com.ftkj.db.ao.logic.impl;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IStreetBallAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.StreetBallDAO;
import com.ftkj.db.domain.StreetBallPO;

public class StreetBallAOImple extends BaseAO implements IStreetBallAO {

	@IOC
	private StreetBallDAO dao;
	
	@Override
	public StreetBallPO getStreetBallByTeam(long teamId) {
		return dao.getStreetBallByTeam(teamId);
	}

}
