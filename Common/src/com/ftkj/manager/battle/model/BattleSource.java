package com.ftkj.manager.battle.model;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.cfg.battle.HomeAway;
import com.ftkj.console.BattleConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.TacticType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.battle.BattleStep;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.server.GameSource;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年2月16日
 * 比赛数据
 */
public class BattleSource implements Serializable {
    private static final long serialVersionUID = -6448121699647452896L;
    /** 比赛详细信息 */
    private BattleInfo battleInfo;
    /** 主场球队 */
    private BattleTeam home;
    /** 客场球队 */
    private BattleTeam away;
    /** 比赛回合数据 */
    private Round round;
    /** 比赛小节配置 */
    private BattleStep stepConfig;
    
    
    /** 比赛回合报表 */
    private RoundReport report;
    /** 结束报表 */
    private EndReport endReport;
    /**  */
    private boolean cross;
    /** 比赛跨服传参 */
    private Map<Long, BattleAttribute> attributeMap;
    /** 行为统计信息 */
    private final Stats stats = new Stats();

    public BattleSource(long battleId, EBattleType battleType, BattleStep stepConfig,
                        int speed, DropBean winDrop, DropBean lossDrop, long homeTeamId) {
        this(battleId, battleType, stepConfig, speed, winDrop, lossDrop, false, homeTeamId);
    }

    public BattleSource(long battleId, EBattleType battleType, BattleStep stepConfig,
                        int roundSpeedMod, DropBean winDrop, DropBean lossDrop, boolean cross, long homeTeamId) {
        BattleBean bb = BattleConsole.getBattleOrDefault(battleType);
        this.battleInfo = new BattleInfo(battleId, battleType, bb, winDrop, lossDrop, homeTeamId);
        this.round = new Round(roundSpeedMod);
        this.stepConfig = stepConfig;
        this.report = new RoundReport(new BattleHint(bb.getHints()));
        this.attributeMap = Maps.newHashMap();
        this.cross = cross;
    }

    public long getId() {
        return getBattleId();
    }

    public long getBattleId() {
        return battleInfo.getBattleId();
    }

    public void addTeam(BattleTeam homeTeam, BattleTeam awayTeam) {
        this.home = homeTeam;
        this.away = awayTeam;
        if (this.battleInfo.getHomeTeamId() == 0) {
            this.battleInfo.setHomeTeamId(this.home.getTeamId());
        }

        setCapBuff(homeTeam);
        setCapBuff(awayTeam);

        if (cross) {
            this.home.crossSourceSyn();
            this.away.crossSourceSyn();
        }
    }

    private void setCapBuff(BattleTeam homeTeam) {
        if (GameSource.isNPC(homeTeam.getTeamId())) {
            NPCBean ai = NPCConsole.getNPC(homeTeam.getTeamId());
            if (ai.getCapBuf() != 0) {
                getAttributeMap(homeTeam.getTeamId()).addVal(EBattleAttribute.NPCBUF, ai.getCapBuf());
            }
        }
    }

    public EBattleStep getCurStep() {
        return this.round.getCurStep();
    }

    //    public boolean run() {
    //        boolean result = this.round.run();
    //        return result;
    //    }

    public RoundReport getReport() {
        return report;
    }

    public ReadOnlyActionStats getRtActionStats() {
        return stats.rtActionStats;
    }

    public ReadOnlyStepActionStats getStepActionStats() {
        return stats.stepActionStats;
    }

    public void setBattleStep(BattleStep step) {
        this.stepConfig = step;
    }

    public void setEndBattle(EndReport endReport) {
        this.endReport = endReport;
    }

    /** 获得当前球权的球队 */
    public BattleTeam getBallTeam() {
        long teamId = report.getBallTeamId();
        return this.home.getTeamId() == teamId ? this.home : this.away;
    }

    /** 获得当前防守方的球队 */
    public BattleTeam getBallOtherTeam() {
        long teamId = report.getBallTeamId();
        return home.getTeamId() == teamId ? away : home;
    }

    public void updateStage(EBattleStage stage) {
        this.round.updateStage(stage);
    }

