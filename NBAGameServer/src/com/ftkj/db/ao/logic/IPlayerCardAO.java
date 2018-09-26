package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.CardPO;

public interface IPlayerCardAO {

	public List<CardPO> getCardPOList(long teamId);
	
}
