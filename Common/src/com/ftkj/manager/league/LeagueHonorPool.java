package com.ftkj.manager.league;

import java.util.Map;

import com.ftkj.db.domain.LeagueHonorPoolPO;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年5月25日
 * 联盟成就勋章池
 */
public class LeagueHonorPool {
	private int leagueId;
	private Map<Integer,LeagueHonorPoolPO> propMap;
	
	public LeagueHonorPool(int leagueId) {
		this.leagueId = leagueId;
		this.propMap = Maps.newHashMap();
	}
	
	public void initAppendProp(LeagueHonorPoolPO po){
		this.propMap.put(po.getPid(), po);
	}
	
	
	public int getCurPropCount(int pid){
		LeagueHonorPoolPO po = this.propMap.get(pid);
		return po==null?0:po.getNum();
	}
	
	public void appendHonorProp(int pid,int num){
		LeagueHonorPoolPO po = propMap.computeIfAbsent(pid, (key)->new LeagueHonorPoolPO(this.leagueId,key,0));
		po.setNum(po.getNum()+num);
		po.save();
	}

	public Map<Integer, LeagueHonorPoolPO> getPropMap() {
		return propMap;
	}

	public void setPropMap(Map<Integer, LeagueHonorPoolPO> propMap) {
		this.propMap = propMap;
	}	
	
}
