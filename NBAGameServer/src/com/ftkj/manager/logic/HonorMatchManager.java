package com.ftkj.manager.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.MMatchConditionBean;
import com.ftkj.cfg.MMatchLevBean.Star;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.cfg.honor.HonorDivBean;
import com.ftkj.cfg.honor.HonorLevBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.HonorMatchConsole;
import com.ftkj.db.dao.logic.HonorMatchDao;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.handle.AutoBattleQuickHandle;
import com.ftkj.manager.battle.handle.BattleAllStar;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.logic.LocalBattleManager.BattleContxt;
import com.ftkj.manager.logic.MainMatchManager.ScoreResult;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.match.HonorMatch;
import com.ftkj.manager.match.HonorMatch.HDiv;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.vip.TeamVip;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.HonorMatchPB.CurrHonorDiv;
import com.ftkj.proto.HonorMatchPB.HonorMatchBoxReward;
import com.ftkj.proto.HonorMatchPB.HonorMatchDiv;
import com.ftkj.proto.HonorMatchPB.HonorMatchLev;
import com.ftkj.proto.HonorMatchPB.HonorMatchQuickMatchPushResp;
import com.ftkj.proto.HonorMatchPB.UpdateBoxReward;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.util.ListsUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.ImmutableList;

/**
 * 球星荣耀
 * @author zehong.he
 *
 */
public class HonorMatchManager extends AbstractBaseManager implements OfflineOperation{
  
    private static final Logger log = LoggerFactory.getLogger(HonorMatchManager.class);
    private static final Logger starLog = LoggerFactory.getLogger("com.ftkj.manager.logic.HonorMatchManager");
    
    //每个关卡最大星级
    public static final int maxStar = 3;
    
    //每个章节宝箱数量
    public static final int boxNum = 3;
    
    //少基说扫荡给死3星
    public static final int saodang_star = 3;
    
    @IOC
    private HonorMatchDao honorMatchDao;
    @IOC
    private TeamNumManager teamNumManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private VipManager vipManager;
    @IOC
    private LocalBattleManager localBattleManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private JedisUtil redis;
    
    private ConcurrentMap<Long, HonorMatch> honorMatchMap = new ConcurrentHashMap<>();

    public HonorMatchManager() {
        super();
    }

    public HonorMatchManager(boolean init) {
        super(init);
    }

    @Override
    public void instanceAfter() {
      
    }
    
