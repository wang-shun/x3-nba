package com.ftkj.manager.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.StarletConsole;
import com.ftkj.db.ao.logic.IStarletAO;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TeamDayNumType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.handle.BattleCommon;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.StarletMatchEnd;
import com.ftkj.manager.battle.model.PlayerActStat;
import com.ftkj.manager.logic.LocalBattleManager.BattleContxt;
import com.ftkj.manager.logic.TeamDayStatsManager.TeamNums;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.starlet.DualMeetBean;
import com.ftkj.manager.starlet.DualMeetRadixBean;
import com.ftkj.manager.starlet.StarletDualMeet;
import com.ftkj.manager.starlet.StarletInfo;
import com.ftkj.manager.starlet.StarletPlayer;
import com.ftkj.manager.starlet.StarletRank;
import com.ftkj.manager.starlet.StarletRankAwardBean;
import com.ftkj.manager.starlet.StarletTeamDualMeet;
import com.ftkj.manager.starlet.StarletTeamRedix;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.BattlePB.BattlePlayerStatResp;
import com.ftkj.proto.BattlePB.TeamRedix;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.StarletPb.PlayerDualMeetData;
import com.ftkj.proto.StarletPb.RankMatchData;
import com.ftkj.proto.StarletPb.StarletDualMeetData;
import com.ftkj.proto.StarletPb.StarletInfoData;
import com.ftkj.proto.StarletPb.StarletRankData;
import com.ftkj.proto.StarletPb.StarletRankInfoList;
import com.ftkj.proto.StarletPb.StarletRankList;
import com.ftkj.proto.StarletPb.StarletTeam;
import com.ftkj.proto.StarletPb.StarletTeamData;
import com.ftkj.proto.StarletPb.StarletTeamRedixData;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.IDUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * 新秀对抗赛
 * 新秀排位赛
 * @author qin.jiang
 *
 */
