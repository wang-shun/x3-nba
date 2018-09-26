package com.ftkj.client.action.zgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZDraftJoinAction extends BaseAction<ZGameRobot> {

	public ZDraftJoinAction() {
//		super(BaseAction.主体行为, ServiceCode.GameManager_loadData);
		super(BaseAction.Main_Action, ServiceCode.LocalDraftManager_joinDraft);
	}

	@Override
	public void run(ZGameRobot robot,Object...val) {
		robot.actionJob(get(),val);
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
		System.err.println("进入房间成功拉");
//		GameLoadPB.GameLoadDataMain data = GameLoadPB.GameLoadDataMain.parseFrom(response.getData());
//		System.err.println("数据load成功，获得玩家["+data.getTeamData().getTeamName()+"]的数据.");
//		ActionConsole.getAction(ServiceCode.ShopManager_showLeagueShop).run(robot);
	}

	@Override
	public void init() {
		
	}
	
	

}
