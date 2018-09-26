package com.ftkj.client.action.zgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZStageManagerPK extends BaseAction<ZGameRobot>{

	public ZStageManagerPK() {
		super(BaseAction.Main_Action, ServiceCode.Main_Stage_Fight);
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
		
		
		
	}

	@Override
	public void init() {
		
	}

}
