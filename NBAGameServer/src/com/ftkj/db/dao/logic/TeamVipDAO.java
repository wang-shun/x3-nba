package com.ftkj.db.dao.logic;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.VipPO;

public class TeamVipDAO extends GameConnectionDAO {

	private RowHandler<VipPO> VIPPO = new RowHandler<VipPO>() {

		@Override
		public VipPO handleRow(ResultSetRow row) throws Exception {
			VipPO po = new VipPO();
			po.setTeamId(row.getLong("team_id")); 
			po.setAddMoney(row.getInt("add_money")); 
			po.setExp(row.getInt("exp")); 
			po.setLevel(row.getInt("level")); 
			po.setBuyStatus(row.getString("buy_status")); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			po.setUpdateTime(new DateTime(row.getTimestamp("update_time"))); 
			
			return po;
		}
	};
	
	
	public VipPO getTeamVip(long teamId) {
		String sql = "select * from t_u_vip where team_id = ? ";
		return queryForObject(sql, VIPPO, teamId);
	}
	
}
