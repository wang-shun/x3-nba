package com.ftkj.client.action.zgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.proto.GameLoadPB;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;
import com.ftkj.client.zgame.ZGameGloab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZLoadDataAction extends BaseAction<ZGameRobot> {
	private static final Logger log = LoggerFactory.getLogger(ZLoadDataAction.class);
	public ZLoadDataAction() {
		super(BaseAction.Main_Action, ServiceCode.GameManager_loadData);
	}

	@Override
	public void run(ZGameRobot robot,Object...val) {
		robot.actionJob(get());
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
		GameLoadPB.GameLoadDataMain data = GameLoadPB.GameLoadDataMain.parseFrom(response.getData());
		log.info("数据load成功，获得玩家[{}]的数据.", data.getTeamData().getTeamName());
		ZGameGloab.loadDataMap.put(robot.getTeamId(), data);
		//随机执行一个无需参数的事件
		randomAction().run(robot);
//		ActionConsole.getAction(ServiceCode.LocalDraftManager_joinDraft).run(robot,3);
//		ActionConsole.getAction(ServiceCode.GameManager_debugLoginTeam).run(robot,10110000000457l);
//		ActionConsole.getAction(ServiceCode.Main_Stage_Fight).run(robot,1);
//		ActionConsole.getAction(ServiceCode.BattlePVPManager_match).run(robot);
//		ActionConsole.getAction(ServiceCode.TaskManager_showTaskMain).run(robot);
//		ActionConsole.getAction(ServiceCode.ShopManager_buyMoneyProp).run(robot,1001,1);
	}

	@Override
	public void init() {
	}
	
	

}
