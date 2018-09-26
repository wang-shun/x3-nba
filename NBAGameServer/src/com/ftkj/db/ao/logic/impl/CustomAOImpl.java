package com.ftkj.db.ao.logic.impl;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ICustomAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.CustomDAO;
import com.ftkj.db.domain.CustomTeamPO;

/**
 * @author tim.huang
 * 2017年8月11日
 *
 */
public class CustomAOImpl extends BaseAO implements ICustomAO {

	@IOC
	private CustomDAO customDAO;
	
	@Override
	public CustomTeamPO getCustomTeam(long teamId) {
		return customDAO.getCustomTeam(teamId);
	}
	
}
