package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.group.LeagueGroupPO;
import com.ftkj.db.domain.group.LeagueGroupSeasonPO;
import com.ftkj.db.domain.group.LeagueGroupTeamPO;

/**
 * @author tim.huang
 * 2017年5月31日
 *
 */
public class LeagueGroupDAO extends GameConnectionDAO {
	
	public LeagueGroupDAO() {
		DBManager.putGameDelSql("delete from t_u_league_group where status = -1");
		DBManager.putGameDelSql("delete from t_u_league_group_team where status = -1");
	}
	
	private RowHandler<LeagueGroupPO> LEAGUEGROUPPO = new RowHandler<LeagueGroupPO>() {

		@Override
		public LeagueGroupPO handleRow(ResultSetRow row) throws Exception {
			LeagueGroupPO po = new LeagueGroupPO();
			po.setLeagueId(row.getInt("league_id")); 
			po.setGroupId(row.getInt("group_id")); 
			po.setName(row.getString("name")); 
			po.setScore(row.getInt("score")); 
			po.setWinNum(row.getInt("win_num")); 
			po.setLossNum(row.getInt("loss_num")); 
			po.setStatus(row.getInt("status")); 
			
			return po;
		}
	};
	
	private RowHandler<LeagueGroupTeamPO> LEAGUEGROUPTEAMPO = new RowHandler<LeagueGroupTeamPO>() {

		@Override
		public LeagueGroupTeamPO handleRow(ResultSetRow row) throws Exception {
			LeagueGroupTeamPO po = new LeagueGroupTeamPO();
			po.setLeagueId(row.getInt("league_id")); 
			po.setGroupId(row.getInt("group_id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setPrivity(row.getInt("privity")); 
			po.setPosition(row.getInt("position")); 
			po.setLevel(row.getInt("level")); 
			po.setStatus(row.getInt("status")); 
			
			return po;
		}
	};
	
	private RowHandler<LeagueGroupSeasonPO> LEAGUEGROUPSEASONPO = new RowHandler<LeagueGroupSeasonPO>() {

		@Override
		public LeagueGroupSeasonPO handleRow(ResultSetRow row) throws Exception {
			LeagueGroupSeasonPO po = new LeagueGroupSeasonPO();
			po.setId(row.getInt("id")); 
			po.setName(row.getString("name")); 
			po.setStartTime(new DateTime(row.getTimestamp("start_time"))); 
			po.setEndTime(new DateTime(row.getTimestamp("end_time"))); 
			po.setStatus(row.getInt("status")); 
			
			return po;
		}
	};
	
	public LeagueGroupSeasonPO getLeagueGroupSeason() {
		return queryForObject("select * from t_u_league_group_season order by id desc limit 1", LEAGUEGROUPSEASONPO);
	}
	
	public List<LeagueGroupPO> getLeagueGroupList() {
		return queryForList("select * from t_u_league_group where status<>-1", LEAGUEGROUPPO);
	}
	
	public List<LeagueGroupTeamPO> getLeagueGroupTeamList() {
		return queryForList("select * from t_u_league_group_team where status<>-1", LEAGUEGROUPTEAMPO);
	}
	
}
