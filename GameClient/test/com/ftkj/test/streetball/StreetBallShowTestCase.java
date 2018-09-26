package com.ftkj.test.streetball;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.StreetBallPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class StreetBallShowTestCase extends BaseTestCase{
	
	// 连接服务器
	public StreetBallShowTestCase() {
//		super("192.168.10.181", 9999999);
		super(IPUtil.localIP, 10000000432L);
	}
	
	@Test
	public void showView() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.StreetBallManager_showView, StreetBallPB.StreetBallData.class));
	}
	
	@Test
	public void challenge() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.StreetBallManager_challenge), 3001);
	}
	
	@Test
	public void sweep() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.StreetBallManager_sweep), 1001);
	}

}
