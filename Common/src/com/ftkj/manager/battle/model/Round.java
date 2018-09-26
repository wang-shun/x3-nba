package com.ftkj.manager.battle.model;

import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.ResumeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Round implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Round.class);
    public static final boolean isDebug = log.isDebugEnabled();
    private static final long serialVersionUID = -6000638358414292537L;
    /** 是否暂停 */
    private boolean pause;
    /** 恢复暂停规则. 默认手动恢复 */
    private ResumeType resumeType = ResumeType.manual;
    /** 比赛阶段 */
    private EBattleStage stage;
    /** 当前小节 */
    private EBattleStep curStep;
    /** 计数器,每x轮为1回合 */
    private AtomicInteger counter = new AtomicInteger();
    /** 计数器,每x轮为1回合 */
    private AtomicInteger counterAll = new AtomicInteger();
    /** 当前回合数 */
    private int curRound;
    /** 已执行回合数 */
    private int runRound;
    /** 回合技能 */
    private RoundSkill skill;
    /** 强制执行比赛逻辑 */
    private boolean forceRun;
    /** 可修改的回合取模数，每200毫秒执行一次的话，需要多少次为1回合 */
    private int speedMod;
    /** 回合延迟 */
    private AtomicInteger roundDelay = new AtomicInteger();
    private int initSpeedMod;

    public Round(int speedMod) {
        this.pause = false;
        this.stage = EBattleStage.Before;
        this.curStep = EBattleStep.Start;
        this.curRound = 0;
        this.runRound = 0;
        this.forceRun = false;
        this.speedMod = speedMod;
        this.initSpeedMod = speedMod;
        this.skill = new RoundSkill();
    }

    /** 赛前动画，在推送阶段计数 */
    public boolean beforeRun() {
        if (stage != EBattleStage.TipTeam) {
            return false;
        }
        int c = counter.incrementAndGet();
        return c >= 0;
    }

    public RoundSkill getSkill() {
        return skill;
    }

    public void next() {
        this.counterAll.set(0);
    }

    public void reset() {
        this.speedMod = this.initSpeedMod;
        this.counterAll.set(0);
    }

    public void setInitSpeedMod(int initSpeedMod) {
        this.initSpeedMod = initSpeedMod;
    }

    public void setSpeedMod(int speedMod) {
        this.speedMod = speedMod;
    }

    /** 回合+1. true 成功 */
    public boolean run() {
        if (forceRun) {//强制执行一次之后，复位。如果需要一直执行，在逻辑代码中自行修改状态
            this.forceRun = false;
            curRound++;
            return true;
        }
        if (pause || runRound != curRound
                || curStep == EBattleStep.Closed) {
            if (isDebug) {
                log.debug("btround run. step {} run {} cur {} delay {} pause {}", curStep, runRound, curRound, roundDelay, pause);
            }
            return false;//暂停，当前回合并未结束 不继续逻辑。
        }
        if (roundDelay.get() > 1) {
            roundDelay.decrementAndGet();
            if (isDebug) {
                log.debug("btround run. step {} run {} cur {} delay {} pause {}", curStep, runRound, curRound, roundDelay, pause);
            }
            return false;
        }
        //还未到动画播放阶段，并且当前是响应回合，增加动画播放小节
        //		log.debug("技能起始回合{},当前回合{}",skill.getStartRound(),this.counter.get() );
        if (skill.decrResponseRound() <= 0) {//使用了技能，等待响应期间，不走技能回合暂停逻辑
            int cd = skill.decrCD();
            if (cd <= 0 && skill.getStartRound() <= this.counter.get()
                    && skill.getStat() != RoundSkill.STATUS_READY
                    && (skill.getHomeSkill() != null || skill.getAwaySkill() != null)) {//当前是响应技能小节，增加技能动画时间\
                //			if(cd<=0 && skill.getStartRound() == this.counter.get()){//当前是响应技能小节，增加技能动画时间\
                this.counter.set(0);
                skill.playerAnimation();
                //				//如果当前球权不是技能使用方球员，即刻执行，
                //				if(this.ballTeam != skill.getTeamId()){
                //					nono();
                //				}
                return false;
            }
            //			log.debug("当前技能增加的时效[{}]", cd);
            if (cd == 0) {//等于0说明动画播放结束，直接进入比赛逻辑。
                curRound++;
                return true;
            } else if (cd > 0) {//大于0说明正在跑等待响应或者动画播放阶段
                return false;
            }
        }
        this.counter.incrementAndGet();
        int r = this.counterAll.incrementAndGet() % speedMod;
        if (isDebug) {
            log.debug("btround count. step {} run {} cur {} delay {} count {} {}/{} r {}", curStep, runRound, curRound,
                    roundDelay, counter.get(), counterAll.get(), speedMod, r);
        }
        if (r != 0) {
            return false;//还没到回合时间
        }
        //		log.debug("本次行为的X值：{}", x);
        curRound++;
        return true;
    }

    public void setRoundDelay(int val) {
        this.roundDelay.set(val);
    }

    public int getRoundDelay() {
        return roundDelay.get();
    }

    public void setForceRun() {
        forceRun = true;
    }

    public void quickRun() {
        curRound++;
    }

    public void desRun() {
        curRound--;
    }

    public int getCurRound() {
        return curRound;
    }

    public void setCurRound(int curRound) {
        this.curRound = curRound;
    }

    public void roundOver() {
        runRound++;
    }

    public int getRunRound() {
        return runRound;
    }

    public void setRunRound(int runRound) {
        this.runRound = runRound;
    }

    public EBattleStep getCurStep() {
        return curStep;
    }

    public void setCurStep(EBattleStep curStep) {
        this.curStep = curStep;
    }

    /** true 暂停比赛 */
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPause() {
        return pause;
    }

    public ResumeType getResumeType() {
        return resumeType;
    }

    public void setResumeType(ResumeType resumeType) {
        this.resumeType = resumeType;
    }

    public void closeServer() {
        this.speedMod = 1;
    }

    public void updateStage(EBattleStage stage) {
        this.counter.set(0);
        this.stage = stage;
    }

    public EBattleStage getStage() {
        return stage;
    }

    /** 比赛已经结束或关闭 */
    public boolean isDone() {
        return stage == EBattleStage.End || stage == EBattleStage.Close;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

}