    public BattleTeam getTeam(long teamId) {
        if (home.getTeamId() == teamId) {
            return home;
        } else if (away.getTeamId() == teamId) {
            return away;
        } else {
            return null;
        }
    }

    public BattleTeam getOtherTeam(long tid) {
        if (home.getTeamId() == tid && away.getTeamId() != tid) {
            return away;
        } else if (home.getTeamId() != tid && away.getTeamId() == tid) {
            return home;
        }
        return null;
    }

    public BattleTeam getTeam(HomeAway homeAway) {
        if (homeAway == HomeAway.home) {
            return home;
        } else if (homeAway == HomeAway.away) {
            return away;
        }
        return null;
    }

    public BattleTeam getTeamOrRandom(HomeAway homeAway) {
        if (homeAway == HomeAway.home) {
            return home;
        } else if (homeAway == HomeAway.away) {
            return away;
        } else {
            return randomTeam();
        }
    }

    public BattleTeam randomTeam() {
        return ThreadLocalRandom.current().nextBoolean() ? home : away;
    }

    public EndReport getEndReport() {
        return endReport;
    }

    public BattleStep getStepConfig() {
        return this.stepConfig;
    }

    public BattleTeam getHome() {
        return home;
    }

    public long getHomeTid() {
        return home != null ? home.getTeamId() : 0;
    }

    public int getHomeScore() {
        return home != null ? home.getScore() : 0;
    }

    public BattleTeam getAway() {
        return away;
    }

    public long getAwayTid() {
        return away != null ? away.getTeamId() : 0;
    }

    public int getAwayScore() {
        return away != null ? away.getScore() : 0;
    }

    public EBattleStage getStage() {
        return this.round.getStage();
    }

    /** 比赛已经结束或关闭 */
    public boolean isDone() {
        return round.isDone();
    }

    public BattleInfo getInfo() {
        return battleInfo;
    }

    public EBattleType getType() {
        return battleInfo.getBattleType();
    }

    public Round getRound() {
        return round;
    }

    public void roundOver() {
        round.roundOver();
        stats.rtActionStats.set(EActionType.Round, round.getRunRound());
    }

    public BattleAttribute getAttributeMap(long teamId) {
        BattleAttribute ba = this.attributeMap.get(teamId);
        if (ba == null) {
            ba = new BattleAttribute(teamId);
            this.attributeMap.put(teamId, ba);
        }
        return ba;
    }

    public BattleAttribute getOrCreateAttributeMap(long teamId) {
        return this.attributeMap.computeIfAbsent(teamId, tid -> new BattleAttribute(teamId));
    }

    public void addBattleAttribute(BattleAttribute attribute) {
        this.attributeMap.put(attribute.getTeamId(), attribute);
    }

    public Stats getStats() {
        return stats;
    }

    public Stats stats() {
        return stats;
    }

    public void updateTacticCap() {
        home.updateTactics(home.getPkTactics(TacticType.Offense), home.getPkTactics(TacticType.Defense),
                away.getPkTactics(TacticType.Offense), away.getPkTactics(TacticType.Defense));
        away.updateTactics(away.getPkTactics(TacticType.Offense), away.getPkTactics(TacticType.Defense),
                home.getPkTactics(TacticType.Offense), home.getPkTactics(TacticType.Defense));
    }

    /** 更新得分. 球队分数数据 球员得分数据 */
    public void updateScore(BattleTeam scoreTeam, BattleTeam otherTeam, BattlePlayer pr, EBattleStep step, int addScore) {
        if (addScore == 0) {
            return;
        }
        stats.upRtAndStep(pr, step, new ActionVal(EActionType.pts, addScore));
        scoreTeam.updateRunPoint(addScore);
        scoreTeam.updateScore(addScore);
        report.addRoundScore(scoreTeam.getTeamId(), addScore);
        otherTeam.clearRunPoint();
    }

