package com.ftkj.manager.pk;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.cfg.EmailViewBean;
import com.ftkj.cfg.RankedMatchBean.RankedMatchSeasonBean;
import com.ftkj.cfg.RankedMatchBean.RatingConvertBean;
import com.ftkj.cfg.RankedMatchMedalBean;
import com.ftkj.cfg.RankedMatchTierBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ConfigConsole.GlobalBean;
import com.ftkj.console.RankedMatchConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.db.dao.pk.XRankedMatchDAO;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.battle.BattleAPI;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.handle.BattleRanked;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattleInfo;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.RankedMatchEnd;
import com.ftkj.manager.match.RankedMatch;
import com.ftkj.manager.match.RankedMatch.Season;
import com.ftkj.manager.match.RankedMatch.SeasonHistory;
import com.ftkj.manager.team.TeamNode;
import com.ftkj.proto.BattlePB;
import com.ftkj.proto.CommonPB.TeamSimpleData;
import com.ftkj.proto.GameLogPB;
import com.ftkj.proto.RankedMatchPb.RMatchHisListResp;
import com.ftkj.proto.RankedMatchPb.RMatchMedalRankResp;
import com.ftkj.proto.RankedMatchPb.RMatchTeamRankResp;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.server.rpc.IZKMaster;
import com.ftkj.server.rpc.RpcTask;
import com.ftkj.server.rpc.RpcTask.RpcResp;
import com.ftkj.tool.quartz.QuartzServer;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.util.BitUtil;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.Page;
import com.ftkj.util.tuple.Tuple2Long;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/** 跨服天梯赛 */
public class XRankedMatchManager extends XBaseManager implements IZKMaster {
    private static final Logger log = LoggerFactory.getLogger(XRankedMatchManager.class);
    private static final Logger poollog = LoggerFactory.getLogger("com.ftkj.manager.pk.XRankedMatchManagerPool");
    @IOC
    private XRankedMatchDAO rankedMatchDao;
    @IOC
    private CrossBattleManager crossBattleManager;
    @IOC
    private CrossPVPManager crossPVPManager;
    @IOC
    private JedisUtil redis;
    private final AtomicLong seasonHisId = new AtomicLong();
    private final AtomicLong roomId = new AtomicLong();
    /** 排行榜最后一次更新时间 */
    private volatile Long upRankJobLastUpTime;
    //    private Map<Integer, ScheduledFuture<?>> seasonEndSfs = new ConcurrentHashMap<>();
    /** 球队天梯赛信息. map[tid, RankedMatch] */
    private ConcurrentMap<Long, RankedMatch> teams = new ConcurrentHashMap<>();
    /** 所有将要或者已经在匹配池的球队(过渡列表), 匹配成功后移动到 {@link #matchingTeams}. map[tid, V]. */
    private ConcurrentMap<Long, MatchTeam> joinTeams = new ConcurrentHashMap<>();
    /** 正在比赛的球队信息. map[tid, V] */
    private ConcurrentMap<Long, MatchTeam> matchingTeams = new ConcurrentHashMap<>();
    /** 比赛信息. map[Room.id, Room] */
    private ConcurrentMap<Long, Room> rooms = new ConcurrentHashMap<>();

