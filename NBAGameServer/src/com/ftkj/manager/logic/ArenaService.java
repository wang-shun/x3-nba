package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.ArenaBean.RankAwardBean;
import com.ftkj.cfg.EmailViewBean;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.console.ArenaConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ConfigConsole.GlobalBean;
import com.ftkj.console.ModuleConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TeamDayNumType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.arena.Arena;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.handle.BattleCommon;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.ArenaMatchEnd;
import com.ftkj.manager.battle.model.EndReport.TeamReport;
import com.ftkj.manager.battle.model.PlayerActStat;
import com.ftkj.manager.logic.LocalBattleManager.BattleContxt;
import com.ftkj.manager.logic.LocalBattleManager.HistoryType;
import com.ftkj.manager.logic.TeamDayStatsManager.TeamNums;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.CommonPB.BattleHisListResp;
import com.ftkj.proto.CommonPB.PlayerSimpleResp;
import com.ftkj.proto.RankArenaPb.ArenaInfoResp;
import com.ftkj.proto.RankArenaPb.ArenaOpponentResp;
import com.ftkj.proto.RankArenaPb.ArenaRankResp;
import com.ftkj.proto.RankArenaPb.ArenaRanksResp;
import com.ftkj.proto.RankArenaPb.ArenaSelfInfoResp;
import com.ftkj.proto.RankArenaPb.ArenaStartMatchResp;
import com.ftkj.proto.RankArenaPb.ArenaTargetsResp;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.quartz.QuartzServer;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.ImmutableList;
import com.graphbuilder.math.func.SumFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 竞技场. 个人排名竞技.
 *
 * @author luch
 */
public class ArenaService extends AbstractBaseManager implements OfflineOperation, IRedPointLogic {
    private static final Logger log = LoggerFactory.getLogger(ArenaService.class);
    @IOC
    private ArenaManager arenaManager;
    @IOC
    private TeamNumManager teamNumManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private LocalBattleManager lcBattleManager;
    @IOC
    private TeamDayStatsManager teamDayStatsManager;
    @IOC
    private TeamCapManager capManager;
    @IOC
    private RankManager rankManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private TeamEmailManager emailManager;
    @IOC
    private RedPointManager redPointManager;
    /** 刷新对手cd(毫秒) */
    private static final int Refresh_CD = 3_000;
    /** 对手前N位固定 */
    private static final int Target_Top_Fixed_Num = Arena.Target_Top_Fixed_Num;
    /** 对手总数量 */
    private static final int Target_Total_Num = 16;
    private static final int Target_Other_Num = Target_Total_Num - Target_Top_Fixed_Num;
    /** 正常情况下 高于自身排名的对手数量 */
    private static final int Target_Gt_Num = 3;
    /** 正常情况下 低于自身排名的对手数量 */
    private static final int Target_Lt_Num = Target_Other_Num - Target_Gt_Num - 1;
    /** 对手排名默认区间 */
    private static final int Target_Rank_Num = 50;

    /** 操作模块 */
    private ModuleLog mc = ModuleLog.getModuleLog(EModuleCode.Arena, "Arena");

    public ArenaService() {
        super();
        checkNum();
    }

    public ArenaService(boolean init) {
        super(init);
        checkNum();
    }

    private void checkNum() {
        //noinspection ConstantConditions
        if (Target_Gt_Num + Target_Lt_Num + 1 != Target_Other_Num) {
            throw new RuntimeException("竞技场对手数量配置错误");
        }
        if (Target_Gt_Num < 0 || Target_Lt_Num < 0) {
            throw new RuntimeException("竞技场对手数量配置错误");
        }
    }

    /** 系统启动时, 加载关卡排行榜 */
    @Override
    public void instanceAfter() {
        arenaManager.init();
        arenaRankRewardTask();
    }

    /** 竞技场排行奖励 */
    private void arenaRankRewardTask() {
        long awardTime = ConfigConsole.global().arenaAwardTime;
        long midnight = DateTimeUtil.midnight();
        long curr = System.currentTimeMillis();
        long delay = midnight + awardTime - curr;
        if (delay < 0) {
            delay = DateTimeUtil.DAILY - delay;
        }
        ScheduledFuture<?> sf = QuartzServer.scheduleAtFixedRate(this::rankAward,
            delay, DateTimeUtil.DAILY, TimeUnit.MILLISECONDS);
        log.info("竞技场排行奖励离下次时间 {}", DateTimeUtil.duration(sf));
    }

