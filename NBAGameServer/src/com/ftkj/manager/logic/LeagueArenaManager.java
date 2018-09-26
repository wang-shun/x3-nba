package com.ftkj.manager.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joda.time.DateTime;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.LeagueArenaConsole;
import com.ftkj.db.ao.logic.ILeagueAO;
import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleManager;
import com.ftkj.manager.battle.handle.BattleCommon;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.buff.TeamBuff;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.league.leagueArean.AreanRewardBean;
import com.ftkj.manager.league.leagueArean.LeagueAreanTeam;
import com.ftkj.manager.league.leagueArean.LeaguePostion;
import com.ftkj.manager.league.leagueArean.LeagueTotalMScoreRank;
import com.ftkj.manager.league.leagueArean.LeagueWeekScoreRank;
import com.ftkj.manager.league.leagueArean.PostionScoreBean;
import com.ftkj.manager.logic.LocalBattleManager.BattleContxt;
import com.ftkj.manager.logic.RankManager.TeamCap;
import com.ftkj.manager.logic.TeamNumManager.TeamNum;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.manager.train.LeagueTrain;
import com.ftkj.proto.LeagueMatchPB.ChangeLeaguePostions;
import com.ftkj.proto.LeagueMatchPB.LeagueHistoryRankData;
import com.ftkj.proto.LeagueMatchPB.LeagueHistoryRanks;
import com.ftkj.proto.LeagueMatchPB.LeagueMatchRankData;
import com.ftkj.proto.LeagueMatchPB.LeagueMatchRanks;
import com.ftkj.proto.LeagueMatchPB.LeagueMatchTeamRankData;
import com.ftkj.proto.LeagueMatchPB.LeagueMatchTeamRanks;
import com.ftkj.proto.LeagueMatchPB.LeaguePostionChallangeState;
import com.ftkj.proto.LeagueMatchPB.LeaguePostionData;
import com.ftkj.proto.LeagueMatchPB.LeagueScoreRankData;
import com.ftkj.proto.LeagueMatchPB.LeagueScoreRanks;
import com.ftkj.proto.LeagueMatchPB.leagueMatahTeamData;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.quartz.QuartzServer;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 联盟球馆赛
 * @author Jay
 * @time:2017年7月31日 上午11:06:21
 */
public class LeagueArenaManager extends BaseManager {
    @IOC
    private ILeagueAO leagueAO;
    @IOC
    public static LeagueManager leagueManager;
    @IOC
    public static TeamManager teamManager;
    @IOC
    public static BuffManager buffManager;
    @IOC
    public static PropManager propManager;
    @IOC
    public static TeamEmailManager teamEmailManager;
    @IOC
    public static TaskManager taskManager;
    @IOC
    private LocalBattleManager localBattleManager;
    @IOC
    private RankManager rankManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private BattleManager battleManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamNumManager teamNumManager;
    @IOC
    private TrainManager trainManager;

    /** 比赛结束处理 */
    private BattleEnd battleEnd = this::endMatch0;    

    /** 联盟赛联盟总积分排行数据缓存<类型,<联盟ID, 总积分排行数据>>*/
    private Map<Integer, Map<Integer, LeagueTotalMScoreRank>> rankMap = Maps.newConcurrentMap();

    /** 操作模块 */
    private ModuleLog mc = ModuleLog.getModuleLog(EModuleCode.联盟赛球馆赛, "LeagueArena");
    
    /** 参赛类型 -> 类型:名额[type:num]*/
    public static List<Integer[]> types = Lists.newArrayList(); 

    @Override
    public void instanceAfter() {        
        // 初始化联盟类型和参赛名额
        String[] typeStr = StringUtil.toStringArray(ConfigConsole.getGlobal().leagueArenaQuota, StringUtil.COMMA);
        for(int index = 0; index < typeStr.length; index++) {
            types.add(StringUtil.toIntegerArray(typeStr[index], StringUtil.COLON));
        }       
        
        // 发奖调度
        endRankRewardTask();
    }

