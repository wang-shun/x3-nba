package com.ftkj.manager.logic;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.AllStarBean;
import com.ftkj.cfg.AllStarBean.AwardBean;
import com.ftkj.cfg.AllStarBean.NpcBean;
import com.ftkj.cfg.AllStarBean.ScorePersonalAward;
import com.ftkj.cfg.EmailViewBean;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.console.AllStarConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.User;
import com.ftkj.manager.battle.handle.BattleAllStar;
import com.ftkj.manager.battle.handle.BattleAllStar.BattleAttr;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.AllStarMatchEnd;
import com.ftkj.manager.logic.LocalBattleManager.BattleContxt;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.match.SysAllStar;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.AllStarPb.AllStarAllResp;
import com.ftkj.proto.AllStarPb.AllStarKillRankResp;
import com.ftkj.proto.AllStarPb.AllStarNpcResp;
import com.ftkj.proto.AllStarPb.AllStarRankResp;
import com.ftkj.proto.AllStarPb.AllStarTeamRankResp;
import com.ftkj.proto.AllStarPb.KillReward;
import com.ftkj.proto.AllStarPb.ScoreReward;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.quartz.QuartzServer;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.tool.redis.JedisUtil.Tuple;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * 挑战全明星
 *
 * @author luch
 */
public class AllStarManager extends AbstractBaseManager {
  private static final Logger log = LoggerFactory
      .getLogger(AllStarManager.class);
  private static final int Rank_Num = 20;
  @IOC
  private TeamManager teamManager;
  @IOC
  private LocalBattleManager localBattleManager;
  @IOC
  private JedisUtil redis;
  @IOC
  private TeamStatusManager teamStatusManager;
  @IOC
  private TeamEmailManager teamEmailManager;
  @IOC
  private ChatManager chatManager;
  @IOC
  private PropManager propManager;
  @IOC
  private TeamNumManager teamNumManager;

  /**
   * 系统全明星信息 key:全明星配置ID
   */
  private ConcurrentMap<Integer, SysAllStar> sys = new ConcurrentHashMap<>();

  /**
   * 击杀记录 key:全明星配置ID value:玩家ID
   */
  private Map<Integer, Long> killMap = null;

  /**
   * 默认第一个全明星球队配置ID
   */
  private static final int firstAllStarId = 1;

  // ------------------------------------------------------客户端请求-----------------------------------------------------
  /** 获取信息 */
  @ClientMethod(code = ServiceCode.AllStar_Info)
  public void info(int allStarRid) {
    long teamId = getTeamId();
    ErrorCode ret = info0(teamId, allStarRid);
    sendMsg(ret);
    log.debug("allstar info. tid {} rid {} ret {}", teamId, allStarRid, ret);
  }

  private ErrorCode info0(long teamId, int allStarRid) {
    SysAllStar sas = getSys(allStarRid);
    if (sas == null) {
      return ErrorCode.AllStar_Null;
    }
    AllStarAllResp resp = allResp(teamId, allStarRid, sas);
    sendMessage(teamId, resp, ServiceCode.AllStar_Info_Push);
    return ErrorCode.Success;
  }

  /** 开始比赛 */
  @ClientMethod(code = ServiceCode.AllStar_Start_Match)
  public void startMatch(int allStarRid) {
    long teamId = getTeamId();
    ErrorCode ret = startMatch0(teamId, allStarRid);
    sendMsg(ret);
    log.debug("allstar startMatch. tid {} rid {} ret {}", teamId, allStarRid,
        ret);
  }

  /** 获取npc状态 */
  @ClientMethod(code = ServiceCode.AllStar_Npc)
  public void npc(int allStarRid) {
    long teamId = getTeamId();
    ErrorCode ret = npc0(teamId, allStarRid);
    sendMsg(ret);
    log.debug("allstar npc. tid {} rid {} ret {}", teamId, allStarRid, ret);
  }

  private ErrorCode npc0(long teamId, int allStarRid) {
    SysAllStar sas = getSys(allStarRid);
    if (sas == null) {
      return ErrorCode.AllStar_Null;
    }
    AllStarNpcResp npc = npcResp(sas, teamId);
    sendMessage(teamId, npc, ServiceCode.AllStar_Npc_Push);
    return ErrorCode.Success;
  }

  /** 获取我的全明星赛击杀奖励信息 */
  @ClientMethod(code = ServiceCode.AllStar_Kill_Reward)
  public void allStarKillReward() {
    long teamId = getTeamId();
    ErrorCode ret = allStarKillReward0(teamId);
    sendMsg(ret);
    log.debug("allStarKillReward info. tid {} ret {}", teamId, ret);
  }

  /** 获取我的全明星赛个人积分奖励信息 */
  @ClientMethod(code = ServiceCode.AllStar_Score_Reward)
  public void AllStarScoreReward() {
    long teamId = getTeamId();
    ErrorCode ret = allStarScoreReward0(teamId);
    sendMsg(ret);
    log.debug("AllStarScoreReward info. tid {} ret {}", teamId, ret);
  }

  /** 今日挑战的全明星队 */
  @ClientMethod(code = ServiceCode.AllStar_Today_Npc)
  public void allStarTodayNpc() {
    long teamId = getTeamId();
    ErrorCode ret = null;
    SysAllStar star = toDaySysAllStar();
    if (star == null) {
      ret = ErrorCode.AllStar_Null;
    }
    if (ret == null && star != null) {
      ret = info0(teamId, star.getId());
    }
    sendMsg(ret);
    log.debug("allstar allStarTodayNpc. tid {} rid {} ret {}", teamId, ret);
  }

  /** 领取我的全明星赛击杀奖励 */
  @ClientMethod(code = ServiceCode.AllStar_Get_Kill_Reward)
  public void allStarGetKillReward(int allStarRid, int level) {
    long teamId = getTeamId();
    ErrorCode ret = allStarGetKillReward0(teamId, allStarRid, level);
    sendMsg(ret);
    log.debug("allstar allStarGetKillReward. tid {} rid {} ret {}", teamId,
        allStarRid, ret);
  }

