package com.ftkj.manager.battle;

import com.ftkj.cfg.KnockoutMatchBean;
import com.ftkj.cfg.TacticsCapBean;
import com.ftkj.cfg.battle.BaseSubActionBean;
import com.ftkj.cfg.battle.BattleActionBean;
import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.cfg.battle.BattleStepBean;
import com.ftkj.console.BattleConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ConfigConsole.GlobalBean;
import com.ftkj.console.DropConsole;
import com.ftkj.console.KnockoutMatchConsole;
import com.ftkj.console.TacticsConsole;
import com.ftkj.constant.MatchConst;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.enums.battle.EBattleAction;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.ResumeType;
import com.ftkj.enums.battle.TacticZone;
import com.ftkj.manager.battle.model.ActionVal;
import com.ftkj.manager.battle.model.BattleBuffer;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTactics;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.BattleTeam.Status;
import com.ftkj.manager.battle.model.BattleTeamAbility;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.Round;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.battle.model.RoundReport.ReportTeam;
import com.ftkj.manager.battle.model.RoundSkill;
import com.ftkj.manager.battle.model.Skill;
import com.ftkj.manager.battle.subaction.BaseSubAction;
import com.ftkj.manager.battle.subaction.CoachSkillAction;
import com.ftkj.manager.battle.subaction.PlayerSkillAction;
import com.ftkj.manager.battle.subaction.SubAction;
import com.ftkj.manager.battle.subaction.SubActionFactory;
import com.ftkj.manager.battle.subaction.TacticsAction;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.tactics.TacticsBean;
import com.ftkj.manager.tactics.TacticsSimple;
import com.ftkj.proto.BattlePB;
import com.ftkj.server.GameSource;
import com.ftkj.util.StringUtil;
import com.ftkj.util.tuple.Tuple2;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 比赛处理
 *
 * @author tim.huang
 * @author luch
 */
public abstract class BaseBattleHandle implements BattleHandle {
    private static final Logger log = LoggerFactory.getLogger(BaseBattleHandle.class);
    private BattleSource battleSource;
    private BattleEnd end;
    private BattleRoundReport round;
    private BattleStartPush startPush;
    private Random ran;
    private ActionPlayerHandle actionPlayerHandle;
    private int timeOut = 0;

    public BaseBattleHandle() {
    }

    public BaseBattleHandle(BattleSource battleSource) {
        this.battleSource = battleSource;
        this.ran = new Random(battleSource.getInfo().getBattleId());
        this.actionPlayerHandle = createActionPlayerHandle();
    }

    public BaseBattleHandle(BattleSource battleSource, BattleEnd end, BattleRoundReport round) {
        this(battleSource);
        this.end = end;
        this.round = round;
        init();
    }

    public void setBattleSource(BattleSource battleSource) {
        this.battleSource = battleSource;
        this.ran = new Random(battleSource.getInfo().getBattleId());
    }

    /** 初始化比赛信息 */
    public final synchronized void init() {
        BattleSource bs = this.battleSource;
        log.debug("btmain init. bid {}", bs.getId());
        initPre();//初始化 前置处理
        RoundReport report = bs.getReport();
        bs.getRound().getSkill().setHomeTeamId(bs.getHome().getTeamId());
        bs.getRound().getSkill().setAwayTeamId(bs.getAway().getTeamId());
        bs.updateTacticCap();
        recalcPowerAndCap(ConfigConsole.global(), bs, null, null);
        initReport(bs, report);//初始化报表
        //        initPost();//初始化 后置处理
        buildBeforeDataCache(bs, report.getHome(), bs.getHome());
        buildBeforeDataCache(bs, report.getAway(), bs.getAway());
        initAI(bs, bs.getRound().getCurRound());
    }

    /** 初始化 AI 规则 */
    private void initAI(BattleSource bs, int curRound) {
        bs.getHome().getAi().init(bs.getId(), bs.getHomeTid(), curRound);
        bs.getAway().getAi().init(bs.getId(), bs.getAwayTid(), curRound);
    }

    /** 初始化比赛信息时初始化时报告 */
    protected void initReport(BattleSource bs, RoundReport report) {
        report.nextStep(bs.getCurStep());
        report.setNextBall(bs.randomTeam().getTeamId());//随机获得初始回合的球权
        report.initTeamCap(bs.getHome().getAbility(), bs.getAway().getAbility());
    }

    /** 在初始化比赛信息之前 处理比赛信息 */
    protected void initPre() {
    }

    //    /** 在初始化比赛信息之后 处理比赛信息 */
    //    protected void initPost() {
    //    }

    /** 赛前玩家数据缓存 */
    private void buildBeforeDataCache(BattleSource bs, ReportTeam rt, BattleTeam bt) {
        BattlePB.BattlePKTeamData teamData = BattlePb.pKTeamData(rt, bt, bs.getRound().getCurRound());
        bs.getAttributeMap(bt.getTeamId()).addVal(EBattleAttribute.Before_Data, teamData);
    }

    @Override
    public synchronized boolean checkBeforeTimeOut() {
        timeOut++;
        return timeOut > 8;
    }

    @Override
    public synchronized boolean ready(long teamId) {
        BattleSource bs = battleSource;
        BattleTeam team = bs.getTeam(teamId);
        if (team == null || team.getReady() == Status.Ready) {
            return false;
        }

        team.ready();
        if (bs.getHomeTid() == bs.getAwayTid()) {
            bs.getHome().ready();
            bs.getAway().ready();
        }

        if (bs.getHome().getReady() == Status.Ready
            && bs.getAway().getReady() == Status.Ready) {
            //双方准备完毕，修改阶段状态在下一次计数器自增时开启战斗逻辑与推送(每200毫秒自增一次)
            bs.updateStage(EBattleStage.TipTeam);
            return true;
        }
        return false;
    }

    @Override
    public synchronized RoundReport pk() {
        return pk0();
    }

    private RoundReport pk0() {
        final BattleSource bs = this.battleSource;
        try {
            final ThreadLocalRandom tlr = ThreadLocalRandom.current();
            final Round round = bs.getRound();
            final int currRound = round.getCurRound();

            RoundReport report = pk1(bs, round, currRound);//回合行为
            handleAIPostRound(bs, currRound, tlr);//执行下回合的AI行为
            roundEnd(bs, report);//回合结束
        } catch (Exception e) {
            log.error("btmain pk " + e.getMessage(), e);
        }
        return bs.getReport();
    }

    /** 比赛结束处理 */
    @Override
    public void end() {

    }

