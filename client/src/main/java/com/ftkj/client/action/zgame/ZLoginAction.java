package com.ftkj.client.action.zgame;

import com.ftkj.client.ActionConsole;
import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.console.GameConsole;
import com.ftkj.proto.DefaultPB;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.RandomUtil;

public class ZLoginAction extends BaseAction<ZGameRobot> {

	public ZLoginAction() {
		super(BaseAction.Main_Action, ServiceCode.GameManager_debugLoginTeam);
	}

	@Override
	public void run(ZGameRobot robot,Object...val) {
		robot.actionJob(get(), robot.getTeamId());
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
//		DefaultPB.DefaultData data = DefaultPB.DefaultData.parseFrom(response.getData());
//		System.err.println("登录成功->");
		DefaultPB.DefaultData data = DefaultPB.DefaultData.parseFrom(response.getData());
		if(data.getCode()==GameConsole.Game_Load_Code_Success){//已创建角色
//			System.err.println("登录成功->已创建角色");
			ActionConsole.getAction(ServiceCode.GameManager_loadData).run(robot);
//			robot.setTeamId(data.getBigNum());
		}else{
			ActionConsole.getAction(ServiceCode.GameManager_createTeam)
				.run(robot,"压测机器人"+(data.getBigNum() % 100000000) + RandomUtil.randInt(900000),"1",ZGameRobot.getRanXPlayer(),RandomUtil.randInt(10),1);
		}
	}

	@Override
	public void init() {
		
	}
	
	

}
