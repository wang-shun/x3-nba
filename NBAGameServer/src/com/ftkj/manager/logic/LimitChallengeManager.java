package com.ftkj.manager.logic;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.cfg.EmailViewBean;
import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.console.SystemActiveConsole;
import com.ftkj.db.ao.common.IActiveAO;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.LimitChallengeTitle;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.battle.handle.BattleAllStar;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.TeamReport;
import com.ftkj.manager.battle.model.PlayerActStat;
import com.ftkj.manager.logic.LocalBattleManager.BattleContxt;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.LimitChallengePB.LimitChallengeRank;
import com.ftkj.proto.LimitChallengePB.LimitChallengeView;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.quartz.QuartzServer;
import com.ftkj.tool.zookeep.ZookeepServer;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.ImmutableList;

/**
 *  极限挑战
 * @author zehong.he
 *
 */
public class LimitChallengeManager extends AbstractBaseManager {
  private static final Logger log = LoggerFactory.getLogger(LimitChallengeManager.class);
  
  @IOC
  private IActiveAO activeAO;
  @IOC
  private LocalBattleManager localBattleManager;
  @IOC
  private TeamEmailManager teamEmailManager;
  @IOC
  private TeamNumManager teamNumManager;
  @IOC
  private TeamManager teamManager;
  
  /**
   * 排行榜显示条数
   */
  public static final int show_limit = 10;
  
  /**
   * 最大排名
   */
  public static final int rank_limit = 100;
  
  /**
   * 默认挑战次数
   */
  public static final int challengeNum = 1;
  
  /**
   * 恢复挑战次数所需时间（秒）
   */
  public static final int recoveryChallengeNumTime = 30 * 60;
  
  /**
   * key:teamId
   */
  public static Map<Long,Long> callengeTeam = new ConcurrentHashMap<>();
  
  
  /**
   * Active配置config字段键
   * @author zehong.he
   *
   */
  public static enum ConfigKey{
    //npcId=id1_id2...
    npcIds("npcId"),
    
    //---------------行为积分--------------
    //得分
    df("df"),
    
    //篮板
    lb("lb"),
    
    //助攻
    zg("zg"),
    
    //抢断
    qd("qd"),
    
    //盖帽
    gm("gm"),
    
    //--------------------------------------------
    
    //开启时间
    startTime("startTime"),
    
    //结束时间
    endTime("endTime"),
    
    //发奖时间
    rewardTime("rewardTime"),
    
    //每天购买次数上限
    buyMax("buyMax"),
    
    maxRank("maxRank"),
    
    minRank("minRank")
    ;
   
    private String key;
    
    private ConfigKey(String key) {
      this.key = key;
    }
    
    public String getKey() {
      return key;
    }
  }
  
  /**
   * npcId
   * @return
   */
  public int getNpcId() {
    int npcId = getDayNum(0, RedisKey.Limit_Challenge_npcId_Day);
    if(npcId == 0) {
      SystemActiveBean config = SystemActiveConsole.getSystemActive(EAtv.极限挑战.getAtvId());
      String npcIds = config.getConfigMap().get(ConfigKey.npcIds.getKey());
      String[] arr = npcIds.split("_");
      int index = RandomUtil.randInt(0, arr.length-1);
      npcId = Integer.parseInt(arr[index]);
      setDayNum(0, RedisKey.Limit_Challenge_npcId_Day,npcId);
    }
    return npcId;
  }
  
  /**
   * key：排名
   */
  private Map<Integer,ActiveBasePO> rank = null;

  public Map<Integer,ActiveBasePO> getRank(){
    if(rank == null) {
      initRank();
    }
    return rank;
  }
  
  /**
   * 开启时间
   * @return
   */
  public LocalTime getStarTime() {
    return getTime(ConfigKey.startTime);
  }
  
  /**
   * 结束时间
   * @return
   */
  public LocalTime getEndTime() {
    return getTime(ConfigKey.endTime);
  }
  
  /**
   * 发奖时间
   * @return
   */
  public LocalTime getRewardTime() {
    return getTime(ConfigKey.rewardTime);
  }
  
  /**
   * 能否在比赛时间内
   * @return
   */
  public boolean isInChallengeTime() {
    LocalTime now = LocalTime.now();
    return now.isAfter(getStarTime()) && now.isBefore(getEndTime());
  }
  
