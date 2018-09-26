package com.ftkj.client.action.xgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class DemoAction extends BaseAction<ZGameRobot> {

	public DemoAction() {
		super(BaseAction.Main_Action, ServiceCode.DemoManager_dbTest);
	}

	@Override
	public void run(ZGameRobot robot, Object... val) {
		robot.actionJob(get());
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
		
	}

	@Override
	public void init() {
		
	}

	
}