    /** 初始化可参赛联盟ID*/
    private void InitLeagueQuota() {
        if(DateTimeUtil.getCurrWeekDay() != ConfigConsole.getGlobal().leagueArenaOpenWeekday) return; 
    
        // 初始化参与联盟赛的联盟ID状态
        Integer state = redis.getInt(RedisKey.League_Arena_Ids_Refresh_State);
        if (state != null && state == 1)
            return;
        
        // 参赛名额
        List<String> listStr = redis.zrevrange(RedisKey.League_Week_Score_Sum, 0, -1);
        if (listStr.size() < 1)
            return;

        List<Integer> leagueIds = Lists.newArrayList();
        for (String s : listStr) {
            int leagueId = Integer.valueOf(s);
            
            List<LeagueTeam> leagueteams = leagueManager.getLeagueTeamDataList(leagueId);
            if (leagueteams == null)
                continue;
            
            int sumScore = leagueteams.stream().mapToInt(leagueteam -> leagueteam.getWeekScore()).sum();
            if (sumScore < 1)
                continue;
            
            leagueIds.add(leagueId);
        }

        
        // 设置刷新状态
        redis.set(RedisKey.League_Arena_Ids_Refresh_State, 1+"");
        
        int index = 0;
        for (Integer[] type : types) {
            if (leagueIds.size() > index + type[1]) {
                List<Integer> list = leagueIds.subList(index, type[1] + index);
                redis.rpush(RedisKey.getLeagueArenaIds(type[0]), list);
                log.info("InitLeagueQuota type{}, leagueIds{}", type[0], list.toString());
            
            } else { 
                List<Integer> list = leagueIds.subList(index, leagueIds.size());
                redis.rpush(RedisKey.getLeagueArenaIds(type[0]), list);
                log.info("InitLeagueQuota type{}, leagueIds{}", type[0], list.toString());
                return;
            }  
            
            index = index + type[1];
        }     
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_GetLeaguePostions)
    public void getLeaguePostions() {
        long teamId = getTeamId();

        // 发奖时间结束
        if (LeagueArenaConsole.isSendRewardEnd(new DateTime())) {
            sendMsg(ErrorCode.League_Arena_5);
            return;
        }

        // 不在赛区内无法获取列表
        int type = isCanJoinMatchType(teamId);
        if (type < 1) {
            sendMsg(ErrorCode.League_Arena_6);
            return;
        }

        ChangeLeaguePostions.Builder builder = ChangeLeaguePostions.newBuilder();
        builder.setType(type);

        for (Map.Entry<Integer, LeaguePostion> mapPos : this.getLeaguePostionMapByType(type).entrySet()) {
            builder.addPostions(builderLeaguePostionData(mapPos.getValue()));
        }

        sendMsg(ErrorCode.Success);
        sendMessage(teamId, builder.build(), ServiceCode.LeagueArenaManager_push_leaguepostions);
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_GetLeagueHistoryRanks)
    public void getLeagueHistoryRanks(int type) {
        // 赛时内无法获取列表
        if (LeagueArenaConsole.isPKingTime(new DateTime())) {
            sendMsg(ErrorCode.League_Arena_7);
            return;
        }

        long teamId = getTeamId();
        LeagueHistoryRanks.Builder builder = LeagueHistoryRanks.newBuilder();

        List<String> rankStrList = redis.zrevrange(RedisKey.getLeagueHistoryRankKey(type), 0, -1);
        int rank = 1;
        for (String leagueIdStr : rankStrList) {
            int leagueId = Integer.valueOf(leagueIdStr);
            builder.addRankList(builderLeagueHistoryRankData(leagueId, rank));
            rank++;
        }

        sendMsg(ErrorCode.Success);
        sendMessage(teamId, builder.build(), ServiceCode.LeagueArenaManager_push_leagueHistoryRanks);
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_GetLeagueScoreRanks)
    public void getLeagueScoreRanks(int count) {
        long teamId = getTeamId();

        LeagueScoreRanks.Builder builder = LeagueScoreRanks.newBuilder();
        builder.setType(isCanJoinMatchType(teamId));

        Map<Integer, LeagueWeekScoreRank> leagueScoreRankMap = getLeagueWeekScoreRank(count);
        for (Map.Entry<Integer, LeagueWeekScoreRank> entry : leagueScoreRankMap.entrySet()) {
            builder.addRankList(builderLeagueScoreRankData(entry.getValue()));
        }

        sendMsg(ErrorCode.Success);
        sendMessage(teamId, builder.build(), ServiceCode.LeagueArenaManager_push_leagueScoreRanks);
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_GetLeagueMatchRanks)
    public void getLeagueMatchRanks(int type) {      
        long teamId = getTeamId();
        
        // 发奖时间结束
        if (LeagueArenaConsole.isSendRewardEnd(new DateTime())) {
            sendMsg(ErrorCode.League_Arena_5);
            return;
        }

        LeagueMatchRanks.Builder builder = LeagueMatchRanks.newBuilder();

        List<LeagueTotalMScoreRank> list = this.getTotalMScoreRank(type);
        list.forEach(rank -> {
            builder.addRankList(builderLeagueMatchRankData(rank));
        });

        sendMsg(ErrorCode.Success);
        sendMessage(teamId, builder.build(), ServiceCode.LeagueArenaManager_push_leagueMatchRanks);
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_GetLeagueMatchTeamRanks)
    public void getLeagueMatchTeamRanks(int type, int count) {       
        long teamId = getTeamId();
        
        // 发奖时间结束
        if (LeagueArenaConsole.isSendRewardEnd(new DateTime())) {
            sendMsg(ErrorCode.League_Arena_5);
            return;
        }
        
        List<LeagueAreanTeam> rankList = this.getLeagueArenaTeamRank(type, count);

        LeagueMatchTeamRanks.Builder builder = LeagueMatchTeamRanks.newBuilder();
        rankList.forEach(LeagueAreanTeam -> {
            builder.addRankList(builderLeagueMatchTeamRankData(LeagueAreanTeam));
        });

        sendMsg(ErrorCode.Success);
        sendMessage(teamId, builder.build(), ServiceCode.LeagueArenaManager_push_leagueMatchTeamRanks);
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_occupy)
    public void occupy(int posId, int type) {
        if (!LeagueArenaConsole.isPKingTime(new DateTime())) {
            sendMsg(ErrorCode.League_Arena_5);
            return;
        }

        PostionScoreBean postionScoreBean = LeagueArenaConsole.postionScoreMap.get(posId);
        if (postionScoreBean == null) {
            sendMsg(ErrorCode.League_Arena_4);
            return;
        }

        long teamId = getTeamId();
        int leagueId = leagueManager.getLeagueId(teamId);

        // 是否有资格占位
        if (isCanJoinMatchType(teamId) != type) {
            sendMsg(ErrorCode.League_Arena_2);
            return;
        }

        // 清理历史占位数据
        int lastPosId = getPosIdByTeamId(teamId);
        // 占领自己的位置
        if (posId == lastPosId) {
            sendMsg(ErrorCode.League_Arena_8);
            return;
        }

        // 新位置数据
        LeaguePostion leaguePostion = getLeaguePostion(type, posId);
        if (leaguePostion.getTeamId() > 0 && leaguePostion.getLeagueId() > 0) {
            sendMsg(ErrorCode.League_Arena_10);
            return;
        }

        // 改变的位置数据列表
        List<LeaguePostion> changeList = Lists.newArrayList();

        // 清理
        LeaguePostion changePostion = this.resetPostion(type, lastPosId);
        if (changePostion != null) {
            changeList.add(changePostion);
        }

        // 设置
        leaguePostion.setLeagueId(leagueId);
        leaguePostion.setTeamId(teamId);
        changeList.add(leaguePostion);

        // 设置redis
        redis.set(RedisKey.getLeaguePostionTeamKey(teamId), type + "_" + posId);
        redis.hset(RedisKey.getLeaguePostionKey(type), String.valueOf(posId), leagueId + "_" + teamId + "_" + 0);

        sendMsg(ErrorCode.Success);
        this.pushChangeLeaguePostionToALL(type, changeList);
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_challenge)
    public void challenge(int posId, int type) {
        long teamId = getTeamId();

        if (!LeagueArenaConsole.isPKingTime(new DateTime())) {
            sendMsg(ErrorCode.League_Arena_5);
            return;
        }

        PostionScoreBean postionScoreBean = LeagueArenaConsole.postionScoreMap.get(posId);
        if (postionScoreBean == null) {
            sendMsg(ErrorCode.League_Arena_4);
            return;
        }

        // 是否有资格挑战
        if (isCanJoinMatchType(teamId) != type) {
            sendMsg(ErrorCode.League_Arena_2);
            return;
        }

        LeaguePostion leaguePostion = this.getLeaguePostion(type, posId);
        if (leaguePostion.getTeamId() < 1) {
            sendMsg(ErrorCode.League_Arena_9);
            return;
        }

        if (isInBattle(leaguePostion.getTeamId()) || isInBattle(teamId)) {
            sendMsg(ErrorCode.League_Arena_1);
            return;
        }

        // 无法挑战同盟玩家
        Set<Long> lists = leagueManager.getLeagueTeamList(leaguePostion.getLeagueId());
        if (lists.contains(teamId)) {
            sendMsg(ErrorCode.League_Arena_11);
            return;
        }

        // 无法挑战自己
        int lastPosId = getPosIdByTeamId(teamId);
        if (posId == lastPosId) {
            sendMsg(ErrorCode.League_Arena_8);
            return;
        }

        // 下次可挑战时间
        LeagueAreanTeam leagueAreanTeam = this.getLeagueAreanTeam(teamId, type);
        if (leagueAreanTeam.getWartime() > 0) {
            if (DateTimeUtil.difTimeMill(leagueAreanTeam.getWartime()) < 0) {
                sendMsg(ErrorCode.League_Arena_0);
                return;
            }
        }

        // 放弃之前的占位
        List<LeaguePostion> changeList = Lists.newArrayList();
        LeaguePostion postion = this.resetPostion(type, lastPosId);
        if (postion != null) {
            changeList.add(postion);
            
            this.pushLeagueChallangeState(teamId, 2);
        }

        // 加载比赛
        EBattleType bt = EBattleType.联盟球馆赛;
        BattleSource bs = localBattleManager.buildBattle(bt, teamId, leaguePostion.getTeamId(), null, null, teamId);
        BattleContxt bc = localBattleManager.defaultContext(battleEnd);
        BaseBattleHandle bb = new BattleCommon(bs);
        localBattleManager.initBattleWithContext(bb, bs, bc);
        localBattleManager.start(bs, bc, bb);

        leaguePostion.setBattleId(bs.getBattleId());
        changeList.add(leaguePostion);
        this.pushChangeLeaguePostionToALL(type, changeList);

        redis.hset(RedisKey.getLeaguePostionKey(type), String.valueOf(posId), leaguePostion.getLeagueId() + "_" + leaguePostion.getTeamId() + "_" + leaguePostion.getBattleId());
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_speed_up)
    public void speedUp() {
        long teamId = getTeamId();

        if (!LeagueArenaConsole.isPKingTime(new DateTime())) {
            sendMsg(ErrorCode.League_Arena_5);
            return;
        }

        int type = isCanJoinMatchType(teamId);
        LeagueAreanTeam leagueAreanTeam = getLeagueAreanTeam(teamId, type);
        ErrorCode ret = teamNumManager.consumeNumCurrency(teamId, TeamNumType.League_Speed_Num, 1, mc);
        if (ret.isError()) {
            sendMsg(ret);
            return;
        }

        leagueAreanTeam.setWartime(System.currentTimeMillis());
        redis.hset(RedisKey.getLeagueArenaTeamKey(type), String.valueOf(teamId), leagueAreanTeam.redisString());
        leagueMatahTeamData.Builder builder = leagueMatahTeamData.newBuilder();
        builder.setPkTime(leagueAreanTeam.getWartime());
        builder.setTeamId(teamId);
        builder.setSpeedCount(teamNumManager.getUsedNum(teamId, TeamNumType.League_Speed_Num));
        sendMessage(teamId, builder.build(), ServiceCode.LeagueArenaManager_push_leagueMatchTeam);
    }