    private RoundReport pk1(BattleSource bs, Round round, int currRound) {
        final RoundReport report = bs.getReport();
        final EBattleStep srcstep = bs.getCurStep();
        final RoundSkill rs = round.getSkill();
        final GlobalBean gb = ConfigConsole.global();

        report.nextRound(bs.getHome().getAbility(), bs.getAway().getAbility());//清空上一轮临时战斗报表, 更换当前球权
        bs.stats().setStep(srcstep, EActionType.Step_Round, report.getRoundOfStep() + 1);

        roundStart(bs, report);//回合开始

        if (srcstep == EBattleStep.End) {//结束，直接返回结束结果
            report.end();
            bs.setEndBattle(initEndReport());//生成结束报表
            bs.updateStage(EBattleStage.End);
            report.setAction(EBattleAction.A_关闭);
            log.info("btmain end. bid {} round {} step {} htid {} atid {}", bs.getId(), currRound, srcstep,
                bs.getHomeTid(), bs.getAwayTid());
            return report;
        }
        if (currRound <= 1 && log.isInfoEnabled()) {
            log.info("btmain start. id {} round {}, home {} pr {} cap {}, away {} pr {} cap {}", bs.getId(),
                currRound,
                bs.getHomeTid(), bs.getHome().getPlayers().size(), bs.getHome().getAbility().getTotalCap(),
                bs.getAwayTid(), bs.getAway().getPlayers().size(), bs.getAway().getAbility().getTotalCap());
        }
        //执行逻辑前，更新当前小节阶段
        boolean isRound = true;
        EBattleStep currRoundStep = getStepByRound(currRound, bs.getStepConfig());
        EBattleStep nextStep = getNextStep(bs);
        boolean hasSkill = rs.hasSkill();
        if (nextStep != currRoundStep && !hasSkill) { //小节暂停
            round.setRoundDelay(round.getRoundDelay() + bs.getInfo().getStepDelay(nextStep, 0));
            bs.getReport().addSubAct(0, 0, EActionType.Step_Pause, bs.getCurStep().getType(), 0, 0, false);
            log.info("btmain steppause. bid {} round {} step curr {} next {} delay {}", bs.getId(), currRound, currRoundStep,
                nextStep, round.getRoundDelay());
        }
        if (stepCanChange(bs, currRoundStep)) {
            if (!hasSkill) {
                nextStep(bs);
                updateActionAndStepScoreWhenStepChange(bs, report, currRoundStep);
                bs.roundOver();
                addActiveBuffRound(bs, currRound);
                return report;
            } else if (rs.getStat() != RoundSkill.STATUS_START) {
                isRound = false;
                round.desRun();
            }
        }

        bs.getHome().getStat().clear(); //重置球队的状态
        bs.getAway().getStat().clear();
        pkCoach(bs, currRound);//触发教练技能
        changePossession(bs, report);//强制转换球权
        if (hasSkill && rs.getStat() == RoundSkill.STATUS_READY && rs.getTeamId() != report.getBallTeamId()) {
            log.debug("btmain force ball. bid {} round {} stid {} btid {}", bs.getId(), currRound,
                rs.getTeamId(), report.getBallTeamId());
            report.setBallTeamId(rs.getTeamId());
        }

        BattleActionBean actb = getAction(bs, report);//获取父行为
        log.info("btmain getact. bid {} round {} step {} btid {} actb {} stepRound {} delay {}",
            bs.getId(), currRound, currRoundStep, report.getBallTeamId(), actb.getAction(), report.getRoundOfStep(),
            actb.getPostRoundDelay());
        report.setAction(actb.getAction());
        //        if (actb.getScore() > 0) { //将分数放入球队总分中
        //            bs.getBallTeam().updateScore(actb.getScore());
        //        }
        for (BaseSubActionBean subactb : actb.getSubActions()) {//执行子行为
            SubAction ba = getSubAction(subactb);
            if (ba == null) {
                log.warn("btmain doaction. bid {} subact {} null", bs.getId(), subactb.getAction());
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug("btmain doaction. bid {} btid {} otid {} actb {} acti {}, subactb {}", bs.getId(),
                    report.getBallTeamId(), bs.getBallOtherTeam().getTeamId(), actb.getAction(), ba.getType(), subactb);
            }
            ba.doAction(ba.newContext(bs, bs.getBallTeam(), bs.getBallOtherTeam(), actb, subactb, rs, ran, actPlayerHandle()));
        }

        final BattleTeam ball = bs.getBallTeam();
        final BattleTeam otherBall = bs.getBallOtherTeam();
        recalcPowerAndCap(gb, bs, ball, otherBall);//重新计算体力, 重新计算攻防, 重新计算双方行为类型命中概率
        if (isRound) {
            bs.roundOver();
        }

        juesha(bs, report, nextStep);//绝杀判断
        if (rs.getStat() == RoundSkill.STATUS_START) {//技能暴击处理
            skillSuper(bs, ball, otherBall, report, srcstep);
        }
        postRoundBeforeUpScore(bs, report);//回合结束, 处理得分
        updateScore(bs, report);//同步最新得分

        EBattleStep step = round.getCurStep();
        updateSkillPowerAndMin(bs, step);//更新技能能量, 上场时间
        //        pkAfter();
        updateStreakScoreMorale(bs.getHome(), report);//计算连续得分增加的士气
        updateStreakScoreMorale(bs.getAway(), report);
        round.reset();
        if (rs.getStat() == RoundSkill.STATUS_READY) {//有技能未执行，强制下一个小节直接执行比赛逻辑，不走正常回合
            round.setForceRun();
            report.addSubAct(ball.getTeamId(), 0, EActionType.remove_effect, 0, 0, 0, false);
        } else if (rs.getStat() == RoundSkill.STATUS_START) {//技能已经执行，清空当前的回合技能数据
            rs.clear();
            if (actb.getSkillRoundDelay() != 0) {
                round.setRoundDelay(round.getRoundDelay() + actb.getSkillRoundDelay());
            }
        }
        postRoundDelay(bs, round, actb);//下一回合延迟轮数
        return report;
    }

    private void handleAIPostRound(BattleSource bs, int currRound, ThreadLocalRandom tlr) {
        bs.getHome().getAi().handleAIPostRound(bs, bs.getHome(), bs.getAway(), currRound, tlr);
        bs.getAway().getAi().handleAIPostRound(bs, bs.getAway(), bs.getHome(), currRound, tlr);
    }

    /** 下一回合延迟轮数 */
    protected void postRoundDelay(BattleSource bs, Round round, BattleActionBean actb) {
        if (actb.getPostRoundDelay() != 0) {
            round.setRoundDelay(round.getRoundDelay() + actb.getPostRoundDelay());
        }
    }

    /** 转换球权 */
    protected void changePossession(BattleSource bs, RoundReport report) {
    }

    /** 回合开始 */
    protected void roundStart(BattleSource bs, RoundReport report) {
    }

