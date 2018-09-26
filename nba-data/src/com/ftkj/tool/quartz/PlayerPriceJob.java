package com.ftkj.tool.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.ao.data.job.GameDataJob;
import com.ftkj.tool.log.LoggerManager;

public class PlayerPriceJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LoggerManager.error("开始执行身价更新");
		GameDataJob.UpdateNBAPlayer();
	}

	
}
