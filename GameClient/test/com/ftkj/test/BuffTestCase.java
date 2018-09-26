package com.ftkj.test;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.BuffPB;
import com.ftkj.proto.FriendPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class BuffTestCase extends BaseTestCase{
	
	// 连接服务器
	public BuffTestCase() {
		super(IPUtil.testServerIP, 1000002027L);
//		super(IPUtil.localIP, 1000003230L);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.BuffManager_buff_list, BuffPB.TeamBuffList.class));
	}

}
