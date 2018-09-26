package com.ftkj.job.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;

/**
 * @Description:系统活动定时器
 * @author Jay
 * @time:2017年5月26日 下午8:29:35
 */
@JobExpression(expression="0/1 * * * * ?",group=JobExpression.GAME,name="SystemActiveJob")
public class SystemActiveJob extends BaseJob {
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		EventBusManager.post(EEventType.活动定时器, arg0.getScheduledFireTime());
	}

}