@ActiveAnno(redType=ERedType.活动, atv = EAtv.新秀活动_竞技赛)
public class StarletManager extends ActiveBaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(StarletManager.class);
    @IOC
    private TeamManager teamManager;
    @IOC
    private LeagueManager leagueManager;  
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamCapManager teamCapManager; 
    @IOC
    private TeamNumManager teamNumManager; 
    @IOC
    private TeamDayStatsManager teamDayStatsManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private LocalBattleManager localBattleManager;
    @IOC
    private IStarletAO starletAO;
    
    /** 球队比赛缓存对抗赛数据 */
    private Map<Long, StarletTeamDualMeet> starletTeamDualMeetMap = Maps.newConcurrentMap();
    /** 球队球员对抗赛数据 */
    private Map<Long, Map<Integer, StarletDualMeet>> starletDualMeetMap;
    /** 新秀阵容数据 */
    private Map<Long, Map<Integer, StarletPlayer>> starletPlayerMap;
    /** 新秀排行缓存数据 */
    private Map<Integer, StarletRank> starletRankMap;
    /** 球队对抗赛基数数据 */
    private Map<Long,  Map<Integer, StarletTeamRedix>> starletTeamRedixMap;
    /** 比赛结束处理 */
    private BattleEnd battleEnd = this::endMatch0; 
    /** 操作模块 */
    private ModuleLog mc = ModuleLog.getModuleLog(EModuleCode.Starlet, "Starlet");
    
    @Override
    public void instanceAfter() {
        super.instanceAfter();
        starletRankMap = initStarletRankMap();
        starletDualMeetMap = Maps.newConcurrentMap();
        starletPlayerMap = Maps.newConcurrentMap();
        starletTeamRedixMap = Maps.newConcurrentMap();
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_getDualMeetData)
    public void getDualMeetData() {
        long tid = getTeamId();
        ErrorCode ret = getDualMeetData0(tid);
        sendMsg(ret);   
        
        log.trace("sm getDualMeetData tid{}, ret{}", tid, ret);
    }
    
    private ErrorCode getDualMeetData0(long tid) {       
        int useNum = teamDayStatsManager.getNums(tid, TeamDayNumType.Dual_Meet_Use_Num);
        int buyNum = teamNumManager.getUsedNum(tid, TeamNumType.Dual_Meet_Buy_Num);
        
        TeamPlayer teamPlayer = playerManager.getTeamPlayer(tid);
        
        StarletDualMeetData.Builder builder = StarletDualMeetData.newBuilder();
        builder.setUseNum(useNum);
        builder.setBuyNum(buyNum);        
     
        for(Integer prid : teamPlayer.getNPridsAndStorage()) {
            StarletDualMeet starletDualMeet = this.getStarletDualByPRid(tid, prid);
            if(starletDualMeet == null || starletDualMeet.getTotal() < 1) continue;
            builder.addDualmeet(builderPlayerDualMeetData(starletDualMeet));
        }

        sendMessage(tid, builder.build(), ServiceCode.StarletManager_push_getDualMeetData);
        return ErrorCode.Success;
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_getDualMeetType)
    public void getDualMeetType() {
        long tid = getTeamId();
        if(!isInBattle(tid)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Starlet_Not_In_Battle.code).build()); 
            return;
        }
        
        long bid = this.getBattleId(tid, EBattleType.Starlet_Dual_Meet);
        StarletTeamDualMeet starletTeamDualMeet = this.getStarletTeamDualMeel(tid);
        if(starletTeamDualMeet.getBattleId() != bid) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Starlet_Not_In_Battle.code).build()); 
            return;
        }
        
        sendMessage(DefaultPB.DefaultData.newBuilder().setBigNum(starletTeamDualMeet.getType()).setCode(ErrorCode.Success.code).build());  
        log.trace("sm getDualMeetType tid{}, type{}, ret.code{}", tid, starletTeamDualMeet.getType(), ErrorCode.Success.code);
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_dualMeetChallange)
    public void dualMeetChallange(int type) {
        long tid = getTeamId();
        ErrorCode ret = dualMeetChallange0(tid, type);
        sendMsg(ret);  
        
        log.trace("sm dualMeetChallange tid{}, type{}, ret{}", tid, type, ret);
    }
    
    private ErrorCode dualMeetChallange0(long tid, int type) {
        if(getStatus() != EActiveStatus.进行中) {
            return ErrorCode.Starlet_Not_Start_Match;
        }
        
        TeamPlayer teamPlayer = playerManager.getTeamPlayer(tid);
        if(teamPlayer.getNPrids().isEmpty()) {
            return ErrorCode.Starlet_Not_Have;
        }
        
        if(isInBattle(tid)) {
            return ErrorCode.Starlet_In_Battle;
        }
        
        int useNum = teamDayStatsManager.getNums(tid, TeamDayNumType.Dual_Meet_Use_Num);
        int buyNum = teamNumManager.getUsedNum(tid, TeamNumType.Dual_Meet_Buy_Num);
        if(useNum >= buyNum + ConfigConsole.getGlobal().dualMeetInitCount) {
            return ErrorCode.Starlet_Count_Not_Enough;
         }
        
        TeamNums tn = teamDayStatsManager.getNums(tid);
        teamDayStatsManager.saveTeamNums(tn, TeamDayNumType.Dual_Meet_Use_Num, useNum + 1);
        
        DualMeetBean dualMeetBean = StarletConsole.getDualMeetBeanMap().get(type);
        if(dualMeetBean == null) {
            log.error("starlet DualMeetBean type{} config error", type);
            return ErrorCode.Error;
        }
        
        NPCBean npcBean =  NPCConsole.getNPC(dualMeetBean.getInitNpcTeam());
        if(npcBean == null) {
            log.error("starlet npcBean npcTeamId{} config error", dualMeetBean.getInitNpcTeam());
            return ErrorCode.Error;
        }
        
        // 加载比赛
        EBattleType bt = EBattleType.Starlet_Dual_Meet;
        BattleSource bs = localBattleManager.buildBattle(bt, tid, npcBean.getNpcId(), null, null, tid);
        BattleContxt bc = localBattleManager.defaultContext(battleEnd);
        bc.setStartPush(this::startPush);
        BaseBattleHandle bb = new BattleCommon(bs);
        localBattleManager.initBattleWithContext(bb, bs, bc);
        localBattleManager.start(bs, bc, bb);        
        
        StarletTeamDualMeet starletTeamDualMeet = this.getStarletTeamDualMeel(tid);
        starletTeamDualMeet.setBattleId(bs.getBattleId());
        starletTeamDualMeet.setType(type);
        
        return ErrorCode.Success;
    }  
    
    @ClientMethod(code = ServiceCode.StarletManager_rankMatchTeamData)
    public void rankMatchTeamData(long tid) {
        ErrorCode ret = rankMatchTeamData0(tid);
        sendMsg(ret);  
        log.trace("sm rankMatchTeamData tid{}, ret{}", tid, ret);
    }
    
    private ErrorCode rankMatchTeamData0(long tid) {
        Map<Integer, StarletPlayer> map = this.getStarletPlayerMap(tid);  
        StarletTeam.Builder builder = StarletTeam.newBuilder();
        builder.setTeamId(tid);
        for(Map.Entry<Integer, StarletPlayer> entry : map.entrySet()) {
            builder.addData(builderStarletTeamData(entry.getValue()));
        }
   
        sendMessage(getTeamId(), builder.build(), ServiceCode.StarletManager_push_rankMatchTeamData);
        return ErrorCode.Success;
    }
 
    @ClientMethod(code = ServiceCode.StarletManager_dualMeetBuyNum)
    public void dualMeetBuyNum() {
        long tid = getTeamId();
        ErrorCode ret = dualMeetBuyNum0(tid, 1);
        sendMsg(ret);
        
        log.trace("sm dualMeetBuyNum tid{}, ret{}", tid, ret);
    }
    
    private ErrorCode dualMeetBuyNum0(long tid, int num) {
        ErrorCode ret = teamNumManager.consumeNumCurrency(tid, TeamNumType.Dual_Meet_Buy_Num, num, mc);
        if (ret.isError()) {
            return ret;
        }
        return ErrorCode.Success;
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_rankMatchBuyNum)
    public void rankMatchBuyNum() {
        long tid = getTeamId();
        ErrorCode ret = rankMatchBuyNum0(tid, 1);
        sendMsg(ret);
        
        log.trace("sm rankMatchBuyNum tid{}, ret{}", tid, ret);
    }
    
    private ErrorCode rankMatchBuyNum0(long tid, int num) {
        ErrorCode ret = teamNumManager.consumeNumCurrency(tid, TeamNumType.Dual_Meet_Rank_Buy_Num, num, mc);
        if (ret.isError()) {
            return ret;
        }
        return ErrorCode.Success;
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_rankMatchTeamSet)
    public void rankMatchTeamSet(String pos) {
        long tid = getTeamId();
        ErrorCode ret = rankMatchTeamSet0(tid, pos);
        sendMsg(ret);
        
        log.debug("sm rankMatchTeamSet0 tid{}, pos{}, ret{}", tid, pos, ret);
    }
    
    private ErrorCode rankMatchTeamSet0(long tid, String pos) {
        Integer[] prids = StringUtil.toIntegerArray(pos);
        TeamPlayer teamPlayer = playerManager.getTeamPlayer(tid);
        
        for(int idx = 0; idx < prids.length; idx++){
            Integer prid = prids[idx];
            Player player = teamPlayer.getPlayer(prid);
            if(player == null) {
                log.error("rankMatchTeamSet0 player is null, prid{}", prid); 
                continue;
            }
            
            StarletDualMeet starletDualMeet = this.getStarletDualByPRid(tid, prid);
            int cap = this.calculCapByPRid(starletDualMeet);
            if(cap < 1) {               
                continue;
            }
            
            if(!teamPlayer.getNPrids().contains(player.getPlayerRid())) continue;            
            
            StarletPlayer starletPlayer = this.getStarletPlayerByPos(tid, EPlayerPosition.getEPlayerPosition(idx+1));
            if(starletPlayer == null) {
                starletPlayer = new StarletPlayer();
                starletPlayer.setTeamId(tid);
                starletPlayer.setLineupPosition(EPlayerPosition.getEPlayerPosition(idx+1).name());
                
                this.getStarletPlayerMap(tid).put(prid, starletPlayer);
            }          
          
            starletPlayer.setPlayerRid(prid);          
            
            // 某个球员的对抗赛次数           
            starletPlayer.setCap(cap); 
            starletPlayer.save();
            
            log.trace("sm rankMatchTeamSet0 tid{}, pid{}, cap{}, pos{}", cap, tid, player.getPid(), starletPlayer.getLineupPosition());
        }
        
        // 设置球员排位赛数据
        StarletRank starletRank = this.getStarletRankByTeamId(tid);
        if(starletRank == null) {
            starletRank = new StarletRank();
            starletRank.setTeamId(tid);
            
            Map<Integer, StarletRank> rankMap = getStarletRankMap();
            int rank = rankMap.size()+1;
            starletRank.setRank(rank);
            starletRank.save();
            
            rankMap.put(rank, starletRank);
            
            log.trace("sm rankMatchTeamSet0 tid{}, rank{}", tid, rank);
        }
       
        return rankMatchTeamData0(tid);       
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_rankMatchGetData)
    public void rankMatchGetData() {
        long tid = getTeamId();
        ErrorCode ret = rankMatchGetData0(tid);
        sendMsg(ret);
        
        log.trace("sm rankMatchGetData tid{}, ret{}", tid, ret);
    }
 
    private ErrorCode rankMatchGetData0(long tid) {
        if(this.getStarletPlayerMap(tid).isEmpty()) {
           return ErrorCode.Starlet_Player_Is_Null;
        }
        
        StarletRank starletRank = this.getStarletRankByTeamId(tid);
        if(starletRank == null) {
            return ErrorCode.Starlet_Rank_Data_Is_Null;
        }
            
        int useNum = teamDayStatsManager.getNums(tid, TeamDayNumType.Dual_Meet_Rank_Use_Num);
        int buyNum = teamNumManager.getUsedNum(tid, TeamNumType.Dual_Meet_Rank_Buy_Num);
        
        RankMatchData.Builder builder = RankMatchData.newBuilder();
        builder.setBuyNum(buyNum);
        builder.setUseNum(useNum);
        
        List<StarletRank> list = this.getStarletRankList(starletRank.getRank());
        for(StarletRank rank : list) {
            builder.addRanks(builderStarletRankData(rank));
        }
        
        log.trace("sm rankMatchGetData0 tid{}, buyNum{}, useNum{}", buyNum, useNum);        
        sendMessage(tid, builder.build(), ServiceCode.StarletManager_push_rankMatchGetData);
        return ErrorCode.Success;
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_getStarletRankInfoList)
    public void getStarletRankInfoList() {
        long tid = getTeamId();
        ErrorCode ret = getStarletRankInfoList0(tid);
        sendMsg(ret);
        
        log.trace("sm getStarletRankInfoList tid{}, ret{} ", tid, ret);
    }
    
    private ErrorCode getStarletRankInfoList0(long tid) {
        List<StarletInfo> starletInfoList = getStarletInfoList(tid);
        
        StarletRankInfoList.Builder srbuilder = StarletRankInfoList.newBuilder();
        for (StarletInfo ti : starletInfoList) {
            srbuilder.addInfoList(builderStarletInfoData(ti));
        }
        
        sendMessage(tid, srbuilder.build(), ServiceCode.StarletManager_push_getStarletRankInfoList);
        
        return ErrorCode.Success;
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_rankMatchCallange)
    public void rankMatchCallange(int rank) {
        if(getStatus() != EActiveStatus.进行中) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Starlet_Not_Start_Match.code).build());
            return;
        }
        
        long tid = getTeamId();
        // 获取球队自己的排行数据
        StarletRank selfRank = this.getStarletRankByTeamId(tid);
        if(selfRank == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Starlet_Is_Null_Have_Rank.code).build());
            return;
        }
        
        StarletRank otherRank = getStarletRankByRank(rank);
        if(otherRank == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Starlet_Rank_Data_Is_Null.code).build());
            return;
        }
        
        // 获取球队的新秀总攻防
        long ttid = otherRank.getTeamId();
        int otherCap = this.getStarletTeamCap(ttid);
        
        int selfCap = this.getStarletTeamCap(tid);   
        if(selfCap < 1) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Starlet_Not_Have_Cap.code).build());
            return;
        }
        
        int useNum = teamDayStatsManager.getNums(tid, TeamDayNumType.Dual_Meet_Rank_Use_Num);
        int buyNum = teamNumManager.getUsedNum(tid, TeamNumType.Dual_Meet_Rank_Buy_Num);
        if(useNum >= buyNum + ConfigConsole.getGlobal().dualMeetRankInitCount) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Starlet_Rank_Count_Not_Enough.code).build());
            return;
        }
        
        TeamNums tn = teamDayStatsManager.getNums(tid);
        teamDayStatsManager.saveTeamNums(tn, TeamDayNumType.Dual_Meet_Rank_Use_Num, useNum + 1);
        
        // 高排名玩家挑战低排名玩家 (0:失败, 1:成功)
        if(selfRank.getRank() < otherRank.getRank()) {
            int state = 2;
            if(selfCap > otherCap) {
                state = 1;
                
                // 给自己添加记录(0:挑战,1:被挑战)
                this.addStarletInfo(tid, ttid, 0, 1, 0);
                // 给目標玩家添加记录
                this.addStarletInfo(ttid, tid, 1, 0, 0);
            }else {
                // 给自己添加记录
                this.addStarletInfo(tid, ttid, 0, 0, 0);
                // 给目標玩家添加记录
                this.addStarletInfo(ttid, tid, 1, 1, 0);
            }            
            
            sendMessage(DefaultPB.DefaultData.newBuilder().setBigNum(state).setCode(ErrorCode.Success.code).build());
            return;
        }
        
        if(selfCap > otherCap) {
            otherRank.setTeamId(tid);
            otherRank.save();
            
            selfRank.setTeamId(ttid);
            selfRank.save();            
            
            sendMessage(DefaultPB.DefaultData.newBuilder().setBigNum(1).setCode(ErrorCode.Success.code).build());
            log.trace("sm rankMatchCallange tid{}, state{} ", tid, 2);
            
            StarletRankList.Builder builder = StarletRankList.newBuilder();
            builder.addRanks(builderStarletRankData(otherRank));
            builder.addRanks(builderStarletRankData(selfRank));
            sendMessage(tid, builder.build(), ServiceCode.StarletManager_push_rankMatchChengeRanks);            
            
            // 给自己添加记录
            this.addStarletInfo(tid, ttid, 0, 1, otherRank.getRank());
            // 给目標玩家添加记录
            this.addStarletInfo(ttid, tid, 1, 0, selfRank.getRank());            
           
        }else {
           sendMessage(DefaultPB.DefaultData.newBuilder().setBigNum(2).setCode(ErrorCode.Success.code).build());  
           log.trace("sm rankMatchCallange tid{}, state{} ", tid, 1);
           
           // 给自己添加记录
           this.addStarletInfo(tid, ttid, 0, 0, 0);
           // 给目标添加记录
           this.addStarletInfo(ttid, tid, 1, 1, 0);
        }     
    }     
    
    @ClientMethod(code = ServiceCode.StarletManager_rankMatchRanks)
    public void rankMatchRanks() {
        long tid = getTeamId();
        ErrorCode ret = rankMatchRanks0(tid);
        sendMsg(ret);
        
        log.trace("sm rankMatchRanks tid{}, ret{} ", tid, ret);
    }
    
    private ErrorCode rankMatchRanks0(long tid) {
        if(this.getStarletPlayerMap(tid).isEmpty()) {
            return ErrorCode.Starlet_Player_Is_Null;
         }
        
        StarletRankList.Builder builder = StarletRankList.newBuilder();
        
        List<StarletRank> list = this.getStarletRankList(1);
        for(StarletRank starletRank : list) {
            builder.addRanks(builderStarletRankData(starletRank));
        }
        
        sendMessage(tid, builder.build(), ServiceCode.StarletManager_push_rankMatchRanks);
        return ErrorCode.Success;
    }
    
    @ClientMethod(code = ServiceCode.StarletManager_getStarletTeamRedix)
    public void getStarletTeamRedix() {
        long tid = getTeamId();
        ErrorCode ret = getStarletTeamRedix0(tid);
        sendMsg(ret);   
        
        log.trace("sm getStarletTeamRedix tid{}, ret{}", tid, ret);
    }

    private ErrorCode getStarletTeamRedix0(long tid) {
        
        Map<Integer, StarletTeamRedix> map = this.getStarletTeamRedixMap(tid);
        
        StarletTeamRedixData.Builder builder = StarletTeamRedixData.newBuilder();
        for(Map.Entry<Integer, StarletTeamRedix> entry : map.entrySet()) {
            StarletTeamRedix redix = entry.getValue();
            builder.addRedix(TeamRedix.newBuilder().setCardType(redix.getCardType()).setRedixNum(redix.getRedixNum()));
        }
     
        sendMessage(tid, builder.build(), ServiceCode.StarletManager_push_getStarletTeamRedix);
        return ErrorCode.Success;
    }

    /**
     * @param 发起队伍
     * @param 目标队伍ID
     * @param 挑战状态
     * @param 下降名次
     */
    private void addStarletInfo(long tid, long ttid, int cState, int state, int num) {

        List<StarletInfo> changeList = Lists.newArrayList();
        List<StarletInfo> starletInfoList = getStarletInfoList(tid);
        StarletInfo starletInfo = null;
        if (starletInfoList.size() >= ConfigConsole.global().starletRankCallangeInfoCount) {
            starletInfo = starletInfoList.remove(0);
            changeList.add(starletInfo);
        } else {
            starletInfo = new StarletInfo();
        }

        Team team = teamManager.getTeam(ttid);
        starletInfo.setMsgId(IDUtil.geneteId(StarletInfo.class));
        starletInfo.setcState(cState);
        starletInfo.setTeamName(team.getName());
        starletInfo.setNum(num);
        starletInfo.setState(state);
        starletInfo.setCreateTime(System.currentTimeMillis());       
        starletInfoList.add(starletInfo);
        changeList.add(starletInfo);

        redis.rpush(RedisKey.getStarletInfoListKey(tid), starletInfoList, RedisKey.MONTH);
    }
 
    
    private StarletInfoData.Builder builderStarletInfoData(StarletInfo starletInfo){
        
        StarletInfoData.Builder builder = StarletInfoData.newBuilder();
        builder.setNum(starletInfo.getNum());
        builder.setCState(starletInfo.getcState());
        builder.setState(starletInfo.getState());
        builder.setTeamName(starletInfo.getTeamName());
        builder.setCreateTime(starletInfo.getCreateTime());
        
        log.trace("sm builderStarletInfoData num{}, state{}, teamName{}, createTime{}", builder.getNum(), builder.getState(), builder.getTeamName(), builder.getCreateTime());
        
        return builder;
    }
    
    /** 获取挑战记录列表 */
    private List<StarletInfo> getStarletInfoList(long teamId) {
        List<StarletInfo> starletInfoList = redis.getList(RedisKey.getStarletInfoListKey(teamId));
        if (starletInfoList == null) {
            starletInfoList = Lists.newArrayList();
            redis.rpush(RedisKey.getStarletInfoListKey(teamId), starletInfoList);
        }

        return starletInfoList;
    }

    /** 是否在比赛中 */
    private boolean isInBattle(long tid) {
        return TeamStatus.inBattle(teamStatusManager.getTeamStatus(tid), EBattleType.Starlet_Dual_Meet);
    }    
    
    /** 获取所有球队对抗赛基数数据*/
    private StarletTeamRedix getStarletTeamRedixByType(long tid, int cardType) {
        Map<Integer, StarletTeamRedix> map = getStarletTeamRedixMap(tid);
        
        StarletTeamRedix starletTeamRedix = map.get(cardType);
        if(starletTeamRedix == null) {
            starletTeamRedix = new StarletTeamRedix();
            
            starletTeamRedix.setTeamId(tid);
            starletTeamRedix.setCardType(cardType);
            starletTeamRedix.save();
            
            map.put(cardType, starletTeamRedix);
        }
        
        return starletTeamRedix;
    }    
    
    /** 获取所有球队对抗赛基数数据*/
    public Map<Integer, Integer> getStarletTeamRedixValueMap(long tid) {
        Map<Integer, StarletTeamRedix> map = getStarletTeamRedixMap(tid);
        return map.values().stream().collect(Collectors.toMap(StarletTeamRedix::getCardType, StarletTeamRedix::getRedixNum));
    }  
    
    /** 获取所有球队对抗赛基数数据*/
    private Map<Integer, StarletTeamRedix> getStarletTeamRedixMap(long tid) {
        Map<Integer, StarletTeamRedix> map = starletTeamRedixMap.get(tid);
        if(map == null) {
            map = Maps.newConcurrentMap(); 
            starletTeamRedixMap.put(tid, map);
            
            List<StarletTeamRedix> list = starletAO.getStarletTeamRedix(tid);
            for(StarletTeamRedix starletTeamRedix : list) {
                map.put(starletTeamRedix.getCardType(), starletTeamRedix);
            }    
        }     
        
        return map;
    }

    /** 获取球队对抗赛*/
    private StarletTeamDualMeet getStarletTeamDualMeel(long tid) {        
        StarletTeamDualMeet starletTeamDualMeet = starletTeamDualMeetMap.get(tid);
        if(starletTeamDualMeet == null) {
            starletTeamDualMeet = new StarletTeamDualMeet();
            starletTeamDualMeet.setTeamId(tid);    
            
            starletTeamDualMeetMap.put(tid, starletTeamDualMeet); 
        }
        
        return starletTeamDualMeet;
    }

    /** 初始化排位赛排行榜*/
    private Map<Integer, StarletRank> initStarletRankMap() {
        List<StarletRank> list = starletAO.getAllStarletRank();
        if(starletRankMap == null) {
            starletRankMap = Maps.newConcurrentMap(); 
            
            for(StarletRank starletRank : list) {
                starletRankMap.put(starletRank.getRank(), starletRank);
            }
        }
        
        return starletRankMap;
    }
        
    /** 获取排位赛排行榜*/
    private Map<Integer, StarletRank> getStarletRankMap() {
        
        return starletRankMap;
    }
    
    /** 获取某个排名数据*/
    private StarletRank getStarletRankByRank(int rank) {
        
        return getStarletRankMap().get(rank);
    }
    
    /** 获取自己的排名数据*/
    private StarletRank getStarletRankByTeamId(long tid) {       
        return getStarletRankMap().values().stream().filter(s -> s.getTeamId() == tid).findFirst().orElse(null);
    }
 
    /** 获取新秀阵容单个玩家*/
    private StarletPlayer getStarletPlayer(long tid, int prid) {
        Map<Integer, StarletPlayer> map = this.getStarletPlayerMap(tid);        
        return  map.get(prid);
    }
    
    /** 获取阵容所有玩家*/
    private Map<Integer, StarletPlayer> getStarletPlayerMap(long tid) {
        Map<Integer, StarletPlayer> map = starletPlayerMap.get(tid);
        if(map == null) {
            
            map = Maps.newConcurrentMap(); 
            starletPlayerMap.put(tid, map);
            
            List<StarletPlayer> list = starletAO.getStarletPlayer(tid);
            for(StarletPlayer starletPlayer : list) {
                map.put(starletPlayer.getPlayerRid(), starletPlayer);
            }            
        }
        
        return map;
    }
    
    /** 根据阵容位置获取玩家*/
    private StarletPlayer getStarletPlayerByPos(long tid, EPlayerPosition position) {
        Map<Integer, StarletPlayer> map = this.getStarletPlayerMap(tid);
        if(map.isEmpty()) {          
            return null;
        }
        
        for(Map.Entry<Integer, StarletPlayer> entry : map.entrySet()) {
            StarletPlayer starletPlayer = entry.getValue();
            if(starletPlayer.getLineupPosition().equals(position.name())) return starletPlayer;
        }
        
        return null;          
    }
    
    /** 获取球队对抗赛数据*/
    private Map<Integer, StarletDualMeet> getStarletDualMeetMapByTeamId(long tid) {
        
        Map<Integer, StarletDualMeet> map = starletDualMeetMap.get(tid);
        if(map == null) {
            map = Maps.newConcurrentMap(); 
            starletDualMeetMap.put(tid, map);
            
            
            List<StarletDualMeet> list = starletAO.getStarletDualMeetByTeamId(tid);
            if(list != null) {
               for(StarletDualMeet starletDualMeet : list) {
                   map.put(starletDualMeet.getPlayerRid(), starletDualMeet);
               }  
            }
         }
        
        return map;
    }
    
    /** 获取球队球员对抗赛数据*/
    private StarletDualMeet getStarletDualByPRid(long tid, int prid) {
        
        Map<Integer, StarletDualMeet> map = getStarletDualMeetMapByTeamId(tid);
        if(map == null) return null;

        StarletDualMeet starletDualMeet = map.get(prid);
        if(starletDualMeet == null) {
            starletDualMeet = new StarletDualMeet();
            starletDualMeet.setTeamId(tid);
            starletDualMeet.setPlayerRid(prid);
            map.put(prid, starletDualMeet);
        }
        return starletDualMeet;
    }   
 
  

    /** 获取新秀阵容攻防最大的球员*/
    private StarletPlayer getStarletPlayerByMaxCap(long tid) {
        Map<Integer, StarletPlayer> spMap =  getStarletPlayerMap(tid);
        if(!spMap.isEmpty()) {
          
            List<StarletPlayer> list =  spMap.values().stream().collect(Collectors.toList());
            list.sort(Comparator.comparing(StarletPlayer::getCap));
            
            return list.get(list.size() - 1);
        }
        
        return null;
    }

    /** 获取新秀阵容总攻防 */
    private int getStarletTeamCap(long tid) {
        Map<Integer, StarletPlayer> spMap =  getStarletPlayerMap(tid);
        if(spMap.isEmpty()) return 0;
        List<StarletPlayer> list = spMap.values().stream().collect(Collectors.toList());
        
        return list.stream().mapToInt(StarletPlayer::getCap).sum();
    }
    
    
    /** 比赛结束 */
    private void endMatch0(BattleSource bs) {
        BattleTeam homeTeam = bs.getHome();
        BattleTeam awayTeam = bs.getAway();        
      
        if(homeTeam.isWin()){            
            int type = this.getTypeByNpcTeamId(awayTeam.getTeamId());
            DualMeetBean dualMeetBean = StarletConsole.getDualMeetBeanMap().get(type);
            if(dualMeetBean == null) {
                log.warn("endMatch0 DualMeetBean npcTeamId{} type{} config error", type, awayTeam.getTeamId());  
                localBattleManager.sendEndMain(bs, true);
            }
            
            Integer[] types = dualMeetBean.getRadixList();
            StarletTeamRedix starletTeamRedix = this.getStarletTeamRedixByType(homeTeam.getTeamId(), types[1] );
            starletTeamRedix.setRedixNum(Math.min(starletTeamRedix.getRedixNum() + types[0], types[2]));
            starletTeamRedix.save();
            
            final EndReport report = bs.getEndReport();
            report.addAdditional(EBattleAttribute.Starlet_Match_End,
                    new StarletMatchEnd( types[1],types[0]));
          
            log.trace("endMatch0 TeamRedix.Builder teamId{}, CardType{}, RedixNum{}", types[0], types[1]);
        }
        
        localBattleManager.sendEndMain(bs, true);
        
        // 统计数值计算战力
        TeamPlayer teamPlayer = playerManager.getTeamPlayer(homeTeam.getTeamId());
        for(BattlePlayer bp : bs.getHome().getPlayers()) {
            PlayerActStat pks = bp.getRealTimeActionStats();
            if(!teamPlayer.isNPlayer(bp.getPlayerId())) continue;
            
            log.trace("BattlePlayer teamId{}, pid{}", homeTeam.getTeamId(), bp.getPid() );
            
            StarletDualMeet starletDualMeet = this.updataStarletDual(homeTeam.getTeamId(), bp.getRid(), pks);
          
            StarletPlayer starletPlayer = getStarletPlayer(homeTeam.getTeamId(), bp.getRid());
            if(starletPlayer == null) {
                log.error("endMatch0 starletPlayer tid{}, Rid(){}", homeTeam.getTeamId(), bp.getRid());
                continue; 
            }
           
            int cap = this.calculCapByPRid(starletDualMeet);
            starletPlayer.setCap(cap);
            starletPlayer.save();           
        }         
    }

    /** 更新比赛数据*/
    private StarletDualMeet updataStarletDual(long tid, int prid, PlayerActStat pks) {
        StarletDualMeet starletDualMeet = this.getStarletDualByPRid(tid, prid);
        starletDualMeet.setAst(starletDualMeet.getAst() + pks.getIntValue(EActionType.ast));
        starletDualMeet.setBlk(starletDualMeet.getBlk() + pks.getIntValue(EActionType.blk));
        starletDualMeet.setPf(starletDualMeet.getPf() + pks.getIntValue(EActionType.pf));
        starletDualMeet.setPts(starletDualMeet.getPts() + pks.getIntValue(EActionType.pts));
        starletDualMeet.setReb(starletDualMeet.getReb() + pks.getIntValue(EActionType.reb));
        starletDualMeet.setStl(starletDualMeet.getStl() + pks.getIntValue(EActionType.stl));
        starletDualMeet.setTo(starletDualMeet.getTo() + pks.getIntValue(EActionType.to));
        starletDualMeet.setTotal(starletDualMeet.getTotal() + 1);
        starletDualMeet.save();
        
        return starletDualMeet;
    }
    
    /** 计算玩家战力*/
    private int calculCapByPRid(StarletDualMeet starletDualMeet) {
        int endNum = 0;
        List<DualMeetRadixBean> nums = StarletConsole.getDualMeetRadixMap().values().stream().collect(Collectors.toList());
        nums.sort(Comparator.comparing(DualMeetRadixBean::getNum));
        for(DualMeetRadixBean dualMeetRadixBean : nums) {
            if(starletDualMeet.getTotal() >= dualMeetRadixBean.getNum()) {
                endNum = dualMeetRadixBean.getNum();
            }
        }
        
        DualMeetRadixBean bean = StarletConsole.getDualMeetRadixMap().get(endNum);
        if(bean == null) {
            log.info("calculCapByPRid DualMeetRadixBean is nil, teamId{}, prid{}, num{}", starletDualMeet.getTeamId(), starletDualMeet.getPlayerRid(), endNum);
            return 0;
        }
       
        return (int)Math.round(calculCap(starletDualMeet, bean)) > 0?(int)Math.ceil(calculCap(starletDualMeet, bean)):0;
    }

    /** 计算战力 (得分*a+篮板*b+助攻*c+抢断*d+盖帽*e-失误*f-犯规*g)*/
    public double calculCap(StarletDualMeet avg, DualMeetRadixBean bean) {
        return (double)avg.getPts()/avg.getTotal() * bean.getPts() +
               (double)avg.getReb()/avg.getTotal() * bean.getReb() + 
               (double)avg.getAst()/avg.getTotal() * bean.getAst() + 
               (double)avg.getStl()/avg.getTotal() * bean.getStl() +  
               (double)avg.getBlk()/avg.getTotal() * bean.getBlk() - 
               (double)avg.getTo()/avg.getTotal() * bean.getTo() - 
               (double)avg.getPf()/avg.getTotal() * bean.getPf();
    }  
    
    /** 根据队舞ID确认比赛类型*/
    private int getTypeByNpcTeamId(long npcId) {
        for(Map.Entry<Integer, DualMeetBean> entry : StarletConsole.getDualMeetBeanMap().entrySet()) {
            DualMeetBean dualMeetBean = entry.getValue();
            if(dualMeetBean.getInitNpcTeam() == npcId) return entry.getKey();
        }
        
        return 0;
    }
    
    /** 根据自己的排名获取排行榜*/
    public List<StarletRank> getStarletRankList(int rank) {
        Map<Integer, StarletRank> map = this.getStarletRankMap();
        
        if(rank > map.size()) {
            log.error("getStarletRankList  rank{} is error", rank);
            return Lists.newArrayList();
        }
    
        if(map.size() <= StarletConsole.getTotalNum()){
            return map.values().stream().collect(Collectors.toList());
        }
        
        // 自己的排名 
        List<StarletRank> ranks = new ArrayList<>();
        if(rank <= StarletConsole.getTotalNum()) {
           // 取比我排名高的数据
           for(int i = 1; i <= rank; i++) {
               log.trace("取的名次 rank{}", i);
               ranks.add(map.get(i));
           }
           
           // 取比我排名低的数据
          int endNum = StarletConsole.getTotalNum() - ranks.size();
          for(int idx = 1; idx <= endNum; idx ++) {
              rank = rank + 1;
              log.trace("取的名次 rank{}", idx);
              ranks.add(map.get(rank));
          }
          
        }else {
            for(int i = rank; i > 0; i--) {
                if(ranks.size() < StarletConsole.getTotalNum() -1) {
                    log.trace("取的名次 rank{}", i);
                    ranks.add(map.get(i));  
                }else {
                    break;
                }
            }
            
            if(rank < StarletConsole.getTotalNum() + 10) {
                ranks.add(map.get(1));
                log.trace("取的名次 rank{}", 1);
            }else {
                ranks.add(map.get(rank - 19));
                log.trace("取的名次 rank{}", rank - 19);
            }   
        }
        
        return ranks;
    }
    
    /** 比赛开始通知 */
    private void startPush(BattleSource bs) {
        //比赛开启成功推送，前端初始化比赛信息
        sendMessage(bs.getHomeTid(), BattlePb.battleStartResp(bs), ServiceCode.Battle_Start_Push);
    }
    
    /**
     * 获取新秀对抗赛赛比赛ID
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
    
    /** 从仓库中中删除玩家调用*/
    public void deleteStarletTeamPlayer(TeamPlayer teamPlayer, long tid, Integer prid) {
        if(!teamPlayer.getNPridsAndStorage().contains(prid)) { 
            Map<Integer, StarletPlayer> map =  this.getStarletPlayerMap(tid); 
            map.remove(prid);
            starletPlayerMap.put(tid, map);
            
            starletAO.deleteStarletPlayer(tid, prid);
            
            Map<Integer, StarletDualMeet> map1 = this.getStarletDualMeetMapByTeamId(tid);
            map1.remove(prid);
            starletDualMeetMap.put(tid, map1);
            starletAO.deleteStarletDualMeet(tid, prid);
        }      
    }
    
    /** 每日发奖调度*/
    public void quartzSendReward() {
        if(getStatus() != EActiveStatus.进行中) {
            return;
        }
        
        Map<Integer, StarletRank> rankMap = this.getStarletRankMap();
        if(rankMap.isEmpty()) return;
        
        for(Map.Entry<Integer, StarletRank> entry : rankMap.entrySet()) {
            StarletRank starletRank = entry.getValue();
            StarletRankAwardBean rewardBean = this.getStarletRankAwardBean(starletRank.getRank());
            if(rewardBean == null) {              
                continue;
            }
            
            emailManager.sendEmail(starletRank.getTeamId(), 50001, starletRank.getRank()+"", rewardBean.getReward());
            log.trace("SM quartzSendReward tid{}, rank{}", starletRank.getTeamId(), starletRank.getRank());
        }
    }
    
  private PlayerDualMeetData.Builder builderPlayerDualMeetData(StarletDualMeet starletDualMeet) {
        
        PlayerDualMeetData.Builder msg = PlayerDualMeetData.newBuilder();      
        msg.setPlayerRid(starletDualMeet.getPlayerRid());
        msg.setNum(starletDualMeet.getTotal());
        msg.setCap(this.calculCapByPRid(starletDualMeet));
        msg.addStat(BattlePlayerStatResp.newBuilder().setType(EActionType.ast.getType()).setValue(starletDualMeet.getAst()));
        msg.addStat(BattlePlayerStatResp.newBuilder().setType(EActionType.blk.getType()).setValue(starletDualMeet.getBlk()));
        msg.addStat(BattlePlayerStatResp.newBuilder().setType(EActionType.pf.getType()).setValue(starletDualMeet.getPf()));
        msg.addStat(BattlePlayerStatResp.newBuilder().setType(EActionType.pts.getType()).setValue(starletDualMeet.getPts()));
        msg.addStat(BattlePlayerStatResp.newBuilder().setType(EActionType.reb.getType()).setValue(starletDualMeet.getReb()));
        msg.addStat(BattlePlayerStatResp.newBuilder().setType(EActionType.stl.getType()).setValue(starletDualMeet.getStl()));
        msg.addStat(BattlePlayerStatResp.newBuilder().setType(EActionType.to.getType()).setValue(starletDualMeet.getTo()));
                
        log.trace("sm builderPlayerDualMeetData tid{},  prid{}, num{}, cap{}, statList{}", starletDualMeet.getTeamId(), msg.getPlayerRid(),
                msg.getNum(), msg.getCap(), msg.getStatList().toString());
        return msg;
    }
     
    private StarletRankData.Builder builderStarletRankData(StarletRank starletRank) {
        Team team = teamManager.getTeam(starletRank.getTeamId());
        
        StarletRankData.Builder msg = StarletRankData.newBuilder();
        msg.setTeamId(starletRank.getTeamId());
        msg.setRank(starletRank.getRank());
        msg.setTeamLv(team.getLevel());
        msg.setTeamName(team.getName());
        
        int totalCap = getStarletTeamCap(starletRank.getTeamId());      
        msg.setTotalCap(totalCap);
        
        StarletPlayer starletPlayer = this.getStarletPlayerByMaxCap(starletRank.getTeamId());
        if(starletPlayer != null) {
            msg.setPlayerRid(starletPlayer.getPlayerRid());
            
            log.trace("sm builderStarletRankData tid {}, rank {}, maxCapPlayerRid {}, maxCap {}",
                    starletRank.getTeamId(), starletRank.getRank(), starletPlayer.getPlayerRid(), starletPlayer.getCap());
        }
        
        log.trace("sm builderStarletRankData tid {}, rank {}, teamLv {}, teamName {}, totalCap {}",
                starletRank.getTeamId(), starletRank.getRank(), team.getLevel(), team.getName(), totalCap);
      
        return msg;
    }
    
    private  StarletTeamData.Builder builderStarletTeamData(StarletPlayer starletPlayer){
        StarletTeamData.Builder builder = StarletTeamData.newBuilder();     
        builder.setPlayerRid(starletPlayer.getPlayerRid());   
        builder.setPosition(starletPlayer.getLineupPosition());
        builder.setCap(starletPlayer.getCap());
        
        log.trace("sm rankMatchTeamData0 StarletTeamData pos{}, cap{}", builder.getPosition(), builder.getCap());
        return builder;
    }
    
    private StarletRankAwardBean getStarletRankAwardBean(int rank) {
        Map<Integer, StarletRankAwardBean> beanMap = StarletConsole.getRankAwardBeanMap();
        if(beanMap == null) {
            log.warn("getStarletRankAwardBean is null");
            return null;
        }
        
        return beanMap.get(rank);
    }
    
    @Override
    public void offline(long tid) {
        starletDualMeetMap.remove(tid);
        starletPlayerMap.remove(tid);   
        starletTeamDualMeetMap.remove(tid);
    }

    @Override
    public void dataGC(long tid) {
        starletDualMeetMap.remove(tid);
        starletPlayerMap.remove(tid); 
        starletTeamDualMeetMap.remove(tid);
    }
}
