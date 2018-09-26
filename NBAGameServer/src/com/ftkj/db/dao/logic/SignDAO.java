package com.ftkj.db.dao.logic;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.SignPO;

public class SignDAO extends GameConnectionDAO {

	private RowHandler<SignPO> SIGNPO = new RowHandler<SignPO>() {

		@Override
		public SignPO handleRow(ResultSetRow row) throws Exception {
			SignPO po = new SignPO();
			po.setType(row.getInt("type")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setPeriod(row.getInt("period")); 
			po.setSignNum(row.getInt("sign_num")); 
			po.setPatchNum(row.getInt("patch_num")); 
			po.setTotalSign(row.getInt("total_sign")); 
			po.setTotalPatch(row.getInt("total_patch")); 
			po.setStatus(row.getString("status")); 
			po.setLastSignTime(new DateTime(row.getTimestamp("last_sign_time"))); 
			
			return po;
		}
	};
	
	
	public SignPO getSignMonth(long teamId) {
		String sql = "select * from t_u_sign where team_id = ? and type = 1";
		return queryForObject(sql, SIGNPO, teamId);
	}
	
	public SignPO getSignPeriod(long teamId) {
		String sql = "select * from t_u_sign where team_id = ? and type = 2";
		return queryForObject(sql, SIGNPO, teamId);
	}
	
}
