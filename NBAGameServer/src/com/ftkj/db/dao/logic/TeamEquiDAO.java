package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.EquiPO;
import org.joda.time.DateTime;

import java.util.List;

/**
 * 
 * @Description: 装备DAO
 * @author Jay
 * @time:2017年3月8日 下午2:29:04
 */
public class TeamEquiDAO extends GameConnectionDAO {

	private RowHandler<EquiPO> TEAMEQUIPO = new RowHandler<EquiPO>() {

		@Override
		public EquiPO handleRow(ResultSetRow row) throws Exception {
			EquiPO po = new EquiPO();
			po.setId(row.getInt("id")); 
			po.setType(row.getInt("type"));
			po.setTeamId(row.getLong("team_id")); 
			po.setEquId(row.getInt("equi_id")); 
			po.setPlayerId(row.getInt("player_id")); 
			po.setEquiTeam(row.getInt("equi_team")); 
			po.setStrLv(row.getInt("strLv")); 
			po.setStrBless(row.getFloat("str_bless")); 
			po.setRandAttr(row.getString("rand_attr")); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			po.setEndTime(new DateTime(row.getTimestamp("end_time"))); 
			return po;
		}
	};
	
	/**
	 * 查询装备列表
	 * @return
	 */
	public List<EquiPO> getTeamEquiPOList(long teamId) {
        String sql = "select * from t_u_equi where team_id=?";
		return queryForList(sql, TEAMEQUIPO, teamId);
	}
	
	
}
