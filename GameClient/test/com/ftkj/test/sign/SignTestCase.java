package com.ftkj.test.sign;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.SignPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class SignTestCase extends BaseTestCase{
	
	// 连接服务器
	public SignTestCase() {
//		super("192.168.10.181", 9999999);
		super(IPUtil.testServerIP, 10000001764L);
	}
	
	@Test
	public void signData() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.SignManager_getTeamSignData, SignPB.SignMainData.class));
	}
	
	@Test
	public void signMonth() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.SignManager_signMonth));
	}
	
	@Test
	public void signMonthPatch() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.SignManager_signMonthPatch));
	}

	@Test
	public void signPeriod() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.SignManager_signPeriod), 0);
	}
}
