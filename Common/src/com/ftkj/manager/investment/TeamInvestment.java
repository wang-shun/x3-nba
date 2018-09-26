package com.ftkj.manager.investment;

import java.util.Map;

import org.joda.time.DateTime;

public class TeamInvestment {

	private TeamInvestmentInfo info;
	private Map<Integer, TeamPlayerInvestment> playerMap;

	
	
	
	public TeamInvestment(TeamInvestmentInfo info,
			Map<Integer, TeamPlayerInvestment> playerMap) {
		super();
		this.info = info;
		this.playerMap = playerMap;
	}




	public TeamPlayerInvestment addPlayer(int playerId,int price,int num){
		TeamPlayerInvestment player = getPlayer(playerId);
		if(player == null){
			player = new TeamPlayerInvestment(this.info.getTeamId(),playerId,0,DateTime.now(),price);
			this.playerMap.put(playerId, player);
		}
		player.buy(num, price);
		return player;
	}
	
	
	
	
	public TeamPlayerInvestment getPlayer(int playerId){
		return playerMap.get(playerId);
	}
	
	
	/**
	 * 获得当前持有量
	 * @return
	 */
	public int getTotal(){
		return playerMap.values().stream().mapToInt(player->player.getTotal()).sum();
	}
	
	
	
	
	
	
	public TeamInvestmentInfo getInfo() {
		return info;
	}

	public void setInfo(TeamInvestmentInfo info) {
		this.info = info;
	}

	public Map<Integer, TeamPlayerInvestment> getPlayerMap() {
		return playerMap;
	}

	public void setPlayerMap(Map<Integer, TeamPlayerInvestment> playerMap) {
		this.playerMap = playerMap;
	}

}
