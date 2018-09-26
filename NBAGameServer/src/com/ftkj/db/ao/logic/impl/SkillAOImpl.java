package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ISkillAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.SkillDAO;
import com.ftkj.manager.skill.PlayerSkill;

/**
 * @author tim.huang
 * 2017年9月27日
 *
 */

public class SkillAOImpl extends BaseAO implements ISkillAO{
	
	
	@IOC
	private SkillDAO skillDAO;
	
	
	@Override
	public List<PlayerSkill> getPlayerSkillList(long teamId) {
		return skillDAO.getPlayerSkillList(teamId);
	}
	
}
