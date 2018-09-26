package com.ftkj.manager.task;

import java.util.Map;

import org.joda.time.DateTime;

/**
 * 活动刷新任务接口
 * @author Jay
 * @time:2018年3月27日 下午3:09:21
 */
public interface ITaskActiveRefrush {

	/**
	 * 任务ID：结束时间
	 * @return
	 */
	public Map<Integer, DateTime> getRefrushTask();
	
}
