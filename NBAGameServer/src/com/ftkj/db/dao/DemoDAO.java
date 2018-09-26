package com.ftkj.db.dao;

import java.util.List;

import com.ftkj.db.conn.dao.DataConnectionDAO;

public class DemoDAO extends DataConnectionDAO {
	
	public List<Long> testLongList(){
		String sql = "select id from t_c_demo";
		return queryForList(sql, LONG_ROW_HANDLER);
	}
	
	public void synTest(){
		String sql = "update t_c_test set sex = 1";
		executeUpdate(sql);
//		Date now = new Date();
//		TestPO tp1 = new TestPO("t", 2, 2, now);
//		TestPO tp2 = new TestPO("s", 1, 3, now);
//		TestPO tp3 = new TestPO("dog", 5, 6, now);
//		TestPO tp4 = new TestPO("b", 7, 7, now);
//		List<TestPO> list = new ArrayList<TestPO>();
//		list.add(tp1);
//		list.add(tp2);
//		list.add(tp3);
//		list.add(tp4);
//		StringBuffer sb = new StringBuffer("");
//		for(TestPO tp : list){
//			sb.append(tp.getSource());
//		}
//		InputStream is  = new ByteArrayInputStream(sb.toString().getBytes());
//		loadData(is, "t_c_test",tp1.getRowNames());
	}
	
}
