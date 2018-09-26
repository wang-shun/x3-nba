package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.coach.Coach;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author tim.huang
 * 2017年9月20日
 *
 */
public class CoachDAO extends GameConnectionDAO {
	
	
	private RowHandler<Coach> COACH = new RowHandler<Coach>() {
		
		@Override
		public Coach handleRow(ResultSetRow row) throws Exception {
			Coach coach = new Coach();
			coach.setCid(row.getInt("cid"));
			coach.setStatus(row.getInt("status"));
			coach.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			coach.setTeamId(row.getLong("team_id"));
			coach.setTid(row.getInt("tid"));
			return coach;
		}
	};
	
	public List<Coach> getTeamCoach(long teamId) {
        String sql = "select * from t_u_coach where team_id = ?";
		return queryForList(sql, COACH, teamId);
	}
}