    /** 回合结束 */
    protected void roundEnd(BattleSource bs, RoundReport report) {
    }

    /** 更新比分前处理比分 */
    protected void postRoundBeforeUpScore(BattleSource bs, RoundReport report) {
    }

    /** 更新比分数据 */
    protected final void updateScore(BattleSource bs, RoundReport report) {
        final int hs = bs.getHome().getScore();
        final int as = bs.getAway().getScore();
        final int hsold = report.getHomeScore();
        final int asold = report.getAwayScore();
        log.debug("btmain upscore. bid {} hs {} -> {} as {} -> {}", bs.getId(), hsold, hs, asold, as);
        if (hs == hsold && as == asold) {//nothing changed
            return;
        }

        report.updateScore(hs, as);

        if (hs != as) {
            final EActionType srnAct = EActionType.Team_Score_Rotation_Num;//双方分数交替达到X次
            if (hsold <= asold && hs > as) {//主场分数交替
                bs.stats().upRtAndStep(bs.getHome(), srnAct, 1);
            }
            if ((hsold >= asold && hs < as)) {//客场分数交替
                bs.stats().upRtAndStep(bs.getAway(), srnAct, 1);
            }
        }

        final int sgap = Math.abs(hs - as);
        final EActionType bact = EActionType.Battle_Score_Gap;//比赛分差
        if (bs.getRtActionStats().getIntValue(bact) < sgap) {
            bs.stats().setRtAndStep(bact, sgap);
        }
        final EActionType gapAct = EActionType.Team_Score_Gap;//球队分差
        if (hs > as) {
            final int hagap = hs - as;
            if (bs.getHome().getRtActionStats().getIntValue(gapAct) < hagap) {//主场
                bs.stats().setRtAndStep(bs.getHome(), gapAct, hagap);
            }
        }
        if (as > hs) {
            final int ahgap = as - hs;
            if (bs.getAway().getRtActionStats().getIntValue(gapAct) < ahgap) {
                bs.stats().setRtAndStep(bs.getAway(), gapAct, ahgap);
            }
        }
    }

    /** 按规则获取父行为 */
    protected BattleActionBean getAction(BattleSource bs, RoundReport report) {
        final BattleTeam ball = bs.getBallTeam();
        BattleActionBean action = getAction0(bs, report, ball);
        if (report.getStep() == EBattleStep.Overtime) {//加时特殊处理行为
            int lr = bs.getStepConfig().getBattleStepRound(EBattleStep.Overtime) - report.getRoundOfStep();
            TacticZone tt = ball.getPkTactics(TacticType.Offense).getTactics().getZone();
            boolean successAction = ball.getScore() == bs.getBallOtherTeam().getScore();
            if (lr == 1) {//加时最后一回合，比分相等，强制命中得分行为 //加时最后一回合，比分不等，强制命中失败行为
                log.debug("btmain getact. bid {} step is ot and score equals. force use succ [{}] act",
                    bs.getId(), successAction);
                action = BattleConsole.getActionBean(bs.getInfo().getActions(), tt, successAction, ran);
            }
        }
        return action;
    }

    /** 获得一个当前回合的行为枚举 */
    private BattleActionBean getAction0(BattleSource bs, RoundReport report, BattleTeam ball) {
        RoundSkill rs = bs.getRound().getSkill();
        if (log.isDebugEnabled() && rs.hasSkill()) {
            log.debug("btmain getact by skill. bid {} btid {} skill {}", bs.getId(), ball.getTeamId(), rs);
        }
        if (rs.getStat() == RoundSkill.STATUS_READY && rs.getTeamId() == report.getBallTeamId()) {//有技能未触发，并且当前球权等于技能球权球队
            return getActionBySkill(bs, report, rs, ball);
        }
        boolean succAction = ball.randomSuccessAction(ran);
        TacticZone tt = ball.getPkTactics(TacticType.Offense).getTactics().getZone();
        return BattleConsole.getActionBean(bs.getInfo().getActions(), tt, succAction, ran);
    }

    private BattleActionBean getActionBySkill(BattleSource bs, RoundReport report, RoundSkill rs, BattleTeam ball) {
        final BattleTeam otherBall = bs.getBallOtherTeam();
        //取球权方技能
        Skill os = ball.getTeamId() == rs.getHomeTeamId() ? rs.getHomeSkill() : rs.getAwaySkill();
        //取防守方技能
        Skill ds = ball.getTeamId() == rs.getHomeTeamId() ? rs.getAwaySkill() : rs.getHomeSkill();
        ball.setNextPlayer(os != null ? os.getPlayerId() : 0);
        otherBall.setNextPlayer(ds != null ? ds.getPlayerId() : 0);
        ball.setNextPlayerAction(os != null ? os.getSkill().getSubAction() : EActionType.NULL);
        Skill skill = rs.getSkillActionTeam(false);
        //增加技能触发方士气
        if (skill.attackSkill()) {
            ball.updateMorale(10);
            if (os != null && ds != null) {//克制，输的一方扣10士气
                otherBall.updateMorale(-10);
            }
        } else {
            otherBall.updateMorale(10);
            if (os != null && ds != null) {//克制，输的一方扣10士气
                ball.updateMorale(-10);
            }
        }

        report.addSubAct(rs.getSkillTeamId(), 0, EActionType.skill,
            skill.getPlayerId(),
            skill.getSkill().getSkillId(),
            skill.getExecuteLevel(), false);
        //标记该技能已经执行，在逻辑结尾，初始化回合技能的所有数据
        rs.setStat(RoundSkill.STATUS_START);
        BattleActionBean ba = bs.getInfo().getActions().getActionBean(skill.getSkill().getAction());
        if (log.isDebugEnabled()) {
            log.debug("btmain getActionBySkill. bid {} tid {} rstid {} skill {} os {} ds {} ba {}", bs.getId(),
                ball.getTeamId(), rs.getTeamId(), skill, os, ds, ba);
        }
        return ba;
    }

    /** 获取子行为 */
    protected SubAction getSubAction(BaseSubActionBean subActionBean) {
        return SubActionFactory.getAction(subActionBean.getAction());
    }

