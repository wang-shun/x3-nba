package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.CustomerServicePO;

/**
 * 超过30天的客服工单会被删除.
 * @author mr.lei
 * 2018年8月31日14:15:25
 */
public class CustomerServiceDAO extends GameConnectionDAO {
	
	public CustomerServiceDAO() {
		//超过30天的客服工单会被删除.
		DBManager.putGameDelSql("delete from t_u_customer_service "
				+ "where DATE_SUB(CURDATE(), INTERVAL 30 DAY) > date(create_time);");
	}

	private RowHandler<CustomerServicePO> CUSTOMERSERVICEPO = new RowHandler<CustomerServicePO>() {
		
		@Override
		public CustomerServicePO handleRow(ResultSetRow row) throws Exception {
			CustomerServicePO po = new CustomerServicePO();
			po.setAreaName(row.getString("area_name"));
			po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
			po.setCsId(row.getLong("cs_id"));
			po.setOccurTime(row.getString("occur_time"));
			po.setPlayerName(row.getString("player_name"));
			po.setProblem(row.getString("problem"));
			po.setResponse(row.getString("response"));
			po.setRespStatus(row.getString("resp_status"));
			po.setTeamId(row.getLong("team_id"));
			po.setTelphone(row.getString("telphone"));
			po.setVipLevel(row.getInt("vip_level"));
			po.setDeleteFlag(row.getInt("delete_flag"));
			
			return po;
		}
	};
	
	public List<CustomerServicePO> getAllCustomerService() {
		String sql = "select cs_id,area_name,team_id,vip_level,player_name,telphone,"
        		+ "problem,response,resp_status,occur_time,delete_flag,create_time "
        		+ "from t_u_customer_service where delete_flag=0 order by create_time desc";
		return queryForList(sql, CUSTOMERSERVICEPO);
	}
	
	public List<CustomerServicePO> getPlayerCustomerServiceData(long teamId) {
		String sql = "select cs_id,area_name,team_id,vip_level,player_name,telphone,"
        		+ "problem,response,resp_status,occur_time,delete_flag,create_time "
        		+ "from t_u_customer_service where team_id=? and delete_flag=0 order by create_time desc";
		return queryForList(sql, CUSTOMERSERVICEPO, teamId);
	}
	
	public CustomerServicePO getCustomerServiceDataByCsId(long csId){
		String sql = "select cs_id,area_name,team_id,vip_level,player_name,telphone,"
        		+ "problem,response,resp_status,occur_time,delete_flag,create_time "
        		+ "from t_u_customer_service where cs_id=?";
		return queryForObject(sql, CUSTOMERSERVICEPO, csId);
	}

	public int getRowCount() {
		return queryForInteger("select count(cs_id) from t_u_customer_service");
	}

}
