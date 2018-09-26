package com.ftkj.job.cross;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.manager.cross.PlayerBidManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;


/**
 * @author tim.huang
 * 2018年3月23日
 * 12点后，更新界面
 */
@JobExpression(expression="0/10 * 12-15 * * ? *",group=JobExpression.GAME,name="PlayerBidUpdateJob")
public class PlayerBidUpdateJob extends BaseJob{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		PlayerBidManager manager = getManager(PlayerBidManager.class);
		manager.autoUpdateBidSource();
	}

}
