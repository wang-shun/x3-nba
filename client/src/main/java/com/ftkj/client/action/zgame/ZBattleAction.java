package com.ftkj.client.action.zgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZBattleAction extends BaseAction<ZGameRobot> {

	
	
	public ZBattleAction() {
//		super(BaseAction.主体行为, ServiceCode.GameManager_debugStartBattle);
		super(BaseAction.Main_Action, ServiceCode.BattlePVPManager_match);
	}

	@Override
	public void run(ZGameRobot robot, Object... val) {
		robot.actionJob(get(), val);
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
		
	}

	@Override
	public void init() {
	}

}
