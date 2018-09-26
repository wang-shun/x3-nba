package com.ftkj.client;

import com.ftkj.client.robot.BaseRobot;
import com.ftkj.util.RandomUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2016年8月24日
 * 行为父类
 */
public abstract class BaseAction<T extends BaseRobot> {
	/** 主要行为 */
	public static final int Main_Action = 1;
	/** 次要行为 */
	public static final int Minor_Action = 2;
	/** 随机行为 */
	public static final int Random_Action = 3;
	
	
	/**
	 * 行为类型
	 */
	private int actionType;
	
	private int serviceCode;
	
	private long start;
	
	/**
	 * 后续行为集合
	 */
	private List<BaseAction<BaseRobot>> nextActionList;
	
	@SuppressWarnings("unchecked")
	public BaseAction(int actionType, int serviceCode){
		this.actionType = actionType;
		this.serviceCode = serviceCode;
		ActionConsole.append((BaseAction<BaseRobot>) this);
	}
	
	public BaseAction<BaseRobot> randomNextAction(){
		int index = RandomUtil.randInt(nextActionList.size());
		return nextActionList.get(index);
	}
	
	public BaseAction<BaseRobot> randomAction(){
		return ActionConsole.getRanAction(BaseAction.Random_Action);
	}
	
	
	
	
	/**
	 * 主体行为->次要行为
	 * 主体行为->随机行为
	 * 主体行为->主体行为
	 * 次要行为->随机行为
	 * 次要行为->次要行为
	 * 次要行为->主体行为
	 * 随机行为->主体行为
	 * 随机行为->随机行为
	 */
	public BaseAction<T> appendAction(BaseAction<BaseRobot> action){
		if(getActionType()==BaseAction.Random_Action &&
				action.getActionType()==BaseAction.Minor_Action){
			return this;
		}
		nextActionList.add(action);
		return this;
	}
	
	public void run(T robot,Object...val){
		robot.actionJob(get(),val);
	}
	
	public int getActionType() {
		return actionType;
	}
	public int getServiceCode() {
		return serviceCode;
	}
	
	public BaseAction<BaseRobot> get(){
		return ActionConsole.getAction(getServiceCode());
	}

	/**
	 * 处理行为返回的结果
	 * @param response
	 */
	public abstract void callback(ClientResponse response,T robot)throws Exception;
	
	/**
	 * 是否等待返回结构
	 * @return
	 */
	public boolean isWaitCallBack(){
		return true;
	}
	
	
	public void start(){
		this.start = System.currentTimeMillis();
	}
	
	public long end(){
		return System.currentTimeMillis()-start;
	}
	
	public abstract void init();
	
}
