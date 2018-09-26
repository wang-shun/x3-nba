package com.ftkj.db.ao.logic.syn;

import com.ftkj.db.conn.ao.SynAO;

/**
 * @author tim.huang
 * 2016年12月28日
 * 异步DB操作,接口继承SynAO即可
 */
public interface DemoSynAO extends SynAO{
	void updateName(int id,String name);
}
