package com.ftkj.job.logic;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.manager.logic.AllStarManager;
import com.ftkj.manager.logic.LeagueArenaManager;
import com.ftkj.manager.logic.LimitChallengeManager;
import com.ftkj.manager.logic.PlayerManager;
import com.ftkj.manager.logic.StarletManager;
import com.ftkj.manager.logic.TaskManager;
import com.ftkj.manager.logic.TeamManager;
import com.ftkj.manager.logic.TrainManager;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.annotation.JobExpression;

/**
 * 每天调度执行
 * @author qin.jiang
 *
 */ 
@JobExpression(expression = "0 0 0 * * ?", group = JobExpression.GAME, name = "DayJob")
public class DayJob extends BaseJob {
    private static final Logger log = LoggerFactory.getLogger(DayJob.class);

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.debug("开始执行每天的job");

        log.debug("执行每日数据处理任务");      
        TeamManager teamManager = getManager(TeamManager.class);
        teamManager.clearDailyData();        
        
        log.debug("执行每日数据处理任务");      
        TaskManager taskManager = getManager(TaskManager.class);
        taskManager.clearDailyDate();
        
        log.debug("执行清理联盟荣誉榜任务");  
        LeagueArenaManager leagueArenaManager = getManager(LeagueArenaManager.class);
        leagueArenaManager.clearLeagueWeekScoreRank(); 
   
        log.debug("新秀排位赛发奖任务"); 
        StarletManager starletManager = getManager(StarletManager.class);
        starletManager.quartzSendReward();
        
        log.debug("全明星赛零点重置"); 
        AllStarManager allStarManager = getManager(AllStarManager.class);
        allStarManager.zeroReset();
        
        log.debug("周六清理联盟训练馆相关任务"); 
        TrainManager trainManager = getManager(TrainManager.class);
        trainManager.clearAllLeagueTrain();
        
        log.debug("执行刷新联盟荣誉榜任务");  
        leagueArenaManager.refreshLeagueWeekScoreRank();
        
        log.debug("极限挑战零点重置"); 
        LimitChallengeManager limitChallengeManager = getManager(LimitChallengeManager.class);
        limitChallengeManager.zeroReset();
        
        log.debug("执行每日竞猜活动数据的重新加载");      
        PlayerManager playerManager = getManager(PlayerManager.class);
        playerManager.zeroUpdateGameGuessData();
    }
}