  private ErrorCode allStarGetKillReward0(long teamId, int allStarRid,
      int level) {
    SysAllStar star = getSys(allStarRid);
    if (star == null) {
      return ErrorCode.AllStar_Null;
    }
    if (level > star.getNpcLev()) {
      return ErrorCode.AllStar_Kill_Reward_Level;
    }
    if (star.getNpcLev() == level && star.getHp() > 0) {
      return ErrorCode.AllStar_Kill_Reward_Level;
    }
    if (checkRewardIsGet(teamId, level, RedisKey.All_Star_Kill_Reward_Day)) {
      return ErrorCode.AllStar_Kill_Reward_Has_Get;
    }
    int myScore = getDayNum(teamId, RedisKey.All_Star_Team_Score);
    if (myScore < ConfigConsole.getGlobal().allStarKillRewardBaseScore) {
      return ErrorCode.AllStar_Kill_Reward_Base_Score;
    }
    final AllStarBean asb = AllStarConsole.getBean(allStarRid);
    NpcBean npc = asb.getNpc(level);
    List<PropSimple> rewards = PropSimple
        .getPropBeanByStringNotConfig(npc.getKillServerReward());
    propManager.addPropList(teamId, rewards, true,
        ModuleLog.getModuleLog(EModuleCode.全明星赛本服击杀奖, "全明星赛本服击杀奖"));
    setRewardGet(teamId, level, RedisKey.All_Star_Kill_Reward_Day);
    allStarKillReward0(teamId);
    return ErrorCode.Success;
  }

  /** 全明星赛-领取个人积分奖励 */
  @ClientMethod(code = ServiceCode.AllStar_Get_Score_Reward)
  public void allStarGetScoreReward(int score) {
    long teamId = getTeamId();
    ErrorCode ret = allStarGetScoreReward0(teamId, score);
    sendMsg(ret);
    log.debug("allstar allStarGetScoreReward. tid {} score {} ret {}", teamId,
        score, ret);
  }

  private ErrorCode allStarGetScoreReward0(long teamId, int score) {
    int myScore = getDayNum(teamId, RedisKey.All_Star_Team_Score);
    if (checkRewardIsGet(teamId, score, RedisKey.All_Star_Score_Reward_Day)) {
      return ErrorCode.AllStar_Score_Reward_Has_Get;
    }
    if(myScore < score) {
      return ErrorCode.AllStar_Have_No_Score_Reward;
    }
    List<PropSimple> rewards = AllStarConsole.getScorePersonalAward(score);
    if (rewards == null) {
      return ErrorCode.AllStar_Have_No_Score_Reward;
    }
    propManager.addPropList(teamId, rewards, true,
        ModuleLog.getModuleLog(EModuleCode.全明星赛本服积分奖, "全明星赛本服积分奖"));
    setRewardGet(teamId, score, RedisKey.All_Star_Score_Reward_Day);
    allStarScoreReward0(teamId);
    return ErrorCode.Success;
  }

  /** 全明星赛-购买挑战次数 */
  @ClientMethod(code = ServiceCode.AllStar_Buy_Challenge_Num)
  void buyMatchNum() {
    long tid = getTeamId();
    ErrorCode ret = buyMatchNum0(tid, 1);
    sendMsg(ret);
    log.debug("arena buyMatchNum. tid {} num {} ret {}", tid, 1, ret);
  }

  private ErrorCode buyMatchNum0(long tid, int num) {
    LocalTime now = LocalTime.now();
    if (now.isAfter(endTime2())) {
      return ErrorCode.AllStar_Can_Not_Buy_Time_Limit;
    }
    ErrorCode ret = teamNumManager.consumeNumCurrency(tid,
        TeamNumType.All_Star_Buy_Num, num,
        ModuleLog.getModuleLog(EModuleCode.AllStar, "购买挑战次数"));
    if (ret.isError()) {
      return ret;
    }
    SysAllStar star = toDaySysAllStar();
    npc0(tid, star.getId());
    return ErrorCode.Success;
  }

  /** 全明星赛-激励次数 */
  @ClientMethod(code = ServiceCode.AllStar_Jili_Num)
  void allStarJiliNum() {
    long tid = getTeamId();
    ErrorCode ret = allStarJiliNum(tid);
    sendMsg(ret);
    log.debug("arena allStarJiliNum. tid {} ret {}", tid, ret);
  }

  private ErrorCode allStarJiliNum(long tid) {
    LocalTime now = LocalTime.now();
    if (now.isAfter(endTime2())) {
      return ErrorCode.AllStar_Can_Not_Jili_Time_Limit;
    }
    ErrorCode ret = teamNumManager.consumeNumCurrency(tid,
        TeamNumType.All_Star_Jili_Num, 1,
        ModuleLog.getModuleLog(EModuleCode.AllStar, "全明星赛激励"));
    if (ret.isError()) {
      return ret;
    }
    SysAllStar star = toDaySysAllStar();
    npc0(tid, star.getId());
    return ErrorCode.Success;
  }

  // ---------------------------------------------------------------------------

  /**
   * 系统给次数
   * 
   * @return
   */
  private int sysGiveNum() {
    String allStarSendChallengeNum = ConfigConsole
        .getGlobal().allStarSendChallengeNum;
    String[] numArr = allStarSendChallengeNum.split(",");
    String[] numStr1 = numArr[0].split("-");
    String[] numStr2 = numArr[1].split("-");
    String time1Str = numStr1[0];
    String time2Str = numStr2[0];
    String num1Str = numStr1[1];
    String num2Str = numStr2[1];
    LocalTime now = LocalTime.now();
    LocalTime time1 = LocalTime.parse(time1Str,
        DateTimeFormatter.ISO_LOCAL_TIME);
    LocalTime time2 = LocalTime.parse(time2Str,
        DateTimeFormatter.ISO_LOCAL_TIME);
    int num = 0;
    if (now.isAfter(time1) && now.isBefore(time2)) {
      num = Integer.parseInt(num1Str);
    }
    if (now.isAfter(time2)) {
      num = Integer.parseInt(num1Str) + Integer.parseInt(num2Str);
    }
    return num;
  }
  
  public int getLimitNum() {
    String allStarSendChallengeNum = ConfigConsole
        .getGlobal().allStarSendChallengeNum;
    String[] numArr = allStarSendChallengeNum.split(",");
    String[] numStr1 = numArr[0].split("-");
    String num1Str = numStr1[1];
    int num = Integer.parseInt(num1Str);
    return num;
  }

