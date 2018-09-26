package com.ftkj.job.pk;

import com.ftkj.manager.pk.XRankedMatchManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.Expression;
import com.ftkj.tool.quartz.annotation.JobExpression;
import org.quartz.JobExecutionContext;

/**
 * 跨服天梯赛每日任务.
 */
@JobExpression(expression = Expression.DAILY_0001, group = JobExpression.GAME, name = "XRankedJobDaily")
public class XRankedDailyJob extends BaseJob {

    @Override
    public void execute(JobExecutionContext arg0) {
        XRankedMatchManager manager = getManager(XRankedMatchManager.class);
        manager.dailyJob();
    }

}
