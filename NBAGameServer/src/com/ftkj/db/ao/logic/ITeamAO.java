package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.MoneyPO;
import com.ftkj.db.domain.TeamPO;
import com.ftkj.manager.team.TeamDaily;

/**
 * @author tim.huang
 * 2017年3月15日
 * 玩家信息AO
 */
public interface ITeamAO {
	
	public TeamPO getTeam(long teamId);
	
	public TeamDaily getTeamDaily(long teamId);
	
	public List<TeamDaily> getAllTeamDaily();
	
	public List<TeamPO> getAllSimpleTeam();

	public MoneyPO getTeamMoney(long teamId);
	
	public List<Long> getChatBlackTeamList();
	
	public List<Long> getLockBlackTeamList();

	void clearAllData();
	
	void clearDailyData();
}
