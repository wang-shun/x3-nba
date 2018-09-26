package com.ftkj.manager.battle.handle;

import com.ftkj.cfg.battle.BaseBattleBean;
import com.ftkj.cfg.battle.BaseSubActionBean;
import com.ftkj.cfg.battle.BattleActionBean;
import com.ftkj.cfg.battle.BattleCustomBean;
import com.ftkj.cfg.battle.BattleCustomBean.CustomRoundBean;
import com.ftkj.cfg.battle.BattleCustomBean.CustomSubActionBean;
import com.ftkj.cfg.battle.BattleCustomBean.FindPlayerRule;
import com.ftkj.cfg.battle.BattleCustomBean.PlayerStatBean;
import com.ftkj.cfg.battle.BattleCustomBean.TeamStatBean;
import com.ftkj.cfg.battle.BattlePlayerPowerBean;
import com.ftkj.cfg.battle.BattleSkillPowerBean;
import com.ftkj.console.BattleConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleAction;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.cfg.battle.BattleStepBean;
import com.ftkj.manager.battle.model.BattleInfo;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.Round;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.battle.model.RoundReport.ReportTeam;
import com.ftkj.manager.battle.subaction.BaseSubAction;
import com.ftkj.manager.battle.subaction.RebAction.FixedRebAction;
import com.ftkj.manager.battle.subaction.SubAction;
import com.ftkj.manager.battle.subaction.SubActionFactory;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * 策划自定义比赛. 策划控制回合行为和回合数据.
 */
public class BattleDesignCustomHandle extends BattleCommon {
    private static final Logger log = LoggerFactory.getLogger(BattleDesignCustomHandle.class);

    private final BattleCustomBean.CustomBean cb;
    private volatile CustomRoundBean rb;

    public BattleDesignCustomHandle(BattleSource bs, BattleCustomBean.CustomBean cb) {
        super(bs);
        this.cb = cb;
        if (cb != null) {
            BaseBattleBean bbb = cb.getBase();
            log.debug("btcus create. bid {} cbid {} speed {} startr {} startd {} roundpower {} skillpower {}",
                    bs.getId(), cb.getId(), bbb.getSpeed(), cb.getStartRound(), cb.getStartDelay(),
                    bbb.getPlayerPowerBean(), bbb.getSkillPowerBean());
            BattleInfo bi = bs.getInfo();
            bs.setBattleStep(bbb.getSteps());
            if (bbb.getSpeed() > 0) {
                bs.getRound().setSpeedMod(bbb.getSpeed());
                bs.getRound().setInitSpeedMod(bbb.getSpeed());
            }
            if (bbb.getStepDelay() != null) {
                bi.setStepDelay(bbb.getStepDelay());
            }
            if (cb.getStartDelay() > 0) {
                bs.getRound().setRoundDelay(bs.getRound().getRoundDelay() + cb.getStartDelay());
            }
            bi.setSkillStrategy(bbb.getSkillStrategy());

            BattlePlayerPowerBean bppb = BattleConsole.getPlayerRoundPowers(bbb.getPlayerPowerBean());
            if (bppb != null) {
                bi.setPlayerRoundPower(bppb);
                log.debug("btcus create. bid {} cbid {} roundpower {}", bs.getId(), cb.getId(), bppb);
            }
            BattleSkillPowerBean bspb = BattleConsole.getSkillPowers(bbb.getSkillPowerBean());
            if (bspb != null) {
                bi.setSkillPower(bspb);
                log.debug("btcus create. bid {} cbid {} skillpower {}", bs.getId(), cb.getId(), bspb);
            }
            if (bbb.getResumeType() != null) {
                bs.getRound().setResumeType(bbb.getResumeType());
            }
        }
    }

    /** 比赛初始化时, 前置初始化 */
    @Override
    protected void initPre() {
        BattleSource bs = getBattleSource();
        Round r = bs.getRound();
        int startRound = cb.getStartRound() - 1;
        if (startRound > 0) {
            BattleStepBean stepb = cb.getBase().getSteps().getStepByRound(cb.getStartRound());
            if (stepb != null) {
                r.setCurStep(stepb.getStep());
                bs.getReport().setRoundOfStep(cb.getStartRound() - stepb.getRoundInterval().getLower());
            }
            r.setRunRound(startRound);
            r.setCurRound(startRound);
        }
        setTeamScore(cb.getHomeInit(), bs.getHome());
        setTeamScore(cb.getAwayInit(), bs.getAway());
        log.debug("btcus initpre. bid {} step {} stepr {} rounds {} curr {} score {}:{}", bs.getId(),
                r.getCurStep(), bs.getReport().getRoundOfStep(), r.getRunRound(), r.getCurRound(),
                bs.getHomeScore(), bs.getAwayScore());
    }

