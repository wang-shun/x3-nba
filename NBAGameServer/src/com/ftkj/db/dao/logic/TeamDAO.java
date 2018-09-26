package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.MoneyPO;
import com.ftkj.db.domain.TeamPO;
import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2017年3月15日
 * 
 */
public class TeamDAO extends GameConnectionDAO {
	
	private RowHandler<TeamPO> TEAMPO = new RowHandler<TeamPO>() {
		
		@Override
		public TeamPO handleRow(ResultSetRow row) throws Exception {
			TeamPO po = new TeamPO();
			po.setLevel(row.getInt("level"));
			po.setLogo(row.getString("logo"));
			po.setName(row.getString("name"));
			po.setPrice(row.getInt("price"));
			po.setShardId(row.getInt("shard_id"));
			po.setTeamId(row.getLong("team_id"));
			po.setTitle(row.getString("title"));
			po.setUserId(row.getLong("user_id"));
			po.setSecId(row.getInt("sec_id"));
			po.setPriceCount(row.getInt("price_count"));
			po.setLastLoginTime(new DateTime(row.getTimestamp("last_login_time")));
			po.setLastOfflineTime(new DateTime(row.getTimestamp("last_offline_time")));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			po.setHelp(row.getString("help_step"));
			po.setTaskStep(row.getInt("task_step"));
			po.setLineupCount(row.getInt("lineup_count"));
			po.setUserStatus(row.getInt("user_status"));
			po.setChatStatus(row.getInt("chat_status"));
			return po;
		}
	};
	
	private RowHandler<TeamPO> SIMPLETEAMPO = new RowHandler<TeamPO>() {
		
		@Override
		public TeamPO handleRow(ResultSetRow row) throws Exception {
			TeamPO po = new TeamPO();
			po.setName(row.getString("name"));
			po.setTeamId(row.getLong("team_id"));
			return po;
		}
	};
	
	private RowHandler<MoneyPO> MONEYPO = new RowHandler<MoneyPO>() {
		
		@Override
		public MoneyPO handleRow(ResultSetRow row) throws Exception {
			MoneyPO po = new MoneyPO();
			po.setExp(row.getInt("exp"));
			po.setGold(row.getInt("gold"));
			po.setMoney(row.getInt("money"));
			po.setTeamId(row.getLong("team_id"));
			po.setBdMoney(row.getInt("bd_money"));
			return po;
		}
	};
	public TeamPO getTeam(long teamId) {
		String sql = "select * from t_u_team where team_id = ?";
		return queryForObject(sql, TEAMPO, teamId);
	}

	public List<TeamPO> getAllSimpleTeam() {
		String sql = "select team_id,name from t_u_team";
		return queryForList(sql, SIMPLETEAMPO);
	}

	public MoneyPO getTeamMoney(long teamId) {
		String sql = "select * from t_u_money where team_id = ?";
		return queryForObject(sql, MONEYPO, teamId);
	}
	
	/**
	 * 禁言球队列表
	 * @return
	 */
	public List<Long> getChatBlackTeamList() {
		String sql = "select team_id from t_u_team where chat_status = 1";
		return queryForList(sql, new RowHandler<Long>() {
			@Override
			public Long handleRow(ResultSetRow row) throws Exception {
				return row.getLong("team_id");
			}
		});
	}
	
	/**
	 * 封号列表
	 * @return
	 */
	public List<Long> getLockBlackTeamList() {
		String sql = "select team_id from t_u_team where user_status = 1";
		return queryForList(sql, new RowHandler<Long>() {
			@Override
			public Long handleRow(ResultSetRow row) throws Exception {
				return row.getLong("team_id");
			}
		});
	}
	
	//------------------------------------------------------------------------------
	private RowHandler<String> TableNamePO = new RowHandler<String>() {
		@Override
		public String handleRow(ResultSetRow row) throws Exception {
			return row.getString("table_name");
		}
	};
	
	/**
	 * 暂时只有清档调用
	 * 20个球队以上，不执行操作 
	 * @param teamId
	 */
	public void clearAllData() {
		int teamNum  = queryForInteger("select count(1) num from t_u_team;");
		if(teamNum > 2) {
			return;
		}
		String tableNameSql = "select table_name from information_schema.tables where table_schema='nba_?'";
		List<String> tablesList = queryForList(tableNameSql, TableNamePO, GameSource.shardId);
		// 查询所有表并删除
		for(String table : tablesList) {
			execute("delete from ?", table);
		}
	}
	
	
}
