package com.ftkj.manager.logic;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.EmailViewBean;
import com.ftkj.cfg.GroupWarSeasonBean;
import com.ftkj.cfg.GroupWarTierBean;
import com.ftkj.console.EmailConsole;
import com.ftkj.console.LeagueGroupWarConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.db.ao.logic.ILeagueAO;
import com.ftkj.db.domain.group.LeagueGroupSeasonPO;
import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.ELeagueGroupPos;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.buff.TeamBuff;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.league.groupwar.GroupTeam;
import com.ftkj.manager.league.groupwar.LeagueGroup;
import com.ftkj.manager.league.groupwar.LeagueGroupSeason;
import com.ftkj.manager.league.groupwar.PairPool;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.LeagueGroupPB;
import com.ftkj.proto.LeagueGroupPB.LgPKRoundData.Builder;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

/**
 * 战队赛赛季管理
 * @author lin.lin
 *
 */
public class LeagueGroupSeasonManager extends BaseManager {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	@IOC
	private ILeagueAO leagueAO;
	@IOC
	private LocalBattleManager localBattleManager;
	@IOC
	private LeagueGroupWarManager groupWarManager;
	@IOC
	private TeamDayStatsManager teamDayStatsManager;
	@IOC
	private TeamManager teamManager;
	@IOC
	private LeagueManager leagueManager;
	@IOC
	private BuffManager buffManager;
	@IOC
	private TeamEmailManager teamEmailManager;
	@IOC
	private PropManager propManager;
	// 调试开关
	@SuppressWarnings("unused")
	private static final boolean isDebug = false; 
	
	/**
	 * 当前赛季
	 */
	private LeagueGroupSeason season;
	
//	/**
//	 * 联盟战队ID（联盟ID + 战队编号） :  战队信息 </BR>
//	 * 开始比赛后就没存了
//	 */
//	private Map<Long, LeagueGroup> groupMatchMap;
	/**
	 * 匹配比赛中
	 */
	private List<PairPool> vsQueue;
	
	@Override
	public void instanceAfter() {
		EventBusManager.register(EEventType.活动定时器, this);
		//groupMatchMap = Maps.newConcurrentMap();
		vsQueue = Lists.newArrayList();
		// 查询最新赛季信息
		LeagueGroupSeasonPO seasonPO = leagueAO.getLeagueGroupSeason();
		if(seasonPO != null) {
			season = new LeagueGroupSeason(seasonPO);
		}else {
			GroupWarSeasonBean bean = LeagueGroupWarConsole.getTheSeasonById(1);
			if(bean == null) {
				return;
			}
			// 默认初始第一赛季，非开始状态
			createSeason(bean);
		}
		
		// 测试匹配结果
//		if(isDebug) {
//			addGroupToBattle(LeagueGroup.createNPCGroup(101, 1, "大宝剑NPC1", 100, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(101, 2, "大宝剑NPC2", 205, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(102, 1, "筋斗云NPC1", 105, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(102, 2, "筋斗云NPC2", 225, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(102, 3, "筋斗云NPC3", 215, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(103, 1, "无敌寂寞NPC1", 999, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(103, 2, "无敌寂寞NPC2", 922, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(103, 3, "无敌寂寞NPC3", 298, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(103, 4, "无敌寂寞NPC4", 601, getRandNPC()));
//			addGroupToBattle(LeagueGroup.createNPCGroup(104, 5, "一人一梦NPC5", 666, getRandNPC()));
//			// 测试补NPC
//			LeagueGroup t1 = LeagueGroup.createNPCGroup(103, 5, "无敌寂寞NPC5", 92581, getRandNPC());
//			t1.addJoinFail(1);
//			addGroupToBattle(t1);
//		}
	}
	
	@Subscribe
	private void everyDayRefreshJob(Date dateTime) {
		DateTime now = new DateTime(dateTime);
		everySecond(now);
	}
	