    @ClientMethod(code = ServiceCode.LeagueArenaManager_getLeagueMatchTeamData)
    public void getLeagueAreanTeamData() {
        long teamId = getTeamId();

        if (!LeagueArenaConsole.isPKingTime(new DateTime())) {
            sendMsg(ErrorCode.League_Arena_5);
            return;
        }

        int type = isCanJoinMatchType(teamId);
        LeagueAreanTeam leagueAreanTeam = this.getLeagueAreanTeam(teamId, type);

        leagueMatahTeamData.Builder builder = leagueMatahTeamData.newBuilder();
        builder.setPkTime(leagueAreanTeam.getWartime());
        builder.setTeamId(teamId);
        builder.setSpeedCount(teamNumManager.getUsedNum(teamId, TeamNumType.League_Speed_Num));

        sendMsg(ErrorCode.Success);
        sendMessage(teamId, builder.build(), ServiceCode.LeagueArenaManager_push_leagueMatchTeam);
    }

    /** 清理历史占位数据*/
    private LeaguePostion resetPostion(int type, int lastPosId) {
        if (lastPosId < 1)
            return null;

        LeaguePostion lastPos = this.getLeaguePostion(type, lastPosId);
        // 数据错误, 当前位置没有占领数据
        if (lastPos == null || lastPos.getLeagueId() < 1 || lastPos.getTeamId() < 1) {
            return lastPos;
        }

        long teamId = lastPos.getTeamId();
        lastPos.setLeagueId(0);
        lastPos.setTeamId(0);

        redis.del(RedisKey.getLeaguePostionTeamKey(teamId));
        redis.hset(RedisKey.getLeaguePostionKey(type), lastPosId + "", "");

        return lastPos;
    }

    /** 是否在比赛中 */
    public boolean isInBattle(long teamId) {
        return TeamStatus.inBattle(teamStatusManager.getTeamStatus(teamId), EBattleType.联盟球馆赛);
    }