    @Override
    public void offline(long teamId) {
        Arena ar = arenaManager.removeArenas(teamId);
        if (ar == null) {
			return;
		}
        arenaManager.update(ar);
    }

    @Override
    public void dataGC(long teamId) {
        arenaManager.removeArenas(teamId);
    }

    @Override
    public RedPointParam redPointLogic(long teamId) {
        int num = redPointManager.getNum(teamId, ERedPoint.Arena);
        return num <= 0 ? null : new RedPointParam(teamId, ERedPoint.Arena.getId(), num);
    }

    /** 竞技场所有信息 */
    @ClientMethod(code = ServiceCode.Arena_Info)
    void arenaInfo() {
        ErrorCode ret = arenaInfo0();
        sendMsg(ret);
    }

    private ErrorCode arenaInfo0() {
        long tid = getTeamId();
        Team team = teamManager.getTeam(tid);
        Arena ar = getOrEnableArena(team);
        if (ar == null) {
            return ErrorCode.Arena_Disable;
        }
        ArenaInfoResp.Builder resp = ArenaInfoResp.newBuilder();
        resp.setSelfArena(selfInfoResp(tid, ar));
        ar.getTargets().clear();//清空对手
        resp.addAllOpponents(randomTargets(ar));
        sendMessage(tid, resp.build(), ServiceCode.Arena_Info_Push);
        return ErrorCode.Success;
    }

    private ArenaSelfInfoResp selfInfoResp(long tid, Arena ar) {
        GlobalBean gb = ConfigConsole.global();
        int usedNum = teamDayStatsManager.getNums(tid, TeamDayNumType.Arena_Match_Free_Num);
        int buyNum = teamNumManager.getUsedNum(tid, TeamNumType.Arena_Buy_Match_Num);
        int cd = getGtRankCd(ar, gb);
        log.debug("arena self info. tid {} rank {} maxr {} unum {} buynum {} cd {} time {}",
            tid, ar.getRank(), ar.getMaxRank(), usedNum, buyNum, cd, ar.getTempGtRankTargetMatchTime());
        return ArenaSelfInfoResp.newBuilder()
            .setRank(ar.getRank())
            .setMaxRank(ar.getMaxRank())
            .setPreMatchTime(ar.getPreMatchTime())
            .setTotalMatchCount(ar.getTotalMatchCount())
            .setTotalWinCount(ar.getTotalWinCount())
            .setUsedMatchNum(usedNum)
            .setFreeMatchNumCfg(gb.arenaFreeMatchNum)
            .setBuyMatchNum(buyNum)
            .setMatchCd(cd)
            .build();
    }

    private int getGtRankCd(Arena ar, GlobalBean gb) {
        return (int) Math.max(0, gb.arenaMatchGreaterRankCd -
            Math.max(0, System.currentTimeMillis() - ar.getTempGtRankTargetMatchTime()));
    }

    /** 刷新对手 */
    @ClientMethod(code = ServiceCode.Arena_Refresh_Target)
    void refreshTarget() {
        ErrorCode ret = refreshTarget0();
        sendMsg(ret);
    }

    private ErrorCode refreshTarget0() {
        long tid = getTeamId();
        Team team = teamManager.getTeam(tid);
        Arena ar = getOrEnableArena(team);
        if (ar == null) {
            return ErrorCode.Arena_Disable;
        }
        long curr = System.currentTimeMillis();
        if (ar.getTempPreRefreshOpponentTime() + Refresh_CD > curr) {
            return ErrorCode.Arena_Refresh_Time;
        }
        ar.setTempPreRefreshOpponentTime(curr);
        ar.getTargets().clear();//清空对手
        ArenaTargetsResp.Builder resp = ArenaTargetsResp.newBuilder();
        resp.addAllTargets(randomTargets(ar));
        sendMessage(tid, resp.build(), ServiceCode.Arena_Refresh_Target_Push);
        return ErrorCode.Success;
    }

    /** 随机获取对手 */
    private List<ArenaOpponentResp> randomTargets(Arena ar) {
        List<ArenaOpponentResp> resps = new ArrayList<>(Target_Total_Num);
        addTopFixedNum(ar.getTeamId(), resps);//前 N 名固定
        addOtherTargets(ar, resps);
        if (log.isDebugEnabled()) {
            log.debug("arena target. tid {} rank {} targets {} all {}",
                ar.getTeamId(), ar.getRank(), ar.getTargets(), resps.size());
        }
        return resps;
    }

