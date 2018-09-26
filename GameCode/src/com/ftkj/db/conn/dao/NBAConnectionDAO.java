package com.ftkj.db.conn.dao;

import java.sql.Connection;

import com.ftkj.db.conn.annotation.Resource;

/**
 * @author tim.huang
 * 2017年3月15日
 * NBA数据
 */
public class NBAConnectionDAO extends BaseDAO {
	@Resource(value = ResourceType.DB_nba)
	public Database database;
	
	
	@Override
	protected Connection getRealConnection() {
		return database.getConnection();
	}

}
