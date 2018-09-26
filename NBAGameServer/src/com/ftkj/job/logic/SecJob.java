package com.ftkj.job.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.manager.logic.TrainManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;

/**
 * @author tim.huang
 * 2017年3月22日
 * 每秒执行一次
 */
@JobExpression(expression = "0/1 * * * * ?", group = JobExpression.GAME, name = "SecJob")
public class SecJob extends BaseJob {
    private static final Logger log = LoggerFactory.getLogger(SecJob.class);

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.debug("开始执行每秒一次的job");

        log.debug("执行选球队任务");
        TrainManager trainManager = getManager(TrainManager.class);
        trainManager.autoChioseTram();
    }

}
