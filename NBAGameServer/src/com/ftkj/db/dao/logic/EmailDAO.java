package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.EmailPO;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmailDAO extends GameConnectionDAO {

	public EmailDAO() {
		DBManager.putGameDelSql("delete from t_u_email where DATE_SUB(CURDATE(), INTERVAL 30 DAY) > date(create_time); ");
	}
	
	private RowHandler<EmailPO> EMAILPO = new RowHandler<EmailPO>() {

		@Override
		public EmailPO handleRow(ResultSetRow row) throws Exception {
			EmailPO po = new EmailPO();
			po.setId(row.getInt("id")); 
			po.setTeamId(row.getLong("team_id")); 
			po.setType(row.getInt("type")); 
			po.setViewId(row.getInt("view_id")); 
			po.setTitle(row.getString("title")); 
			po.setContent(row.getString("content")); 
			po.setStatus(row.getInt("status")); 
			po.setAwardConfig(row.getString("award_config")); 
			po.setCreateTime(new DateTime(row.getTimestamp("create_time"))); 
			
			return po;
		}
	};
	
	public List<EmailPO> getTeamEmailList(long teamId) {
        String sql = "select * from t_u_email where team_id=?";
		return queryForList(sql, EMAILPO, teamId);
	}
	
	public Map<Long, Integer> getTeamEmailSeqMap() {
		String sql = "select team_id, max(id) as seq from (select team_id, id from t_u_email) e group by team_id";
		Map<String, String> map = queryForMap(sql);
		List<Long> keys = map.keySet().stream().mapToLong(k->Long.parseLong(k)).boxed().collect(Collectors.toList());
		return Maps.toMap(keys, k-> Integer.parseInt(map.get(k+"")));
	}
	
	/**
	 * 给所有球队补发邮件奖励
	 * @param sendId
	 */
	public void sendEmailToAllTeam(int sendId) {
		execute("update t_u_email_to_team set status=1 where id=?", sendId);
		execute("insert into t_u_email select e.seq_id, t.team_id, 0, 3, e.title, e.content, 0, e.award_config,now() from t_u_team t left join  t_u_email_to_team e on t.team_id<>e.id where e.id=?;", sendId);
	}

	/**
	 * 查询系统补发邮件
	 * @return
	 */
	public List<Integer> getTeamEmailSendList() {
		String sql = "select id,seq_id from t_u_email_to_team where status=0";
		Map<String, String> map = queryForMap(sql);
		List<Integer> keys = map.keySet().stream().mapToInt(k->Integer.parseInt(k)).boxed().collect(Collectors.toList());
		return keys;
	}

}
