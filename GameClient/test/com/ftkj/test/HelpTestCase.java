package com.ftkj.test;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class HelpTestCase extends BaseTestCase{
	
	public HelpTestCase() {
		super(IPUtil.localIP, 1000026292L);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.LocalDraftManager_showHelpPlayers));
	}

}
