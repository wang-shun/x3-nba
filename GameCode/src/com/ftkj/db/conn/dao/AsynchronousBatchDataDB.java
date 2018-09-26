package com.ftkj.db.conn.dao;

/**
 * 公共数据库继承
 * @author Jay
 * @time:2018年1月10日 上午10:00:41
 */
public abstract class AsynchronousBatchDataDB extends AsynchronousBatchDB {

	@Override
	public synchronized void save() {
        if (this.sourceStatus) {
            return;
        }
        this.sourceStatus = true;
        DBManager.putAsynchronousDataDB(this);
    }

}
