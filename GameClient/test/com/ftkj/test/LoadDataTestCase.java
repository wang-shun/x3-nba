package com.ftkj.test;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.GameLoadPB;
import com.ftkj.proto.TargetPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class LoadDataTestCase extends BaseTestCase{
	
	// 连接服务器
	public LoadDataTestCase() {
//		super("192.168.10.181", 9999999);
		super(IPUtil.localIP, 1000003921L);
	}
	
	@Test
	public void test() {
		this.robot.actionJob(DefaultAction.instanceService(ServiceCode.GameManager_loadData, GameLoadPB.GameLoadDataMain.class));
	}
	
	@Test
	public void test2() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.GameManager_topicTarget, TargetPB.TargetMainData.class))
		.send(1011000026252L);
	}

}
