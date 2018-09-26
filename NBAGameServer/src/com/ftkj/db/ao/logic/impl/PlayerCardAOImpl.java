package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IPlayerCardAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.PlayerCardDAO;
import com.ftkj.db.domain.CardPO;

public class PlayerCardAOImpl extends BaseAO implements IPlayerCardAO {
	
	@IOC
	private PlayerCardDAO playerCardDAO;

	@Override
	public List<CardPO> getCardPOList(long teamId) {
		return playerCardDAO.getCardPOList(teamId);
	}

}