    /** 重置npc buff */
    private boolean replaceNPCCapBuf(BattleSource bs, BattleTeam team) {
        boolean change = false;
        if (GameSource.isNPC(team.getTeamId())) {//
            Integer capBuf = bs.getAttributeMap(team.getTeamId()).getVal(EBattleAttribute.NPCBUF);
            if (capBuf != null && capBuf != 0) {
                BattleTeamAbility ta = team.getAbility();
                final float ocap = ta.getAttrData(EActionType.ocap);
                final float dcap = ta.getAttrData(EActionType.dcap);
                final int at = Math.round(ocap * capBuf / 100);
                final int df = Math.round(dcap * capBuf / 100);
                ta.getTmpAbility().setAttr(EActionType.ocap, at);
                ta.getTmpAbility().setAttr(EActionType.dcap, df);
                change = true;

                if (log.isDebugEnabled()) {
                    log.debug("btmain npc buff. bs {} tid {} buff {} cap s {} {} temp {} {} f {} {}", bs.getId(),
                        team.getId(), capBuf, ocap, dcap, at, df, ta.getAttrData(EActionType.ocap), ta.getAttrData(EActionType.dcap));
                }
            }

        }
        return change;
    }

    /** 更新技能能量, 上场时间 */
    protected void updateSkillPowerAndMin(BattleSource bs, EBattleStep step) {
        updateSkillPowerAndMin(bs, step, bs.getHome());
        updateSkillPowerAndMin(bs, step, bs.getAway());
    }

    private void updateSkillPowerAndMin(BattleSource bs, EBattleStep step, BattleTeam team) {
        for (BattlePosition bp : team.getLineupPlayers().values()) {
            bs.stats().upSkillPower(bp.getPlayer(), bs.getInfo().getSkillPower().getRoundPower());
            bs.stats().upRtAndStep(bp, step, act(EActionType.min, 1));
        }
    }

    private static ActionVal act(EActionType act, int val) {
        return BaseSubAction.act(act, val);
    }

    private void juesha(BattleSource bs, RoundReport report, EBattleStep nextStep) {
        if (EBattleStep.End != nextStep) {//下轮结束，判断绝杀
            return;
        }
        BattleTeam afterWinTeam = report.getHomeScore() > report
            .getAwayScore() ? bs.getHome() : bs.getAway();
        BattleTeam curWinTeam = bs.getHome().getScore() > bs.getAway().getScore()
            ? bs.getHome() : bs.getAway();
        if (afterWinTeam != curWinTeam) {//分数被反超，假如绝杀行为
            report.addSubAct(curWinTeam.getTeamId(), 0, EActionType.clutch_shot, 0, 0, 0, false);
            bs.stats().setRt(curWinTeam, EActionType.Team_Clutch_Shot, 1);
            bs.stats().setRt(EActionType.Battle_Clutch_Shot, 1);
        }
    }

    /** 技能暴击处理 */
    private void skillSuper(BattleSource bs, BattleTeam ball, BattleTeam otherBall, RoundReport report, EBattleStep step) {
        Skill skill = bs.getRound().getSkill().getSkillActionTeam(false);
        if (skill == null) {
            return;
        }
        int skillScoreRate = skill.getExecuteLevel();
        if (skillScoreRate > 1) {//只对大于1的暴击，做处理
            BattlePosition bp = ball.getLineupPlayer(ball.getPlayer(ball.getRunPointPlayerId()).getLineupPosition());
            skillScoreRate--;//扣除原本正常逻辑中的得分，剩余的就是暴击所增加的得分
            int skillScore = report.getRoundTotalScore() * skillScoreRate;
            if (bp != null) {
                bs.updateScore(ball, otherBall, bp.getPlayer(), step, skillScore);//球队分数数据 球员得分数据
            }
        }
    }

    /** 小节结束，增加所有技能的有效轮数 */
    private void addActiveBuffRound(BattleSource bs, int round) {
        handleActiveBuff(bs.getHome().getBuffers(), round, buff -> buff.increaseEndRound(1));
        handleActiveBuff(bs.getAway().getBuffers(), round, buff -> buff.increaseEndRound(1));
    }

    /** 触发教练技能 */
    private void pkCoach(BattleSource bs, int round) {
        handleActiveBuff(bs.getHome().getBuffers(), round, buff -> {
            buff.getBuffer().execute(bs.getHome().getTeamId(), bs);
        });
        handleActiveBuff(bs.getAway().getBuffers(), round, buff -> {
            buff.getBuffer().execute(bs.getAway().getTeamId(), bs);
        });
    }

    private void handleActiveBuff(List<BattleBuffer> buffers, int round, Consumer<BattleBuffer> consumer) {
        for (BattleBuffer buff : buffers) {
            if (buff == null || !buff.active(round)) {
                continue;
            }
            consumer.accept(buff);
        }
    }

    /** 计算连续得分增加的士气 */
    private void updateStreakScoreMorale(BattleTeam team, RoundReport report) {
        if (report.getRoundScoreLastTeamId() != team.getTeamId() || !report.hasRoundTotalScore()) {
            return;
        }
        if (team.getRunPoint() >= 17) {
            team.updateMorale(10);
        } else if (team.getRunPoint() >= 8) {
            team.updateMorale(5);
        }
    }

    /** player Power log */
    private static final class PowerLog {
        private static final Logger log = LoggerFactory.getLogger(BaseBattleHandle.class.getCanonicalName() +
            "." + PowerLog.class.getSimpleName());
    }

    /** 重新计算体力,重新计算攻防,重新计算双方行为类型命中概率 */
    private void recalcPowerAndCap(GlobalBean gb, BattleSource bs, BattleTeam ball, BattleTeam other) {
        recalcPlayerPower(bs, ball, TacticType.Offense); //重新计算体力
        recalcPlayerPower(bs, other, TacticType.Defense);
        //重新计算攻防
        //        boolean attack = bs.getReport().getBallTeamId() == bs.getHome().getTeamId();
        bs.getHome().recalcPlayerAbility();
        bs.getAway().recalcPlayerAbility();
        replaceNPCCapBuf(bs, bs.getHome());//重置npc buff
        replaceNPCCapBuf(bs, bs.getAway());
        //重新计算双方行为类型命中概率
        recalcBattleActionWeight(gb, bs);
    }

    /** 重新计算体力 */
    private void recalcPlayerPower(BattleSource bs, BattleTeam bt, TacticType et) {
        if (bt == null) {
            return;
        }
        TacticsBean tb = bt.getPkTactics(et).getTactics();
        for (BattlePosition bp : bt.getLineupPlayers().values()) { //体力每回合扣除(战术)
            BattlePlayer pr = bp.getPlayer();
            float val = pr.calcPower(-tb.getPower(bp.getPosition()) * bs.getInfo().getPlayerRoundPower().getLineupPower());
            float src = pr.getPower();
            float fv = bs.stats().upPower(pr, val);
            if (PowerLog.log.isTraceEnabled()) {
                PowerLog.log.trace("btmain prpower sub. bid {} tid {} prid {} power {} up {} f {}",
                    bs.getId(), bt.getTeamId(), pr.getPlayerId(), src, val, fv);
            }
        }

        for (BattlePlayer pr : bt.getPlayers()) { //替补球员每回合体力增加
            if (pr.getLineupPosition() == EPlayerPosition.NULL) {
                float val = pr.calcPower(bs.getInfo().getPlayerRoundPower().getSubPower());
                float src = pr.getPower();
                float fv = bs.stats().upPower(pr, val);
                if (PowerLog.log.isTraceEnabled()) {
                    PowerLog.log.trace("btmain prpower up. bid {} tid {} prid {} power {} up {} f {}",
                        bs.getId(), bt.getTeamId(), pr.getPlayerId(), src, val, fv);
                }
            }
        }
    }

