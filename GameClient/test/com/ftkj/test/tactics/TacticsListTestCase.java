package com.ftkj.test.tactics;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.console.GameConsole;
import com.ftkj.proto.TacticsPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class TacticsListTestCase extends BaseTestCase{
	
	public TacticsListTestCase() {
//		super(IPUtil.getLocalIp(), 999999);
		super(IPUtil.getTestServerIp(), 431);
		//10100000000431
		//10110000000431
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Tactics_List, TacticsPB.TeamTacticsData.class));
	}

}
