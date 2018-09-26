package com.ftkj.db.dao.logic;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.PlayerExchangePO;

public class PlayerExchangeDAO extends GameConnectionDAO {
	
	public PlayerExchangeDAO() {
		DBManager.putGameDelSql("delete from t_c_player_exchange where DATE_SUB(CURDATE(), INTERVAL 30 DAY) > date(create_date); ");
	}

	private RowHandler<PlayerExchangePO> PLAYEREXCHANGEPO = new RowHandler<PlayerExchangePO>() {

		@Override
		public PlayerExchangePO handleRow(ResultSetRow row) throws Exception {
			PlayerExchangePO po = new PlayerExchangePO();
			po.setPlayerId(row.getInt("player_id")); 
			po.setExchangeNum(row.getInt("exchange_num")); 
			po.setCreateDate(new DateTime(row.getTimestamp("create_date"))); 
			
			return po;
		}
	};
	
	/**
	 * 查询多少天以前的球员兑换数量
	 * @param beforeDay
	 * @return
	 */
	public List<PlayerExchangePO> getPlayerExchangeList(int beforeDay) {
		String sql = "select player_id,create_date,sum(exchange_num) exchange_num from t_c_player_exchange where DATE_SUB(CURDATE(), INTERVAL ? DAY) > date(create_date) group by player_id,create_date";
		return queryForList(sql, PLAYEREXCHANGEPO, beforeDay);
	}
	
	public List<PlayerExchangePO> getPlayerExchangeListByDate(Date date) {
		String sql = "select player_id,create_date,exchange_num from t_c_player_exchange where date(create_date)=?";
		return queryForList(sql, PLAYEREXCHANGEPO, date);
	}
}
