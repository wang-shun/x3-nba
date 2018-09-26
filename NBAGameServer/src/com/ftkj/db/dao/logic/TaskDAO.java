package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.TaskConditionPO;
import com.ftkj.db.domain.TaskPO;

import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collectors;

public class TaskDAO extends GameConnectionDAO {
	private RowHandler<TaskPO> TASKPO = new RowHandler<TaskPO>() {
		
		@Override
		public TaskPO handleRow(ResultSetRow row) throws Exception {
			TaskPO po = new TaskPO();
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			po.setStatus(row.getInt("status"));
			po.setTeamId(row.getLong("team_id"));
			po.setTid(row.getInt("tid"));
			return po;
		}
	};
	
	private RowHandler<TaskConditionPO> TASKCONDITIONPO = new RowHandler<TaskConditionPO>() {
		
		@Override
		public TaskConditionPO handleRow(ResultSetRow row) throws Exception {
			TaskConditionPO po = new TaskConditionPO();
			po.setCid(row.getInt("cid"));
			po.setTeamId(row.getLong("team_id"));
			po.setTid(row.getInt("tid"));
			po.setValInt(row.getInt("val_int"));
			po.setValStr(row.getString("val_str"));
			return po;
		}
	};
	
	public List<TaskPO> getTeamTaskList(long teamId) {
		String sql = "select * from t_u_task where team_id = ?";
		return queryForList(sql, TASKPO, teamId);
	}

	public List<TaskConditionPO> getTeamTaskConditionList(long teamId,List<Integer> tidList) {
		String ids = tidList.stream().map(t->""+t).collect(Collectors.joining(","));
		String sql = "select * from t_u_task_condition where team_id = ?";
		if(tidList.size()>0){
			sql = sql+" and tid in ( "+ids+" )";
		}
		return queryForList(sql, TASKCONDITIONPO, teamId);
	}
	
	/**
	 * 情况任务条件相关数据
	 * @param tid
	 */
	public void deleteTeamTaskCondition(long teamId, int tid) {
        String sql = "delete from t_u_task_condition where team_id =? and tid = ?";
        execute(sql, teamId, tid);
    }	
}
