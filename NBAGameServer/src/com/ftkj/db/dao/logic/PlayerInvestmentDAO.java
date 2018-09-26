package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.investment.TeamInvestmentInfo;
import com.ftkj.manager.investment.TeamPlayerInvestment;
import org.joda.time.DateTime;

import java.util.List;

public class PlayerInvestmentDAO extends GameConnectionDAO {

	
	private RowHandler<TeamInvestmentInfo> TEAMINVESTMENTINFO = new RowHandler<TeamInvestmentInfo>() {
		
		@Override
		public TeamInvestmentInfo handleRow(ResultSetRow row) throws Exception {
			TeamInvestmentInfo info = new TeamInvestmentInfo();
			info.setMaxTotal(row.getInt("max_total"));
			info.setMoney(row.getInt("money"));
			info.setTeamId(row.getLong("team_id"));
			return info;
		}
	};
	
	
	private RowHandler<TeamPlayerInvestment> TEAMPLAYERIVESTMENT = new RowHandler<TeamPlayerInvestment>() {
		
		@Override
		public TeamPlayerInvestment handleRow(ResultSetRow row) throws Exception {
			TeamPlayerInvestment player = new TeamPlayerInvestment();
			player.setBasePrice(row.getInt("price"));
			player.setBuyTime(new DateTime(row.getTimestamp("buy_time")));
			player.setPlayerId(row.getInt("player_id"));
			player.setTeamId(row.getLong("team_id"));
			player.setTotal(row.getInt("total"));
			return player;
		}
	};
	
	
	public TeamInvestmentInfo getTeamInvestmentInfo(long teamId) {
		String sql = "select * from t_u_player_inv_team where team_id = ?";
		return queryForObject(sql, TEAMINVESTMENTINFO, teamId);
	}

	public List<TeamPlayerInvestment> getTeamPlayerInvestments(long teamId) {
		String sql = "select * from t_u_player_inv where team_id = ?";
		return queryForList(sql, TEAMPLAYERIVESTMENT, teamId);
	}
	
}
