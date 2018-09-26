package com.ftkj.db.dao.logic;

import java.util.List;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.StagePO;

public class StageDAO extends GameConnectionDAO {

	private RowHandler<StagePO> STAGEPO = new RowHandler<StagePO>() {

		@Override
		public StagePO handleRow(ResultSetRow row) throws Exception {
			StagePO po = new StagePO();
			po.setTeamId(row.getLong("team_id")); 
			po.setScene(row.getInt("scene")); 
			po.setStageId(row.getInt("stage_id")); 
			po.setStep(row.getInt("step")); 
			po.setScore(row.getString("score")); 
			
			return po;
		}
	};
	
	
	
	public List<StagePO> getStageList() {
		String sql = "select * from t_u_stage ";
		return queryForList(sql, STAGEPO);
	}

	public StagePO getStageByTeam(long teamId) {
		String sql = "select * from t_u_stage where team_id = ?";
		return queryForObject(sql, STAGEPO, teamId);
	}
	
	
	
}
