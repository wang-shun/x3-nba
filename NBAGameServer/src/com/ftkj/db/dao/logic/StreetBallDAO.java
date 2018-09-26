package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.StreetBallPO;

public class StreetBallDAO extends GameConnectionDAO {

	private RowHandler<StreetBallPO> STREETBALLPO = new RowHandler<StreetBallPO>() {

		@Override
		public StreetBallPO handleRow(ResultSetRow row) throws Exception {
			StreetBallPO po = new StreetBallPO();
			po.setTeamId(row.getLong("team_id")); 
			po.setType1(row.getInt("type_1")); 
			po.setType2(row.getInt("type_2")); 
			po.setType3(row.getInt("type_3")); 
			po.setType4(row.getInt("type_4")); 
			po.setType5(row.getInt("type_5")); 
			
			return po;
		}
	};
	
	public StreetBallPO getStreetBallByTeam(long teamId) {
		String sql = "select * from t_u_street_ball where team_id = ?";
		return queryForObject(sql, STREETBALLPO, teamId);
	}

}
