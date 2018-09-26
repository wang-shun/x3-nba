package com.ftkj.test.atv;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.proto.AtvStarLoadPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class StarLoadTestCase extends BaseTestCase{
	
	public StarLoadTestCase() {
		super(IPUtil.testServerIP, 10000003078L);
	}
	
	@Test
	public void showview() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.AtvStarLoadManager_showView, AtvStarLoadPB.StarLoadData.class))
		.send(EAtv.巨星之路_球队等级.getAtvId());
	}
	@Test
	public void getAward() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.AtvStarLoadManager_getAward))
		.send(11,2);
	}
	@Test
	public void getRankAward() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.AtvStarLoadManager_getRankAward))
		.send(11);
	}

}
