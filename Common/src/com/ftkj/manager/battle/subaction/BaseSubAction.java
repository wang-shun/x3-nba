package com.ftkj.manager.battle.subaction;

import com.ftkj.cfg.battle.BaseSubActionBean;
import com.ftkj.cfg.battle.BattleActionBean;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.manager.battle.ActionPlayerHandle;
import com.ftkj.manager.battle.model.ActionVal;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.PlayerActStat;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.battle.model.RoundSkill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseSubAction implements SubAction {
    private static final Logger log = LoggerFactory.getLogger(BaseSubAction.class);
    private final EActionType type;
    protected static final int MAX_CHANCE = 100;

    public BaseSubAction(EActionType type) {
        this.type = type;
    }

    @Override
    public final EActionType getType() {
        return type;
    }

    @Override
    public final SubActionContext newContext(BattleSource bs,
                                             BattleTeam ball,
                                             BattleTeam otherBall,
                                             BattleActionBean actionBean,
                                             BaseSubActionBean subActionBean,
                                             RoundSkill roundSkill,
                                             Random ran,
                                             ActionPlayerHandle actPlayerHandle) {
        BaseBattleActionContext ctx = newContext0();
        ctx.init(bs, ball, otherBall, actionBean, subActionBean, roundSkill, ran, actPlayerHandle);
        initContext0(ctx);
        return ctx;
    }

    public final SubActionContext newContext(SubActionContext src) {
        BaseBattleActionContext ctx = newContext0();
        ctx.init(src.bs(), src.ball(), src.otherBall(),
            src.bean(), src.subBean(),
            src.roundSkill(), src.random(), src.actPlayerHandle());
        initContext0(ctx);
        return ctx;
    }

    protected BaseBattleActionContext newContext0() {
        return new BaseBattleActionContext();
    }

    void initContext0(BaseBattleActionContext ctx) {
        // NOOP
    }

    @Override
    public void doAction(SubActionContext ctx) {
        calcAndFindActPlayer(ctx);//取该行为的球员
        doAction0(ctx);
    }

    protected final void doAction0(SubActionContext ctx) {
        //首次行为决定进攻球员
        if (ctx.ball().getTeamId() == ctx.report().getBallTeamId() && ctx.report().getOffensePlayer() == null) {
            ctx.report().setOffensePlayer(ctx.bp());
        }
        BattlePlayer pr = ctx.bp().getPlayer();
        updatePlayerRoundPower(ctx, pr);
        updatePlayerSkillPower(ctx, getType());
        updatePlayerActStat(ctx, getType(), 1);
        addReportAction(ctx, pr.getPower());
    }

    /** 更新行为球员回合体力 */
    protected final void updatePlayerRoundPower(SubActionContext ctx, BattlePlayer pr) {
        //扣除体力,将体力变更放入回合战报行为中
        float roundPower = pr.calcPower(ctx.bs().getInfo().roundPower(getType()));
        if (roundPower != 0) {
            ctx.bs().stats().upPower(pr, roundPower);
        }
        if (log.isDebugEnabled()) {
            log.debug("subact base doact. bid {} btid {} otid {} acti {} ptid {} pid {} prid {} power {}", ctx.bs().getId(),
                ctx.ball().getTeamId(), ctx.otherBall().getTeamId(), type, pr.getTid(), pr.getPid(), pr.getRid(), roundPower);
        }
    }

    /** 获取球权方的球员，如果是防守行为，调用时将球权参数位置对调一下 */
    protected final BattlePosition calcAndFindActPlayer(SubActionContext ctx) {
        return calcAndFindActPlayer0(ctx, ctx.ball(), ctx.otherBall());
    }

    /** 获取球权方防守行为的球员 */
    final BattlePosition calcAndFindDefensePlayer(SubActionContext ctx) {
        return calcAndFindActPlayer0(ctx, ctx.otherBall(), ctx.ball());
    }

    /** 获取球权方的球员，如果是防守行为，调用时将球权参数位置对调一下 */
    private BattlePosition calcAndFindActPlayer0(SubActionContext ctx, BattleTeam ball, BattleTeam otherBall) {
        EPlayerPosition pos = null;
        boolean isForcePos = false;
        if (ball.getForceActionRound() > 0 &&
            ball.getForcePosActions() != null && ball.getForcePosActions().contains(getType())) {//强制命中球员
            List<EPlayerPosition> forcePos = ball.getForcePos();
            if (forcePos != null && !forcePos.isEmpty()) {
                int idx = ThreadLocalRandom.current().nextInt(forcePos.size());
                pos = forcePos.get(idx);
                isForcePos = true;
                if (log.isDebugEnabled()) {
                    log.debug("subact base force act pr. bid {}  bid {} btid {} otid {} targetpos {}",
                        ctx.bid(), ball.getTeamId(), otherBall.getTeamId(), pos);
                }
                ball.setForceActionRound(ball.getForceActionRound() - 1);
            }
        }

        if (pos == null) {
            pos = ctx.calcAndFindActPlayer(getType(), ball, otherBall, ctx.random());
        }
        BattlePosition bp = ball.getLineupPlayer(pos);
        bp.setForce(isForcePos);
        if (log.isDebugEnabled()) {
            log.debug("subact base find act pr. bid {} btid {} otid {} targetpos {} prid {}",
                ctx.bid(), ball.getTeamId(), otherBall.getTeamId(), pos, bp != null ? bp.getPlayer().getRid() : 0);
        }
        ctx.setBp(bp);
        return bp;
    }

    //    /** 更新得分 */
    //    public void updateScore(SubActionContext ctx, int addScore) {
    //        ctx.bs().stats().upRtAndStep(ctx.bpr(), ctx.step(), act(EActionType.pts, addScore));
    //        ctx.ball().updateRunPoint(addScore);
    //        ctx.ball().updateScore(addScore);
    //        ctx.report().addRoundScore(ctx.ball().getTeamId(), addScore);
    //        ctx.otherBall().clearCS();
    //    }

    /** 更新得分 */
    public void updateScore(SubActionContext ctx, int addScore) {
        ctx.bs().updateScore(ctx.ball(), ctx.otherBall(), ctx.bpr(), ctx.step(), addScore);
    }

    /** 更新球员个人比赛数据 {@link PlayerActStat} */
    protected void updatePlayerActStat(SubActionContext ctx, EActionType act, int actVal) {
        ctx.bs().stats().upRtAndStep(ctx.bpr(), ctx.step(), act(act, actVal));
    }

    /** 添加子行为报表 */
    protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.ball().getTeamId(), ctx.bpr().getPlayerId(), getType(), 0, 0, 0,ctx.bp().isForce());
    }

    /** 添加行为到战报 */
    protected final void updatePlayerSkillPower(SubActionContext ctx, EActionType act) {
        ctx.bs().stats().upSkillPower(ctx.bpr(), ctx.bs().getInfo().skillPower(act));
    }

    /** 子行为概率能否出发相关行为 */
    protected boolean isTriggerByChance(SubActionContext ctx, int defaultValue) {
        int subchance = ctx.subBean().getChance(defaultValue);
        if (subchance >= MAX_CHANCE) {
            return true;
        }
        int r = ctx.random().nextInt(MAX_CHANCE);
        return r <= subchance;
    }

    public static ActionVal act(EActionType act, int val) {
        return new ActionVal(act, val);
    }

    /** 行为执行参数 */
    protected static class BaseBattleActionContext implements SubActionContext {
        private BattleSource bs;
        private RoundReport report;
        private BattleActionBean actionBean;
        private BaseSubActionBean subActionBean;
        private RoundSkill roundSkill;
        private Random ran;
        private BattleTeam ball;
        private BattleTeam otherBall;
        private BattlePosition bp;
        /** 获取球权方执行行为的球员 */
        private ActionPlayerHandle actPlayerHandle;

        
        protected BaseBattleActionContext() {
        }

        //        public BaseBattleActionContext(BattleSource bs,
        //                                       BattleTeam ball,
        //                                       BattleTeam otherBall,
        //                                       BattleActionBean bean,
        //                                       BattleRoundSkill roundSkill,
        //                                       Random ran,
        //                                       ActionPlayerHandle actPlayerHandle) {
        //            this.bs = bs;
        //            this.report = bs.getReport();
        //            this.ball = ball;
        //            this.otherBall = otherBall;
        //            this.bean = bean;
        //            this.roundSkill = roundSkill;
        //            this.ran = ran;
        //            this.actPlayerHandle = actPlayerHandle;
        //        }

        private void init(BattleSource bs, BattleTeam ball, BattleTeam otherBall,
                          BattleActionBean bean, BaseSubActionBean subActionBean,
                          RoundSkill roundSkill, Random ran, ActionPlayerHandle actPlayerHandle) {
            this.bs = bs;
            this.report = bs.getReport();
            this.ball = ball;
            this.otherBall = otherBall;
            this.actionBean = bean;
            this.subActionBean = subActionBean;
            this.roundSkill = roundSkill;
            this.ran = ran;
            this.actPlayerHandle = actPlayerHandle;
        }

        @Override
        public final long bid() {
            return bs.getId();
        }

        @Override
        public final BattleSource bs() {
            return bs;
        }

        @Override
        public final EBattleStep step() {
            return bs.getCurStep();
        }

        @Override
        public final RoundReport report() {
            return report;
        }

        @Override
        public final void swapBall() {
            BattleTeam ball = this.ball;
            this.ball = this.otherBall;
            this.otherBall = ball;
        }

        @Override
        public final BattleTeam ball() {
            return ball;
        }

        @Override
        public final BattleTeam otherBall() {
            return otherBall;
        }

        @Override
        public final BattleActionBean bean() {
            return actionBean;
        }

        @Override
        public final BaseSubActionBean subBean() {
            return subActionBean;
        }

        @Override
        public final RoundSkill roundSkill() {
            return roundSkill;
        }

        @Override
        public final Random random() {
            return ran;
        }

        @Override
        public final BattlePosition bp() {
            return bp;
        }

        @Override
        public final BattlePlayer bpr() {
            return bp.getPlayer();
        }

        @Override
        public final void setBp(BattlePosition bp) {
            this.bp = bp;
        }

        @Override
        public final ActionPlayerHandle actPlayerHandle() {
            return actPlayerHandle;
        }

        @Override
        public EPlayerPosition calcAndFindActPlayer(EActionType type, BattleTeam team, BattleTeam otherTeam, Random ran) {
            return actPlayerHandle.calcAndFindActPlayer(type, team, otherTeam, ran);
        }
        
    }

}
