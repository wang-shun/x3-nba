package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.DataConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.bean.ConfigBeanVO;

import java.util.List;

/**
 * @author tim.huang
 * 2017年3月7日
 * 缓存配置DAO
 */
//@Deprecated
public class CacheConfigDAO extends DataConnectionDAO {
	

	
	private RowHandler<ConfigBeanVO> CONFIGBEANPO = new RowHandler<ConfigBeanVO>() {
		
		@Override
		public ConfigBeanVO handleRow(ResultSetRow row) throws Exception {
			ConfigBeanVO po = new ConfigBeanVO();
			po.setKey(row.getString("key"));
			po.setVal(row.getString("value"));
			return po;
		}
	};
	
	public List<ConfigBeanVO> getAllConfigBean() {
		String sql = "select * from t_c_config";
		return queryForList(sql, CONFIGBEANPO);
	}
	
}
