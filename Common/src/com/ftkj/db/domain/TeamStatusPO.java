package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;

import java.util.List;

/**
 * @author tim.huang
 * 2017年5月5日
 * 球队状态
 */
public class TeamStatusPO extends AsynchronousBatchDB {
	
	
	@Override
	public String getSource() {
		return null;
	}

	@Override
	public String getRowNames() {
		return null;
	}

    @Override
    public List<Object> getRowParameterList() {
        return null;
    }

	@Override
	public String getTableName() {
		return null;
	}

	@Override
	public void del() {
	}


}
