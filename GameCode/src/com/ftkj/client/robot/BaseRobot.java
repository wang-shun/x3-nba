package com.ftkj.client.robot;

import com.ftkj.client.BaseAction;
import com.ftkj.client.BaseClient;
import com.ftkj.client.ClientData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2016年3月21日
 * 机器人父类
 */
public abstract class BaseRobot {
	public static final Logger log = LoggerFactory.getLogger(BaseRobot.class);
	/**
	 * 连接端
	 */
	private BaseClient client;
	private BaseAction<BaseRobot> action;
	
	private long userId;
	
	private long teamId;
	
	public BaseRobot(long userId) {
		this.userId = userId;
		this.teamId = userId;
		this.client = new BaseClient();
		init();
	}
	
	public void conn(){
		try {
			this.client.conn(this);
			log.info("玩家[{}]已经连接到服务器.",userId);
		} catch (Exception e) {
		}
	}
	public void conn(String ip,int port){
		try {
			this.client.conn(this,ip,port);
			log.info("玩家[{}]已经连接到服务器.",userId);
			BaseAction<BaseRobot> rb = getAction();
			if(rb!=null) {
				rb.run(this);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	protected void init(){
	}
	
	public BaseRobot actionJob(BaseAction<BaseRobot> action,Object...args) {
		this.action = action;
		ClientData data = genClientData(action.getServiceCode(), args);
		this.client.sendData(data);
//		log.info("执行[{}]类型行为,协议号[{}].参数[{}]"
//				,new Object[]{action.getActionType(),action.getServiceCode()
//						,Arrays.stream(args).map(arg->arg.toString()).collect(Collectors.joining(","))});
		this.action.start();
		return this;
	}
	
	public BaseRobot action(BaseAction<BaseRobot> action) {
		this.action = action;
		return this;
	}
	
	public BaseRobot send(Object...args) {
		this.client.sendData(genClientData(action.getServiceCode(), args));
		log.info("执行[{}]类型行为,协议号[{}].参数[{}]"
				,new Object[]{action.getActionType(),action.getServiceCode()
						,Arrays.stream(args).map(arg->arg.toString()).collect(Collectors.joining(","))});
		return this;
	}
	
	public ClientData genClientData(int serviceCode, Object...args) {
		ClientData data = new ClientData(action.getServiceCode());
		if(args!=null && args.length>0){
			Arrays.stream(args).forEach(arg->data.appendValues(arg.toString()));
		}
		return data;
	}

	public BaseAction<BaseRobot> getAction() {
		return action;
	}
	
	protected void setAction(BaseAction<BaseRobot> action) {
		this.action = action;
	}

	public BaseClient getClient() {
		return client;
	}
	/**
	 * 延时
	 * @param time
	 * @return
	 */
	public BaseRobot sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
		return this;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public long getTeamId() {
		return teamId;
	}

	public long getUserId() {
		return userId;
	}
	
}
