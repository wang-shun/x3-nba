package com.ftkj.test.atv;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.proto.AtvCardAwardPB.AtvCardAwardData;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class AtvCardTestCase extends BaseTestCase{
	
	public AtvCardTestCase() {
		super(IPUtil.testServerIP, 10000000715L);
//		super(IPUtil.localIP, 10000001134L);
	}
	
	@Test
	public void showview() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.AtvCardAwardManager_showView, AtvCardAwardData.class))
		.send();
	}

	@Test
	public void getCardAward() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.AtvCardAwardManager_getAward))
		.send(EAtv.装备强化周卡.getAtvId());
	}
	
}