    /** 统计信息. 更新或设置 比赛/球队/球员 的 实时/小节 统计数据 */
    public final class Stats {
        /**
         * 实时比赛行为统计. 只包括比赛本身级别的统计信息.
         * 球员统计信息在球员 {@link BattlePlayer} 上,
         * 球队统计在球队 {@link BattleTeam} 上,
         * 比赛本身级别的在比赛 {@link BattleSource} 上.
         */
        private final ActionStatistics rtActionStats = new ActionStatistics();
        /**
         * 实时比赛小节行为统计. 只包括比赛本身级别的统计信息.
         * 球员统计信息在球员 {@link BattlePlayer} 上,
         * 球队统计在球队 {@link BattleTeam} 上,
         * 比赛本身级别的在比赛 {@link BattleSource} 上.
         */
        private final StepActionStatistics stepActionStats = new StepActionStatistics();

        //========================================================================
        //=========  更新或设置 比赛/球队/球员 的 实时/小节 统计数据 ================
        //========================================================================

        //=========  更新或设置 比赛 的 实时/小节 统计数据 ================

        /** 更新比赛本身小节数据 */
        public void upStep(EBattleStep step, EActionType act, int val) {
            stepActionStats.sum(step, act, val);
            report.addHintActionType(act);
        }

        /** 更新比赛本身数据 */
        public void upRt(EActionType act, int val) {
            rtActionStats.sum(act, val);
            report.addHintActionType(act);
        }

        /** 更新比赛本身数据 */
        public void upRtAndStep(EBattleStep step, EActionType act, int val) {
            rtActionStats.sum(act, val);
            stepActionStats.sum(step, act, val);
            report.addHintActionType(act);
        }

        /** 设置比赛本身小节数据 */
        public void setStep(EBattleStep step, EActionType act, int val) {
            stepActionStats.set(step, act, val);
            report.addHintActionType(act);
        }

        /** 设置比赛本身小节数据 */
        public void setRtAndStep(EActionType act, int val) {
            stepActionStats.set(getCurStep(), act, val);
            rtActionStats.set(act, val);
            report.addHintActionType(act);
        }

        /** 设置比赛本身数据 */
        public void setRt(EActionType act, int val) {
            rtActionStats.set(act, val);
            report.addHintActionType(act);
        }

        //=========  更新或设置 球队 的 实时/小节 统计数据 ================

        /** 更新球队比赛数据 */
        public void upRt(BattleTeam bt, EActionType act, int val) {
            bt.getRtActionStats().sum(act, val);
            report.addHintActionType(act);
        }

        /** 更新球队比赛数据 */
        public void upRtAndStep(BattleTeam bt, EActionType act, int val) {
            upRtAndStep(bt, getCurStep(), act, val);
        }

        /** 更新球队比赛数据 */
        public void upRtAndStep(BattleTeam bt, EBattleStep step, EActionType act, int val) {
            bt.getRtActionStats().sum(act, val);
            bt.getStepActionStats().sum(step, act, val);
            report.addHintActionType(act);
        }

        /** 设置球队比赛数据 */
        public void setRt(BattleTeam bt, EActionType act, int val) {
            bt.getRtActionStats().set(act, val);
            report.addHintActionType(act);
        }

        /** 设置球队比赛数据 */
        public void setRtAndStep(BattleTeam bt, EActionType act, int val) {
            setRtAndStep(bt, getCurStep(), act, val);
        }

        /** 设置球队比赛数据 */
        public void setRtAndStep(BattleTeam bt, EBattleStep step, EActionType act, int val) {
            bt.getRtActionStats().set(act, val);
            bt.getStepActionStats().set(step, act, val);
            report.addHintActionType(act);
        }

        //=========  更新或设置 球员 的 实时/小节 统计数据 ================

        /** 更新球员小节数据 */
        public void upRtAndStep(BattlePosition bp, ActionVal... actVals) {
            upRtAndStep(bp, getCurStep(), actVals);
        }

        /** 更新球员小节数据 */
        public void upRtAndStep(BattlePosition bp, EBattleStep step, ActionVal... actVals) {
            upRtAndStep(bp.getPlayer(), step, actVals);
        }

        /** 更新球员小节数据 */
        public void upRtAndStep(BattlePlayer pr, ActionVal... actVals) {
            upRtAndStep(pr, getCurStep(), actVals);
        }

