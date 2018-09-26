package com.ftkj.job.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.logic.TrainManager;
import com.ftkj.manager.logic.log.GameKeyValLogManager;
import com.ftkj.server.GameSource;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;

/**
 * @author Jay
 * @Description:每分钟执行一次
 * @time:2017年5月26日 下午8:29:35
 */
@JobExpression(expression = "0 * * * * ? *", group = JobExpression.GAME, name = "MinJob")
public class MinJob extends BaseJob {
    private static final Logger log = LoggerFactory.getLogger(MinJob.class);
    
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        EventBusManager.post(EEventType.任务调度每分钟, arg0.getScheduledFireTime());
        GameKeyValLogManager.Log(GameKeyValLogManager.在线, "" + GameSource.getOlineCount());
        
        // 每分钟调度  
        log.debug("开始执行抢夺次数刷新");
        TrainManager trainManager = getManager(TrainManager.class);
        trainManager.refrushRobbedCount();  
          
    }

}
