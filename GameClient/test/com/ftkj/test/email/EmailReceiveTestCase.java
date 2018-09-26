package com.ftkj.test.email;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.EmailPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;

public class EmailReceiveTestCase extends BaseTestCase{
	
	@Test
	public void test() {
		this.robot
			.actionJob(DefaultAction.instanceService(ServiceCode.Email_List, EmailPB.TeamEmailData.class))
			.sleep(500)
			.actionJob(DefaultAction.instanceService(ServiceCode.Email_Receive), 0)
			.sleep(500)
			.actionJob(DefaultAction.instanceService(ServiceCode.Email_List, EmailPB.TeamEmailData.class))
			;
	}
	
}
