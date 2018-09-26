package com.ftkj.client.action.xgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.xgame.XGameRobot;
import com.ftkj.server.ServiceCode;

public class SignAction extends BaseAction<XGameRobot> {

	public SignAction() {
		super(BaseAction.Main_Action,ServiceCode.SignManager_signPeriod);
//		super(BaseAction.随机行为,1);
	}

	@Override
	public void callback(ClientResponse response, XGameRobot robot)
			throws Exception {
		
		
	}

	@Override
	public void run(XGameRobot robot,Object...val) {
		
	}

	@Override
	public void init() {
		
	}

	
}
