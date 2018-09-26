package com.ftkj.client.action.zgame.random;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZTactics_List extends BaseAction<ZGameRobot> {
	
	
	public ZTactics_List() {
		super(BaseAction.Random_Action, ServiceCode.Tactics_List);
	}

	@Override
	public void run(ZGameRobot robot, Object... val) {
		robot.actionJob(get(), val);
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
