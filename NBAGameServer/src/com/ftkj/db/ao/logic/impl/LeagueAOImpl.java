package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ILeagueAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.LeagueDAO;
import com.ftkj.db.dao.logic.LeagueGroupDAO;
import com.ftkj.db.domain.LeagueHonorPO;
import com.ftkj.db.domain.LeagueHonorPoolPO;
import com.ftkj.db.domain.LeaguePO;
import com.ftkj.db.domain.LeagueTeamPO;
import com.ftkj.db.domain.LeagueTeamSimplePO;
import com.ftkj.db.domain.group.LeagueGroupPO;
import com.ftkj.db.domain.group.LeagueGroupSeasonPO;
import com.ftkj.db.domain.group.LeagueGroupTeamPO;

/**
 * @author tim.huang
 * 2017年5月31日
 *
 */
public class LeagueAOImpl extends BaseAO implements ILeagueAO {

	
	@IOC
	private LeagueDAO leagueDAO;
	@IOC
	private LeagueGroupDAO groupDAO;

	@Override
	public List<LeaguePO> getAllLeague() {
		return leagueDAO.getAllLeague();
	}

	@Override
	public LeagueTeamPO getLeagueTeamPO(long teamId) {
		return leagueDAO.getLeagueTeamPO(teamId);
	}

	@Override
	public List<LeagueTeamSimplePO> getAllLeagueTeam() {
		return leagueDAO.getAllLeagueTeam();
	}

	@Override
	public List<LeagueHonorPO> getAllLeagueHonor() {
		return leagueDAO.getAllLeagueHonor();
	}

	@Override
	public List<LeagueHonorPoolPO> getAllLeagueHonorPool() {
		return leagueDAO.getAllLeagueHonorPool();
	}

	@Override
	public LeagueGroupSeasonPO getLeagueGroupSeason() {
		return groupDAO.getLeagueGroupSeason();
	}

	@Override
	public List<LeagueGroupPO> getLeagueGroupList() {
		return groupDAO.getLeagueGroupList();
	}

	@Override
	public List<LeagueGroupTeamPO> getLeagueGroupTeamList() {
		return groupDAO.getLeagueGroupTeamList();
	}
	
	@Override
    public void clearAllLeagueTeamWeekScore() {
       leagueDAO.clearAllLeagueTeamWeekScore();
    }
	
	
}