    /** 比赛初始化时, 初始化报表 */
    @Override
    protected void initReport(BattleSource bs, RoundReport report) {
        report.nextStep(bs.getCurStep());
        report.setNextBall(rollBall());
        setReportTeam(report.getHome(), bs.getHome(), cb.getHomeInit());
        setReportTeam(report.getAway(), bs.getAway(), cb.getAwayInit());
    }

    /** 设置球队报表状态 */
    private void setReportTeam(ReportTeam rt, BattleTeam bt, TeamStatBean tsb) {
        if (tsb == null) {
            RoundReport.initTeamCap(rt, bt.getAbility());
            return;
        }
        if (tsb.getScore() > 0) {
            rt.setScore(tsb.getScore());
        }
        if (tsb.getOcap() > 0) {
            rt.setOffenseCap(tsb.getOcap());
        }
        if (tsb.getDcap() > 0) {
            rt.setDefenseCap(tsb.getDcap());
        }
        if (tsb.getOcap() == 0 && tsb.getDcap() == 0) {
            RoundReport.initTeamCap(rt, bt.getAbility());
        }
        log.trace("btcus reportteam. tid {} score {} tsb {} ", bt.getTeamId(), bt.getScore(), tsb);
    }

    /** 随机获得初始回合的球权 */
    private long rollBall() {
        BattleSource bs = getBattleSource();
        if (cb.getStartRound() <= 0) {
            return bs.randomTeam().getTeamId();
        }
        CustomRoundBean rb = cb.getRoundAct(cb.getStartRound());
        if (rb == null) {
            return bs.randomTeam().getTeamId();
        }
        if (rb.getSubActions().isEmpty()) {
            return bs.randomTeam().getTeamId();
        }
        CustomSubActionBean sab = rb.getSubActions().get(0);
        return bs.getTeamOrRandom(sab.getHomeAway()).getTeamId();
    }

    /** 回合开始 */
    @Override
    protected void roundStart(BattleSource bs, RoundReport report) {
        int round = bs.getRound().getCurRound();
        rb = cb.getRoundAct(round);
        log.debug("btcus roundStart. bid {} curround {} custom {} cusround {}", bs.getId(), round,
                cb.getId(), rb != null ? rb.getId() : "null");
    }

    /** 回合结束. 更新技能能量, 上场时间 */
    @Override
    protected void updateSkillPowerAndMin(BattleSource bs, EBattleStep step) {
        super.updateSkillPowerAndMin(bs, step);
        if (rb == null) {
            return;
        }
        if (rb.isHomeSkillPower() || rb.isHomePlayerPower()) {
            updatePlayerPowerAndSkillPower(bs, rb.getHomePlayerStat(), bs.getHome());
        }
        if (rb.isAwaySkillPower() || rb.isAwayPlayerPower()) {
            updatePlayerPowerAndSkillPower(bs, rb.getAwayPlayerStat(), bs.getAway());
        }
    }

    /** 回合结束. 更新球员能量, 技能能量 */
    private void updatePlayerPowerAndSkillPower(BattleSource bs, ImmutableList<PlayerStatBean> psbs, BattleTeam bt) {
        for (PlayerStatBean psb : psbs) {
            int sp = psb.getSkillPower();
            int pp = psb.getPlayerPower();
            if (sp == 0 && pp == 0) {
                continue;
            }
            EPlayerPosition pos = findPlayerLineupPos(bt, psb.getFindPlayerRule());
            log.trace("btcus upprpower. bid {} tid {} pos {} id {} fpr {}", bs.getId(), bt.getId(), pos,
                    psb.getId(), psb.getFindPlayerRule());
            if (pos == null) {
                continue;
            }
            BattlePosition bp = bt.getLineupPlayer(pos);
            if (bp != null) {
                BattlePlayer pr = bp.getPlayer();
                log.trace("btcus upprpower. bid {} tid {} pos {} skillpower {} {} prpower {} {} bp {}",
                        bs.getId(), bt.getId(), pos, sp, pr.getSkillPower(), pp, pr.getPower(), bp);
                if (sp != 0) {
                    bs.stats().setSkillPower(pr, sp);
                }
                if (pp != 0) {
                    bs.stats().setPower(pr, pp);
                }
            }
        }
    }

