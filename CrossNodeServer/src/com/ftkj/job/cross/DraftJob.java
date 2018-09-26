package com.ftkj.job.cross;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.manager.cross.DraftManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;


@JobExpression(expression="0/1 * * * * ?",group=JobExpression.GAME,name="DraftJob")
public class DraftJob extends BaseJob{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		DraftManager manager = getManager(DraftManager.class);
		manager.execute();
	}

}
