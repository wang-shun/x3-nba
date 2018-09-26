package com.ftkj.action.data;

import java.util.Date;
import java.util.List;

import com.ftkj.domain.data.NBAGameDetail;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBAPlayerScore;
import com.ftkj.domain.data.NBASeason;
import com.ftkj.domain.data.NBATeamDetail;
import com.ftkj.domain.data.PlayerAvgRate;
import com.ftkj.domain.data.PlayerAbi;
import com.ftkj.domain.data.PlayerPrice;

public interface NBADataAction {

	List<NBAPlayerDetail>getPlayers();
	
	void changeTeamId(int playerId,int teamId);
	
	void changeInjured();

	List<PlayerAbi>getPlayerCaps();

	List<NBATeamDetail>getTeams();

	void calculateData(Date date);	
	
	PlayerAbi getPlayerAbi(PlayerAvgRate avg);
	
	void calculateData_rand(Date date);
	
	void addPlayerMoneyNotData();
	
	List<PlayerAvgRate>getPlayerAvgs(int seasonId);
	
	List<PlayerPrice> getPlayerMoney(int playerId);
	
	List<NBAPlayerScore> getPlayerScoreDay(int playerId);
	
	String getMaxNbaGameData();
	
	int getFBGrade(String id);
	
	void addNbaDataRunLog();
	
	int getNbaDataRunLog();
	
	void changeTeamId0(int teamId);
	
	void changeMatchScore(List<NBAGameDetail>list);

	Date getRandSchedulerDate(NBASeason season, String startTime, String endTime, int minVS);
}