    private LeaguePostionData.Builder builderLeaguePostionData(LeaguePostion leaguePostion) {
        long teamId = getTeamId();

        // 取球队总攻防
        LeaguePostionData.Builder msg = LeaguePostionData.newBuilder();
        if (leaguePostion.getTeamId() > 1) {
            Team team = teamManager.getTeam(leaguePostion.getTeamId());
            TeamCap tc = rankManager.calcTeamCap(leaguePostion.getTeamId());
            String leagueName = leagueManager.getLeagueName(leaguePostion.getLeagueId());

            TeamPlayer teamPlayer = playerManager.getTeamPlayer(leaguePostion.getTeamId());
            List<Player> players = teamPlayer.getPlayers();

            msg.setCap(tc.getTotalCap());
            msg.setLeagueId(leaguePostion.getLeagueId());
            msg.setLeagueName(leagueName);
            msg.setPosId(leaguePostion.getId());
            msg.setTeamId(leaguePostion.getTeamId());
            msg.setTeamName(team.getTeamInfo().getName());
            msg.setState(isInBattle(leaguePostion.getTeamId()) ? 1 : 0);

            long battleId = this.getBattleId(teamId, EBattleType.联盟球馆赛);
            if (battleId == leaguePostion.getBattleId()) {
                msg.setBattleId(battleId);
            }

            if (players != null) {
                PlayerAbility playerCap = playerManager.getPlayerAbilityByMaxCap(players);
                if (playerCap != null) {
                    Player player = teamPlayer.getPlayer(playerCap.getPlayerId());
                    msg.setPlayerRid(player.getPlayerRid());
                }
            }
        } else {
            msg.setPosId(leaguePostion.getId());
        }

        return msg;
    }

    private LeagueScoreRankData.Builder builderLeagueScoreRankData(LeagueWeekScoreRank leagueScoreRank) {
        League league = leagueManager.getLeague(leagueScoreRank.getLeagueId());

        LeagueScoreRankData.Builder msg = LeagueScoreRankData.newBuilder();
        msg.setLeagueId(leagueScoreRank.getLeagueId());
        msg.setRank(leagueScoreRank.getRank());
        msg.setScore(leagueScoreRank.getWeekScoreSum());
        msg.setLeagueLogo(league.getLogo());
        msg.setLeagueLevel(league.getLeagueLevel());
        msg.setLeagueNum(league.getPeopleCount());
        msg.setLeagueName(league.getLeagueName());

        log.debug("leagueScoreRank : " + leagueScoreRank.toString());

        return msg;
    }

    private LeagueHistoryRankData.Builder builderLeagueHistoryRankData(int leagueId, int rank) {
        League league = leagueManager.getLeague(leagueId);

        LeagueHistoryRankData.Builder msg = LeagueHistoryRankData.newBuilder();
        msg.setLeagueId(leagueId);
        msg.setLeagueLevel(league.getLeagueLevel());
        msg.setLeagueLogo(league.getLogo());
        msg.setLeagueName(league.getLeagueName());
        msg.setLeagueNum(league.getPeopleCount());
        msg.setLeagueRank(rank);

        log.debug("LeagueHistoryRankData leagueId {}, leagueName {}, rank {}", leagueId, league.getLeagueName(), rank);
        return msg;
    }

    private LeagueMatchRankData.Builder builderLeagueMatchRankData(LeagueTotalMScoreRank rank) {
        String leagueName = leagueManager.getLeagueName(rank.getLeagueId());

        LeagueMatchRankData.Builder msg = LeagueMatchRankData.newBuilder();
        msg.setLeagueName(leagueName);
        msg.setFailCount(rank.getFailCountSum());
        msg.setMatchScore(rank.getMatchScoreSum());
        msg.setRank(rank.getRank());
        msg.setWinCount(rank.getWinCountSum());

        return msg;
    }

    private LeagueMatchTeamRankData.Builder builderLeagueMatchTeamRankData(LeagueAreanTeam LeagueAreanTeam) {
        Team team = teamManager.getTeam(LeagueAreanTeam.getTeamId());
        String leagueName = leagueManager.getLeagueName(LeagueAreanTeam.getLeagueId());

        LeagueMatchTeamRankData.Builder msg = LeagueMatchTeamRankData.newBuilder();
        msg.setFailCount(LeagueAreanTeam.getFailCount());
        msg.setLeagueName(leagueName);
        msg.setRank(LeagueAreanTeam.getRank());
        msg.setTeamName(team.getName());
        msg.setWinCount(LeagueAreanTeam.getWinCount());

        return msg;
    }

    /** 每周六刷新联盟贡献排行*/
    public void refreshLeagueWeekScoreRank() {
        List<Integer> list = leagueManager.getLeagueIds();
        if (list == null)
            return;

        list.forEach(leagueId -> {
            List<LeagueTeam> leagueteams = leagueManager.getLeagueTeamDataList(leagueId);
            if (leagueteams == null)
                return;

            int sumScore = leagueteams.stream().mapToInt(leagueteam -> leagueteam.getWeekScore()).sum();
            if (sumScore < 1)
                return;
            
            redis.zadd(RedisKey.League_Week_Score_Sum, sumScore, leagueId + "");
        });        
        
        InitLeagueQuota();      
    }

    /** 每周天0点,清理联盟贡献排行*/
    public void clearLeagueWeekScoreRank() {
        DateTime now = new DateTime();
        int dayOfWeek = now.getDayOfWeek();
        if (dayOfWeek != LeagueArenaConsole.resetScoreRankWeek)
            return;

        log.debug("每周天0点,清理联盟贡献排行");
        redis.del(RedisKey.League_Week_Score_Sum);
        redis.del(RedisKey.League_Arena_Ids_Refresh_State);

        // 清理所有联盟的周贡献
        List<Integer> list = leagueManager.getLeagueIds();
        if (list == null)
            return;

        list.forEach(leagueId -> {
            List<LeagueTeam> leagueteams = leagueManager.getLeagueTeamDataList(leagueId);
            if (leagueteams == null)
                return;

            leagueteams.forEach(leagueteam -> {
                if (leagueteam.getWeekScore() > 0) {
                    leagueteam.setWeekScore(0);
                }
            });
        });

        // 清数据库
        leagueAO.clearAllLeagueTeamWeekScore();
        
    }

    /** 取出指定个数周贡献排行数据*/
    private Map<Integer, LeagueWeekScoreRank> getLeagueWeekScoreRank(int count) {
        this.refreshLeagueWeekScoreRank();

        Map<Integer, LeagueWeekScoreRank> rankMap = Maps.newHashMap();

        List<String> listStr = redis.zrevrange(RedisKey.League_Week_Score_Sum, 0, count - 1);

        int rank = 1;
        for (String leagueIdStr : listStr) {
            int leagueId = Integer.valueOf(leagueIdStr);
            List<LeagueTeam> leagueteams = leagueManager.getLeagueTeamDataList(leagueId);
            if (leagueteams == null)
                continue;
            int sumScore = leagueteams.stream().mapToInt(leagueteam -> leagueteam.getWeekScore()).sum();
            if (sumScore < 1)
                continue;

            LeagueWeekScoreRank leagueScoreRank = new LeagueWeekScoreRank();
            leagueScoreRank.setLeagueId(leagueId);
            leagueScoreRank.setWeekScoreSum(sumScore);
            leagueScoreRank.setRank(rank);

            rankMap.put(rank, leagueScoreRank);

            rank = rank + 1;
        }

        return rankMap;
    }

