package com.ftkj.job.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.manager.logic.RankManager;
import com.ftkj.manager.logic.ScoutManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;

/**
 * @author tim.huang
 * 2017年3月22日
 * 每小时执行一次
 */
@JobExpression(expression = "0 0 * * * ?", group = JobExpression.GAME, name = "HourJob")
public class HourJob extends BaseJob {
    private static final Logger log = LoggerFactory.getLogger(HourJob.class);

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.debug("开始执行每小时一次的job");

        log.debug("执行球探任务");
        ScoutManager scoutManager = getManager(ScoutManager.class);
        scoutManager.updateVersion();

        log.debug("执行排行榜任务");
        RankManager rankManager = getManager(RankManager.class);
        rankManager.updateTeamCapTask();

        log.debug("执行玩家目标");
       // GameManager gameManager = getManager(GameManager.class);
       // gameManager.topicOnlineTeamTarget();
        log.debug("结束每小时一次的job");
    }

}