	/**
	 * 每秒钟
	 */
	private void everySecond(DateTime now) {
		if(season == null) {
			return;
		}
		work(now);
		// 每天0点检查是否开启新的赛季
		if(season.getStatus() == 2) {
			// 检查是否开启新赛季
			GroupWarSeasonBean bean = LeagueGroupWarConsole.getTheSeasonByTime(now);
			if(bean == null) {
				return;
			}
			createSeason(bean);
		}
		// 检查是否结束
		else if(season.getStatus() == 1 && DateTime.now().isAfter(season.getEndTime())){
			endSeason();
		}
		else if(season.getStatus() == 0 && DateTime.now().isAfter(season.getStartTime())) {
			startSeason();
		}
	}
	
	/**
	 * 创建新赛季
	 */
	private void createSeason(GroupWarSeasonBean bean) {
		log.debug("创建联盟战队赛，第{}赛季", bean.getId());
		season = new LeagueGroupSeason(new LeagueGroupSeasonPO(bean.getId(), bean.getName(), bean.getStartTime(), bean.getEndTime()));
		season.save();
	}
	
	/**
	 * 赛季开始
	 * @param bean
	 */
	private synchronized void startSeason() {
		log.warn("联盟战队赛第{}赛季开始, time {}", season.getId(), DateTimeUtil.getStringSql(DateTime.now()));
		int oldStatus = season.getStatus();
		season.updateStatus(1);
		// 初始化赛季数据
		groupWarManager.startSeasonInit(oldStatus, season);
	}
	
	private synchronized void endSeason() {
		log.warn("联盟战队赛第{}赛季结束, time {}", season.getId(), DateTimeUtil.getStringSql(DateTime.now()));
		season.updateStatus(2);
		// 发送赛季结束奖励
		Map<Integer, List<LeagueGroup>> rankMap = groupWarManager.getTierRankMap(1);
		EmailViewBean emailBean = EmailConsole.getEmailView(40007);
		for (int tierId : rankMap.keySet()) {
			List<LeagueGroup> list = rankMap.get(tierId);
			if(list == null || list.size() == 0) {
				continue;
			}
			// 段位
			GroupWarTierBean bean = LeagueGroupWarConsole.getTierById(tierId);
			// 赛季奖励
			String awardConfig = PropSimple.getPropStringByList(bean.getSeasonAwardList());
			//
			Set<Long> sendList = Sets.newHashSet();
			list.forEach(g-> sendList.addAll(g.getTeamIds()));
			sendList.forEach(teamId->{
				teamEmailManager.sendEmailFinal(teamId, emailBean.getType(), emailBean.getId(), 
						 emailBean.getTitle(), tierId + "", awardConfig);
			});
		}
		//
	}
	
	/**
	 * 每天比赛开始
	 */
	private void startEveryDayMatch() {
		vsQueue.clear();
		groupWarManager.clearStatus();
	}
	
	/**
	 * 每天比赛结束
	 */
	private void endEveryDayMatch() {
		vsQueue.clear();
		groupWarManager.clearStatus();
	}

//	/**
//	 * 是否在匹配或者比赛中
//	 * @param leagueGroupId
//	 * @return
//	 */
//	public boolean inBattle(long leagueGroupId) {
//		if(groupMatchMap.containsKey(leagueGroupId)) {
//			return true;
//		}
//		return false;
//	}

	/**
	 * 战队开始比赛，参与匹配
	 * @param group
	 */
	public synchronized void addGroupToBattle(LeagueGroup group) {
		log.warn("group {} join pool", group.getName());
		group.setStatus(1);
		group.save();
		vsQueue.add(new PairPool(group.getLeagueId(), group.getGroupId(), group.getScore(), DateTime.now().getMillis(), 0));
	}
	
	/**
	 * 比赛结束移除，推包状态变化
	 * @param group
	 */
	public synchronized void removeGroupBattle(LeagueGroup group) {
		if(group == null) {
			return;
		}
		// 恢复状态
		group.setStatus(0);
		group.save();
		groupWarManager.groupStatusChange(group);
	}

	/**
	 * 是否是比赛时间
	 * @return
	 */
	public boolean isMatchTime() {
		DateTime now = DateTime.now();
		int todayMillis = now.getMillisOfDay();
		if(todayMillis < LeagueGroupWarConsole.StartSecondOfDay || todayMillis > LeagueGroupWarConsole.EndSecondOfDay) {
			return false;
		}
		return season != null && season.getStatus() == 1;
	}

