package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.manager.player.PlayerGrade;

public interface IPlayerGradeAO {
	
	public List<PlayerGrade> getPlayerGradeList(long teamId);
	
	
}
