package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.BuffPO;

public class BuffDAO extends GameConnectionDAO {

	private RowHandler<BuffPO> BUFFPO = new RowHandler<BuffPO>() {

		@Override
		public BuffPO handleRow(ResultSetRow row) throws Exception {
			BuffPO po = new BuffPO();
			po.setId(row.getInt("id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setParams(row.getString("params")); 
			po.setEndTime(new DateTime(row.getTimestamp("end_time"))); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			
			return po;
		}
	};
	
	public List<BuffPO> getTeamBuffList(long teamId) {
		String sql = "select * from t_u_buff where team_id=?";
		return queryForList(sql, BUFFPO, teamId);
	}
	
	
}