	/**
     * 玩家进行匹配后，进入匹配池，
     * 匹配池每20S排序一次
     * 排序后根据顺序由高到低两两匹配（由第一匹配第二，第三匹配第四），
     * 若匹配后分差少于100即匹配成功，开始比赛
     * 若匹配后分差大于100，则把第一名剔除重新匹配
     * 第一名则重新进入下一轮匹配池
     * 玩家第四次进入匹配池时必然会匹配成功（无视分差）
     */
	private synchronized void startPair(DateTime now) {
		// 控制20秒匹配1轮
		if(now.getSecondOfDay() % LeagueGroupWarConsole.PairEachSeconde != 0) {
			return;
		}
		if(!isMatchTime()) {
			return;
		}
		Map<Object, List<PairPool>> leagueGroupList = vsQueue.stream()
				.collect(Collectors.groupingBy(g->g.getLeagueId(),Collectors.toList()));
		//
		if(leagueGroupList.size() < 1) {
			// 少于1个盟的战队，不匹配
			return;
		}
		// 匹配时间排序
		vsQueue.sort((o1, o2) -> Long.compare(o1.getStartTime(), o2.getStartTime()));
		log.warn("league gruop pair pool size {}", vsQueue.size());
		// 开始匹配
		List<PairPool> success = Lists.newArrayList();
		pairMatch(success);
		pairSuccess(success);
		// 强制匹配
		success = Lists.newArrayList();
		checkPairMoreCount(success);
		pairSuccess(success);
		log.warn("league gruop pair pool size {}", vsQueue.size());
		// 匹配超时的移除
		checkPairTimeOut();
	}
	
	/**
	 * 超时的移除
	 */
	private void checkPairTimeOut() {
		if(vsQueue.size() < 1) {
			return;
		}
		final long nowTime = DateTime.now().getMillis();
		List<PairPool> removeList = vsQueue.stream().filter(p-> nowTime - p.getStartTime() > 70 * 1000).collect(Collectors.toList());
		if(removeList.size() < 1) {
			return;
		}
		vsQueue.removeAll(removeList);
		removeList.stream().forEach(p-> {
			LeagueGroup group = groupWarManager.getLeagueGroup(p.getLeagueId(), p.getGroupId());
			removeGroupBattle(group);
		});
		log.warn("League group pair timeout size {}", removeList.size());
	}

	/**
	 * 开始比赛会移除
	 * @param success
	 */
	private void pairSuccess(List<PairPool> success) {
		if(success.size() > 0) {
			// 移除匹配
			log.warn("league grop pair success size {}, npc size {}", success.size(), success.stream().filter(p->p.isNPC()).count());
			vsQueue.removeAll(success);
			for(int i=0; i < success.size(); i+=2) {
				// 开始比赛
				PairPool a = success.get(i);
				PairPool b = success.get(i+1);
				LeagueGroup home = a.isNPC() ? createNPCGroup() : groupWarManager.getLeagueGroup(a.getLeagueId(), a.getGroupId());
				LeagueGroup away = b.isNPC() ? createNPCGroup() : groupWarManager.getLeagueGroup(b.getLeagueId(), b.getGroupId());
				GruopWarVS vs = new GruopWarVS(this, localBattleManager, home, away);
				log.warn("league group pair {} success : {}", i % 2 + 1, vs.toString());
				vs.startMatch();
			}
		}
	}
	
	private LeagueGroup createNPCGroup() {
		// 加入一些随机性
		int startId = (10000 * RandomUtil.randInt(3) + 1) + RandomUtil.randInt(100);
		List<NPCBean> list = NPCConsole.getRoundNpcId(3, Lists.newArrayList(), startId, 99999);
		return LeagueGroup.createNPCGroup(list, list.get(0).getNpcName() + "队");
	}
	
	private synchronized void pairMatch(List<PairPool> success) {
		//
		Iterator<PairPool> pairPool = vsQueue.iterator();
		Iterator<PairPool> aIterator = vsQueue.iterator();
		//
		boolean finish = false;
		while(!finish && aIterator.hasNext()) {
			PairPool a = aIterator.next();
			finish = pairVS(a, pairPool, success);
		}
	}
	
