package com.ftkj.test.player;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.MatchPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class PlayerStorageTestCase extends BaseTestCase{
	
	public PlayerStorageTestCase() {
		super(IPUtil.localIP, 10000000440L);
//		super(IPUtil.getTestServerIp(), 2583690);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Player_Tran_Store), -1, 3);
	}

}