    /** 更新比分前处理比分 */
    @Override
    protected void postRoundBeforeUpScore(BattleSource bs, RoundReport report) {
        if (rb == null) {
            return;
        }
        setTeamScore(rb.getHomeEnd(), bs.getHome());
        setTeamScore(rb.getAwayEnd(), bs.getAway());
    }

    private void setTeamScore(TeamStatBean tsb, BattleTeam bt) {
        if (tsb != null && tsb.getScore() > 0) {
            bt.setScore(tsb.getScore());
            log.trace("btcust score. tid {} score {} tsb {}", bt.getTeamId(), bt.getScore(), tsb);
        }
    }

    //    protected void skillDelay(BattleSource bs, Round round, BattleActionBean actb) {
    //        if (rb == null) {
    //            super.skillDelay(bs, round, actb);
    //            return;
    //        }
    //        if (rb.getSkillRoundDelay() != 0) {
    //            round.setRoundDelay(round.getRoundDelay() + rb.getSkillRoundDelay());
    //        }
    //    }

    @Override
    protected void postRoundDelay(BattleSource bs, Round round, BattleActionBean actb) {
        if (rb == null) {
            super.postRoundDelay(bs, round, actb);
            return;
        }
        if (rb.getPostRoundDelay() != 0) {
            round.setRoundDelay(round.getRoundDelay() + rb.getPostRoundDelay());
        }
    }

    /** 回合结束 */
    @Override
    protected void roundEnd(BattleSource bs, RoundReport report) {
        if (rb == null) {
            return;
        }
        setReportTeam(report.getHome(), bs.getHome(), rb.getHomeEnd());
        setReportTeam(report.getAway(), bs.getAway(), rb.getAwayEnd());
        updatePlayerActStats(bs, rb.getHomePlayerStat(), bs.getHome());
        updatePlayerActStats(bs, rb.getAwayPlayerStat(), bs.getAway());
        rb = null;
    }

    /** 回合结束. 更新球员行为数据 */
    private void updatePlayerActStats(BattleSource bs, ImmutableList<PlayerStatBean> psbs, BattleTeam bt) {
        for (PlayerStatBean psb : psbs) {
            if (psb.getActStats() == null || psb.getActStats().isEmpty()) {
                continue;
            }
            EPlayerPosition pos = findPlayerLineupPos(bt, psb.getFindPlayerRule());
            log.trace("btcus roundend. bid {} tid {} pos {} id {} fpr {}", bs.getId(), bt.getId(), pos,
                    psb.getId(), psb.getFindPlayerRule());
            if (pos == null) {
                continue;
            }
            BattlePosition bp = bt.getLineupPlayer(pos);
            if (bp != null) {
                BattlePlayer pr = bp.getPlayer();
                log.trace("btcus roundend. bid {} tid {} pos {} bp {} actstats {}",
                        bs.getId(), bt.getId(), pos, bp, psb.getActStats());
                psb.getActStats().forEach((k, v) ->
                        pr.getRealTimeActionStats().set(k, v.getVal()));
            }
        }
    }

    /** 初始化结束报表 */
    @Override
    protected synchronized EndReport initEndReport() {
        BattleSource bs = getBattleSource();
        //        EndReport report = super.initEndReport();
        //        setReportTeam(report.getHome(), bs.getHome(), cb.getHomeEnd());
        //        setReportTeam(report.getAway(), bs.getAway(), cb.getAwayEnd());
        setTeamScore(cb.getHomeEnd(), bs.getHome());
        setTeamScore(cb.getAwayEnd(), bs.getAway());
        log.debug("btcust end report. bid {} htid {} atid {} force score {}:{}", bs.getId(),
                bs.getHomeTid(), bs.getAwayTid(), bs.getHomeScore(), bs.getAwayScore());
        return super.initEndReport();
    }

