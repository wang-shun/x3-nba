package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jredis.ri.alphazero.support.DefaultCodec;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.LeagueAppointBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.LeagueConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.db.ao.logic.ILeagueAO;
import com.ftkj.db.domain.LeagueTeamPO;
import com.ftkj.db.domain.LeagueTeamSimplePO;
import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EChat;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EEmailType;
import com.ftkj.enums.ELeagueTeamLevel;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERank;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueDonateLog;
import com.ftkj.manager.league.LeagueHonor;
import com.ftkj.manager.league.LeagueLog;
import com.ftkj.manager.league.LeagueSimple;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.league.LeagueTeamSimple;
import com.ftkj.manager.league.groupwar.LeagueGroup;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.rank.LeagueRank;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamSimple;
import com.ftkj.manager.train.LeagueTrain;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.LeaguePB;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import redis.clients.jedis.Jedis;

/**
 * @author tim.huang
 * 2017年5月18日
 * 联盟模块
 */
public class LeagueManager extends BaseManager implements OfflineOperation, IRedPointLogic {
    @IOC
    private TeamManager teamManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private PropManager propManager;
    @IOC
    private LeagueHonorManager leagueHonorManager;
    @IOC
    private RankManager rankManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private ChatManager chatManager;
    @IOC
    private BuffManager buffManager;
    @IOC
    private ILeagueAO leagueAO;
    @IOC
    private TeamEmailManager emailManager;
    @IOC
    private LeagueGroupSeasonManager groupSeasonManager;
    @IOC
    private LeagueGroupWarManager groupWarManager;
    @IOC
    private LeagueArenaManager leagueArenaManager;
    @IOC
    private TrainManager trainManager;
    
    private Map<Integer, League> leagueMap;

    private Map<Long, LeagueTeam> leagueTeamMap;


    /**
     * 联盟成员列表
     */
    private Map<Integer, Set<Long>> leagueTeamList;

    private BiMap<Integer, String> leagueNameMap;

    private AtomicInteger ids;

    //--------------------------------------------
    private int _createLevel;
    private int _createMoney;
    private int _createProp;
    private int _maxLeagueFMZ;
    private int _maxLeagueLS;
    private int _maxLeagueJY;
    private List<Integer> _maxLeagueCY;
    private String _createLeagueLog;
    private String _quitLeagueLog;
    private String _joinLeagueLog;
    @SuppressWarnings("unused")
    private String _donateMoneyLeagueLog;
    public String _donateMedalLeagueLog;
    public String _levelUpLeagueLog;
    public String _honorLevelUpLeagueLog;
    private String _appotionLeagueLog;
    private String _noticeLeagueLog;
    private String _declarationLeagueLog;

    /**
     * 创建联盟
     *
     * @param leagueName
     * @param leagueTip
     * @param logo
     */
    @ClientMethod(code = ServiceCode.LeagueManager_createLeague)
    public synchronized void createLeague(String leagueName, String leagueTip, String logo) {
        long teamId = getTeamId();
        if (leagueTip.length() > 150 || leagueName.length() > 50) {//长度有误
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        boolean exist = leagueNameMap.inverse().containsKey(leagueName);
        if (exist) {//联盟名字已经存在
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_2.code).build());
            return;
        }

        if (chatManager.shieldText(leagueName)) {//名字含有敏感字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_14.code).build());
            return;
        }

        if (chatManager.shieldText(leagueTip)) {//名字含有敏感字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_2.code).build());
            return;
        }

        LeagueTeam leagueTeam = getLeagueTeam(teamId);
        if (leagueTeam != null && leagueTeam.getLeagueId() != 0) {//已经有联盟了
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        Team team = teamManager.getTeam(teamId);
        if (team.getLevel() < _createLevel) {//等级不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Level.code).build());
            return;
        }
        TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
        if (tm.getGold() < _createMoney) {//经费不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_0.code).build());
            return;
        }
        TeamProp prop = propManager.getTeamProp(teamId);
        if (!prop.checkPropNum(_createProp, 1)) {//创建联盟道具不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }
        teamMoneyManager.updateTeamMoney(tm, 0, -_createMoney, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.联盟, "创建"));
        propManager.delProp(teamId, _createProp, 1, true, true);
        //将联盟数据保存起来
        int leagueId = getLeagueNewId();
        //联盟数据，默认人数1
        League league = League.createLeague(leagueId, leagueName, logo, team.getName(), leagueTip, "");
        leagueMap.put(league.getLeagueId(), league);
        leagueNameMap.put(leagueId, leagueName);
        LeagueHonor lh = leagueHonorManager.createLeague(leagueId);
        addLeagueLog(leagueId, team.getName(), StringUtil.formatString(_createLeagueLog, team.getName()));
        joinTeam(leagueId, teamId, ELeagueTeamLevel.盟主, team.getLevel(), leagueName, team.getName());
        rankManager.updateLeagueRank(league, lh.getAllLevel());
        sendMessage(teamId, getGameLoadLeagueData(getLeagueTeam(teamId), league), ServiceCode.Push_League_Join);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 查看联盟信息
     *
     * @param leagueId
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showLeagueInfo)
    public void showLeagueInfo(int leagueId) {
        League league = getLeague(leagueId);
        if (league == null) { return; }
        int rank = rankManager.getLeagueRank(leagueId);
        sendMessage(getLeagueListData(league, rank));
    }