    private void addOtherTargets(Arena ar, List<ArenaOpponentResp> resps) {
        if (!ar.getTargets().isEmpty()) {//有缓存
            addCacheTargets(resps, ar);
            return;
        }
        final int selfRank = (ar.getRank() > 0 || ar.getRank() <= Arena.Max_Rank_Size) ? ar.getRank() : Arena.Max_Rank_Size;
        final RankAwardBean rb = ArenaConsole.getAwardByRank(selfRank);
        final long teamId = ar.getTeamId();
        if (rb == null) {
            log.warn("arena target. tid {} rank {} config is null", teamId, selfRank);
        }

        final int targetGtNumCfg = rb == null ? Target_Rank_Num : rb.getTargetRankMin();
        final int targetLtNumCfg = rb == null ? Target_Rank_Num : rb.getTargetRankMax();
        final Map<Long, Integer> targetsTidAndRank = new LinkedHashMap<>();
        final ThreadLocalRandom tlr = ThreadLocalRandom.current();
        if (selfRank <= Target_Total_Num) {// 自己 <= 16名时
            if (selfRank > Target_Top_Fixed_Num) {
                targetsTidAndRank.put(ar.getTeamId(), selfRank);
            }

            randomTarget(ar, targetsTidAndRank, tlr,
                Math.max(Target_Top_Fixed_Num + 1, selfRank - targetGtNumCfg),
                Math.min(Arena.Max_Rank_Size, selfRank + targetLtNumCfg),
                selfRank > Target_Top_Fixed_Num ? Target_Other_Num - 1 : Target_Other_Num);

            ar.setTargets(targetsTidAndRank);
            addCacheTargets(resps, ar);
            return;
        }

        int targetGtNum = selfRank >= Arena.Max_Rank_Size - Target_Other_Num ? Target_Other_Num - 1 : Target_Gt_Num;
        randomTarget(ar, targetsTidAndRank, tlr,
            Math.max(Target_Top_Fixed_Num + 1, selfRank - targetGtNumCfg), selfRank, targetGtNum);//排名高于自己的
        targetsTidAndRank.put(ar.getTeamId(), selfRank);//自己
        if (selfRank < Arena.Max_Rank_Size - Target_Other_Num) {
            randomTarget(ar, targetsTidAndRank, tlr,
                selfRank, Math.min(Arena.Max_Rank_Size, selfRank + targetLtNumCfg), Target_Lt_Num);//排名低于自己的
        }

        ar.setTargets(targetsTidAndRank);
        addCacheTargets(resps, ar);
    }

    /**
     * 按规则随机产生对手
     *
     * @param rankMin 包括
     * @param rankMax 不包括
     */
    private void randomTarget(Arena ar,
                              Map<Long, Integer> targetsTidAndRank,
                              ThreadLocalRandom tlr,
                              int rankMin,
                              int rankMax,
                              int targetNum) {
        log.debug("arena random target. tid {} rank {} min {} max {} num {}",
            ar.getTeamId(), ar.getRank(), rankMin, rankMax, targetNum);
        if (rankMin >= rankMax) {
            return;
        }
        int count = 0;
        int whileCount = 0;
        while (count < targetNum && whileCount < Target_Other_Num * 10) {
            int trank = tlr.nextInt(rankMin, rankMax);
            Long ttid = arenaManager.getTeamIdByRank(trank);
            whileCount++;
            log.trace("arena random target. tid {} trank {} ttid {} count {} wc {}",
                ar.getTeamId(), trank, ttid, count, whileCount);
            if (invalidTargetId(ar.getTeamId(), targetsTidAndRank, ttid)) {
                continue;
            }
            targetsTidAndRank.put(ttid, trank);
            count++;
        }
    }

    /** 获取前N位固定对手 */
    private void addTopFixedNum(long tid, List<ArenaOpponentResp> resps) {
        arenaManager.forEachRanks(0, Target_Top_Fixed_Num, (idx, teamId) -> {
            if (teamId == null) {
                log.warn("arena fixed target. tid {} rankidx {} t null", tid, idx);
                return;
            }
            Arena arena = arenaManager.getArena(teamId);
            if (arena == null) {
                log.warn("arena fixed target. tid {} rankidx {} ttid {} ar null", tid, idx, teamId);
                return;
            }
            resps.add(targetResp(arena));
        });
    }

