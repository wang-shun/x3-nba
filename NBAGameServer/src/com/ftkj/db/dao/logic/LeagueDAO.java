package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.LeagueHonorPO;
import com.ftkj.db.domain.LeagueHonorPoolPO;
import com.ftkj.db.domain.LeaguePO;
import com.ftkj.db.domain.LeagueTeamPO;
import com.ftkj.db.domain.LeagueTeamSimplePO;

/**
 * @author tim.huang
 * 2017年5月31日
 *
 */
public class LeagueDAO extends GameConnectionDAO {
	
	private RowHandler<LeaguePO> LEAGUEPO = new RowHandler<LeaguePO>() {
		
		@Override
		public LeaguePO handleRow(ResultSetRow row) throws Exception {
			LeaguePO po = new LeaguePO();
			po.setHonor(row.getInt("honor"));
			po.setHonorLimit(row.getInt("limit_honor"));
			po.setLeagueId(row.getInt("league_id"));
			po.setLeagueLevel(row.getInt("league_level"));
			po.setLeagueExp(row.getInt("league_exp"));
			po.setLeagueTotalExp(row.getInt("league_total_exp"));
			po.setLeagueName(row.getString("league_name"));
			po.setLogo(row.getString("logo"));
			po.setScore(row.getLong("score"));
			po.setPeopleCount(row.getInt("people_count"));
			po.setShopLimit(row.getInt("limit_shop"));
			po.setTeamName(row.getString("team_name"));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			return po;
		}
	};

	
	private RowHandler<LeagueTeamPO> LEAGUETEAMPO = new RowHandler<LeagueTeamPO>() {
		
		@Override
		public LeagueTeamPO handleRow(ResultSetRow row) throws Exception {
			
			LeagueTeamPO po = new LeagueTeamPO();
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			po.setFeats(row.getInt("feats"));
			po.setLeagueId(row.getInt("league_id"));
			po.setLevel(row.getInt("level"));
			po.setScore(row.getInt("score"));
			po.setWeekScore(row.getInt("week_score"));
			po.setTeamId(row.getLong("team_id"));
			return po;
		}
	};
	
	private RowHandler<LeagueTeamSimplePO> LEAGUETEAMSIMPLEPO = new RowHandler<LeagueTeamSimplePO>() {
		
		@Override
		public LeagueTeamSimplePO handleRow(ResultSetRow row) throws Exception {
			LeagueTeamSimplePO po = new LeagueTeamSimplePO();
			po.setLeagueId(row.getInt("league_id"));
			po.setTeamId(row.getLong("team_id"));
			return po;
		}
	};
	
	private RowHandler<LeagueHonorPO> LEAGUEHONORPO = new RowHandler<LeagueHonorPO>() {
		
		@Override
		public LeagueHonorPO handleRow(ResultSetRow row) throws Exception {
			LeagueHonorPO po = new LeagueHonorPO();
			po.setEndTime(new DateTime(row.getTimestamp("end_time")));
			po.setHonorId(row.getInt("honor_id"));
			po.setLeagueId(row.getInt("league_id"));
			po.setLevel(row.getInt("level"));
			return po;
		}
	};
	
	private RowHandler<LeagueHonorPoolPO> LEAGUEHONORPOLLPO = new RowHandler<LeagueHonorPoolPO>() {
		@Override
		public LeagueHonorPoolPO handleRow(ResultSetRow row) throws Exception {
			LeagueHonorPoolPO po = new LeagueHonorPoolPO();
			po.setLeagueId(row.getInt("league_id"));
			po.setNum(row.getInt("num"));
			po.setPid(row.getInt("prop_id"));
			return po;
		}
	};
	
	public List<LeagueTeamSimplePO> getAllLeagueTeam(){
		String sql = "select * from t_u_league_team where league_id>0";
		return queryForList(sql, LEAGUETEAMSIMPLEPO);
	}
	
	public List<LeaguePO> getAllLeague() {
		String sql = "select *  from t_u_league";
		return queryForList(sql, LEAGUEPO);
	}

	public LeagueTeamPO getLeagueTeamPO(long teamId) {
		String sql = "select * from t_u_league_team where team_id = ?";
		return queryForObject(sql, LEAGUETEAMPO, teamId);
	}
	

	public List<LeagueHonorPO> getAllLeagueHonor() {
		String sql = "select * from t_u_league_honor";
		return queryForList(sql, LEAGUEHONORPO);
	}

	public List<LeagueHonorPoolPO> getAllLeagueHonorPool() {
		String sql = "select * from t_u_league_honor_pool";
		return queryForList(sql, LEAGUEHONORPOLLPO);
	}
	
	public void clearAllLeagueTeamWeekScore() {
        String sql = "update t_u_league_team set week_score = 0";
        executeUpdate(sql);
	}
}
