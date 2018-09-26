package com.ftkj.client.action.zgame.random;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZMain_Stage_show extends BaseAction<ZGameRobot> {
	
	
	public ZMain_Stage_show() {
		super(BaseAction.Random_Action, ServiceCode.Main_Stage_show);
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