    /** act Weight log */
    private static final class WeightLog {
        private static final Logger log = LoggerFactory.getLogger(BaseBattleHandle.class.getCanonicalName() +
            "." + WeightLog.class.getSimpleName());
    }

    /** 重新计算双方行为类型权重 */
    private void recalcBattleActionWeight(GlobalBean gb, BattleSource bs) {
        recalcBattleActionWeight(gb, bs, bs.getHome(), bs.getAway());
        recalcBattleActionWeight(gb, bs, bs.getAway(), bs.getHome());
    }

    /**
     * 重新计算双方行为权重
     *
     * @param oTeam 另外一个球队
     */
    private void recalcBattleActionWeight(GlobalBean gb, BattleSource bs, BattleTeam team, BattleTeam oTeam) {
        final BattleTactics ot = team.getPkTactics(TacticType.Offense);
        final BattleTactics dt = team.getPkTactics(TacticType.Defense);

        final TacticsCapBean otcb = ot.getTacticsCapBean();
        float otSelfPrCap = 0; //进攻方战术位的球员攻防
        float otOtherPrCap = 0; //进攻方战术位的球员对位的防守方球员攻防
        if (otcb != null) {
            for (Entry<EPlayerPosition, Float> e : otcb.getEffectivePosCapRate().entrySet()) {
                EPlayerPosition pos = e.getKey();
                float prcap = getPrCap(team, pos, EActionType.ocap);
                otSelfPrCap += prcap;
                float oprcap = getPrCap(oTeam, pos, EActionType.dcap);
                otOtherPrCap += oprcap;

                if (WeightLog.log.isTraceEnabled()) {
                    WeightLog.log.trace("btmain weight. bid {} pos {} tid {} prid {} cap {} capf {} otid {} prid {} cap {} capf {}",
                        bs.getId(), pos, team.getId(), team.getLineupPrid(pos), prcap, otSelfPrCap,
                        oTeam.getTeamId(), oTeam.getLineupPrid(pos), oprcap, otOtherPrCap);
                }
            }
        }

        final int cap = team.getAbility().getTotalCap();
        final float ocap = oTeam.getAbility().getTotalCap() * 1.0f;

        final float k = cap >= ocap ? ocap / Math.max(1, cap) : cap / Math.max(1, ocap);
        final float capscore = gb.battleActWeightA * k * otSelfPrCap + gb.battleActWeightB * (1 - k) * cap;
        final float ocapscore = gb.battleActWeightA * k * otOtherPrCap + gb.battleActWeightB * (1 - k) * ocap;
        int succWeight = bs.getInfo().getActions().getSucessWeight(ot.getTactics().getZone());
        int failWeight = bs.getInfo().getActions().getFailWeight(dt.getTactics().getZone());
        if (capscore < ocapscore) {
            final float d = (float) Math.pow(capscore * 1.0f / ocapscore * 1.0f, 2);
            succWeight = Math.round(succWeight * d);
            failWeight = 1000 - succWeight;
        }

        if (WeightLog.log.isDebugEnabled()) {
            WeightLog.log.debug("btmain weight. bid {} a {} b {} k {} ot {}" +
                    " tid {} cap {} cappr {} caps {} otid {} cap {} cappr {} caps {} weight succ {} fail {}",
                bs.getId(), gb.battleActWeightA, gb.battleActWeightB, ot.getTactics().getId(),
                k, team.getId(), cap, otSelfPrCap, capscore,
                oTeam.getTeamId(), ocap, otOtherPrCap, ocapscore, succWeight, failWeight);
        }

        team.getAbility().setSuccWeight(succWeight);
        team.getAbility().setFailWeight(failWeight);
        if (bs.getRound().getCurRound() > 20 && team.getStepScoreNow() < 5) {
            team.getAbility().setSuccWeight(100);
            team.getAbility().setFailWeight(1);
        }
    }

    /** 获取指定位置上球员的 进攻和防守 cap */
    private float getPrCap(BattleTeam team, EPlayerPosition pos, EActionType oOrDcap) {
        BattlePosition bp = team.getLineupPlayer(pos);
        if (bp == null) {
            return 0;
        }
        BattlePlayer bpr = bp.getPlayer();
        if (bpr == null) {
            return 0;
        }
        return bpr.getAbility().get(oOrDcap);
    }

    /** 本论小节是否发生变化. true 发生变化 */
    private boolean stepCanChange(BattleSource bs, EBattleStep currRoundStep) {
        EBattleStep cur = bs.getCurStep();
        if (cur == EBattleStep.End) {
            return false;
        }
        if (EBattleStep.Start == currRoundStep) {
            return true;
        }
        //加时时效内，未出结果，额外特殊处理加时
        if (cur == EBattleStep.Overtime && currRoundStep == EBattleStep.End &&
            bs.getHome().getScore() == bs.getAway().getScore()) {
            return false;
        }
        return currRoundStep != cur;
    }

    private void nextStep(BattleSource bs) {
        EBattleStep cur = bs.getCurStep();
        EBattleStep nextStep = next(cur);
        if (isOvertime(bs, nextStep)) {//首次修改状态时判断是否需要进入加时，如果不需要直接将状态修改为结束
            nextStep = EBattleStep.End;
        }
        bs.getRound().setCurStep(nextStep);//修改比赛当前小节
        //更新临时比赛报表中小节数据
        bs.getReport().nextStep(nextStep);
        bs.getHome().updateStepScore(cur);
        bs.getAway().updateStepScore(cur);
        log.info("btmain upstep. bid {} step curr {} next {} delay {} stepscore {}:{}", bs.getId(), cur, nextStep,
            bs.getRound().getRoundDelay(), bs.getHome().getStepScore(cur), bs.getAway().getStepScore(cur));
        if (bs.getRound().getCurStep() == EBattleStep.Overtime) {
            bs.getOrCreateAttributeMap(0).addVal(EBattleAttribute.加时, 1);
        }
    }

    private EBattleStep getNextStep(BattleSource bs) {
        EBattleStep cur = bs.getCurStep();
        BattleStepBean currsb = bs.getStepConfig().getBattleStep(cur);
        if (bs.getRound().getCurRound() + 1 <= currsb.getRoundInterval().getUpper()) {
            return cur;
        }
        EBattleStep nextStep = next(cur);
        if (isOvertime(bs, nextStep)) {//首次修改状态时判断是否需要进入加时，如果不需要直接将状态修改为结束
            nextStep = EBattleStep.End;
        }
        return nextStep;
    }

