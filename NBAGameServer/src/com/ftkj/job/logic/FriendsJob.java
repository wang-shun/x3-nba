package com.ftkj.job.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.manager.logic.FriendManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;

/**
 * @Description:每小时执行一次
 * @author Jay
 * @time:2017年5月26日 下午8:29:35
 */
@JobExpression(expression="0 0/30 * * * ? ",group=JobExpression.GAME,name="FriendJob")
public class FriendsJob extends BaseJob {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		FriendManager friendManager = getManager(FriendManager.class);
		friendManager.refrushReconmend();
	}

}