    /** 转换球权 */
    @Override
    protected void changePossession(BattleSource bs, RoundReport report) {
        if (rb == null || rb.getHomeAway() == null) {
            return;
        }
        long srctid = report.getBallTeamId();
        report.setBallTeamId(bs.getTeam(rb.getHomeAway()).getTeamId());
        //        report.setNextBall(report.getBallTeamId());
        log.debug("btcust changePossession. bid {} htid {} atid {} possession {} -> {}", bs.getId(),
                bs.getHomeTid(), bs.getAwayTid(), srctid, report.getBallTeamId());
    }

    /** 获取父行为 */
    @Override
    protected BattleActionBean getAction(BattleSource bs, RoundReport report) {
        if (rb == null || rb.getMainAction() == null) {
            return super.getAction(bs, report);
        }
        if (log.isDebugEnabled()) {
            log.debug("btcust getAction. bid {} round {} cus act {} {} subactsize {}", bs.getId(),
                    bs.getRound().getCurRound(), rb.getId(), rb.getMainAction(), rb.getSubActions().size());
        }
        //        if (rb.getMainAction() == EBattleAction.Coach_Skill) {
        //
        //        } else if (rb.getMainAction() == EBattleAction.skill) {
        //
        //        }
        BattleActionBean bab = bs.getInfo().getActions().getActionBean(rb.getMainAction());
        List<BaseSubActionBean> acts = buildSubActions(bab, rb);
        return new CustomBattleActionBean(bab, acts);
    }

    /** 获取子行为 */
    @Override
    protected SubAction getSubAction(BaseSubActionBean subActionBean) {
        if (rb == null) {
            return super.getSubAction(subActionBean);
        }
        SubAction bsa = SubActionFactory.getAction(subActionBean.getAction());
        if (subActionBean instanceof CustomSubActionBean) {
            if (bsa.getType() == EActionType.reb) {
                bsa = new FixedRebAction(bsa.getType());
            }
            return new CustomBattleAction(bsa, (CustomSubActionBean) subActionBean);
        }
        return bsa;
    }

    /** 构建子行为 */
    private List<BaseSubActionBean> buildSubActions(BattleActionBean bab, CustomRoundBean rb) {
        List<BaseSubActionBean> acts = bab.getSubActions();
        if (!rb.getSubActions().isEmpty()) {
            ImmutableList.Builder<BaseSubActionBean> list = ImmutableList.builder();
            for (CustomSubActionBean subact : rb.getSubActions()) {
                SubAction act = SubActionFactory.getAction(subact.getAction());
                if (act == null) {
                    continue;
                }
                list.add(subact);
            }
            acts = list.build();
        }
        return acts;
    }

    /** 比赛父行为 */
    private static class CustomBattleActionBean implements BattleActionBean {
        private static final long serialVersionUID = 8845281217941920043L;
        private final List<BaseSubActionBean> actions;
        private final BattleActionBean delegate;

        CustomBattleActionBean(BattleActionBean delegate, List<BaseSubActionBean> actions) {
            this.actions = actions;
            this.delegate = delegate;
        }

        @Override
        public List<BaseSubActionBean> getSubActions() {
            return actions;
        }

        @Override
        public int getId() {
            return delegate.getId();
        }

        @Override
        public EBattleAction getAction() {
            return delegate.getAction();
        }

        @Override
        public int getPostRoundDelay() {
            return delegate.getPostRoundDelay();
        }

        @Override
        public int getSkillRoundDelay() {
            return delegate.getSkillRoundDelay();
        }

        @Override
        public boolean isSucessAction() {
            return delegate.isSucessAction();
        }

        @Override
        public int getFtNum() {
            return delegate.getFtNum();
        }

        @Override
        public int getScore() {
            return delegate.getScore();
        }

        @Override
        public int getNormalTacticsWeight() {
            return delegate.getNormalTacticsWeight();
        }

        @Override
        public int getInsideTacticsWeight() {
            return delegate.getInsideTacticsWeight();
        }

        @Override
        public int getOuterTacticsWeight() {
            return delegate.getOuterTacticsWeight();
        }
    }

    /** 比赛子行为 */
    private static class CustomBattleAction extends BaseSubAction {
        private final SubAction srcAction;
        private final CustomSubActionBean subActionBean;

        CustomBattleAction(SubAction srcAction, CustomSubActionBean subActionBean) {
            super(srcAction.getType());
            this.srcAction = srcAction;
            this.subActionBean = subActionBean;
        }