  /**
   * 是否可以比赛
   * 
   * @param allStarId
   * @return
   */
  public boolean canMatch(int allStarId) {
    LocalTime now = LocalTime.now();
    SysAllStar sysAllStar = sys.get(allStarId);
    if (sysAllStar == null) {
      return false;
    }
    if (now.isAfter(openTime1()) && now.isBefore(endTime1())) {
      return true;
    }
    if (now.isAfter(openTime2()) && now.isBefore(endTime2())) {
      return true;
    }
    return false;
  }

  /**
   * 第一个开启时间
   * 
   * @return
   */
  public LocalTime openTime1() {
    return getAllStarTime(0, 0);
  }

  /**
   * 第二个开启时间
   * 
   * @return
   */
  public LocalTime openTime2() {
    return getAllStarTime(1, 0);
  }

  /**
   * 第一个结束时间
   * 
   * @return
   */
  public LocalTime endTime1() {
    return getAllStarTime(0, 1);
  }

  /**
   * 第二个结束时间
   * 
   * @return
   */
  public LocalTime endTime2() {
    return getAllStarTime(1, 1);
  }

  /**
   * 发奖时间
   * 
   * @return
   */
  public LocalTime rewardTime() {
    String allStarSendRewardTime = ConfigConsole
        .getGlobal().allStarSendRewardTime;
    LocalTime localTime = LocalTime.parse(allStarSendRewardTime,
        DateTimeFormatter.ISO_LOCAL_TIME);
    return localTime;
  }

  /**
   * 全明星赛时间
   * 
   * @param timeNO
   *          0-第一个时间段；1-第二个时间段
   * @param timeType
   *          0-开启时间；1-结束时间
   * @return
   */
  private LocalTime getAllStarTime(int timeNO, int timeType) {
    String allStarTime = ConfigConsole.getGlobal().allStarTime;
    String[] times = allStarTime.split(",");
    String timeStr = times[timeNO];
    String[] time = timeStr.split("-");
    String theTime = time[timeType];
    LocalTime localTime = LocalTime.parse(theTime,
        DateTimeFormatter.ISO_LOCAL_TIME);
    return localTime;
  }

  /** 系统启动时, 初始化定时器, 系统数据 */
  @Override
  public void instanceAfter() {
    // 系统启动时,加载系统数据
    boolean hasTodayAllStar = false;
    for (AllStarBean asb : AllStarConsole.getAllStars().values()) {
      SysAllStar sas = redis.getObj(RedisKey.All_Star_Sys + asb.getId());
      if (sas == null) {
        sas = new SysAllStar();
        sas.setId(asb.getId());
        initSys(sas, asb);
        redis.set(RedisKey.All_Star_Sys + asb.getId(), sas);
      } else {
        log.info("allstar init. load sys {} {}", asb.getId(), sas);
      }
      sys.put(sas.getId(), sas);
      if (hasTodayAllStar) {
        // 如果当前有1个以上激活的全明星队，则纠正
        if (sas.isActivate()) {
          sas.setActivate(false);
          sas.setHasSendedReward(false);
        }
      } else {
        hasTodayAllStar = sas.isActivate();
      }
    }

    if (hasTodayAllStar == false) {
      SysAllStar sysAllStar = sys.get(firstAllStarId);
      if (sysAllStar != null) {
        sysAllStar.setActivate(true);
        sysAllStar.setHasSendedReward(false);
      } else {
        log.error("SysAllStar is null id:{}", firstAllStarId);
      }
    }

    //初始化发奖励任务
    SysAllStar star = toDaySysAllStar();
    LocalTime now = LocalTime.now();
    if(now.isAfter(rewardTime()) && star.isHasSendedReward() == false) {
      award();
    }
    long midnight = DateTimeUtil.midnight();
    long curr = System.currentTimeMillis();
    long awardTime = rewardTime().toNanoOfDay() / 1000_000;
    long rewardDelay = midnight + awardTime - curr;
    if (rewardDelay < 0) {
      Delayed sd = QuartzServer.scheduleAtFixedRate(() -> award(),
            rewardDelay + DateTimeUtil.DAILY, DateTimeUtil.DAILY, TimeUnit.MILLISECONDS);
        log.info("allstar init. rid {} reward task postpone. delay[{}]", star.getId(), DateTimeUtil.duration(sd));
    } else {
      Delayed sd = QuartzServer.scheduleAtFixedRate(() -> award(),
          rewardDelay, DateTimeUtil.DAILY, TimeUnit.MILLISECONDS);
      log.info("allstar init. rid {} reward task postpone. delay[{}]", star.getId(), DateTimeUtil.duration(sd));
    }
    
  }

  /**
   * 选出今日全明星队
   * 
   * @return
   */
  private SysAllStar selectTodayAllStar() {
    AllStarBean asb = null;
    for (SysAllStar star : sys.values()) {
      if (star.isActivate()) {
        asb = AllStarConsole.getAllStars().get(star.getId() + 1);
        if (asb == null) {
          asb = AllStarConsole.getAllStars().get(firstAllStarId);
        }
        star.setActivate(false);
        star.setHasSendedReward(false);
        break;
      }
    }
    if (asb != null) {
      SysAllStar star = sys.get(asb.getId());
      if (star != null) {
        star.setActivate(true);
        star.setHasSendedReward(false);
        return star;
      } else {
        log.error("SysAllStar is null id:{}", asb.getId());
      }
    } else {
      log.error("AllStarBean is null");
    }
    return null;
  }

  /**
   * 零点重置
   */
  public void zeroReset() {
    SysAllStar star = toDaySysAllStar();
    if(star != null) {
      clearScoreRank(star.getId());
    }
    killMap = null;
    SysAllStar sysAllStar = selectTodayAllStar();
    if (sysAllStar != null) {
      resetSys(sysAllStar);
      //推送
      for(User user : GameSource.getUsers()) {
        info0(user.getTeamId(),sysAllStar.getId());
      }
    }
  }

  void shutdown() {
    for (SysAllStar sas : sys.values()) {
      saveAllStar(sas);
    }
  }
  
