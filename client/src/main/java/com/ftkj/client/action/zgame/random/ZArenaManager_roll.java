package com.ftkj.client.action.zgame.random;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZArenaManager_roll extends BaseAction<ZGameRobot> {
	
	public ZArenaManager_roll() {
		super(BaseAction.Main_Action, ServiceCode.ArenaManager_roll);
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
