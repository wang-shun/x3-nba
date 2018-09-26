package com.ftkj;

import com.ftkj.action.DefaultAction;
import com.ftkj.proto.GameLoadPB;
import com.ftkj.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;

public class ClientStartup {

	public static void main(String[] args)throws Exception {
		// 都是通过Action来处理
//		ZGameRobot robot = new ZGameRobot("192.168.10.181", 8038, 1000003); // 测试
		ZGameRobot robot = new ZGameRobot("192.168.12.78", 8038, 1000103); //本地
		//
//		robot.actionJob(DefaultAction.instanceService(ServiceCode.GameManager_debugLogin),  123456);
		// 创建球队
		//robot.actionJob(DefaultAction.instanceService(ServiceCode.GameManager_createTeam),  "test002", "cool", 9000101, 1, 1);
		// 加载数据
		robot.actionLogin().actionJob(DefaultAction.instanceService(ServiceCode.GameManager_loadData, GameLoadPB.GameLoadDataMain.class));
//		
//		robot.actionLogin().actionJob(DefaultAction.instanceService(ServiceCode.Prop_Add, TeamPropsData.class), 1009, 10);
//		robot.actionLogin().actionJob(DefaultAction.instanceService(ServiceCode.Prop_Sale, TeamPropsData.class), 1038, 1);
//		robot.actionLogin().actionJob(DefaultAction.instanceService(ServiceCode.ScoutManager_refreshScout, ScoutPB.ScoutMain.class));
		
		//
		// 主线赛程
//		robot.actionLogin().actionJob(DefaultAction.instanceService(ServiceCode.Main_Stage_show, TeamStageData.class));
//		robot.actionLogin().actionJob(DefaultAction.instanceService(ServiceCode.Main_Stage_Next_Scene));
//		robot.actionLogin().actionJob(DefaultAction.instanceService(ServiceCode.Main_Stage_Fight), 1);
		
	}

}
