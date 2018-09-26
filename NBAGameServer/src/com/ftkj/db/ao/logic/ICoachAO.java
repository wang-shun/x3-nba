package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.manager.coach.Coach;

public interface ICoachAO {
	
	public List<Coach> getTeamCoach(long teamId);
}
