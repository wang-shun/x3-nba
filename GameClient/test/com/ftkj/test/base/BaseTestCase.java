package com.ftkj.test.base;

import org.junit.After;
import org.junit.Before;

import com.ftkj.robot.ZGameRobot;
import com.ftkj.util.IPUtil;

public abstract class BaseTestCase {

	protected ZGameRobot robot;
	
	private static String ip = IPUtil.getLocalIp();
//	private static String ip = "192.168.10.181";
	private static long userId = 1000103;
	
	public BaseTestCase(String ip, long userId) {
		BaseTestCase.ip = ip;
		BaseTestCase.userId = userId;
		robot = new ZGameRobot(ip, 8038, userId);
	}
	
	public BaseTestCase() {
		robot = new ZGameRobot(ip, 8038, userId);
	}
	
	
	@Before
	public void setUp() throws Exception {
		this.robot.actionLogin();
	}
	
	@After
	public void after() {
		this.robot.sleep(3000);
	}
	
}
