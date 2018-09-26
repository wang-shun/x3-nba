package com.ftkj.client.action.xgame;

import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.client.robot.xgame.XGameRobot;
import com.ftkj.server.ServiceCode;

/**
 * @author tim.huang
 * 2016年8月25日
 * 登录角色
 */
public class SendAllMailAction extends BaseAction<XGameRobot> {

	public SendAllMailAction() {
		super(BaseAction.Main_Action,ServiceCode.GMManager_sendEmail);
//		super(BaseAction.主体行为,1);            
	}

	@Override
	public void callback(ClientResponse response,XGameRobot robot)throws Exception {
	}

	@Override
	public void run(XGameRobot robot,Object...val) {
		robot.actionJob(get(), val);
	}

	@Override
	public void init() {
	}
	

}
