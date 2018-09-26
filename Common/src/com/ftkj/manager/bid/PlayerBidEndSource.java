package com.ftkj.manager.bid;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author tim.huang
 * 2018年3月26日
 * 结算数据
 */
public class PlayerBidEndSource implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Map<Integer,Set<Long>> teams;
	

	public PlayerBidEndSource() {
		this.teams = Maps.newConcurrentMap();
	}

	public Set<Long> getTeams(int id) {
		Set<Long> result = teams.get(id);
		if(result == null){
			result = Sets.newHashSet();
		}
		return result;
	}
	
	public void putAllTeam(int id,Set<Long> ts){
		this.teams.put(id, ts);
	}

	public Map<Integer, Set<Long>> getTeams() {
		return teams;
	}
	
	
	
	
	
}