  private LocalTime getTime(ConfigKey timeKey) {
    SystemActiveBean config = SystemActiveConsole.getSystemActive(EAtv.极限挑战.getAtvId());
    String startTime = config.getConfigMap().get(timeKey.getKey());
    LocalTime localTime = LocalTime.parse(startTime,
        DateTimeFormatter.ISO_LOCAL_TIME);
    return localTime;
  }
  
  /**
   * 剩余挑战次数
   * @param teamId
   * @return
   */
  private int leftChallengeNum(long teamId) {
    int time = getDayNum(teamId, RedisKey.Limit_Challenge_recovery_time);
    int num = 0;
    if(time == 0) {
      return challengeNum;
    }else {
      int dTime = (int)(System.currentTimeMillis() / 1000 - time);
      if(dTime >= recoveryChallengeNumTime) {
        num++;
        //clear
        setDayNum(teamId,RedisKey.Limit_Challenge_recovery_time,0);
      }
    }
    return num;
  }
  
  @Override
  public void instanceAfter() {
    initRank();
    initReward();
    initUpdateRank();
  }
  
  private void initUpdateRank() {
      Watcher w = new Watcher() {
      @Override
      public void process(WatchedEvent event) {
          try {
            updateRank();
          } catch (Exception e) {
              log.error("initUpdateRank error " + e.getMessage(), e);
          }
          ZookeepServer.getInstance().exists(ZookeepServer.getLimitChallengePath(), this);
      }
    };
    ZookeepServer.getInstance().exists(ZookeepServer.getLimitChallengePath(), w);
  }
  
  /**
   * 初始化发奖
   */
  private void initReward() {
    LocalTime now = LocalTime.now();
    boolean isRewarded = getDayNum(0,RedisKey.Limit_Challenge_reward_Day) > 0;
    LocalTime rewardTime = getRewardTime();
    if(now.isAfter(rewardTime) && isRewarded == false) {
      award();
    }
    long midnight = DateTimeUtil.midnight();
    long curr = System.currentTimeMillis();
    long awardTime = rewardTime.toNanoOfDay() / 1000_000;
    long rewardDelay = midnight + awardTime - curr;
    if (rewardDelay < 0) {
      Delayed sd = QuartzServer.scheduleAtFixedRate(() -> award(),
            (rewardDelay + DateTimeUtil.DAILY), DateTimeUtil.DAILY, TimeUnit.MILLISECONDS);
        log.info("limitChallenge init reward task postpone. delay[{}]", DateTimeUtil.duration(sd));
    } else {
      Delayed sd = QuartzServer.scheduleAtFixedRate(() -> award(),
          rewardDelay, DateTimeUtil.DAILY, TimeUnit.MILLISECONDS);
      log.info("limitChallenge init  reward task postpone. delay[{}]", DateTimeUtil.duration(sd));
    }
  }
  
  public void zeroReset() {
    activeAO.deleteLimitChallenge(EAtv.极限挑战.getAtvId());
    rank.clear();
    log.info("----------------zeroReset---------------");
  }
  
  /**
   * 发奖
   */
  public void award() {
    SystemActiveBean config = SystemActiveConsole.getSystemActive(EAtv.极限挑战.getAtvId());
    Map<Integer, SystemActiveCfgBean> rewards = config.getAwardConfigList();
    int rank = 0;
    for(SystemActiveCfgBean rewardConfig : rewards.values()) {
      LimitChallengeTitle title = todayTitle();
      int minRank = Integer.parseInt(rewardConfig.getConditionMap().get(ConfigKey.minRank.getKey()));
      int maxRank = Integer.parseInt(rewardConfig.getConditionMap().get(ConfigKey.maxRank.getKey()));
      List<ActiveBasePO> list = activeAO.queryLimitChanllenge(title.ordinal(),Math.max(0, minRank-1), maxRank-minRank+1,EAtv.极限挑战.getAtvId());
      for(ActiveBasePO po : list) {
        rank++;
        if(po.getShardId() == GameSource.shardId) {
          sendMail(po.getTeamId(),rank,rewardConfig.getPropSimpleList());
        }
      }
    }
    addDayNum(0, RedisKey.Limit_Challenge_reward_Day);
  }
  
  private void sendMail(long teaId, int rank,List<PropSimple> props) {
    ImmutableList<String> contentParams = ImmutableList.of("" + rank);
    teamEmailManager.sendEmailWithParamTemplate(teaId,
        EmailViewBean.limit_challenge_rank, ImmutableList.of(), contentParams, props);
  }
  
