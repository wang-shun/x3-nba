package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.PropPO;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author tim.huang
 * 2017年3月13日
 * 
 */
public class PropDAO extends GameConnectionDAO {
	
	private RowHandler<PropPO> PROPPO = new RowHandler<PropPO>() {
		
		@Override
		public PropPO handleRow(ResultSetRow row) throws Exception {
			PropPO po = new PropPO();
			po.setConfig(row.getString("config"));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			po.setEndTime(new DateTime(row.getTimestamp("end_time")));
			po.setNum(row.getInt("num"));
			po.setPropId(row.getInt("pid"));
			po.setTeamId(row.getLong("team_id"));
			po.setId(row.getInt("id"));
			return po;
		}
	};
	
	public List<PropPO> getPropList(long teamId) {
		String sql = "select * from t_u_prop where team_id = ?";
		return queryForList(sql, PROPPO, teamId);
	}
}

