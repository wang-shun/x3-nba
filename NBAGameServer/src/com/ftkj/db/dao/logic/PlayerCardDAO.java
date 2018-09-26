package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.CardPO;

/**
 * 球星卡
 * @author Jay
 * @time:2017年3月16日 下午6:08:11
 */
public class PlayerCardDAO extends GameConnectionDAO {

	public PlayerCardDAO() {
//		DBManager.putGameDelSql("delete from t_u_card where player_id < 0; ");
	}
	
	private RowHandler<CardPO> CARDPO = new RowHandler<CardPO>() {

		@Override
		public CardPO handleRow(ResultSetRow row) throws Exception {
			CardPO po = new CardPO();
			po.setTeamId(row.getLong("team_id")); 
			po.setPlayerId(row.getInt("player_id")); 
			po.setType(row.getInt("type")); 
			po.setQua(row.getInt("qua")); 
			po.setStarLv(row.getInt("starLv")); 
			po.setExp(row.getInt("exp"));
			po.setCostNum(row.getInt("cost_num"));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			
			return po;
		}
	};
	
	public List<CardPO> getCardPOList(long teamId) {
        String sql = "select * from t_u_card where team_id=?";
		return queryForList(sql, CARDPO, teamId);
	}

}