    /** 获取缓存的对手信息 */
    private void addCacheTargets(List<ArenaOpponentResp> resps, Arena ar) {
        for (Long targetTeamId : ar.getTargets().keySet()) {
            Arena target = arenaManager.getArena(targetTeamId);
            if (target == null) {
                continue;
            }
            resps.add(targetResp(target));
        }
    }

    /** @return true 不是一个合理的对手 */
    private boolean invalidTargetId(long selfTid, Map<Long, Integer> targetsTidAndRank, Long targetTid) {
        return targetTid == null || targetTid == selfTid || targetsTidAndRank.containsKey(targetTid);
    }

    private ArenaOpponentResp targetResp(Arena target) {
        TeamPlayer tp = playerManager.getTeamPlayer(target.getTeamId());
        List<PlayerSimpleResp> prs = new ArrayList<>();
        for (Player pr : tp.getStartingPlayers()) {
            PlayerAbility pa = capManager.getPlayerAllCap(target.getTeamId(), pr.getPlayerRid(), pr.getPlayerTalent());
            prs.add(playerSimpleResp(pr, pa));
        }
        int totalCap = rankManager.getRankCapOrCalcedCap(target.getTeamId());
        return ArenaOpponentResp.newBuilder()
            .setTeamId(target.getTeamId())
            .setTeam(teamManager.teamResp(target.getTeamId()))
            .setMaxRank(target.getMaxRank())
            .setRank(target.getRank())
            .setCap(totalCap)
            .addAllPlayers(prs)
            .build();
    }

    private PlayerSimpleResp playerSimpleResp(Player pr, PlayerAbility pa) {
        PlayerSimpleResp.Builder psr = PlayerSimpleResp.newBuilder();
        psr.setRid(pr.getPlayerRid());
        psr.setLpPos(pr.getLineupPosition().getId());
        psr.setPos(pr.getPlayerPosition().getId());
        psr.setPrice(pr.getPrice());
        psr.setOcap(pa.getInt(EActionType.ocap));
        psr.setDcap(pa.getInt(EActionType.dcap));
        return psr.build();
    }

    /** 开始比赛 */
    @ClientMethod(code = ServiceCode.Arena_Start_Match)
    public void startMatch(int rank, long targetTid) {
        long teamId = getTeamId();
        ErrorCode ret = startMatch0(teamId, rank, targetTid);
        if (ret.isError()) {
            sendMsg(ret);
        }
        log.debug("arena startMatch. tid {} ttid {} rank {} ret {}", teamId, targetTid, rank, ret);
    }

