package com.ftkj.manager.logic;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.UnCheck;
import com.ftkj.cfg.EmailViewBean;
import com.ftkj.cfg.KnockoutMatchBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.EmailConsole;
import com.ftkj.console.KnockoutMatchConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.db.ao.logic.IMatchAO;
import com.ftkj.db.domain.match.MatchBestPO;
import com.ftkj.db.domain.match.MatchPKPO;
import com.ftkj.db.domain.match.MatchPO;
import com.ftkj.db.domain.match.MatchSignPO;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EMatchStatus;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.User;
import com.ftkj.manager.battle.BattleManager;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.logic.match.KnockoutMatch;
import com.ftkj.manager.logic.match.MatchMT;
import com.ftkj.manager.logic.match.MatchType;
import com.ftkj.manager.match.MatchBest;
import com.ftkj.manager.match.MatchSign;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.system.CheckAPI;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceManager;
import com.ftkj.util.ThreadPoolUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

/**
 * @author Jay
 * @Description:多人赛
 * @time:2017年5月17日 下午6:30:23
 */
public class KnockoutMatchPKManager extends AbstractBaseManager {

    @IOC
    private IMatchAO matchAO;
    @IOC
    private TeamManager teamManager;
    @IOC
    private LocalBattleManager localBattleManager;
    @IOC
    private BattleManager battleManager;
    @IOC
    private TeamEmailManager emailManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private LeagueManager leagueManager;
    @IOC
    private ChatManager chatManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private GameManager gameManager;
    /**
     * matchId：seq (开启比赛的最大seqID)
     */
    private Map<Integer, AtomicInteger> seqMap;
    /**
     * 比赛类型 matchId : [seq:match] </BR>
     */
    private Map<Integer, Map<Integer, KnockoutMatch>> fastMatchMap;

    @Override
    public void initConfig() {
        clearStopMatch();
        initMatch();
    }

    @Override
    public void instanceAfter() {
        EventBusManager.register(EEventType.任务调度每分钟, this);
        // 初始化线程池
        ThreadPoolUtil.newScheduledPool(KnockoutMatchConsole.Thread_Name, KnockoutMatchConsole.Thread_Num);
        // 集合
        fastMatchMap = Maps.newConcurrentMap();
        seqMap = Maps.newHashMap();
        // 从DB加载已经开启的每届最新比赛，结束的不在列表
        List<MatchPO> matchPOList = matchAO.getMatchPOList();
        // 序列（届数）
        initMatchSeq(matchAO.getMatchSeq());
        initMatchPOList(matchPOList);
    }

