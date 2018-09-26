package com.ftkj.db.dao.logic;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.CustomTeamPO;

/**
 * @author tim.huang
 * 2017年8月11日
 *
 */
public class CustomDAO extends GameConnectionDAO {
	
	private RowHandler<CustomTeamPO> CUSTOMTEAMPO = new RowHandler<CustomTeamPO>() {
		
		@Override
		public CustomTeamPO handleRow(ResultSetRow row) throws Exception {
			CustomTeamPO po = new CustomTeamPO();
			po.setTeamId(row.getLong("team_id"));
			po.setMoney(row.getInt("money"));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			return po;
		}
	};
	
	
	public CustomTeamPO getCustomTeam(long teamId) {
		String sql = "select * from t_u_custom_team where team_id = ?";
		return queryForObject(sql, CUSTOMTEAMPO, teamId);
	}
	
	
}
