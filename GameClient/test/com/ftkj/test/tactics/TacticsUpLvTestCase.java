package com.ftkj.test.tactics;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.console.GameConsole;
import com.ftkj.proto.TacticsPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class TacticsUpLvTestCase extends BaseTestCase{
	
	public TacticsUpLvTestCase() {
		super(IPUtil.getLocalIp(), 9999999);
//		super(IPUtil.getTestServerIp(), 9999999);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Tactics_Up), 11);
	}

}