	private void checkPairMoreCount(List<PairPool> success) {
		Iterator<PairPool> pairPool = vsQueue.iterator();
		List<PairPool> failList = Lists.newArrayList();
		GroupWarTierBean bean = LeagueGroupWarConsole.getTierById(LeagueGroupWarConsole.NoReaderTier);
		while(pairPool.hasNext()) {
			PairPool a = pairPool.next();
			if(success.indexOf(a) == -1 && a.getCount() > LeagueGroupWarConsole.PairMaxCount) {
				// 非本盟最小分差直接匹配上
				List<PairPool> blist = vsQueue.stream()
					.filter(l-> l.getLeagueId() != a.getLeagueId())
					.filter(l-> success.indexOf(l) == -1 || failList.indexOf(l) != -1)
					.sorted(new Comparator<PairPool>() {
						@Override
						public int compare(com.ftkj.manager.league.groupwar.PairPool o1,
								com.ftkj.manager.league.groupwar.PairPool o2) {
							if(o1.getScore() == o2.getScore()) {
								return 0;
							}
							return Math.abs(a.getScore()-o1.getScore()) - Math.abs(a.getScore()-o2.getScore());
						}
					}).collect(Collectors.toList());
				//
				if(blist.size() >= 1) {
					success.add(a);
					success.add(blist.get(0));
				}
				// a.getJoinFailCount() > 0 && 
				else if(a.getScore() <= bean.getMinRating()) {
					// 补NPC
					success.add(a);
					success.add(PairPool.CreateNPC());
				}else {
					failList.add(a);
				}
			}else {
				a.addCount(1);
			}
		}
//		log.warn("移除配对失败{}", failList);
		if(failList.size() < 1) {
			return;
		}
		log.warn("League group pair fail size {}", failList.size());
		vsQueue.removeAll(failList);
		failList.forEach(p-> {
			LeagueGroup group = groupWarManager.getLeagueGroup(p.getLeagueId(), p.getGroupId());
			removeGroupBattle(group);
		});
	}
	
	private String getTeamName(long teamId) {
		return teamManager.getTeamNameById(teamId);
	} 
	
	/**
	 * 匹配
	 * @param PairPool 匹配池
	 * @param success 成功配对的
	 * @param pairPoolMap 4次不成功强制配对的
	 * @return 
	 */
	private boolean pairVS(PairPool a, Iterator<PairPool> PairPool, List<PairPool> success) {
		if(!PairPool.hasNext()) {
			return true;
		}
		if(success.indexOf(a) != -1) {
			return false;
		}
		while(PairPool.hasNext()) {
			PairPool b = PairPool.next();
			if(a.getLeagueId() == b.getLeagueId()) {
				continue;
			}
			if(Math.abs(a.getScore() - b.getScore()) > LeagueGroupWarConsole.PairAbsScore) {
				continue;
			}
			// 过滤掉已匹配
			if(success.indexOf(b) != -1) {
				continue;
			}
			// 匹配成功
			success.add(a);
			success.add(b);
			return false;
		}
		return false;
	}
	
	/**
	 * 匹配并开始比赛
	 * @param now 
	 */
	public void work(DateTime now) {
		checkStartMatch(now);
		everyWeekAward(now);
		startPair(now);
	}
	
	/**
	 * 检查每天比赛开始
	 * @param now
	 */
	private void checkStartMatch(DateTime now) {
		int todayMillis = now.getMillisOfDay();
		if(todayMillis == LeagueGroupWarConsole.StartSecondOfDay) {
			startEveryDayMatch();
		}
		else if(todayMillis == LeagueGroupWarConsole.EndSecondOfDay) {
			endEveryDayMatch();
		}
	}

