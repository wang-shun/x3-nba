package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.TradeP2PPO;
import com.ftkj.db.domain.TradePO;

public class TradeDAO extends GameConnectionDAO {

	private RowHandler<TradePO> TRADEPO = new RowHandler<TradePO>() {

		@Override
		public TradePO handleRow(ResultSetRow row) throws Exception {
			TradePO po = new TradePO();
			po.setId(row.getInt("id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setPid(row.getInt("pid")); 
			po.setPlayerId(row.getInt("player_id")); 
			po.setPosition(row.getString("position")); 
			po.setPrice(row.getInt("price")); 
			po.setMarketPrice(row.getInt("market_price")); 
			po.setMoney(row.getInt("money")); 
			po.setStatus(row.getInt("status")); 
			po.setTalent(row.getString("talent"));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			po.setEndTime(new DateTime(row.getTimestamp("end_time"))); 
			po.setDealTime(new DateTime(row.getTimestamp("deal_time"))); 
			po.setBuyTeam(row.getLong("buy_team")); 
			return po;
		}
	};
	
	/**
	 * 查询最大ID
	 * @return
	 */
	public Integer queryMaxId() {
		String sql = "select max(id) from t_u_trade;";
		return queryForInteger(sql);
	}
	
	/**
	 * 查询当前交易列表（上架）
	 * @return
	 */
	public List<TradePO> queryTradeList() {
		String sql = "select * from t_u_trade where status=0";
		return queryForList(sql, TRADEPO);
	}
	
	/**
	 * 近30天球员交易记录
	 * @return
	 */
	public List<TradePO> queryPlayerHistoryList(int playerId) {
		String sql = "select * from t_u_trade where player_id=? and status=2 and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(deal_time) order by deal_time desc limit 30";
		return queryForList(sql, TRADEPO, playerId);
	}

	/**
	 * 近30天球队交易记录
	 * @param teamId
	 * @return
	 */
	public List<TradePO> queryTeamHistoryList(long teamId) {
		String sql = "select * from t_u_trade where (team_id=? or buy_team=?) and status=2 and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(deal_time) order by deal_time desc limit 30";
		return queryForList(sql, TRADEPO, teamId, teamId);
	}

	//------------------------------
	
	private RowHandler<TradeP2PPO> TRADEP2PPO = new RowHandler<TradeP2PPO>() {

		@Override
		public TradeP2PPO handleRow(ResultSetRow row) throws Exception {
			TradeP2PPO po = new TradeP2PPO();
			po.setId(row.getInt("id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setPlayerId(row.getInt("player_id")); 
			po.setPosition(row.getString("position")); 
			po.setPrice(row.getInt("price")); 
			po.setMarketPrice(row.getInt("market_price")); 
			po.setBuyMoney(row.getInt("money")); 
			po.setStatus(row.getInt("status")); 
			po.setMinTalent(row.getInt("talent"));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			po.setEndTime(new DateTime(row.getTimestamp("end_time"))); 
			po.setDealTime(new DateTime(row.getTimestamp("deal_time"))); 
			po.setBuyTeam(row.getLong("buy_team")); 
			return po;
		}
	};
	
	/**
	 * 查询最大ID
	 * @return
	 */
	public Integer queryP2PMaxId() {
		String sql = "select max(id) from t_u_trade_p2p;";
		return queryForInteger(sql);
	}
	
	/**
	 * 求购
	 * @return
	 */
	public List<TradeP2PPO> queryTradeP2PList() {
		String sql = "select * from t_u_trade_p2p where status=0";
		return queryForList(sql, TRADEP2PPO);
	}
	
	/**
	 * 近30天球员交易记录
	 * @return
	 */
	public List<TradeP2PPO> queryPlayerP2PHistoryList(int playerId) {
		String sql = "select * from t_u_trade_p2p where player_id=? and status=2 and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(deal_time) order by deal_time desc limit 30";
		return queryForList(sql, TRADEP2PPO, playerId);
	}
	
}
