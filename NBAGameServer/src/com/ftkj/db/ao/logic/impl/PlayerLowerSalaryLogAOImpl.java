package com.ftkj.db.ao.logic.impl;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IPlayerLowerSalaryLogAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.PlayerLowerSalaryLogDAO;

/**
 * @author tim.huang
 * 2017年5月31日
 *
 */
public class PlayerLowerSalaryLogAOImpl extends BaseAO implements IPlayerLowerSalaryLogAO {

	
	@IOC
	private PlayerLowerSalaryLogDAO dao;

	@Override
	public long queryMaxKeyId() {
		return dao.queryMaxKeyId();
	}
	
}