    /** 刷新球队比赛排行数据*/
    public void refreahLeagueMatchTeamRank(int type) {
        Map<Long, LeagueAreanTeam> teamMap = this.getLeagueArenaTeamMap(type);
        if (teamMap == null) {
            return;
        }

        // 清理历史排行数据
        redis.del(RedisKey.getLeagueMatchTeamRankKey(type));
        teamMap.forEach((k, v) -> redis.zadd(RedisKey.getLeagueMatchTeamRankKey(type), v.getWinScore(), k + ""));
    }

    /** 取出参赛联盟球队比赛排行数据*/
    private List<LeagueAreanTeam> getLeagueArenaTeamRank(int type, int count) {
        // 获取数据前刷新
        this.refreahLeagueMatchTeamRank(type);

        Map<Long, LeagueAreanTeam> teamMap = this.getLeagueArenaTeamMap(type);
        List<LeagueAreanTeam> rankList = Lists.newArrayList();

        List<String> rankStrList = redis.zrevrange(RedisKey.getLeagueMatchTeamRankKey(type), 0, count - 1);

        int rank = 1;
        for (String teamIdStr : rankStrList) {

            long teamId = Long.valueOf(teamIdStr);
            LeagueAreanTeam LeagueAreanTeam = teamMap.get(teamId);
            LeagueAreanTeam.setRank(rank);

            rankList.add(LeagueAreanTeam);

            rank++;
        }

        return rankList;
    }

    /** 刷新联盟赛联盟总积分排行数据*/
    public void refrushTotalMScoreRank(int type) {
        
        List<Integer> leagueIds = redis.getList(RedisKey.getLeagueArenaIds(type));
        if (leagueIds == null || leagueIds.size() < 1)
            return;

        Map<Integer, LeagueTotalMScoreRank> totalMap = rankMap.get(type);
        if (totalMap == null) {
            totalMap = Maps.newConcurrentMap();
            rankMap.put(type, totalMap);
        } else {
            // 每次刷新,先清理一下以前的数据
            totalMap.clear();
            redis.del(RedisKey.getLeagueHistoryRankKey(type));
        }

        Map<Long, LeagueAreanTeam> leagueAreanTeamMap = this.getLeagueArenaTeamMap(type);
        for (int leagueId : leagueIds) {
            // 联盟成员比赛积分总和
            List<Long> teamIds = Lists.newArrayList(leagueManager.getLeagueTeamList(leagueId));

            int scoreSum = 0;
            int winSum = 0;
            int failSum = 0;
            for (long tid : teamIds) {
                LeagueAreanTeam leagueteam = leagueAreanTeamMap.get(tid);
                if (leagueteam == null)
                    continue;

                scoreSum = scoreSum + leagueteam.getWinScore();
                winSum = winSum + leagueteam.getWinCount();
                failSum = failSum + leagueteam.getFailCount();
            }

            // 训练馆占位积分总和
            scoreSum = scoreSum + this.getLeagueScoreSum(type, leagueId);

            LeagueTotalMScoreRank leagueTotalMScoreRank = new LeagueTotalMScoreRank();
            leagueTotalMScoreRank.setFailCountSum(failSum);
            leagueTotalMScoreRank.setWinCountSum(winSum);
            leagueTotalMScoreRank.setLeagueId(leagueId);
            leagueTotalMScoreRank.setMatchScoreSum(scoreSum);
            totalMap.put(leagueId, leagueTotalMScoreRank);

            // 加入历史记录数据
            redis.zadd(RedisKey.getLeagueHistoryRankKey(type), scoreSum, leagueId + "");
        }
    }

    /** 取联盟总积分*/
    private int getLeagueScoreSum(int type, int leagueId) {

        int scoreSum = 0;

        Map<Integer, LeaguePostion> posMap = this.getLeaguePostionMapByType(type);
        for (Map.Entry<Integer, LeaguePostion> entry : posMap.entrySet()) {
            LeaguePostion leaguePostion = entry.getValue();
            if (leaguePostion.getLeagueId() != leagueId)
                continue;

            PostionScoreBean psb = LeagueArenaConsole.getPostionScoreMap().get(leaguePostion.getId());
            scoreSum = scoreSum + psb.getEndScore();
        }

        return scoreSum;
    }

    /** 获取联盟赛联盟总积分排行数据*/
    private List<LeagueTotalMScoreRank> getTotalMScoreRank(int type) {

        // 获取前刷新
        refrushTotalMScoreRank(type);

        Map<Integer, LeagueTotalMScoreRank> totalMap = rankMap.get(type);
        if (totalMap == null)
            return null;

        // 加入历史记录数据
        List<String> rankStrList = redis.zrevrange(RedisKey.getLeagueHistoryRankKey(type), 0, -1);

        List<LeagueTotalMScoreRank> rankList = Lists.newArrayList();

        int index = 1;
        for (String leagueIdStr : rankStrList) {

            int leagueId = Integer.valueOf(leagueIdStr);
            LeagueTotalMScoreRank leagueTotalMScoreRank = totalMap.get(leagueId);
            if (leagueTotalMScoreRank == null)
                continue;

            leagueTotalMScoreRank.setRank(index);

            rankList.add(leagueTotalMScoreRank);

            index++;
        }

        return rankList;
    }

    /** 比赛结束清理球队相关*/
    private void clearTeam(long teamId) {
        // 结束结算时清理
        redis.del(RedisKey.getLeaguePostionTeamKey(teamId));

        // 清理加速次数
        TeamNum teamNum = new TeamNum(teamId);
        teamNum.setNum(0);
        teamNum.setType(TeamNumType.League_Speed_Num);
        teamNumManager.resetNumTo0(teamNum);
    }

    /** 比赛结束清理所有相关数据*/
    private void clearAll() {
        types.forEach(type -> {
            redis.del(RedisKey.getLeagueArenaIds(type[0]));    
            // 比赛结束清理
            redis.del(RedisKey.getLeaguePostionKey(type[0]));
            redis.del(RedisKey.getLeagueArenaTeamKey(type[0]));
        }); 
        
        if(!rankMap.isEmpty()) {
            rankMap.clear();
        }
    }

