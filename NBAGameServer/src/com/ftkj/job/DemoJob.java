package com.ftkj.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.manager.logic.DemoManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;

@JobExpression(expression="* * 1 * * ?",group=JobExpression.SYSTEM,name="DemoJob")
public class DemoJob extends BaseJob{
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
//		getManager(DemoManager.class).helloQuartz();
	}

}
