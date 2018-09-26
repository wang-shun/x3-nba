package com.ftkj.manager.player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年9月28日
 * 球队球员等级
 */
public class TeamPlayerGrade {
	private Map<Integer,PlayerGrade> playerGradeMap;
	
	public TeamPlayerGrade(List<PlayerGrade> list) {
		this.playerGradeMap = list.stream().collect(Collectors.toMap(PlayerGrade::getPlayerId, val->val));
	}
 
	
	public Map<Integer, PlayerGrade> getPlayerGradeMap() {
		return playerGradeMap;
	}



	public PlayerGrade getPlayerGrade(int playerId){
		return this.playerGradeMap.get(playerId);
	}
	
//	public int getGradeSum() {
//		return playerGradeMap.values().stream().mapToInt(s-> s.getGrade()).sum();
//	}
//	
//	public int getStarSum() {
//		return playerGradeMap.values().stream().mapToInt(s-> s.getStar()).sum();
//	}
	
}
