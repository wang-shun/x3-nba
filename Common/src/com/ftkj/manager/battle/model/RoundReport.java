package com.ftkj.manager.battle.model;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleAction;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.proto.BattlePB.BattleHintData;

import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * @author tim.huang
 * 2017年2月22日
 * 比赛回合报表，存放回合数据。
 */
public class RoundReport implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(RoundReport.class);
    private static final long serialVersionUID = -8698749894652667280L;
    /** 当前球权的球队ID */
    private long ballTeamId;
    /** 下一回合球权方 */
    private long nextBall;
    /** 父行为 */
    private EBattleAction action;
    /** 进攻球员ID */
    private BattlePosition offensePlayer;
    /** 小节回合数，小节切换时清空 */
    private int roundOfStep;
    /** 当前小节 */
    private EBattleStep step;
    /** 回合总得分 */
    private int roundTotalScore;
    /** 回合最后得分的球队 */
    private long roundScoreLastTeamId;
    /** 比赛是否已经结束 */
    private boolean end;
    /** 主场 */
    private final ReportTeam home;
    /** 客场 */
    private final ReportTeam away;
    /** 行为播报队列 */
    private Queue<ActionReport> actionReportQueue;
    /** 比赛提示配置及信息 */
    private final BattleHint hintCfg;
    /** 计算好比赛提示, 每回合清除 */
    private List<BattleHintData> hints;

    public RoundReport(BattleHint hintCfg) {
        super();
        this.ballTeamId = 0;
        this.action = null;
        this.offensePlayer = null;
        this.roundOfStep = 1;
        this.step = EBattleStep.Start;
        this.nextBall = 0;
        this.roundTotalScore = 0;
        this.roundScoreLastTeamId = 0L;
        this.end = false;
        home = new ReportTeam();
        away = new ReportTeam();
        this.hintCfg = hintCfg;
        this.actionReportQueue = Queues.newConcurrentLinkedQueue();
    }

    public boolean hasRoundTotalScore() {
        return roundTotalScore > 0;
    }

    public void addRoundScore(long teamId, int score) {
        roundTotalScore += score;
        roundScoreLastTeamId = teamId;
    }

    public int getRoundTotalScore() {
        return roundTotalScore;
    }

    public long getRoundScoreLastTeamId() {
        return roundScoreLastTeamId;
    }

    public void nextRound(BattleTeamAbility home, BattleTeamAbility away) {
        this.action = null;
        this.ballTeamId = this.nextBall;
        initTeamCap(home, away);
        this.offensePlayer = null;
        this.roundOfStep++;
        this.roundTotalScore = 0;
        this.roundScoreLastTeamId = 0L;
        if (hints != null && !hints.isEmpty()) {
            this.hints = Collections.emptyList();
        }
    }

    public void initTeamCap(BattleTeamAbility home, BattleTeamAbility away) {
        initTeamCap(this.home, home);
        initTeamCap(this.away, away);
        //        log.debug("report team cap. home {} away {} trace {}", this.home, this.away, StringUtil.printlnStackTrace());
    }

    public static void initTeamCap(ReportTeam rt, BattleTeamAbility bta) {
        initTeamCap(rt,
            Math.round(bta.getViewCap(EActionType.ocap)),
            Math.round(bta.getViewCap(EActionType.dcap)));
    }

    private static void initTeamCap(ReportTeam rt, int ocap, int dcap) {
        rt.setOffenseCap(ocap);
        rt.setDefenseCap(dcap);
    }

    public void nextStep(EBattleStep step) {
        this.step = step;
        this.roundOfStep = 0;
    }

    public void clear() {
        this.actionReportQueue.clear();
    }

    public BattlePosition getOffensePlayer() {
        return offensePlayer;
    }

    public void setOffensePlayer(BattlePosition offensePlayer) {
        this.offensePlayer = offensePlayer;
    }

    public Queue<ActionReport> getActionReportQueue() {
        return actionReportQueue;
    }

    /**
     * 添加子行为
     *
     * @param playerId 执行子行为的球员
     * @param act      子行为
     * @param v1       关联参数1
     * @param v2       关联参数2
     * @param v3       关联参数3
     */
    public void addSubAct(long teamId, int playerId, EActionType act, int v1, int v2, int v3,boolean isForce) {
        ActionReport ar = new ActionReport(teamId, playerId, act, v1, v2, v3,isForce);
        this.actionReportQueue.offer(ar);
        if (log.isDebugEnabled()) {
            log.debug("btreport addact. btid {} actr {}", ballTeamId, ar);
        }
        if (act == EActionType.update_power) {
            return;
        }
        addHintActionType(act);
    }

    /** 添加提示类型 */
    public void addHintActionType(EActionType actionType) {
        if (actionType.isDisableAct() || hintCfg.isAllHintsTriggered()) {
            return;
        }
        hintCfg.addActionTrigger(actionType);
    }

    public BattleHint getHintCfg() {
        return hintCfg;
    }

    public void setHints(List<BattleHintData> hints) {
        this.hints = hints;
    }

    public List<BattleHintData> getHints() {
        return hints == null ? Collections.emptyList() : hints;
    }

    public void setAction(EBattleAction action) {
        this.action = action;
    }

    public EBattleAction getAction() {
        return action;
    }

    public void updateScore(int homeScore, int awayScore) {
        home.setScore(homeScore);
        away.setScore(awayScore);
    }

    public boolean isEnd() {
        return end;
    }

    /** 比赛结束 */
    public void end() {
        this.end = true;
    }

    public int getHomeScore() {
        return home.getScore();
    }

    public int getAwayScore() {
        return away.getScore();
    }

    public int getRoundOfStep() {
        return roundOfStep;
    }

    public void setRoundOfStep(int roundOfStep) {
        this.roundOfStep = roundOfStep;
    }

    public ReportTeam getHome() {
        return home;
    }

    public ReportTeam getAway() {
        return away;
    }

    public EBattleStep getStep() {
        return step;
    }

    public long getBallTeamId() {
        return ballTeamId;
    }

    public void setBallTeamId(long ballTeamId) {
        this.ballTeamId = ballTeamId;
    }

    public long getNextBall() {
        return nextBall;
    }

    public void setNextBall(long nextBall) {
        this.nextBall = nextBall;
    }

    @Override
    public String toString() {
        return "{" +
            "\"ballTeamId\":" + ballTeamId +
            ", \"action\":" + action +
            ", \"offensePlayer\":" + offensePlayer +
            ", \"roundOfStep\":" + roundOfStep +
            ", \"step\":" + step +
            ", \"nextBall\":" + nextBall +
            ", \"roundTotalScore\":" + roundTotalScore +
            ", \"roundScoreLastTeamId\":" + roundScoreLastTeamId +
            ", \"end\":" + end +
            ", \"home\":" + home +
            ", \"away\":" + away +
            '}';
    }

    public static final class ReportTeam implements Serializable {
        private static final long serialVersionUID = 4179143669379393030L;
        /** 分数 */
        private int score;
        /** 进攻战力 */
        private int offenseCap;
        /** 防守战力 */
        private int defenseCap;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getOffenseCap() {
            return offenseCap;
        }

        public void setOffenseCap(int offenseCap) {
            this.offenseCap = offenseCap;
        }

        public int getDefenseCap() {
            return defenseCap;
        }

        public void setDefenseCap(int defenseCap) {
            this.defenseCap = defenseCap;
        }

        @Override
        public String toString() {
            return "{" +
                "\"score\":" + score +
                ", \"oCap\":" + offenseCap +
                ", \"dCap\":" + defenseCap +
                ", \"cap\":" + (offenseCap + defenseCap) +
                '}';
        }
    }

}