    private synchronized ErrorCode startMatch0(final long teamId, final int targetRank, final long targetTid) {
        if (teamId == targetTid) {
            return ErrorCode.Battle_Target_Self;
        }
        if (isInBattle(teamId)) {
            return ErrorCode.Battle_In;
        }
        if (isInBattle(targetTid)) {
            return ErrorCode.Battle_Target_In;
        }
        GlobalBean gb = ConfigConsole.global();
        Team team = teamManager.getTeamWithoutGC(teamId);
        if (isModuleDisable(team)) {
            return ErrorCode.Team_Level;
        }
        Arena ar = getOrEnableArena(team);
        if (ar == null) {
            return ErrorCode.Arena_Disable;
        }
        long curr = System.currentTimeMillis();
        Arena target = arenaManager.getArena(targetTid);
        if (target == null) {
            return ErrorCode.Arena_Target_Null;
        }
        Integer targetRankOld = ar.getTargets().get(targetTid);
        //不在目标对手中, 或者非目标对手排名差距过大
        final int selfRank = ar.getRankOrLast();
        if (targetRankOld == null) {
            if (Math.abs(target.getRankOrLast() - selfRank) > gb.arenaNonTargetRankRange) {
                return ErrorCode.Arena_Target_Rank_Range;
            }
            targetRankOld = targetRank;
        }

        if (target.getRankOrLast() != targetRankOld) {//老对手排名变化
            Long newTargetTid = arenaManager.getTeamIdByRank(targetRankOld);
            if (newTargetTid == null) {
                return ErrorCode.Arena_New_Target_Null0;
            }
            Arena newTarget = arenaManager.getArena(newTargetTid);
            if (newTarget == null) {
                return ErrorCode.Arena_New_Target_Null;
            }
            ar.getTargets().remove(targetTid);
            ar.getTargets().put(targetTid, targetRankOld);//更新对手信息, 排名固定

            ArenaStartMatchResp resp = ArenaStartMatchResp.newBuilder()
                .setRank(targetRankOld)
                .setNewTarget(targetResp(newTarget))
                .build();
            log.debug("arena target change. tid {} ttid {} rank {} {} newtarget {} {}",
                teamId, targetTid, ar.getRank(), targetRankOld, newTargetTid, newTarget.getRank());
            sendMessage(teamId, resp, ServiceCode.Arena_Target_Change_Push);
            return ErrorCode.Arena_Target_Chnage;
        }

        //挑战排名高于自己的的玩家时冷却时间未到
        if (getGtRankCd(ar, gb) > 0) {
            return ErrorCode.Arena_Greater_Rank_Cd;
        }
        TeamNums tn = teamDayStatsManager.getNums(teamId);
        int usedNum = tn.getNum(TeamDayNumType.Arena_Match_Free_Num, 0);
        int buyNum = teamNumManager.getUsedNum(teamId, TeamNumType.Arena_Buy_Match_Num);
        if (buyNum + gb.arenaFreeMatchNum - usedNum <= 0) {//比赛次数不足
            return ErrorCode.Arena_Match_Num;
        }
        teamDayStatsManager.saveTeamNums(tn, TeamDayNumType.Arena_Match_Free_Num, usedNum + 1);
        ar.setPreMatchTime(curr);
        if (selfRank > 0 && selfRank > targetRankOld) {
            ar.setTempGtRankTargetMatchTime(curr);
        } else {
            resetGtRankCd(ar);
        }
        ar.setTotalMatchCount(ar.getTotalWinCount() + 1);
        arenaManager.update(ar);
        ar.getTargets().clear();//清空对手
        if (targetRank < 100) {
			log.info("startBattle|myTeamId{}|myRank|{}|targetTeamId|{}|targetRank|{}",
				teamId, ar.getRank(), targetTid, targetRank);
		}
        startBattle(teamId, ar, targetTid, targetRankOld);
        sendMsg(ErrorCode.Success);

        if (!GameSource.isNPC(targetTid)) {
            redPointManager.upNum(targetTid, ERedPoint.Arena, 1);
            EventBusManager.post(EEventType.奖励提示, new RedPointParam(targetTid, ERedPoint.Arena.getId(), 1));
        }
        return ErrorCode.Success;
    }

    /** 开启比赛 */
    private void startBattle(long teamId, Arena ar, long targetTid, Integer targetRankOld) {
        BattleSource bs = lcBattleManager.buildBattle(EBattleType.Arena, teamId, targetTid, teamId);
        BattleContxt bc = lcBattleManager.defaultContext(this::endMatch0);
        bc.setStartPush(this::startPush);
        BattleAttribute ba = new BattleAttribute(teamId);
        ba.addVal(EBattleAttribute.Arena_Target_Tid, targetTid);
        ba.addVal(EBattleAttribute.Arena_Target_Rank, targetRankOld);
        bs.addBattleAttribute(ba);

        BaseBattleHandle bh = new BattleCommon(bs);
        log.debug("arena start match. tid {} ttid {} rank {} {}", teamId, targetTid, ar.getRank(), targetRankOld);
        lcBattleManager.initBattleWithContext(bh, bs, bc);
        lcBattleManager.start(bs, bc, bh);
    }

    /** 比赛开始通知 */
    private void startPush(BattleSource bs) {
        //比赛开启成功推送，前端初始化比赛信息
        sendMessage(bs.getHomeTid(), BattlePb.battleStartResp(bs), ServiceCode.Battle_Start_Push);
    }

    /** 比赛结束 */
    private void endMatch0(BattleSource bs) {
        try {
            endMatch1(bs);
        } catch (Exception e) {
            log.error("arena " + e.getMessage(), e);
        }
    }

