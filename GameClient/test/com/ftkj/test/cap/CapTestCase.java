package com.ftkj.test.cap;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class CapTestCase extends BaseTestCase{
	
	public CapTestCase() {
		super(IPUtil.localIP, 1000003383L);
	}
	
	/**
	 * 测试攻防
	 */
	@Test
	public void testCap() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.GMManager_Cap_Test), 1011000003758L, 3);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
