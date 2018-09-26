package com.ftkj.action.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ftkj.domain.data.GameVS;
import com.ftkj.domain.data.MatchData;
import com.ftkj.domain.data.NBAGameDetail;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBAPlayerScore;
import com.ftkj.domain.data.NBATeamScore;
import com.ftkj.domain.data.PlayerAvgRate;
import com.ftkj.exception.GameDataException;


public interface MatchDataAction {
	
	void clearTodayData();
	
	void addCurrData(MatchData data);
	
	MatchData getMatchData(String boxId)throws GameDataException ;
	
	List<MatchData> updateMatchData(Date gameDate,List<NBAPlayerDetail> map)throws Exception;
	
	List<NBATeamScore>queryTeamScores(int gameId);
	
	List<NBAPlayerScore>queryPlayerScores(int gameId);
	
	List<NBAGameDetail>getNBAGameDetail(Date date) throws GameDataException;
	
	Map<Integer, List<PlayerAvgRate>> getPlayerAvg();
	
	Map<Integer, List<PlayerAvgRate>> getPlayerAvg(int gameType);
	
	List<PlayerAvgRate> getPlayerSeasonMax();
	
	List<PlayerAvgRate> getPlayerSeasonMax(int gameType);
	
	List<PlayerAvgRate> getPlayerSeasonAvg();
	
	List<PlayerAvgRate> getPlayerSeasonAvg(int gameType);

	Map<Integer, List<PlayerAvgRate>> getPlayerTotal();

	Map<Integer, List<PlayerAvgRate>> getPlayerTotal(int gameType);

	List<Date> getDatesNeedToRunJob();
	
	void saveGameVS();
	
	void changeTeamRank();
	
	List<GameVS> getGameVS();
	
	List<GameVS> getGameVSByDate(Date date);
	
	List<NBAGameDetail> getSchedule_not_pk();
	
	MatchData getSchedule_not_pk_gameData(NBAGameDetail info);
	
	List<Integer> getPlayerInjuries();
	
	List<NBAPlayerDetail> getPlayerListByTeam(String teamName,int teamId);

}