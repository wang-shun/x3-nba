package com.ftkj.test.player;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.ScoutPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class PlayerExchangeTestCase extends BaseTestCase{
	
	public PlayerExchangeTestCase() {
		super(IPUtil.testServerIP, 1000003301L);
//		super(IPUtil.getTestServerIp(), 2583690);
	}
	
	@Test
	public void showview() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.ExchangePlayerManager_showView, ScoutPB.ExchangePlayerMain.class));
	}
	
	@Test
	public void signPlayer() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.ExchangePlayerManager_signPlayer), 0, 3138156);
	}
	
}
