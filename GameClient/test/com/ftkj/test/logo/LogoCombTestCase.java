package com.ftkj.test.logo;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.PlayerLogoPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class LogoCombTestCase extends BaseTestCase{
	
	public LogoCombTestCase() {
		super(IPUtil.getTestServerIp(), 2583690);
//		super(IPUtil.getLocalIp(), 2583690);
	}
	@Test
	public void test() {
		this.robot
			.actionJob(DefaultAction.instanceService(ServiceCode.Player_Logo_Comb), 9000160)
			.actionJob(DefaultAction.instanceService(ServiceCode.Player_Logo_List, PlayerLogoPB.PlayerLogoData.class));
	}

}
