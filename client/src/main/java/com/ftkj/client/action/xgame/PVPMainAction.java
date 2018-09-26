package com.ftkj.client.action.xgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.xgame.XGameRobot;
import com.ftkj.server.ServiceCode;

public class PVPMainAction extends BaseAction<XGameRobot> {

	
	
	public PVPMainAction() {
		super(BaseAction.Main_Action, ServiceCode.ArenaManager_showArenaMain);
//		super(BaseAction.随机行为, 1);
	}

	@Override
	public void run(XGameRobot robot,Object...val) {
		robot.actionJob(get());
	}

	@Override
	public void callback(ClientResponse response, XGameRobot robot)
			throws Exception {
		System.err.println("完成竞技场创建");
	}

	@Override
	public void init() {
		
	}
	
}
