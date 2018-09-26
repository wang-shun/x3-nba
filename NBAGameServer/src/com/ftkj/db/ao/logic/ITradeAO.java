package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.TradeP2PPO;
import com.ftkj.db.domain.TradePO;

public interface ITradeAO {

	public Integer queryMaxId();
	
	public List<TradePO> queryTradeList();
	
	public List<TradePO> queryPlayerHistoryList(int playerId);
	
	public List<TradePO> queryTeamHistoryList(long teamId);
	
	//
	
	public Integer queryP2PMaxId();
	
	public List<TradeP2PPO> queryTradeP2PList();
	
	public List<TradeP2PPO> queryPlayerP2PHistoryList(int playerId);
	
}
