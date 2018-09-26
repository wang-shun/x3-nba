package com.ftkj.test.email;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.EmailPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class EmailListTestCase extends BaseTestCase{
	
	public EmailListTestCase() {
		super(IPUtil.testServerIP, 1000003605L);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Email_List, EmailPB.TeamEmailData.class));
	}

}
