package com.ftkj.test.stage;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.TeamStagePB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class StageShowTestCase extends BaseTestCase{
	
	// 连接服务器
	public StageShowTestCase() {
//		super(IPUtil.testServerIP, 10000002107L);
		super(IPUtil.localIP, 10000002107L);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Main_Stage_show, TeamStagePB.TeamStageData.class));
	}

}
