package com.ftkj.test.player;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class PlayerTrainTestCase extends BaseTestCase{
	
	public PlayerTrainTestCase() {
		super(IPUtil.testServerIP, 10000000567L);
//		super(IPUtil.getTestServerIp(), 2583690);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.TrainPlayerManager_UpLv), 9000125);
	}
	
}
