package com.ftkj.client.action.zgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.proto.GameLogPB;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ZShowPlayerSourceAction extends BaseAction<ZGameRobot> {

	public ZShowPlayerSourceAction() {
//		super(BaseAction.主体行为, ServiceCode.GameManager_loadData);
		super(BaseAction.Main_Action, ServiceCode.Battle_PK_showPlayerSource);
	}

	@Override
	public void run(ZGameRobot robot,Object...val) {
		robot.actionJob(get(),val);
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
		GameLogPB.BattleEndLogData logData = GameLogPB.BattleEndLogData.parseFrom(response.getData());
		System.err.println(logData);
//		GameLoadPB.GameLoadDataMain data = GameLoadPB.GameLoadDataMain.parseFrom(response.getData());
//		System.err.println("数据load成功，获得玩家["+data.getTeamData().getTeamName()+"]的数据.");
//		ActionConsole.getAction(ServiceCode.ShopManager_showLeagueShop).run(robot);
	}

	@Override
	public void init() {
		
	}
	
	

}
