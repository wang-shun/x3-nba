package com.ftkj.test.stage;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class StagePKTestCase extends BaseTestCase{
	
	// 连接服务器
	public StagePKTestCase() {
//		super("192.168.10.181", 9999999);
		super(IPUtil.localIP, 10000000432L);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Main_Stage_Fight), 82);
	}

}