    /** 比赛结束 */
    private void endMatch1(BattleSource bs) {
        EndReport report = bs.getEndReport();
        long teamId = report.getHomeTeamId();
        BattleAttribute ba = bs.getAttributeMap(teamId);
        Long targetTid = ba.getVal(EBattleAttribute.Arena_Target_Tid, 0L);
        Integer targetRankOld = ba.getVal(EBattleAttribute.Arena_Target_Rank, 0);
        Arena ar = arenaManager.getArena(teamId);
        Arena target = arenaManager.getArena(targetTid);
        log.debug("arena end. tid {} ttid {} wintid {} rank {} {} score {}:{}", teamId, targetTid, report.getWinTeamId(),
            ar != null ? ar.getRank() : null, targetRankOld, report.getHomeScore(), report.getAwayScore());
        if (ar == null || target == null || GameSource.isNPC(teamId)) {
            lcBattleManager.sendEndMain(bs, true);
            return;
        }
        ArenaMatchEnd arend = new ArenaMatchEnd();
        arend.setSelfRank(ar.getRankOrLast());
        arend.setTargetRank(target.getRankOrLast());
        //处理低战力赢了高战力的比赛
        forceUpdateArenaResult(bs);
        final boolean win = report.getWinTeamId() == report.getHomeTeamId();
        log.debug("arena end. tid {} ttid {} wintid {} rank {} {} score {}:{}", teamId, targetTid, report.getWinTeamId(),
                ar != null ? ar.getRank() : null, targetRankOld, report.getHomeScore(), report.getAwayScore());
        if (win) {//胜利
            endMatchWin(report, teamId, ar, target, arend);
        } else {//失败
            report.appendWinAwardList(ArenaConsole.getLoseAwardByRank(ar.getRankOrLast()));
        }

        report.addAdditional(EBattleAttribute.Arena_Match_End, arend);
        lcBattleManager.sendEndMain(bs, true);//修改了比赛的状态
        sendMessage(teamId, selfInfoResp(teamId, ar), ServiceCode.Arena_End_Match_Push);
    }
    
    /**
     * 如果竞技场挑战,双方战力差某个数值,并且战力低的反而胜利了,
     * 则需要强制修改竞技场比赛的胜利方位战力高的, 修改下比赛获得的分数.
     * @param report
     */
    private void forceUpdateArenaResult(BattleSource bs){
    	try {
    		EndReport report = bs.getEndReport();
    		long homeTeamId = report.getHomeTeamId();
        	long awayTeamId = report.getAwayTeamId();
        	int homeTotalCap = rankManager.getRankCapOrCalcedCap(homeTeamId);
        	int awayTotalCap = rankManager.getRankCapOrCalcedCap(awayTeamId);
        	boolean isHomeMaxCap =  homeTotalCap > awayTotalCap;
        	// 战力值相差的绝对值小于2000,直接返回比赛结果
        	if (Math.abs(homeTotalCap - awayTotalCap) < ConfigConsole.getGlobal().arenaTotalCapSub) {
        		return;
    		}
        	
        	// 主队战力高,并且赢了比赛直接返回  or 客队战力高,并且赢了比赛直接返回
        	if ((isHomeMaxCap && report.getWinTeamId() == report.getHomeTeamId())
        		|| (!isHomeMaxCap && report.getWinTeamId() == report.getAwayTeamId())){
        		return;
        	}
        	
        	TeamReport teamReport = null;
        	BattleTeam battleTeam = null;
        	Map<Integer, PlayerActStat> sources = null;
        	// home战力高,并且比赛输了,则需要把比赛修改成胜利.
        	if (isHomeMaxCap && report.getWinTeamId() != report.getHomeTeamId()) {
    			report.setWinTeamId(homeTeamId);
    			teamReport = report.getHome();
    			sources = teamReport.getSources();
    			battleTeam = bs.getHome();
    		}else if (!isHomeMaxCap && report.getWinTeamId() != report.getAwayTeamId()) {
    			report.setWinTeamId(awayTeamId);
    			teamReport = report.getAway();
    			sources = teamReport.getSources();
    			battleTeam = bs.getAway();
    		}
        	
        	int subScore = Math.abs(report.getAwayScore() - report.getHomeScore());
        	// 计算给每个球员加2分要加多少个球员,额外再加2分保证加的分数能够赢.
        	int num = ((subScore % 2) == 0 ? (subScore / 2) : (subScore / 2 + 1)) + 1;
        	int count = 0;
        	while(true){
        		for (PlayerActStat obj : sources.values()) {
        			obj.sum(EActionType.pts, 2);
        			obj.sum(EActionType.fga, 1);
        			obj.sum(EActionType.fgm, 1);
        			if (num == (++count)) {
    					break;
    				}
        		}
        		
        		if (num == count) {
    				break;
    			}
        	}
        	
        	int addScore = num * 2;
        	// 比赛胜利
        	battleTeam.setWin(true);
        	battleTeam.setScore(battleTeam.getScore() + addScore);
        	battleTeam.updateStepScore(EBattleStep.Second_Period, addScore);
        	teamReport.setScore(teamReport.getScore() + addScore);
		} catch (Exception e) {
			log.warn("forceUpdateArenaResult exception:{}", e.getMessage());
		}
    	
    }

