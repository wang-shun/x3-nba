package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IPlayerGradeAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.PlayerDAO;
import com.ftkj.manager.player.PlayerGrade;

/**
 * @author tim.huang
 * 2017年9月28日
 *
 */
public class PlayerGradeAOImpl extends BaseAO implements IPlayerGradeAO {

	@IOC
	private PlayerDAO playerDAO;
	
	public List<PlayerGrade> getPlayerGradeList(long teamId) {
		return playerDAO.getPlayerGradeList(teamId);
	}

}
