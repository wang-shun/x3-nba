package com.ftkj.ao.data;

import com.ftkj.ao.BaseAO;
import com.ftkj.ao.data.datafetch.ESPNPageAnalyzer;
import com.ftkj.ao.data.datafetch.PageAnalyzerException;
import com.ftkj.dao.data.GameDataDAO;
import com.ftkj.dao.data.NBADataDAO;
import com.ftkj.domain.data.GameDataJobRunLog;
import com.ftkj.domain.data.GameVS;
import com.ftkj.domain.data.MatchData;
import com.ftkj.domain.data.NBAGameDetail;
import com.ftkj.domain.data.NBAGameSchedule;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBAPlayerScore;
import com.ftkj.domain.data.NBASeason;
import com.ftkj.domain.data.NBATeamDetail;
import com.ftkj.domain.data.NBATeamScore;
import com.ftkj.domain.data.PlayerAvgRate;
import com.ftkj.domain.data.PlayerStats;
import com.ftkj.exception.GameDataException;
import com.ftkj.invoker.Resource;
import com.ftkj.invoker.ResourceType;
import com.ftkj.util.UtilDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GameDataAO extends BaseAO{
	private static final Logger logger = LoggerFactory.getLogger(GameDataAO.class);
	@Resource(value = ResourceType.GameDataDAO)
	public GameDataDAO gameDataDAO;
	
	@Resource(value = ResourceType.NBADataDAO)
	public NBADataDAO nbaDataDAO;
	
	/**
	 * 赛季信息：开始时间~结束时间 
	 */
	private static List<NBASeason>seasonList;
	
	/**
	 * 抓取数据逻辑
	 */
	private ESPNPageAnalyzer pageAnalyzer;
	
	//
	public GameDataAO() {
		pageAnalyzer=new ESPNPageAnalyzer();
		seasonList=new ArrayList<NBASeason>();
		seasonList.add(NBASeason.create(2009,transDate("20091025"),transDate("20100620")));
		seasonList.add(NBASeason.create(2010,transDate("20100621"),transDate("20110620")));				
		seasonList.add(NBASeason.create(2011,transDate("20111026"),transDate("20120620")));
		seasonList.add(NBASeason.create(2012,transDate("20121025"),transDate("20130620")));
		seasonList.add(NBASeason.create(2013,transDate("20131025"),transDate("20140620")));
		seasonList.add(NBASeason.create(2014,transDate("20141025"),transDate("20150620")));
		seasonList.add(NBASeason.create(2015,transDate("20151025"),transDate("20160620")));
		seasonList.add(NBASeason.create(2016,transDate("20161023"),transDate("20170620")));
		seasonList.add(NBASeason.create(2017,transDate("20171010"),transDate("20180611"))); 
		// 随机身价期间2017，新赛季开始后要改成2018
		seasonList.add(NBASeason.create(2017,transDate("20180612"),transDate("20190620"))); // 身价更新结束时间
	}
	//
	private Date transDate(String timeStr){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		try {
			return sdf.parse(timeStr);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public List<Integer> getPlayerInjuries(){
		List<Integer> list = new ArrayList<Integer>();
		try{
			list = pageAnalyzer.getPlayerInjuries();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return list;
	}
	
	public void changeInjured(){
		gameDataDAO.changeInjured();
	}
	
	public List<NBAPlayerDetail> getPlayerListByTeam(String teamName,int teamId){
		List<NBAPlayerDetail> playerList = new ArrayList<NBAPlayerDetail>();
		try{
			playerList = pageAnalyzer.getPlayerListByTeam(teamName, teamId);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return playerList;
	}

	public static NBASeason getSeason(Date date){
		for(NBASeason season:seasonList){
			if(season.getStartDate().equals(date)||season.getEndDate().equals(date)){
				return season;
			}
			if(season.getStartDate().before(date)&&season.getEndDate().after(date)){
				return season;
			}
		}
		return null;
	}
	//
	public NBASeason getLastSeason(){
		return seasonList.get(seasonList.size()-1);
	}
	//
	public List<NBASeason>getSeasons(){
		return seasonList;
	}
	//
	public void addGameDataJobRunLog(GameDataJobRunLog log){
		gameDataDAO.addRunLog(log);
	}
	//
	public GameDataJobRunLog queryRunLog(Date gameTime){
		/*
		Calendar c=Calendar.getInstance();
		c.setTime(gameTime);
		c.set(Calendar.HOUR_OF_DAY, 1);
		Date startTime=c.getTime();
		c.set(Calendar.HOUR_OF_DAY, 23);
		Date endTime=c.getTime();
		*/
		return gameDataDAO.queryRunLogByTime(gameTime);
	}
	//
	public List<GameDataJobRunLog>queryGameDataJobRunLogs(){
		return gameDataDAO.queryRunLogs();
	}
	
	public void clearTodayData(){
		gameDataDAO.clearTodayData();
	}	
	
	public void addCurrData(MatchData gameData){
		try{
			if(gameData.isPlayoff()){
				gameData.setGameType(NBAGameSchedule.GAME_TYPE_季后赛);
			}
			if(gameData.getHomeTeamId()<100){
				gameData.setGameType(NBAGameSchedule.GAME_TYPE_全明星赛);
			}
			int status = 1;
			if(gameData.getState().toLowerCase().indexOf("final")==-1){
				status = 0;
			}
			gameDataDAO.x_addGameData(gameData,status);
			gameDataDAO.x_addTeamScore(gameData.getScoreAway());
			gameDataDAO.x_addTeamScore(gameData.getScoreHome());
			
			for(PlayerStats ps:gameData.getPlayerScoreHome()){
				//ps.isStarter=(count++)<=5;
				gameDataDAO.x_addPlayerScore(ps);
			}
			for(PlayerStats ps:gameData.getPlayerScoreAway()){
				//ps.isStarter=(count++)<=5;
				gameDataDAO.x_addPlayerScore(ps);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//
	public void updateMatchData(MatchData gameData,List<NBAPlayerDetail> map){
		try{
			if(gameData.isPlayoff()){
				gameData.setGameType(NBAGameSchedule.GAME_TYPE_季后赛);
			}
			if(gameData.getHomeTeamId()<100){
				gameData.setGameType(NBAGameSchedule.GAME_TYPE_全明星赛);
			}
			gameDataDAO.addGameData(gameData);
			gameDataDAO.addTeamScore(gameData.getScoreAway());
			gameDataDAO.addTeamScore(gameData.getScoreHome());			
			Set<Integer> playerIdSet = new HashSet<Integer>();
			int count=0;
			for(PlayerStats ps:gameData.getPlayerScoreHome()){
				//ps.isStarter=(count++)<=5;
				gameDataDAO.addPlayerScore(ps);
				playerIdSet.add(ps.getPlayerId());
			}
			for(NBAPlayerDetail T:map){
				if(T.getTeamId()==gameData.getHomeTeamId()&& 
						!playerIdSet.contains(T.getPlayerId())){
					gameDataDAO.updateInjuries(T.getPlayerId());
				}
			}
			
			count=0;
			playerIdSet.clear();
			for(PlayerStats ps:gameData.getPlayerScoreAway()){
				//ps.isStarter=(count++)<=5;
				gameDataDAO.addPlayerScore(ps);
				playerIdSet.add(ps.getPlayerId());
			}
			for(NBAPlayerDetail T:map){
				if(T.getTeamId()==gameData.getAwayTeamId()&&
						!playerIdSet.contains(T.getPlayerId())){
					gameDataDAO.updateInjuries(T.getPlayerId());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//
	private int getTeamId(String shortEname,
			Map<String,Integer>teamRelationMap,
			MatchData gameData)throws GameDataException{
		Integer teamId=teamRelationMap.get(shortEname.toLowerCase());
		if(teamId==null){
			return 0;
		}
		return teamId;
	}
	//
	private void setPlayerEspnMap(Map<Integer,Integer> playerEspnMap){
		playerEspnMap.clear();
		for(NBAPlayerDetail info:nbaDataDAO.getPlayers()){
			playerEspnMap.put(info.getEspnId(),info.getPlayerId());
		}
	}

	/**
	 * 赛季空则抛出异常GameDataException
	 * @param date
	 * @return
	 * @throws GameDataException
	 */
	public List<MatchData> getMatchData(Date date) throws GameDataException{
		NBASeason season=getSeason(date);
		if(season==null){
			throw new GameDataException(GameDataException.CODE_CAN_NOT_FOUND_SEASON,
					UtilDateTime.toDateString(date, UtilDateTime.SIMPLEFORMATSTRING)+"");
		}
		Map<String,Integer> teamRelationMap=new HashMap<String, Integer>();
		Map<Integer,Integer> playerRelationMap=new HashMap<Integer, Integer>();
		//
		for(NBATeamDetail teamInfo:nbaDataDAO.getTeams()){
			teamRelationMap.put(teamInfo.getEspnName(),teamInfo.getTeamId());			
		}
		setPlayerEspnMap(playerRelationMap);
		List<Integer> listSchedule = gameDataDAO.getSchedule();
		try {
			// 今天的比赛 ? 
			List<MatchData>l=pageAnalyzer.fetchMatchData(date,listSchedule);
			for(MatchData data:l){
				data.setSeasonId(season.getId());
				int awayTeamId=getTeamId(data.getScoreAway().team,teamRelationMap,data);
				int homeTeamId=getTeamId(data.getScoreHome().team,teamRelationMap,data);
				if(awayTeamId==0 || homeTeamId==0)continue;
				data.setAwayTeamId(awayTeamId);
				data.setHomeTeamId(homeTeamId);
				data.getScoreAway().teamId=awayTeamId;
				data.getScoreHome().teamId=homeTeamId;
				setPlayerScoreData(playerRelationMap,data.getPlayerScoreHome(),data.getHomeTeamId(),data.getGameBoxId());
				setPlayerScoreData(playerRelationMap,data.getPlayerScoreAway(),data.getAwayTeamId(),data.getGameBoxId());
			}
			return l;
		} catch (Exception e) {
			throw new GameDataException(GameDataException.CODE_GET_DATA_ERROR,e);
		}
	}
	
	public MatchData getMatchData(String boxId)throws GameDataException{		
		Map<String,Integer> teamEspnMap=new HashMap<String, Integer>();
		Map<Integer,Integer> playerEspnMap=new HashMap<Integer, Integer>();

		for(NBATeamDetail teamInfo:nbaDataDAO.getTeams()){
			teamEspnMap.put(teamInfo.getEspnName(),teamInfo.getTeamId());			
		}
		setPlayerEspnMap(playerEspnMap);
		try {
			MatchData data=pageAnalyzer.getMatchData(boxId);
			//System.out.println("******"+data);
			int awayTeamId=getTeamId(data.getScoreAway().team,teamEspnMap,data);
			int homeTeamId=getTeamId(data.getScoreHome().team,teamEspnMap,data);
			
			logger.info("//"+awayTeamId+" / "+homeTeamId);
			
			data.getScoreAway().teamId=awayTeamId;
			data.getScoreHome().teamId=homeTeamId;
			data.setAwayTeamId(awayTeamId);
			data.setHomeTeamId(homeTeamId);
			setPlayerScoreData(playerEspnMap,data.getPlayerScoreHome(),data.getHomeTeamId(),data.getGameBoxId());
			setPlayerScoreData(playerEspnMap,data.getPlayerScoreAway(),data.getAwayTeamId(),data.getGameBoxId());

			return data;
		} catch (Exception e) {
			throw new GameDataException(GameDataException.CODE_GET_DATA_ERROR,e);
		}
	}
	
	//
	private void setPlayerScoreData(Map<Integer,Integer> playerRelationMap,List<PlayerStats> list,
	int teamId,String gameId){
		Iterator<PlayerStats> psIt = list.iterator();
		while(psIt.hasNext()){
			PlayerStats ps=psIt.next();
			if(ps.playerId==0){
				psIt.remove();
				logger.warn("error espn player ps.playerId= "+ps.playerId+","+ps.playerName);
				continue;
			}
			Integer gamePlayerId=playerRelationMap.get(ps.playerId);
			if(gamePlayerId==null){
				logger.warn("can not found espn playerId= "+ps.playerId+","+ps.playerName);
				addNewPlayer(ps.playerId,ps.playerName,teamId);
				setPlayerEspnMap(playerRelationMap);
				gamePlayerId=ps.playerId;
			}
			ps.gamePlayerId=gamePlayerId;
			ps.gameId=Integer.valueOf(gameId);
			ps.teamId=teamId;
		}
	}
	//
	private void addNewPlayer(int espnId,String name,int teamId){
		//if(espnId>7000)return;
		NBAPlayerDetail info=new NBAPlayerDetail();		
		info.setGrade("N");		
		info.setPrice(50);
		info.setBeforePrice(50);		
		info.setEspnId(espnId);
		info.setPlayerId(espnId);
		info.setTeamId(teamId);
		info.setDraft("0");
		//info.setTeamId(0);
		//System.out.println("******************"+name);
		String aname=name.substring(0,name.lastIndexOf(','));
		
		String pos=name.substring(name.lastIndexOf(',')+1).replace(" ", "").trim();
		info.setName(aname);
		info.setEname(aname);
		info.setShortName(aname);
		if(pos.equals("G"))pos="PG/SG";
		else if(pos.equals("F"))pos="SF/PF";
		info.setPosition(pos);
		nbaDataDAO.addPlayerBaseInfo(info);
	}
	
	public List<NBAGameDetail> getNowNBAGameDetail(Date date) throws GameDataException{
		List<NBATeamDetail> teamList=nbaDataDAO.getTeams();
		try {
			List<NBAGameDetail> list = pageAnalyzer.getNowNBAGameDetail(date);
			for(NBAGameDetail N:list){
				N.setAwayTeamId(getTeamId(N.getAwayTeamName(),teamList));
				N.setHomeTeamId(getTeamId(N.getHomeTeamName(),teamList));
				logger.info("-----"+N.getAwayTeamName()+","+N.getHomeTeamName()+"-----"+N.getAwayTeamId()+","+N.getHomeTeamId());	
			}			
			return list;
		} catch (PageAnalyzerException e) {
			throw new GameDataException(GameDataException.CODE_GET_DATA_ERROR,e);
		}
	}
	
	private int getTeamId(String shortEname,List<NBATeamDetail> list){
		for(NBATeamDetail t:list){
			if(t.getTeamEname().indexOf(shortEname)!=-1)return t.getTeamId();
		}		
		return 0;
	}

	public List<NBAPlayerScore>queryPlayerScores(int gameId){
		return gameDataDAO.queryPlayerScores(gameId);
	}
	
	
	public List<NBATeamScore>queryTeamScores(int gameId){
		return gameDataDAO.queryTeamScores(gameId);
	}
	
	public Map<Integer,List<PlayerAvgRate>> getPlayerAvg(int gameType){
		List<PlayerAvgRate> list=gameDataDAO.getPlayerAvg(gameType);
		return getMap(list);
	}
	
	public List<PlayerAvgRate> getPlayerSeasonMax(int gameType){
		return gameDataDAO.getPlayerSeasonMax(gameType);
	}

	public List<PlayerAvgRate> getPlayerSeasonAvg(int gameType){
		return gameDataDAO.getPlayerSeasonAvg(gameType);
	}
	//
	public Map<Integer, List<PlayerAvgRate>> getPlayerTotal(int gameType) {
		List<PlayerAvgRate> list=gameDataDAO.getPlayerTotal(gameType);
		return getMap(list);
		
	}
	private Map<Integer,List<PlayerAvgRate>> getMap(List<PlayerAvgRate> list){
		Map<Integer,List<PlayerAvgRate>> avgMap=new HashMap<Integer, List<PlayerAvgRate>>();
		for(PlayerAvgRate e:list){
			List<PlayerAvgRate> avgList=avgMap.get(e.getPlayerId());
			if(avgList==null){
				avgList=new ArrayList<PlayerAvgRate>();
				avgList.add(e);
				avgMap.put(e.getPlayerId(), avgList);
			}
			else{
				avgList.add(e);
			}
		}
		return avgMap;
	}
	
	public void saveGameVS(){
		List<GameVS> list = new ArrayList<GameVS>();
		Date date = new Date();
		for(int i=-1;i<5;i++){
			list.addAll(pageAnalyzer.getGameVSByDate(UtilDateTime.getNextDateAddDay(date, i)));
		}
		for(GameVS G:list){
			gameDataDAO.saveVS(G);
		}
	}
	
	public void changeTeamRank(){
		List<NBATeamDetail> list = pageAnalyzer.changeTeamRank();
		for(NBATeamDetail T:list){
			gameDataDAO.changeRank(T);
		}
	}
	
	public List<GameVS> getGameVS(){
		List<NBATeamDetail> teamList=nbaDataDAO.getTeams();
		List<GameVS> list = gameDataDAO.getGameVS();
		if(list.size()==0 || list==null)return new ArrayList<GameVS>();
		for(GameVS G:list){
			G.setHomeTeamId(getVsTeamId(G.getHome(),teamList));
			G.setAwayTeamId(getVsTeamId(G.getAway(),teamList));			
		}
		return list;
	}
	
	public List<GameVS> getGameVSByDate(Date date){
		List<NBATeamDetail> teamList=nbaDataDAO.getTeams();
		List<GameVS> list = gameDataDAO.getGameVS(date);
		if(list.size()==0 || list==null)return new ArrayList<GameVS>();
		for(GameVS G:list){
			G.setHomeTeamId(getVsTeamIdByEName(G.getHome(),G.getHomeName(),teamList));
			G.setAwayTeamId(getVsTeamIdByEName(G.getAway(),G.getAwayName(),teamList));			
		}
		return list;
	}
	
	private int getVsTeamIdByEName(String espnName,String eName,List<NBATeamDetail> list){
		for(NBATeamDetail t:list){
			if(t.getEspnName().toLowerCase().equals(espnName.toLowerCase())){
				return t.getTeamId();
			}
			if(eName!=null&&t.getTeamEname().indexOf(eName)!=-1){
				return t.getTeamId();
			}
		}
		return 0;
	}
	
	private int getVsTeamId(String espnName,List<NBATeamDetail> list){
		for(NBATeamDetail t:list){
			if(t.getEspnName().toLowerCase().equals(espnName.toLowerCase()))return t.getTeamId();
		}
		return 0;
	}
	
	public List<NBAGameDetail> getSchedule_not_pk(){
		return gameDataDAO.getSchedule_not_pk(nbaDataDAO.getRandSchedulerTime());
	}
	
	public MatchData getSchedule_not_pk_gameData(NBAGameDetail info){
		return gameDataDAO.getSchedule_not_pk_gameData(info);
	}
	public void addNbaDataRunLog(){
		gameDataDAO.addNbaDataRunLog();
	}
	public int getNbaDataRunLog(){
		return gameDataDAO.getNbaDataRunLog();
	}
	public void addGuessPlayer(String time){
		gameDataDAO.addGuessPlayer(time);
	}
	public void changeTeamId0(int teamId){
		gameDataDAO.changeTeamId0(teamId);
	}	
	public void changeMatchScore(List<NBAGameDetail> list){
		for(NBAGameDetail T:list){
			gameDataDAO.changeMatchScore(T);
		}
	}
	
	
}
