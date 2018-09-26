//package com.ftkj.action;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import com.ftkj.client.ActionConsole;
//import com.ftkj.client.BaseAction;
//import com.ftkj.client.ClientResponse;
//import com.ftkj.client.robot.BaseRobot;
//import com.ftkj.proto.DefaultPB;
//import com.ftkj.robot.XGameRobot;
//import com.ftkj.server.ServiceCode;
//import com.ftkj.util.RandomUtil;
//
///**
// * @author tim.huang
// * 2016年8月27日
// * 创建角色
// */
//public class CreatePlayerAction extends BaseAction<XGameRobot> {
//
//	private static List<String> names;
//	private static int index =0 ;
//	
//	public CreatePlayerAction() {
//		super(BaseAction.次要行为, ServiceCode.GameManager_createTeam);
//	}
//
//	@Override
//	public void callback(ClientResponse response, XGameRobot robot)
//			throws Exception {
//		DefaultPB.DefaultData data = DefaultPB.DefaultData.parseFrom(response.getData());
//		if(data.getCode() == 0){
//			ActionConsole.getAction(ServiceCode.GameManager_loadPlayerGameSource).run(robot);
//		}else{
//			BaseRobot.log.error("创建角色有误[{}]",robot.getTeamId());
//		}
//	}
//	@Override
//	public void run(XGameRobot robot) {
////		robot.actionJob(get(), "机器人"+RandomUtil.ran(Integer.MAX_VALUE),RandomUtil.ran(1,2));
//		robot.actionJob(get(), names.get((index++)%names.size())+"_"+RandomUtil.ran(100000),RandomUtil.ran(1,2));
//	}
//
//	@Override
//	public void init() {
//		names = new ArrayList<>();
//		names.add("呆萌吕布");
//		names.add("呆萌张飞");
//		names.add("呆萌关羽");
//		names.add("呆萌赵云");
//		names.add("书呆子");
//		names.add("炊事班Boss");
//		names.add("搞基领导");
//		names.add("某云");
//		names.add("智勇双全的瘪三");
//		names.add("瘪三也很牛");
//		names.add("改名字真难");
//		names.add("你猜我猜不猜");
//		names.add("神奇宝贝");
//		names.add("妇女质检员");
//		names.add("穷剩钱");
//		names.add("老穷鬼");
//		names.add("我是大土壕");
//		names.add("除了高没别的");
//		names.add("浮云浮云");
//		names.add("果园收割者");
//	}
//
//}
