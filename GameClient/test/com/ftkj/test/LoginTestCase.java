package com.ftkj.test;

import org.junit.Test;

import com.ftkj.test.base.BaseTestCase;

public class LoginTestCase extends BaseTestCase{
	

	@Override
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void test() {
		this.robot.actionLogin();
	}

}