  /**
   * 初始化排行榜
   */
  private void initRank() {
    rank = new HashMap<>();
    LimitChallengeTitle title = todayTitle();
    List<ActiveBasePO> list = activeAO.queryLimitChanllenge(title.ordinal(),0, rank_limit,EAtv.极限挑战.getAtvId());
    int rank0 = 0;
    for(ActiveBasePO ab : list) {
      rank.put(++rank0, ab);
    }
  }
  
  /**
   * 今天的题目是
   */
  private LimitChallengeTitle todayTitle(){
    int dayOfWeek = DateTimeUtil.getCurrWeekDay();
    LimitChallengeTitle title = LimitChallengeTitle.valueOf(dayOfWeek - 1);
    if(title == null) {
      return LimitChallengeTitle.两双王_d_l;
    }
    return title;
  }
  
  /**
   * 极限挑战-获取主界面信息
   * @param allStarRid
   */
  @ClientMethod(code = ServiceCode.Limit_Challenge_get_view)
  public void mainView() {
    long teamId = getTeamId();
    ErrorCode ret = mainView(teamId);
    sendMsg(ret);
    log.debug("mainView info. ret {}", teamId, ret);
  }
  
  private ErrorCode mainView(long teamId) {
    Map<Integer,ActiveBasePO> rank = getRank();
    LimitChallengeView.Builder builder = LimitChallengeView.newBuilder();
    builder.setTodayTitle(todayTitle().ordinal());
    int myRank = 0;
    ActiveBasePO myData = null;
    int num = 0;
    for(Entry<Integer,ActiveBasePO> entry : rank.entrySet()) {
      ActiveBasePO po = entry.getValue();
      if(num < rank_limit) {
        builder.addPlayerRank(rankBuilder(po,entry.getKey()));
        num++;
      }
      if(po.getTeamId() == teamId) {
        myRank = entry.getKey();
        myData = po;
      }
      if(num >= rank_limit && myData != null) {
        break;
      }
    }
    if(myData == null) {
      myData = activeAO.getLimitChanllenge(teamId,EAtv.极限挑战.getAtvId());
    }
    if(myData != null) {
      builder.setMyData(rankBuilder(myData,myRank));
    }else {
      LimitChallengeRank.Builder myBuilder = LimitChallengeRank.newBuilder();
      Team team = teamManager.getTeam(teamId);
      myBuilder.setServerId(GameSource.shardId);
      myBuilder.setTeamName(team.getName());
      builder.setMyData(myBuilder);
    }
    int leftChallengeNum = leftChallengeNum(teamId); 
    builder.setLeftChallengeNum(leftChallengeNum);
    int time = getDayNum(teamId, RedisKey.Limit_Challenge_recovery_time);
    if(time > 0) {
      builder.setCdEndTime(time + recoveryChallengeNumTime);
    }
    int buyNum = teamNumManager.getUsedNum(teamId,
        TeamNumType.limit_challenge_buy_num);
    builder.setBuyNum(buyNum);
    SystemActiveBean config = SystemActiveConsole.getSystemActive(EAtv.极限挑战.getAtvId());
    String startTime = config.getConfigMap().get(ConfigKey.startTime.getKey());
    String endTime = config.getConfigMap().get(ConfigKey.endTime.getKey());
    String rewardTime = config.getConfigMap().get(ConfigKey.rewardTime.getKey());
    builder.setStartTime(startTime);
    builder.setEndTime(endTime);
    builder.setRewardTime(rewardTime);
    int npcId = getNpcId();
    builder.setNpcId(npcId);
    sendMessage(teamId, builder.build(), ServiceCode.Limit_Challenge_push_view);
    return ErrorCode.Success;
  }
  

