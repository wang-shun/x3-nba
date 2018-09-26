package com.ftkj.test;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.FriendPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class FindFriendsTestCase extends BaseTestCase{
	
	// 连接服务器
	public FindFriendsTestCase() {
//		super("192.168.10.181", 9999999);
		super(IPUtil.testServerIP, 10000000717L);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.Friend_recommend, FriendPB.FindFriendData.class));
	}

}
