package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ITradeAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.TradeDAO;
import com.ftkj.db.domain.TradeP2PPO;
import com.ftkj.db.domain.TradePO;

public class TradeAOImpl extends BaseAO implements ITradeAO {

	@IOC
	private TradeDAO dao;
	
	@Override
	public Integer queryMaxId() {
		return dao.queryMaxId();
	}
	
	@Override
	public List<TradePO> queryTradeList() {
		return dao.queryTradeList();
	}

	@Override
	public List<TradePO> queryPlayerHistoryList(int playerId) {
		return dao.queryPlayerHistoryList(playerId);
	}

	@Override
	public List<TradePO> queryTeamHistoryList(long teamId) {
		return dao.queryTeamHistoryList(teamId);
	}

	@Override
	public List<TradeP2PPO> queryTradeP2PList() {
		return dao.queryTradeP2PList();
	}
	
	@Override
	public Integer queryP2PMaxId() {
		return dao.queryP2PMaxId();
	}
	
	@Override
	public List<TradeP2PPO> queryPlayerP2PHistoryList(int playerId) {
		return dao.queryPlayerP2PHistoryList(playerId);
	}

}