  private void saveAllStar(SysAllStar sas) {
    redis.set(RedisKey.All_Star_Sys + sas.getId(), sas);
    log.info("allstar stop. save sys {}", sas);
  }

  // /** 全明星开始. 初始化奖励定时器 */
  // private void start(int allStarRid) {
  // log.info("allstar starttast. rid {} start", allStarRid);
  // AllStarBean asb = AllStarConsole.getBean(allStarRid);
  // if (asb == null) {
  // return;
  // }
  // SysAllStar sas = getSys(allStarRid);
  // if (sas == null) {
  // sas = new SysAllStar();
  // sas.setId(allStarRid);
  // sys.put(sas.getId(), sas);
  // }
  // initSys(sas, asb);
  //
  // long midnight = DateTimeUtil.midnight();
  // long curr = System.currentTimeMillis();
  // long awardTime = rewardTime().toNanoOfDay() / 1000_000;
  // long awardDelay = midnight + awardTime - curr;
  // if (awardDelay > 0) {//没有错过奖励时间, 只触发一次
  // Delayed ad = QuartzServer.schedule(() -> award(asb.getId()), awardDelay,
  // TimeUnit.MILLISECONDS);
  // log.info("allstar starttask. rid {} award task. delay[{}]", asb.getId(),
  // DateTimeUtil.duration(ad));
  // } else {
  // log.warn("allstar starttask. rid {} award task. delay <= 0", asb.getId());
  // }
  // }

  /** 重置系统信息 */
  private void resetSys(SysAllStar sas) {
    if (sas == null) {
      return;
    }
    AllStarBean asb = AllStarConsole.getBean(sas.getId());
    if (asb == null) {
      return;
    }
    initSys(sas, asb);
  }

  private void initSys(SysAllStar sas, AllStarBean asb) {
    // List<EActionType> acts = asb.getActHps().keySet().asList();
    // int rnd = ThreadLocalRandom.current().nextInt(acts.size());
    // sas.setAct(acts.get(rnd));
    sas.setNpcLev(AllStarBean.Npc_Lev_Start);
    NpcBean npc = asb.getNpc(sas.getNpcLev());
    sas.setHp(Math.max(1, npc.getMaxHp()));
    redis.set(RedisKey.All_Star_Sys + asb.getId(), sas);
    log.info("allstar initsys. sys {}", sas);
  }

  /** 奖励结算. 结算完毕后清空排行榜 */
  private void award() {
    try {
      award0();
    } catch (Exception e) {
      log.error("allstar award " + e.getMessage(), e);
    }
  }
  
  private void clearScoreRank(int allStarRid) {
    String key = getRankKey(allStarRid);
    long count = redis.zremrangeByRank(key, 0, -1);
    log.info("allstar award final. rid {} delete rank num {}", allStarRid,
        count);
    resetSys(getSys(allStarRid));
  }

  /** 奖励结算 */
  private synchronized void award0() {
    SysAllStar star = toDaySysAllStar();
    if(star == null) {
      return;
    }
    int allStarRid = star.getId();
    log.info("allstar award0 start. rid {}", allStarRid);
    AllStarBean asb = AllStarConsole.getBean(allStarRid);
    if (asb == null) {
      return;
    }
    String key = getRankKey(allStarRid);
    Long currCount = redis.zcard(key);// count
    log.info("allstar award0. rid {} rank num {}", allStarRid, currCount);
    List<Tuple> revrange = redis.zrevrangeWithScores(key, 0, -1);// get all
    for (int i = 0; i < revrange.size(); i++) {// 处理排行榜中的球队
      final Tuple tp = revrange.get(i);
      final int rank = i + 1;
      final long tid = toLong(tp.getElement());
      final double hp = tp.getScore();
      AwardBean ab = asb.getAward(rank);
      log.info("allstar award0 rank. rid {} tid {} hp {} rank {} award {}",
          allStarRid, tid, hp, rank, ab);
      if (GameSource.isNPC(tid)) {
        continue;
      }
      sendMail(allStarRid, tid, rank, hp, ab);
    }
  }

  private void sendMail(int allStarRid, long tid, int rank, double hp,
      AwardBean ab) {
    ImmutableList<String> contentParams = ImmutableList.of("" + hp, "" + rank,
        "" + allStarRid);
    DropBean db = DropConsole.getDrop(ab.getDrop());
    List<PropSimple> props = db != null ? db.roll() : Collections.emptyList();
    props = PropSimple.getPropListComposite(props, ab.getProps());
    teamEmailManager.sendEmailWithParamTemplate(tid,
        EmailViewBean.All_Star_Rank, ImmutableList.of(), contentParams, props);
  }

  private void sendKillReward(long teamId, NpcBean npc) {
    List<PropSimple> rewards = PropSimple
        .getPropBeanByStringNotConfig(npc.getKillReward());
    ImmutableList<String> contentParams = ImmutableList.of("" + npc.getLev());
    teamEmailManager.sendEmailWithParamTemplate(teamId,
        EmailViewBean.All_Star_Kill, ImmutableList.of(), contentParams,
        rewards);
  }

  public ErrorCode allStarKillReward0(long teamId) {
    SysAllStar star = toDaySysAllStar();
    if (star == null) {
      return ErrorCode.AllStar_Null;
    }
    KillReward.Builder killRewardBuilder = killRewardBuilder(teamId);
    sendMessage(teamId, killRewardBuilder.build(),
        ServiceCode.AllStar_Kill_Reward_Push);
    return ErrorCode.Success;
  }
  
  private KillReward.Builder killRewardBuilder(long teamId){
    SysAllStar star = toDaySysAllStar();
    if (star == null) {
      return null;
    }
    KillReward.Builder killRewardBuilder = KillReward.newBuilder();
    if (star.getHp() > 0) {
      killRewardBuilder.setKillMaxNpcLevel(star.getNpcLev() - 1);
    } else {
      killRewardBuilder.setKillMaxNpcLevel(star.getNpcLev());
    }
    int myScore = getDayNum(teamId, RedisKey.All_Star_Team_Score);
    killRewardBuilder.setRewardState(
        myScore >= ConfigConsole.getGlobal().allStarKillRewardBaseScore ? 1
            : 0);
    for (int level = star.getNpcLev(); level >= 1; level--) {
      if (checkRewardIsGet(teamId, level, RedisKey.All_Star_Kill_Reward_Day)) {
        killRewardBuilder.addNpcLevel(level);
      }
    }
    return killRewardBuilder;
  }