    @ClientMethod(code = ServiceCode.honor_Match_buy_Challenge_Num)
    public void buyChallengeNum() {
      long teamId = getTeamId();
      ErrorCode ret = buyMatchNum0(teamId, 1);
      if(ret == ErrorCode.Success) {
        int buyNum = teamNumManager.getUsedNum(teamId,
            TeamNumType.honorMatch);
        sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(buyNum+"").build(), ServiceCode.honor_Match_buy_Challenge_Num);
      }else {
        sendMsg(ret);
      }
      log.debug("buyChallengeNum. teamId {} num {} ret {}", teamId, 1, ret);
    }
    
    private ErrorCode buyMatchNum0(long teamId, int num) {
      int honorbuyCount = ConfigConsole.getGlobal().honorbuyCount;
      TeamVip vip = vipManager.getVip(teamId);
      if (vip != null && vip.isVip()) {
        int value = vipManager.getVipValue(teamId, EBuffType.honorBuy);
        honorbuyCount += value;
      }
      int buyNum = teamNumManager.getUsedNum(teamId,
          TeamNumType.honorMatch);
      if(buyNum >= honorbuyCount) {
        return ErrorCode.honor_team_buy_limit;
      }
      ErrorCode ret = teamNumManager.consumeNumCurrency(teamId,
          TeamNumType.honorMatch, num,
          ModuleLog.getModuleLog(EModuleCode.球星荣耀, "球星荣耀"));
      if (ret.isError()) {
        return ret;
      }
      return ErrorCode.Success;
    }

    @ClientMethod(code = ServiceCode.honor_match_div)
    public void honorMatchDiv(int divId) {
      long teamId = getTeamId();
      ErrorCode ret = honorMatchDiv(teamId,divId);
      sendMsg(ret);
      log.debug("honor_match_div info. ret {}", teamId, ret);
    }
    
    @ClientMethod(code = ServiceCode.honor_Match_Curr_div_id)
    public void honorMatchMaxDiv() {
      long teamId = getTeamId();
      pushMaxDivId(teamId);
    }
    
    public void pushMaxDivId(long teamId) {
      int maxDiv = maxDiv(teamId);
      CurrHonorDiv.Builder builder = CurrHonorDiv.newBuilder();
      builder.setDivId(maxDiv);
      sendMessage(teamId, builder.build(), ServiceCode.honor_Match_push_Curr_div_id);
    }
    
    public ErrorCode honorMatchDiv(long teamId,int divId) {
      if(HonorMatchConsole.getDivBean(divId) == null) {
        log.debug("honorMatchDiv is null teamId {} ,divId{}",teamId ,divId);
        return ErrorCode.honor_no_div;
      }
      HonorMatchDiv.Builder builder = honorMatchDivBuilder(teamId,divId);
      sendMessage(teamId, builder.build(), ServiceCode.honor_match_push_div);
      return ErrorCode.Success;
    }
    
    public int maxDiv(long teamId) {
      HonorMatch honorMatch = getHonorMatch(teamId);
      return honorMatch.getMaxDiv();
    }
    
    public HonorMatchDiv.Builder honorMatchDivBuilder(long teamId,int divId){
      HonorMatch honorMatch = getHonorMatch(teamId);
      HonorMatchDiv.Builder builder = HonorMatchDiv.newBuilder();
      int dayChallengeNum = getDayNum(teamId,RedisKey.HonorMatch_challenge_day);
      builder.setDayChallengeNum(dayChallengeNum);
      int buyNum = teamNumManager.getUsedNum(teamId,
          TeamNumType.honorMatch);
      builder.setDayBuyNum(buyNum);
      builder.setDivId(divId);
      HDiv hDiv = honorMatch.getDivMap().get(divId);
      if(hDiv == null) {
        hDiv = new HDiv();
        hDiv.setDivId(divId);
      }
      for(HonorMatchBoxReward.Builder boxBuilder : hDiv.boxBuilders()) {
        builder.addBoxRewards(boxBuilder);
      }
      Set<HonorLevBean> lvs = HonorMatchConsole.getLevsOfDiv(divId);
      int minId = Integer.MAX_VALUE;
      for(HonorLevBean lv : lvs) {
        HonorMatchLev.Builder lvBuilder = HonorMatchLev.newBuilder();
        lvBuilder.setId(lv.getId());
        int index = getLvIndex(lv.getId());
        lvBuilder.setStar(hDiv.getStart(index));
        builder.addMatchLevs(lvBuilder);
        if(minId > lv.getId()) {
          minId = lv.getId();
        }
      }
      int state = 2;
      if(minId != Integer.MAX_VALUE) {
        HonorLevBean honorLevBean = HonorMatchConsole.getLevBean(minId);
        if(honorLevBean.getEnablePreId() == 0) {
          state = 1;
        }else {
          Map<Integer,HDiv> map = honorMatch.getDivMap();
          HonorLevBean prehonorLevBean = HonorMatchConsole.getLevBean(honorLevBean.getEnablePreId());
          if(prehonorLevBean != null) {
            HDiv div = map.get(prehonorLevBean.getDivId());
            state = div != null && div.getStart(getLvIndex(prehonorLevBean.getId())) > 0 ? 1 : 2; 
          }else {
            state = 1;
          }
        }
      }
      builder.setState(state);
      return builder;
    }
    
    /**
     * 计算总星数
     */
    public int calcTotalStar(HDiv hDiv) {
      Set<HonorLevBean> lvs = HonorMatchConsole.getLevsOfDiv(hDiv.getDivId());
      int totalStarNum = 0;
      for(HonorLevBean lv : lvs) {
        int index = getLvIndex(lv.getId());
        totalStarNum += hDiv.getStart(index);
      }
      hDiv.setTotalStarNum(totalStarNum);
      return totalStarNum;
    }
    
    @ClientMethod(code = ServiceCode.honor_match_start)
    public void challenge(int lvId) {
      long teamId = getTeamId();
      ErrorCode ret = start(teamId,lvId);
      if(ret == ErrorCode.Success) {
        addDayNum(teamId, RedisKey.HonorMatch_challenge_day);
      }
      sendMsg(ret);
      log.debug("challenge info. ret {}", teamId, ret);
    }
    
    @ClientMethod(code = ServiceCode.honor_match_open_box_reward)
    public void openBox(int divId,int boxId) {
      long teamId = getTeamId();
      ErrorCode ret = openBox0(teamId,divId,boxId);
      sendMsg(ret);
      log.debug("challenge info. ret {}", teamId, ret);
    }
    
    @ClientMethod(code = ServiceCode.honor_Quick_Match)
    public void honorQquickMatch(int lvId) {
        long teamId = getTeamId();
        ErrorCode ret = honorQquickMatch0(teamId, lvId);
        if(ret == ErrorCode.Success) {
          addDayNum(teamId, RedisKey.HonorMatch_challenge_day);
        }
        sendMsg(ret);
        log.debug("honor honorQquickMatch. tid {} levRid {} ret {}", teamId, lvId, ret);
    }
    
    private ErrorCode honorQquickMatch0(long teamId, int lvId) {
      ErrorCode checked = checkChallenge(teamId, lvId, 1);
      if(checked != ErrorCode.Success) {
        return checked;
      }
      HonorLevBean honorLevBean = HonorMatchConsole.getLevBean(lvId);
      if(honorLevBean == null) {
        return ErrorCode.honor_no_lv; 
      }
      int npcId = honorLevBean.getNpcId();
      BattleSource bs = localBattleManager.buildBattle(EBattleType.honorMatch, teamId, npcId, teamId);
      BaseBattleHandle bh = new AutoBattleQuickHandle(bs, bs1 -> {
          log.debug("mmatch battle quick match end. bid {}", bs1.getId());
          quickEndMatch(teamId, lvId,bs1);
      }, false);
      localBattleManager.start(bs, bh);
      return ErrorCode.Success;
    }
    
    public ErrorCode openBox0(long teamId,int divId,int boxId) {
      HonorMatch honorMatch = getHonorMatch(teamId);
      Map<Integer,HDiv> map = honorMatch.getDivMap();
      HDiv hDiv = map.get(divId);
      HonorDivBean honorDivBean = HonorMatchConsole.getDivBean(divId);
      int needStarNum = 0;
      String itemStr = honorDivBean.getItem(boxId);
      String dropStr = honorDivBean.getDrop(boxId);
      if(boxId == 1) {
          needStarNum = honorDivBean.getStarNum1();
      }else if(boxId == 2) {
          needStarNum = honorDivBean.getStarNum2();
      }else if(boxId == 3) {
          needStarNum = honorDivBean.getStarNum3();
      }else {
        return ErrorCode.honor_can_not_open_box;
      }
      if(hDiv == null || hDiv.getTotalStarNum() < needStarNum) {
        return ErrorCode.honor_can_not_open_box;
      }
      if(hDiv.getBoxState(boxId) == HDiv.box_state_has_got) {
        return ErrorCode.honor_can_not_open_box1;
      }
      List<PropSimple> items = new ArrayList<>();
      if(itemStr != null && !"".equals(itemStr)) {
        items = PropSimple.parseItems(itemStr);
      }
      ImmutableList<Integer> drops = null;
      if(dropStr != null && !"".equals(dropStr)) {
        drops = ImmutableList.copyOf(StringUtil.toIntegerArray(dropStr, StringUtil.COMMA));
      }
      List<PropSimple> props = propManager.getPropSimples(items, drops);
      propManager.addPropList(teamId, props, true,
          ModuleLog.getModuleLog(EModuleCode.球星荣耀, "球星荣耀"));
      hDiv.setBoxStateIsGet(boxId);
      honorMatch.setDiv0(map);
      honorMatch.save();
      UpdateBoxReward.Builder boxBuilder = UpdateBoxReward.newBuilder();
      boxBuilder.setDivId(divId);
      boxBuilder.setId(boxId);
      boxBuilder.setState(hDiv.getBoxState(boxId));
      sendMessage(teamId, boxBuilder.build(), ServiceCode.honor_update_box_reward_info);
      
      return ErrorCode.Success;
    }
    
    /**
     * 今日剩余挑战次数
     * @param teamId
     * @return
     */
    private int leftChallengeNum(long teamId) {
      int honorChallengeCount = ConfigConsole.getGlobal().honorChallengeCount;
      int buyNum = teamNumManager.getUsedNum(teamId,TeamNumType.honorMatch);
      int dayChallengeNum = getDayNum(teamId,RedisKey.HonorMatch_challenge_day);
      return Math.max(0, honorChallengeCount + buyNum - dayChallengeNum);
    }
    
    public static int getLvIndex(int lvId) {
      //TODO id规则不能改
      return lvId % 100;
    }
    
    /**
     * 检查能不能打
     * @param teamId
     * @param lvId
     * @param challengeType 1-挑战；2-扫荡
     */
    public ErrorCode checkChallenge(long teamId,int lvId,int challengeType) {
      HonorLevBean honorLevBean = HonorMatchConsole.getLevBean(lvId);
      if(honorLevBean == null) {
        return ErrorCode.honor_no_lv; 
      }
      Team team = teamManager.getTeam(teamId);
      if(team.getLevel() < honorLevBean.getTeamLev()) {
        return ErrorCode.honor_team_level;
      }
      //次数判断  时间判断
      int leftChallengeNum = leftChallengeNum(teamId);
      if(leftChallengeNum <= 0) {
        return ErrorCode.limit_challenge_num;
      }
      HonorMatch honorMatch = getHonorMatch(teamId);
      Map<Integer,HDiv> map = honorMatch.getDivMap();
      if(honorLevBean.getEnablePreId() > 0) {
        HonorLevBean lastHonorLevBean = HonorMatchConsole.getLevBean(honorLevBean.getEnablePreId());
        HDiv div = map.get(lastHonorLevBean.getDivId());
        if(div == null || (div != null && div.getStart(getLvIndex(lastHonorLevBean.getId())) < 1)){
          return ErrorCode.honor_can_not_challenge_1;
        }
      }
      HDiv currDiv = map.get(honorLevBean.getDivId());
      if(challengeType == 1) {
        if(currDiv != null) {
          if(currDiv.getStart(getLvIndex(lvId)) >= 3 && honorLevBean.getFinish() == 0) {
            return ErrorCode.limit_challenge_3star;
          }
        }
      }else if(challengeType == 2) {
        if(honorLevBean.getFinish() == 0) {
          return ErrorCode.honor_can_not_saodang;
        }
        if(currDiv == null) {
          return ErrorCode.honor_can_not_saodang;
        }
        if(currDiv.getStart(getLvIndex(lvId)) < 3) {
          return ErrorCode.honor_can_not_saodang;
        }
      }
      return ErrorCode.Success;
    }
    
    private ErrorCode start(long teamId,int lvId) {
      HonorLevBean honorLevBean = HonorMatchConsole.getLevBean(lvId);
      if(honorLevBean == null) {
        return ErrorCode.honor_no_lv; 
      }
      ErrorCode checked = checkChallenge(teamId, lvId, 1);
      if(checked != ErrorCode.Success) {
        return checked;
      }
      int npcId = honorLevBean.getNpcId();
      BattleSource bs = localBattleManager.buildBattle(EBattleType.honorMatch,
          teamId,npcId, teamId);
      BattleContxt bc = localBattleManager.defaultContext(this::endMatch);
      BattleAttribute ba = new BattleAttribute(teamId);
      ba.addVal(EBattleAttribute.honorMatch_lv_id, lvId);
      bs.addBattleAttribute(ba);
      BattleAllStar bh = new BattleAllStar(bs);
      localBattleManager.initBattleWithContext(bh, bs, bc);
      localBattleManager.start(bs, bc);
      return ErrorCode.Success;
    }
    
    private void quickEndMatch(long teamId, int lvId, BattleSource bs) {
      HonorLevBean honorLevBean = HonorMatchConsole.getLevBean(lvId);
      List<PropSimple> props = getReward(honorLevBean,saodang_star);
      EndReport report = bs.getEndReport();
      report.appendWinAwardList(props);
      HonorMatchQuickMatchPushResp.Builder resp = HonorMatchQuickMatchPushResp.newBuilder();
      if (!ListsUtil.isEmpty(report.getWinAwardList())) {
          resp.addAllProps(PropManager.getPropSimpleListData(report.getWinAwardList()));
      }
      HonorMatchDiv.Builder divDuilder = honorMatchDivBuilder(teamId,honorLevBean.getDivId());
      resp.setDiv(divDuilder);
      resp.setLevRid(lvId);
      localBattleManager.sendEndMain(bs, true);
      sendMessage(teamId, resp.build(), ServiceCode.honor_Match_Quick_Match_Push);
  }
    
    public void gmCompletePass(long teamId,int divId,int lvNum,int star) {
      HonorMatch honorMatch = getHonorMatch(teamId);
      Map<Integer,HDiv> map = honorMatch.getDivMap();
      Set<HonorLevBean> list = HonorMatchConsole.getLevsOfDiv(divId);
      if(list == null) {
        return;
      }
      if(lvNum > list.size()) {
        lvNum = list.size();
      }
      if(star < 0 || star > 3) {
        star = 1;
      }
      int index = 0;
      for(HonorLevBean bean : list) {
        index++;
        if(index > lvNum) {
          break;
        }
        HDiv div = map.get(divId);
        if(div == null) {
          div = HDiv.valueOf(divId);
        }
        if(div.getStart(getLvIndex(bean.getId())) == 0) {
          div.setStar(star, getLvIndex(bean.getId()));
          calcTotalStar(div);
          refreshBoxState(div);
          boolean allPass = bean.getId() == HonorMatchConsole.getMaxLvIDByDivId(bean.getDivId());
          if(allPass) {
            int nextDivId = bean.getDivId() + 1;
            if(HonorMatchConsole.getLevsOfDiv(nextDivId) != null) {
              honorMatch.setMaxDiv(nextDivId);
            }
          }else if(bean.getDivId() > honorMatch.getMaxDiv()) {
            honorMatch.setMaxDiv(bean.getDivId());
          }
          map.put(div.getDivId(), div);
        }
      }
      honorMatch.setDiv0(map);
      honorMatch.save();
    }
    
    private void endMatch(BattleSource bs) {
      try {
        final EndReport report = bs.getEndReport();
        final long teamId = report.getHomeTeamId();
        final BattleAttribute ba = bs.getAttributeMap(teamId);
        final int lvId = ba.getVal(EBattleAttribute.honorMatch_lv_id);
        if (GameSource.isNPC(teamId)) {
          log.warn("honorMatch. tid {} lvId {} home is npc", teamId,
              lvId);
            localBattleManager.sendEndMain(bs, true);
          return;
        }
        final boolean win = report.getWinTeamId() == report.getHomeTeamId();
        if (win) {//胜利
          HonorMatch honorMatch = getHonorMatch(teamId);
          HonorLevBean honorLevBean = HonorMatchConsole.getLevBean(lvId);
          if(honorLevBean != null) {
            int star = calcStar(teamId, maxStar, honorLevBean, bs);
            if(star > 0) {
              report.addAdditional(EBattleAttribute.Main_Match_Star, star);
              Map<Integer,HDiv> divMap = honorMatch.getDivMap();
              HDiv hDiv = divMap.get(honorLevBean.getDivId());
              if(hDiv == null) {
                hDiv = HDiv.valueOf(honorLevBean.getDivId());
              }
              hDiv.setStar(star, getLvIndex(lvId));
              //totalStarNum
              calcTotalStar(hDiv);
              refreshBoxState(hDiv);
              divMap.put(honorLevBean.getDivId(), hDiv);
              boolean allPass = honorLevBean.getId() == HonorMatchConsole.getMaxLvIDByDivId(honorLevBean.getDivId());
              if(allPass) {
                int nextDivId = honorLevBean.getDivId() + 1;
                if(HonorMatchConsole.getLevsOfDiv(nextDivId) != null) {
                  honorMatch.setMaxDiv(nextDivId);
                  pushMaxDivId(teamId);
                }
              }else if(honorLevBean.getDivId() > honorMatch.getMaxDiv()) {
                honorMatch.setMaxDiv(honorLevBean.getDivId());
                pushMaxDivId(teamId);
              }
              //save
              honorMatch.setDiv0(divMap);
              honorMatch.save();
              //reward
              List<PropSimple> props = getReward(honorLevBean,star);
              report.appendWinAwardList(props);
            }
          }
        }
        localBattleManager.sendEndMain(bs, true);
      } catch (Exception e) {
        log.error("endMatch " + e.getMessage(), e);
      }
    }
    
    public List<PropSimple> getReward(HonorLevBean honorLevBean,int star){
      String rewardItem = honorLevBean.reward(star);
      List<PropSimple> items = new ArrayList<>();
      if(rewardItem != null && !"".equals(rewardItem)) {
        items = PropSimple.parseItems(rewardItem);
      }
      String drop = honorLevBean.drop(star);
      ImmutableList<Integer> drops = null;
      if(drop != null && !"".equals(drop)) {
        drops = ImmutableList.copyOf(StringUtil.toIntegerArray(drop, StringUtil.COMMA));
      }
      List<PropSimple> props = propManager.getPropSimples(items, drops);
      return props;
    }
    
    /**
     * 更新宝箱状态
     * @param hDiv
     */
    public void refreshBoxState(HDiv hDiv) {
      if(hDiv.getBox1State() != HDiv.box_state_no && hDiv.getBox2State() != HDiv.box_state_no && hDiv.getBox3State() != HDiv.box_state_no) {
        return;
      }
      HonorDivBean honorDivBean = HonorMatchConsole.getDivBean(hDiv.getDivId());
      if(hDiv.getBox1State() == HDiv.box_state_no && hDiv.getTotalStarNum() >= honorDivBean.getStarNum1()) {
        hDiv.setBox1State(HDiv.box_state_can_get);
      }
      if(hDiv.getBox2State() == HDiv.box_state_no && hDiv.getTotalStarNum() >= honorDivBean.getStarNum2()) {
        hDiv.setBox2State(HDiv.box_state_can_get);
      }
      if(hDiv.getBox3State() == HDiv.box_state_no && hDiv.getTotalStarNum() >= honorDivBean.getStarNum3()) {
        hDiv.setBox3State(HDiv.box_state_can_get);
      }
    }
    
    /** 根据比赛数据计算星级 */
    private int calcStar(long teamId, int maxStar, HonorLevBean honorLevBean, BattleSource bs) {
        log.trace("honor calcstar. tid {} lev {}. battle id {} home {} away {} score {}:{}", teamId, honorLevBean.getId(),
            bs.getInfo().getBattleId(), bs.getHome().getTeamId(), bs.getAway().getTeamId(),
            bs.getHome().getScore(), bs.getAway().getScore());
        int star = 1;
        EndReport report = bs.getEndReport();
        for (Star starCfg : honorLevBean.getStars().values()) {
            ScoreResult sr = new ScoreResult();
            for (Integer wcid : starCfg.getWinConditionId()) {
                MMatchConditionBean wcb = HonorMatchConsole.getWinCondition(wcid);
                if (wcb == null) {
                    sr.allMatch = false;
                    break;
                }
                starLog.trace("mmatch calcstar. tid {} star {} wc {}", teamId, starCfg.getStar(), wcb);
                sr.allMatch = MainMatchManager.calcMatchData(teamId, wcb, bs, report);
                if (!sr.allMatch) {// false
                    break;
                }
            }
            if (sr.allMatch) {//匹配当前星级所有要求
                star = starCfg.getStar();
                break;
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("mmatch calcstar. tid {} star {}", teamId, star);
        }
        return star;
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
     * 玩家球员荣耀数据
     * @param teamId
     * @return
     */
    public HonorMatch getHonorMatch(long teamId) {
      HonorMatch honorMatch = honorMatchMap.get(teamId);
      if(honorMatch == null) {
        honorMatch = honorMatchDao.getHonorMatchByTeam(teamId);
        if(honorMatch == null) {
          honorMatch = new HonorMatch();
          honorMatch.setTeamId(teamId);
          DateTime date = new DateTime();
          honorMatch.setCreateTime(date);
          honorMatch.setUpdateTime(date);
          honorMatch.setMaxDiv(HonorMatchConsole.minDivId);
          honorMatchDao.addHonorMatch(honorMatch);
        }
        honorMatchMap.put(teamId, honorMatch);
      }
      return honorMatch;
    }

    @Override
    public void offline(long teamId) {
      honorMatchMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
      honorMatchMap.remove(teamId);
    }
    
    public void resetChallengeNum(long teamId) {
      setDayNum(teamId, RedisKey.HonorMatch_challenge_day, 0);
    }
}
