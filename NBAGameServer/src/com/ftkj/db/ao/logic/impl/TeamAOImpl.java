package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ITeamAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.TeamDAO;
import com.ftkj.db.dao.logic.TeamDailyDAO;
import com.ftkj.db.domain.MoneyPO;
import com.ftkj.db.domain.TeamPO;
import com.ftkj.manager.team.TeamDaily;

/**
 * @author tim.huang
 * 2017年3月15日
 *
 */
public class TeamAOImpl extends BaseAO implements ITeamAO {
    
	@IOC	
	private TeamDAO teamDAO;
	
    @IOC    
    private TeamDailyDAO teamDailyDAO;
	
	@Override
	public TeamPO getTeam(long teamId) {
		return teamDAO.getTeam(teamId);
	}

	@Override
	public List<TeamPO> getAllSimpleTeam() {
		return teamDAO.getAllSimpleTeam();
	}

	@Override
	public MoneyPO getTeamMoney(long teamId) {
		return teamDAO.getTeamMoney(teamId);
	}
	
	@Override
	public List<Long> getChatBlackTeamList() {
		return teamDAO.getChatBlackTeamList();
	}
	
	@Override
	public List<Long> getLockBlackTeamList() {
		return teamDAO.getLockBlackTeamList();
	}

    @Override
    public TeamDaily getTeamDaily(long teamId) {
        return teamDailyDAO.getTeamDaily(teamId);
    }
    
	@Override
	public void clearAllData() {
		teamDAO.clearAllData();
		teamDailyDAO.clearAllData();
	}

    @Override
    public void clearDailyData() {
        teamDailyDAO.clearTeamDaily();        
    }

    @Override
    public List<TeamDaily> getAllTeamDaily() {
        return teamDailyDAO.getAllTeamDaily();
    }
}
