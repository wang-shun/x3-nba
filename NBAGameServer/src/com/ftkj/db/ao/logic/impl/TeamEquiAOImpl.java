package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ITeamEquiAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.TeamEquiDAO;
import com.ftkj.db.domain.EquiPO;

/**
 * 装备模块AO实现
 * @author Jay
 * @time:2017年3月10日 上午11:19:54
 */
public class TeamEquiAOImpl extends BaseAO implements ITeamEquiAO {

	@IOC
	private TeamEquiDAO dao;
	
	@Override
	public List<EquiPO> getTeamEquiPOList(long teamId) {
		return dao.getTeamEquiPOList(teamId);
	}
	
	

}
