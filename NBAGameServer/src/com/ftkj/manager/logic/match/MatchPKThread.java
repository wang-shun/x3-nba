package com.ftkj.manager.logic.match;

import com.ftkj.console.KnockoutMatchConsole;
import com.ftkj.enums.EMatchStatus;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.logic.LocalBattleManager;
import com.ftkj.manager.match.MatchPK;
import com.ftkj.server.instance.InstanceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Jay
 * @Description: 比赛线程
 * @time:2017年5月22日 上午10:02:15
 */
public class MatchPKThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(MatchPKThread.class);
    /**
     * 比赛
     */
    private KnockoutMatch match;
    /**
     * 0 未匹配球队
     * 1 匹配中
     * 2 匹配完成，本轮比赛进行中(如果本轮比赛全部打完，会直接回到0，匹配下一轮对战)
     */
    private int status;
    /**
     * 是否延时中
     */
    private boolean isDelay;
    /**
     * 多人赛名称
     */
    private String matchName;
    private LocalBattleManager localBattleManager;

    public MatchPKThread(KnockoutMatch knockoutMatch) {
        this.match = knockoutMatch;
        this.localBattleManager = InstanceFactory.get().getInstance(LocalBattleManager.class);
        this.status = 0;
        this.isDelay = false;
        this.matchName = KnockoutMatchConsole.getName(this.match.getMatchId()) + "[" + this.match.getMatchId() + "][" + this.match.getSeqId() + "]";
        this.setName(this.matchName);
        this.setPriority(9);
    }

    // 1,判断当前轮不是结束，
    // 2,创建本轮比赛
    // 3,开始比赛，循环；(每轮结束怎么判定)
    @Override
    public void run() {
        try {
            this.isDelay = false;
            boolean waitign = false;
            log.debug("多人赛[{}]线程开始，状态{}", this.matchName, this.match.getStatus());
            int maxCount = 0;
            while (this.match.getStatus() == EMatchStatus.比赛中.status && maxCount < 10000) {
                //log.debug("多人赛[{}]线程当前状态：{}", this.matchName, this.status);
                // 本轮匹配完，比赛没打完，跳过，比赛已打完，匹配下一轮
                if (this.status == 0) {
                    // 匹配对手如果发生异常退出进程
                    if (this.match.matchRound() > 0) {
                        log.debug("多人赛[{}]匹配对手异常", this.matchName);
                        // 匹配异常次数
                        this.match.addStartPKCount(1);
                        break;
                    }
                    this.status = 1;
                }
                // 这里改用结束，延时开始线程即可。
                if (this.status == 1 && this.match.getMaxRound() > 1) {
                    this.status = 2;
                    waitign = true;
                    break;
                }
                // 创建比赛并开始PK
                if (this.status == 2) {
                    // 本轮未打完比赛的匹配
                    List<MatchPK> pkList = this.match.getTheRoundMatchPKNoStart();
                    if (pkList.size() == 0) {
                        log.debug("多人赛[{}]开始第[{}]轮比赛已打完,进入下一轮匹配", this.matchName);
                        this.status = 0;
                        continue;
                    }
                    log.info("knockout match start. kid {} seqid {} round {}/{} size {} status {}", match.getMatchId(),
                        match.getSeqId(), match.getRound(), match.getMaxRound(), pkList.size(), status);
                    // 创建比赛
                    for (MatchPK pkInfo : pkList) {
                        BattleSource bs = localBattleManager.buildBattle(this.match.battleType, pkInfo.getBattleId(), pkInfo.getHomeId()
                            , pkInfo.getAwayId(), null, null, 0);
                        log.info("knockout match start. kid {} seqid {} round {}/{} bid {} htid {} atid {}",
                            match.getMatchId(), match.getSeqId(), match.getRound(), match.getMaxRound(),
                            bs.getBattleId(), bs.getHomeTid(), bs.getAwayTid());

                        localBattleManager.start(bs, this::matchEnd, this::roundReport);
                    }
                    break;
                }
                maxCount++;
            }
            if (waitign) {
                log.debug("多人赛{}匹配完成，准备中，即将开始", this.matchName);
                this.match.startPKRoundThred(this.status, KnockoutMatchConsole.Match_Ready_Time);
            }
            log.debug("多人赛[{}]线程执行结束，状态：{}", this.matchName, this.getStatus());
        } catch (Exception e) {
            log.error("多人赛[{}]线程异常", this.matchName);
            log.error(e.getMessage(), e);
            this.match.addStartPKCount(1);
        }
    }

    private void matchEnd(BattleSource bs) {
        try {
            localBattleManager.sendEndMain(bs, true);
            match.matchEnd(bs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void roundReport(BattleSource bs, RoundReport report) {
        try {
            match.roundReport(bs, report);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public boolean isDelay() {
        return isDelay;
    }

    public void setDelay(boolean isDelay) {
        this.isDelay = isDelay;
    }

}
