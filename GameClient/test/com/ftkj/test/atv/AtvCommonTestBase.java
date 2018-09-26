package com.ftkj.test.atv;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class AtvCommonTestBase extends BaseTestCase {
	
	//1011000001316
	public AtvCommonTestBase() {
		super(IPUtil.testServerIP, 1000026482L);
	}
	
	@Test
	public void showview() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.SystemActiveManager_ShowView, AtvCommonPB.AtvCommonData.class))
		// 活动ID参数
		.send(45);
	}
	
	@Test
	public void getAward() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.SystemActiveManager_GetAward, AtvCommonPB.AtvAwardData.class))
		// 活动ID参数，奖励ID
		.send(36, 1);
	}
	
	@Test
	public void getDayAward() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.SystemActiveManager_GetDayAward, AtvCommonPB.AtvAwardData.class))
		// 活动ID参数
		.send(37);
	}
	
	@Test
	public void buyFinish() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.SystemActiveManager_BuyFinish, AtvCommonPB.AtvAwardData.class))
		// 活动ID参数，奖励ID
		.send(36, 2);
	}

	
}