	/**
	 * 每周工资帽奖励，周日21点
	 * @param now
	 */
	private void everyWeekAward(DateTime now) {
		if(season.getStatus() != 1) {
			return;
		}
		if(now.getDayOfWeek() != 7) {
			return;
		}
		// 9点发奖励
		if(now.getMillisOfDay() != LeagueGroupWarConsole.Week_Award_Send_Hour) {
			return;
		}
		//
		// 本盟所有成员增加工资帽一周
		 EmailViewBean emailBean = EmailConsole.getEmailView(40006);
		 DateTime endTime = now.plusDays(7);
		 int rank = 1;
		 // 前8排名奖励
		 for(int leagueId : groupWarManager.getLeagueRankIds(9999999)) {
			 int priceCap = LeagueGroupWarConsole.getWeekAwardByRank(rank).getPriceCap();
			 final int currRank = rank;
			 TeamBuff buff = new TeamBuff(EBuffKey.联盟组队赛, EBuffType.工资帽, new int[]{priceCap}, endTime, true);
			 leagueManager.getLeagueTeamList(leagueId).stream()
			 .forEach(teamId-> {
				 buffManager.addBuff(teamId, buff);
				 teamEmailManager.sendEmailFinal(teamId, emailBean.getType(), emailBean.getId(), 
						 emailBean.getTitle(), currRank + "," + priceCap, "");
			 });
			 //
			 rank++;
		 }
	}
	
	/**
	 * 战队赛主界面
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_getGroupWarMain)
	public void getGroupWarMain() {
		if(season == null) {
			sendMessage(LeagueGroupPB.LgGroupWarMainData.newBuilder()
					.setSeasonId(0)
					.setStartTime(0L)
					.setEndTime(0L)
					.setStatus(0)
					.setTodayAwardStatus(0)
	                .build());
			return;
		}
		long teamId = getTeamId();
		// 今日领奖状态
		int awardStatus = 0;
		int pkCount = teamDayStatsManager.getTeamDayStatistics(teamId).getPkCount(EBattleType.联盟组队赛);
		if(pkCount >= LeagueGroupWarConsole.DayAwardMinPKCount) {
			String key = RedisKey.getDayKey(teamId, RedisKey.League_Group_War_Day_Award);
			int status = redis.getIntNullIsZero(key);
			if(status == 1) {
				awardStatus = 2; // 已领
			}else {
				LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
				LeagueGroup group = null;
				int privity = 0;
		        if (lt != null) {
		        	group = groupWarManager.findTeamGruop(lt.getLeagueId(), lt.getTeamId());
		        	privity = group == null ? 0 : group.getTeamPrivity(teamId);
		        }
		        if(privity == 100) {
		        	awardStatus = 1;
		        }
			}
		}
        //
        sendMessage(LeagueGroupPB.LgGroupWarMainData.newBuilder()
        		.setSeasonId(season.getId())
        		.setStartTime(season.getStartTime().getMillis())
        		.setEndTime(season.getEndTime().getMillis())
        		.setStatus(season.getStatus())
        		.setTodayAwardStatus(awardStatus)
                .build());
	}

	/**
	 * 领取每日奖励
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_get_day_award)
	public void getEveryDayAward() {
		long teamId = getTeamId();
		String key = RedisKey.getDayKey(teamId, RedisKey.League_Group_War_Day_Award);
		int awardStatus = redis.getIntNullIsZero(key);
		if(awardStatus == 1) {
			log.debug("已领取过奖励!");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Active_7.code).build());
			return;
		}
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
		if(lt == null) {
			log.debug("没有加入联盟！");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
			return;
		}
		LeagueGroup group = groupWarManager.findTeamGruop(lt.getLeagueId(), lt.getTeamId());
		if(group == null) {
			log.debug("没有加入战队！");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
			return;
		}
		if(teamDayStatsManager.getTeamDayStatistics(teamId).getPkCount(EBattleType.联盟组队赛) < LeagueGroupWarConsole.DayAwardMinPKCount) {
			log.debug("比赛场次数不足！");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.MMatch_Num_Beyond_Max.code).build());
			return;
		}
		if(group.getTeamPrivity(teamId) < 100) {
			log.debug("比赛场次数不足！");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.MMatch_Num_Beyond_Max.code).build());
			return;
		}
        int teirId = groupWarManager.getTierInfo(group).getTierId();
        GroupWarTierBean bean = LeagueGroupWarConsole.getTierById(teirId);
        propManager.addPropList(teamId, bean.getDayAwardList(), true, ModuleLog.getModuleLog(EModuleCode.联盟组队赛	, "每日奖励"));
        redis.set(key, "1");
        sendMessage(DefaultPB.DefaultData.newBuilder()
        		.setCode(ErrorCode.Success.code)
        		.setMsg(PropSimple.getPropStringByList(bean.getDayAwardList()))
        		.build());
	}

	/**
	 * 某场比赛结束
	 * @param vs
	 */
	public void gruopWarVSEnd(GruopWarVS vs) {
		removeGroupBattle(vs.getHomeGroup());
		removeGroupBattle(vs.getAwayGroup());
		groupWarManager.GroupBattleEnd(vs);
	}
	
