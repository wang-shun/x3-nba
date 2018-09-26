package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IPlayerInvestmentAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.PlayerInvestmentDAO;
import com.ftkj.manager.investment.TeamInvestmentInfo;
import com.ftkj.manager.investment.TeamPlayerInvestment;

public class PlayerInvestmentAO extends BaseAO implements IPlayerInvestmentAO {

	@IOC
	private PlayerInvestmentDAO playerInvestmentDAO;
	
	@Override
	public TeamInvestmentInfo getTeamInvestmentInfo(long teamId) {
		return playerInvestmentDAO.getTeamInvestmentInfo(teamId);
	}

	@Override
	public List<TeamPlayerInvestment> getTeamPlayerInvestments(long teamId) {
		return playerInvestmentDAO.getTeamPlayerInvestments(teamId);
	}

	
}
