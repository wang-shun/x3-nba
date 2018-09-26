package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IPropAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.PropDAO;
import com.ftkj.db.domain.PropPO;

/**
 * @author tim.huang
 * 2017年3月13日
 *
 */
public class PropAOImpl extends BaseAO implements IPropAO {

	@IOC
	private PropDAO propDAO;
	
	@Override
	public List<PropPO> getPropList(long teamId) {
		return propDAO.getPropList(teamId);
	}

	
}
