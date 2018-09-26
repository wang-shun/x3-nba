package com.ftkj.manager.battle;

import com.ftkj.cfg.ActionCondition;
import com.ftkj.cfg.battle.BattleHintBean;
import com.ftkj.console.BattleConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.manager.battle.model.BattleHint;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.TeamReport;
import com.ftkj.manager.battle.model.PlayerActStat;
import com.ftkj.manager.battle.model.ReadOnlyActionStats;
import com.ftkj.manager.battle.model.ReadOnlyStepActionStats;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.proto.BattlePB.BattleHintActionData;
import com.ftkj.proto.BattlePB.BattleHintData;
import com.ftkj.util.tuple.Tuple2;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.TextFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** 处理提示. 每回合生成提示信息 */
public class BattleHintHandle {
    private static final Logger log = LoggerFactory.getLogger(BattleHintHandle.class);
    private static final Logger logb = LoggerFactory.getLogger(BaseHintHandleType.class);
    private BattleSource bs;
    private RoundReport report;

    public BattleHintHandle(BattleSource bs, RoundReport report) {
        this.bs = bs;
        this.report = report;
    }

    /** 每回合生成提示信息 */
    public final List<BattleHintData> calcHints() {
        BattleHint cfg = report.getHintCfg();
        if (log.isDebugEnabled()) {
            log.debug("bthint start. bid {} all triggered {}. round acts {} acts {}", bs.getId(),
                    cfg.isAllHintsTriggered(), cfg.getRoundHintActions().size(), cfg.getRoundHintActions());
        }
        if (cfg.isAllHintsTriggered()) {//所有提示已经触发
            return Collections.emptyList();
        }
        if (cfg.getRoundHintActions().isEmpty()) {//没有任何行为触发
            return Collections.emptyList();
        }
        return calcHints0(cfg);
    }

    public void clear() {
        if (report != null) {
            BattleHint cfg = report.getHintCfg();
            cfg.getRoundHintActions().clear();
        }
    }

    private List<BattleHintData> calcHints0(BattleHint cfg) {
        List<BattleHintData> ret = new ArrayList<>();
        //先计算小节开始的提示
        calcStepHint(cfg, ret);
        if (log.isDebugEnabled()) {
            log.debug("bthint end. bid {} htid {} atid {} round {} hints {} {}", bs.getId(),
                    bs.getHome().getTeamId(), bs.getAway().getTeamId(), bs.getRound().getCurRound(), ret.size(),
                    ret.stream().map(TextFormat::shortDebugString).collect(Collectors.joining(", ")));
        }
        return ret;
    }

    private void calcStepHint(BattleHint cfg, List<BattleHintData> ret) {
        EBattleStep currStep = report.getStep();//触发提示的小节节数
        if (currStep == null) {
            return;
        }
        Set<Integer> handledIds = new HashSet<>();
        for (EActionType act : cfg.getRoundHintActions()) {
            ImmutableSet<Integer> ids = cfg.getIds(act);
            if (ids == null) {
                continue;
            }
            for (Integer hid : ids) {
                BattleHintBean bhb = BattleConsole.getHint(hid);
                if (bhb == null || handledIds.contains(hid) || cfg.getTriggeredMaxNums().contains(hid)) {//已经触发过
                    continue;
                }
                handledIds.add(hid);
                boolean stepcomp = bhb.compareStep(currStep);
                log.trace("bthint handle. bid {} hid {} ht {}. st cfg {} {} {} = {}", bs.getId(), hid,
                        bhb.getHintType(), currStep, bhb.getStepOp(), bhb.getStep(), stepcomp);
                if (!stepcomp) {//小节不匹配
                    continue;
                }
                HandleRet triggeredHints = handleHintType(cfg, bhb, ret);
                log.trace("bthint handle. bid {} hid {} ret {}", bs.getId(), hid, triggeredHints);

                if (triggeredHints == HandleRet.Max_Limit) {//完全触发
                    cfg.getTriggeredMaxNums().add(hid);
                    int cfgSize = bs.getInfo().getBattleBean().getHintGroup().size();
                    int tSize = cfg.getTriggeredMaxNums().size();
                    log.debug("bthint handle. bid {} hid {} triggered max num. triggered ids size cfg  {} curr {}",
                            bs.getId(), bhb.getId(), cfgSize, tSize);
                    if (cfgSize == tSize) {
                        cfg.setAllHintsTriggered(true);
                    }
                }//end if
            }//end for hid
        }//end for act
    }

