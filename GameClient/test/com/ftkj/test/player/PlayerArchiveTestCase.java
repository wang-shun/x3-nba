package com.ftkj.test.player;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class PlayerArchiveTestCase extends BaseTestCase{
	
	public PlayerArchiveTestCase() {
		super(IPUtil.localIP, 1000003301L);
//		super(IPUtil.getTestServerIp(), 2583690);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.PlayerArchiveManager_showPlayerList));
	}
	
	@Test
	public void tran() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.PlayerArchiveManager_transPlayer), 9000300, 3187, "0,0,0,0,1");
	}
	
}
 