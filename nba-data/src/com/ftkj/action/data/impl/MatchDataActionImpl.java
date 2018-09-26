package com.ftkj.action.data.impl;

import com.ftkj.action.BaseAction;
import com.ftkj.action.data.MatchDataAction;
import com.ftkj.ao.data.GameDataAO;
import com.ftkj.domain.data.GameDataJobRunLog;
import com.ftkj.domain.data.GameVS;
import com.ftkj.domain.data.MatchData;
import com.ftkj.domain.data.NBAGameDetail;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBAPlayerScore;
import com.ftkj.domain.data.NBATeamScore;
import com.ftkj.domain.data.PlayerAvgRate;
import com.ftkj.exception.GameDataException;
import com.ftkj.invoker.Resource;
import com.ftkj.invoker.ResourceType;
import com.ftkj.util.UtilDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MatchDataActionImpl extends  BaseAction implements MatchDataAction{
	private static final Logger logger = LoggerFactory.getLogger(MatchDataActionImpl.class);
	@Resource(value = ResourceType.GameDataAO)
	public GameDataAO gameDataAO;

	//
	@Override
	public List<Date>getDatesNeedToRunJob(){
		List<GameDataJobRunLog>logs=gameDataAO.queryGameDataJobRunLogs();
		List<Date>result=new ArrayList<Date>();
		if(logs.isEmpty()){
			return result;
		}
		Collections.reverse(logs);
		Date startTime=logs.get(0).getGameTime();
		Calendar c=Calendar.getInstance();
		c.setTime(startTime);
		c.set(Calendar.HOUR_OF_DAY, 1);
		Calendar nowC=Calendar.getInstance();
		nowC.setTime(new Date());
		nowC.set(Calendar.HOUR_OF_DAY,0);
		Date now=nowC.getTime();
		while(c.getTime().before(now)){		
			//System.out.println("1=="+UtilDateTime.toDateString(c.getTime(), UtilDateTime.SIMPLEFORMATSTRING));
			if(!containsDate(logs,c.getTime())){
				result.add(c.getTime());
			}
			c.add(Calendar.DAY_OF_YEAR, 1);
		}
		return result;
	}
		
	//
	private boolean containsDate(List<GameDataJobRunLog>logs,Date gameTime){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		for(GameDataJobRunLog log:logs){
			if(sdf.format(log.getGameTime()).equals(sdf.format(gameTime))){
				return true;
			}
		}
		return false;
	}
	//
	@Override
	public List<MatchData> updateMatchData(Date gameDate,List<NBAPlayerDetail> map) throws Exception {
		logger.warn("updateGameDate:"+UtilDateTime.toDateString(gameDate, UtilDateTime.SIMPLEFORMATSTRING));
		//
		GameDataJobRunLog lastLog=gameDataAO.queryRunLog(gameDate);
		if(lastLog!=null){
			return new ArrayList<MatchData>();
		}
		GameDataJobRunLog log=new GameDataJobRunLog();
		log.setRunTime(new Date());
		log.setGameTime(gameDate);
		gameDataAO.addGameDataJobRunLog(log);
		//
		logger.error("update game data:"+gameDate);
		List<MatchData>datas=gameDataAO.getMatchData(gameDate);
		for(MatchData data:datas){
			logger.error("update game data:\n"+data.getGameBoxId());
			gameDataAO.updateMatchData(data,map);
		}
		return datas;
	}
	
	public void clearTodayData(){
		gameDataAO.clearTodayData();
	}
	
	public void addCurrData(MatchData data){
		logger.info("*************************************"+data.getState());		
		gameDataAO.addCurrData(data);
	}
	
	/**
	 * @throws GameDataException 
	 */
	@Override
	public List<NBAGameDetail> getNBAGameDetail(Date date)throws GameDataException {
		return gameDataAO.getNowNBAGameDetail(date);
	}
	//
	@Override
	public List<NBAPlayerScore> queryPlayerScores(int gameId) {
		return gameDataAO.queryPlayerScores(gameId);
	}
	//
	@Override
	public List<NBATeamScore> queryTeamScores(int gameId) {
		return gameDataAO.queryTeamScores(gameId);
	}	
	//
	@Override
	public Map<Integer,List<PlayerAvgRate>> getPlayerAvg(){
		return gameDataAO.getPlayerAvg(-1);
	}
	//
	@Override
	public Map<Integer,List<PlayerAvgRate>> getPlayerAvg(int gameType){
		return gameDataAO.getPlayerAvg(gameType);
	}
	@Override
	public Map<Integer,List<PlayerAvgRate>> getPlayerTotal(){
		return gameDataAO.getPlayerTotal(-1);
	}
	@Override
	public Map<Integer,List<PlayerAvgRate>> getPlayerTotal(int gameType){
		return gameDataAO.getPlayerTotal(gameType);
	}
	@Override
	public List<PlayerAvgRate> getPlayerSeasonMax() {
		return gameDataAO.getPlayerSeasonMax(-1);
	}
	@Override
	public List<PlayerAvgRate> getPlayerSeasonMax(int gameType) {
		return gameDataAO.getPlayerSeasonMax(gameType);
	}
	@Override
	public List<PlayerAvgRate> getPlayerSeasonAvg() {
		return gameDataAO.getPlayerSeasonAvg(-1);
	}
	@Override
	public List<PlayerAvgRate> getPlayerSeasonAvg(int gameType) {
		return gameDataAO.getPlayerSeasonAvg(gameType);
	}
	@Override
	public MatchData getMatchData(String boxId)throws GameDataException {
		return gameDataAO.getMatchData(boxId);
	}

	@Override
	public void saveGameVS() {
		gameDataAO.saveGameVS();
	}
	
	@Override
	public void changeTeamRank() {
		gameDataAO.changeTeamRank();
	}
	
	@Override
	public List<GameVS> getGameVS() {
		return gameDataAO.getGameVS();
	}
	
	@Override
	public List<GameVS> getGameVSByDate(Date date){
		return gameDataAO.getGameVSByDate(date);
	}

	@Override
	public List<NBAGameDetail> getSchedule_not_pk() {
		return gameDataAO.getSchedule_not_pk();
	}

	@Override
	public MatchData getSchedule_not_pk_gameData(NBAGameDetail info) {
		return gameDataAO.getSchedule_not_pk_gameData(info);
	}

	@Override
	public List<Integer> getPlayerInjuries() {
		return gameDataAO.getPlayerInjuries();
	}

	@Override
	public List<NBAPlayerDetail> getPlayerListByTeam(String teamName, int teamId) {
		return gameDataAO.getPlayerListByTeam(teamName, teamId);
	}
}
