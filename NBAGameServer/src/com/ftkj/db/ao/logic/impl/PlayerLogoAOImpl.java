package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IPlayerLogoAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.PlayerLogoDAO;
import com.ftkj.db.domain.LogoPO;
import com.ftkj.db.domain.PlayerLogoPO;

public class PlayerLogoAOImpl extends BaseAO implements IPlayerLogoAO {

	@IOC
	private PlayerLogoDAO dao;
	

	@Override
	public List<LogoPO> getLogoPOList(long teamId) {
		return dao.getLogoPOList(teamId);
	}

	@Override
	public List<PlayerLogoPO> getPlayerLogoPOList(long teamId) {
		return dao.getPlayerLogoPOList(teamId);
	}

}
