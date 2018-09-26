package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.LeagueHonorPO;
import com.ftkj.db.domain.LeagueHonorPoolPO;
import com.ftkj.db.domain.LeaguePO;
import com.ftkj.db.domain.LeagueTeamPO;
import com.ftkj.db.domain.LeagueTeamSimplePO;
import com.ftkj.db.domain.group.LeagueGroupPO;
import com.ftkj.db.domain.group.LeagueGroupSeasonPO;
import com.ftkj.db.domain.group.LeagueGroupTeamPO;

public interface ILeagueAO {
	
	public List<LeaguePO> getAllLeague();
	
	public LeagueTeamPO getLeagueTeamPO(long teamId);
	
	public List<LeagueTeamSimplePO> getAllLeagueTeam();
	
	public List<LeagueHonorPO> getAllLeagueHonor();
	
	public List<LeagueHonorPoolPO> getAllLeagueHonorPool();
	
	// 战队赛
	public LeagueGroupSeasonPO getLeagueGroupSeason();
	public List<LeagueGroupPO> getLeagueGroupList();
	public List<LeagueGroupTeamPO> getLeagueGroupTeamList();

	public void clearAllLeagueTeamWeekScore();
}