  public ErrorCode allStarScoreReward0(long teamId) {
    ScoreReward.Builder builder = scoreRewardBuilder(teamId);
    sendMessage(teamId, builder.build(), ServiceCode.AllStar_Score_Reward_Push);
    return ErrorCode.Success;
  }
  
  private ScoreReward.Builder scoreRewardBuilder(long teamId){
    int myScore = getDayNum(teamId, RedisKey.All_Star_Team_Score);
    ScoreReward.Builder builder = ScoreReward.newBuilder();
    builder.setMyScore(myScore);
    for (ScorePersonalAward scorePersonalAward : AllStarConsole
        .getScorePersonalAward()) {
      if (checkRewardIsGet(teamId, scorePersonalAward.getScore(),
          RedisKey.All_Star_Score_Reward_Day)) {
        builder.addScore(scorePersonalAward.getScore());
      }
    }
    return builder;
  }

  private AllStarAllResp allResp(long teamId, int allStarRid, SysAllStar sas) {
    AllStarAllResp.Builder resp = AllStarAllResp.newBuilder();
    String key = getRankKey(allStarRid);
    resp.setNpc(npcResp(sas, teamId));
    resp.setTeam(selfResp(teamId, key));
    resp.setRanks(ranksResp(allStarRid, key));
    Set<Integer> tjPlayers = getTjPlayers();
    for (Integer playerId : tjPlayers) {
      resp.addTjPlayers(playerId);
    }
    KillReward.Builder killRewardBuilder = killRewardBuilder(teamId);
    if(killRewardBuilder != null) {
      resp.setKillReward(killRewardBuilder);
    }
    ScoreReward.Builder scoreReward = scoreRewardBuilder(teamId);
    if(scoreReward != null) {
      resp.setScoreReward(scoreReward);
    }
    return resp.build();
  }

  private AllStarRankResp ranksResp(int allStarRid, String key) {
    AllStarRankResp.Builder ranks = AllStarRankResp.newBuilder();
    ranks.setId(allStarRid);
    List<Tuple> revrange = redis.zrevrangeWithScores(key, 0, Rank_Num);
    for (int i = 0; i < revrange.size(); i++) {
      Tuple tp = revrange.get(i);
      long tid = toLong(tp.getElement());
      int rank = i + 1;
      ranks.addTeams(AllStarTeamRankResp.newBuilder().setTid(tid)
          .setTeam(teamManager.teamResp(tid)).setRank(rank)
          .setHp((int) tp.getScore()));
    }
    Map<Integer, Long> killMap = getKillRank();
    for (Entry<Integer, Long> entry : killMap.entrySet()) {
      ranks
          .addKillRank(AllStarKillRankResp.newBuilder().setNpcId(entry.getKey())
              .setTeam(teamManager.teamResp(entry.getValue()))
              .setTid(entry.getValue()));
    }
    return ranks.build();
  }

  private AllStarNpcResp npcResp(SysAllStar sas, long teamId) {
    int buyNum = teamNumManager.getUsedNum(teamId,
        TeamNumType.All_Star_Buy_Num);
    //剩余次数
    int currentChallengeNum = getDayNum(teamId,RedisKey.All_Star_Challenge_Day);
    int challengeNum = getTotalChallengeNum(teamId) - currentChallengeNum;
    int totalChallengeNum = getLimitNum();
    int jiliNum = getJiliNum(teamId);
    return AllStarNpcResp.newBuilder().setId(sas.getId())
        .setLev(sas.getNpcLev()).setHp(sas.getHp())
          .setBuyNum(buyNum)
        .setChallengeNum(challengeNum).setTotalChallengeNum(totalChallengeNum)
        .setJiliNum(jiliNum).build();
  }

  private AllStarTeamRankResp selfResp(long teamId, String key) {
    Long rank = redis.zrevrank(key, str(teamId));
    Double hp = redis.zscore(key, str(teamId));

    return AllStarTeamRankResp.newBuilder().setTid(teamId)
        .setRank(rank != null ? rank.intValue() + 1 : 0)
        .setHp(hp != null ? hp.intValue() : 0).build();
  }

  private ErrorCode startMatch0(long teamId, int allStarRid) {
    if (isInBattle(teamId)) {
      return ErrorCode.Battle_In;
    }
    AllStarBean asb = AllStarConsole.getBean(allStarRid);
    if (asb == null) {
      return ErrorCode.AllStar_Bean_Null;
    }
    Team team = teamManager.getTeam(getTeamId());
    if (team.getLevel() < asb.getTeamLev()) {
      return ErrorCode.Team_Level;
    }
    if (canMatch(allStarRid) == false) {
      return ErrorCode.AllStar_Match_Time;
    }
    if (checkChallengeNum(teamId) == false) {
      return ErrorCode.AllStar_Challenge_Num;
    }
    SysAllStar sas = getSys(allStarRid);
    if (sas == null || sas.isActivate() == false) {
      return ErrorCode.AllStar_Null;
    }
    if (sas.getHp() <= 0) {
      NpcBean nextnb = asb.getNpc(sas.getNpcLev() + 1);
      if (nextnb == null) {
        return ErrorCode.AllStar_Npc_Max_Lev;
      }
    }
    NpcBean nb = asb.getNpc(sas.getNpcLev());
    if (nb == null) {
      return ErrorCode.AllStar_Npc_Null;
    }
    // 开启比赛
    // BattleSource bs = localBattleManager.buildBattle(EBattleType.AllStar,
    // teamId, nb.getNpcId(), teamId);
    // BattleContxt bc = localBattleManager.defaultContext(this::endMatch);
    // BattleAttribute ba = new BattleAttribute(teamId);
    // ba.addVal(EBattleAttribute.All_Star_Rid, allStarRid);
    // ba.addVal(EBattleAttribute.All_Star_Lev, sas.getNpcLev());
    // bs.addBattleAttribute(ba);
    // log.debug("allstar startMatch. tid {} rid {} npcid {}", teamId,
    // allStarRid,
    // nb.getNpcId());
    // localBattleManager.start(bs, bc);
    start1(teamId, allStarRid, sas, nb);
    addDayNum(teamId, RedisKey.All_Star_Challenge_Day);
    return ErrorCode.Success;
  }

