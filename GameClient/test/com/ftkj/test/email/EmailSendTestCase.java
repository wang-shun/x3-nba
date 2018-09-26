package com.ftkj.test.email;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.EmailPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;

public class EmailSendTestCase extends BaseTestCase{
	
	@Test
	public void test() {
		this.robot
			.actionJob(DefaultAction.instanceService(ServiceCode.Email_Send, EmailPB.EmailData.class), robot.getTeamId(), 0, "测试邮件", "注册奖励1", "1033:10")
			.sleep(500)
			.actionJob(DefaultAction.instanceService(ServiceCode.Email_List, EmailPB.TeamEmailData.class));
	}
	
}