    /** 比赛胜利.更新状态, 结算奖励 */
    private void endMatchWin(EndReport report, long teamId, Arena ar, Arena target, ArenaMatchEnd arend) {
        final int oldSelfRank = ar.getRankOrLast();
        final int oldMaxSelfRank = ar.getMaxRankOrLast();
        final int oldTargetRank = target.getRankOrLast();
        GlobalBean gb = ConfigConsole.global();

        if (oldSelfRank > oldTargetRank) {//挑战排名高于自己的
            arend.setOldMaxRank(ar.getMaxRank());
            arend.setNewMaxRank(oldTargetRank);
            arenaManager.swapRank(ar, target, oldSelfRank, oldTargetRank);
        }

        ar.setTotalWinCount(ar.getTotalWinCount() + 1);
        arenaManager.update(ar);

        final int maxRankChange = oldMaxSelfRank - ar.getMaxRank();
        if (maxRankChange > 0) {//最高排名发生变化
            Map<Integer, PropSimple> props = ArenaConsole.getChangeRankReward(oldMaxSelfRank, ar.getMaxRank(), gb.arenaMaxRankCurrItemId);
            log.debug("arena end. tid {} maxrank {} -> {} award {}", teamId, oldMaxSelfRank, ar.getMaxRank(), props);
            PropSimple maxRankAward = props.get(gb.arenaMaxRankCurrItemId);
            if (maxRankAward != null) {
                arend.setMaxRankAward(maxRankAward.getNum());
            }
            report.appendWinAwardList(new ArrayList<>(props.values()));

        }
        report.appendWinAwardList(ArenaConsole.getWinAwardByRank(ar.getRankOrLast()));
    }

    /** 是否在比赛中 */
    private boolean isInBattle(long teamId) {
        return TeamStatus.inBattle(teamStatusManager.getTeamStatus(teamId), EBattleType.Arena);
    }

    /** 购买比赛次数 */
    @ClientMethod(code = ServiceCode.Arena_Buy_Match_Num)
    void buyMatchNum(int num) {
        long tid = getTeamId();
        ErrorCode ret = buyMatchNum0(tid, num);
        sendMsg(ret);
        log.debug("arena buyMatchNum. tid {} num {} ret {}", tid, num, ret);
    }

    private ErrorCode buyMatchNum0(long tid, int num) {
        Team team = teamManager.getTeam(tid);
        Arena arena = getOrEnableArena(team);
        if (arena == null) {
            return ErrorCode.Arena_Disable;
        }
        ErrorCode ret = teamNumManager.consumeNumCurrency(tid, TeamNumType.Arena_Buy_Match_Num, num, mc);
        if (ret.isError()) {
            return ret;
        }
        sendMessage(tid, selfInfoResp(tid, arena), ServiceCode.Arena_Buy_Match_Num_Push);
        return ErrorCode.Success;
    }

    /** 重置比赛cd */
    @ClientMethod(code = ServiceCode.Arena_Reset_Match_Cd)
    void resetMatchCd() {
        long tid = getTeamId();
        ErrorCode ret = resetMatchCd0(tid);
        sendMsg(ret);
        log.debug("arena resetMatchCd. tid {} ret {}", tid, ret);
    }

    private ErrorCode resetMatchCd0(long tid) {
        Team team = teamManager.getTeam(tid);
        Arena ar = getOrEnableArena(team);
        if (ar == null) {
            return ErrorCode.Arena_Disable;
        }
        ErrorCode ret = teamNumManager.consumeNumCurrency(tid, TeamNumType.Arena_Reset_CD_Num, 1, mc);
        if (ret.isError()) {
            return ret;
        }
        resetGtRankCd(ar);
        return ErrorCode.Success;
    }

    private void resetGtRankCd(Arena ar) {
        ar.setTempGtRankTargetMatchTime(ar.getTempGtRankTargetMatchTime() - ConfigConsole.global().arenaMatchGreaterRankCd);
    }

