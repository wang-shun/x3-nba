package com.ftkj.job.cross;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.manager.cross.PlayerBidManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;


/**
 * @author tim.huang
 * 2018年3月23日
 * 整点，更新竞价球员
 */
@JobExpression(expression="0 0 15 * * ? ",group=JobExpression.GAME,name="PlayerBidEndJob")
public class PlayerBidEndJob extends BaseJob{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		PlayerBidManager manager = getManager(PlayerBidManager.class);
		manager.endBid();
	}

}
