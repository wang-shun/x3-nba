package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.BeSignPlayerPO;
import org.joda.time.DateTime;

import java.util.List;

public class BeSignPlayerDAO extends GameConnectionDAO {

	/**
	 * 过期30天清理，已签和移除清理
	 */
	public BeSignPlayerDAO() {
		DBManager.putGameDelSql("delete from t_u_besign where DATE_SUB(CURDATE(), INTERVAL 30 DAY) > date(end_time) or player_id = -1; ");
	}

	private RowHandler<BeSignPlayerPO> BESIGNPLAYERPO = new RowHandler<BeSignPlayerPO>() {

		@Override
		public BeSignPlayerPO handleRow(ResultSetRow row) throws Exception {
			BeSignPlayerPO po = new BeSignPlayerPO();
			po.setId(row.getInt("id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setPlayerId(row.getInt("player_id")); 
			po.setPrice(row.getInt("price")); 
			po.setTid(row.getInt("tid"));
            po.setBind(row.getBoolean("bind"));
            po.setEndTime(new DateTime(row.getTimestamp("end_time")));
			return po;
		}
	};
	
	/**
	 * 没过期的待签球员
	 * @param teamId
	 * @return
	 */
	public List<BeSignPlayerPO> getBeSignPlayerList(long teamId) {
        String sql = "select * from t_u_besign where team_id = ? and end_time > now()";
		return queryForList(sql, BESIGNPLAYERPO, teamId);
	}

}
