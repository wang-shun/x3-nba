package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.FriendPO;
import org.joda.time.DateTime;

import java.util.List;

public class FriendDAO extends GameConnectionDAO {

	
	private RowHandler<FriendPO> FRIENDPO = new RowHandler<FriendPO>() {

		@Override
		public FriendPO handleRow(ResultSetRow row) throws Exception {
			FriendPO po = new FriendPO();
			po.setTeamId(row.getLong("team_id")); 
			po.setFriendTeamId(row.getLong("friend_team_id")); 
			po.setType(row.getInt("type")); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			
			return po;
		}
	};
	
	public List<FriendPO> getFriendByTeam(long teamId) {
        String sql = "select * from t_u_friend where team_id=? and type <> -1";
		return queryForList(sql, FRIENDPO, teamId);
	}
	
	
}