  private void start1(long teamId, int allStarRid, SysAllStar sas, NpcBean nb) {
    BattleSource bs = localBattleManager.buildBattle(EBattleType.AllStar,
        teamId, nb.getNpcId(), teamId);
    BattleContxt bc = localBattleManager.defaultContext(this::endMatch);
    BattleAttribute ba = new BattleAttribute(teamId);
    ba.addVal(EBattleAttribute.All_Star_Rid, allStarRid);
    ba.addVal(EBattleAttribute.All_Star_Lev, sas.getNpcLev());
    bs.addBattleAttribute(ba);
    int allStarRecPlayerRate = ConfigConsole.getGlobal().allStarRecPlayerRate;
    int allStarExcitationRate = ConfigConsole.getGlobal().allStarExcitationRate;
    // 激励
    int jiliNum = getJiliNum(teamId);
    Set<Integer> tjPlayers = getTjPlayers();
    float jiliCapRate = (float)allStarExcitationRate * (float)jiliNum / 100f;
    // 推荐球员
    float tjCapRate = (float)allStarRecPlayerRate / 100;
    ba.addVal(EBattleAttribute.All_Star_Battle_Attr,
        new BattleAttr(jiliCapRate, tjCapRate, tjPlayers));//战力加成
    BattleAllStar bh = new BattleAllStar(bs);
    localBattleManager.initBattleWithContext(bh, bs, bc);
    log.debug("allstar startMatch. tid {} rid {} npcid {}", teamId, allStarRid,
        nb.getNpcId());
    localBattleManager.start(bs, bc);
  }

  /** 比赛结束. 计算要扣除的血量 */
  private void endMatch(BattleSource bs) {
    try {
      endMatch0(bs);
    } catch (Exception e) {
      log.error("allstar " + e.getMessage(), e);
    }
  }

  /** 比赛结束. 计算要扣除的血量 */
  private void endMatch0(BattleSource bs) {
    final EndReport report = bs.getEndReport();
    final long teamId = report.getHomeTeamId();
    final BattleAttribute ba = bs.getAttributeMap(teamId);
    final int allStarRid = ba.getVal(EBattleAttribute.All_Star_Rid);
    final int srcNpcLev = ba.getVal(EBattleAttribute.All_Star_Lev);
    final int homeScore = report.getHomeScore(); 
    if (GameSource.isNPC(teamId)) {
      log.warn("allstar endmatch. tid {} rid {} home is npc", teamId,
          allStarRid);
      localBattleManager.sendEndMain(bs, true);
      return;
    }
    final SysAllStar sas = getSys(allStarRid);
    final AllStarBean asb = AllStarConsole.getBean(allStarRid);
    if (sas == null || asb == null) {
      log.warn("allstar endmatch. tid {} rid {} sys all star is null", teamId,
          allStarRid);
      localBattleManager.sendEndMain(bs, true);
      return;
    }
    int subTotalHp = updateHpAndScore(teamId, allStarRid, srcNpcLev, sas, asb,
        homeScore);
    report.addAdditional(EBattleAttribute.All_Star_Match_End,
        new AllStarMatchEnd(srcNpcLev, sas.getNpcLev(), homeScore, subTotalHp));
    localBattleManager.sendEndMain(bs, true);

    AllStarAllResp resp = allResp(teamId, allStarRid, sas);
    sendMessage(teamId, resp, ServiceCode.AllStar_End_Match_Push);
    addDayNum(teamId, RedisKey.All_Star_Team_Score, subTotalHp);
  }

  /** 扣除boss血量, 增加boss难度, 增加个人积分 */
  private int updateHpAndScore(final long tid, final int rid, final int oldLev,
      final SysAllStar sas, final AllStarBean asb, final int homeScore) {
    synchronized (sas) {
      final int oldHp = sas.getHp();
      final int currLev = sas.getNpcLev();
      int subTotalHp = 0;
      if (oldHp > homeScore) {
        subTotalHp = homeScore;
        sas.setHp(Math.max(0, oldHp - homeScore));
      } else {
        NpcBean npc = asb.getNpc(currLev);
        // kill
        if(oldHp > 0) {
          Map<Integer, Long> killMap = getKillRank();
          killMap.put(npc.getNpcId(), tid);
          setKillRank(killMap);
          sendKillReward(tid, npc);
          chatManager.pushGameTip(EGameTip.全明星赛击杀, 0,
              teamManager.getTeamNameById(tid), npc.getLev() + "");
        }
        NpcBean nextnb = asb.getNpc(currLev + 1);
        if (nextnb == null) {
          sas.setHp(0);
        } else {
          sas.setNpcLev(nextnb.getLev());
          int nextNpcHp = nextnb.getMaxHp();
          int leftScore = homeScore - oldHp;
          subTotalHp += oldHp;
          if (leftScore > 0) {
            int nextCostHp = (int) (leftScore * nextnb.getxLevRate());
            subTotalHp += nextCostHp;
            nextNpcHp = nextnb.getMaxHp() - nextCostHp;
          }
          sas.setHp(Math.max(0, nextNpcHp));
        }
      }
      if (subTotalHp > 0) {// 增加积分
        String key = getRankKey(rid);
        redis.zincrby(key, subTotalHp, str(tid));// add new score
      }
      saveAllStar(sas);
      return subTotalHp;
    }
  }

  public static void main(String[] args) {
    testSubHp();
    // int c = 8;//111
    // int index = 1;
    // System.out.println((c & (1 << (index - 1))) == (1 << (index - 1)));
    // index++;
    // System.out.println((c & (1 << (index - 1))) == (1 << (index - 1)));
    // index++;
    // System.out.println((c & (1 << (index - 1))) == (1 << (index - 1)));
    // index++;
    // System.out.println((c & (1 << (index - 1))) == (1 << (index - 1)));
    // index++;
    // System.out.println((c & (1 << (index - 1))) == (1 << (index - 1)));
  }

