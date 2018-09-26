package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.manager.skill.PlayerSkill;

public interface ISkillAO {
	public List<PlayerSkill> getPlayerSkillList(long teamId);
	
	
}
	