    private boolean isOvertime(BattleSource bs, EBattleStep nextStep) {
        return nextStep == EBattleStep.Overtime &&
            bs.getHome().getScore() != bs.getAway().getScore();
    }

    private void updateActionAndStepScoreWhenStepChange(BattleSource bs, RoundReport report, EBattleStep currRoundNewStep) {
        EBattleAction act = BattleConsole.getActionBySteps(currRoundNewStep);
        BattleActionBean roundStepAction = bs.getInfo().getActions().getActionBean(act, EBattleAction.NULL);
        if (currRoundNewStep == EBattleStep.First_Period) {//report
            report.addSubAct(report.getNextBall(), 0, EActionType.Change_Possession, 0, 0, 0, false);
        }
        report.setAction(roundStepAction.getAction());
        bs.getRound().next();
    }

    private static EBattleStep next(EBattleStep step) {
        switch (step) {
            case Start: return EBattleStep.First_Period;
            case First_Period: return EBattleStep.Second_Period;
            case Second_Period: return EBattleStep.Thrid_Period;
            case Thrid_Period: return EBattleStep.Fourth_Period;
            case Fourth_Period: return EBattleStep.Overtime;
            case Overtime: return EBattleStep.End;
            default: return EBattleStep.End;
        }
    }

    /**
     * 初始化结束报表
     */
    protected synchronized EndReport initEndReport() {
        ///获得获胜和失败的球队
        BattleTeam win = winTeam();
        BattleSource bs = getBattleSource();
        BattleTeam loss = bs.getHome().getTeamId() == win.getTeamId() ? bs.getAway() : bs.getHome();
        loss.setWin(false);
        win.setWin(true);

        boolean enableTacticExp = bs.getInfo().getBattleBean().isEnableEndTacticExp();
        List<PropSimple> winGifts = enableTacticExp ? getWinBaseAwardProps(bs, win, loss) : new ArrayList<>();
        List<PropSimple> lossGifts = enableTacticExp ? getLoseBaseAwardProps(bs, win, loss) : new ArrayList<>();
        addConfigAwardProps(bs, winGifts, lossGifts);
        return createReport(bs, win, winGifts, lossGifts);
    }

    protected final EndReport createReport(BattleSource bs, BattleTeam win,
                                           List<PropSimple> winGifts, List<PropSimple> lossGifts) {
        return new EndReport(bs.getId(), bs.getType(), bs.getHome(), bs.getAway(),
            winGifts, lossGifts, win.getTeamId());
    }

    /** 获取比赛结束时策划配置奖励 */
    protected static void addConfigAwardProps(BattleSource bs, List<PropSimple> winGifts, List<PropSimple> loseGifts) {
        BattleBean bb = bs.getInfo().getBattleBean();
        winGifts.addAll(getConfigAwardProps(bb.getWinDrop(), bb.getWinProps()));
        loseGifts.addAll(getConfigAwardProps(bb.getLoseDrop(), bb.getLoseProps()));
    }

    /** 获取动态失败时的奖励 */
    private List<PropSimple> getLoseBaseAwardProps(BattleSource bs, BattleTeam win, BattleTeam loss) {
        int exp = getLossExp(win, loss);
        int tactics = getLossTactics(win, loss);
        return getBaseAwardProps(bs.getInfo().getLossDrop(), exp, tactics);
    }

    /** 获取动态胜利时的奖励 */
    private List<PropSimple> getWinBaseAwardProps(BattleSource bs, BattleTeam win, BattleTeam loss) {
        int tactics = getWinTactics(win, loss);
        int exp = getWinExp(win, loss);
        return getBaseAwardProps(bs.getInfo().getWinDrop(), exp, tactics);
    }

    /** 获取动态奖励 */
    private List<PropSimple> getBaseAwardProps(DropBean db, int exp, int tactics) {
        //				lossGifts.add(new PropSimple(4001,lossGlod));
        List<PropSimple> props = Lists.newArrayList();
        if (exp > 0) {
            props.add(new PropSimple(4002, exp));
        }
        if (tactics > 0) {
            props.add(new PropSimple(1201, tactics));
        }
        if (db != null) {
            props.addAll(db.roll());
        }
        return props;
    }

    /** 获取比赛结束时策划配置奖励 */
    private static List<PropSimple> getConfigAwardProps(String dropIds, List<PropSimple> propsCfg) {
        List<PropSimple> props = new ArrayList<>();
        List<Integer> list = BattleBean.getDropIds(dropIds);
        for (Integer dropId : list) {
            DropBean db = DropConsole.getDrop(dropId);
            if (db != null) {
                props.addAll(db.roll());
            }
        }
        if (propsCfg != null && !propsCfg.isEmpty()) {
            props.addAll(propsCfg);
        }
        return props;
    }

    protected int getWinTactics(BattleTeam win, BattleTeam loss) {
        return Math.round(win.getLevel() * ConfigConsole.getGlobal().Main_Win_Tactics);//修改成3, 2018-8-23 17:07:35
    }

    protected int getLossTactics(BattleTeam win, BattleTeam loss) {
        return Math.round(win.getLevel() * ConfigConsole.getGlobal().Main_Lost_Tactics);//修改成2, 2018-8-23 17:07:35
    }

    protected int getWinExp(BattleTeam win, BattleTeam loss) {
        return 0;
    }

    protected int getLossExp(BattleTeam win, BattleTeam loss) {
        return 0;
    }

    public BattleTeam winTeam() {
        BattleSource bs = this.battleSource;
        return bs.getHomeScore() > bs.getAwayScore() ? bs.getHome() : bs.getAway();
    }

    /** 布置道具 */
    @Override
    public synchronized void changeProp(long teamId, List<PropSimple> props) {
        //只有赛前阶段才可以
        BattleSource bs = this.battleSource;
        if (bs.getStage() != EBattleStage.Before) {
            return;
        }
        //        bs.getTeam(teamId).addProp(props);
        resumeByClientAct(bs);
    }

    /** 布置战术 */
    @Override
    public synchronized void changeTactis(long teamId, List<TacticsSimple> tacticsList) {
        //只有赛前阶段才可以布置战术
        BattleSource bs = this.battleSource;
        if (bs.getStage() != EBattleStage.Before) {
            return;
        }
        BattleTeam team = bs.getTeam(teamId);
        if (!addTactic(team, tacticsList)) {
            return;
        }
        resumeByClientAct(bs);
    }