    /** 排行榜 */
    @ClientMethod(code = ServiceCode.Arena_Rank)
    public void rankInfo() {
        final ArenaRanksResp.Builder resp = ArenaRanksResp.newBuilder();
        arenaManager.forEachRanks(0, 50, (idx, teamId) -> {
            if (teamId == null) {
                return;
            }
            Arena arena = arenaManager.getArena(teamId);
            if (arena == null) {
                return;
            }
            resp.addTeams(rankResp(arena));
        });
        sendMessage(resp.build());
    }

    /** 最近几次的比赛记录, 防守记录. */
    @ClientMethod(code = ServiceCode.Arena_Match_His)
    public void matchHis(int pageNo) {
        long teamId = getTeamId();
        BattleHisListResp resp = lcBattleManager.matchHistory(teamId, EBattleType.Arena, HistoryType.Away,
            20, pageNo, (hisb, his) -> {
                Arena har = arenaManager.getArena(his.getHomeTeamId());
                if (har != null) {
                    hisb.setVi3(har.getRank());
                }
                return hisb;
            });
        sendMessage(resp);
        log.debug("arena matchHis. tid {} n {} size {}", teamId, pageNo, resp.getHisCount());
        redPointManager.removeNum(teamId, ERedPoint.Arena);
    }

    private ArenaRankResp rankResp(Arena target) {
        int totalCap = rankManager.getRankCapOrCalcedCap(target.getTeamId());
        return ArenaRankResp.newBuilder()
            .setTeamId(target.getTeamId())
            .setMaxRank(target.getMaxRank())
            .setRank(target.getRank())
            .setCap(totalCap)
            .setTeam(teamManager.teamResp(target.getTeamId()))
            .build();
    }

    /** 竞技场排行奖励. 定时结算奖励 */
    private void rankAward() {
        log.info("arenaRankReward 竞技场排行榜定时结算奖励 start");
        final long curr = System.currentTimeMillis();
        final long disableTime = ConfigConsole.global().arenaRankAwardDisableTime;
        arenaManager.forEachArena(arena -> {
            if (arena == null || GameSource.isNPC(arena.getTeamId())) {
                return;
            }
            int rank = arena.getRankOrLast();
            if (arena.getLastRank() != rank) {
                arena.setLastRank(rank);
                arenaManager.update(arena);
            }
            //不活跃的用户不发放奖励
            if (curr - arena.getPreMatchTime() > disableTime) {
                return;
            }

            Team team = teamManager.getTeam(arena.getTeamId());
            if (team == null) {
                return;
            }
            mail(arena, rank);
        });
        log.info("arenaRankReward 竞技场排行榜定时结算奖励 end");
    }

    /** 通过邮件发送奖励 */
    private void mail(Arena arena, int rank) {
        RankAwardBean ab = ArenaConsole.getAwardByRank(rank);
        if (ab == null) {
            return;
        }
        ImmutableList<String> contentParams = ImmutableList.of("" + rank);
        if (log.isDebugEnabled()) {
            log.debug("arena rank award. tid {} rank {} award {}", arena.getTeamId(), rank, ab.getRankAward());
        }
        emailManager.sendEmailWithParamTemplate(arena.getTeamId(), EmailViewBean.Arena_Rank,
            ImmutableList.of(), contentParams, ab.getRankAward());
    }

    /** 获取自己的竞技场信息. 没有并且达到激活条件则创建 */
    private Arena getOrEnableArena(Team team) {
        if (isModuleDisable(team)) {
            return null;
        }
        Arena ar = arenaManager.getArena(team.getTeamId());
        if (ar == null) {
            ar = enableArena(team);
            arenaManager.addArenas(ar);
        }
        return ar;
    }

    private boolean isModuleDisable(Team team) {
        return ModuleConsole.isDisabled(team.getLevel(), EModuleCode.Arena);
    }

    private Arena enableArena(Team team) {
        return arenaManager.insertArena(team);
    }

    /** gm 命令. 重置比赛冷却时间 */
    void gmResetCd(long tid) {
        Arena ar = arenaManager.getArena(tid);
        if (ar == null) {
            return;
        }
        resetGtRankCd(ar);
    }

    /** gm 命令. 发放排名奖励 */
    void gmRankAward() {
        rankAward();
    }
}