  private static void testSubHp() {
    final int maxHp = 200;
    final float xrate = 0.8f;
    SysAllStar sas = new SysAllStar();
    sas.setNpcLev(1);
    sas.setHp(maxHp);
    AllStarBean asb = new AllStarBean(0, 0,
        ImmutableMap.of(1, new NpcBean(1, 1, 1, maxHp, xrate, "", ""), 2,
            new NpcBean(1, 1, 2, maxHp, xrate, "", ""), 3,
            new NpcBean(1, 1, 3, maxHp, xrate, "", ""), 4,
            new NpcBean(1, 1, 4, maxHp, xrate, "", ""), 5,
            new NpcBean(1, 1, 5, maxHp, xrate, "", "")),
        ImmutableList.of());

    int curLevSubHp = 1000;
    int subTotalHp = 0;
    int oldHp = 200;

    subTotalHp += oldHp;
    sas.setHp(0);
    int remainHp = curLevSubHp - oldHp;
    log.warn("lev {} preR {} hpCfg {} hp {} subAllHp {}", sas.getNpcLev(),
        remainHp, oldHp, sas.getHp(), subTotalHp);
    NpcBean nextnb = asb.getNpc(sas.getNpcLev() + 1);
    while (nextnb != null && sas.getHp() <= 0) {
      NpcBean currNb = asb.getNpc(sas.getNpcLev());
      sas.setNpcLev(nextnb.getLev());// 难度增加
      int rateRemain = (int) (remainHp * currNb.getxLevRate());
      sas.setHp(Math.max(0, nextnb.getMaxHp() - rateRemain));
      int subHp = nextnb.getMaxHp() - sas.getHp();
      subTotalHp += subHp;
      log.warn(
          "lev {} preR {} rate {} rateR {} hpCfg {} hp {} afterR {} subAllHp {}",
          sas.getNpcLev(), remainHp, currNb.getxLevRate(), rateRemain,
          nextnb.getMaxHp(), sas.getHp(), rateRemain - subHp, subTotalHp);
      remainHp = rateRemain - subHp;
      nextnb = asb.getNpc(nextnb.getLev() + 1);
    }
  }

  /** 计算跨难度时, 削减后要扣除的血量 */
  private int calcCrossLevSubHp(long tid, int rid, int oldLev, AllStarBean asb,
      int currLev, final int subHp) {
    if (currLev == oldLev) {
      return subHp;
    }
    float ret = subHp;
    for (int i = oldLev; i < currLev; i++) {// 难度发生了变化
      NpcBean preLevNb = asb.getNpc(i);
      if (preLevNb != null) {
        ret = ret * preLevNb.getxLevRate();
      }
    }
    log.debug("allstar xlevsubhp. tid {} rid {} lev {} -> {} hp {} -> {}", tid,
        rid, oldLev, currLev, subHp, ret);
    return (int) ret;
  }

  // private int getActTotalHp(EndReport report, AllStarBean asb, EActionType
  // act) {
  // final int actHpCfg = asb.getActHp(act);
  // int actTotalVal = 0;
  // for (PlayerActStat bpas : report.getHome().getSourceLists()) {
  // int pav = bpas.getIntValue(act);
  // if (log.isTraceEnabled()) {
  // log.trace("allstar actval. tid {} prid {} actv {}", report.getHomeTeamId(),
  // bpas.getPlayerRid(), pav);
  // }
  // actTotalVal += pav;
  // }
  // log.trace("allstar actval. tid {} act {} val {} hpcfg {}",
  // report.getHomeTeamId(), act, actTotalVal, actHpCfg);
  // return actTotalVal * actHpCfg;
  // }

  private static String str(Object obj) {
    return String.valueOf(obj);
  }

  private static long toLong(String str) {
    try {
      return Long.parseLong(str);
    } catch (Exception e) {
      log.error("parse long. " + str, e);
    }
    return 0L;
  }

  /**
   * 排行榜 redis key
   *
   * @param medalMid
   *          大段位id
   */
  private static String getRankKey(int allStarRid) {
    return RedisKey.All_Star_Rank + allStarRid;
  }

  private SysAllStar getSys(int allStarRid) {
    return sys.get(allStarRid);
  }

  private SysAllStar toDaySysAllStar() {
    for (SysAllStar star : sys.values()) {
      if (star.isActivate()) {
        return star;
      }
    }
    return null;
  }

  /** 是否在比赛中 */
  private boolean isInBattle(long teamId) {
    return TeamStatus.inBattle(teamStatusManager.get(teamId),
        EBattleType.AllStar);
  }

