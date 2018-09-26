package com.ftkj.client.robot;

import com.ftkj.client.ActionConsole;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;

import java.util.List;

public class ZGameRobot extends BaseRobot {

	private static List<Integer> XPlayerList = Lists.newArrayList();
	
	static {
		XPlayerList.add(9000209);
		XPlayerList.add(9000160);
		XPlayerList.add(9000134);
		XPlayerList.add(9000101);
		XPlayerList.add(9000121);
		XPlayerList.add(9000125);
		XPlayerList.add(9000201);
		XPlayerList.add(9000234);
		XPlayerList.add(9000147);
		XPlayerList.add(9000148);
		XPlayerList.add(9000181);
		XPlayerList.add(9000281);
		XPlayerList.add(9000117);
		XPlayerList.add(9000122);
		XPlayerList.add(9000154);
		XPlayerList.add(9000258);
		XPlayerList.add(9000106);
		XPlayerList.add(9000138);
		XPlayerList.add(9000165);
		XPlayerList.add(9000266);
	}
	
	
	public ZGameRobot(long teamId) {
		super(teamId);
	}
	
	public static int getRanXPlayer(){
		return XPlayerList.get(RandomUtil.randInt(XPlayerList.size()));
	}

	@Override
	protected void init() {
		setAction(ActionConsole.getAction(ServiceCode.GameManager_debugLoginTeam));
	}



}
