package com.main.test;

import com.ftkj.db.domain.active.base.DBList;

public class ClaTest {

	private int id;
	
	public static class A extends ClaTest {
		
	}
	
	public static void main(String[] args) {
//		A a = new A();
		
//		System.err.println(a);
		
		
		DBList a = new DBList("1,0,1");
		System.err.println(a.getValueStr());
		a.setAllValue(0);
		System.err.println(a.getValueStr());
	}
	
}
