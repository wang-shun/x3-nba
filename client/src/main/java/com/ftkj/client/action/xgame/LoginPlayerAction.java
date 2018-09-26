package com.ftkj.client.action.xgame;

import com.ftkj.client.ActionConsole;
import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.BaseRobot;
import com.ftkj.proto.DefaultPB;
import com.ftkj.client.robot.xgame.XGameRobot;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.MD5Util;

/**
 * @author tim.huang
 * 2016年8月25日
 * 登录角色
 */
public class LoginPlayerAction extends BaseAction<XGameRobot> {

	public LoginPlayerAction() {
		super(BaseAction.Main_Action,ServiceCode.GameManager_loginPC);
//		super(BaseAction.主体行为,1);            
	}

	@Override
	public void callback(ClientResponse response,XGameRobot robot)throws Exception {
		DefaultPB.DefaultData data = DefaultPB.DefaultData.parseFrom(response.getData());
		if(data.getCode()==0){//登录成功，加载数据
			ActionConsole.getAction(ServiceCode.GameManager_loadData).run(robot);
		}else if(data.getCode()==2){
			ActionConsole.getAction(ServiceCode.GameManager_createTeam).run(robot);
		}else{
			BaseRobot.log.error("登录有误[{}]",robot.getTeamId());
		}
	}

	@Override
	public void run(XGameRobot robot,Object...val) {
		long time = System.currentTimeMillis();
		robot.actionJob(get(), robot.getTeamId(),MD5Util.encodeMD5(robot.getTeamId(), time),time);
	}

	@Override
	public void init() {
	}
	

}
