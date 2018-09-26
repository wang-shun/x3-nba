package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.TacticsPO;

/**
 * @author tim.huang
 * 2017年3月8日
 *
 */
public class TacticsDAO extends GameConnectionDAO {
	
	
	private RowHandler<TacticsPO> TACTICSPO = new RowHandler<TacticsPO>() {
		
		@Override
		public TacticsPO handleRow(ResultSetRow row) throws Exception {
			TacticsPO po = new TacticsPO();
			po.setLevel(row.getInt("level"));
			po.setTeamId(row.getLong("team_id"));
			po.setTid(row.getInt("tid"));
			po.setBuffTime(new DateTime(row.getTimestamp("buff_time")));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			return po;
		}
	};
	
	
	public List<TacticsPO> getTacticsList(long teamId) {
		String sql = "select * from t_u_tactics where team_id = ?";
		return queryForList(sql, TACTICSPO, teamId);
	}

	
	
	
	
}
