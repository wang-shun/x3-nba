package com.ftkj.tool.quartz;

import org.quartz.Job;

import com.ftkj.manager.BaseManager;
import com.ftkj.server.instance.InstanceFactory;

/**
 * 任务调度父类
 * @author tim.huang
 * 2015年12月14日
 */
public abstract class BaseJob implements Job{

	protected <T extends BaseManager> T getManager(Class<T> cla){
		return InstanceFactory.get().getInstance(cla);
	}
	
}
