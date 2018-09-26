package com.ftkj.manager.league;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年5月31日
 *
 */
public class LeagueTaskTeam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Integer,Boolean> giftMap;
	
	private Map<Integer,Integer> taskHonorMap;
	
	public LeagueTaskTeam(){
		this.giftMap = Maps.newHashMap();
		this.taskHonorMap = Maps.newHashMap();
	}
	
	
	public void updateGiftStatus(int tid,boolean stat){
		this.giftMap.put(tid, stat);
	}
	
	public void updateHonor(int tid,int val){
		int v = getHonor(tid);
		this.taskHonorMap.put(tid, v+val);
	}
	
	public boolean getGiftStatus(int tid){
		return this.giftMap.computeIfAbsent(tid, key->false);
	}
	
	public int getHonor(int tid){
		return this.taskHonorMap.computeIfAbsent(tid, key->0);
		
	}
	public Map<Integer, Boolean> getGiftMap() {
		return giftMap;
	}

	public Map<Integer, Integer> getTaskHonorMap() {
		return taskHonorMap;
	}
	
	
	
}
