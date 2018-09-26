package com.ftkj.client.action.zgame.random;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZChatManager_chatWorld extends BaseAction<ZGameRobot> {
	
	
	public ZChatManager_chatWorld() {
		super(BaseAction.Main_Action, ServiceCode.ChatManager_chatWorld);
	}

	@Override
	public void run(ZGameRobot robot, Object... val) {
		robot.actionJob(get(), "大家好，我是"+robot.getTeamId());
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
