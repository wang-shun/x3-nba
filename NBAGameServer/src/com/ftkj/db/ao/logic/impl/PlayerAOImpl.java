package com.ftkj.db.ao.logic.impl;

import java.util.Date;
import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IPlayerAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.PlayerDAO;
import com.ftkj.db.dao.logic.PlayerExchangeDAO;
import com.ftkj.db.domain.PlayerAvgInfo;
import com.ftkj.db.domain.PlayerExchangePO;
import com.ftkj.db.domain.PlayerPO;
import com.ftkj.manager.player.PlayerMinPrice;
import com.ftkj.manager.player.PlayerTalent;

/**
 * @author tim.huang
 * 2017年3月8日
 *
 */
public class PlayerAOImpl extends BaseAO implements IPlayerAO {

	@IOC
	private PlayerDAO playerDAO;
	@IOC
	private PlayerExchangeDAO exchangeDAO;
	
	@Override
	public List<PlayerPO> getPlayerList(long teamId) {
		return playerDAO.getPlayerList(teamId);
	}

	@Override
	public List<PlayerTalent> getPlayerTalentList(long teamId) {
		return playerDAO.getPlayerTalentList(teamId);
	}

	@Override
	public List<PlayerMinPrice> getPlayerMinPriceList() {
		return playerDAO.getPlayerMinPriceList();
	}

	@Override
	public List<PlayerAvgInfo> getPlayerAvgList(long teamId) {
		return playerDAO.getPlayerAvgList(teamId);
	}

	@Override
	public List<PlayerExchangePO> getPlayerExchangeList(int beforeDay) {
		return exchangeDAO.getPlayerExchangeList(beforeDay);
	}
	
	@Override
	public List<PlayerExchangePO> getPlayerExchangeListByDate(Date date) {
		return exchangeDAO.getPlayerExchangeListByDate(date);
	}

}
