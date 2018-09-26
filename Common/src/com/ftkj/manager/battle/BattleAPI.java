package com.ftkj.manager.battle;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.manager.battle.model.BattleInfo;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.Round;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.util.ThreadPoolUtil;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年2月22日
 * 战斗相关API
 */
public class BattleAPI {
    private static final Logger log = LoggerFactory.getLogger(BattleAPI.class);
    private final Map<Long, BattleHandle> battles = Maps.newConcurrentMap();
    //    private final BlockingQueue<IBattle> battleQueue = Queues.newArrayBlockingQueue(10000);

    private final static String Thread_Battle_Main = "battle_main_";
    private final static String Thread_Battle_PK = "battle_pk_";
    private final static String Thread_Battle_End = "battle_end_";

    private final static int PK_Thread_Count = 4;
    private final static int PK_End_Thread_Count = 8;

    private BattleAPI() {
    }

    public static BattleAPI getInstance() {
        return Holder.SINGLETON;
    }

    private static final class Holder {
        private static final BattleAPI SINGLETON;

        static {
            SINGLETON = new BattleAPI();
            SINGLETON.init();
        }
    }

    private void init() {
        ThreadPoolUtil.newScheduledPool(Thread_Battle_Main, 1)
            .scheduleWithFixedDelay(() -> this.pk(), 200L, 200L, TimeUnit.MILLISECONDS);

        for (int i = 0; i < PK_Thread_Count; i++) {
            ThreadPoolUtil.newScheduledPool(Thread_Battle_PK + i, 1);
        }
        for (int i = 0; i < PK_End_Thread_Count; i++) {
            ThreadPoolUtil.newScheduledPool(Thread_Battle_End + i, 1);
        }
    }