    /** 可参赛类型*/
    public int isCanJoinMatchType(long teamId) {
        int leagueId = leagueManager.getLeagueId(teamId);
        if (leagueId < 1)
            return 0;

       
        for (Integer[] type : types) {
            List<Integer> leagueIds = redis.getList(RedisKey.getLeagueArenaIds(type[0]));
            if(leagueIds.contains(leagueId)) {
                return type[0];
            }
        }     

        return 0;
    }

    /** 发奖调度*/
    public void quartzSendReward() {
        if(DateTimeUtil.getCurrWeekDay() != LeagueArenaConsole.OpenDayOfWeek)
            return;

        DateTime now = new DateTime();
        if (!LeagueArenaConsole.isEnd(now))
            return;

        types.forEach(type -> {
            log.debug("quartzSendReward rankList type{}", type[0]);
            
            List<LeagueTotalMScoreRank> rankList = this.getTotalMScoreRank(type[0]);
            if (rankList == null || rankList.isEmpty()) {
                log.debug("quartzSendReward rankList is null, type{}", type[0]);
                return; 
            }
            
            log.debug("quartzSendReward rankList  type{}, size{},", type[0], rankList.size());
              
            Map<Integer, AreanRewardBean> rankRewardMap = LeagueArenaConsole.getMatchRewardMap().get(type[0]);
            if (rankRewardMap == null) {
                log.error("config is error, quartzSendReward rankRewardMap is null, type{}", type[0]);
                return;
            }
              
            rankList.forEach(rank -> {
                log.debug("quartzSendReward rank: {}", rank);
                
                AreanRewardBean arenaRewardBean = rankRewardMap.get(rank.getRank());
                if (arenaRewardBean == null) {
                    log.error("config is error, quartzSendReward rank{}, arenaRewardBean data is null", rank);
                    return;
                }
                
                Set<Long> teamList = leagueManager.getLeagueTeamList(rank.getLeagueId());
                if (teamList == null || teamList.isEmpty()) {
                    log.debug("quartzSendReward leagueId{}, teamList data is null", rank.getLeagueId());
                    return;
                }
                
                // 工资帽
                DateTime endTime = now.plusDays(7);
                Integer[] priceList = arenaRewardBean.getPriceList();
                TeamBuff buff = new TeamBuff(EBuffKey.联盟球馆赛, EBuffType.工资帽, new int[]{priceList[0]}, endTime, true);
                
                // 邮件参数
                String param = type[0] + "," + rank.getRank() + "," + priceList[0] + "," +
                               arenaRewardBean.getRewardProp().getNum() + "," + 
                               arenaRewardBean.getHonorProp().getNum();
                log.debug("quartzSendReward mail param{}", param);
                
                // 初始化可选联盟训练馆的球队
                LeagueTrain leagueTrain = trainManager.getLeagueTrainById(arenaRewardBean.getLeaTrainId());
                if(leagueTrain == null) {
                    log.error("quartzSendReward LeaTrainId {}", arenaRewardBean.getLeaTrainId());
                    return;
                }
                
                log.debug("quartzSendReward rank.getLeagueId(){}", rank.getLeagueId());
                leagueTrain.setLeagueId(rank.getLeagueId());
                leagueTrain.save();
                
                // 给联盟内球队发奖
                this.rankReward(teamList, buff, arenaRewardBean.getHonorProp().getNum(), param, arenaRewardBean.getReward(), arenaRewardBean.getLeaTrainId());
                
            });
        });        
        
        //清数据
        this.clearAll();
    }

    /**
     * 给联盟内所有球队发奖
     * @param teamList
     * @param buff
     * @param honor
     * @param param
     * @param reward
     */
    private void rankReward(Set<Long> teamList, TeamBuff buff, int honor, String param, String reward, int blId) {
    	final AtomicBoolean bool = new AtomicBoolean();
    	bool.set(true);
        teamList.stream().forEach(teamId -> {

            // 加联盟荣誉点
            int leagueId = leagueManager.getLeagueId(teamId);
            League league = leagueManager.getLeague(leagueId);
            if (bool.get()) {//荣誉值只是加一次
            	league.updateLeagueHonor(honor);
            	bool.set(false);
			}
            
            buffManager.addBuff(teamId, buff);

            // 发邮件
            teamEmailManager.sendEmail(teamId, 60003, param, reward);
            

            // 给联盟玩家增加训练馆
            for(long tid : teamList) {            
                trainManager.addLeagueTrain(tid, blId);
            } 

            // 清理球队比赛数据
            this.clearTeam(teamId);
            
            log.debug("rankReward teamId{}, send reward is over", teamId);
        });
    }
    
