package com.ftkj.test.besign;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.TeamBeSignPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class BeSignListTestCase extends BaseTestCase{
	
	public BeSignListTestCase() {
		super(IPUtil.localIP, 1000026482L);
//		super(IPUtil.getTestServerIp(), 999999);
	}
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.BeSignManager_List, TeamBeSignPB.TeamBeSignData.class));
	}
	
//	@Test
//	public void testAdd() {
//		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.GMManager_Besign_Player_Add), );
//	}
	
	@Test
	public void recycleAll() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.BeSignManager_recycleAll));
	}

}