    private void pk() {
        for (BattleHandle battle : battles.values()) {
            try {
                synchronized (battle) {
                    pk0(battle);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void pk0(final BattleHandle battle) {
        final BattleSource bs = battle.getBattleSource();
        final BattleInfo bi = bs.getInfo();
        final long bid = bi.getBattleId();
        //        log.debug("battle handle battle {}", bid);
        final Round round = bs.getRound();
        if (bs.getStage() == EBattleStage.End) {//执行比赛结束回调操作
            log.info("btapi end bid {} round {} htid {} atid {} score {}:{}", bid, round.getCurRound(),
                bs.getHomeTid(), bs.getAwayTid(), bs.getHomeScore(), bs.getAwayScore());
            battleEnd(bid);
        } else if (bs.getStage() == EBattleStage.PK && round.run()) {//执行比赛逻辑
            log.debug("btapi pk round start. bid {} round {}", bid, round.getCurRound());
            ThreadPoolUtil.getScheduledPool(getPKThreadName(bid))
                .submit(() -> runRound(battle, bs));//执行比赛逻辑,返回战斗报表.封装报表为协议数据推送给对应的玩家
        } else if (bs.getStage() == EBattleStage.TipTeam && round.beforeRun()) {
            //双方准备完毕，通知双方进入战斗，因为推送是异步的。所以放在计数线程也可以
            bs.updateStage(EBattleStage.PK);
            bs.getInfo().setStartTime(System.currentTimeMillis());
            round.setRoundDelay(round.getRoundDelay() + bi.getStepDelay(bs.getCurStep(), 0));
            log.info("btapi tip bid {} change stage from TipTeam to PK. delay {}", bid, round.getRoundDelay());
        } else if (bs.getStage() == EBattleStage.Before) {//赛前，默认自动计时N秒后还没开赛即修改比赛状态
            //赛前准备时间超时，修改比赛状态，将双方拉入比赛
            //            log.debug("battle before bid {}", bid);
            ThreadPoolUtil.getScheduledPool(getPKThreadName(bid))
                .submit(() -> {
                    if (battle.checkBeforeTimeOut()) {//双方准备完毕进入下个阶段
                        battle.ready(bs.getHome().getTeamId());
                        battle.ready(bs.getAway().getTeamId());
                        log.info("btapi before bid {}. ready to next stage TipTeam", bid);
                    }
                });
        }
    }

    public void putBattle(BattleHandle battle) {
        BattleSource bs = battle.getBattleSource();
        //        int id = bs.getInfo().getBattleType().getId();
        //        if (id >= 100 && id < 200) { //TODO 多人赛干扰测试, 测试完毕后注释掉
        //            return;
        //        }

        bs.getInfo().setAddTime(System.currentTimeMillis());
        if (bs.getHomeTid() == bs.getAwayTid()) {
            log.warn("btapi start. bid {} type {} htid {} == atid {}. ret", bs.getId(), bs.getType(),
                bs.getHomeTid(), bs.getAwayTid());
            return;
        }

        battles.put(bs.getId(), battle);
        //        battleQueue.offer(battle);
        log.info("btapi start. bid {} btype {} htid {} atid {}", bs.getId(), bs.getType(), bs.getHomeTid(), bs.getAwayTid());
        addForceEndTask(bs);
    }

    /** 添加强制结束监听器 */
    private void addForceEndTask(BattleSource srcbs) {
        final long bid = srcbs.getId();

        ThreadPoolUtil.getScheduledPool(getPKEndThreadName(bid)).schedule(() -> {
            BattleHandle battle = battles.get(bid);
            if (battle == null) {
                return;
            }
            synchronized (battle) {
                final BattleSource bs = battle.getBattleSource();
                if (bs.isDone()) {//已经结束
                    return;
                }
                if (log.isInfoEnabled()) {
                    long startTime = bs.getInfo().getStartTime();
                    log.info("btapi forceEnd. bid {} time add {} start {} {} timeout, force end", bs.getId(),
                        bs.getInfo().getAddTime(), startTime, new Date(startTime));
                }
                //强制结束
                battle.pause(false);
                battle.quickEnd();
            }
        }, srcbs.getInfo().getBattleBean().getMaxMatchTime(), TimeUnit.SECONDS);
    }

    /** 比赛结束 */
    private void battleEnd(long bid) {
        ThreadPoolUtil.getScheduledPool(getPKEndThreadName(bid))
            .submit(() -> {
                BattleHandle battle = battles.get(bid);
                if (battle == null) {
                    return;
                }
                synchronized (battle) {
                    final BattleSource bs = battle.getBattleSource();
//                    if (bs.getStage() == EBattleStage.Close) {//已经结束
//                        return;
//                    }
//                    try {
//                        battle.end();
//                        battle.getEnd().end(bs);
//                    } catch (Exception e) {
//                        log.error(e.getMessage(), e);
//                    }
//                    bs.updateStage(EBattleStage.Close);//关闭比赛
                    endMatch(bs,battle);
                }

                battles.remove(bid);
            });
    }

    public static void endMatch(final BattleSource bs,BattleHandle battle) {
        if (bs.getStage() == EBattleStage.Close) {//已经结束
          return;
      }
      try {
          battle.end();
          battle.getEnd().end(bs);//比赛结束处理
      } catch (Exception e) {
          log.error(e.getMessage(), e);
      }
      bs.updateStage(EBattleStage.Close);//关闭比赛
    }
    
    /** 执行比赛逻辑,返回战斗报表.封装报表为协议数据推送给对应的玩家 */
    private void runRound(BattleHandle battle, BattleSource bs) {
        RoundReport report = battle.pk();//回合计算
        if (report == null) {
            return;
        }
        BattleHintHandle hh = new BattleHintHandle(bs, report);//回合结束计算比赛提示
        report.setHints(hh.calcHints());
        hh.clear();
        battle.getRound().round(bs, report);
    }

    /** 关闭服务器，结束所有正在进行的比赛 */
    public synchronized void closeServer() {
    	int size = battles.size();
    	AtomicInteger succesize = new AtomicInteger();
		log.info("关闭服务器，结束所有正在进行的比赛 {}", size);
        battles.values().forEach(battle -> {
            try {
                synchronized (battle) {
                    final BattleSource bs = battle.getBattleSource();
                    if (bs.getStage() == EBattleStage.Close) {//已经结束
                        return;
                    }
                    if (battle instanceof BaseBattleHandle) {
                        BaseBattleHandle bbh = (BaseBattleHandle) battle;
                        bbh.forceEnd(false,bbh);
                    }
                    bs.updateStage(EBattleStage.Close);//关闭比赛
                    succesize.incrementAndGet();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        log.info("关闭服务器，结束所有正在进行的比赛 {}/{}", succesize, size);
    }

    public BattleHandle getBattleHandle(long battleId) {
        return this.battles.get(battleId);
    }

    private String getPKThreadName(long battleId) {
        return Thread_Battle_PK + (battleId % PK_Thread_Count);
    }

    private String getPKEndThreadName(long battleId) {
        return Thread_Battle_End + (battleId % PK_End_Thread_Count);
    }

}