    private static boolean addTactic(BattleTeam bt, List<TacticsSimple> addtactics) {
        List<BattleTactics> tacts = addtactics.stream()
            .map(tac -> new BattleTactics(TacticsConsole.getBean(tac.getTid()), tac.getLevel())).collect(Collectors.toList());
        boolean jg = tacts.stream().anyMatch(tac -> tac.getTactics().getId() == bt.getPkTactics(TacticType.Offense).getTactics().getId());
        boolean fs = tacts.stream().anyMatch(tac -> tac.getTactics().getId() == bt.getPkTactics(TacticType.Defense).getTactics().getId());
        if (jg && fs) {//必须携带默认战术
            if (tacts.stream().filter(tac -> tac.getTactics().getType() == TacticType.Offense).count() > 5) {
                return false;
            }
            if (tacts.stream().filter(tac -> tac.getTactics().getType() == TacticType.Defense).count() > 5) {
                return false;
            }
            bt.replaceAllTactics(tacts);
        }
        return true;
    }

    @Override
    public synchronized void changePlayerPos(long teamId, int aid, int bid) {
        BattleSource bs = this.battleSource;
        if (bs.getStage() != EBattleStage.Before) {
            return;
        }
        BattleTeam team = bs.getTeam(teamId);
        BattleTeam other = bs.getOtherTeam(team.getTeamId());
        team.updatePlayerPosition(aid, bid, false,
            other.getPkTactics(TacticType.Offense), other.getPkTactics(TacticType.Defense));
        resumeByClientAct(bs);
    }

    /** 战斗中使用道具 */
    @Override
    public synchronized ErrorCode useProp(long teamId, int pid, int tagerPlayer, int otherPlayer) {
        BattleSource bs = this.battleSource;
        resumeByClientAct(bs);
        return ErrorCode.Fail;
    }

    /** 战斗中修改球员位置 */
    @Override
    public synchronized ErrorCode updatePlayerPosition(long teamId, String targetPlayerStr) {
        List<Integer> targetPrids = Arrays.asList(StringUtil.toIntegerArray(targetPlayerStr, StringUtil.DEFAULT_ST));
        return updatePlayerPosition(battleSource, teamId, EPlayerPosition.values(), targetPrids, false);
    }

    /**
     * 战斗中修改球员位置
     *
     * @param lpPos       场上要下场的球员位置, 按下标换人
     * @param targetPrids 场下要上场的球员id列表, 按下标换人
     */
    public static synchronized ErrorCode updatePlayerPosition(BattleSource bs, long teamId,
                                                              EPlayerPosition[] lpPos, List<Integer> targetPrids,
                                                              boolean ignoreCD) {
        if (log.isDebugEnabled()) {
            log.debug("btmain uppos. bid {} tid {} lppos {} targetPrids {} ignorecd {}", bs.getId(), teamId,
                lpPos, targetPrids, ignoreCD);
        }
        if (bs.getStage() != EBattleStage.PK) {
            return ErrorCode.Battle_1;
        }
        BattleTeam bt = bs.getTeam(teamId);
        if (!ignoreCD && (!bt.getStat().isCanSubPlayer() || bt.getStat().getReplacePlayerCD() > 0)) {
            return ErrorCode.Battle_UpPos_Cd;
        }
        int upposMaxPf = ConfigConsole.global().battleUpPosMaxPf;
        BattleTeam other = bs.getOtherTeam(bt.getTeamId());
        for (int posId = 0; posId < Math.min(lpPos.length, targetPrids.size()); posId++) {
            EPlayerPosition position = lpPos[posId];
            if (position == EPlayerPosition.NULL) {
                continue;
            }
            BattlePosition lpPr = bt.getLineupPlayer(position);//场上球员
            BattlePlayer pr = lpPr.getPlayer();
            if (!ignoreCD && pr.getRealTimeActionStats().getIntValue(EActionType.pf) >= upposMaxPf) {
                return ErrorCode.Battle_UpPos_Pf;
            }
            Integer targetPrid = targetPrids.get(posId);
            if (pr.getPlayerId() == targetPrid) {
                continue;
            }
            bs.getReport().addSubAct(teamId, 0, EActionType.substitute, pr.getPlayerId(), targetPrid, 0, false);
            bt.updatePlayerPosition(pr.getPlayerId(), targetPrid, true,
                other.getPkTactics(TacticType.Offense), other.getPkTactics(TacticType.Defense));
        }
        bs.stats().upRtAndStep(bt, EActionType.substitute, 1);
        bt.getStat().updateReplacePlayerCD();
        resumeByClientAct(bs);
        return ErrorCode.Success;
    }

    /** 战斗中修改战术 {@link TacticsAction#updateTactic(BattleSource, long, TacticsBean, TacticsBean)} */
    @Override
    public final synchronized ErrorCode updateTactics(long teamId, int atid, int dtid) {
        BattleSource bs = battleSource;
        if (bs.getStage() != EBattleStage.PK) {
            return ErrorCode.Battle_Stage_Pk;//比赛当前阶段不是PK阶段
        }
        BattleTeam team = bs.getTeam(teamId);
        if (!team.getStat().isCanUseTactics() || team.getStat().getReplaceTacticCD() > 0) {
            return ErrorCode.Battle_Up_Tactics_CD;//比赛当前被封印了战术更换操作
        }
        BattleTactics srcot = team.getPkTactics(TacticType.Offense);
        TacticId otid = TacticId.convert(atid);
        TacticsBean otb = TacticsConsole.getBean(otid);
        TacticsBean dtb = TacticsConsole.getBean(TacticId.convert(dtid));
        if (otb.getType() != TacticType.Offense || dtb.getType() != TacticType.Defense) {
            return ErrorCode.Battle_Up_Tactics_Type;//比赛中更换的战术类型不是对应的进攻或防守战术
        }
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(bs.getType().getId());
        if (bean != null && bean.getType() == 2) {//奖杯超快赛
            int fast_cup_tactics_Count = ConfigConsole.getGlobal().fast_cup_tactics_Count;
            if (team.getUpdateTacticsNum() >= fast_cup_tactics_Count) {
                return ErrorCode.Battle_update_Tactic_num_limit;//本场比赛更换战术次数已用完
            }
        }
        BattleTactics ot = team.getTactic(otb.getId());
        BattleTactics dt = team.getTactic(dtb.getId());
        log.debug("btmain uptactics. bid {} tid {} atid {} dtid {} ot {} dt {}",
            bs.getId(), teamId, atid, dtid, ot != null, dt != null);
        if (ot == null || dt == null) {
            return ErrorCode.Battle_Up_Tactics_Null;
        }

        BattleTeam other = bs.getOtherTeam(team.getTeamId());
        BattleTactics oot = other.getPkTactics(TacticType.Offense);
        BattleTactics odt = other.getPkTactics(TacticType.Defense);
        team.updateTactics(ot, dt, oot, odt);
        other.updateTactics(oot, odt, ot, dt);
        bs.getHome().recalcPlayerAbility();
        bs.getAway().recalcPlayerAbility();

        bs.getReport().addSubAct(teamId, 0, EActionType.change_tactics, atid, dtid, 0, false);
        bs.stats().upRtAndStep(team, EActionType.change_tactics, 1);
        team.getStat().updateTacticCdAndNum();
        resumeByClientAct(bs);
        team.addUpdateTacticsNum();
        if (srcot != null && srcot.getTactics().getId() != otid /** && 其他条件 */) {//进攻战术更换 
            team.setForceActionRound(MatchConst.forceActionRound);//从本回合开始需要强制选择球员的回合数. 配置读取计算
            TacticsCapBean tacticsCapBean = ot.getTacticsCapBean();
            team.setForcePos(new ArrayList<>(tacticsCapBean.getEffectivePosCapRate().keySet()));//进攻战术影响的阵容位置. 根据配置读取计算出来
            team.setForcePosActions(ImmutableSet.of(EActionType._3p, EActionType.pts, EActionType.tip_in, EActionType._2p_missed));//需要强制选择位置的子行为 SubActionFactory
        }
        return ErrorCode.Success;
    }

