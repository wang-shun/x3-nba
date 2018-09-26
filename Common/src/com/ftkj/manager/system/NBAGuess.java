package com.ftkj.manager.system;

import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author tim.huang
 * 2018年3月20日
 * 球赛竞猜
 */
public class NBAGuess {
	
	private int gameId;
	private int homeId;
	private int awayId;
	private DateTime time;
	private int winId;
	private Set<Long> homeTeamIds;
	private Set<Long> awayTeamIds;
	
	public NBAGuess(int gameId, int homeId, int awayId, DateTime time,
			int winId) {
		super();
		this.gameId = gameId;
		this.homeId = homeId;
		this.awayId = awayId;
		this.time = time;
		this.winId = winId;
		this.homeTeamIds = Sets.newConcurrentHashSet();
		this.awayTeamIds = Sets.newConcurrentHashSet();
	}
	
	public int getTeamGuessId(long teamId){
		if(this.homeTeamIds.contains(teamId)){
			return this.homeId;
		}
		if(this.awayTeamIds.contains(teamId)){
			return this.awayId;
		}
		return 0;
	}

	public void addTeam(int tid,long teamId){
		if(tid == this.homeId){
			this.homeTeamIds.add(teamId);
		}else{
			this.awayTeamIds.add(teamId);
		}
	}

	public Set<Long> getHomeTeamIds() {
		return homeTeamIds;
	}

	public Set<Long> getAwayTeamIds() {
		return awayTeamIds;
	}

	public void setWinId(int winId) {
		this.winId = winId;
	}

	public int getGameId() {
		
		return gameId;
	}

	public int getHomeId() {
		return homeId;
	}

	public int getAwayId() {
		return awayId;
	}

	public DateTime getTime() {
		return time;
	}

	public int getWinId() {
		return winId;
	}
	
	
}