    @SuppressWarnings("unused")
    private void test() {
        //  测试开多场比赛
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
        }
        testSignMatch(1011000001316L, 102, 1);
        testSignMatch(1011000001329L, 103, 2);
        testSignMatch(1011000001355L, 103, 3);
        testSignMatch(1011000003605L, 103, 4);
        testSignMatch(10110000000499L, 103, 5);
    }

    /**
     * 取比赛的管理map,不会为空
     *
     * @param matchId
     * @return
     */
    private Map<Integer, KnockoutMatch> getMatchMap(int matchId) {
        Map<Integer, KnockoutMatch> map = fastMatchMap.get(matchId);
        if (map == null) {
            map = Maps.newConcurrentMap();
            fastMatchMap.put(matchId, map);
        }
        return map;
    }

    /**
     * 从内存中移除比赛
     *
     * @param matchId
     * @param seqId
     */
    private void removeMatch(int matchId, int seqId) {
        Map<Integer, KnockoutMatch> map = getMatchMap(matchId);
        map.remove(seqId);
    }

    /**
     * 根据tid取现有的比赛类型
     *
     * @param tid
     * @return
     */
    private List<KnockoutMatch> getMatchByTid(int tid) {
        List<KnockoutMatch> list = Lists.newArrayList();
        List<Integer> matchIds = KnockoutMatchConsole.getMatchList().stream().filter(bean -> bean.getTid() == tid).map(bean -> bean.getMatchId()).collect(Collectors.toList());
        for (int matchId : matchIds) {
            list.addAll(getMatchMap(matchId).values().stream().filter(m -> m.getStatus() != EMatchStatus.结束.status).collect(Collectors.toList()));
        }
        return list;
    }

    /**
     * 取所有的常规赛
     *
     * @return
     */
    private List<KnockoutMatch> getKnockoutMatchList() {
        List<KnockoutMatch> list = Lists.newArrayList();
        for (int matchId : fastMatchMap.keySet()) {
            if (KnockoutMatchConsole.getMatchById(matchId).getType() == 1) {
                list.addAll(getMatchMap(matchId).values());
            }
        }
        return list;
    }

    /**
     * 取所有的超快赛
     *
     * @return
     */
    private List<KnockoutMatch> getFastMatchList() {
        List<KnockoutMatch> list = Lists.newArrayList();
        for (int matchId : fastMatchMap.keySet()) {
            if (KnockoutMatchConsole.getMatchById(matchId).getType() == 2) {
                list.addAll(getMatchMap(matchId).values());
            }
        }
        return list;
    }

    /**
     * 取所有的比赛
     *
     * @return
     */
    @SuppressWarnings("unused")
    private List<KnockoutMatch> getAllMatch() {
        List<KnockoutMatch> list = Lists.newArrayList();
        for (int matchId : fastMatchMap.keySet()) {
            list.addAll(getMatchMap(matchId).values());
        }
        return list;
    }

    /**
     * 每分钟任务调度
     *
     * @param datetime
     */
    @Subscribe
    public void everyMinJob(Date datetime) {
        DateTime now = new DateTime(datetime);
        checkStartPK(now);
        matchBeforeTopic(now);
        checkCreateMatch(now);
        checkFastTimeOut(now);
    }

    /**
     * 初始化比赛，配置变更加载比赛
     */
    private void initMatch() {
        // 重新载入新的列表
        for (KnockoutMatchBean bean : KnockoutMatchConsole.getMatchList()) {
            int matchId = bean.getMatchId();
            // 定时多人赛
            Map<Integer, KnockoutMatch> map = getMatchMap(matchId);
            if (map.size() < getGameMax(bean)) {
                KnockoutMatch match = KnockoutMatch.createMatch(getSeqIdByMatch(matchId), matchId);
                map.put(match.getSeqId(), match);
            }
        }
    }

    /**
     * 同时最大开启比赛次数，目前是超快赛不限制，周期赛只能1场
     *
     * @param bean
     * @return
     */
    private int getGameMax(KnockoutMatchBean bean) {
        if (bean.getType() == 2) {
            return 9999;
        }
        if (bean.getType() == 1) {
            return 1;
        }
        return 1;
    }

    /**
     * 结束配置里面已经去掉的比赛
     */
    private void clearStopMatch() {
        // 汇总比赛
        Collection<KnockoutMatch> list = Lists.newArrayList();
        for (Map<Integer, KnockoutMatch> map : fastMatchMap.values()) {
            list.addAll(map.values());
        }
        // 清理配置已经去掉的比赛，但是一般不会这样做;
        for (KnockoutMatch match : list) {
            KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(match.getMatchId());
            if (bean != null) {
                continue;
            }
            match.setStatus(EMatchStatus.结束.status);
            //
            if (match.isSaveDB()) {
                match.save();
            }
            removeMatch(match.getMatchId(), match.getSeqId());
        }
    }

    /**
     * 比赛届数ID初始化
     *
     * @param map
     */
    private void initMatchSeq(Map<String, String> map) {
        // 先分组，再设置
        for (String matchId : map.keySet()) {
            seqMap.put(Integer.valueOf(matchId), new AtomicInteger(Integer.valueOf(map.get(matchId))));
        }
    }

    /**
     * 初始化比赛列表
     *
     * @param list
     */
    private void initMatchPOList(List<MatchPO> list) {
        /*
         * 1，当前届已结束则开启新的
         * 2，如果还没有结束，则正常初始化就好
         * 3，如果过期了，延顺就行
         */
        List<MatchPO> starting = list.stream().filter(m -> m.getStatus() != 3).collect(Collectors.toList());
        for (MatchPO match : starting) {
            initMatch(match);
        }
    }

    /**
     * 初始化调用，重置比赛调用
     *
     * @param match
     */
    private void initMatch(MatchPO match) {
        if (match == null || match.getStatus() == 3) {
            return;
        }
        // 保证配置有，才会load, 保证可以直接加载DB的比赛
        //        if (KnockoutMatchConsole.getMatchById(match.getMatchId()) == null) {
        //            return;
        //        }
        // 比赛中,查询本届所有比赛记录
        List<MatchSignPO> signList = matchAO.getMatchSignPOList(match.getSeqId(), match.getMatchId());
        List<MatchPKPO> pkList = null;
        if (match.getStatus() == 2) {
            pkList = matchAO.getMatchPKPOList(match.getSeqId(), match.getMatchId());
        }
        Map<Integer, KnockoutMatch> map = getMatchMap(match.getMatchId());
        // 重新加载DB里的比赛
        if (map.containsKey(match.getSeqId())) {
            KnockoutMatch knockoutMatch = map.get(match.getSeqId());
            knockoutMatch.setStatus(EMatchStatus.结束.status);
            map.remove(match.getSeqId());
        }
        // 重新put进去
        KnockoutMatch nMatch = new KnockoutMatch(match, signList, pkList);
        map.put(nMatch.getSeqId(), nMatch);
    }

    /**
     * 根据比赛类型取序列
     *
     * @param id
     * @return
     */
    public synchronized int getSeqIdByMatch(int id) {
        if (!seqMap.containsKey(id)) {
            seqMap.put(id, new AtomicInteger(0));
        }
        return seqMap.get(id).incrementAndGet();
    }

    /**
     * 是否已报名某种比赛
     *
     * @param teamId
     * @param match_tid 大类型tid
     * @return
     */
    public boolean isSignMatch(long teamId, int match_tid) {
        return getMatchByTid(match_tid).stream().anyMatch(m -> m.isSign(teamId));
    }
    
    /**
     * 多人赛比赛中
     * @param teamId
     * @param match_tid
     * @return
     */
    public boolean isInMatching(long teamId, int match_tid) {
      return getMatchByTid(match_tid).stream().anyMatch(m -> m.isInMatching(teamId));
    }
    

    // ---------------------------------初始化 end-------------------------------

    /**
     * 历史最佳
     *
     * @param teamId
     */
    @ClientMethod(code = ServiceCode.Match_History_Best)
    public void showBestRank(long teamId) {
        sendMessage(MatchMT.getMatchBestListData(teamId, getTeamMatchBest(teamId).values()));
    }

    /**
     * 取球队的历史最佳
     *
     * @param teamId
     * @return
     */
    private Map<Integer, MatchBest> getTeamMatchBest(long teamId) {
        String key = RedisKey.Match_History_Best + teamId;
        Map<Integer, MatchBest> rankMap = redis.getMapAllKeyValues(key);
        if (rankMap == null || rankMap.size() == 0) {
            List<MatchBestPO> poList = matchAO.getTeamMatchBestList(teamId);
            rankMap = poList.stream().collect(Collectors.toMap(MatchBestPO::getMatchId, (best) -> new MatchBest(best)));
            redis.hmset(key, rankMap);
        }
        return rankMap;
    }

    /**
     * 更新历史最佳
     *
     * @param matchId
     * @param list
     */
    public void updateBestRank(int matchId, List<MatchSign> list) {
        for (MatchSign matchSign : list) {
            // 球队的排名数据
            long teamId = matchSign.getTeamId();
            if (NPCConsole.isNPC(teamId)) {
                continue;
            }
            Map<Integer, MatchBest> rankMap = getTeamMatchBest(teamId);
            MatchBest best = null;
            // 新排名
            if (!rankMap.containsKey(matchId)) {
                best = new MatchBest(new MatchBestPO(matchId, teamId, matchSign.getRank()));
                best.save();
                rankMap.put(best.getMatchId(), best);
                redis.hmset(RedisKey.Match_History_Best + teamId, rankMap);
                continue;
            }
            // 更新排名
            best = rankMap.get(matchId);
            if (best.getRank() > matchSign.getRank()) {
                best.setRank(matchSign.getRank());
                best.save();
                rankMap.put(best.getMatchId(), best);
                redis.hmset(RedisKey.Match_History_Best + teamId, rankMap);
            }
        }
    }

    /**
     * 上届排名
     *
     * @param matchId
     */
    @ClientMethod(code = ServiceCode.Match_Last_Rank)
    public void showLastRank(int matchId) {
        // 最后一届打完比赛类型的排名
        Map<Integer, KnockoutMatch> map = getMatchMap(matchId);
        int lastSeq = map.keySet().stream().mapToInt(k -> k).max().orElse(1) - 1;
        String key = RedisKey.Match_Last_Rank + matchId + "_" + lastSeq;
        List<MatchSign> rankList = redis.getList(key);
        if (rankList == null || rankList.size() == 0) {
            // 前八排名
            List<MatchSignPO> poList = matchAO.getMatchSignPOListRank(lastSeq, matchId, 8);
            rankList = poList.stream().map(po -> new MatchSign(po)).collect(Collectors.toList());
            redis.rpush(key, rankList);
        }
        sendMessage(MatchMT.getMatchLastRank(rankList, teamManager));
    }

    // ------------------------------------------------------------------------

    /**
     * 主界面,所有的多人赛类型
     */
    @ClientMethod(code = ServiceCode.Match_List)
    public void showMatchList() {
        long teamId = getTeamId();
        //
        ServiceManager.addService(ServiceConsole.MatchList, teamId);
        //
        int dailySignupNum = getFastCupTodayFight(teamId);
        sendMessage(MatchMT.getMatchListData(getMatchList(teamId), teamId,dailySignupNum));
    }

