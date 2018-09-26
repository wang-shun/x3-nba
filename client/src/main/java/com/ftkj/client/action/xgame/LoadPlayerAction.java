package com.ftkj.client.action.xgame;

import com.ftkj.client.ActionConsole;
import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.xgame.XGameRobot;
import com.ftkj.server.ServiceCode;

/**
 * @author tim.huang
 * 2016年8月29日
 * 加载玩家数据
 */
public class LoadPlayerAction extends BaseAction<XGameRobot> {
	
	public LoadPlayerAction() {
		super(BaseAction.Main_Action, ServiceCode.GameManager_loadData);
//		super(BaseAction.次要行为, 1);
	}

	@Override
	public void callback(ClientResponse response, XGameRobot robot)
			throws Exception {
//		GameLoadPB.TeamLoadData loadData = GameLoadPB.TeamLoadData.parseFrom(response.getData());
//		robot.appendHeros(loadData.getTeam().getHerosList());
		ActionConsole.getAction(ServiceCode.ArenaManager_showArenaMain).run(robot);
		XGameRobot.log.info("第{}名玩家数据创建完毕:[{}]",robot.getNum(),robot.getTeamId());
	}
	
	@Override
	public void run(XGameRobot robot,Object...val) {
		robot.actionJob(get());
	}

	@Override
	public void init() {
		
	}
	
	

}