	/**
	 * 比赛
	 */
	public static class GruopWarVS implements BattleEnd, BattleRoundReport {
		Map<Long, GruopBattle> battleStatus;
		LeagueGroup homeGroup;
		LeagueGroup awayGroup;
		LocalBattleManager localBattleManager;
		LeagueGroupSeasonManager leagueGroupSeasonManager;
		
		public GruopWarVS(LeagueGroupSeasonManager leagueGroupSeasonManager, LocalBattleManager localBattleManager, LeagueGroup homeGroup, LeagueGroup awayGroup) {
			super();
			this.battleStatus = Maps.newConcurrentMap();
			this.homeGroup = homeGroup;
			this.awayGroup = awayGroup;
			this.leagueGroupSeasonManager = leagueGroupSeasonManager;
			this.localBattleManager = localBattleManager;
		}
		
		/**
		 * 开始所有比赛
		 */
		private void startMatch() {
			for(ELeagueGroupPos pos : ELeagueGroupPos.values()) {
				GroupTeam homeTeam = homeGroup.getBattleTeamByPos(pos.getId());
				GroupTeam awayTeam = awayGroup.getBattleTeamByPos(pos.getId());
				BattleSource bs = localBattleManager.buildBattle(EBattleType.联盟组队赛, localBattleManager.getNewBattleId(), 
						homeTeam.getTeamId(), 
						awayTeam.getTeamId(),
						null, null, 0);
				localBattleManager.start(bs, this, this);
				addBattle(pos.getId(), bs, homeTeam.getTeamId(), awayTeam.getTeamId());
			}
			homeGroup.setStatus(2);
			homeGroup.save();
			awayGroup.setStatus(2);
			awayGroup.save();
			// 推包
			leagueGroupSeasonManager.sendMessageTeamIds(homeGroup.getTeamIds(), 
	        		DefaultPB.DefaultData.newBuilder().setCode(0).setBigNum(homeGroup.getStatus()).build(), 
	        		ServiceCode.LeagueGroupWarManager_push_groupStatus);
			leagueGroupSeasonManager.sendMessageTeamIds(awayGroup.getTeamIds(), 
	        		DefaultPB.DefaultData.newBuilder().setCode(0).setBigNum(awayGroup.getStatus()).build(), 
	        		ServiceCode.LeagueGroupWarManager_push_groupStatus);
		}
		
		public void addBattle(int position, BattleSource bs, long home, long away) {
			GruopBattle vs = new GruopBattle(position, bs, leagueGroupSeasonManager.getTeamName(home), leagueGroupSeasonManager.getTeamName(away));
			battleStatus.put(bs.getBattleId(), vs);
		}
		
		@Override
		public synchronized void end(BattleSource source) {
			localBattleManager.sendEndMain(source, true);
			// 内部业务
			GruopBattle gb = battleStatus.get(source.getBattleId());
			gb.updateBS(source);
			// 已经全部完成比赛
			if(battleStatus.values().stream().allMatch(m-> m.isEnd())) {
				leagueGroupSeasonManager.gruopWarVSEnd(this);
			}
		}
		
		/**
		 * 取获得胜利的队伍
		 * @return
		 */
		public long getWinGroupId() {
			// 比分总和的差，如果比分相同，3盘2胜
			int homeScore = 0;
			int awayScore = 0;
			int homeWin = 0;
			int awayWin = 0;
			for(GruopBattle gb : battleStatus.values()) {
				homeScore += gb.bs.getHomeScore();
				awayScore += gb.bs.getAwayScore();
				if(gb.bs.getHomeScore() > gb.bs.getAwayScore()) {
					homeWin += 1;
				}
				else if(gb.bs.getHomeScore() < gb.bs.getAwayScore()) {
					awayWin += 1;
				}
			}
			if(homeScore != awayScore) {
				return homeScore > awayScore ? homeGroup.getLeagueGroupId() : awayGroup.getLeagueGroupId();
			}
			// 按胜场出得出胜负队
			if(homeWin != awayWin) {
				return homeWin > awayWin ? homeGroup.getLeagueGroupId() : awayGroup.getLeagueGroupId();
			}
			// 平局?
			return homeGroup.getLeagueGroupId();
		}
		