    private enum HandleRet {
        /** 当前提示触发成功 */
        Triggered,
        /** 提示在目标上已经达到最大触发次数, 直接返回 */
        Max_Limit,
        /** 提示条件匹配失败, 没有触发提示 */
        MisMatch,
    }

    /** 是否触发了此条提示. true 触发 */
    private HandleRet handleHintType(BattleHint cfg, BattleHintBean bhb, List<BattleHintData> ret) {
        switch (bhb.getHintType()) {//提示类型
            case Match:
                return new HandleHintMatchType(bs, bhb, cfg, ret).handle();
            case Team:
                return new HandleHintTeamType(bs, bhb, cfg, ret).handle();
            case Player_Any_All:
                return new HandleHintAnyPlayerAllType(bs, bhb, cfg, ret).handle();
            case Player_Any_Num:
                return new HandleHintAnyPlayerNumType(bs, bhb, cfg, ret).handle();
            case Player_Each:
                return new HandleHintEachPlayerNumType(bs, bhb, cfg, ret).handle();
            case Mvp:
                return new HandleHintMvpType(bs, bhb, cfg, ret).handle();
        }
        return HandleRet.MisMatch;
    }

    /** 统计数据 */
    final static class Stats {

        /** 关联的球队id */
        private final long teamId;
        /** 关联的球员id */
        private final int playerId;
        private final ReadOnlyActionStats actionStats;
        private final ReadOnlyStepActionStats stepActionStats;

        Stats(long teamId, int playerId, ReadOnlyActionStats actionStats, ReadOnlyStepActionStats stepActionStats) {
            this.teamId = teamId;
            this.playerId = playerId;
            this.actionStats = actionStats;
            this.stepActionStats = stepActionStats;
        }
    }

    /** 处理和匹配单个提示信息 */
    static abstract class BaseHintHandleType {
        protected final BattleSource bs;
        final BattleHintBean bhb;
        protected final BattleHint cfg;
        protected final List<BattleHintData> ret;

        BaseHintHandleType(BattleSource bs, BattleHintBean bhb, BattleHint cfg, List<BattleHintData> ret) {
            this.bs = bs;
            this.bhb = bhb;
            this.cfg = cfg;
            this.ret = ret;
        }

        /** 处理提示. 短路匹配行为 */
        final boolean matchActions(Stats as) {
            int matchSize = 0;//已经匹配行为的数量
            List<BattleHintActionData> matchActions = new ArrayList<>();

            for (ActionCondition ac : bhb.getConditions().values()) {
                EActionType act = ac.getAct();
                if (!cfg.getRoundHintActions().contains(act)) {//本轮没有此行为触发
                    if (logb.isTraceEnabled()) {
                        logb.trace("bthint act. bid {} hid {} ht {}. tid {} pid {} cond act {} [{}] not triggered in this round. skip.",
                                bs.getId(), bhb.getId(), bhb.getHintType(), as.teamId, as.playerId, act.getType(), act.getConfigName());
                    }
                    break;
                }
                int actCurrVal = getActCurrVal(bhb, as.stepActionStats, as.actionStats, ac);
                if (logb.isTraceEnabled()) {
                    logb.trace("bthint act. bid {} hid {} ht {}. tid {} pid {} cond act {} [{}] val cfg {} op {} v {}. ms {}",
                            bs.getId(), bhb.getId(), bhb.getHintType(), as.teamId, as.playerId, act.getType(),
                            act.getConfigName(), ac.getValue(), ac.getOp().getOp(), actCurrVal, matchSize);
                }
                if (!ac.match(actCurrVal)) {
                    if (isMatchLoopShortBreak()) {
                        break;
                    } else {
                        continue;
                    }
                }
                matchActions.add(hintActionData(ac, actCurrVal));//匹配一个行为
                matchSize++;
            }
            if (matchSize == getMatchSize()) {//满足配置数量, 触发提示
                ret.add(BattleHintData.newBuilder()
                        .addAllActions(matchActions)
                        .setId(bhb.getId())
                        .setTeamId(as.teamId)
                        .setPlayerId(as.playerId)
                        .build());
                return true;
            }
            return false;
        }

        BattleHintActionData hintActionData(ActionCondition ac, int actCurrVal) {
            return BattleHintActionData.newBuilder()
                    .setActionType(ac.getAct().getType())
                    .setV(actCurrVal)
                    .build();
        }

        /**
         * 处理提示
         *
         * @return true 触发了提示
         */
        abstract HandleRet handle();

        /** 要匹配的行为数量 */
        int getMatchSize() {
            return bhb.getConditions().size();
        }

        /** 匹配行为是否快速短路. true 短路匹配行为, false 匹配所有行为 */
        boolean isMatchLoopShortBreak() {
            return true;
        }

