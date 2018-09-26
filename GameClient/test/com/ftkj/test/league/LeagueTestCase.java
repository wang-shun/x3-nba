package com.ftkj.test.league;

import org.junit.Test;

import com.ftkj.action.DefaultAction;
import com.ftkj.server.ServiceCode;
import com.ftkj.test.base.BaseTestCase;
import com.ftkj.util.IPUtil;

public class LeagueTestCase extends BaseTestCase{
	
	// 连接服务器
	public LeagueTestCase() {
//		super(IPUtil.localIP, 1000003230L);
		super(IPUtil.localIP, 1000000012L);
	}
	
	/**
	 * 联盟申请
	 */
	@Test
	public void aceeptJoinLeague() {
		this.robot
		.action(DefaultAction.instanceService(ServiceCode.LeagueManager_aceeptJoinLeague))
		.send(1010002);
	}
}
