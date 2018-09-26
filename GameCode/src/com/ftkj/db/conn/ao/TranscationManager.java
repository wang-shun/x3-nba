package com.ftkj.db.conn.ao;

import com.ftkj.db.conn.dao.ConnectionDAO;



/**
 * @author Marc.Wang 2012-4-17 下午03:00:39
 * 功能：事物管理
 */
public class TranscationManager {
	/**
	 * 事物COMMIT
	 */
	public void afterTransaction(){
		ConnectionDAO.commit();
	}
	/**
	 * 开起事物
	 * @param method
	 */
	public void beforeTransaction(){
		ConnectionDAO.startTransaction();
	}
	/**
	 * 结束事物
	 * @param method
	 */
	public void endTransaction(){
		ConnectionDAO.endTransaction();
	}
	/**
	 * 错误回滚
	 * @param method
	 */
	public void onError(){
		ConnectionDAO.rollback();
	}
}
