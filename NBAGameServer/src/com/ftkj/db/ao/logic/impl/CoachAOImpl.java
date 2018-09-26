package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ICoachAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.CoachDAO;
import com.ftkj.manager.coach.Coach;

/**
 * @author tim.huang
 * 2017年9月20日
 *
 */
public class CoachAOImpl extends BaseAO implements ICoachAO{

	@IOC
	private CoachDAO coachDAO;
	
	@Override
	public List<Coach> getTeamCoach(long teamId) {
		return coachDAO.getTeamCoach(teamId);
	}
	
}