    /** 比赛结束 */
    private void endMatch0(BattleSource bs) {
        BattleTeam awayTeam = bs.getAway();
        BattleTeam homeTeam = bs.getHome();

        int posId = getPosIdByTeamId(awayTeam.getTeamId());
        PostionScoreBean postionScoreBean = LeagueArenaConsole.postionScoreMap.get(posId);
        if (postionScoreBean == null) {
            log.error("endMatch1 posId {} postionScoreBean is error", posId);
            localBattleManager.sendEndMain(bs, true);
            return;
        }

        int type = isCanJoinMatchType(awayTeam.getTeamId());
        LeagueAreanTeam awayATeam = this.getLeagueAreanTeam(awayTeam.getTeamId(), type);
        if (awayATeam.getTeamId() < 1 || awayATeam.getLeagueId() < 1) {
            log.error("endMatch1 teamId{}, LeagueAreanTeam data is error", awayATeam.redisString());
            localBattleManager.sendEndMain(bs, true);                                                                                      // return
            return;
        }

        LeagueAreanTeam homeATeam = this.getLeagueAreanTeam(homeTeam.getTeamId(), type);
        if (homeATeam.getLeagueId() < 1 || homeATeam.getTeamId() < 1) {
            log.error("endMatch1 LeagueAreanTeam data is error", homeATeam.redisString());
            localBattleManager.sendEndMain(bs, true);
            return;
        }
        
        localBattleManager.sendEndMain(bs, true);
        
        long winTeamId = 0;
        long failTeamId = 0;

        List<LeaguePostion> changeList = Lists.newArrayList();
        if (awayTeam.isWin()) {
            // 胜利获得积分
            awayATeam.setWinCount(awayATeam.getWinCount() + 1);
            awayATeam.setWinScore(awayATeam.getWinScore() + postionScoreBean.getWinScore());

            homeATeam.setFailCount(homeATeam.getFailCount() + 1);
            homeATeam.setWartime(System.currentTimeMillis() + ConfigConsole.getGlobal().leagueArenaChallengeCD * DateTimeUtil.MINUTE);

            winTeamId = awayATeam.getTeamId();
            failTeamId = homeATeam.getTeamId();
            
            LeaguePostion leaguePostion = this.getLeaguePostion(type, posId); 
            leaguePostion.setBattleId(0);
            changeList.add(leaguePostion);
            
            redis.hset(RedisKey.getLeaguePostionKey(type), String.valueOf(posId), leaguePostion.getLeagueId() + "_" + leaguePostion.getTeamId() + "_" + 0);
       
            // 失败后推送冷却时间
            leagueMatahTeamData.Builder builder = leagueMatahTeamData.newBuilder();
            builder.setPkTime(homeATeam.getWartime());
            builder.setTeamId(failTeamId);
            builder.setSpeedCount(teamNumManager.getUsedNum(failTeamId, TeamNumType.League_Speed_Num));
            sendMessage(failTeamId, builder.build(), ServiceCode.LeagueArenaManager_push_leagueMatchTeam);
        
        } else {
            // 失败者
            awayATeam.setFailCount(awayATeam.getFailCount() + 1);

            // 胜利获得积分
            homeATeam.setWinCount(homeATeam.getWinCount() + 1);
            homeATeam.setWinScore(homeATeam.getWinScore() + postionScoreBean.getWinScore());

            winTeamId = homeATeam.getTeamId();
            failTeamId = awayATeam.getTeamId();

            LeaguePostion leaguePostion = this.getLeaguePostion(type, posId);
            int leagueId = leagueManager.getLeagueId(winTeamId);
            leaguePostion.setLeagueId(leagueId);
            leaguePostion.setTeamId(winTeamId);
            leaguePostion.setBattleId(0);
            changeList.add(leaguePostion);
            
            redis.hset(RedisKey.getLeaguePostionKey(type), String.valueOf(posId), leagueId + "_" + winTeamId + "_" + 0);

            // 清理redis
            redis.del(RedisKey.getLeaguePostionTeamKey(failTeamId));
            redis.set(RedisKey.getLeaguePostionTeamKey(winTeamId), type + "_" + posId);
            

            // 推送所有联盟成员(占领,丢失位置)
            pushLeagueChallangeState(winTeamId, 1);
            pushLeagueChallangeState(failTeamId, 2);
        }     
        
        // 同步位置变化
        this.pushChangeLeaguePostionToALL(isCanJoinMatchType(homeTeam.getTeamId()), changeList);

        // 设置改变的值
        redis.hset(RedisKey.getLeagueArenaTeamKey(type), String.valueOf(awayATeam.getTeamId()), awayATeam.redisString());
        redis.hset(RedisKey.getLeagueArenaTeamKey(type), String.valueOf(homeATeam.getTeamId()), homeATeam.redisString());
    }

    /** 推送(占领或丢失)位置变化数据给所有联盟成员 */
    private void pushLeagueChallangeState(long teamId, int state) {
        int leagueId = leagueManager.getLeagueId(teamId);
        List<Long> teamIds = Lists.newArrayList(leagueManager.getLeagueTeamList(leagueId));
        Team team = teamManager.getTeam(teamId);
        LeaguePostionChallangeState.Builder winBuilder = LeaguePostionChallangeState.newBuilder();
        winBuilder.setState(state);
        winBuilder.setTeamName(team.getName());
        sendMessageTeamIds(teamIds, winBuilder.build(), ServiceCode.LeagueArenaManager_push_leaguePostionChallangeState);
    }

    /** 位置改变数据推送给所有赛区内的球队*/
    private void pushChangeLeaguePostionToALL(int type, List<LeaguePostion> changeList) {
        ChangeLeaguePostions.Builder builder = ChangeLeaguePostions.newBuilder();
        builder.setType(type);
        for (LeaguePostion lp : changeList) {
            builder.addPostions(builderLeaguePostionData(lp));
        }

        List<Long> teamIds = this.getLeagueArenaAllTeamIdsByType(type);
        sendMessageTeamIds(teamIds, builder.build(), ServiceCode.LeagueArenaManager_push_leaguepostions);
    }

    private int getPosIdByTeamId(long teamId) {
        String posStr = redis.getStr(RedisKey.getLeaguePostionTeamKey(teamId));
        if (posStr != null && !posStr.equals("")) {
            int[] strs = StringUtil.toIntArray(posStr, StringUtil.UNDERLINE);
            return strs[1];
        }

        return 0;
    }

    /**
     * 获取赛区内所有球队比赛信息
     * @param type
     * @return
     */
    private Map<Long, LeagueAreanTeam> getLeagueArenaTeamMap(int type) {
        Map<Long, String> map = redis.hgetallLKSV(RedisKey.getLeagueArenaTeamKey(type));
        Map<Long, LeagueAreanTeam> posMap = Maps.newConcurrentMap();
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            posMap.put(entry.getKey(), LeagueAreanTeam.toLeagueAreanTeam(entry.getValue()));
        }

