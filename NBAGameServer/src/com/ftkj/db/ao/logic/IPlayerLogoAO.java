package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.LogoPO;
import com.ftkj.db.domain.PlayerLogoPO;


public interface IPlayerLogoAO {

	
	/**
	 * 取玩家的头像
	 * @param teamId
	 * @return
	 */
	public List<LogoPO> getLogoPOList(long teamId);
	
	
	/**
	 * 取玩家的所有球员头像
	 * @param teamId
	 * @return
	 */
	public List<PlayerLogoPO> getPlayerLogoPOList(long teamId);
}
