package com.ftkj.db.ao.logic.impl;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ITeamVipAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.TeamVipDAO;
import com.ftkj.db.domain.VipPO;

public class TeamVipAOImpl extends BaseAO implements ITeamVipAO {

	@IOC
	private TeamVipDAO dao;
	
	@Override
	public VipPO getTeamVip(long teamId) {
		return dao.getTeamVip(teamId);
	}

}
