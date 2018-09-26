package com.ftkj.manager.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.job.logic.TeamOfflineJob;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.CloseOperation;
import com.ftkj.manager.battle.BattleAPI;
import com.ftkj.server.ManagerOrder;

/**
 * Manager案例
 *
 * @author tim.huang
 * 2015年12月11日
 */
public class ShutdownManager extends BaseManager implements CloseOperation {
    private static final Logger log = LoggerFactory.getLogger(ShutdownManager.class);
    @IOC
    private MessageStatsManager messageStatsManager;
    @IOC
    private AllStarManager allStarManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private LeagueArenaManager leagueArenaManager;

    /** 按顺序关闭服务 */
    @Override
    public void close() {
      log.info("------------------------close---------------------------------");
        log.info("服务器准备关闭。按顺序关闭服务");
        //=========== 按顺序关闭服务 ===========
        //强制所有战斗结束
        executeCatchException(() -> BattleAPI.getInstance().closeServer());
        // 强迫所有玩家下线
        List<Long> onlineList = teamManager.getAllOnlineTeam().stream().map(t->t.getTeamId()).collect(Collectors.toList());
        executeCatchException(() -> TeamOfflineJob.forceOfflineList(onlineList));
        
        executeCatchException(() -> allStarManager.shutdown());

        executeCatchException(() -> messageStatsManager.shutdown()); 
        
        executeCatchException(() -> DBManager.run(true));     

        //=========== 按顺序关闭服务 ===========
        log.info("服务器关闭完毕.");
        //shutdown log4j2
        if (LogManager.getContext() instanceof LoggerContext) {
            //                        log.info("Shutting down log4j2");
            Configurator.shutdown((LoggerContext) LogManager.getContext());
        } else {
            System.err.print("Unable to shutdown log4j2");
        }
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Shutdown.getOrder();
    }

    @Override
    public void instanceAfter() {
    }

    void catchException(Exception e) {
        log.error(e.getMessage(), e);
    }

    @FunctionalInterface
    interface MaybeExceptionTask {
        void execute();
    }

    void executeCatchException(MaybeExceptionTask task) {
        try {
            task.execute();
        } catch (Exception e) {
            catchException(e);
        }
    }
}
