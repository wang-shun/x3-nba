package com.ftkj.action.data.impl;

import java.util.Date;
import java.util.List;

import com.ftkj.action.BaseAction;
import com.ftkj.action.data.NBADataAction;
import com.ftkj.ao.data.GameDataAO;
import com.ftkj.ao.data.NBADataAO;
import com.ftkj.domain.data.NBAGameDetail;
import com.ftkj.domain.data.NBAPlayerDetail;
import com.ftkj.domain.data.NBAPlayerScore;
import com.ftkj.domain.data.NBASeason;
import com.ftkj.domain.data.NBATeamDetail;
import com.ftkj.domain.data.PlayerAbi;
import com.ftkj.domain.data.PlayerAvgRate;
import com.ftkj.domain.data.PlayerPrice;
import com.ftkj.invoker.Resource;
import com.ftkj.invoker.ResourceType;
import com.ftkj.util.UtilDateTime;


public class NBADataActionImpl extends  BaseAction implements NBADataAction{
	
	@Resource(value = ResourceType.NBADataAO)
	public NBADataAO nbaDataAO;
	
	@Resource(value = ResourceType.GameDataAO)
	public GameDataAO gameDataAO;
	
	@Override
	public List<NBAPlayerDetail> getPlayers() {
		return nbaDataAO.getPlayers();
	}
	
	@Override
	public void changeInjured(){
		gameDataAO.changeInjured();
	}

	@Override
	public List<PlayerAbi> getPlayerCaps() {
		return nbaDataAO.getPlayerCaps();
	}

	@Override
	public List<NBATeamDetail> getTeams() {
		return nbaDataAO.getTeams();
	}
	
	@Override
	public PlayerAbi getPlayerAbi(PlayerAvgRate avg){
		return nbaDataAO.getPlayerAbi(avg);
	}	

	/**
	 * 赛季算法
	 */
	@Override
	public void calculateData(Date date) {
		NBASeason season=GameDataAO.getSeason(date);
		if(season==null){
			return;
		}
		nbaDataAO.executePlayerAvg(season.getId());
		nbaDataAO.executePlayerAbi(season.getId(), "");	
		gameDataAO.addGuessPlayer("");//竞猜
		nbaDataAO.addPlayerMoney();
		nbaDataAO.changePlus(season.getId());//状态
		nbaDataAO.delete_price_50();
	}
	
	/**
	 * 随机身价算法
	 */
	@Override
	public void calculateData_rand(Date date) {
		//正常的赛季内，不走随机身价
		NBASeason season=GameDataAO.getSeason(date);
		if(season==null){
			return;
		}
		//nbaDataAO.executePlayerAvg(season.getId());
		String time = UtilDateTime.toDateString(date, UtilDateTime.YYYY_MM_DD);
		nbaDataAO.executePlayerAbi(season.getId(), time);
		try{
			gameDataAO.addGuessPlayer(time);//竞猜,此方法是空方法
		}catch(Exception e){}
		nbaDataAO.addPlayerMoney();
		nbaDataAO._changePlus(season.getId());//状态
	}
	
	@Override
	public Date getRandSchedulerDate(NBASeason season, String startTime, String endTime, int minVS) {
		//排好序的比赛时间，按时间差来取第几个即可.
		List<String> rand_timeList = nbaDataAO.getSchedulerSeason(season.getId(), startTime, endTime, minVS);
		int index = (int) UtilDateTime.reckonDifferenceDay(new Date(), season.getStartDate());
		index = Math.abs(index) % rand_timeList.size();
		String time = rand_timeList.get(index);
		return UtilDateTime.toDataTime(time);
	}

	@Override
	public List<PlayerAvgRate> getPlayerAvgs(int seasonId) {		
		return nbaDataAO.getPlayerAvgs(seasonId);
	}

	@Override
	public List<PlayerPrice> getPlayerMoney(int playerId) {
		return nbaDataAO.getPlayerMoneyList(playerId);
	}
	
	@Override
	public List<NBAPlayerScore> getPlayerScoreDay(int playerId){
		return nbaDataAO.getPlayerScoreDay(playerId);
	}

	@Override
	public void addPlayerMoneyNotData() {
		nbaDataAO.addPlayerMoneyNotData();
	}

	@Override
	public String getMaxNbaGameData() {
		return nbaDataAO.getMaxNbaGameData();
	}

	@Override
	public int getFBGrade(String id) {
		return nbaDataAO.getFBGrade(id);
	}

	@Override
	public void changeTeamId(int playerId, int teamId) {
		nbaDataAO.changeTeamId(playerId, teamId);
	}
	@Override
	public void addNbaDataRunLog(){
		gameDataAO.addNbaDataRunLog();
	}
	@Override
	public int getNbaDataRunLog(){
		return gameDataAO.getNbaDataRunLog();
	}
	@Override
	public void changeTeamId0(int teamId){
		gameDataAO.changeTeamId0(teamId);
	}	
	@Override
	public void changeMatchScore(List<NBAGameDetail>list){
		gameDataAO.changeMatchScore(list);
	}
	
	
	public static void main(String[] args) {
	}
	
}