//    /**
//     * 取主界面所有比赛
//     *
//     * @param teamId
//     * @return
//     */
//    public List<KnockoutMatch> getMatchList(long teamId) {
//      // 常规多人赛
//      if (NPCConsole.isNPC(teamId)) {
//          return Lists.newArrayList();
//      }
//      Team team = teamManager.getTeam(teamId);
//      List<KnockoutMatch> matchList = Lists.newArrayList();
//      /*
//       * 快速多人赛，这里有多场，有两种情况；
//       * 1，已报名，则返回准备的场次数据
//       * 2，未报名，一个闲置的场次数据
//       */
//      List<KnockoutMatchBean> beanList = KnockoutMatchConsole.getMatchList();
//      // 过滤了等级，但是还应该加上已报名的
//      List<Integer> signType = Lists.newArrayList();
//      for (KnockoutMatchBean bean : beanList) {
//          KnockoutMatch match = null;
//          // 已报名
//          int matchId = bean.getMatchId();
//          int tid = bean.getTid();
//          if (signType.contains(tid)) {
//              continue;
//          }
//          Map<Integer, KnockoutMatch> map = getMatchMap(matchId);
//          // 已报名
//          if (isSignMatch(teamId, tid)) {
//              match = map.values().stream().filter(m -> m.isSign(teamId)).findFirst().orElse(null);
//          }
//          // 满足等级直接取最新的比赛
//          else if (KnockoutMatchConsole.checkTeamLevel(matchId, team.getLevel())) {
//              // 直接取在报名中
//
//              if (bean.getType() == 2) {
//                  match = map.values().stream().filter(m -> m.getStatus() == EMatchStatus.开始报名.status).findFirst().orElse(null);
//                  if (match == null) {
//                      match = getFastMatchTheNewes(matchId);
//                  }
//              } else if (bean.getType() == 1) {
//                  // 只取1场，现在也只有1场，取序列最大的一场
//                  match = map.values().stream()
//                      .sorted(Comparator.comparingInt(KnockoutMatch::getSeqId).reversed())
//                      .findFirst().orElse(null);
//              }
//          }
//          // 超快赛特殊处理
//          if (match != null) {
//              signType.add(tid);
//              matchList.add(match);
//          }
//      }
//      // 过滤掉当天不显示的每日赛
//      matchList = matchList.stream()
//          .filter(m -> m.getStatus() != EMatchStatus.开始报名.status || m.isTodayShow())
//          .sorted(Comparator.comparingInt(KnockoutMatch::getSort)).collect(Collectors.toList());
//      return matchList;
//    }
    
    /**
     * 取主界面所有比赛
     *
     * @param teamId
     * @return
     */
    public List<KnockoutMatch> getMatchList(long teamId) {
      // 常规多人赛
      if (NPCConsole.isNPC(teamId)) {
        return Lists.newArrayList();
      }
      Team team = teamManager.getTeam(teamId);
      List<KnockoutMatch> matchList = Lists.newArrayList();
      /*
       * 快速多人赛，这里有多场，有两种情况；
       * 1，已报名，则返回准备的场次数据
       * 2，未报名，一个闲置的场次数据
       */
      List<KnockoutMatchBean> beanList = KnockoutMatchConsole.getMatchList();
      // 过滤了等级，但是还应该加上已报名的
      List<Integer> signType = Lists.newArrayList();
      for (KnockoutMatchBean bean : beanList) {
        KnockoutMatch match = null;
        // 已报名
        int matchId = bean.getMatchId();
        int tid = bean.getTid();
        if (signType.contains(tid)) {
          continue;
        }
        Map<Integer, KnockoutMatch> map = getMatchMap(matchId);
        int teamCap = teamManager.getTeamAllAbility(teamId).getTotalCap();
        // 不是超快赛：已报名；是超快赛：已报名并且没被淘汰
        if ((isSignMatch(teamId, tid) && bean.getType() != 2) || (bean.getType() == 2 && isInMatching(teamId, tid))) {
          if(bean.getType() != 2) {
            match = map.values().stream().filter(m -> m.isSign(teamId)).findFirst().orElse(null);
          }else {
            match = map.values().stream().filter(m -> m.isSign(teamId) && m.getTeamSignMap().get(teamId).isOut() == false).findFirst().orElse(null);
          }
        }else if(bean.getType() == 2) {//是超快赛，没报名
          if(KnockoutMatchConsole.checkTeamCombat(matchId, teamCap)) {
            match = map.values().stream().filter(m -> m.getStatus() == EMatchStatus.开始报名.status).findFirst().orElse(null);
            if (match == null) {
              match = getFastMatchTheNewes(matchId);
            }
          }
        }
        // 满足等级直接取最新的比赛
        else if (KnockoutMatchConsole.checkTeamLevel(matchId, team.getLevel())) {
          // 直接取在报名中
          if (bean.getType() == 1) {
            // 只取1场，现在也只有1场，取序列最大的一场
            match = map.values().stream()
                .sorted(Comparator.comparingInt(KnockoutMatch::getSeqId).reversed())
                .findFirst().orElse(null);
          }
        }
        // 超快赛特殊处理
        if (match != null) {
          signType.add(tid);
          matchList.add(match);
        }
      }
      // 过滤掉当天不显示的每日赛
      matchList = matchList.stream()
          .filter(m -> m.getStatus() != EMatchStatus.开始报名.status || m.isTodayShow())
          .sorted(Comparator.comparingInt(KnockoutMatch::getSort)).collect(Collectors.toList());
      return matchList;
    }

    //	/**
    //	 * 判断是否报名同类型的比赛
    //	 * @param teamId
    //	 * @param matchId
    //	 * @return
    //	 */
    //	@Deprecated
    //	private boolean isJoinMatchType(long teamId, int matchId) {
    //		if(!joinMathMap.containsKey(teamId)) return false;
    //		return joinMathMap.get(teamId).containsKey(KnockoutMatchConsole.getTid(matchId));
    //	}

    /**
     * 关闭窗口，移除监听推包
     */
    @ClientMethod(code = ServiceCode.Match_Main_View_Exit)
    public void closeView() {
        long teamId = getTeamId();
        ServiceManager.removeService(ServiceConsole.MatchList, teamId);
    }

    /**
     * 比赛详细界面
     */
    @ClientMethod(code = ServiceCode.Match_Detai_View_Join)
    public void openDetailView(int matchId, int seqId) {
        KnockoutMatch match = getMatch(matchId, seqId);
        if (match == null || match.getSeqId() != seqId) {
            return;
        }
        long teamId = getTeamId();
        //
        ServiceManager.addService(ServiceConsole.getMatchDetailKey(matchId, seqId), teamId);
        //
        sendMessage(MatchMT.getMatchDetailData(match, leagueManager));
    }

    /**
     * 关闭详细界面
     */
    @ClientMethod(code = ServiceCode.Match_Detail_View_Exit)
    public void closeDetailView(int matchId, int seqId) {
        long teamId = getTeamId();
        ServiceManager.removeService(ServiceConsole.getMatchDetailKey(matchId, seqId), teamId);
    }

    // -----------------------------------------------------------------------------

    /**
     * 测试报名即开（自动补NPC的比赛）
     *
     * @param teamId
     * @param matchId
     * @param seqId
     */
    private void testSignMatch(long teamId, int matchId, int seqId) {
        ErrorCode code = sign(teamId, matchId, seqId, true);
        log.warn("测试多人赛报名失败：{}", code.getTip());
    }

    /**
     * 报名多人赛
     *
     * @param seq     序列，当前开启的多人赛届数
     * @param matchId 比赛ID，也是比赛类型
     */
    @ClientMethod(code = ServiceCode.Match_Sign)
    public void sign(int matchId, int seqId) {
        ErrorCode ret = sign(getTeamId(), matchId, seqId, false);
        sendMsg(ret);
    }

    /**
     * 报名多人赛
     *
     * @param teamId
     * @param matchId 比赛ID，也是比赛类型
     * @param seqId   序列，当前开启的多人赛届数
     * @param autoNpc 强制补NPC，不读配置，只限超快赛
     * @return
     */
    public ErrorCode sign(long teamId, int matchId, int seqId, boolean autoNpc) {
        if (matchId == 0 && seqId == 0) {
            log.warn("多人赛报名参数异常");
            return ErrorCode.ParamError;
        }
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
        if (bean == null) {
            log.debug("比赛不存在，参数异常");
            return ErrorCode.Match_2;
        }
        //奖杯超快赛特殊处理 
        if(bean.getTid() == MatchType.j_b_c_k_s.getType()) {
          return signFastCup(teamId,matchId,seqId,autoNpc);
        }
        // 报名条件
        int lv = teamManager.getTeam(teamId).getLevel();
        if (lv < bean.getNeedLv() || lv > bean.getNeedMaxLv()) {
            log.debug("不符合报名等级!");
            return ErrorCode.Team_Level;
        }
        
        // 取当场比赛
        KnockoutMatch match = getMatchMap(matchId).get(seqId);
        if (match == null) {
            log.debug("没有找到比赛类型或者比赛已结束");
            return ErrorCode.Match_2;
        }
        
        if (isSignMatch(teamId, bean.getTid())) {
          log.debug("报名失败，已报名同类型比赛未结束");
          return ErrorCode.Match_4;
        }
        // 满人或者开始比赛
        if (match.getStatus() != EMatchStatus.开始报名.status) {
            log.debug("报名失败，已截止报名");
            return ErrorCode.Match_3;
        }
        // 已报名
        if (match.isSign(teamId)) {
            log.debug("已报过名");
            return ErrorCode.Match_4;
        }
        // 报名消耗
        TeamProp teamProp = propManager.getTeamProp(teamId);
        TeamMoney teamMoney = teamMoneyManager.getTeamMoney(teamId);
        List<PropSimple> needList = Lists.newArrayList();
        if (bean.getNeedPropList() != null && bean.getNeedPropList().size() > 0) {
            needList.addAll(bean.getNeedPropList());
        }
        // 突破上限消耗
        if (match.getSignNum() + 1 > bean.getMaxTeam() && bean.getBeyondPropList() != null && bean.getBeyondPropList().size() > 0) {
            needList = bean.getBeyondPropList();
        }
        if (needList.size() > 0 && !CheckAPI.checkTeamPropNum(needList, teamProp, teamMoney)) {
            log.debug("报名已满，突破报名数道具不足");
            return ErrorCode.Prop_0;
        }
        synchronized (match) {
            // 已报名
            if (match.isSign(teamId)) {
                log.debug("已报过名");
                return ErrorCode.Match_4;
            }
            // 判断人数
            if (match.getSignNum() + 1 > bean.getBeyondMax()) {
                log.debug("报名人数已满");
                return ErrorCode.Match_1;
            }
            // 扣道具
            propManager.usePropOrMoney(teamId, needList, true, ModuleLog.getModuleLog(EModuleCode.多人赛, "报名-" + bean.getName()));
            // 报名传入战力值
            match.sign(teamId, teamManager.getTeamAllAbility(teamId).getTotalCap());
            if (bean.getType() == 2) {
                // 超快赛：是否自动补满NPC,这个判断只是为了超快赛方便测试
                if (bean.getAutoNpc() == 1 || autoNpc) {
                    // 补NPC
                    int npcNum = bean.getMaxTeam() - match.getSignNum();
                    log.debug("多人赛[{}], 补发ＮＰＣ报名人数{}", match.getMatchId(), npcNum);
                    List<Long> npcTeamList = NPCConsole.getSeqNpcId(npcNum, match.getSignNpcList(), KnockoutMatchConsole.NPC_Min_ID, KnockoutMatchConsole.NPC_Max_ID);
                    for (long npcId : npcTeamList) {
                        match.sign(npcId, 100);//-- NPC的战力值，补NPC时，100这个值没意义
                    }
                }
                // 多人赛人满即开
                if (match.getSignNum() == bean.getMaxTeam()) {
                    match.startPK();
                }
            }
            // 五分钟内刚好满下限的情况，推送赛前提示通知
            else if (match.getSignNum() == bean.getMinTeam()) {
                matchStartTopic(match, DateTime.now());
            }
            int dailySignupNum = getFastCupTodayFight(teamId);
            // 推包处理，超快赛直接推新的比赛包
            if (bean.getType() == 2 && match.getSignNum() == bean.getMaxTeam()) {
                // 已报满，推送老比赛列表，并开启新的
                sendMessageTeamIds(match.getSignTeamListNotNpc(), MatchMT.getMatchTopicData(match,dailySignupNum), ServiceCode.Match_Topic);
                KnockoutMatch newMatch = getFastMatchTheNewes(matchId);
                if (newMatch != null) {
                    sendMessage(ServiceConsole.MatchList, match.getSignTeamListNotNpc(), MatchMT.getMatchTopicData(newMatch,dailySignupNum), ServiceCode.Match_Topic);
                }
            } else {
                // 其他比赛推包
                sendMessage(ServiceConsole.MatchList, MatchMT.getMatchTopicData(match,dailySignupNum), ServiceCode.Match_Topic);
            }
        }
        // 推送一下目标变化
        // gameManager.topicMyTarget(teamId);
        return ErrorCode.Success;
    }
    
    /**
     * 奖杯超快赛报名
     * @param teamId
     * @param matchId
     * @param seqId
     * @param autoNpc
     * @return
     */
    public ErrorCode signFastCup(long teamId, int matchId, int seqId, boolean autoNpc) {
      if (matchId == 0 && seqId == 0) {
        log.warn("多人赛报名参数异常");
        return ErrorCode.ParamError;
      }
      KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
      if (bean == null) {
        log.debug("比赛不存在，参数异常");
        return ErrorCode.Match_2;
      }
      
      //战力是否满足 
      int teamCap = teamManager.getTeamAllAbility(teamId).getTotalCap();
      if (teamCap < bean.getNeedCombat() || teamCap > bean.getNeedMaxCombat()) {
          log.debug("不符合报名战力!");
          return ErrorCode.Team_5;
      }
      
      int dayNum = getFastCupTodayFight(teamId);
      int fast_cup_Count = ConfigConsole.getGlobal().fast_cup_Count;
      if(dayNum >= fast_cup_Count) {
        log.debug("今日报名次数已用完");
        return ErrorCode.Team_daily_times_limit; 
      }
      // 取当场比赛
      KnockoutMatch match = getMatchMap(matchId).get(seqId);
      if (match == null) {
        log.debug("没有找到比赛类型或者比赛已结束");
        return ErrorCode.Match_2;
      }
        //正在同类型多人赛比赛中
      if (isInMatching(teamId, bean.getTid())) {
            log.debug("报名失败，已报名同类型比赛未结束");
            return ErrorCode.Match_4;
      }
      // 满人或者开始比赛
      if (match.getStatus() != EMatchStatus.开始报名.status) {
        log.debug("报名失败，已截止报名");
        return ErrorCode.Match_3;
      }
      // 已报名
      if (match.isSign(teamId)) {
        log.debug("已报过名");
        return ErrorCode.Match_4;
      }
      // 报名消耗
      TeamProp teamProp = propManager.getTeamProp(teamId);
      TeamMoney teamMoney = teamMoneyManager.getTeamMoney(teamId);
      List<PropSimple> needList = Lists.newArrayList();
      if (bean.getNeedPropList() != null && bean.getNeedPropList().size() > 0) {
        needList.addAll(bean.getNeedPropList());
      }
      // 突破上限消耗
      if (match.getSignNum() + 1 > bean.getMaxTeam() && bean.getBeyondPropList() != null && bean.getBeyondPropList().size() > 0) {
        needList = bean.getBeyondPropList();
      }
      if (needList.size() > 0 && !CheckAPI.checkTeamPropNum(needList, teamProp, teamMoney)) {
        log.debug("报名已满，突破报名数道具不足");
        return ErrorCode.Prop_0;
      }
      synchronized (match) {
        // 已报名
        if (match.isSign(teamId)) {
          log.debug("已报过名");
          return ErrorCode.Match_4;
        }
        // 判断人数
        if (match.getSignNum() + 1 > bean.getBeyondMax()) {
          log.debug("报名人数已满");
          return ErrorCode.Match_1;
        }
        // 扣道具
        propManager.usePropOrMoney(teamId, needList, true, ModuleLog.getModuleLog(EModuleCode.多人赛, "报名-" + bean.getName()));
        // 报名传入战力值
        match.sign(teamId, teamManager.getTeamAllAbility(teamId).getTotalCap());
        if (bean.getType() == 2) {
          // 超快赛：是否自动补满NPC,这个判断只是为了超快赛方便测试
          if (bean.getAutoNpc() == 1 || autoNpc) {
            // 补NPC
            int npcNum = bean.getMaxTeam() - match.getSignNum();
            log.debug("多人赛[{}], 补发ＮＰＣ报名人数{}", match.getMatchId(), npcNum);
            List<Long> npcTeamList = NPCConsole.getSeqNpcId(npcNum, match.getSignNpcList(), KnockoutMatchConsole.NPC_Min_ID, KnockoutMatchConsole.NPC_Max_ID);
            for (long npcId : npcTeamList) {
              match.sign(npcId, 100);//-- NPC的战力值，补NPC时，100这个值没意义
            }
          }
          // 多人赛人满即开
          if (match.getSignNum() == bean.getMaxTeam()) {
            match.startPK();
          }
        }
        // 五分钟内刚好满下限的情况，推送赛前提示通知
        else if (match.getSignNum() == bean.getMinTeam()) {
          matchStartTopic(match, DateTime.now());
        }
        int dailySignupNum = addFastCupTodayFight(teamId);
        // 推包处理，超快赛直接推新的比赛包
        if (bean.getType() == 2 && match.getSignNum() == bean.getMaxTeam()) {
          // 已报满，推送老比赛列表，并开启新的
          sendMessageTeamIds(match.getSignTeamListNotNpc(), MatchMT.getMatchTopicData(match,dailySignupNum), ServiceCode.Match_Topic);
          KnockoutMatch newMatch = getFastMatchTheNewes(matchId);
          if (newMatch != null) {
            sendMessage(ServiceConsole.MatchList, match.getSignTeamListNotNpc(), MatchMT.getMatchTopicData(newMatch,dailySignupNum), ServiceCode.Match_Topic);
          }
        } else {
          // 其他比赛推包
          sendMessage(ServiceConsole.MatchList, MatchMT.getMatchTopicData(match,dailySignupNum), ServiceCode.Match_Topic);
        }
      }
      taskManager.updateTask(teamId, ETaskCondition.多人赛, 1, "");
      // 推送一下目标变化
      // gameManager.topicMyTarget(teamId);
      return ErrorCode.Success;
    }

    //    /**
    //     * 取消报名多人赛
    //     *
    //     * @param teamId
    //     * @param tid
    //     * @param seq
    //     */
    //    private void unSign(long teamId, int tid) {
    //        MatchSignSta sta = joinMathMap.get(teamId);
    //        if (sta == null) { return; }
    //        sta.removeSign(tid);
    //    }

    /**
     * 新手引导比赛
     */
    public void signHelpMatch(long teamId, int matchId) {
        // 特定ID，有报名就创建比赛。
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
        if (bean == null) {
            log.debug("比赛不存在，参数异常");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Match_2.code).build());
            return;
        }
        KnockoutMatch match = KnockoutMatch.createMatch(getSeqIdByMatch(matchId), matchId);
        //报名
        match.sign(teamId, teamManager.getTeamAllAbility(teamId).getTotalCap());
        // 补NPC
        if (bean.getAutoNpc() == 1) {
            int npcNum = bean.getMaxTeam() - match.getSignNum();
            log.debug("多人赛[{}], 补发ＮＰＣ报名人数{}", match.getMatchId(), npcNum);
            for (int i = 0; i < npcNum; i++) {
                match.sign(NPCConsole.getNPC((10001L + i)).getNpcId(), 100);
            }
        }
        if (match.getSignNum() == bean.getMaxTeam()) {
            match.startPK();
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }
    
    /**
     * gm添加超快赛npc
     * @param teamId
     * @param npcNum
     * @param cap
     */
    public void gmSignNpc(long teamId,int npcNum,int cap) {
      KnockoutMatch match = null;
      for(Entry<Integer, Map<Integer, KnockoutMatch>> entry : fastMatchMap.entrySet()) {
        for(Entry<Integer, KnockoutMatch> entrty1 : entry.getValue().entrySet()) {
          KnockoutMatch tempMatch = entrty1.getValue();
          KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(tempMatch.getMatchId());
          if(bean!= null && bean.getType() == 2 && tempMatch.getTeamSignMap().containsKey(teamId) && tempMatch.getTeamSignMap().get(teamId).isOut() == false) {
            match = tempMatch;
            break;
          }
        }
      }
      if(match != null) {
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(match.getMatchId());
        if(bean != null) {
          // 自动补满NPC
          synchronized (match) {
            npcNum = (npcNum + match.getSignNum()) > bean.getBeyondMax() ? bean.getBeyondMax() - match.getSignNum() : npcNum;
            log.debug("多人赛[{}], 补发ＮＰＣ报名人数{}", match.getMatchId(), npcNum);
            List<Long> npcTeamList = NPCConsole.getSeqNpcId(npcNum, match.getSignNpcList(), KnockoutMatchConsole.NPC_Min_ID, KnockoutMatchConsole.NPC_Max_ID);
            for (long npcId : npcTeamList) {
              match.sign(npcId, cap);//-- NPC的战力值，补NPC时，100这个值没意义
            }
          }
        }
        //
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        // 超快赛报名处理
        if (bean.getType() == 2 && match.getSignNum() == bean.getMaxTeam()) {
          // 多人赛人满即开
          match.startPK();
          // 已报满，推送老比赛列表，并开启新的
          for(long teamId0 : match.getSignTeamListNotNpc()) {
            int dailySignupNum = getFastCupTodayFight(teamId0);
            sendMessage(teamId0, MatchMT.getMatchTopicData(match,dailySignupNum), ServiceCode.Match_Topic);
          }
          KnockoutMatch newMatch = getFastMatchTheNewes(match.getMatchId());
          if (newMatch != null) {
            sendMessage(ServiceConsole.MatchList, match.getSignTeamListNotNpc(), MatchMT.getMatchTopicData(newMatch,0), ServiceCode.Match_Topic);
            return;
          }
        }
        // 其他比赛推包
        sendMessage(ServiceConsole.MatchList, MatchMT.getMatchTopicData(match,0), ServiceCode.Match_Topic);
      }
    }

    /**
     * NPC报名
     *
     * @param matchId
     * @param seqId
     * @param signNum 报名人数
     */
    @ClientMethod(code = ServiceCode.GMManager_Match_Sign_Npc)
    @UnCheck
    public void signNpc(int matchId, int seqId, int npcNum, int cap) {
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
        if (bean == null) {
            log.debug("比赛不存在，参数异常");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Match_2.code).build());
            return;
        }
        // 取当场比赛
        KnockoutMatch match = getMatchMap(matchId).get(seqId); ;
        if (match == null) {
            log.debug("没有找到比赛类型或者比赛已结束");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Match_2.code).build());
            return;
        }
        // 满人或者开始比赛
        if (match.getStatus() != EMatchStatus.开始报名.status) {
            log.debug("报名失败，已截止报名");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Match_3.code).build());
            return;
        }
        // 自动补满NPC
        synchronized (match) {
            npcNum = (npcNum + match.getSignNum()) > bean.getBeyondMax() ? bean.getBeyondMax() - match.getSignNum() : npcNum;
            log.debug("多人赛[{}], 补发ＮＰＣ报名人数{}", match.getMatchId(), npcNum);
            List<Long> npcTeamList = NPCConsole.getSeqNpcId(npcNum, match.getSignNpcList(), KnockoutMatchConsole.NPC_Min_ID, KnockoutMatchConsole.NPC_Max_ID);
            for (long npcId : npcTeamList) {
                match.sign(npcId, cap);//-- NPC的战力值，补NPC时，100这个值没意义
            }
        }
        //
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        // 超快赛报名处理
        if (bean.getType() == 2 && match.getSignNum() == bean.getMaxTeam()) {
            // 多人赛人满即开
            match.startPK();
            // 已报满，推送老比赛列表，并开启新的
            for(long teamId : match.getSignTeamListNotNpc()) {
              int dailySignupNum = getFastCupTodayFight(teamId);
              sendMessage(teamId, MatchMT.getMatchTopicData(match,dailySignupNum), ServiceCode.Match_Topic);
            }
            KnockoutMatch newMatch = getFastMatchTheNewes(matchId);
            if (newMatch != null) {
                sendMessage(ServiceConsole.MatchList, match.getSignTeamListNotNpc(), MatchMT.getMatchTopicData(newMatch,0), ServiceCode.Match_Topic);
                return;
            }
        }
        // 其他比赛推包
        sendMessage(ServiceConsole.MatchList, MatchMT.getMatchTopicData(match,0), ServiceCode.Match_Topic);
    }

    /**
     * 取最新的超快赛场次 未开始比赛，且人数未满
     *
     * @return
     */
    private synchronized KnockoutMatch getFastMatchTheNewes(int matchId) {
        Map<Integer, KnockoutMatch> map = getMatchMap(matchId);
        if (map.size() == 0) {
            // 没有空余场次， 创建新的
            KnockoutMatch match = KnockoutMatch.createMatch(getSeqIdByMatch(matchId), matchId);
            map.put(match.getSeqId(), match);
            return match;
        }
        //
        for (KnockoutMatch match : map.values()) {
            if (match.getStatus() == EMatchStatus.开始报名.status) {
                return match;
            }
        }
        // 没有空余场次， 创建新的
        KnockoutMatch match = KnockoutMatch.createMatch(getSeqIdByMatch(matchId), matchId);
        map.put(match.getSeqId(), match);
        return match;
    }

    /**
     * 取指定的比赛场次
     *
     * @param matchId
     * @param seqId
     * @return 没有检查seqId是否同一场次
     */
    private KnockoutMatch getMatch(int matchId, int seqId) {
        KnockoutMatch match = getMatchMap(matchId).get(seqId); ;
        return match;
    }

    //------------------------比赛状态变更，主动推包----------------------------

    /**
     * 检查是否有要开始的比赛
     */
    public void checkStartPK(DateTime dateTime) {
        for (KnockoutMatch match : getKnockoutMatchList()) {
            KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(match.getMatchId());
            // 配置移除的比赛不开始
            if (bean == null) {
                continue;
            }
            if ((match.getStatus() == EMatchStatus.结束.status)) {
                continue;
            }
            // 刚好到点比赛
            else if (match.getStatus() == EMatchStatus.开始报名.status
                && dateTime.withSecondOfMinute(0).withMillisOfSecond(0)
                .isEqual(match.getMatchTime().withSecondOfMinute(0).withMillisOfSecond(0))) {
                match.startPK();
            }
            // 过期30min后，延顺比赛处理
            else if (match.getStatus() == EMatchStatus.开始报名.status && dateTime.isAfter(match.getMatchTime().plusMinutes(30))) {
                log.debug("多人赛[{}]，延顺报名周期", match.getMatchId());
                match.setMatchTime(KnockoutMatch.getStartMatchTime(bean));
                match.save();
            }
            // 开始比赛，特殊原因进程被中断
            else if (match.getStatus() == EMatchStatus.异常.status
                && match.getStartPKFailTime() != null
                && !match.isDelayThread()
                && match.getStartPKCount() < 3
                && match.getThreadStatus() == Thread.State.TERMINATED
                && DateTime.now().isAfter(match.getStartPKFailTime().plusMinutes(10))) {
                // 异常10分钟后，重新开始比赛
                match.startPK();
            } else if (match.getStatus() == EMatchStatus.比赛中.status && dateTime.isAfter(match.getMatchTime().plusHours(3))) {
                // 3个小时没打完，自动移除
                match.setStatus(EMatchStatus.结束.status);
                match.save();
            }
        }
    }

    /**
     * 检查超时的比赛，一般只有超快赛
     */
    private void checkFastTimeOut(DateTime now) {
        // 多人赛卡比赛，如果是人满即开，如果满了，就开始比赛。
        Set<KnockoutMatch> removeFastMatch = Sets.newHashSet();
        for (KnockoutMatch fm : getFastMatchList()) {
            KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(fm.getMatchId());
            if (fm.getSignNum() < bean.getMaxTeam()) {
                continue;
            }
            // 失败的比赛
            else if (fm.getStartPKCount() >= 2) {
                removeFastMatch.add(fm);
                continue;
            }
            // 超时的比赛
            else if (fm.getStartPKTime() != null && fm.getStartPKTime().plusHours(1).isBeforeNow()) {
                // 超时的比赛也处理掉
                removeFastMatch.add(fm);
            }
        }
        // 有要销毁的多人赛
        for (KnockoutMatch fm : removeFastMatch) {
            // 取消其报名，设置状态为结束
            fm.setStatus(EMatchStatus.结束.status);
            removeMatch(fm.getMatchId(), fm.getSeqId());
            log.warn("释放卡多人赛的比赛[{}], signNum={}, signTeam={}", fm.getName(), fm.getSignNum(), fm.getSignTeamList());
        }
    }

    /**
     * 战斗每回合回调处理
     *
     * @param bs
     * @param report
     * @param match
     */
    public void roundReport(BattleSource bs, RoundReport report, KnockoutMatch match) {
        if (report.getAction() == null) {
            return;
        }
        localBattleManager.roundReport(bs, report);
        // 每20个回合推送一次。
        if (report.getRoundOfStep() == 1
            || report.isEnd()
            || report.getRoundOfStep() % KnockoutMatchConsole.Match_Round_Push_Num == 0) {
            pushMatchPKList(match);
        }
    }

    /**
     * 推送消息，回合切换推送，回合比分推送
     *
     * @param match
     */
    public void pushMatchPKList(KnockoutMatch match) {
        sendMessage(ServiceConsole.getMatchDetailKey(match.getMatchId(), match.getSeqId()),
            MatchMT.getMatchDetailData(match, leagueManager), ServiceCode.Match_Detail_Topic);
    }

    /**
     * 比赛结束
     */
    public void matchEnd(KnockoutMatch match, List<MatchSign> joinPKTeamList) {
        int matchId = match.getMatchId();
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
        // 移除本场参赛者
        //        for (MatchSign sign : joinPKTeamList) {
        //            if (!joinMathMap.containsKey(sign.getTeamId())) {
        //                continue;
        //            }
        //            unSign(sign.getTeamId(), bean.getTid());
        //        }
        //		// 创建新的或者保留老的报名
        //		if(bean.getType() == 1) {
        //			matchMap.put(matchId, KnockoutMatch.createMatch(getSeqIdByMatch(matchId), matchId,DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Battle_KnockoutMatch_Win_Drop))
        //					,DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Battle_KnockoutMatch_Lose_Drop))));
        //		}
        // 超快赛直接创建
        if (bean.getType() == 2) {
            // 移除多人赛已打完的记录
            removeMatch(matchId, match.getSeqId());
            // 给本比赛类型已报名玩家推送新的比赛场次
            KnockoutMatch newMatch = getFastMatchTheNewes(matchId);
            for(long teamId : match.getSignTeamListNotNpc()) {
              int dailySignupNum = getFastCupTodayFight(teamId);
              sendMessage(teamId, MatchMT.getMatchTopicData(newMatch,dailySignupNum), ServiceCode.Match_Topic);
            }
        }
        // 历史最佳， 临时捕获处理
        if (matchId != 100) {
            this.updateBestRank(matchId, joinPKTeamList);
        }
    }

    /**
     * 赛前提醒推包,定时推送
     *
     * @param dateTime
     */

    public void matchBeforeTopic(DateTime dateTime) {
        // 即将开始的多人赛，正式报名的人员，推送通知的
        for (KnockoutMatch match : getKnockoutMatchList()) {
            if (match.isStartTipTopic()) {
                continue;
            }
            matchStartTopic(match, dateTime);
        }
    }

    /**
     * 0点检查是否创建新比赛，只限周期赛
     *
     * @param dateTime
     */
    public void checkCreateMatch(DateTime dateTime) {
        if (dateTime.getHourOfDay() != 0 || dateTime.getMinuteOfHour() != 0) {
            return;
        }
        resetCreateMatch();
    }

    /**
     * 检测开启多人赛，0点自动创建已结束的常规赛
     */
    public void resetCreateMatch() {
        for (KnockoutMatch match : getKnockoutMatchList()) {
            int matchId = match.getMatchId();
            Map<Integer, KnockoutMatch> map = getMatchMap(matchId);
            if (match.getStatus() == EMatchStatus.结束.status) {
                map.remove(match.getSeqId());
            }
            //
            KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
            if (bean == null) {
                continue;
            }
            // 创建新的
            if (map.size() < getGameMax(bean)) {
                KnockoutMatch nMatch = KnockoutMatch.createMatch(getSeqIdByMatch(matchId), matchId);
                map.put(nMatch.getSeqId(), nMatch);
            }
        }
    }

    /**
     * 多人赛开始通知
     *
     * @param match
     * @param dateTime
     */

    private void matchStartTopic(KnockoutMatch match, DateTime dateTime) {
        int min = Minutes.minutesBetween(dateTime, match.getMatchTime()).getMinutes();
        // 赛前五分钟
        if (match.isMustStart() && min > 0 && min <= KnockoutMatchConsole.Match_Start_Tip_Min) {
            //
            match.setStartTipTopic(true);
            //
            List<User> users = match.getJoinPKTeamList().stream()
                .filter(s -> !NPCConsole.isNPC(s.getTeamId()) && GameSource.isOline(s.getTeamId()))
                .map(m -> GameSource.getUser(m.getTeamId())).collect(Collectors.toList());
            //
            if (users != null && users.size() > 0) {
                sendMessage(users, MatchMT.getMatchStartTipData(match, min), ServiceCode.Match_Before_Topic);
            }
        }
    }

    /**
     * 用户登录
     * 多人赛赛前提醒
     *
     * @param teamId
     */
    public void loginMatchStartTipTopic(long teamId) {
        // 上线里比赛开始时间小于五分钟，且已经推了所有在线通知包，那就提醒玩家
        // 取用户所有参加的比赛
        for (KnockoutMatch match : getKnockoutMatchList()) {
            if (!match.isSign(teamId)) {
                continue;
            }
            int min = Minutes.minutesBetween(DateTime.now(), match.getMatchTime()).getMinutes();
            log.debug("用户上线,推送多人赛开始通知, 分钟:{}", min);
            if (match.isMustStart() && min > 0 && min <= KnockoutMatchConsole.Match_Start_Tip_Min) {
                sendMessage(teamId, MatchMT.getMatchStartTipData(match, min), ServiceCode.Match_Before_Topic);
            }
        }
    }

    /** 比赛结束推包 */
    public void topicMatchReport(MatchSign matchSign) {
        // NPC不处理
        if (NPCConsole.isNPC(matchSign.getTeamId())) {
            return;
        }
        // 发送奖励
        int drop = KnockoutMatchConsole.getMatchDropByRank(matchSign.getMatchId(), matchSign.getRank());
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchSign.getMatchId());
        // 取排名奖励,发送至邮箱.
        String awardConfig = "";
        if (DropConsole.containsKey(drop)) {
            awardConfig = PropSimple.getPropStringByListNotConfig(DropConsole.getDrop(drop).roll());
        }
        // 判断是否发邮件
        if (bean.getEmailId() != 0) {
            EmailViewBean emailBean = EmailConsole.getEmailView(bean.getEmailId());
            String content = emailParams(bean.getName(), matchSign.getRank() + "");
            emailManager.sendEmailFinal(matchSign.getTeamId(), emailBean.getType(), emailBean.getId(),
                emailBean.getTitle(), content, awardConfig);
        }

        if (matchSign.getRank() <= 0) {
            log.error("knockout award. ms tid {} rank {}", matchSign.getTeamId(), matchSign.getRank());
        }
        taskManager.updateTask(matchSign.getTeamId(), ETaskCondition.多人赛名次, 1, bean.getTid() + "," + matchSign.getRank());
        log.info("knockout award. drop {} mail {} award {} ms {}", drop, bean.getEmailId(), awardConfig, matchSign);
        sendMessage(matchSign.getTeamId(), MatchMT.getMatchReportData(matchSign), ServiceCode.Match_Report);
    }

    private String emailParams(String... params) {
        StringBuilder sb = new StringBuilder();
        for (String s : params) {
            sb.append(s).append(",");
        }
        return sb.toString();
    }

    //    /**
    //     * 玩家报名比赛的状态，重新加载已报名的比赛，要初始化
    //     * 格式： tid : [matchId, seqId]
    //     * @author Jay
    //     * @time:2018年5月11日 上午11:23:41
    //     */
    //    private static class MatchSignSta {
    //        private Map<Integer, int[]> signMap;
    //
    //		private boolean containsKey(int tid) {
    //            return signMap.containsKey(tid);
    //        }
    //
    //        private void putSign(int tid, int id, int seqId) {
    //            signMap.put(tid, new int[]{id, seqId});
    //        }
    //
    //        private void removeSign(int tid) {
    //            signMap.remove(tid);
    //        }
    //
    //        public MatchSignSta() {
    //            signMap = Maps.newConcurrentMap();
    //        }
    //
    //        /**
    //         * tid + id
    //         */
    //        public Integer getMatchId(int tid) {
    //            if (!signMap.containsKey(tid)) { return 0; }
    //            return tid + getId(tid);
    //        }
    //
    //        public Integer getId(int tid) {
    //            return signMap.get(tid)[0];
    //        }
    //
    //        public Integer getSeqId(int tid) {
    //            return signMap.get(tid)[1];
    //        }
    //    }

    @Override
    public int getOrder() {
        return ManagerOrder.KnockoutMatch.getOrder();
    }

    /**
     * 是否需要推送第一名跑马灯
     *
     * @param teamId
     * @param matchId
     */
    public void matchEndGameTip(long teamId, int matchId) {
        // 第一名跑马灯
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
        if (bean.getType() != 1) {
            return;
        }
        String teamName = NPCConsole.isNPC(teamId) ? NPCConsole.getNPC(teamId).getNpcName() : teamManager.getTeamNameById(teamId);
        chatManager.pushGameTip(EGameTip.多人赛冠军, 0, teamName);
    }

    /**
     * 重置多人赛，结束当前场次，退出报名
     *
     * @param matchId
     * @param seqId
     */
    public void resetMatch(int matchId, int seqId) {
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
        KnockoutMatch match = getMatchMap(matchId).get(seqId);
        if (match == null) {
            return;
        }
        // 结束状态，抛弃进程，移除报名
        match.setStatus(EMatchStatus.结束.status);
        if (bean.getType() == 2) {
            removeMatch(matchId, seqId);
        } else if (bean.getType() == 1) {
            /**
             * 重置比赛,超快赛有自动处理机制
             * 所以只支持周赛制就行了
             */
            initMatch(matchAO.getMatchPO(seqId, matchId));
        }
    }

    /**
     * 奖杯超快赛今天挑战次数
     * @return
     */
    public int getFastCupTodayFight(long teamId) {
      String key = RedisKey.getDayKey(teamId, RedisKey.Fast_cup_Day);
      int value = redis.getIntNullIsZero(key);
      log.debug("奖杯超快赛{}今天挑战次数：{}", teamId, value);
      return value;
    }

    /**
     * 自增奖杯超快赛挑战次数
     */
    public int addFastCupTodayFight(long teamId) {
      setFastCupTodayFight(teamId, getFastCupTodayFight(teamId) + 1);
      return getFastCupTodayFight(teamId);
    }

    /**
     * 保存奖杯超快赛挑战次数
     * @param teamId
     * @param num
     */
    public void setFastCupTodayFight(long teamId, int num) {
      String key = RedisKey.getDayKey(teamId, RedisKey.Fast_cup_Day);
      redis.set(key, ""+num, RedisKey.DAY);
    }
    
}
