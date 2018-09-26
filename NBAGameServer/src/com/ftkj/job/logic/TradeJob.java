package com.ftkj.job.logic;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.console.TradeConsole;
import com.ftkj.manager.logic.TradeManager;
import com.ftkj.manager.logic.TradeP2PManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;

/**
 * @Description:每分钟执行一次
 * @author Jay
 * @time:2017年5月26日 下午8:29:35
 */
@JobExpression(expression="0/1 * * * * ?",group=JobExpression.GAME,name="TradeJob")
public class TradeJob extends BaseJob {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		DateTime now = new DateTime(arg0.getScheduledFireTime());
		// 每秒清理一下过期交易列表
		TradeManager tradeManager = getManager(TradeManager.class);
		tradeManager.clearOffTrade();
		//
		TradeP2PManager tradeP2PManager = getManager(TradeP2PManager.class);
		tradeP2PManager.clearTimeOut(now);
		// 每五分钟筛选即将过期
		if(now.getSecondOfDay() % (TradeConsole.Check_Past_Min * 60) == 0) {
			tradeManager.refrushPast();
		}
	}

}