  /** gm 设置难度, 血量变满 */
  ErrorCode gmRestart(int allStarRid, int lev) {
    SysAllStar sas = getSys(allStarRid);
    if (sas == null) {
      return ErrorCode.AllStar_Null;
    }
    AllStarBean asb = AllStarConsole.getBean(allStarRid);
    if (asb == null) {
      return ErrorCode.AllStar_Bean_Null;
    }
    NpcBean nb = asb.getNpc(lev);
    if (nb == null) {
      return ErrorCode.AllStar_Npc_Null;
    }
    // noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (sas) {
      sas.setNpcLev(lev);
      sas.setHp(nb.getMaxHp());
    }
    return ErrorCode.Success;
  }

  /** gm 设置剩余血量. hp > 0 */
  ErrorCode gmSetHp(int allStarRid, int hp) {
    SysAllStar sas = getSys(allStarRid);
    if (sas == null) {
      return ErrorCode.AllStar_Null;
    }
    AllStarBean asb = AllStarConsole.getBean(allStarRid);
    if (asb == null) {
      return ErrorCode.AllStar_Bean_Null;
    }
    NpcBean nb = asb.getNpc(sas.getNpcLev());
    if (nb == null) {
      return ErrorCode.AllStar_Npc_Null;
    }
    // noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (sas) {
      sas.setHp(Math.max(1, Math.min(nb.getMaxHp(), hp)));
    }
    return ErrorCode.Success;
  }

  /** gm 发放排名奖励 */
  ErrorCode gmAward(int allStarRid) {
    SysAllStar sas = getSys(allStarRid);
    if (sas == null) {
      return ErrorCode.AllStar_Null;
    }
    AllStarBean asb = AllStarConsole.getBean(allStarRid);
    if (asb == null) {
      return ErrorCode.AllStar_Bean_Null;
    }
    award0();
    return ErrorCode.Success;
  }

  /** gm 修改球队伤害 */
  ErrorCode gmTeamHp(long teamId, int allStarRid, int hp) {
    SysAllStar sas = getSys(allStarRid);
    if (sas == null) {
      return ErrorCode.AllStar_Null;
    }
    AllStarBean asb = AllStarConsole.getBean(allStarRid);
    if (asb == null) {
      return ErrorCode.AllStar_Bean_Null;
    }
    redis.zincrby(getRankKey(allStarRid), hp, str(teamId));
    return ErrorCode.Success;
  }

  /**
   * 检查挑战次数
   * 
   * @param teamId
   * @return
   */
  public boolean checkChallengeNum(long teamId) {
    return getDayNum(teamId,
        RedisKey.All_Star_Challenge_Day) < getTotalChallengeNum(teamId);
  }

  /**
   * 总挑战次数
   * 
   * @param teamId
   * @return
   */
  public int getTotalChallengeNum(long teamId) {
    int buyNum = teamNumManager.getUsedNum(teamId,
        TeamNumType.All_Star_Buy_Num);
    return buyNum + sysGiveNum();
  }

  /**
   * 激励次数
   * 
   * @param teamId
   * @return
   */
  public int getJiliNum(long teamId) {
    int buyNum = teamNumManager.getUsedNum(teamId,
        TeamNumType.All_Star_Jili_Num);
    return buyNum;

  }

  public int getDayNum(long teamId, String constantKey) {
    String key = RedisKey.getDayKey(teamId, constantKey);
    int value = redis.getIntNullIsZero(key);
    return value;
  }

  public int addDayNum(long teamId, String constantKey) {
    return addDayNum(teamId, constantKey, 1);
  }

  public int addDayNum(long teamId, String constantKey, int add) {
    setDayNum(teamId, constantKey, getDayNum(teamId, constantKey) + add);
    return getDayNum(teamId, constantKey);
  }

  public void setDayNum(long teamId, String constantKey, int num) {
    String key = RedisKey.getDayKey(teamId, constantKey);
    redis.set(key, "" + num, RedisKey.DAY);
  }

  /**
   * 击杀缓存数据
   * 
   * @return
   */
  public Map<Integer, Long> getKillRank() {
    if (this.killMap != null) {
      return this.killMap;
    }
    String key = RedisKey.getDayKey(1, RedisKey.ALL_STAR_KILL_RANK_DAY);
    String killStr = redis.getStr(key);
    Map<Integer, Long> killMap = new HashMap<Integer, Long>();
    if (killStr != null && !"".equals(killStr)) {
      String[] arr = killStr.split(",");
      for (String kill : arr) {
        String[] killArr = kill.split("_");
        killMap.put(Integer.parseInt(killArr[0]), Long.parseLong(killArr[1]));
      }
    }
    this.killMap = killMap;
    return killMap;
  }

  /**
   * 随机推荐球员
   */
  public Set<Integer> randTjPlayer() {
    String starPZ = ConfigConsole.global().allStarRecPlayer;
    String[] pz = starPZ.split(",");
    StringBuffer sb = new StringBuffer();
    int index = 0;
    Set<Integer> list = new HashSet<>();
    for (String p : pz) {
      index++;
      EPlayerGrade minGrade = EPlayerGrade.convertByName(p);
      EPlayerGrade maxGrade = minGrade;
      List<Integer> filterIds = new ArrayList<>(list);
      List<PlayerBean> players = PlayerConsole
          .getRanPlayer(minGrade, maxGrade, EPlayerPosition.NULL, null,1,filterIds);
      if(players != null && players.size() > 0) {
        int playerId = players.get(0)
            .getPlayerRid();
        sb.append(playerId);
        if (index < pz.length) {
          sb.append(",");
        }
        list.add(playerId);
      }
    }
    setStarTJ(sb.toString());
    return list;
  }

  public void setStarTJ(String players) {
    String key = RedisKey.getDayKey(1, RedisKey.All_Star_Tj_Day);
    redis.set(key, players, RedisKey.DAY);
  }

  /**
   * 获取推荐球员
   * 
   * @return
   */
  public Set<Integer> getTjPlayers() {
    String key = RedisKey.getDayKey(1, RedisKey.All_Star_Tj_Day);
    String value = redis.getObj(key);
    Set<Integer> list = new HashSet<>();
    if (value == null) {
      list = randTjPlayer();
    } else {
      String[] arr = value.split(",");
      for (String p : arr) {
        list.add(Integer.parseInt(p));
      }
    }
    return list;
  }

  /**
   * 保存击杀缓存
   * 
   * @param killMap
   */
  public void setKillRank(Map<Integer, Long> killMap) {
    String key = RedisKey.getDayKey(1, RedisKey.ALL_STAR_KILL_RANK_DAY);
    StringBuffer sb = new StringBuffer();
    int index = 0;
    for (Entry<Integer, Long> entry : killMap.entrySet()) {
      index++;
      sb.append(entry.getKey()).append("_").append(entry.getValue());
      if (index < killMap.size()) {
        sb.append(",");
      }
    }
    redis.set(key, sb.toString(), RedisKey.DAY);
  }

  /**
   * 奖励是否已领取
   * 
   * @param teamId
   * @param level
   *          奖励阶级
   * @return
   */
  public boolean checkRewardIsGet(long teamId, int level, String baseKey) {
    int keyFlag = 32 / level;
    String key = baseKey + keyFlag;
    int flag = getDayNum(teamId, key);
    if (flag == 0) {
      return false;
    }
    return (flag & (1 << (level - 1))) == (1 << (level - 1));
  }

  /**
   * 保存奖励领取状态
   * 
   * @param teamId
   * @param level
   *          奖励阶级
   * @param baseKey
   */
  public void setRewardGet(long teamId, int level, String baseKey) {
    int keyFlag = 32 / level;
    String key = baseKey + keyFlag;
    int flag = getDayNum(teamId, key);
    if (checkRewardIsGet(teamId, level, baseKey)) {
      return;
    }
    flag |= 1 << (level - 1);
    setDayNum(teamId, key, flag);
  }
}
