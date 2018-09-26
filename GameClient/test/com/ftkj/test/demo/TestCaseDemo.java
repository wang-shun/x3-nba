package com.ftkj.test.demo;

import org.junit.Test;

import com.ftkj.test.base.BaseTestCase;

public class TestCaseDemo extends BaseTestCase{

	@Test
	public void test() {
		// 测试登录
		this.robot.actionLogin();
	}

}
