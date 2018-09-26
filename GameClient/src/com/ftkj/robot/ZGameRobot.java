package com.ftkj.robot;

import com.ftkj.action.DefaultAction;
import com.ftkj.client.robot.BaseRobot;
import com.ftkj.server.ServiceCode;

public class ZGameRobot extends BaseRobot {
	
	/**
	 * 用玩家ID初始化
	 * @param teamId
	 */
	public ZGameRobot(long teamId) {
		super(teamId);
	}
	/**
	 * 连接初始化
	 * @param ip
	 * @param port
	 * @param teamId
	 */
	public ZGameRobot(String ip, int port, long userId) {
		super(userId);
		this.conn(ip, port);
	}
	
	@Override
	protected void init() {
		super.init();
	}
	
	/**
	 * 执行登录
	 * @return
	 */
	public BaseRobot actionLogin() {
		return this.actionJob(DefaultAction.instanceService(ServiceCode.GameManager_debugLogin),  getUserId()).sleep(500);
	}
	
}
