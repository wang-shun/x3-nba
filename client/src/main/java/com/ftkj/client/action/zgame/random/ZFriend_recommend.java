package com.ftkj.client.action.zgame.random;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZFriend_recommend extends BaseAction<ZGameRobot> {
	
	public ZFriend_recommend() {
		super(BaseAction.Random_Action, ServiceCode.Friend_recommend);
	}

	@Override
	public void run(ZGameRobot robot, Object... val) {
		robot.actionJob(get(),val);
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
		randomAction().run(robot);
	}

	@Override
	public void init() {
		
	}

}