        /** 获取行为的当前值 */
        private static int getActCurrVal(BattleHintBean bhb,
                                         ReadOnlyStepActionStats stepAs,
                                         ReadOnlyActionStats rtAs,
                                         ActionCondition ac) {
            return bhb.isMatchAllSteps() ?
                    rtAs.getIntValue(ac.getAct()) :
                    stepAs.getStepActionSum(bhb::compareStep, ac.getAct());
        }

        /**
         * 本条提示是否已经达到配置触发次数
         *
         * @param triggeredNum 在目标上已经触发的次数
         * @return true 已经达到配置触发次数
         */
        boolean isHintTriggered(long id, Map<Integer, Integer> triggeredNum) {
            int tnum = triggeredNum != null ? triggeredNum.getOrDefault(bhb.getId(), 0) : 0;
            boolean ret = bhb.isTriggerLimited() && tnum >= bhb.getTriggerNum();
            logb.debug("bthint istriggered. bid {} hid {} asid {} tnum {} cfgnum {}", bs.getId(), bhb.getId(),
                    id, tnum, bhb.getTriggerNum());
            return ret;
        }
    }

    /** 处理提示 match	比赛行为	比赛行为满足 N 组数据 */
    final static class HandleHintMatchType extends BaseHintHandleType {
        private HandleHintMatchType(BattleSource bs, BattleHintBean bhb, BattleHint cfg, List<BattleHintData> ret) {
            super(bs, bhb, cfg, ret);
        }

        /** @return true 触发了提示 */
        @Override
        public HandleRet handle() {
            if (isHintTriggered(bs.getId(), cfg.getMatchHitNums())) {//已经触发
                return HandleRet.Max_Limit;
            }
            if (bhb.getConditions().isEmpty()) {
                ret.add(BattleHintData.newBuilder().setId(bhb.getId()).build()); //没有条件要求, 直接触发提示
                cfg.addMatchHintNum(bhb.getId(), 1);
                return HandleRet.Triggered;
            } else {//有条件要求
                boolean triggered = matchActions(new Stats(0L, 0, bs.getRtActionStats(), bs.getStepActionStats()));
                if (triggered) {
                    cfg.addMatchHintNum(bhb.getId(), 1);
                }
                return triggered ? HandleRet.Triggered : HandleRet.MisMatch;
            }
        }
    }

    /** 处理提示 mvp */
    final static class HandleHintMvpType extends BaseHintHandleType {
        private HandleHintMvpType(BattleSource bs, BattleHintBean bhb, BattleHint cfg, List<BattleHintData> ret) {
            super(bs, bhb, cfg, ret);
        }

        @Override
		HandleRet handle() {
            if (isHintTriggered(bhb.getId(), cfg.getMatchHitNums())) {//已经触发
                return HandleRet.Max_Limit;
            }
            boolean triggered = matchActions(new Stats(0L, 0, bs.getRtActionStats(), bs.getStepActionStats()));
            if (triggered) {
                Map<Integer, PlayerActStat> homeacs = TeamReport.getPlayerPks(bs.getHome());
                Map<Integer, PlayerActStat> awayacs = TeamReport.getPlayerPks(bs.getAway());
                Tuple2<Integer, Float> homemvp = EndReport.calcMvp(homeacs.values());
                Tuple2<Integer, Float> awaymvp = EndReport.calcMvp(awayacs.values());
                ret.add(BattleHintData.newBuilder()
                        .setId(bhb.getId())
                        .setVi1(homemvp._1())
                        .setVi2(awaymvp._1())
                        .setVi3(homemvp._2().intValue())
                        .setVi4(awaymvp._2().intValue())
                        .build());
                log.trace("bthint mvp. bid {} hid {}. home {} away {}", bs.getId(), bhb.getId(), homemvp, awaymvp);
                cfg.addMatchHintNum(bhb.getId(), 1);
            }
            return triggered ? HandleRet.Triggered : HandleRet.MisMatch;
        }
    }

    /** 处理提示 team 球队行为 球队行为满足 N 组数据 */
    final static class HandleHintTeamType extends BaseHintHandleType {
        private HandleHintTeamType(BattleSource bs, BattleHintBean bhb, BattleHint cfg, List<BattleHintData> ret) {
            super(bs, bhb, cfg, ret);
        }

        /** @return true 触发了提示 */
        @Override
		public HandleRet handle() {
            HandleRet ret = handleTeamActions(bs.getHome());
            if (ret == HandleRet.MisMatch) {
                ret = handleTeamActions(bs.getAway());
            }
            return ret;
        }

