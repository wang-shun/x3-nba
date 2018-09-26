package com.ftkj.client.action.zgame.random;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZShowLeagueShopAction extends BaseAction<ZGameRobot> {

	public ZShowLeagueShopAction() {
//		super(BaseAction.主体行为, ServiceCode.GameManager_loadData);
		super(BaseAction.Random_Action, ServiceCode.ShopManager_showLeagueShop);
	}

	@Override
	public void run(ZGameRobot robot,Object...val) {
		robot.actionJob(get());
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
		randomAction().run(robot);
//		GameLoadPB.GameLoadDataMain data = GameLoadPB.GameLoadDataMain.parseFrom(response.getData());
//		System.err.println("数据load成功，获得玩家["+data.getTeamData().getTeamName()+"]的数据.");
//		ActionConsole.getAction(ServiceCode.ShopManager_buyLeagueProp).run(robot,1001,1);
	}

	@Override
	public void init() {
		
	}
	
	

}