    @Override
    public final synchronized ErrorCode usePlayerSkill(long teamId, int playerId, boolean attack) {
        BattleSource bs = this.battleSource;
        BattleTeam team = bs.getTeam(teamId);
        BattlePlayer player = team.getPlayer(playerId);
        if (player == null) {
            return ErrorCode.Player_Null;
        }
        ErrorCode ret = PlayerSkillAction.pkUseSkill(bs, teamId, player, attack);
        if (ret.isError()) {
            return ret;
        }
        resumeByClientAct(bs);
        return ErrorCode.Success;
    }

    @Override
    public final synchronized Tuple2<ErrorCode, List<BattleBuffer>> useCoachSkill(long teamId, int skillId) {
        return CoachSkillAction.useCoach(battleSource, teamId, skillId, false);
    }

    @Override
    public synchronized void pause(boolean pause) {
        BattleSource bs = this.battleSource;
        if (pause) {
            bs.getRound().setPause(true);
        } else {
            bs.getRound().setPause(false);
            //            bs.getRound().setForceRun();
        }
    }

    private static void resumeByClientAct(BattleSource bs) {
        if (ResumeType.client_act.equals(bs.getRound().getResumeType())) {
            bs.getRound().setPause(false);
        }
    }

    private static final class EndLog {
        private static final Logger log = LoggerFactory.getLogger(BaseBattleHandle.class.getCanonicalName() +
            "." + EndLog.class.getSimpleName());
    }

    /** 快速结束比赛 */
    @Override
    public final synchronized void quickEnd() {
        BattleSource bs = getBattleSource();
        if (bs.getRound().getStage() != EBattleStage.PK) {
            bs.updateStage(EBattleStage.PK);
            bs.getInfo().setStartTime(System.currentTimeMillis());
        }
        pause(false);
        bs.getInfo().setDisablePushMessage(true);
        bs.getRound().getSkill().clear();
        EndLog.log.debug("quick end. bid {} htid {} atid {}", bs.getId(), bs.getHomeTid(), bs.getAwayTid());
        for (int i = 0; i < 2000; i++) {
            bs.getRound().quickRun();
            pk0();
            quickEndLog(bs);
            if (bs.getReport() != null) {
                bs.getReport().clear();
            }
            if (bs.getRound().getStage() == EBattleStage.End) {
                break;
            }
        }
    }

    private void quickEndLog(BattleSource bs) {
        if (EndLog.log.isDebugEnabled()) {
            RoundReport report = bs.getReport();
            if (report == null) {
                return;
            }
            BattleTeam home = bs.getHome();
            BattleTeam away = bs.getAway();
            EBattleStep step = report.getStep();
            EndLog.log.debug("quick round. bid {} htid {} atid {} round {} step {} score {}:{} gap {}" +
                    " act {} subactn {} hints {} home {} away {}",
                bs.getId(), home.getTeamId(), away.getTeamId(), bs.getRound().getCurRound(), step.getStepName(),
                home.getScore(), away.getScore(), Math.abs(home.getScore() - away.getScore()),
                report.getAction().name(), report.getActionReportQueue().size(),
                report.getHints().size(), report.getHome(), report.getAway());
        }
    }

    /**
     * 强制结束比赛
     *
     * @param forceHomeWin 是否强制主场获胜
     */
    public synchronized void forceEnd(boolean forceHomeWin,BaseBattleHandle bbh) {
        BattleSource bs = getBattleSource();
        bs.getInfo().setDisablePushMessage(true);
        EndLog.log.debug("forceEnd end. bid {} htid {} atid {}", bs.getId(), bs.getHomeTid(), bs.getAwayTid());
        bs.getRound().getSkill().clear();
        for (int i = 0; i < 2000; i++) {
            bs.getRound().quickRun();
            if (bs.getCurStep() == EBattleStep.End && forceHomeWin) {//结束，生成结束报表
                //                this.battleSource.endBattle(initEndReport());//生成结束报表
                int hs = bs.getHome().getScore();
                int as = bs.getAway().getScore();
                if (hs < as) {
                    bs.getHome().updateScore(as - hs + 1);
                }
            }
            pk0();
            quickEndLog(bs);
            if (bs.getReport() != null) {
                bs.getReport().clear();
            }
            if (bs.getRound().getStage() == EBattleStage.End) {
                break;
            }
        }
        BattleAPI.endMatch(bs, bbh);
    }

    private static EBattleStep getStepByRound(final int round, BattleStep battleStep) {
        BattleStepBean stepBean = battleStep.getStepByRound(round);
        return stepBean != null ? stepBean.getStep() : EBattleStep.End;
    }

    private ActionPlayerHandle createActionPlayerHandle() {
        return new DefaultActionPlayerHandle();
    }

    private ActionPlayerHandle actPlayerHandle() {
        return actionPlayerHandle;
    }

    @Override
    public BattleSource getBattleSource() {
        return battleSource;
    }

    public void setEnd(BattleEnd end) {
        this.end = end;
    }

    @Override
    public BattleEnd getEnd() {
        return end;
    }

    public void setRound(BattleRoundReport round) {
        this.round = round;
    }

    @Override
    public BattleRoundReport getRound() {
        return this.round;
    }

    public BattleStartPush getStartPush() {
        return startPush;
    }

    public void setStartPush(BattleStartPush startPush) {
        this.startPush = startPush;
    }
}
