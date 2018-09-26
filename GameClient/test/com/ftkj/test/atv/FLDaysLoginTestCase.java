package com.ftkj.test.atv;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.GameLoadPB;
import com.ftkj.proto.AtvCommonPB.AtvCommonData;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class FLDaysLoginTestCase extends BaseTestCase{
	
	public FLDaysLoginTestCase() {
		super(IPUtil.testServerIP, 10000000803L);
//		super(IPUtil.localIP, 10000000741L);
	}
	
	@Test
	public void showview() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.FLDaysLoginManager_showView, AtvCommonData.class))
		.send();
	}
	
	@Test
	public void login() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.GameManager_loadData, GameLoadPB.GameLoadDataMain.class));
	}
	
	@Test
	public void getAward() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.FLDaysLoginManager_getAward), 1);
	}

}