		@Override
		public String toString() {
			return homeGroup.getName() + " VS " + awayGroup.getName();
		}
		
		/**
		 * 取分差
		 * @return
		 */
		public int getScoreGap() {
			int homeScore = 0;
			int awayScore = 0;
			for(GruopBattle gb : battleStatus.values()) {
				homeScore += gb.bs.getHomeScore();
				awayScore += gb.bs.getAwayScore();
			}
			return Math.abs(homeScore - awayScore);
		}

		public LeagueGroup getHomeGroup() {
			return homeGroup;
		}

		public void setHomeGroup(LeagueGroup homeGroup) {
			this.homeGroup = homeGroup;
		}

		public LeagueGroup getAwayGroup() {
			return awayGroup;
		}

		public void setAwayGroup(LeagueGroup awayGroup) {
			this.awayGroup = awayGroup;
		}

		/**
		 * 每回合处理
		 * @param bs
		 * @param report
		 */
		@Override
		public void round(BattleSource bs, RoundReport report) {
			// A队 : B 队
			long battleId = bs.getInfo().getBattleId();
	        String key = ServiceConsole.getBattleKey(battleId);
	        leagueGroupSeasonManager.sendMessage(key, BattlePb.battleRoundMainData(bs, report), ServiceCode.Battle_Round_Push);
	        // 统计总队的比分.
	        LeagueGroupPB.LgPKRoundData data = getScoreDetailData(battleId, true);
	        leagueGroupSeasonManager.sendMessageTeamIds(Lists.newArrayList(bs.getHomeTid(), bs.getAwayTid()), data, ServiceCode.LeagueGroupWarManager_push_match_detail);
		}
		
		/**
		 * 整队比分明细数据
		 * @return
		 */
		public LeagueGroupPB.LgPKRoundData getScoreDetailData(long battleId, boolean filter) {
			Builder data = LeagueGroupPB.LgPKRoundData.newBuilder();
	        List<LeagueGroupPB.LgPKMatchData> dataList = Lists.newArrayList();
	        int homeScore = 0;
	        int awayScore = 0;
	        for(GruopBattle gb : battleStatus.values()) {
	        	int homeCurr = gb.bs.getHomeScore();
	        	int awayCurr = gb.bs.getAwayScore();
	        	homeScore += homeCurr;
				awayScore += awayCurr;
				if(filter && battleId == gb.bs.getBattleId()) {
					continue;
	        	}
				dataList.add(LeagueGroupPB.LgPKMatchData.newBuilder()
						.setHomeName(gb.getHomeName())
						.setAwayName(gb.getAwayName())
						.setHomeScore(homeCurr)
						.setAwayScore(awayCurr)
						.setPos(gb.position)
						.build());
			}
	        data.setHomeName(homeGroup.getName());
	        data.setAwayName(awayGroup.getName());
	        data.setHomeScore(homeScore);
	        data.setAwayScore(awayScore);
	        data.addAllMatchDetail(dataList);
			return data.build();
		}
		
	}
	
	public static class GruopBattle {
		private int position; // 对战的位置
		private BattleSource bs;
		private boolean isEnd = false;
		private String homeName;
		private String awayName;
		
		public GruopBattle(int position, BattleSource bs, String homeName, String awayName) {
			super();
			this.position = position;
			this.bs = bs;
			this.homeName = homeName;
			this.awayName = awayName;
		}
		
		public void updateBS(BattleSource bs) {
			this.bs = bs;
			this.isEnd = true;
		}
		
		public boolean isEnd() {
			return isEnd;
		}

		public String getHomeName() {
			return homeName;
		}

		public String getAwayName() {
			return awayName;
		}
		
	}

	public LeagueGroupSeason getSeason() {
		return season;
	}
	
	
}
