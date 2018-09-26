package com.ftkj.manager.custom;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年8月28日
 * 擂台赛竞猜
 */
public class CustomGuess {
	private int roomId;
	private Map<Long,CustomTeamGuess> teamGuess;
	
	public CustomGuess(int roomId) {
		super();
		this.roomId = roomId;
		teamGuess = Maps.newConcurrentMap();
	}
	
	public CustomTeamGuess getTeamGuess(long teamId){
		return this.teamGuess.get(teamId);
	}

	public Map<Long, CustomTeamGuess> getTeamGuess() {
		return teamGuess;
	}


	public int getRoomId() {
		return roomId;
	}

	public boolean hasTeam(long teamId){
		return teamGuess.containsKey(teamId);
	}
	
}