        /** 修改原始子行为的参数 */
        @Override
        public CustomBattleActionContext newContext0() {
            return new CustomBattleActionContext(subActionBean);
        }

        /** 代理原始子行为 */
        @Override
        public void doAction(SubActionContext ctx) {
            log.debug("btcust subact do. bid {} round {} id {} dosubact {}", ctx.bs().getId(),
                    ctx.bs().getRound().getCurRound(), subActionBean.getId(), getType());
            if (subActionBean.getHomeAway() != null) {
                BattleSource bs = ctx.bs();
                RoundReport report = ctx.report();
                long srctid = report.getBallTeamId();
                report.setBallTeamId(bs.getTeam(subActionBean.getHomeAway()).getTeamId());
                if (srctid != report.getBallTeamId()) {
                    ctx.swapBall();
                }
                //        report.setNextBall(report.getBallTeamId());
                log.debug("btcust subact do change possession. bid {} htid {} atid {} possession {} -> {}", bs.getId(),
                        bs.getHomeTid(), bs.getAwayTid(), srctid, report.getBallTeamId());
            }
            srcAction.doAction(ctx);
        }

        /** 子行为执行参数 */
        protected static class CustomBattleActionContext extends BaseBattleActionContext {
            private final CustomSubActionBean subActionBean;

            CustomBattleActionContext(CustomSubActionBean subActionBean) {
                this.subActionBean = subActionBean;
            }

            /** 子行为查找执行球员规则 */
            @Override
            public EPlayerPosition calcAndFindActPlayer(EActionType type, BattleTeam team, BattleTeam otherTeam, Random ran) {
                if (subActionBean == null || !type.equals(subActionBean.getAction())) {
                    log.debug("cusBtActCtx find act pr. bid {} tid {} acttype {} subact {}",
                            bid(), team.getTeamId(), type, subActionBean);
                    return super.calcAndFindActPlayer(type, team, otherTeam, ran);
                }
                FindPlayerRule fpr = subActionBean.getFindPlayerRule();
                boolean noFpr = fpr == null || fpr.isAllNull();
                if (subActionBean.getHomeAway() == null && noFpr) {
                    log.debug("cusBtActCtx find act pr. bid {} acttype {} tid {} subactbid {} all is null",
                            bid(), type, team.getTeamId(), subActionBean.getId());
                    return super.calcAndFindActPlayer(type, team, otherTeam, ran);
                }
                BattleTeam bt = subActionBean.getHomeAway() != null ? bs().getTeam(subActionBean.getHomeAway()) : team;
                if (noFpr) {
                    log.debug("cusBtActCtx find act pr. bid {} acttype {} tid {} rule is null",
                            bid(), type, bt.getTeamId());
                    return super.calcAndFindActPlayer(type, bt, bs().getOtherTeam(bt.getTeamId()), ran);
                }
                EPlayerPosition pos = findPlayerLineupPos(bt, fpr);
                log.debug("cusBtActCtx find act pr. bid {} acttype {} tid {} rule {} pos {}",
                        bid(), type, bt.getTeamId(), fpr, pos);
                if (pos == null) {
                    return super.calcAndFindActPlayer(type, bt, bs().getOtherTeam(bt.getTeamId()), ran);
                }
                return pos;
            }

        }
    }

    /** 按规则查找要执行行为的场上球员位置 */
    private static EPlayerPosition findPlayerLineupPos(BattleTeam bt, FindPlayerRule fpr) {
        if (fpr.getQuality() != null) {//第一优先场上品质对应的场上位置
            for (BattlePosition bp : bt.getLineupPlayers().values()) {
                if (fpr.getQuality().equals(bp.getPlayer().getPlayer().getGrade())) {
                    return bp.getPosition();
                }
            }
        }

        if (fpr.getQualityReverse() != null) {//第二优先场下品质对应的场上位置
            for (BattlePlayer pr : bt.getPlayers()) {
                if (pr.getLineupPosition() == EPlayerPosition.NULL) {
                    if (fpr.getQualityReverse().equals(pr.getPlayer().getGrade())) {
                        return pr.getPlayerPosition();
                    }
                }
            }
        }
        //最后使用保底位置
        return fpr.getPos();
    }

}