        /** 更新球员小节数据 */
        public void upRtAndStep(BattlePlayer pr, EBattleStep step, ActionVal... actVals) {
            if (pr == null) {
                return;
            }
            for (ActionVal av : actVals) {
                upPlayerRtAndStepAttr(pr, step, av);
                report.addHintActionType(av.getAct());
            }
        }

        private void upPlayerRtAndStepAttr(BattlePlayer pr, EBattleStep step, ActionVal av) {
            switch (av.getAct()) {
                case skill_power:
                    int fv = pr.getPlayerSkill().updateSkillPower(av.getValue());//技能能量
                    pr.setRtAndStepActStats(step, av.getAct(), fv);
                    break;
                default:
                    pr.upRtAndStepActionStats(step, av.getAct(), av.getValue());//更新球员实时数据
                    break;
            }
        }

        /** 更新球员小节数据 */
        public void setRtAndStep(BattlePlayer pr, EBattleStep step, ActionVal... actVals) {
            if (pr == null) {
                return;
            }
            for (ActionVal av : actVals) {
                setPlayerRtAndStepAttr(pr, step, av);
                report.addHintActionType(av.getAct());
            }
        }

        public void setPlayerRtAndStepAttr(BattlePlayer pr, EBattleStep step, ActionVal av) {
            switch (av.getAct()) {
                case skill_power:
                    int fv = pr.getPlayerSkill().setSkillPower(av.getValue());//技能能量
                    pr.setRtAndStepActStats(step, av.getAct(), fv);
                    break;
                default:
                    pr.setRtAndStepActStats(step, av.getAct(), av.getValue());//更新球员实时数据
                    break;
            }
        }

        private void upPlayerRtAttr(BattlePlayer pr, ActionVal av) {
            switch (av.getAct()) {
                case skill_power:
                    int fv = pr.getPlayerSkill().updateSkillPower(av.getValue());//技能能量
                    pr.setRtActStats(av.getAct(), fv);
                    break;
                default:
                    pr.upRtActStats(av.getAct(), av.getValue());//更新球员实时数据
                    break;
            }
        }

        /** 技能能量 */
        public void upSkillPower(BattlePlayer pr, int val) {
            if (val == 0) {
                return;
            }
            int fv = pr.getPlayerSkill().updateSkillPower(val);//技能能量
            pr.setRtActStats(EActionType.skill_power, fv);
            report.addHintActionType(EActionType.skill_power);
        }

        /** 技能能量 */
        public void setSkillPower(BattlePlayer pr, int val) {
            int fv = pr.getPlayerSkill().setSkillPower(val);//技能能量
            pr.setRtActStats(EActionType.skill_power, fv);
            report.addHintActionType(EActionType.skill_power);
        }

        /** 更新球员实时数据 */
        public void upRt(BattlePosition bp, ActionVal... actVals) {
            upRt(bp.getPlayer(), actVals);
        }

        /** 更新球员实时数据 */
        public void upRt(BattlePlayer pr, ActionVal... actVals) {
            if (pr == null) {
                return;
            }
            for (ActionVal av : actVals) {
                upPlayerRtAttr(pr, av);
                report.addHintActionType(av.getAct());
            }
        }

        /** 更新球员实时体力 */
        //        public float upPower(long teamId, BattlePosition bp, float val) {
        //            return upPower(teamId, bp.getPlayer(), val);
        //        }

        /** 更新球员实时体力 */
        public float upPower(BattlePlayer pr, float val) {
            if (pr == null || val == 0) {
                return 0;
            }
            float old = pr.getPower();
            float result = pr.setPower(old + val);
            pr.setRtActStats(EActionType.update_power, (int) result);
            addPrPowerHintAction(old, result);
            return result;
        }

        /** 更新球员实时体力 */
        public float setPower(BattlePlayer pr, float val) {
            if (pr == null || val < 0) {
                return 0;
            }
            float old = pr.getPower();
            float result = pr.setPower(val);
            pr.setRtActStats(EActionType.update_power, (int) result);
            addPrPowerHintAction(old, result);
            return result;
        }

        private void addPrPowerHintAction(float old, float result) {
            if (Float.compare(old, result) != 0) {
                report.addHintActionType(EActionType.update_power);
            }
        }
    }

}
