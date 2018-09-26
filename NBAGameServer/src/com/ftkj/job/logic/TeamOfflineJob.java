package com.ftkj.job.logic;

import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.logic.TeamManager;
import com.ftkj.server.GameSource;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.tool.quartz.BaseJob;
import com.ftkj.tool.quartz.Expression;
import com.ftkj.tool.quartz.annotation.JobExpression;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * 球队离线
 */
@JobExpression(expression = Expression.Every_10_Minute, group = JobExpression.SYSTEM, name = "TeamOfflineJob")
public class TeamOfflineJob extends BaseJob {
    private static final Logger log = LoggerFactory.getLogger(TeamOfflineJob.class);

    @Override
    public void execute(JobExecutionContext arg0) {
        Collection<Long> offlineTeams = GameSource.removeAllOffLine();
        try {
            batchOffline(offlineTeams);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        Collection<Long> teams = GameSource.removeAllGcTeams();
        try {
            batchGc(teams);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void batchOffline(Collection<Long> offlineTeams) {
        if (offlineTeams == null || offlineTeams.isEmpty()) {
            return;
        }
        List<OfflineOperation> offopt = InstanceFactory.get().getOfflineList();
        TeamManager teamManager = InstanceFactory.get().getInstanceWithoutNew(TeamManager.class);
        if (teamManager == null) {
            log.warn("TeamOfflineJob teamManager is null");
            return;
        }
        for (Long tid : offlineTeams) {
            if (tid == null || GameSource.isNPC(tid) || GameSource.isOline(tid)) {
                continue;
            }
            log.info("team offline tid {}", tid);
            try {
                offopt.forEach(obj -> obj.offline(tid));
                GameSource.removeGC(tid);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("team offline size {}", offlineTeams.size());
    }
    
    /**
     * 强制调用下线方法保存数据
     * @param teamList
     */
    public static void forceOfflineList(List<Long> teamList) {
    	teamList.forEach(tid-> forceOffline(tid));
    }

    public static void forceOffline(long tid) {
        if (GameSource.isNPC(tid)) {
            return;
        }
        List<OfflineOperation> offopt = InstanceFactory.get().getOfflineList();
        log.info("team offline tid {}", tid);
        try {
            offopt.forEach(obj -> obj.offline(tid));
            GameSource.removeGC(tid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 清除球队数据, 在球队离线过程中, 如果其他玩家或者系统获取球队及球队模块信息, 相关信息会被加载到内存中,
     * 需要定时调度清除内存中的数据, 防止OOM.
     */
    private static void batchGc(Collection<Long> teams) {
        if (teams == null || teams.isEmpty()) {
            return;
        }
        List<OfflineOperation> offopt = InstanceFactory.get().getOfflineList();
        for (Long tid : teams) {
            if (tid == null || GameSource.isNPC(tid) || GameSource.isOline(tid) || GameSource.inOffline(tid)) {
                continue;
            }
            log.debug("team gcdata tid {}", tid);
            try {
                offopt.forEach(obj -> obj.dataGC(tid));
                GameSource.removeGC(tid);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("team gcdata size {}", teams.size());
    }

}
