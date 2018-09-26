package com.ftkj.test;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class AddFKTestCase extends BaseTestCase{
	
	// 连接服务器
	public AddFKTestCase() {
		super(IPUtil.localIP, 1000001355L);
	}
	
	@Test
	public void test() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.GMManager_addMoneyCallback))
		//long teamId, int fk, int type, long millis
		.send(1011000001355L, 60, 0, 0);
	}
	

}
