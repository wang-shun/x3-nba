package com.ftkj.job.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.manager.logic.LeagueHonorManager;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;


/**
 * 每周6
 * @author Jay
 * @time:2018年5月8日 下午3:57:07
 */
@JobExpression(expression="0 0 0 ? * 7",group=JobExpression.GAME,name="LeagueHonorJob")
public class LeagueHonorJob extends BaseJob {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LeagueHonorManager manager = InstanceFactory.get().getInstance(LeagueHonorManager.class);
		manager.updateActivate();
	}

}
