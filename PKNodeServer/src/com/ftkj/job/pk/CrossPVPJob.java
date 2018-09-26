package com.ftkj.job.pk;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.manager.pk.CrossPVPManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;


@JobExpression(expression="0/1 * * * * ?",group=JobExpression.GAME,name="CrossPVPJob")
public class CrossPVPJob extends BaseJob{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		CrossPVPManager manager = getManager(CrossPVPManager.class);
		manager.matchTeams();
	}
	
}