    /**
     * 邀请加入
     *
     * @param tid
     */
    @ClientMethod(code = ServiceCode.LeagueManager_inviteTeam)
    public void inviteTeam(long tid) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0 || !(lt.getLevel() == ELeagueTeamLevel.盟主 || lt.getLevel() == ELeagueTeamLevel.副盟主
            || lt.getLevel() == ELeagueTeamLevel.理事)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }
        LeagueTeam other = getLeagueTeam(tid);
        if (other != null && other.getLeagueId() != 0) {//已经有联盟了
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_7.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        if (league.getPeopleCount() >= getMaxCY(league.getLeagueLevel())) {//联盟已达到最大成员上限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_9.code).build());
            return;
        }
        //
        String teamName = teamManager.getTeamNameById(teamId);
        // 邀请列表是面向个人的,可重复邀请
        List<LeagueSimple> lsList = getInviteLeagueList(tid);
        boolean has = lsList.stream().filter(ls -> ls.getLeagueId() == leagueId).findFirst().isPresent();
        if (!has) {
            appendInviteLeagueList(tid, new LeagueSimple(leagueId, league.getLeagueName(), league.getLeagueLevel()));
        }

        sendMessage(tid, DefaultPB.DefaultData.newBuilder()
            .setCode(lt.getLeagueId())
            .setMsg(teamName + "," + league.getLeagueName())
            .build(), ServiceCode.Push_League_Invite);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 接收邀请
     *
     * @param leagueId
     */
    @ClientMethod(code = ServiceCode.LeagueManager_acceptLeague)
    public void acceptLeague(int leagueId) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt != null && lt.getLeagueId() != 0) {//已经有联盟了
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_7.code).build());
            return;
        }
        League league = getLeague(leagueId);
        if (league == null) {
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        if (league.getPeopleCount() >= getMaxCY(league.getLeagueLevel())) {//联盟已达到最大成员上限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_9.code).build());
            return;
        }
        Team team = teamManager.getTeam(teamId);
        boolean ok = joinTeam(leagueId, teamId, ELeagueTeamLevel.成员, team.getLevel(), league.getLeagueName(), team.getName());

        if (ok) {
            //league.updatePeopleCount(1);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        } else {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
        }
    }

    /**
     * 显示联盟列表界面
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showLeagueListMain)
    public void showLeagueListMain(int page) {
        // 从排行榜中取前10数据的LeagueId集合
        int[] tmpTween = getPage(page);
        List<LeagueRank> ranksList = rankManager.getRankList(ERank.League, tmpTween[0], tmpTween[1] + 5);
        //Redis中查询出来的是按score降序排序,在对rankList排序根据联盟等级,贡献值排序
        Collections.sort(ranksList);
        List<LeaguePB.LeagueData> dataList = Lists.newArrayList();
        if (ranksList != null && ranksList.size() > 0) {
            int rank = page * 10 - 10;
            final int size = ranksList.size() < 10 ? ranksList.size() : 10;
            for (int i = 0; i < size; i++) {
                League league = getLeague(ranksList.get(i).getLeagueId());
                if (league == null || league.getPeopleCount() <= 0) {
                    continue;
                }
                dataList.add(getLeagueListData(league, ++rank));
            }
        }
        
//        this.leagueMap.values().forEach(obj -> {int rank = 0;dataList.add(getLeagueListData(obj, ++rank));});
        int maxPage = leagueMap.size() / 10;
        sendMessage(LeaguePB.LeagueListMain.newBuilder().addAllLeagues(dataList).setMaxPage(maxPage).build());
    }

    /**
     * 申请加入联盟
     *
     * @param leagueId
     */
    @ClientMethod(code = ServiceCode.LeagueManager_aceeptJoinLeague)
    public void applyJoinLeague(int leagueId) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt != null && lt.getLeagueId() != 0) {//已经有联盟了
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_7.code).build());
            return;
        }
        League league = getLeague(leagueId);
        if (league == null) {//联盟不存在
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_8.code).build());
            return;
        }
        if (league.getPeopleCount() >= getMaxCY(league.getLeagueLevel())) {//联盟已达到最大成员上限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_9.code).build());
            return;
        }
        List<LeagueSimple> joinList = getJoinLeagueList(teamId);
        LeagueSimple tmp = joinList.stream().filter(ls -> ls.getLeagueId() == leagueId).findFirst().orElse(null);
        if (tmp != null) {//已经申请过一次了
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_3.code).build());
            return;
        }
        Team team = teamManager.getTeam(teamId);
        TeamSimple ts = new TeamSimple(teamId, team.getName(), team.getLogo(), team.getLevel());
        tmp = new LeagueSimple(league.getLeagueId(), league.getLeagueName(), league.getLeagueLevel());
        //
        joinLeagueCache(teamId, leagueId, ts, tmp);
        // 给联盟有审核权限的人推送红点
        leagueRedPoint(leagueId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 计算联盟红点
     *
     * @param leagueId
     */
    private void leagueRedPoint(int leagueId) {
        Set<Long> teams = leagueTeamList.get(leagueId);
        List<Long> tList = Lists.newArrayList(teams);
        try (Jedis j = redis.getJedis()) {
            //取start到end的数据
            byte[] mapKeys = DefaultCodec.encode(RedisKey.League_Team_Info + leagueId);

            byte[][] fileds = new byte[tList.size()][];
            for (int i = 0; i < tList.size(); i++) {
                fileds[i] = DefaultCodec.encode(tList.get(i));
            }
            List<byte[]> infoList = j.hmget(mapKeys, fileds);
            infoList = infoList.stream().filter(info -> info != null).collect(Collectors.toList());
            List<LeagueTeamSimple> ltsList = DefaultCodec.decode(infoList);
            // 遍历本盟成员，给有审核权限的成员推送红点提示.
            int redNum = getLeagueJoinHistory(leagueId).size();
            ltsList.stream().filter(l -> l.getLeagueLevel() <= ELeagueTeamLevel.理事.getId()).forEach(l -> {
                //log.warn("推送联盟申请,红点数={}, 审核者={},{}", redNum, l.getTeamId(), l.getTeamName());
                RedPointParam rpp = new RedPointParam(l.getTeamId(), ERedPoint.联盟申请.getId(), redNum);
                EventBusManager.post(EEventType.奖励提示, rpp);
            });
        } catch (Exception e) {
            log.error("联盟成员列表异常:[{}]", e);
        }
    }

    /**
     * 红点计算
     */
    @Override
    public RedPointParam redPointLogic(long teamId) {
        LeagueTeam l = getLeagueTeam(teamId);
        RedPointParam rpp = null;
        if (l.getLeagueId() > 0) {
            List<TeamSimple> list = getLeagueJoinHistory(l.getLeagueId());
            if (list != null && list.size() > 0 && l.getLevel().getId() <= ELeagueTeamLevel.理事.getId()) {
                rpp = new RedPointParam(l.getTeamId(), ERedPoint.联盟申请.getId(), 1);
                EventBusManager.post(EEventType.奖励提示, rpp);
            }
        }
        return rpp;
    }

    /**
     * 显示玩家当前联盟主界面
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showLeagueMain)
    public void showLeagueMain() {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0) {
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        int rank = rankManager.getLeagueRank(leagueId);
        LeaguePB.LeagueMain data = getLeagueMain(league, rank, lt.getLevel().getId());
        sendMessage(data);
    }

    /**
     * 显示玩家已申请加入联盟列表
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showJoinLeaugeMain)
    public void showJoinLeaugeMain() {
        long teamId = getTeamId();
        List<LeagueSimple> ls = getJoinLeagueList(teamId);
        List<LeaguePB.LeagueSimpleData> dataList = Lists.newArrayList();
        ls.forEach(league -> dataList.add(getLeagueSimpleData(league)));
        sendMessage(LeaguePB.LeagueJoinMain.newBuilder().addAllLeagues(dataList).build());
    }

    /**
     * 显示玩家入盟邀请列表
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showLeagueInviteMain)
    public void showLeagueInviteMain() {
        long teamId = getTeamId();
        List<LeagueSimple> ls = getInviteLeagueList(teamId);
        List<LeaguePB.LeagueSimpleData> dataList = Lists.newArrayList();
        ls.forEach(league -> dataList.add(getLeagueSimpleData(league)));
        sendMessage(LeaguePB.LeagueInviteMain.newBuilder().addAllLeagues(dataList).build());
    }

    /**
     * 取消申请联盟
     *
     * @param leagueId
     */
    @ClientMethod(code = ServiceCode.LeagueManager_cancelJoinLeague)
    public void cancelJoinLeague(int leagueId) {
        long teamId = getTeamId();
        List<LeagueSimple> lsList = getJoinLeagueList(teamId);
        LeagueSimple ls = lsList.stream().filter(l -> l.getLeagueId() == leagueId).findFirst().orElse(null);
        if (ls == null) {//取消的联盟不存在
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_8.code).build());
            return;
        }
        redis.removeListValue(RedisKey.getKey(teamId, RedisKey.League_Team_Join), ls);
        // 取消申请和刷新红点
        clearTeamJoinReaPoint(teamId, leagueId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 取消申请记录和刷新红点
     *
     * @param teamId
     * @param leagueId
     */
    private void clearTeamJoinReaPoint(long teamId, int leagueId) {
        List<TeamSimple> teamList = getLeagueJoinHistory(leagueId);
        TeamSimple ts = teamList.stream().filter(t -> t.getTeamId() == teamId).findFirst().orElse(null);
        if (ts != null) {
            redis.removeListValue(RedisKey.getKey(leagueId, RedisKey.League_Join_History), ts);
        }
        leagueRedPoint(leagueId);
    }

    /**
     * 球队入盟成功后调用
     * 清理所有联盟申请相关记录
     * 联盟方的球队申请记录，和红点计算
     */
    private void clearAllApplyInfo(long teamId) {
        // 联盟方的球队申请记录，和红点计算
        List<LeagueSimple> list = getJoinLeagueList(teamId);
        for (LeagueSimple ls : list) {
            clearTeamJoinReaPoint(teamId, ls.getLeagueId());
        }
    }

    /**
     * 接受联盟邀请
     *
     * @param leagueId
     */
    @ClientMethod(code = ServiceCode.LeagueManager_acceptLeagueInvite)
    public void acceptLeagueInvite(int leagueId) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt != null && lt.getLeagueId() != 0) {//已经有联盟了
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_7.code).build());
            return;
        }
        League league = getLeague(leagueId);
        if (league == null) {//联盟不存在
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_8.code).build());
            return;
        }
        if (league.getPeopleCount() >= getMaxCY(league.getLeagueLevel())) {//联盟已达到最大成员上限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_9.code).build());
            return;
        }
        List<LeagueSimple> lsList = getInviteLeagueList(teamId);
        LeagueSimple ls = lsList.stream().filter(l -> l.getLeagueId() == leagueId).findFirst().orElse(null);
        if (ls == null) {//并没有受到联盟邀请
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_10.code).build());
            return;
        }
        Team team = teamManager.getTeam(teamId);
        boolean ok = joinTeam(leagueId, teamId, ELeagueTeamLevel.成员, team.getLevel(), league.getLeagueName(), team.getName());
        if (ok) {
            //league.updatePeopleCount(1);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        } else {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_7.code).build());
        }
    }

    /**
     * 编辑联盟宣言
     *
     * @param tip
     */
    @ClientMethod(code = ServiceCode.LeagueManager_editLeagueTip)
    public void editLeagueTip(String tip) {
        long teamId = getTeamId();
        if (chatManager.shieldText(tip)) {//敏感字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_2.code).build());
            return;
        }

        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || lt.getLevel() != ELeagueTeamLevel.盟主) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }

        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        league.updateLeagueTip(tip);
        String adminName = teamManager.getTeamNameById(teamId);
        addLeagueLog(leagueId, adminName
            , StringUtil.formatString(_declarationLeagueLog, adminName));
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 编辑联盟公告
     *
     * @param notice
     */
    @ClientMethod(code = ServiceCode.LeagueManager_editLeagueNotice)
    public void editLeagueNotice(String notice) {
        long teamId = getTeamId();
        if (chatManager.shieldText(notice)) {//敏感字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0 || !(lt.getLevel() == ELeagueTeamLevel.盟主 || lt.getLevel() == ELeagueTeamLevel.副盟主)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }

        // 消耗球卷
        if (!teamMoneyManager.checkTeamMoney(teamMoneyManager.getTeamMoney(teamId), ConfigConsole.global().editLeagueNoticePrice, 0, 0, 0)) {
            log.debug("球券不足！");
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Money_1.code).build());
            return;
        }
        teamMoneyManager.updateTeamMoney(teamId, -ConfigConsole.global().editLeagueNoticePrice, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.联盟, "编辑联盟公告"));

        league.updateLeagueNotice(notice);
        String adminName = teamManager.getTeamNameById(teamId);
        addLeagueLog(leagueId, adminName
            , StringUtil.formatString(_noticeLeagueLog, adminName));
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());

        // 发邮件给联盟所有的成员

        emailManager.sendEmailList(getLeagueTeamList(leagueId), EEmailType.Template.getType(), 0,
            "联盟公告", notice, "");

    }

    /**
     * 显示联盟日志
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showLeagueLog)
    public void showLeagueLog(int page) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0) {
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            return;
        }
        int[] in = getPage(page);
        //
        List<LeagueLog> logList = redis.getList(RedisKey.League_Log + leagueId, in[0], in[1]);
        List<LeaguePB.LeagueLogData> logDataList = Lists.newArrayList();
        logList.forEach(l -> logDataList.add(getLeagueLogData(l)));
        sendMessage(LeaguePB.LeagueLogMain.newBuilder().addAllLogList(logDataList).build());
    }

    /**
     * 显示申请联盟列表
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showLeagueAceeptTeamMain)
    public void showLeagueAceeptTeamMain() {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0) {
            log.debug("LeagueManager_showLeagueAceeptTeamMain-[{}]", teamId);
            return;
        }
        //
        List<TeamSimple> teamList = getLeagueJoinHistory(lt.getLeagueId());
        List<LeaguePB.LeagueTeamSimpleData> datas = Lists.newArrayList();
        teamList.forEach(t -> datas.add(getLeagueTeamSimpleData(t)));
        sendMessage(LeaguePB.LeagueAceeptMain.newBuilder().addAllTeams(datas).build());
    }

    /**
     * 退出联盟
     */
    @ClientMethod(code = ServiceCode.LeagueManager_quitLeague)
    public void quitLeague() {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0) {
            log.debug("quitLeague-[{}]", teamId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        if (leagueArenaManager.isInBattle(teamId)) {
            log.debug("在联盟球馆赛中不可退盟", teamId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Arena_3.code).build());
            return;
        }
        LeagueGroup group = groupWarManager.findTeamGruop(leagueId, teamId);
        if (group != null && group.getStatus() != 0) {
            log.debug("正在联盟战队赛中", teamId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle.code).build());
            return;
        }
        synchronized (league) {
            lt = getLeagueTeam(teamId);
            if (lt.getLevel() == ELeagueTeamLevel.盟主) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
                return;
            }
            if (league.getPeopleCount() <= 1) {
                log.debug("联盟不能没有人", teamId);
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
                return;
            }
            String teamName = teamManager.getTeamNameById(teamId);
            quitLeague(lt, league, teamName);
            groupWarManager.exitLeague(group, teamId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        }
        
        // 清理联盟训练馆
        trainManager.clearLeagueTrain(teamId);
        leagueArenaManager.clearLeagueArenaTeamData(leagueId, teamId);
    }

    /**
     * 踢出玩家
     */
    @ClientMethod(code = ServiceCode.LeagueManager_kickTeam)
    public void kickTeam(long tid) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || !(lt.getLevel() == ELeagueTeamLevel.盟主 ||
            lt.getLevel() == ELeagueTeamLevel.副盟主)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            return;
        }
        synchronized (league) {
            String teamName = teamManager.getTeamNameById(teamId);
            LeagueTeam killTeam = getLeagueTeam(tid);
            if (killTeam.getLevel() == ELeagueTeamLevel.盟主) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
                return;
            }
            quitLeague(killTeam, league, teamName);

            //leagueArenaManager.kickTeam(leagueId, teamId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
            //告诉被T出联盟的玩家，
            sendMessage(tid, DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build(), ServiceCode.Push_League_Quit);
        }
        
        // 清理联盟训练馆
        trainManager.clearLeagueTrain(tid);
        leagueArenaManager.clearLeagueArenaTeamData(leagueId, tid);
    }

    /**
     * 同意联盟申请
     *
     * @param teamId
     */
    @ClientMethod(code = ServiceCode.LeagueManager_confirmLeagueAccept)
    public void confirmLeagueAccept(long tid) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || !(lt.getLevel() == ELeagueTeamLevel.盟主 ||
            lt.getLevel() == ELeagueTeamLevel.副盟主 ||
            lt.getLevel() == ELeagueTeamLevel.理事)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            return;
        }

        if (league.getPeopleCount() >= getMaxCY(league.getLeagueLevel())) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_9.code).build());
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            return;
        }

        LeagueTeam other = getLeagueTeam(tid);
        if (other != null && other.getLeagueId() != 0) {//玩家已经加入联盟
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_7.code).build());
            return;
        }
        List<TeamSimple> teamList = getLeagueJoinHistory(leagueId);
        TeamSimple ts = teamList.stream().filter(t -> t.getTeamId() == tid).findFirst().orElse(null);
        if (ts == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }
        String teamName = teamManager.getTeamNameById(teamId);
        Team team = teamManager.getTeam(tid);
        boolean ok = joinTeam(leagueId, tid, ELeagueTeamLevel.成员, team.getLevel(), league.getLeagueName(), teamName);
        if (ok) {
            //			league.updatePeopleCount(1);
            //			addLeagueLog(leagueId,teamName , StringUtil.formatString(_joinLeagueLog, team.getName()));
            redis.removeListValue(RedisKey.getKey(leagueId, RedisKey.League_Join_History), ts);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        } else {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
        }
    }

    /**
     * 拒绝联盟申请
     *
     * @param tid
     */
    @ClientMethod(code = ServiceCode.LeagueManager_refuseLeagueAceept)
    public void refuseLeagueAceept(long tid) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || !(lt.getLevel() == ELeagueTeamLevel.盟主 ||
            lt.getLevel() == ELeagueTeamLevel.副盟主 ||
            lt.getLevel() == ELeagueTeamLevel.理事)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            return;
        }
        // 移除其他联盟的申请记录
        clearTeamJoinReaPoint(tid, leagueId);
        //移除玩家自身的联盟申请列表
        List<LeagueSimple> joinList = getJoinLeagueList(tid);
        LeagueSimple tmp = joinList.stream().filter(ls -> ls.getLeagueId() == leagueId).findFirst().orElse(null);
        //移除列表缓存数据
        redis.removeListValue(RedisKey.getKey(tid, RedisKey.League_Team_Join), tmp);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 捐献球卷,球券捐献每天超过一定数量,不再获得经验和荣誉,贡献和功勋不受限制.
     * @param money
     */
    @ClientMethod(code = ServiceCode.LeagueManager_donateMoney)
    public void donateMoney(int money) {
        long teamId = getTeamId();
        if (money > Integer.MAX_VALUE || money <= 0) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0) {//没有加入联盟
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }

        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
        if (tm.getMoney() < money) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }

        //
        teamMoneyManager.updateTeamMoneyUnCheck(tm, -money, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.联盟, "捐献"));
        String key = RedisKey.getLeagueDayDonateScoreNumKey(league.getLeagueId(), teamId);
		Integer value = redis.getObj(key);
		int beforePidNum = value == null ? 0 : value;
		int fixedCount = ConfigConsole.getGlobal().leagueDonateMax;
		LeagueAppointBean lab = LeagueConsole.getLeagueAppointBean(4004);//4004,球券物品Id
		//联盟成就捐球员处理:2018年9月15日15:59:19
		LeagueHonor lh = leagueHonorManager.getLeagueHonor(leagueId);
		synchronized (lh) {
			//捐赠球券数据量超过一个固定值就不会获得荣誉值
			if (beforePidNum < fixedCount) {
				int num = (beforePidNum + money) > fixedCount ? (fixedCount - beforePidNum) : money;
				league.updateLeagueHonor(num * lab.getHonor());//荣誉
			}
			
			league.updateLeagueScore(lab.getScore() * money);//联盟贡献
			lh.appendHonorProp(4004, money);
			leagueHonorManager.appointLeagueDonte(league, lab, money);
		}
		
    	rankManager.updateLeagueRank(league, lh.getAllLevel());
        
        updateLeagueTeamScore(leagueId, lt, money * lab.getScore());
        lt.updateFeats(money * lab.getFeats());
        addDonateLog(leagueId, teamId, teamManager.getTeamNameById(teamId), money);
        taskManager.updateTask(teamId, ETaskCondition.联盟捐献经费, 1, EModuleCode.联盟.getName());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        
    }

    /**
     * 显示联盟贡献列表
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showDonateLogMain)
    public void showDonateLogMain() {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || !(lt.getLevel() == ELeagueTeamLevel.盟主 ||
            lt.getLevel() == ELeagueTeamLevel.副盟主)) {
            //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            return;
        }
        //
        List<LeagueDonateLog> logList = redis.getList(RedisKey.getDayKey(leagueId, RedisKey.League_Donate_Log));
        List<LeaguePB.LeagueDonateData> dataList = Lists.newArrayList();
        logList.forEach(l -> dataList.add(getLeagueDonateData(l)));
        sendMessage(LeaguePB.LeagueDonateMain.newBuilder().addAllLogs(dataList).build());
    }

    /**
     * 显示联盟成员列表
     */
    @ClientMethod(code = ServiceCode.LeagueManager_showLeagueTeamListMain)
    public void showLeagueTeamListMain(int page) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0) {
            //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            log.debug("联盟玩家数据异常->teamId:[{}],leagueId:[{}]", teamId, leagueId);
            return;
        }

        Set<Long> teams = leagueTeamList.get(leagueId);
        //        int[] p = getPage(page);
        //        LBInt li = new LBInt(-1);
        //		List<Long> tList = teams.stream().filter(t->li.increaseAndGet()>=p[0]).filter(t->li.getVal()<=p[1]).collect(Collectors.toList());
        List<Long> tList = Lists.newArrayList(teams);
        List<LeagueTeamSimple> ltsList = null;
        try (Jedis j = redis.getJedis()) {
            //取start到end的数据
            byte[] mapKeys = DefaultCodec.encode(RedisKey.League_Team_Info + leagueId);

            byte[][] fileds = new byte[tList.size()][];
            for (int i = 0; i < tList.size(); i++) {
                fileds[i] = DefaultCodec.encode(tList.get(i));
            }
            List<byte[]> infoList = j.hmget(mapKeys, fileds);
            infoList = infoList.stream().filter(info -> info != null).collect(Collectors.toList());
            ltsList = DefaultCodec.decode(infoList);
        } catch (Exception e) {
            log.error("联盟成员列表异常:[{}]", e);
            ltsList = Lists.newArrayList();
        }

        //		long oline = teams.stream().filter(t->GameSource.isOline(t)).count();
        List<LeaguePB.LeagueTeamSimpleData> teamDataList = Lists.newArrayList();
        ltsList.forEach(t -> teamDataList.add(getLeagueTeamSimpleData(t, GameSource.isOline(t.getTeamId()))));
        sendMessage(LeaguePB.LeagueTeamMain.newBuilder().setPeopleCount(ltsList.size()).addAllTeams(teamDataList).build());
    }

    /** 获取联盟所有球队ID */
    public Set<Long> getLeagueTeamList(int leagueId) {
        return leagueTeamList.get(leagueId);
    }

    /**
     * 任命
     */
    @ClientMethod(code = ServiceCode.LeagueManager_appoint)
    public void appoint(long tid, int leagueLevel) {
        long teamId = getTeamId();
        ELeagueTeamLevel level = ELeagueTeamLevel.getELeagueTeamLevel(leagueLevel);
        if (level == null) { return; }
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || lt.getLevel() != ELeagueTeamLevel.盟主) { //只有盟主有任命权限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }

        LeagueTeam tt = getLeagueTeam(tid);
        if (tt == null || tt.getLeagueId() != lt.getLeagueId()) {//不是同一个联盟，或者没有联盟
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_0.code).build());
            return;
        }
        //ELeagueTeamLevel oldLevel = tt.getLevel();
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        List<LeagueTeamSimple> ltsList = getLeagueTeamSimpleList(leagueId);
        long count = ltsList.stream().filter(t -> t.getLeagueLevel() == leagueLevel).count();
        int max = getApponitMaxCount(level, league.getLeagueLevel());
        if (level != ELeagueTeamLevel.成员 && count >= max) {//超出任命的上限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
            return;
        }

        updateLeagueLevel(leagueId, tt, level);
        addLeagueLog(leagueId, teamManager.getTeamNameById(teamId)
            , StringUtil.formatString(_appotionLeagueLog, teamManager.getTeamNameById(tt.getTeamId()), level.toString()));
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        sendMessage(tid, DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg("" + leagueLevel).build(), ServiceCode.Push_League_Appoint);
    }

    /**
     * 转让盟主，自己变成普通成员
     *
     * @param tid
     */
    @ClientMethod(code = ServiceCode.LeagueManager_appointLeader)
    public void appointLeader(long tid) {
        long teamId = getTeamId();
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || lt.getLevel() != ELeagueTeamLevel.盟主) { //只有盟主有权限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
            return;
        }
        //
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        //
        synchronized (league) {
            if (lt == null || lt.getLeagueId() == 0
                || lt.getLevel() != ELeagueTeamLevel.盟主) { //只有盟主有权限
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_6.code).build());
                return;
            }
            //
            LeagueTeam tt = getLeagueTeam(tid);
            if (tt == null || tt.getLeagueId() != lt.getLeagueId()) {//不是同一个联盟，或者没有联盟
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_0.code).build());
                return;
            }
            league.updateTeamName(teamManager.getTeamNameById(tt.getTeamId()));
            updateLeagueLevel(leagueId, tt, ELeagueTeamLevel.盟主);
            updateLeagueLevel(leagueId, lt, ELeagueTeamLevel.成员);
            String level = ELeagueTeamLevel.盟主.getId() + "";
            addLeagueLog(leagueId, teamManager.getTeamNameById(teamId)
                , StringUtil.formatString(_appotionLeagueLog, teamManager.getTeamNameById(tt.getTeamId()), level));
            sendMessage(tid, DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(level).build(), ServiceCode.Push_League_Appoint);
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @ClientMethod(code = ServiceCode.LeagueManager_updateLeagueLimit)
    public void updateLeagueLimit(int shopLimit, int honorLimit) {
        long teamId = getTeamId();
        if (shopLimit < 0 || honorLimit < 0) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null || lt.getLeagueId() == 0
            || lt.getLevel() != ELeagueTeamLevel.盟主) { //只有盟主有任命权限
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_13.code).build());
            return;
        }
        int leagueId = lt.getLeagueId();
        League league = getLeague(leagueId);
        if (league == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_8.code).build());
            return;
        }
        league.updateHonorLimit(honorLimit);
        league.updateShopLimit(shopLimit);
        sendMessage(ServiceConsole.getChatKey(EChat.联盟聊天),
            DefaultPB.DefaultData.newBuilder().setMsg(shopLimit + "," + honorLimit).build(), ServiceCode.Push_League_Limit);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());

    }

    /**
     * @param leagueId
     * @return
     */
    public String getLeagueName(int leagueId) {
        League l = getLeague(leagueId);
        return l == null ? "" : l.getLeagueName();
    }

    /**
     * 取联盟名称，没有盟返回 ""
     *
     * @param teamId
     * @return
     */
    public String getLeagueName(long teamId) {
        return getLeagueName(getLeagueId(teamId));
    }

    public int getLeagueId(long teamId) {
        LeagueTeam team = getLeagueTeam(teamId);
        return team == null ? 0 : team.getLeagueId();
    }

    public int getLeagueId(String leagueName) {
        Integer id = leagueNameMap.inverse().get(leagueName);
        return id == null ? 0 : id;
    }

    public void updateLeagueTeamName(long teamId, String teamName) {
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null) { return; }
        int leagueId = lt.getLeagueId();
        LeagueTeamSimple lts = getLeagueTeamSimple(leagueId, lt);
        lts.updateTeamName(teamName);
        setLeagueTeamSimple(leagueId, lts);
    }

    private void updateLeagueLevel(int leagueId, LeagueTeam lt, ELeagueTeamLevel level) {
        lt.updateLevel(level);
        LeagueTeamSimple lts = getLeagueTeamSimple(leagueId, lt);
        lts.updateLeagueLevel(level.getId());
        setLeagueTeamSimple(leagueId, lts);
    }

    public void updateTeamLevel(long teamId, int level) {
        LeagueTeam lt = getLeagueTeam(teamId);
        if (lt == null) { return; }
        int leagueId = lt.getLeagueId();
        LeagueTeamSimple lts = getLeagueTeamSimple(leagueId, lt);
        lts.updateLevel(level);
        setLeagueTeamSimple(leagueId, lts);
    }

    private int getApponitMaxCount(ELeagueTeamLevel level, int leagueLevel) {
        if (ELeagueTeamLevel.副盟主 == level) {
            return _maxLeagueFMZ;
        } else if (ELeagueTeamLevel.理事 == level) {
            return _maxLeagueLS;
        } else if (ELeagueTeamLevel.精英 == level) {
            return _maxLeagueJY;
        } else if (ELeagueTeamLevel.成员 == level) {
            return getMaxCY(leagueLevel);
        }
        return 0;
    }

    public void updateLeagueTeamScore(int leagueId, LeagueTeam lt, int money) {
        lt.updateScore(money);
        lt.updateWeekScore(money);
        LeagueTeamSimple lts = getLeagueTeamSimple(leagueId, lt);
        lts.updateScore(money);
        setLeagueTeamSimple(leagueId, lts);
    }

    public void updateLeagueTeamFeats(long teamId, int money) {
        LeagueTeam lt = getLeagueTeam(teamId);
        lt.updateFeats(money);
    }

    public void updateLeagueLogin(LeagueTeam lt) {
        if (lt == null) { return; }
        int leagueId = lt.getLeagueId();
        if (leagueId != 0) {
            LeagueTeamSimple lts = getLeagueTeamSimple(leagueId, lt);
            if (lts == null) {
                Team team = teamManager.getTeam(lt.getTeamId());
                lts = new LeagueTeamSimple(team.getTeamId(), team.getName(), team.getLevel(), lt.getLevel().getId());
            }
            lts.login();
            setLeagueTeamSimple(leagueId, lts);
        }
    }

    private void removeLeagueTeamSimple(int leagueId, long teamId) {
        redis.removeMapValue(RedisKey.League_Team_Info + leagueId, teamId);
    }

    private void setLeagueTeamSimple(int leagueId, LeagueTeamSimple lts) {
        redis.putMapValue(RedisKey.League_Team_Info + leagueId, lts.getTeamId(), lts);
    }

    private LeagueTeamSimple getLeagueTeamSimple(int leagueId, LeagueTeam lt) {
        LeagueTeamSimple lts = redis.hget(RedisKey.League_Team_Info + leagueId, lt.getTeamId());
        if (lts == null) {
            Team team = teamManager.getTeam(lt.getTeamId());
            lts = new LeagueTeamSimple(team.getTeamId(), team.getName(), team.getLevel(), lt.getLevel().getId());
        }
        return lts;
    }

    /**
     * 缓存是空的话，从DB查，现在不支持直接在内存新增成员
     *
     * @param leagueId
     * @return
     */
    private List<LeagueTeamSimple> getLeagueTeamSimpleList(int leagueId) {
        // 为了兼容是说数据
        List<LeagueTeamSimple> list = redis.getMapValues(RedisKey.League_Team_Info + leagueId);
        Set<Long> teamIds = leagueTeamList.get(leagueId);
        if (list != null && list.size() != teamIds.size()) {
            List<LeagueTeamSimple> removeList = Lists.newArrayList();
            // 已经被移除的球队，从缓存中删除
            list.stream().filter(l -> !teamIds.contains(l.getTeamId())).forEach(l -> {
                removeLeagueTeamSimple(leagueId, l.getTeamId());
                removeList.add(l);
            });
            list.removeAll(removeList);
        }
        return list;
    }

    private LeaguePB.LeagueDonateData getLeagueDonateData(LeagueDonateLog l) {
        return LeaguePB.LeagueDonateData.newBuilder()
            .setMoney(l.getMoney())
            .setTeamName(l.getTeamName())
            .setTime(l.getTime())
            .build();
    }

    public LeagueTeam getLeagueTeam(long teamId) {
        LeagueTeam team = leagueTeamMap.get(teamId);
        if (team == null) {//本地无数据，从DB取
            LeagueTeamPO ltp = leagueAO.getLeagueTeamPO(teamId);
            if (ltp != null) {//将DB中取出来的数据进行初始化放入本地数据中
                team = LeagueTeam.instantLeagueTeam(ltp);
                leagueTeamMap.put(teamId, team);
            } else {
                team = LeagueTeam.createLeagueTeam(0, teamId, ELeagueTeamLevel.成员, 0);
            }
        }
        return team;
    }

    /**
     * 退出联盟，是否会空出职位
     *
     * @param lt
     * @param league
     * @param adminName
     */
    private void quitLeague(LeagueTeam lt, League league, String adminName) {
        long teamId = lt.getTeamId();
        //移除联盟成员列表
        leagueTeamList.get(league.getLeagueId()).remove(lt.getTeamId());
        removeLeagueTeamSimple(league.getLeagueId(), lt.getTeamId());
        //
        lt.quit();
        league.updatePeopleCount(-1);
        rankManager.updateLeaguePeople(league.getLeagueId(), -1);
        //trainManager.updateTrainIndexLeague(teamId, 0, "");
        addLeagueLog(league.getLeagueId(), adminName, StringUtil.formatString(_quitLeagueLog, teamManager.getTeamNameById(teamId)));
        buffManager.removeBuff(teamId, EBuffKey.联盟球馆赛, EBuffType.工资帽);
        buffManager.removeBuff(teamId, EBuffKey.联盟组队赛, EBuffType.工资帽);
        rankManager.updateTeamLeagueName(teamId, "");
    }

    /**
     * 添加联盟动态
     *
     * @param leagueId
     * @param teamName
     * @param msg
     */
    public void addLeagueLog(int leagueId, String teamName, String msg) {
        //行为放入联盟动态
        redis.addListValueL(RedisKey.League_Log + leagueId, new LeagueLog(teamName, msg, DateTimeUtil.getNowTimeDate()));
    }

    /**
     * 加入联盟
     *
     * @param leagueId
     * @param teamId
     * @param leagueLevel
     */
    private synchronized boolean joinTeam(int leagueId, long teamId, ELeagueTeamLevel leagueLevel
        , int teamLevel, String leagueName, String adminName) {
        LeagueTeam leagueTeam = leagueTeamMap.get(teamId);
        if (leagueTeam != null && leagueTeam.getLeagueId() != 0) { return false; }
        leagueTeam = LeagueTeam.createLeagueTeam(leagueId, teamId, leagueLevel, leagueTeam == null ? 0 : leagueTeam.getFeats());
        leagueTeamMap.put(teamId, leagueTeam);//放入缓存
        leagueTeamList.computeIfAbsent(leagueId, key -> ConcurrentHashMap.newKeySet()).add(teamId);//放入成员列表
        LeagueTeamSimple lts = new LeagueTeamSimple(teamId, teamManager.getTeamNameById(teamId), teamLevel, leagueLevel.getId());
        setLeagueTeamSimple(leagueId, lts);
        //移除历史申请和入盟数据
        clearAllApplyInfo(teamId);
        redis.del(RedisKey.getKey(teamId, RedisKey.League_Team_Join));
        redis.del(RedisKey.getKey(teamId, RedisKey.League_Team_Invite));
        //
        rankManager.updateTeamLeagueName(teamId, leagueName);
        //trainManager.updateTrainIndexLeague(teamId, leagueId, leagueName);
        if (leagueLevel != ELeagueTeamLevel.盟主) {
            rankManager.updateLeaguePeople(leagueId, 1);
            League league = getLeague(leagueId);
            league.updatePeopleCount(1);
            //联盟成员数据
            addLeagueLog(leagueId, adminName, StringUtil.formatString(_joinLeagueLog, teamManager.getTeamNameById(teamId)));
            sendMessage(teamId, getGameLoadLeagueData(leagueTeam, league), ServiceCode.Push_League_Join);
        }
        taskManager.updateTask(teamId, ETaskCondition.联盟, 1, EModuleCode.联盟.getName());
        chatManager.registerLeagueChat(teamId, leagueId);
        
        // 加联盟训练馆
        LeagueTrain leagueTrain = trainManager.getLeagueTrainByLeagueId(leagueId);
        if(leagueTrain != null) {
            trainManager.addLeagueTrain(teamId, leagueTrain.getBlId());  
        }
        
        return true;
    }

    /**
     * 取联盟的玩家申请列表
     * 过滤掉已经入盟的申请
     *
     * @param leagueId
     * @return
     */
    private List<TeamSimple> getLeagueJoinHistory(int leagueId) {
        List<TeamSimple> list = redis.getList(RedisKey.getKey(leagueId, RedisKey.League_Join_History));
        if (list == null) {
            list = Lists.newArrayList();
        }
        list = list.stream().filter(t -> {
            boolean b = getLeagueId(t.getTeamId()) == 0;
            if (!b) {
                redis.removeListValue(RedisKey.getKey(leagueId, RedisKey.League_Join_History), t);
            }
            return b;
        }).collect(Collectors.toList());
        return list;
    }

    /**
     * 添加捐赠日志
     *
     * @param teamId
     * @param teamName
     * @param money
     */
    private void addDonateLog(int leagueId, long teamId, String teamName, int money) {
        LeagueDonateLog l = new LeagueDonateLog(teamId, teamName, money);
        redis.addListValueLExp(RedisKey.getDayKey(leagueId, RedisKey.League_Donate_Log), l);
    }

    private int[] getPage(int page) {
        int[] result = new int[2];
        result[1] = page * 10 - 1;
        result[0] = result[1] - 9;
        return result;
    }

    private LeaguePB.LeagueTeamSimpleData getLeagueTeamSimpleData(TeamSimple ts) {
        return LeaguePB.LeagueTeamSimpleData.newBuilder()
            .setTeamLevel(ts.getLevel())
            .setTeamId(ts.getTeamId())
            .setTeamName(ts.getTeamName())
            .setTime(ts.getCreateTime())
            .build();
    }

    private LeaguePB.LeagueTeamSimpleData getLeagueTeamSimpleData(LeagueTeamSimple ts, boolean online) {
        return LeaguePB.LeagueTeamSimpleData.newBuilder()
            .setTeamLevel(ts.getLevel())
            .setTeamId(ts.getTeamId())
            .setTeamName(ts.getTeamName())
            .setTime(ts.getLastLoginTime())
            .setLevel(ts.getLeagueLevel())
            .setOnline(online)
            .setScore(ts.getScore())
            .build();
    }

    private LeaguePB.LeagueLogData getLeagueLogData(LeagueLog l) {

        return LeaguePB.LeagueLogData.newBuilder()
            .setContext(l.getContext())
            .setTeamName(l.getTeamName())
            .setTime(l.getCreateTime())
            .build();

    }

    private LeaguePB.LeagueSimpleData getLeagueSimpleData(LeagueSimple ls) {
        return LeaguePB.LeagueSimpleData.newBuilder()
            .setLeagueId(ls.getLeagueId())
            .setLeagueLevel(ls.getLevel())
            .setLeagueName(ls.getLeagueName())
            .build();
    }

    private LeaguePB.LeagueMain getLeagueMain(League league, int rank, int level) {

        return LeaguePB.LeagueMain.newBuilder()
            .setHonorLimit(league.getHonorLimit())
            .setShopLimit(league.getShopLimit())
            .setLeague(getLeagueListData(league, rank))
            .setTeamLevel(level)
            .addAllHonors(leagueHonorManager.getLeagueHonorMain(league.getLeagueId()))
            .setHonor(league.getHonor())
            .build();

    }

    /**
     * 联盟申请记录
     *
     * @param teamId
     * @param leagueId
     * @param ts
     * @param tmp
     */
    private void joinLeagueCache(long teamId, int leagueId, TeamSimple ts, LeagueSimple tmp) {
        //增加一条自身的联盟申请记录
        redis.addListValueR(RedisKey.getKey(teamId, RedisKey.League_Team_Join), tmp);
        //增加一条联盟的玩家申请记录
        redis.addListValueL(RedisKey.getKey(leagueId, RedisKey.League_Join_History), ts);
    }

    /**
     * 取申请列表
     *
     * @param teamId
     * @return
     */
    private List<LeagueSimple> getJoinLeagueList(long teamId) {
        return redis.getList(RedisKey.getKey(teamId, RedisKey.League_Team_Join));
    }

    /**
     * 取邀请列表
     *
     * @param teamId
     * @return
     */
    private List<LeagueSimple> getInviteLeagueList(long teamId) {
        return redis.getList(RedisKey.getKey(teamId, RedisKey.League_Team_Invite));
    }

    /**
     * 新增邀请
     *
     * @param teamId
     * @param ls
     */
    private void appendInviteLeagueList(long teamId, LeagueSimple ls) {
        redis.addListValueR(RedisKey.getKey(teamId, RedisKey.League_Team_Invite), ls);
    }

    public League getLeague(int leagueId) {
        return this.leagueMap.get(leagueId);
    }

    private LeaguePB.LeagueData getLeagueListData(League league, int rank) {
        return LeaguePB.LeagueData.newBuilder().setCount(league.getPeopleCount())
            .setLeagueLevel(league.getLeagueLevel())
            .setLeagueLogo(league.getLogo())
            .setLeagueName(league.getLeagueName())
            .setLeagueTip(league.getLeagueTip())
            .setRank(rank)
            .setLeagueNotice(league.getLeagueNotice())
            .setTeamName(league.getTeamName())
            .setLeagueId(league.getLeagueId())
            .setLeagueExp(league.getLeagueExp())
            .setLeagueTotalExp(league.getLeagueTotalExp())
            .setLeagueDailyTotalScore(leagueHonorManager.getLeagueDailyTotalScore(league.getLeagueId()))
            .build();
    }

    public LeaguePB.GameLoadLeagueData getGameLoadLeagueData(LeagueTeam lt, League league) {
        return LeaguePB.GameLoadLeagueData.newBuilder()
            .setHonor(lt.getScore())
            .setFeats(lt.getFeats())
            .setHonorLimit(league.getHonorLimit())
            .setLeagueId(lt.getLeagueId())
            .setLeagueName(league.getLeagueName())
            .setShopLimit(league.getShopLimit())
            .setLevel(lt.getLevel().getId())//盟主(1),副盟主(2),理事(3),精英(4),成员(5);
            .addAllHonorList(leagueHonorManager.getLeagueHonorMain(league.getLeagueId()))
            .build();
    }

    private int getLeagueNewId() {
        return GameSource.shardId * 10000 + ids.incrementAndGet();
    }

    @Override
    public void offline(long teamId) {
        leagueTeamMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        leagueTeamMap.remove(teamId);
    }

    @Override
    public void initConfig() {
        _createLevel = ConfigConsole.getIntVal(EConfigKey.League_Create_Level);
        _createMoney = ConfigConsole.getIntVal(EConfigKey.League_Create_Money);
        _createProp = ConfigConsole.getIntVal(EConfigKey.League_Create_Prop);
        _maxLeagueFMZ = ConfigConsole.getIntVal(EConfigKey.League_Max_FMZ);
        _maxLeagueLS = ConfigConsole.getIntVal(EConfigKey.League_Max_LS);
        _maxLeagueJY = ConfigConsole.getIntVal(EConfigKey.League_Max_JY);
        _maxLeagueCY = Arrays.stream(ConfigConsole.getVal(EConfigKey.League_Max_CY).split(",")).mapToInt(n -> Integer.valueOf(n)).boxed().collect(Collectors.toList());
        _createLeagueLog = ConfigConsole.getVal(EConfigKey.League_Create_Log);
        _quitLeagueLog = ConfigConsole.getVal(EConfigKey.League_Quit_Log);
        _joinLeagueLog = ConfigConsole.getVal(EConfigKey.League_Join_Log);
        _donateMoneyLeagueLog = ConfigConsole.getVal(EConfigKey.League_Donate_Money_Log);
        _donateMedalLeagueLog = ConfigConsole.getVal(EConfigKey.League_Donate_Medal_Log);
        _levelUpLeagueLog = ConfigConsole.getVal(EConfigKey.League_Level_Up_Log);
        _honorLevelUpLeagueLog = ConfigConsole.getVal(EConfigKey.League_Honor_Level_Up_Log);
        _appotionLeagueLog = ConfigConsole.getVal(EConfigKey.League_Appotion_Log);
        _noticeLeagueLog = ConfigConsole.getVal(EConfigKey.League_Notice_Log);
        _declarationLeagueLog = ConfigConsole.getVal(EConfigKey.League_Declaration_Log);
    }

    @Override
    public int getOrder() {
        return ManagerOrder.League.getOrder();
    }

    @Override
    public void instanceAfter() {
        leagueMap = Maps.newConcurrentMap();
        leagueTeamMap = Maps.newConcurrentMap();
        leagueTeamList = Maps.newConcurrentMap();
        leagueNameMap = HashBiMap.create();
        
        redis.del(RedisKey.Rank_Sort + ERank.League.getType());// 联盟Id -> 联盟排行Score
        redis.del(RedisKey.Rank_Info + ERank.League.getType());// 联盟Id -> LeagueRank
        
        //
        leagueAO.getAllLeague().forEach(po -> leagueMap.put(po.getLeagueId(), new League(po)));
        int maxId = leagueMap.values().stream().mapToInt(l -> l.getLeagueId()).max().orElse(1);
        //
        List<LeagueTeamSimplePO> teams = leagueAO.getAllLeagueTeam();
        teams.forEach(po -> leagueTeamList.computeIfAbsent(po.getLeagueId(), key -> Sets.newHashSet()).add(po.getTeamId()));
        //从DB中取最高的ID
        ids = new AtomicInteger(maxId);
        //启动服务器的时候，刷新一边联盟缓存
        leagueMap.values().forEach(l -> {
            LeagueHonor h = leagueHonorManager.getLeagueHonor(l.getLeagueId());
            if (h != null) {
                rankManager.updateLeagueRank(l, h.getAllLevel());
            }
            leagueNameMap.put(l.getLeagueId(), l.getLeagueName());
        });
        
    }

    /**
     * 根据等级取最大成员数量
     *
     * @param level
     * @return
     */
    private int getMaxCY(int level) {
        return this._maxLeagueCY.get(level - 1);
    }

    /** 取所有联盟id列表 */
    public List<Integer> getLeagueIds() {
        if (leagueMap == null) { return null; }
        return Lists.newArrayList(leagueMap.keySet());
    }

    /** 取所有联盟成员数据列表 */
    public List<LeagueTeam> getLeagueTeamDataList(Integer leagueId) {
        Set<Long> teamIds = getLeagueTeamList(leagueId);
        if (teamIds == null) { return null; }
        List<Long> teamIdList = Lists.newArrayList(teamIds);

        List<LeagueTeam> leagueteams = Lists.newArrayList();
        teamIdList.forEach(teamId -> {
            leagueteams.add(getLeagueTeam(teamId));
        });
        return leagueteams;
    }
}
