package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ITacticsAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.TacticsDAO;
import com.ftkj.db.domain.TacticsPO;

/**
 * @author tim.huang
 * 2017年3月8日
 *
 */
public class TacticsAOImpl extends BaseAO implements ITacticsAO {
	
	@IOC
	private TacticsDAO tacticsDAO;
	
	@Override
	public List<TacticsPO> getTacticsList(long teamId) {
		return tacticsDAO.getTacticsList(teamId);
	}

}
