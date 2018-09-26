package com.ftkj.test.atv;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.RedPointPB;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class RedPointTestCase extends BaseTestCase{
	
	public RedPointTestCase() {
		super(IPUtil.localIP, 1000003433L);
//		super(IPUtil.localIP, 10000001134L);
	}
	
	@Test
	public void showview() {
		this.robot.action(DefaultAction.instanceService(ServiceCode.RedPointManager_login, RedPointPB.RedPointData.class))
		.send(1011000003434L);
	}

	
}
