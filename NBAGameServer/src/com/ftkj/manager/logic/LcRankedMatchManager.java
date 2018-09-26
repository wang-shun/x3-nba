package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.cfg.RankedMatchBean.FirstAwardBean;
import com.ftkj.cfg.RankedMatchBean.RankedMatchSeasonBean;
import com.ftkj.cfg.RankedMatchMedalBean;
import com.ftkj.cfg.RankedMatchTierBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ConfigConsole.GlobalBean;
import com.ftkj.console.ConfigConsole.TupleTime;
import com.ftkj.console.DropConsole;
import com.ftkj.console.RankedMatchConsole;
import com.ftkj.constant.ChatPushConst;
import com.ftkj.db.dao.logic.BattleDAO;
import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.model.BattleInfo;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.RankedMatchEnd;
import com.ftkj.manager.battle.model.EndReport.RankedMatchEnd.RankedTeam;
import com.ftkj.manager.common.NodeManager;
import com.ftkj.manager.logic.LocalBattleManager.HistoryType;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.match.RankedMatch;
import com.ftkj.manager.match.RankedMatch.Season;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.manager.team.TeamNode;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.CommonPB.BattleHisListResp;
import com.ftkj.proto.PropPB.PropListResp;
import com.ftkj.proto.RankedMatchPb.RMatchAllResp;
import com.ftkj.proto.RankedMatchPb.RMatchHisListResp;
import com.ftkj.proto.RankedMatchPb.RMatchMedalRankResp;
import com.ftkj.proto.RankedMatchPb.RMatchSeasonResp;
import com.ftkj.proto.RankedMatchPb.RMatchSysSeasonResp;
import com.ftkj.proto.RankedMatchPb.RMatchTeamResp;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.RpcTask;
import com.ftkj.server.rpc.RpcTask.RpcResp;
import com.ftkj.util.BitUtil;
import com.ftkj.util.DateTimeUtil;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 跨服天梯赛 */
public class LcRankedMatchManager extends AbstractBaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(LcRankedMatchManager.class);
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private LocalBattleManager localBattleManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private NodeManager nodeManager;
    @IOC
    private TeamEmailManager teamEmailManager;
    @IOC
    private TeamDayStatsManager teamDayStatisticsManager;
    @IOC
    private PropManager propManager;
    @IOC
    private BattleDAO battleDAO;
    @IOC
    private ChatManager chatManager;
    @IOC
    private TaskManager taskManager;

    /** 操作模块 */
    private ModuleLog mc = ModuleLog.getModuleLog(EModuleCode.RankedMatch, "");
    private static final int PAGE_SIZE = 50;

    /** 球队退出 */
    @Override
    public void offline(long teamId) {
        RpcTask.tell(CrossCode.XRaned_Team_Offline, null, teamId);
    }

    @Override
    public void dataGC(long teamId) {

    }

    /** 获取天梯赛信息 */
    @ClientMethod(code = ServiceCode.RMatch_Info)
    public void info() {
        long teamId = getTeamId();
        ErrorCode ret = info0(teamId);
        sendMsg(ret);
        log.debug("rmatch info. tid {} ret {}", teamId, ret);
    }

    /** 获取天梯赛信息 */
    private ErrorCode info0(long teamId) {
        long curr = System.currentTimeMillis();
        //        Team team = teamManager.getTeam(teamId);
        RankedMatchSeasonBean sb = RankedMatchConsole.getCurrOrNextSeason(curr);
        RpcResp<RankedMatch> rpcresp = RpcTask.ask(CrossCode.XRanked_Info, teamId);
        if (rpcresp.isError()) {
            return rpcresp.ret;
        }
        RankedMatch rm = rpcresp.t;
        RMatchAllResp.Builder allr = RMatchAllResp.newBuilder();
        if (sb != null) {
            RMatchSysSeasonResp sysr = RMatchSysSeasonResp.newBuilder()
                .setId(sb.getId())
                .setStart(sb.getStart())
                .setEnd(sb.getEnd())
                .build();
            allr.setSys(sysr);
        }
        RMatchTeamResp.Builder tr = RMatchTeamResp.newBuilder();
        if (rm != null) {
            for (int i = RankedMatchConsole.First_Award_Id_Min; i <= RankedMatchConsole.First_Award_Id_Max; i++) {
                if (BitUtil.hasBit(rm.getFirstAward(), 1 << i)) {
                    tr.addFirstAward(i);
                }
            }
            tr.setTotalMatchCount(rm.getTotalMatchCount());
            tr.setTotalWinCount(rm.getTotalWinCount());
            tr.setWinningStreak(rm.getWinningStreak());
            tr.setLastMatchTime(rm.getLastMatchTime());
            Daily daily = getTeamDaily(teamId);
            tr.setDailyReward(daily.dailyAward || rm.getTotalMatchCount() < 1);
            tr.setInPool(rm.isTempInPool());
            if (rm.getCurrSeason() != null) {
                tr.setCurrSeason(seasonResp(rm.getCurrSeason()));
            }
            if (rm.getLastSeason() != null) {
                tr.setPreSeason(seasonResp(rm.getLastSeason()));
            }
        } else {
            RMatchSeasonResp.Builder sr = RMatchSeasonResp.newBuilder();
            if (sb != null) {
                sr.setId(sb.getId());
                int rating = ConfigConsole.global().rMatchInitRating;
                sr.setRating(rating);
                RankedMatchTierBean tb = RankedMatchConsole.getTierByRating(rating);
                if (tb != null) {
                    sr.setTierId(tb.getId());
                }
                tr.setCurrSeason(sr);
            }
            tr.setDailyReward(true);
        }
        allr.setTeam(tr);

        sendMessage(teamId, allr.build(), ServiceCode.RMatch_Info_Push);
        return ErrorCode.Success;
    }

    private RMatchSeasonResp seasonResp(Season s) {
        return RankedMatch.seasonResp(s);
    }

    /** 开始比赛, 加入匹配池 */
    @ClientMethod(code = ServiceCode.RMatch_Join_Pool)
    public void joinPool() {
        long teamId = getTeamId();
        ErrorCode ret = joinPool0(teamId);
        sendMsg(ret);
        log.debug("rmatch joinPool. tid {} ret {}", teamId, ret);
    }

    /** 开始比赛 */
    private ErrorCode joinPool0(long teamId) {
        Team team = teamManager.getTeam(teamId);
        if (inBattle(teamId)) {
            return ErrorCode.Battle_In;
        }
        long curr = System.currentTimeMillis();
        RankedMatchSeasonBean seasonb = RankedMatchConsole.getCurrSeason(curr);
        if (seasonb == null) {
            return ErrorCode.RMatch_Season_Bean_Null;
        }
        GlobalBean gb = ConfigConsole.getGlobal();
        if (!inMatchTime(gb, curr)) {
            return ErrorCode.RMatch_Match_Time;
        }
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        if (tp.getPlayers().size() < gb.rMatchLineupMinPlayerNum) {
            return ErrorCode.RMatch_Lineup_Pr_Num;
        }

        RpcResp<Object> resp =
            RpcTask.ask(CrossCode.XRanked_Join_Pool, new TeamNode(teamId, GameSource.serverName), team.getLevel());
        return resp.ret;
    }

    /** 开始比赛, 取消加入 */
    @ClientMethod(code = ServiceCode.RMatch_Cancel_Join)
    public void cancelJoin() {
        long teamId = getTeamId();
        ErrorCode ret = cancelJoin0(teamId);
        sendMsg(ret);
        log.debug("rmatch cancelJoin. tid {} ret {}", teamId, ret);
    }

    /** 取消加入 */
    private ErrorCode cancelJoin0(long teamId) {
        if (inBattle(teamId)) {
            return ErrorCode.Battle_In;
        }
        RpcResp<Object> resp = RpcTask.ask(CrossCode.XRanked_Cancel_Join, teamId);
        return resp.ret;
    }

    /** 历史战绩 */
    @ClientMethod(code = ServiceCode.RMatch_Season_His)
    public void seasonHistory(int pageNo) {
        long teamId = getTeamId();
        ErrorCode ret = seasonHistory0(teamId, Math.max(1, pageNo));
        sendMsg(ret);
        log.debug("rmatch seasonHistory. tid {} pageNo {} ret {}", teamId, pageNo, ret);
    }

    /** 历史战绩 */
    private ErrorCode seasonHistory0(long teamId, int pageNo) {
        RpcResp<RMatchHisListResp> resp = RpcTask.ask(CrossCode.XRaned_Season_His, teamId, pageNo, PAGE_SIZE);
        if (resp.isError()) {
            return resp.ret;
        }
        sendMessage(teamId, resp.t, ServiceCode.RMatch_Season_His_Push);
        return ErrorCode.Success;
    }

    /** 段位排行榜 */
    @ClientMethod(code = ServiceCode.RMatch_Rank)
    public void rank(int medalId) {
        long teamId = getTeamId();
        ErrorCode ret = rank0(teamId, medalId);
        sendMsg(ret);
        log.debug("rmatch rank. tid {} mid {} ret {}", teamId, medalId, ret);
    }

    /** 段位排行榜 */
    private ErrorCode rank0(long teamId, int medalId) {
        RankedMatchMedalBean mb = RankedMatchConsole.getMedal(medalId);
        if (mb == null) {
            return ErrorCode.RMatch_Medal_Bean;
        }
        RpcResp<RMatchMedalRankResp> resp = RpcTask.ask(CrossCode.XRaned_Rank, teamId, medalId,
            Math.max(PAGE_SIZE, mb.getRankViewNum()));
        if (resp.isError()) {
            return resp.ret;
        }
        RMatchMedalRankResp tresp = resp.t;
        if (tresp.getTeamsList() == null) {// workaround pb bug
            tresp = RMatchMedalRankResp.newBuilder()
                .setMedalId(tresp.getMedalId())
                .build();
        }
        log.debug("rmatch rank. tid {} mid {} size {}", teamId, medalId, tresp.getTeamsCount());
        sendMessage(teamId, tresp, ServiceCode.RMatch_Rank_Push);
        return ErrorCode.Success;
    }

    /** 比赛历史信息 */
    @ClientMethod(code = ServiceCode.RMatch_Match_His)
    public void matchHis(int battleType, int pageNo) {
        long teamId = getTeamId();
        ErrorCode ret = matchHis0(teamId, battleType, pageNo);
        sendMsg(ret);
        log.debug("rmatch matchHis. tid {} bt {} page {} ret {}", teamId, battleType, pageNo, ret);
    }

    private ErrorCode matchHis0(long teamId, int battleType, int pageNo) {
        EBattleType bt = EBattleType.getBattleType(battleType);
        if (bt == null) {
            return ErrorCode.Battle_Type;
        }
        BattleHisListResp resp = localBattleManager.matchHistory(teamId, bt, HistoryType.All, 50, pageNo, null);
        sendMessage(teamId, resp, ServiceCode.RMatch_Match_His_Push);
        return ErrorCode.Success;
    }

    /** 领取每日奖励 */
    @ClientMethod(code = ServiceCode.RMatch_Receive_Daily_Award)
    public void receiveDailyAward() {
        long teamId = getTeamId();
        ErrorCode ret = receiveDailyAward0(teamId);
        sendMsg(ret);
        log.debug("rmatch receiveDailyAward. tid {} ret {}", teamId, ret);
    }

    /** 领取每日奖励 */
    private ErrorCode receiveDailyAward0(long teamId) {
        Daily daily = getTeamDaily(teamId);
        if (daily.dailyAward) {
            return ErrorCode.RMatch_Daily_Award;
        }
        //        Team team = teamManager.getTeam(teamId);
        RpcResp<RankedMatch> rpcresp = RpcTask.ask(CrossCode.XRanked_Info, teamId);
        if (rpcresp.isError()) {
            return rpcresp.ret;
        }
        RankedMatch rm = rpcresp.t;
        if (rm == null || rm.getCurrSeason() == null) {
            return ErrorCode.RMatch_Season_Curr_Null;
        }
        if (rm.getTotalMatchCount() < 1) {
            return ErrorCode.RMatch_Daily_Reward_Match_Count;
        }
        Season season = rm.getCurrSeason();
        RankedMatchTierBean tb = RankedMatchConsole.getTier(season.getTierId());
        if (tb == null) {
            return ErrorCode.RMatch_Tier_Bean;
        }
        RankedMatchMedalBean mb = RankedMatchConsole.getMedal(tb.getMedalId());
        if (mb == null) {
            return ErrorCode.RMatch_Medal_Bean;
        }

        daily.dailyAward = true;
        upTeamDaily(teamId, daily);
        List<PropSimple> props = propManager.addPropList(teamId, Collections.emptyList(),
            Collections.singletonList(mb.getDailyDrop()), true, mc);
        log.debug("rmatch receiveDailyAward. tid {} medal {} props {}", teamId, mb.getId(), props);
        PropListResp resp = PropListResp.newBuilder()
            .addAllProps(PropManager.getPropSimpleListData(props))
            .build();
        sendMessage(teamId, resp, ServiceCode.RMatch_Receive_Daily_Award_Push);
        return ErrorCode.Success;
    }

    /** 领取首次奖励 */
    @ClientMethod(code = ServiceCode.RMatch_Receive_First_Award)
    public void receiveFirstAward(int awardId) {
        long teamId = getTeamId();
        ErrorCode ret = receiveFirstAward0(teamId, awardId);
        sendMsg(ret);
        log.debug("rmatch receiveFirstAward. tid {} ret {}", teamId, ret);
    }

    /** 领取首次奖励 */
    private ErrorCode receiveFirstAward0(long teamId, int awardId) {
        FirstAwardBean fb = RankedMatchConsole.getFirstAward(awardId);
        if (fb == null) {
            return ErrorCode.RMatch_FirstAward_Bean;
        }
        RpcResp<Object> rresp = RpcTask.ask(CrossCode.XRaned_Receive_First_Award, teamId, awardId);
        if (rresp.isError()) {
            return rresp.ret;
        }

        List<PropSimple> props = propManager.addPropList(teamId, Collections.emptyList(),
            Collections.singletonList(fb.getDrop()), true, mc);
        log.debug("rmatch receiveFirstAward. tid {} rating {} props {}", teamId, fb.getRating(), props);
        PropListResp resp = PropListResp.newBuilder()
            .setVi1(awardId)
            .addAllProps(PropManager.getPropSimpleListData(props))
            .build();
        sendMessage(teamId, resp, ServiceCode.RMatch__Receive_First_Award_Push);
        return ErrorCode.Success;
    }

    public boolean inBattle(long teamId) {
        TeamStatus status = teamStatusManager.get(teamId);
        TeamBattleStatus tbs = status.getBattle(EBattleType.Ranked_Match);
        return tbs != null &&
            (tbs.getStatus() == EBattleRoomStatus.比赛中 || tbs.getStatus() == EBattleRoomStatus.等待开启);
    }

    /** 是否在每天开启时间段 */
    public boolean inMatchTime(GlobalBean gb, long curr) {
        long mn = DateTimeUtil.midnight();
        for (TupleTime tdt : gb.rMatchTime) {
            if (mn + tdt.timeMillis1 <= curr && curr <= mn + tdt.timeMillis2) {
                return true;
            }
        }
        return false;
    }

    /** 中心服务器开启比赛 */
    @RPCMethod(code = CrossCode.XRaned_Start_Match_Push, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void masterNodeStartMatch(long teamId, long battleId, EBattleType bt, long home, long away, String nodeName) {
        log.debug("rmath masterNodeStartMatch. tid {} bid {} bt {} remote server {},", teamId, battleId, bt, nodeName);
        if (GameSource.isNPC(teamId)) {
            return;
        }
        TeamStatus ts = teamStatusManager.get(teamId);
        String nodeIp = nodeManager.getServerIP(nodeName);
        ts.putBattle(bt, battleId, nodeIp, nodeName, EBattleRoomStatus.比赛中);
        log.debug("net {} server ip {}", GameSource.net, nodeIp);
        sendMessage(teamId, BattlePb.battleStartResp(bt, battleId, home, away, nodeIp), ServiceCode.Battle_Start_Push);
    }

    /** 中心服务器结束比赛比赛 */
    @RPCMethod(code = CrossCode.XRaned_End_Match_Push, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void masterNodeEndMatch(BattleInfo info, BattleTeam home, BattleTeam away, EndReport report) {
        GlobalBean gb = ConfigConsole.global();
        List<PropSimple> props = DropConsole.getAndRoll(gb.rMatchDrop);
        List<PropSimple> additionProps = DropConsole.getAndRoll(gb.rMatchAdditionDrop);
        report.appendWinAwardList(props);
        report.appendLossAwardList(props);
        int homemc = teamDayStatisticsManager.getTeamDayStatistics(report.getHomeTeamId()).getPkCount(info.getBattleType());
        if (homemc < gb.rMatchDropAdditionNum) {
            report.appendHomeAwardList(additionProps);
        }
        int awaymc = teamDayStatisticsManager.getTeamDayStatistics(report.getAwayTeamId()).getPkCount(info.getBattleType());
        if (awaymc < gb.rMatchDropAdditionNum) {
            report.appendAwayAwardList(additionProps);
        }
        log.debug("rmatch end match. bid {} home {} mc {} away {} mc {} score {}:{}", info.getBattleId(),
            report.getHomeTeamId(), homemc, report.getAwayTeamId(), awaymc, report.getHomeScore(),
            report.getAwayScore());
        localBattleManager.sendEndMain(info, home, away, report, false);

        //天梯赛积分广播
        RankedMatchEnd rme = report.getAdditional(EBattleAttribute.Ranked_Match_End);
        if (rme != null) {
            RankedTeam homert = rme.getHome();
            RankedTeam awayrt = rme.getAway();
            sendNotice(home, homert);
            sendNotice(away, awayrt);
            taskManager.updateTask(home.getTeamId(),ETaskCondition.更新天梯积分,homert.getFinalRating(),homert.getFinalRating()+"");
            taskManager.updateTask(away.getTeamId(),ETaskCondition.更新天梯积分,awayrt.getFinalRating(),awayrt.getFinalRating()+"");
        }
    }

    private static List<Integer> tipRatingCfg = new ArrayList<>();

    static {
        tipRatingCfg.add(ChatPushConst.CHAT_PUSH_LRMATCH_RATING_1300);
        tipRatingCfg.add(ChatPushConst.CHAT_PUSH_LRMATCH_RATING_1500);
        tipRatingCfg.add(ChatPushConst.CHAT_PUSH_LRMATCH_RATING_1650);
        tipRatingCfg.add(ChatPushConst.CHAT_PUSH_LRMATCH_RATING_1800);
        tipRatingCfg.add(ChatPushConst.CHAT_PUSH_LRMATCH_RATING_1850);
    }

    private void sendNotice(BattleTeam bt, RankedTeam rt) {
        if (bt.getNodeName().equals(GameSource.serverName)) {
            for (Integer tipRating : tipRatingCfg) {//发广播
                if (rt.getSrcRating() < tipRating && rt.getFinalRating() >= tipRating) {
                    chatManager.pushGameTip(EGameTip.天梯赛积分, 0, bt.getName(), rt.getFinalRating() + "");
                }
            }
        }
    }

    //    /** 聊天. 天梯赛积分跑马灯推送. key = pre + teamId, value已推送过的积分段 */
    //    public static String Redis_Chat_Push_LcRanked_Rating = "chat_push_lcRanked_rating_" + teamId;;

    /**
     * 中心服务器发送赛季奖励邮件.
     * <p>
     * 恭喜，你在第${rsid}赛季的天梯赛中，获得了${rtid}段位，评分${rating}，段位排名为${rank}，获得如下奖励。
     * </p>
     */
    @RPCMethod(code = CrossCode.XRaned_Send_Mail_Push, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void masterNodeSendMail(long tid, Season currSeason, int mailViewId, int dropId) {
        ImmutableList<String> contentParams = ImmutableList.of(
            "" + currSeason.getId(),
            "" + currSeason.getTierId(),
            "" + currSeason.getRating(),
            "" + currSeason.getRank()
            //                ,"" + currSeason.getMatchCount()
        );
        DropBean db = DropConsole.getDrop(dropId);
        List<PropSimple> props = db != null ? db.roll() : Collections.emptyList();
        if (log.isDebugEnabled()) {
            log.debug("rmatch sendmail. tid {} mvid {} drop {} season {}", tid, mailViewId, dropId, currSeason);
        }
        teamEmailManager.sendEmailWithParamTemplate(tid, mailViewId, ImmutableList.of(), contentParams, props);
    }

    /** gm. 刷新段位排行榜 */
    public synchronized ErrorCode gmRefreshRank() {
        RpcTask.tell(CrossCode.XRaned_Gm_Up_Rank, null, getTeamId());
        return ErrorCode.Success;
    }

    /** gm. 加入比赛 */
    public ErrorCode gmJoin(long teamId) {
        Team team = teamManager.getTeam(teamId);
        if (inBattle(teamId)) {
            return ErrorCode.Battle_In;
        }
        RpcResp<Object> resp =
            RpcTask.ask(CrossCode.XRanked_Join_Pool, new TeamNode(teamId, GameSource.serverName), team.getLevel());
        return resp.ret;
    }

    /** gm. 强制发放赛季奖励 */
    public synchronized ErrorCode gmSeasonEndAward() {
        RpcTask.tell(CrossCode.XRaned_Gm_Season_End_Award, null, getTeamId());
        return ErrorCode.Success;
    }

    /** gm. 修改评分 */
    public ErrorCode gmUpdateRating(long teamId, int num) {
        RpcTask.tell(CrossCode.XRaned_Gm_Up_Rating, null, teamId, num);
        return ErrorCode.Success;
    }

    /** 重置球队每日状态 */
    public void gmResetDaily(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Ranked_Match_Daily);
        redis.del(key);
    }

    /** 球队每日信息 */
    private void upTeamDaily(long teamId, Daily daily) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Ranked_Match_Daily);
        redis.set(key, daily, RedisKey.WEEK);
    }

    /** 球队每日信息 */
    private Daily getTeamDaily(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Ranked_Match_Daily);
        Daily daily = redis.getObj(key);
        if (daily == null) {
            daily = new Daily();
            redis.set(key, daily, RedisKey.WEEK);
        }
        return daily;
    }

    /** 球队每日信息 */
    static final class Daily implements Serializable {
        private static final long serialVersionUID = 1927417283654941092L;
        /** 每日奖励领取信息 */
        private boolean dailyAward;

        Daily() {
        }
    }

}
