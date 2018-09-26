//package com.ftkj.action;
//
//import com.ftkj.client.ActionConsole;
//import com.ftkj.client.BaseAction;
//import com.ftkj.client.ClientResponse;
//import com.ftkj.proto.GameLoadPB;
//import com.ftkj.robot.XGameRobot;
//import com.ftkj.server.ServiceCode;
//
///**
// * @author tim.huang
// * 2016年8月29日
// * 加载玩家数据
// */
//public class LoadPlayerAction extends BaseAction<XGameRobot> {
//	
//	public LoadPlayerAction() {
//		super(BaseAction.次要行为, ServiceCode.GameManager_loadPlayerGameSource);
//	}
//
//	@Override
//	public void callback(ClientResponse response, XGameRobot robot)
//			throws Exception {
////		GameLoadPB.TeamLoadData loadData = GameLoadPB.TeamLoadData.parseFrom(response.getData());
////		robot.appendHeros(loadData.getTeam().getHerosList());
//		ActionConsole.getAction(ServiceCode.ArenaManager_showPVPMain).run(robot);
//		XGameRobot.log.info("第{}名玩家数据创建完毕:[{}]",robot.getNum(),robot.getTeamId());
//	}
//	
//	@Override
//	public void run(XGameRobot robot) {
//		robot.actionJob(get());
//	}
//
//	@Override
//	public void init() {
//		
//	}
//	
//	
//
//}
