package com.ftkj.manager.coach;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.enums.EStatus;
import com.google.common.collect.Lists;

/**
 * @author tim.huang
 * 2017年9月18日
 * 教练数据
 */
public class TeamCoach {
	private Map<Integer,Coach> coachMap;
	
	public TeamCoach(List<Coach> coachList) {
		super();
		this.coachMap = coachList.stream().collect(Collectors.toMap(key->key.getCid(), val->val));
	}
	
	public void addCoach(Coach coach){
		this.coachMap.put(coach.getCid(), coach);
		coach.save();
	}
	
	public Coach getDefaultCoach(){
		return this.coachMap.values().stream().filter(coach->coach.getStatus() == EStatus.Open.getId()).findFirst().orElse(null);
	}

	public Coach getTeamCoach(int cid){
		return this.coachMap.get(cid);
	}
	
	public List<Coach> getCoachList(){
		return Lists.newArrayList(coachMap.values());
	}
}