    @SuppressWarnings("unused")
    public XRankedMatchManager() {
        this(true);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public XRankedMatchManager(boolean init) {
        super(init);
        if (init) {
            redis = InstanceFactory.get().getInstance(JedisUtil.class);
        }
    }

    /** 系统启动时初始化 */
    @Override
    public void masterInit(String nodeName) {
        long maxHisId = rankedMatchDao.findHisMaxId();
        seasonHisId.set(maxHisId);
        addSeasonEndTask();
        GlobalBean gb = ConfigConsole.getGlobal();
        int roundTime = gb.rMatchMmRoundTime;
        QuartzServer.scheduleWithFixedDelay(this::processMatchPool, roundTime, roundTime, TimeUnit.SECONDS);
        upRankJobLastUpTime = redis.getObj(RedisKey.Ranked_Match_Medal_Rank_Up_Time);
        if (upRankJobLastUpTime == null) {
            upRankJobLastUpTime = 0L;
        }
        log.info("xrmatch init. max his id {}. match pool round time {}s. uprankjob time {}", maxHisId, roundTime,
            upRankJobLastUpTime);
    }

    @Override
    public void instanceAfter() {
    }

    /** 赛季结束 */
    private void addSeasonEndTask() {
        long curr = System.currentTimeMillis();
        RankedMatchSeasonBean currOrNextSeason = RankedMatchConsole.getCurrOrNextSeason(curr);
        if (currOrNextSeason == null) {
            log.warn("xrmatch not found curr or next season config");
            return;
        }
        int sid = currOrNextSeason.getId();
        long endMillis = currOrNextSeason.getEnd();
        ScheduledFuture<?> sf = QuartzServer.schedule(() -> seasonEnd(sid), endMillis - curr, TimeUnit.MILLISECONDS);
        log.info("xrmatch. season {} end trigger after {}", sid, DateTimeUtil.duration(sf));
        //        seasonEndSfs.put(sid, sf);
    }

    /** 赛季结束. 记录数据, 发放奖励 */
    private void seasonEnd(int seasonId) {
        try {
            seasonEnd0(seasonId, true);
            log.info("xrmatch seasonEnd handle done. season {}", seasonId);
        } finally {
            addSeasonEndTask();
        }
    }

    /** 根据规则对匹配池进行配对, 配对成功的进入比赛 */
    private synchronized void processMatchPool() {
        /*
         * 玩家进行匹配后，进入匹配池，
         * 匹配池每20S排序一次
         * 排序后根据顺序由高到低两两匹配（由第一匹配第二，第三匹配第四），
         * 若匹配后分差少于100即匹配成功，开始比赛
         * 若匹配后分差大于100，则把第一名剔除重新匹配
         * 第一名则重新进入下一轮匹配池
         * 玩家第四次进入匹配池时必然会匹配成功（无视分差）
         */
        final int poolsize = joinTeams.size();
        if (poolsize <= 1) {
            log.debug("xrmatch start process match pool. pool size {} <= 1. ret.", poolsize);
            return;
        }
        final List<MatchTeam> teams = new ArrayList<>(joinTeams.values());
        teams.sort((o1, o2) -> Integer.compare(o2.getRating(), o1.getRating()));
        final int size = teams.size();
        final List<Tuple2Long> success = new ArrayList<>(Math.max(8, size / 2));
        final GlobalBean gb = ConfigConsole.getGlobal();
        final int ratingGap = gb.rMatchMmGap;
        final int processMaxNum = gb.rMatchMmMaxNum;
        log.info("xrmatch start process match pool. rating gap {}. teams size {} head {} tail {}", ratingGap,
            size, teams.get(0), teams.get(size - 1));

        processPool(success, ratingGap, processMaxNum, teams.iterator());// first process
        Iterator<MatchTeam> it = teams.iterator();
        if (it.hasNext()) {
            MatchTeam first = it.next();
            if (!first.findTarget) {//第一名剔除重新匹配
                log.debug("xrmatch pool. first {} not found target, remove and process again", first);
                processPool(success, ratingGap, processMaxNum, teams.iterator()); // second process
            }
        }
        List<Room> rooms = new ArrayList<>(success.size());
        long curr = System.currentTimeMillis();
        for (Tuple2Long tp : success) {
            moveToMatchingTeams(tp._1());
            moveToMatchingTeams(tp._2());
            Room room = new Room(roomId.incrementAndGet(), matchingTeams.get(tp._1()),
                matchingTeams.get(tp._2()), curr);
            rooms.add(room);
        }
        if (!rooms.isEmpty()) {
            QuartzServer.submit(() -> startMatch(rooms));
        }
        log.info("xrmatch pool. processed. find {} tuple. remain {} teams", success.size(), size - success.size() * 2);
    }

    private void moveToMatchingTeams(long teamId) {
        MatchTeam mt = joinTeams.get(teamId);
        if (mt != null) {
            matchingTeams.put(mt.teamId, mt);
            joinTeams.remove(mt.teamId);
        }
    }

    /** 处理匹配池 */
    private void processPool(List<Tuple2Long> success, int ratingGap, int processMaxNum, Iterator<MatchTeam> it) {
        outer:
        while (it.hasNext()) {
            MatchTeam mt1 = it.next();
            while (mt1.findTarget) {
                poollog.trace("xrmatch pool. team1 {} has target, find next", mt1.teamId);
                if (!it.hasNext()) {
                    break outer;
                }
                mt1 = it.next();
            }
            if (!it.hasNext()) {
                break;
            }
            MatchTeam mt2 = it.next();
            while (mt2.findTarget) {
                poollog.trace("xrmatch pool. team2 {} has target, find next", mt2.teamId);
                if (!it.hasNext()) {
                    break outer;
                }
                mt2 = it.next();
            }
            log.trace("xrmatch pool. handle team1 {} team2 {}", mt1, mt2);
            if (Math.abs(mt1.rating - mt2.rating) > ratingGap &&
                mt1.processCount < processMaxNum && mt2.processCount < processMaxNum) {
                mt1.processCount++;
                mt2.processCount++;
                continue;
            }
            mt1.findTarget = true;
            mt2.findTarget = true;
            success.add(Tuple2Long.create(mt1.teamId, mt2.teamId));
            log.trace("xrmatch pool. find each other. tid1 {} tid2 {}", mt1.teamId, mt2.teamId);
        }
    }

    /** 异步开启比赛. 跨服获取赛前信息比较耗时, 异步处理. */
    private void startMatch(List<Room> rooms) {
        log.info("xrmatch start match. rooms size {}", rooms.size());
        for (Room room : rooms) {
            try {
                long bid = crossBattleManager.getBattleId();
                if (startMatch(bid, room)) {
                    this.rooms.put(room.id, room);
                } else {
                    rejoin(room);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                try {
                    remove(room);
                } catch (Exception e1) {
                    log.error(e1.getMessage(), e);
                }
            }
        }
        log.info("xrmatch start match done. rooms size {}", rooms.size());
    }

    private void rejoin(Room room) {
        remove(room);
        joinTeams.put(room.home.teamId, room.home);
        joinTeams.put(room.away.teamId, room.away);
    }

    private void remove(Room room) {
        matchingTeams.remove(room.home.teamId);
        matchingTeams.remove(room.away.teamId);
        room.home.findTarget = false;
        room.away.findTarget = false;
    }

    /** 跨服天梯赛. 初始化比赛, 开始比赛 */
    private boolean startMatch(long battleId, Room room) {
        long roomId = room.id;
        TeamNode home = room.home.node;
        TeamNode away = room.away.node;
        long htid = home.getTeamId();
        long atid = away.getTeamId();
        log.info("xrmatch startmatch. room {} home {} away {} bid {}", roomId, htid, atid, battleId);

        RpcResp<BattleTeam> hr = RpcTask.ask(CrossCode.GetBattleTeam, home.getNodeName(), htid);
        if (hr.isError()) {
            log.warn("xrmatch startmatch. room {} not get team. team {} ret {}", roomId, htid, hr.ret);
            return false;
        }
        RpcResp<BattleTeam> ar = RpcTask.ask(CrossCode.GetBattleTeam, away.getNodeName(), atid);
        if (ar.isError()) {
            log.warn("xrmatch startmatch. room {} not get team. team {} ret {}", roomId, atid, hr.ret);
            return false;
        }
        calcSingleMatchRating(room.home, room.away);//计算单场比赛双方的分数

        EBattleType bt = EBattleType.Ranked_Match;
        BattleSource bs = crossPVPManager.createBattleSource(battleId, hr.t, ar.t, bt);
        BattleRanked battle = new BattleRanked(bs);
        battle.setEnd(this::endMatch0);
        battle.setRound(crossPVPManager.getRoundReport());
        battle.init();

        BattleAttribute ba = new BattleAttribute(0);
        ba.addVal(EBattleAttribute.Room_Id, roomId);
        battle.getBattleSource().addBattleAttribute(ba);

        //放入队列并开始运行比赛
        BattleAPI.getInstance().putBattle(battle);
        //告诉逻辑服务器，该玩家匹配成功比赛开启
        int upLcTeam = CrossCode.XRaned_Start_Match_Push;
        RpcTask.tell(upLcTeam, home.getNodeName(), htid, battleId, bt, htid, atid, GameSource.serverName);
        RpcTask.tell(upLcTeam, away.getNodeName(), atid, battleId, bt, htid, atid, GameSource.serverName);
        log.info("xrmatch startmatch done. room {} home {} away {} bid {}", roomId, htid, atid, battleId);
        return true;
    }

    /** 计算单场比赛双方的分数 */
    private void calcSingleMatchRating(MatchTeam home, MatchTeam away) {
        /*
         * ELO 算法
         * D 为分差,即比如 A 与 B 对战,
         * A 的期望得分分差算法 DA 为(A 的当前分数-B 的当前分数)
         * B 的期望得分分差算法 DB 为(B 的当前分数-A 的当前分数)
         * P = 1/(1 + 10^(-d/400))
         * S=K*(R-P)*(1+连胜系数)*(修正系数)(单场得分=得分系数*(实际结果-预期胜率))*(1+连胜系数)*(修正系数)
         * 得分系数 K 是一个系统预设的常数,根据玩家所在分段取值(配置),
         * 计算结果最后取整为向下取整;
         * R 有且只有 2 个取值,胜利为 1,失败为 0。;
         * 连胜系数为配置;
         * 修正系数存在于低于 1500 以下且失败时(配置);
         * <p>
         * plot | 1/(1 + 10^(-x/400)) | x = -1000 to 1000
         * http://www.wolframalpha.com/input/?i=plot+%7C+1%2F(1+%2B+10%5E(-x%2F400))+%7C+x+%3D+-1000+to+1000
         */
        final RankedMatch hrm = getRankedMatch(home.teamId);
        final RankedMatch arm = getRankedMatch(away.teamId);
        final boolean hsb = noCurrSeason(hrm);
        final boolean asb = noCurrSeason(arm);
        if (hsb || asb) {
            log.warn("xrmatch calcrating. home {} {} or away {} {} curr season is null",
                home.teamId, hsb, away.teamId, asb);
            return;
        }
        final Season hs = hrm.getCurrSeason();
        final Season as = arm.getCurrSeason();
        final RankedMatchTierBean hm = RankedMatchConsole.getTier(hs.getTierId());
        final RankedMatchTierBean am = RankedMatchConsole.getTier(as.getTierId());

        final int dh = home.getRating() - away.getRating();
        final int da = -dh;
        float kh = hm != null ? hm.getRatingFactor() : 1;
        float ka = am != null ? am.getRatingFactor() : 1;
        final int rwin = 1;
        final int rlose = 0;
        final double ph = 1 / (1 + Math.pow(10.0, (-dh / 400.0)));
        final double pa = 1 / (1 + Math.pow(10.0, (-da / 400.0)));
        final int wch = hrm.getWinningStreak();
        final int wca = arm.getWinningStreak();
        final float streakh = RankedMatchConsole.getWinningStreak(wch, 0);
        final float streaka = RankedMatchConsole.getWinningStreak(wca, 0);
        final float ffh = hm != null ? hm.getFailCorrectionFactor() : 1;
        final float ffa = am != null ? am.getFailCorrectionFactor() : 1;
        final double shwin = kh * (rwin - ph) * (1 + streakh);
        final double shlose = kh * (rlose - ph) * (1 + streakh) * ffh;
        final double sawin = ka * (rwin - pa) * (1 + streaka);
        final double salose = ka * (rlose - pa) * (1 + streaka) * ffa;
        if (poollog.isTraceEnabled()) {
            poollog.trace("xrmatch calcrating. home tid {} d {} k {} p {} wc {} stk {} ff {} wins {} loses {}, " +
                    "away tid {} d {} k {} p {} wc {} stk {} ff {} wins {} loses {}",
                home.teamId, dh, kh, ph, wch, streakh, ffh, shwin, shlose,
                away.teamId, da, ka, pa, wca, streaka, ffa, sawin, salose);
        }
        home.winRating = (int) shwin;
        home.loseRating = (int) shlose;
        away.winRating = (int) sawin;
        away.loseRating = (int) salose;
    }

    private boolean noCurrSeason(RankedMatch hrm) {
        return hrm == null || hrm.getCurrSeason() == null;
    }

    /** 比赛结束 */
    private void endMatch0(BattleSource bs) {
        final BattleAttribute ba = bs.getOrCreateAttributeMap(0);
        final Long roomId = ba.getVal(EBattleAttribute.Room_Id);
        try {
            endMatch1(bs, roomId);
        } catch (Exception e) {
            log.error("xrmatch " + e.getMessage(), e);
        } finally {
            final EndReport report = bs.getEndReport();
            matchingTeams.remove(report.getHomeTeamId());
            matchingTeams.remove(report.getAwayTeamId());
            if (roomId != null) {
                rooms.remove(roomId);
            }
        }
    }

    /** 比赛结束 */
    private void endMatch1(BattleSource bs, Long roomId) {
        final EndReport report = bs.getEndReport();
        if (this.roomId == null) {
            log.warn("xrmatch end match. room null. home {} away {}", report.getHomeTeamId(), report.getAwayTeamId());
            return;
        }
        if (!updateRating(report, roomId)) {//更新评分和段位
            return;
        }
        //发送结束消息到客户端
        BattleInfo bi = bs.getInfo();
        BattlePB.BattleEndMainData data = BattlePb.battleEndMainData(bi.getBattleId(), bs.getEndReport());
        GameLogPB.BattleEndLogData endLog = BattlePb.getBattleEndLogData(bs.getHome(), bs.getAway());
        redis.set(RedisKey.Battle_End_Source + bs.getId(), endLog, RedisKey.DAY2);
        sendMessage(ServiceConsole.getBattleKey(bi.getBattleId()), data, ServiceCode.Battle_PK_Stage_Round_Main_End);
        BattleTeam home = bs.getHome();
        BattleTeam away = bs.getAway();
        RpcTask.tell(CrossCode.XRaned_End_Match_Push, home.getNodeName(), bi, home, away, report);
        if (!home.getNodeName().equals(away.getNodeName())) {//同一个本地服务器只发送一次结算推送
            RpcTask.tell(CrossCode.XRaned_End_Match_Push, away.getNodeName(), bi, home, away, report);
        }
    }

    /** 比赛结束. 更新评分和段位 */
    private synchronized boolean updateRating(EndReport report, Long roomId) {
        final long curr = System.currentTimeMillis();
        final Room room = rooms.get(roomId);
        final GlobalBean gb = ConfigConsole.global();
        final boolean degrade = gb.rMatchDegradeTier;
        final int minRating = gb.rMatchMinRating;
        final RankedMatch hrm = getRankedMatch(report.getHomeTeamId());
        final RankedMatch arm = getRankedMatch(report.getAwayTeamId());
        if (room == null || noCurrSeason(hrm) || noCurrSeason(arm)) {
            log.warn("xramtch end match. room {} null {} or hrm {} null {} or arm {} null {}", roomId, room == null,
                report.getHomeTeamId(), !noCurrSeason(hrm), report.getAwayTeamId(), !noCurrSeason(arm));
            return false;
        }
        final Season hs = hrm.getCurrSeason();
        final Season as = arm.getCurrSeason();
        log.debug("xrmatch end match. roomid {} home tid {} away tid {} score {}:{}. degrade {} hs {} as {}",
            roomId, report.getHomeTeamId(), report.getAwayTeamId(), report.getHomeScore(), report.getAwayScore(),
            degrade, hs, as);

        final RankedMatchEnd me = new RankedMatchEnd();
        me.getHome().setSrc(hs.getTierId(), hs.getRating());
        me.getAway().setSrc(as.getTierId(), as.getRating());
        final int hsrcrating = hs.getRating();
        final int asrcrating = as.getRating();
        if (report.getWinTeamId() == hrm.getTeamId()) {//更新当前赛季评分
            updateWinAndLoseRating(minRating, hrm, arm, hs, as, room.home.winRating, room.away.loseRating);
        } else {
            updateWinAndLoseRating(minRating, arm, hrm, as, hs, room.away.winRating, room.home.loseRating);
        }
        updateCommon(curr, hrm, hs);
        updateCommon(curr, arm, as);
        //更新段位和排名
        updateTierAndRank(calcNewMedel(hs.getTierId(), degrade, hsrcrating, hs.getRating()), hrm.getTeamId(), hs);
        updateTierAndRank(calcNewMedel(as.getTierId(), degrade, asrcrating, as.getRating()), arm.getTeamId(), as);
        hrm.save();
        arm.save();

        me.getHome().setAfter(hs.getTierId(), hs.getRating(), hs.getRating() - hsrcrating);
        me.getAway().setAfter(as.getTierId(), as.getRating(), as.getRating() - asrcrating);
        report.addAdditional(EBattleAttribute.Ranked_Match_End, me);
        if (log.isDebugEnabled()) {
            log.debug("xrmatch end match. roomid {} home {} away {} matchend {}", roomId, hrm.getTeamId(),
                arm.getTeamId(), me);
        }
        return true;
    }

    /** 比赛结束. 更新共同数据 */
    private void updateCommon(long curr, RankedMatch rm, Season s) {
        s.setMatchCount(s.getMatchCount() + 1);
        rm.setTotalMatchCount(rm.getTotalMatchCount() + 1);
        rm.setLastMatchTime(curr);
    }

    /** 比赛结束. 更新赛季评分和统计数据 */
    private void updateWinAndLoseRating(int minRating,
                                        RankedMatch winrm, RankedMatch loserm,
                                        Season wins, Season loses,
                                        int winRating, int loseRating) {
        wins.setRating(Math.max(minRating, wins.getRating() + winRating));
        loses.setRating(Math.max(minRating, loses.getRating() + loseRating));

        winrm.setWinningStreak(winrm.getWinningStreak() + 1);
        winrm.setTotalWinCount(winrm.getTotalMatchCount() + 1);
        if (wins.getWinningStreakMax() < winrm.getWinningStreak()) {
            wins.setWinningStreakMax(winrm.getWinningStreak());
        }
        wins.setWinCount(wins.getWinCount() + 1);
        loserm.setWinningStreak(0);
    }

    /** 更新层级和排行榜 */
    private void updateTierAndRank(RankedMatchTierBean newtb, long tid, Season s) {
        if (newtb != null) {
            if (newtb.getId() != s.getTierId()) {
                RankedMatchTierBean oldtb = RankedMatchConsole.getTier(s.getTierId());
                if (oldtb != null) {
                    redis.zrem(getCurrMedalRankKey(oldtb.getMedalId()), str(tid));//remove old rank
                }
                s.setTierId(newtb.getId());
            }
            redis.zadd(getCurrMedalRankKey(newtb.getMedalId()), s.getRating(), str(tid));//add new score
        }
    }

    /**
     * 赛季结束. 计算积分, 发放奖励
     *
     * @param autoCleanMedalRank 发放奖励后, 如果为 true, 转换赛季和评分, 重新计算排行榜
     */
    private synchronized void seasonEnd0(int sid, boolean autoCleanMedalRank) {
        log.info("xrmatch seasonEnd handle. season {} autoCleanMedalRank {}", sid, autoCleanMedalRank);
        upRank(sid);//先更新排行榜

        GlobalBean gb = ConfigConsole.global();
        RankedMatchSeasonBean sb = RankedMatchConsole.getSeason(sid);
        final int nextSid = sb.getNextId();

        for (Map.Entry<Integer, RankedMatchMedalBean> e : RankedMatchConsole.getMedals().entrySet()) {
            Integer mid = e.getKey();
            RankedMatchMedalBean mb = e.getValue();

            String hisSeasonMedalKey = getHisSeasonMedalRankKey(sid, mid);
            String currSeasonMedalKey = getCurrMedalRankKey(mid);
            Long hisCount = redis.zcard(hisSeasonMedalKey);//count
            Long currCount = redis.zcard(currSeasonMedalKey);//count
            if (hisCount > 0) {
                log.warn("xrmatch seasonEnd. sid {} mid {} his count {} > 0", sid, mid, hisCount);
            }
            redis.zunionstore(hisSeasonMedalKey, currSeasonMedalKey);// copy curr season to his season
            List<String> revrange = redis.zrevrange(currSeasonMedalKey, 0, -1);//get all
            log.info("xrmatch seasonEnd. sid {} mid {} rank size {} revs {} global min num {}",
                sid, mid, currCount, revrange.size(), gb.rMatchSeasonAwardMinMatch);

            Map<String, Double> scoreMembers = new HashMap<>();
            for (String tidStr : revrange) {//处理排行榜中的球队
                long tid = toLong(tidStr);
                RankedMatch rm = getRankedMatch(tid);
                if (rm == null || rm.getCurrSeason() == null) {
                    log.warn("xrmatch seasonEnd. sid {} mid {} tid {} rm is null", sid, mid, tid);
                    continue;
                }
                final Season currs = new Season(rm.getCurrSeason());
                SeasonHistory sh = new SeasonHistory(seasonHisId.incrementAndGet(), rm, rm.getCurrSeason());
                sh.save();
                if (currs.getMatchCount() >= gb.rMatchSeasonAwardMinMatch) {//发送邮件
                    QuartzServer.submit(() -> RpcTask.tell(CrossCode.XRaned_Send_Mail_Push, rm.getNodeName(),
                        rm.getTeamId(), currs, EmailViewBean.Ranked_Match_Season, mb.getSeasonDrop()));
                }
                if (autoCleanMedalRank) {//转换赛季评分, 重新计算段位, 更新排行榜
                    Season nexts = convertSeason(nextSid, new Season(rm.getCurrSeason()));
                    rm.setLastSeason(rm.getCurrSeason());
                    rm.setCurrSeason(nexts);
                    rm.save();
                    scoreMembers.put(str(tid), (double) nexts.getRating());//add new season score
                }
                if (log.isDebugEnabled()) {
                    log.debug("xrmatch seasonEnd. sendmail. tid {} drop {} season {}", tid, mb.getSeasonDrop(), currs);
                }
            }//end rev rank
            if (autoCleanMedalRank) {
                redis.zremrangeByRank(currSeasonMedalKey, 0, -1);
                redis.zadd(currSeasonMedalKey, scoreMembers);
            }
        }
    }

    /** 球队下线 */
    @RPCMethod(code = CrossCode.XRaned_Team_Offline, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void teamOffline(long teamId) {
        teams.remove(teamId);
        synchronized (this) {
            MatchTeam mt = joinTeams.get(teamId);
            if (mt != null && !mt.findTarget) {
                joinTeams.remove(teamId);
            }
        }

    }

    /** gm命令 强制发放赛季奖励 */
    @RPCMethod(code = CrossCode.XRaned_Gm_Season_End_Award, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void gmSeasonEndAward(long teamId) {
        log.warn("xrmatch gmSeasonEndAward. tid {}", teamId);
        RankedMatchSeasonBean currs = RankedMatchConsole.getCurrSeason(System.currentTimeMillis());
        if (currs == null) {
            log.warn("xrmatch gmSeasonEndAward. curr season null");
            return;
        }
        seasonEnd0(currs.getId(), false);
    }

    /** gm命令 修改评分 */
    @RPCMethod(code = CrossCode.XRaned_Gm_Up_Rating, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void gmUpdateRating(long teamId, int num) {
        log.warn("xrmatch gmUpdateRating. tid {} num {}", teamId, num);
        RankedMatch rm = getRankedMatch(teamId);
        if (rm == null || rm.getCurrSeason() == null || num == 0) {
            return;
        }
        Season s = rm.getCurrSeason();
        RankedMatchTierBean tb = RankedMatchConsole.getTier(s.getTierId());
        if (tb == null) {
            return;
        }
        int srcrating = s.getRating();
        s.setRating(Math.max(0, s.getRating() + num));
        rm.save();
        updateTierAndRank(calcNewMedel(tb.getId(), ConfigConsole.getGlobal().rMatchDegradeTier,
            srcrating, s.getRating()), teamId, s);
        log.warn("xrmatch gmUpdateRating. tid {} num {} srcr {} rm {}", teamId, num, srcrating, rm);
    }

    /** gm命令 刷新段位排名 */
    @RPCMethod(code = CrossCode.XRaned_Gm_Up_Rank, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void gmUpRank(long teamId) {
        log.info("xrmatch gmUpRank. tid {}", teamId);
        upRank();
    }

    /** 刷新段位排名(排行榜) */
    public synchronized void upRankJob() {
        if (upRank()) {
            upRankJobLastUpTime = System.currentTimeMillis();
            redis.set(RedisKey.Ranked_Match_Medal_Rank_Up_Time, upRankJobLastUpTime);
        }
        log.info("xrmatch uprankjob");
    }

    /** 刷新段位排名(排行榜) */
    private synchronized boolean upRank() {
        RankedMatchSeasonBean currs = RankedMatchConsole.getCurrSeason(System.currentTimeMillis());
        if (currs == null) {
            log.warn("xrmatch refresh rank. curr season null");
            return false;
        }

        upRank(currs.getId());
        return true;
    }

    /** 刷新段位排名(排行榜) */
    private synchronized void upRank(int sid) {
        final String today = getTodayYYYYMMDD();
        for (Integer mid : RankedMatchConsole.getMedals().keySet()) {
            String currMedalKey = getCurrMedalRankKey(mid);
            String hisDayMedalKey = getHisDayMedalRankKey(today, mid);
            List<String> revrange = redis.zrevrange(getCurrMedalRankKey(mid), 0, -1);//all
            redis.zunionstore(hisDayMedalKey, currMedalKey);// copy curr season to his day
            log.info("xrmatch upRank. sid {} mid {} rank zrevrange {}", sid, mid, revrange.size());
            int rank = 0;
            for (String tidStr : revrange) {
                rank++;
                long tid = toLong(tidStr);
                RankedMatch rm = getRankedMatch(tid);
                if (rm == null || rm.getCurrSeason() == null) {
                    log.warn("xrmatch upRank. sid {} mid {} tid {} rm is null", sid, mid, tid);
                    continue;
                }
                Season s = rm.getCurrSeason();
                if (s.getRank() != rank) {
                    s.setRank(rank);
                    rm.save();
                }
                if (rank <= 2000) {
                    log.debug("xrmatch upRank. sid {} mid {} tid {} rating {} rank {}", sid, mid, tid, s.getRating(), rank);
                }
            }
        }
    }

    private String getTodayYYYYMMDD() {
        LocalDate nowld = LocalDate.now();
        return nowld.getYear() + "" + nowld.getMonthValue() + "" + nowld.getDayOfMonth();
    }

    //    private String getYesterdayYYYYMMDD() {
    //        LocalDate nowld = LocalDate.now().minusDays(1);
    //        return nowld.getYear() + "" + nowld.getMonthValue() + "" + nowld.getDayOfMonth();
    //    }

    /** 每日计划任务. 扣除分数, 刷新排名 */
    public synchronized void dailyJob() {
        final RankedMatchSeasonBean currs = RankedMatchConsole.getCurrSeason(System.currentTimeMillis());
        if (currs == null) {
            log.warn("xrmatch dailyJob. curr season null");
            return;
        }
        final int sid = currs.getId();
        log.info("xrmatch dailyjob");
        //扣除分数
        subRating(sid);
        //刷新排名
        upRank(sid);
    }

    /** 扣除分数 */
    private synchronized void subRating(int sid) {
        final long curr = System.currentTimeMillis();
        final GlobalBean gb = ConfigConsole.global();
        final boolean autoDegrade = gb.rMatchDegradeTier;

        for (Map.Entry<Integer, RankedMatchMedalBean> e : RankedMatchConsole.getMedals().entrySet()) {
            Integer mid = e.getKey();
            List<String> revrange = redis.zrevrange(getCurrMedalRankKey(mid), 0, -1);//get all
            log.info("xrmatch dailyJob. season {} mid {} rank size {}", sid, mid, revrange.size());
            for (String tidStr : revrange) {
                long tid = toLong(tidStr);
                RankedMatch rm = getRankedMatch(tid);
                if (rm == null || rm.getCurrSeason() == null) {
                    log.warn("xrmatch dailyJob. season {} mid {} tid {} rm is null", sid, mid, tid);
                    continue;
                }
                Season s = rm.getCurrSeason();
                if (rm.getLastMatchTime() + gb.rMatchDecreTime < curr) {
                    final int srcrating = s.getRating();
                    final int srctier = s.getTierId();
                    if (srcrating < gb.rMatchDecreMinRating) {
                        continue;
                    }
                    s.setRating(Math.max(s.getRating() - gb.rMatchDecreRating, gb.rMatchDecreMinRating));
                    if (autoDegrade) {//降级
                        RankedMatchTierBean hm = RankedMatchConsole.getLowerTier(srctier, s.getRating());
                        if (hm != null && hm.getId() != s.getTierId()) {
                            redis.zrem(getCurrMedalRankKey(hm.getMedalId()), str(rm.getTeamId()));//remove old
                            s.setTierId(hm.getId());
                        }
                    }
                    redis.zadd(getCurrMedalRankKey(mid), s.getRating(), str(rm.getTeamId()));//add new score
                    if (log.isDebugEnabled()) {
                        log.debug("xrmatch dailyJob. season {} mid {} tid {} rating {} -> {} tier {} -> {}",
                            sid, mid, tid, srcrating, s.getRating(), srctier, s.getTierId());
                    }
                    rm.save();
                }
            }
        }
    }

    /** 获取球队天梯赛信息 */
    @RPCMethod(code = CrossCode.XRanked_Info, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void info(long teamId) {
        RpcResp<RankedMatch> ret = info0(teamId);
        RpcTask.resp(ret);
        log.debug("xrmatch info. tid {} ret {}", teamId, ret.ret);
    }

    private RpcResp<RankedMatch> info0(long teamId) {
        //        long curr = System.currentTimeMillis();
        //        RankedMatchSeasonBean lastSeason = RankedMatchConsole.getCurrOrLastSeason(curr);
        //        log.debug("xrmatch info. curr {} last season {}", curr, lastSeason);
        //        if (lastSeason == null) {
        //            return ret(ErrorCode.RMatch_Season_Bean_Null);
        //        }
        RankedMatch rm = getRankedMatch(teamId);
        boolean join = joinTeams.containsKey(teamId);
        if (rm != null) {
            rm.setTempInPool(join);
        }
        return succ(rm);
    }

    /** 开始比赛, 加入匹配池 */
    @RPCMethod(code = CrossCode.XRanked_Join_Pool, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void joinPool(TeamNode tn, int teamLev) {
        ErrorCode ret = joinPool0(tn, teamLev);
        RpcTask.resp(ret);
        log.debug("xrmatch joinPool. tid {} tlev {} ret {}", tn.getTeamId(), teamLev, ret);
    }

    private ErrorCode joinPool0(TeamNode tn, int teamLev) {
        long teamId = tn.getTeamId();
        long curr = System.currentTimeMillis();
        RankedMatchSeasonBean sb = RankedMatchConsole.getCurrSeason(curr);
        if (sb == null) {
            return ErrorCode.RMatch_Season_Bean_Null;
        }
        MatchTeam mt = new MatchTeam(teamId, teamLev, tn);
        if (isJoined(mt)) {
            return ErrorCode.RMatch_Joined;
        }
        RankedMatch rm = getRankedMatchOrCreate(teamId, tn.getNodeName());
        checkAndInitSeason(teamId, teamLev, sb, rm);
        Season currs = rm.getCurrSeason();
        if (currs == null) {
            return ErrorCode.RMatch_Season_Curr_Null;
        }

        mt.rating = currs.getRating();
        //        mt.node = currs.getTierId();
        MatchTeam old = joinTeams.put(mt.teamId, mt);
        if (old != null) {
            return ErrorCode.RMatch_Joined;
        }
        return ErrorCode.Success;
    }

    /** 开始比赛, 取消比赛, 离开匹配池 */
    @RPCMethod(code = CrossCode.XRanked_Cancel_Join, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void cancelJoin(long teamId) {
        ErrorCode ret = cancelJoin0(teamId);
        RpcTask.resp(ret);
        log.debug("xrmatch cancelJoin. tid {} ret {}", teamId, ret);
    }

    private ErrorCode cancelJoin0(long teamId) {
        if (matchingTeams.containsKey(teamId)) {
            return ErrorCode.Battle_In;
        }
        synchronized (this) {
            MatchTeam mt = joinTeams.get(teamId);
            if (mt == null) {
                return ErrorCode.Success;
            }
            if (!mt.findTarget) {
                joinTeams.remove(teamId);
            }
        }
        return ErrorCode.Success;
    }

    /** 赛季历史信息 */
    @RPCMethod(code = CrossCode.XRaned_Season_His, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void seasonHistory(long teamId, int pageNo, int pageSize) {
        ErrorCode ret = seasonHistory0(teamId, pageNo, pageSize);
        if (ret.isError()) {
            RpcTask.resp(ret);
        }
        log.debug("xrmatch seasonHistory. tid {} ret {}", teamId, ret);
    }

    private ErrorCode seasonHistory0(long teamId, int pageNo, int pageSize) {
        RMatchHisListResp.Builder resp = RMatchHisListResp.newBuilder();
        resp.setPageNo(pageNo);

        Page page = new Page(RankedMatchConsole.getSeasons().size(), pageSize, pageNo);
        List<SeasonHistory> list = rankedMatchDao.findSeasonHis(teamId, page.getOffset(), page.getPageSize());
        for (SeasonHistory his : list) {
            resp.addHis(RankedMatch.seasonResp(his.getSeason()));
        }
        RpcTask.resp(resp.build());
        return ErrorCode.Success;
    }

    /** 段位排行榜 */
    @RPCMethod(code = CrossCode.XRaned_Rank, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void rankList(long teamId, int medelId, int viewNum) {
        ErrorCode ret = rankList0(medelId, viewNum);
        if (ret.isError()) {
            RpcTask.resp(ret);
        }
        log.debug("xrmatch rankList. tid {} medalId {} viewnum {} ret {}", teamId, medelId, viewNum, ret);
    }

    private ErrorCode rankList0(int medelId, int viewNum) {
        RMatchMedalRankResp.Builder resp = RMatchMedalRankResp.newBuilder();
        resp.setMedalId(medelId);
        resp.setLastUpTime(upRankJobLastUpTime);
        String hisDayMedalKey = getHisDayMedalRankKey(getTodayYYYYMMDD(), medelId);
        List<String> revrange = redis.zrevrange(hisDayMedalKey, 0, viewNum);
        log.debug("xrmatch rankList. mid {} zrevrange {}", medelId, revrange.size());
        int rank = 0;
        for (String tidStr : revrange) {
            rank++;
            long tid = toLong(tidStr);
            RankedMatch rm = getRankedMatchFromRedis(tid);
            if (rm == null || rm.getTeamId() <= 0) {
                log.warn("xrmatch rankList. mid {} tid {} rm is null", medelId, tid);
                continue;
            }
            Season s = rm.getCurrSeason();
            if (s == null) {
                continue;
            }
            RMatchTeamRankResp.Builder trr = RMatchTeamRankResp.newBuilder();
            trr.setTid(rm.getTeamId());
            TeamSimpleData tsd = getTeamSimpleData(rm.getTeamId(), rm.getNodeName());
            if (tsd != null) {
                trr.setTeam(tsd);
            }
            trr.setSeason(RankedMatch.seasonResp(s, rank));
            resp.addTeams(trr);
        }
        if (resp.getTeamsCount() == 0) {
            resp.addAllTeams(Collections.emptyList());
        }
        log.debug("xrmatch rankList. mid {} size {}", medelId, resp.getTeamsCount());
        RpcTask.resp(resp.build());
        return ErrorCode.Success;
    }

    /** 领取首次奖励 */
    @RPCMethod(code = CrossCode.XRaned_Receive_First_Award, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void receiveFirstAward(long teamId, int awardId) {
        ErrorCode ret = receiveFirstAward0(teamId, awardId);
        log.debug("xrmatch receiveFirstAward. tid {} awardId {} ret {}", teamId, awardId, ret);
        RpcTask.resp(ret);
    }

    /** 领取首次奖励 */
    private ErrorCode receiveFirstAward0(long teamId, int awardId) {
        RankedMatch rm = getRankedMatch(teamId);
        if (rm == null) {
            return ErrorCode.RMatch_Team;
        }
        if (BitUtil.hasBit(rm.getFirstAward(), 1 << awardId)) {
            return ErrorCode.RMatch_FirstAward;
        }
        rm.setFirstAward(BitUtil.addBit(rm.getFirstAward(), 1 << awardId));
        rm.save();
        return ErrorCode.Success;
    }

    /** 是否已经加入匹配. true 是 */
    private boolean isJoined(MatchTeam mt) {
        return joinTeams.containsKey(mt.teamId) ||
            matchingTeams.containsKey(mt.teamId);
    }

    private void checkAndInitSeason(long teamId, int teamLev, RankedMatchSeasonBean sb, RankedMatch rm) {
        GlobalBean gb = ConfigConsole.getGlobal();
        log.debug("xrmatch check before. tid {} currs {} rm {}", teamId, sb.getId(), rm);
        boolean change = false;
        if (rm.getLastSeason() == null && rm.getCurrSeason() == null) {//第一次加入天梯, 初始化
            Season s = new Season();
            s.setId(sb.getId());
            s.setRating(gb.rMatchInitRating);
            RankedMatchTierBean tb = RankedMatchConsole.getTierByRating(s.getRating());
            if (tb != null) {
                s.setTierId(tb.getId());
            }
            rm.setCurrSeason(s);
            log.info("xrmatch firstjoin. tid {} tlev {} sid {} rating {}", teamId, teamLev, sb.getId(), s.getRating());
            change = true;
        }
        if (rm.getCurrSeason() != null && rm.getCurrSeason().getId() != sb.getId()) { //参与的当前赛季已经结束
            Season currs = rm.getCurrSeason();
            if (rm.getLastSeason() != null) {//记录最后一个赛季历史记录
                rm.setLastSeason(new Season(currs));
            }
            convertSeason(sb.getId(), currs);
            rm.resetBySeason();
            change = true;
            log.debug("xrmatch convert curr season. tid {} sid {}", teamId, sb.getId());
        }
        if (rm.getLastSeason() != null && rm.getCurrSeason() == null) {//当前赛季没有参加
            Season currs = convertSeason(sb.getId(), new Season(rm.getLastSeason()));
            rm.setCurrSeason(currs);
            rm.resetBySeason();
            change = true;
            log.debug("xrmatch convert last season. tid {} sid {}", teamId, sb.getId());
        }

        if (change) {
            log.debug("xrmatch check after. tid {} currs {} rm {}", teamId, sb.getId(), rm);
            rm.save();
        }
    }

    /** 转换赛季积分和段位 */
    private Season convertSeason(int currSid, Season ss) {
        RatingConvertBean rcb = RankedMatchConsole.getRatingConverts(ss.getRating());
        if (rcb != null) {
            ss.setRating(rcb.getValue());
        }
        RankedMatchTierBean newmedal = RankedMatchConsole.getLowerTier(ss.getTierId(), ss.getRating());
        if (newmedal != null) {
            ss.setTierId(newmedal.getId());
        }
        ss.setId(currSid);
        ss.resetBySeason();
        return ss;
    }

    /**
     * 当前赛季段位排行榜 redis key
     *
     * @param medalMid 大段位id
     */
    private static String getCurrMedalRankKey(int medalMid) {
        return RedisKey.Ranked_Match_Medal_Rank + medalMid;
    }

    /**
     * 每日段位排行榜 redis key
     *
     * @param medalMid 大段位id
     */
    private static String getHisDayMedalRankKey(String yyyyMMdd, int medalMid) {
        return RedisKey.Ranked_Match_Medal_Rank_Day_His + yyyyMMdd + "_m_" + medalMid;
    }

    /**
     * 历史赛季段位排行榜 redis key
     *
     * @param medalMid 大段位id
     */
    private static String getHisSeasonMedalRankKey(int seasonId, int medalMid) {
        return RedisKey.Ranked_Match_Medal_Rank_Season_His + seasonId + "_m_" + medalMid;
    }

    private static String str(Object obj) {
        return String.valueOf(obj);
    }

    private static long toLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            log.error("xrmatch parse long. " + str, e);
        }
        return 0L;
    }

    private RankedMatchTierBean calcNewMedel(int tierId, boolean autoDegrade, int srcrating, int newrating) {
        if (newrating > srcrating) {
            return RankedMatchConsole.getHigherTier(tierId, newrating);
        }
        if (autoDegrade && newrating < srcrating) {
            return RankedMatchConsole.getLowerTier(tierId, newrating);
        }
        return null;
    }

    private TeamSimpleData getTeamSimpleData(long teamId, String nodeName) {
        String key = RedisKey.Ranked_Match_Team_Simple_Data + teamId;
        TeamSimpleData tsd = redis.getObj(key);
        if (tsd == null) {
            RpcResp<TeamSimpleData> ret = RpcTask.ask(CrossCode.Team_Simple_Data, nodeName, teamId);
            if (ret.isError()) {
                log.warn("xrmatch gettsd. tid {} node {} ret {}", teamId, nodeName, ret.ret);
                return null;
            }
            tsd = ret.t;
            redis.set(key, tsd, RedisKey._2HOUR);
        }
        return tsd;
    }

    private RankedMatch getRankedMatchFromRedis(long teamId) {
        RankedMatch rm = getRankedMatch(teamId);
        if (rm != null) {
            return rm;
        }
        String key = RedisKey.Ranked_Match_Team + teamId;
        rm = redis.getObj(key);
        if (rm == null) {
            rm = rankedMatchDao.findByTid(teamId);
            if (rm == null) {
                rm = new RankedMatch();
            }
            redis.set(key, rm, RedisKey._6HOUR);
        }
        return rm;
    }

    private RankedMatch getRankedMatchOrCreate(long teamId, String nodeName) {
        RankedMatch rm = getRankedMatch(teamId);
        if (rm == null) {
            rm = new RankedMatch(teamId);
            rm.setNodeName(nodeName);
            rm.save();
            RankedMatch old = teams.putIfAbsent(teamId, rm);
            if (old != null) {
                rm = old;
            }
        }
        return rm;
    }

    private RankedMatch getRankedMatch(long teamId) {
        RankedMatch rm = teams.get(teamId);
        if (rm == null) {
            rm = rankedMatchDao.findByTid(teamId);
            if (rm == null) {
                return null;
            }
            RankedMatch old = teams.putIfAbsent(teamId, rm);
            if (old != null) {
                rm = old;
            }
        }
        return rm;
    }

    /** 比赛房间信息 */
    public static final class Room implements Serializable {
        private static final long serialVersionUID = -3697879913551425232L;
        private final long id;
        private final MatchTeam home;
        private final MatchTeam away;
        /** 创建时间. 定时清理用 */
        private final long createTime;

        Room(long id, MatchTeam home, MatchTeam away, long createTime) {
            this.id = id;
            this.home = home;
            this.away = away;
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return "{" +
                "\"id\":" + id +
                "\"ct\":" + createTime +
                ", \"home\":" + home.baseInfo() +
                ", \"away\":" + away.baseInfo() +
                '}';
        }
    }

    /** 匹配中的球队信息 */
    private static final class MatchTeam implements Serializable {
        private static final long serialVersionUID = -5920948310559644227L;
        private final long teamId;
        private final int teamLev;
        private final TeamNode node;
        /** 加入匹配时的评分 */
        private int rating;
        /** 匹配次数 */
        private int processCount;
        /** 找到对手 */
        private boolean findTarget;
        /** 胜利时的得分 */
        private int winRating;
        /** 失败时的得分 */
        private int loseRating;

        MatchTeam(long teamId, int teamLev, TeamNode nodeName) {
            this(teamId, teamLev, nodeName, 0);
        }

        MatchTeam(long teamId, int teamLev, int rating) {
            this(teamId, teamLev, null, rating);
        }

        MatchTeam(long teamId, int teamLev, TeamNode node, int rating) {
            this.teamId = teamId;
            this.teamLev = teamLev;
            this.node = node;
            this.rating = rating;
        }

        int getRating() {
            return rating;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MatchTeam)) {
                return false;
            }
            MatchTeam matchTeam = (MatchTeam) o;
            return teamId == matchTeam.teamId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(teamId);
        }

        String baseInfo() {
            return "{" +
                "\"tid\":" + teamId +
                ", \"tlev\":" + teamLev +
                ", \"node\":" + TeamNode.nodeName(node) +
                ", \"rating\":" + rating +
                '}';
        }

        @Override
        public String toString() {
            return "{" +
                "\"tid\":" + teamId +
                ", \"tlev\":" + teamLev +
                ", \"node\":" + TeamNode.nodeName(node) +
                ", \"rating\":" + rating +
                ", \"pc\":" + processCount +
                ", \"ft\":" + findTarget +
                '}';
        }
    }

    public static void main(String[] args) {
        ConcurrentSkipListSet<MatchTeam> pool = new ConcurrentSkipListSet<>(Comparator.comparingInt(o -> o.rating));
        pool.add(new MatchTeam(1L, 0, 10));
        pool.add(new MatchTeam(1L, 0, 20));
        pool.add(new MatchTeam(1L, 0, 30));
        pool.add(new MatchTeam(2L, 0, 10));
        pool.add(new MatchTeam(2L, 0, 20));
        pool.add(new MatchTeam(2L, 0, 30));
        Set<MatchTeam> sortset = new ConcurrentSkipListSet<>(Comparator.comparingInt(o -> o.rating));
        sortset.add(new MatchTeam(1L, 0, 10));
        sortset.add(new MatchTeam(1L, 0, 20));
        sortset.add(new MatchTeam(1L, 0, 30));
        sortset.add(new MatchTeam(2L, 0, 10));
        sortset.add(new MatchTeam(2L, 0, 20));
        sortset.add(new MatchTeam(2L, 0, 29));
        System.out.println(pool);
        System.out.println(sortset);
    }

}
