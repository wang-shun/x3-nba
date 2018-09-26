package com.ftkj.tool.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.server.GameSource;
import com.ftkj.server.ServerStat;


/**
 * @author tim.huang
 * 2017年8月25日
 * 系统消息打印
 */
public class SystemStatJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if(GameSource.statJob)
			ServerStat.print();
	}

}
