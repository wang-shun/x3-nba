package com.ftkj.test;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.ShopPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class ConvertPropTestCase extends BaseTestCase{
	
	public ConvertPropTestCase() {
		super(IPUtil.localIP, 1000001322L);
//		super(IPUtil.localIP, 10000001134L);
	}
	
	@Test
	public void showview() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.PropManager_showConvertMain, ShopPB.ConvertMainData.class))
		.send(5);
	}

	@Test
	public void tranProp() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.PropManager_convertProp, ShopPB.ConvertResultData.class))
		.send(1106,1);
	}
}
