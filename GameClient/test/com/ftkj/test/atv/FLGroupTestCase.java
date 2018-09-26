package com.ftkj.test.atv;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.AtvCommonPB.AtvCommonData;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class FLGroupTestCase extends BaseTestCase{
	
	public FLGroupTestCase() {
		super(IPUtil.testServerIP, 10000000734L);
	}
	
	@Test
	public void showview() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.FLGroupUpFundManager_showView, AtvCommonData.class))
		.send();
	}

}
