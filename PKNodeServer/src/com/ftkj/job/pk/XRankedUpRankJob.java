package com.ftkj.job.pk;

import com.ftkj.manager.pk.XRankedMatchManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.Expression;
import com.ftkj.tool.quartz.annotation.JobExpression;
import org.quartz.JobExecutionContext;

/**
 * 跨服天梯赛定时更新排行榜.
 */
@JobExpression(expression = Expression.Every_Even_Hours, group = JobExpression.GAME, name = "XRankedJobUpRank")
public class XRankedUpRankJob extends BaseJob {

    @Override
    public void execute(JobExecutionContext arg0) {
        XRankedMatchManager manager = getManager(XRankedMatchManager.class);
        manager.upRankJob();
    }

}
