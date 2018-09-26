package com.ftkj.test.logo;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.PlayerLogoPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class LogoListTestCase extends BaseTestCase{
	
	public LogoListTestCase() {
//		super(IPUtil.getLocalIp(), 500015);
		super(IPUtil.getTestServerIp(), 500015);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Player_Logo_List, PlayerLogoPB.PlayerLogoData.class));
	}

}
