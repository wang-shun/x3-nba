package com.ftkj.client.action.zgame;

import com.ftkj.client.ActionConsole;
import com.ftkj.client.BaseAction;
import com.ftkj.client.ClientResponse;
import com.ftkj.proto.DefaultPB;
import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class ZCreateAction extends BaseAction<ZGameRobot> {
	private static List<String> names;
	
	public ZCreateAction() {
		super(BaseAction.Main_Action, ServiceCode.GameManager_createTeam);
	}

	@Override
	public void run(ZGameRobot robot,Object...val){
		robot.actionJob(get(), val);
	}

	@Override
	public void callback(ClientResponse response, ZGameRobot robot)
			throws Exception {
//		System.err.println("登录成功->已创建角色");
		DefaultPB.DefaultData data = DefaultPB.DefaultData.parseFrom(response.getData());
		if(data.getCode()==0)
			ActionConsole.getAction(ServiceCode.GameManager_loadData).run(robot);
		else{
			ActionConsole.getAction(ServiceCode.GameManager_createTeam)
					.run(robot,"压测机器人"+(data.getBigNum() % 100000000) + RandomUtil.randInt(900000),"1",ZGameRobot.getRanXPlayer(),RandomUtil.randInt(10),1);
		}

	}

	@Override
	public void init() {
		names = new ArrayList<>();
		names.add("呆萌吕布");
		names.add("呆萌张飞");
		names.add("呆萌关羽");
		names.add("呆萌赵云");
		names.add("书呆子");
		names.add("炊事班Boss");
		names.add("搞基领导");
		names.add("某云");
		names.add("智勇双全的瘪三");
		names.add("瘪三也很牛");
		names.add("改名字真难");
		names.add("你猜我猜不猜");
		names.add("神奇宝贝");
		names.add("妇女质检员");
		names.add("穷剩钱");
		names.add("老穷鬼");
		names.add("我是大土壕");
		names.add("除了高没别的");
		names.add("浮云浮云");
		names.add("果园收割者");
	}
	 

}
