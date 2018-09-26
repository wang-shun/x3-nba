package com.ftkj.test.atv;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.SystemActivePB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class SystemActiveTestBase extends BaseTestCase {
	
	public SystemActiveTestBase() {
		super(IPUtil.localIP, 1000001355L);
	}
	
	@Test
	public void getAllActive() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.SystemActiveManager_getAllActive, SystemActivePB.SystemActiveData.class))
		.send();
	}
	

	
}
