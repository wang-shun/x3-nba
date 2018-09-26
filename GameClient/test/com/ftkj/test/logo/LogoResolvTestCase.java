package com.ftkj.test.logo;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class LogoResolvTestCase extends BaseTestCase{
	
	public LogoResolvTestCase() {
		super(IPUtil.getLocalIp(), 999999);
//		super(IPUtil.getTestServerIp(), 999999);
	}
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Player_Logo_Resolve), 2, 3, 9, -1, -1);
	}

}
