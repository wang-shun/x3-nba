package com.ftkj.test.logo;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class LogoFourUpQuaTestCase extends BaseTestCase{
	
	public LogoFourUpQuaTestCase() {
//		super(IPUtil.getLocalIp(), 999999);
		super(IPUtil.getLocalIp(), 10000000486L);
	}
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Player_Logo_Tran), 6583, 6585);
	}

}