        return posMap;
    }

    /**
     * 获取球队比赛信息
     * @param teamId
     * @param type
     * @return
     */
    private LeagueAreanTeam getLeagueAreanTeam(long teamId, int type) {
        LeagueAreanTeam leagueAreanTeam = getLeagueArenaTeamMap(type).get(teamId);
        if (leagueAreanTeam == null) {
            leagueAreanTeam = new LeagueAreanTeam();
            int leagueId = leagueManager.getLeagueId(teamId);
            leagueAreanTeam.setTeamId(teamId);
            leagueAreanTeam.setLeagueId(leagueId);
            redis.hset(RedisKey.getLeagueArenaTeamKey(type), String.valueOf(teamId), leagueAreanTeam.redisString());
        }

        return leagueAreanTeam;
    }

    /**
     * 获取位置信息
     * @param type
     * @param posId
     * @return
     */
    private LeaguePostion getLeaguePostion(int type, int posId) {

        LeaguePostion leaguePostion = this.getLeaguePostionMapByType(type).get(posId);
        if (leaguePostion == null) {
            redis.hset(RedisKey.getLeaguePostionKey(type), posId + "", "");
            return new LeaguePostion(posId, 0, 0, 0);
        }

        return leaguePostion;
    }

    /**
     *  获取赛区内所有位置信息
     * @param type
     * @return
     */
    private Map<Integer, LeaguePostion> getLeaguePostionMapByType(int type) {
        Map<Integer, String> map = Maps.newHashMap();

        map = redis.hgetallIKSV(RedisKey.getLeaguePostionKey(type));

        Map<Integer, LeaguePostion> posMap = Maps.newConcurrentMap();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {

            if (entry.getValue() == null || entry.getValue().equals("")) {
                posMap.put(entry.getKey(), new LeaguePostion(entry.getKey(), 0, 0, 0));
                continue;
            }

            long[] pos = StringUtil.toLongArray(entry.getValue(), StringUtil.UNDERLINE);
            posMap.put(entry.getKey(), new LeaguePostion(entry.getKey(), (int) pos[0], pos[1], pos[2]));
        }

        return posMap;
    }

    /**
     *  获取赛区内所有可参赛的球队ID
     * @param type
     * @return
     */
    private List<Long> getLeagueArenaAllTeamIdsByType(int type) {
        List<Long> teamIds = Lists.newArrayList();
        
        List<Integer> leagueIds = redis.getList(RedisKey.getLeagueArenaIds(type));
        for (int leagueId : leagueIds) {
            // 联盟成员总和
            teamIds.addAll(Lists.newArrayList(leagueManager.getLeagueTeamList(leagueId)));
        }

        return teamIds;
    }

    /**
     * 获取联盟球馆赛比赛ID
     * @param teamId
     * @param battleType
     * @return
     */
    private long getBattleId(long teamId, EBattleType battleType) {
        TeamStatus ts = teamStatusManager.getTeamStatus(teamId);
        if (ts != null) {
            TeamBattleStatus tbs = ts.getBattle(battleType);
            if (tbs != null) {
                boolean inbattle = tbs.inBattle();
                if (inbattle) {
                    return tbs.getBattleId();
                }
            }
        }

        return 0;
    }    
    
    /** 联盟球馆赛赛季结束排行奖励 */
    private void endRankRewardTask() {
        // 当前时间到比赛开始时间相差几天    
        int day = Math.abs(LeagueArenaConsole.OpenDayOfWeek - DateTimeUtil.getCurrWeekDay());
        log.info("当前时间到比赛开始时间相差几天  day {}", day);
       
        long awardTime = ConfigConsole.global().leagueSendRewardtime;
        long midnight = DateTimeUtil.midnight();
        long curr = System.currentTimeMillis();
        long delay = day*DateTimeUtil.DAILY + midnight + awardTime - curr;
        
        log.info("发奖时间 {}", curr + delay);
        
        // 超过发奖时间就发奖
        if (delay > 0) {
            ScheduledFuture<?> sf = QuartzServer.scheduleAtFixedRate(this::quartzSendReward,
                    delay, DateTimeUtil.DAILY * 7, TimeUnit.MILLISECONDS);
            
            log.info("排行奖励离下次时间 {}", DateTimeUtil.duration(sf));
        }     
    }
    
    /** 清理所有联盟相关的数据*/
    public void gmClearAll() {
        types.forEach(type -> {
            redis.del(RedisKey.getLeagueArenaIds(type[0]));
           
            // 比赛结束清理
            redis.del(RedisKey.getLeaguePostionKey(type[0]));
            redis.del(RedisKey.getLeagueArenaTeamKey(type[0]));
            redis.del(RedisKey.getLeagueMatchTeamRankKey(type[0]));
            redis.del(RedisKey.getLeagueHistoryRankKey(type[0]));
            
          List<Long> teamList = this.getLeagueArenaAllTeamIdsByType(type[0]);
          teamList.stream().forEach(teamId -> {
              // 清理球队比赛数据
              this.clearTeam(teamId);
          });
        });
        
        redis.del(RedisKey.League_Week_Score_Sum);
        redis.del(RedisKey.League_Arena_Ids_Refresh_State);
        if(!rankMap.isEmpty()) {
            rankMap.clear();
        }
    }  
 
    public List<Integer[]> getTypes() {
        return types;
    }

    public void setTypes(List<Integer[]> types) {
        LeagueArenaManager.types = types;
    }
    
    /** 获取球队可占领的联盟训练馆基础Id*/
    public int getCanChioseBlId(int type, int rank) {
        Map<Integer, AreanRewardBean> rankRewardMap = LeagueArenaConsole.getMatchRewardMap().get(type);
        if (rankRewardMap == null) {
            log.error("config is error, quartzSendReward rankRewardMap is null, type{}", type);
            return 0;
        }
        
        AreanRewardBean arenaRewardBean = rankRewardMap.get(rank);
        if (arenaRewardBean == null) {
            log.error("config is error, quartzSendReward rank{}, arenaRewardBean data is null", rank);
            return 0;
        }
        return arenaRewardBean.getLeaTrainId();
    }   
    
    /**
     * 球队不在联盟中(被踢或者主动退出联盟),如果联盟在参加联盟训练馆占位赛,
     * 则需要把该球队的数据移除(赛区内球队比赛数据,赛区内球队占位数据).
     * @param leagueId	联盟Id
     * @param teamId	球队Id
     */
    public void clearLeagueArenaTeamData(int leagueId, long teamId){
    	if (!LeagueArenaConsole.isPKingTime(new DateTime())) {
            return;
        }
    	
    	boolean bool = false;
    	int flagType = 0;
    	for (Integer[] type : types) {
    		List<Integer> leagueIds = redis.getList(RedisKey.getLeagueArenaIds(type[0]));
    		// 判断联盟是否有资格参赛
			if (leagueIds.contains(leagueId)) {
				bool = true;
				flagType = type[0];
				break;
			}
		}
    	
    	if (!bool) {
			return;
		}
    	
    	int lastPosId = getPosIdByTeamId(teamId);
    	if (lastPosId != 0) {
			this.resetPostion(flagType, lastPosId);
		}
    	
    	// 清除,赛区内球队比赛数据
    	redis.hdel(RedisKey.getLeagueArenaTeamKey(flagType), String.valueOf(teamId));
    	// 清除,赛区内球队占位数据
    	this.clearTeam(teamId);

    }
}
