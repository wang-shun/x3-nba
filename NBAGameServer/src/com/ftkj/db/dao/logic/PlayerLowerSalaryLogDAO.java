package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;

/**
 * 新秀降薪DAO
 * @author mr.lei
 * @time 2018年8月6日16:44:29
 */
public class PlayerLowerSalaryLogDAO extends GameConnectionDAO {
	
	/**
	 * 获取, 最大主键Id的值.
	 * @return
	 */
	public long queryMaxKeyId() {
        String sql = "select max(pls_id) from t_u_player_lower_salary_log";
        Long idLong = queryForLong(sql);
        return idLong == null ? 0 : idLong;
	}
}
