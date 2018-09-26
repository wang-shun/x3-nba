package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.LogoPO;
import com.ftkj.db.domain.PlayerLogoPO;

/**
 * 头像
 * @author Jay
 * @time:2017年3月16日 下午6:08:11
 */
public class PlayerLogoDAO extends GameConnectionDAO {

	public PlayerLogoDAO() {
		DBManager.putGameDelSql("delete from t_u_logo where player_id < 0");
	}
	
	private RowHandler<PlayerLogoPO> PLAYERLOGOPO = new RowHandler<PlayerLogoPO>() {

		@Override
		public PlayerLogoPO handleRow(ResultSetRow row) throws Exception {
			PlayerLogoPO po = new PlayerLogoPO();
			po.setTeamId(row.getLong("team_id")); 
			po.setPlayerId(row.getInt("player_id")); 
			po.setLogoId(row.getInt("logo_id")); 
			po.setLv(row.getInt("lv")); 
			po.setStarLv(row.getInt("starLv")); 
			po.setStep(row.getInt("step")); 
			
			return po;
		}
	};
	
	private RowHandler<LogoPO> LOGOPO = new RowHandler<LogoPO>() {

		@Override
		public LogoPO handleRow(ResultSetRow row) throws Exception {
			LogoPO po = new LogoPO();
			po.setId(row.getInt("id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setPlayerId(row.getInt("player_id")); 
			po.setQuality(row.getInt("quality")); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			return po;
		}
	};
	
	
	
	public List<LogoPO> getLogoPOList(long teamId) {
		String sql = "select * from t_u_logo where team_id = ? and player_id > 0";
		return queryForList(sql, LOGOPO, teamId);
	}
	
	
	public List<PlayerLogoPO> getPlayerLogoPOList(long teamId) {
		String sql = "select * from t_u_logo_player where team_id = ?";
		return queryForList(sql, PLAYERLOGOPO, teamId);
	}
	
	

}
