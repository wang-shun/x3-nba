package com.ftkj.db.ao.logic.impl;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.DemoAO;
import com.ftkj.db.ao.logic.syn.DemoSynAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.DemoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DemoAOImpl extends BaseAO implements DemoAO,DemoSynAO{
	private static final Logger log = LoggerFactory.getLogger(DemoAOImpl.class);
	@IOC
	private DemoDAO dao;
	
	@Override
	public List<Long> testLongList(){
		return dao.testLongList();
	}

	@Override
	public void updateName(int id, String name) {
		log.error("该请求为异步操作，观察日志打印的线程名。");
	}
	
	
}
