package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.manager.investment.TeamInvestmentInfo;
import com.ftkj.manager.investment.TeamPlayerInvestment;

public interface IPlayerInvestmentAO {
	public TeamInvestmentInfo getTeamInvestmentInfo(long teamId);
	public List<TeamPlayerInvestment> getTeamPlayerInvestments(long teamId);
}