  private LimitChallengeRank.Builder rankBuilder(ActiveBasePO po,int rank){
    LimitChallengeRank.Builder builder = LimitChallengeRank.newBuilder();
    builder.setServerId(po.getShardId());
    builder.setTeamName(po.getTeamName());
    SystemActiveBean config = SystemActiveConsole.getSystemActive(EAtv.极限挑战.getAtvId());
    int dfScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.df.getKey()));
    int lbScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.lb.getKey()));
    int zgScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.zg.getKey()));
    int qdScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.qd.getKey()));
    int gmScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.gm.getKey()));
    builder.setDf(po.getiData1() > 0?po.getiData1()/dfScore : 0);
    builder.setLb(po.getiData2() > 0?po.getiData2()/lbScore : 0);
    builder.setZg(po.getiData3() > 0?po.getiData3()/zgScore : 0);
    builder.setQd(Integer.parseInt(po.getsData1()) > 0?Integer.parseInt(po.getsData1())/qdScore:0);
    builder.setGm(Integer.parseInt(po.getsData2())>0?Integer.parseInt(po.getsData2())/gmScore:0);
    builder.setRank(rank);
    return builder;
  }
  
  /**
   * 打
   */
  @ClientMethod(code = ServiceCode.Limit_Challenge_Start_Match)
  public void challenge() {
    long teamId = getTeamId();
    ErrorCode ret = start(teamId);
    if(ret == ErrorCode.Success) {
      setDayNum(teamId,RedisKey.Limit_Challenge_recovery_time,(int)(System.currentTimeMillis() / 1000));
    }
    sendMsg(ret);
    log.debug("challenge info. ret {}", teamId, ret);
  }
  
  
  @RPCMethod(code = CrossCode.LocalGameManagger_limit_challenge_update, pool = EServerNode.Logic, type = ERPCType.ALL)
  public void updateRank() {
    initRank();
    //notice
    List<Long> teams = new ArrayList<>(callengeTeam.keySet());
    callengeTeam.clear();
    for(Long teamId : teams) {
      mainView(teamId);
    }
  }
  
  @ClientMethod(code = ServiceCode.Limit_Challenge_buy_Challenge_Num)
  public void buyChallengeNum() {
    long tid = getTeamId();
    ErrorCode ret = buyMatchNum0(tid, 1);
    sendMsg(ret);
    log.debug("buyChallengeNum. tid {} num {} ret {}", tid, 1, ret);
  }
  
  private ErrorCode buyMatchNum0(long tid, int num) {
    if(isInChallengeTime() == false) {
      return ErrorCode.limit_challenge_time;
    }
    int leftChallengeNum = leftChallengeNum(tid);
    if(leftChallengeNum > 0) {
      return ErrorCode.limit_challenge_buy_num;
    }
    ErrorCode ret = teamNumManager.consumeNumCurrency(tid,
        TeamNumType.limit_challenge_buy_num, num,
        ModuleLog.getModuleLog(EModuleCode.LimitChallenge, "购买挑战次数"));
    if (ret.isError()) {
      return ret;
    }
    //clear
    setDayNum(tid,RedisKey.Limit_Challenge_recovery_time,0);
    mainView(tid);
    return ErrorCode.Success;
  }
  
  
  private ErrorCode start(long teamId) {
    //次数判断  时间判断
    int leftChallengeNum = leftChallengeNum(teamId);
    if(leftChallengeNum <= 0) { 
      return ErrorCode.limit_challenge_num;
    }
    if(isInChallengeTime() == false) {
      return ErrorCode.limit_challenge_time;
    }
    int npcId = getNpcId();
    BattleSource bs = localBattleManager.buildBattle(EBattleType.Limit_Challenge,
        teamId,npcId, teamId);
    BattleContxt bc = localBattleManager.defaultContext(this::endMatch);
    BattleAttribute ba = new BattleAttribute(teamId);
    bs.addBattleAttribute(ba);
    BattleAllStar bh = new BattleAllStar(bs);
    localBattleManager.initBattleWithContext(bh, bs, bc);
    localBattleManager.start(bs, bc);
    return ErrorCode.Success;
  }
  
  /**
   * endMatch
   * @param bs
   */
  private void endMatch(BattleSource bs) {
    try {
        final EndReport report = bs.getEndReport();
        localBattleManager.sendEndMain(bs, true);
        //
        final long teamId = report.getHomeTeamId();
        ActiveBasePO myData = activeAO.getLimitChanllenge(teamId,EAtv.极限挑战.getAtvId());
        LimitChallengeTitle title = todayTitle();
        boolean isNew = false;
        if(myData == null) {
          isNew = true;
          myData = new ActiveBasePO(EAtv.极限挑战.getAtvId(),GameSource.shardId,teamId, report.getHome().getName());
        }
        TeamReport home = report.getHome();
        int df = home.getScore();
        int lb = 0;
        int zg = 0;
        int qd = 0;
        int gm = 0;
        for(PlayerActStat playerActStat : home.getSourceLists()) {
          lb+=playerActStat.getIntValue(EActionType.reb);
          zg+=playerActStat.getIntValue(EActionType.ast);
          qd+=playerActStat.getIntValue(EActionType.stl);
          gm+=playerActStat.getIntValue(EActionType.blk);
        }
        SystemActiveBean config = SystemActiveConsole.getSystemActive(EAtv.极限挑战.getAtvId());
        int dfScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.df.getKey())) * df;
        int lbScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.lb.getKey())) * lb;
        int zgScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.zg.getKey())) * zg;
        int qdScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.qd.getKey())) * qd;
        int gmScore = Integer.parseInt(config.getConfigMap().get(ConfigKey.gm.getKey())) * gm;
        int oldTotalScore = 0;
        int totalScore = 0;
        //得分 i_data1
        //篮板 i_data2
        //助攻 i_data3
        //抢断 s_data1
        //盖帽 s_data2
        if(title == LimitChallengeTitle.两双王_d_l) {
          oldTotalScore = myData.getiData1() + myData.getiData2();
          totalScore = dfScore + lbScore;
          zgScore=qdScore=gmScore=0;
        }else if(title == LimitChallengeTitle.两双王_d_z) {
          oldTotalScore = myData.getiData1() + myData.getiData3();
          totalScore = dfScore + zgScore;
          lbScore=qdScore=gmScore=0;
        }else if(title == LimitChallengeTitle.三双王) {
          oldTotalScore = myData.getiData1() + myData.getiData2() + myData.getiData3();
          totalScore = dfScore + lbScore + zgScore;
          qdScore=gmScore=0;
        }else if(title == LimitChallengeTitle.全能王) {
          oldTotalScore = myData.getiData1() + myData.getiData2() + myData.getiData3();
          if(myData.getsData1() != null && !"".equals(myData.getsData1())) {
            oldTotalScore += Integer.parseInt(myData.getsData1());
          }
          if(myData.getsData2() != null && !"".equals(myData.getsData2())) {
            oldTotalScore += Integer.parseInt(myData.getsData2());
          }
          totalScore = dfScore + lbScore + zgScore + qdScore + gmScore;
        }else if(title == LimitChallengeTitle.得分王){
          if(myData.getsData1() != null && !"".equals(myData.getsData1())) {
            oldTotalScore+=Integer.parseInt(myData.getsData1());
          }
          if(myData.getsData2() != null && !"".equals(myData.getsData2())) {
            oldTotalScore+=Integer.parseInt(myData.getsData2());
          }
          totalScore = qdScore + gmScore;
          lbScore=zgScore=dfScore=0;
        }else if(title == LimitChallengeTitle.助攻王) {
          oldTotalScore = myData.getiData3();
          if(myData.getsData2() != null && !"".equals(myData.getsData2())) {
            oldTotalScore+=Integer.parseInt(myData.getsData2());
          }
          totalScore = zgScore + gmScore;
          dfScore=lbScore=qdScore=0;
        }else if(title == LimitChallengeTitle.篮板王) {
          oldTotalScore = myData.getiData2();
          if(myData.getsData1() != null && !"".equals(myData.getsData1())) {
            oldTotalScore+=Integer.parseInt(myData.getsData1());
          }
          if(myData.getsData2() != null && !"".equals(myData.getsData2())) {
            oldTotalScore+=Integer.parseInt(myData.getsData2());
          }
          totalScore = lbScore + qdScore + gmScore;
          dfScore=zgScore=0;
        }
        if(totalScore > oldTotalScore) {
          myData.setiData1(dfScore);
          myData.setiData2(lbScore);
          myData.setiData3(zgScore);
          myData.setsData1(qdScore+"");
          myData.setsData2(gmScore+"");
          if(isNew) {
            activeAO.addLimitChallenge(myData);
          }else {
            activeAO.updateLimitChallenge(myData);
          }
          //通知所有服刷新排行榜
          notice(teamId,totalScore);
          //RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossLimitChallengeUpdateNotice, null, 0);
          //
          callengeTeam.put(teamId, teamId);
        }
    } catch (Exception e) {
      log.error("endMatch " + e.getMessage(), e);
    }
  }
  
  private void notice(long teamId,int score) {
    try {
      ZookeepServer.getInstance().create(ZookeepServer.getLimitChallengePath(),teamId+"_"+score);
    }catch (Exception e) {
      log.error("{}",e);
    }
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
}