        Stats getAs(long tid, BattleTeam bt) {
            return new Stats(tid, 0, bt.getRtActionStats(), bt.getStepActionStats());
        }

        private HandleRet handleTeamActions(BattleTeam bt) {
            if (isHintTriggered(bt.getTeamId(), bt.getHitNums())) {//已经触发
                return HandleRet.Max_Limit;
            }
            boolean triggered = matchActions(getAs(bt.getTeamId(), bt));
            if (triggered) {
                bt.addHintNum(bhb.getId(), 1);
            }
            return triggered ? HandleRet.Triggered : HandleRet.MisMatch;
        }
    }

    /** 处理提示 球员行为 */
    abstract static class HandleHintPlayerType extends BaseHintHandleType {
        private HandleHintPlayerType(BattleSource bs, BattleHintBean bhb, BattleHint cfg, List<BattleHintData> ret) {
            super(bs, bhb, cfg, ret);
        }

        Stats getAs(long tid, BattlePlayer bp) {
            return new Stats(tid, bp.getPlayerId(), bp.getRealTimeActionStats(), bp.getStepActionStats());
        }
    }

    /** 处理提示 pr_any_all	双方任一球员行为	任一球员行为满足 N 组数据 */
    static class HandleHintAnyPlayerAllType extends HandleHintPlayerType {
        private HandleHintAnyPlayerAllType(BattleSource bs, BattleHintBean bhb, BattleHint cfg, List<BattleHintData> ret) {
            super(bs, bhb, cfg, ret);
        }

        @Override
		public HandleRet handle() {
            HandleRet ret = handlePlayerActions(bs.getHome());
            if (ret == HandleRet.MisMatch) {
                ret = handlePlayerActions(bs.getAway());
            }
            return ret;
        }

        private HandleRet handlePlayerActions(BattleTeam bt) {
            for (BattlePlayer bp : bt.getPlayers()) {
                if (isHintTriggered(bp.getRid(), bp.getHitNums())) {
                    return HandleRet.Max_Limit;
                }
                if (matchActions(getAs(bt.getTeamId(), bp))) {
                    bp.addHintNum(bhb.getId(), 1);
                    return HandleRet.Triggered;
                }
            }
            return HandleRet.MisMatch;
        }

    }

    /** 处理提示 pr_any_num	双方任一球员行为	任一球员行为满足 N 组数据的 vi1 个 */
    final static class HandleHintAnyPlayerNumType extends HandleHintAnyPlayerAllType {
        private HandleHintAnyPlayerNumType(BattleSource bs, BattleHintBean bhb, BattleHint cfg, List<BattleHintData> ret) {
            super(bs, bhb, cfg, ret);
        }

        @Override
        int getMatchSize() {
            return bhb.getVi1();
        }

        @Override
        boolean isMatchLoopShortBreak() {
            return false;
        }
    }

    /** 处理提示 pr_each	双方每个球员行为	每个球员行为满足 N 组数据 */
    final static class HandleHintEachPlayerNumType extends HandleHintAnyPlayerAllType {
        private HandleHintEachPlayerNumType(BattleSource bs, BattleHintBean bhb, BattleHint cfg, List<BattleHintData> ret) {
            super(bs, bhb, cfg, ret);
        }

        /** 任一一个球员满足就返回 HandleRet.Triggered */
        @Override
		public HandleRet handle() {
            HandleRet triggeredHome = handlePlayerActions(bs.getHome());
            HandleRet triggeredAway = handlePlayerActions(bs.getAway());
            if (triggeredHome == HandleRet.Max_Limit && triggeredAway == HandleRet.Max_Limit) {//全部触发
                return HandleRet.Max_Limit;
            }
            if (triggeredHome == HandleRet.Triggered || triggeredAway == HandleRet.Triggered) {//部分触发
                return HandleRet.Triggered;
            }
            return HandleRet.MisMatch;
        }

        private HandleRet handlePlayerActions(BattleTeam bt) {
            int triggeredSize = 0;
            int triggerMax = 0;
            for (BattlePlayer bp : bt.getPlayers()) {
                if (isHintTriggered(bp.getRid(), bp.getHitNums())) {
                    triggerMax++;
                    continue;
                }
                if (matchActions(getAs(bt.getTeamId(), bp))) {
                    bp.addHintNum(bhb.getId(), 1);
                    triggeredSize++;
                }
            }
            if (triggerMax == bt.getPlayers().size()) {
                return HandleRet.Max_Limit;
            }
            return triggeredSize > 0 ? HandleRet.Triggered : HandleRet.MisMatch;
        }
    }

}