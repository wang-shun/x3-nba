package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.StreetBallBean;
import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.console.BattleConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.StreetBallConsole;
import com.ftkj.db.ao.logic.IStreetBallAO;
import com.ftkj.db.domain.StreetBallPO;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.street.StreetBall;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.StreetBallPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 街球副本
 * @author Jay
 * @time:2017年7月17日 下午3:54:43
 */
public class StreetballManager extends BaseManager implements OfflineOperation {

	@IOC
	private IStreetBallAO streetBallAO;
	@IOC
	private TeamManager teamManager;
	@IOC
	private PropManager propManager;
	@IOC
	private BuffManager buffManager;
	@IOC
	private VipManager vipManager;
	@IOC
	private TaskManager taskManager;
	@IOC
	private LocalBattleManager localBattleManager;
	private BattleEnd stageBattleEnd;
	
	private Map<Long, StreetBall> streetBallMap;
	
	@Override
	public void instanceAfter() {
		//
		streetBallMap = Maps.newConcurrentMap();
		/*
		 * 比赛结束回调监听
		 */
		stageBattleEnd = new BattleEnd() {
			@Override
			public void end(BattleSource source) {
				EndReport report = source.getEndReport();
				long winId = report.getWinTeamId();
				int stageId = source.getAttributeMap(0).getVal(EBattleAttribute.街球赛挑战关卡);
				// 胜利
				if(!GameSource.isNPC(winId)) {
					StreetBall sb = getTeamStreetBall(winId);
					endPK(stageId,sb, null, Math.abs(report.getHomeScore()-report.getAwayScore()) >= StreetBallConsole.Win_least_Score);
					taskManager.updateTask(report.getHomeTeamId(), ETaskCondition.街球赛, 1, ""+report.getAwayTeamId());
				}
				localBattleManager.sendEndMain(source,true);
			}
		};
	}
	
	/**
	 * 比赛结束处理
	 * @param sbId 
	 * @param sb
	 * @param list 
	 */
	private void endPK(int sbId, StreetBall sb, List<PropSimple> list, boolean isPass) {
		//
		StreetBallBean bean = StreetBallConsole.getStrBallBean(sbId);
		int type = bean.getType();
		// 首次通关
		boolean isFirst = sb.getTypeStage(type) < bean.getStage();
		// 首次通关不扣次数
		if(isFirst && isPass) {
			sb.setTypeStage(type, sb.getTypeStage(type)+1);
			sb.save();
		}else {
			addTodayFight(sb.getTeamId(), bean.getType());
		}
		StreetBallPB.StreetBallStage stdata = getStreetBallStage(sb.getTeamId(), type, sb.getTypeStage(type));
		if(list != null) {
			propManager.addPropList(sb.getTeamId(), list, true, ModuleLog.getModuleLog(EModuleCode.街球副本, ""));
			//
			sendMessage(sb.getTeamId(), StreetBallPB.SweepStreetBallStage.newBuilder()
					.setStage(stdata)
					.addAllAwardList(PropManager.getPropSimpleListData(list))
					.build(), ServiceCode.StreetBallManager_sweep_push);
		}else {
			sendMessage(sb.getTeamId(), stdata, ServiceCode.StreetBallManager_endPK_push);
		}
	}
	
	/**
	 * 取玩家街球副本记录
	 * @param teamId
	 * @return
	 */
	private StreetBall getTeamStreetBall(long teamId) {
		StreetBall sb = streetBallMap.get(teamId);
		if(sb == null) {
			StreetBallPO  po = streetBallAO.getStreetBallByTeam(teamId);
			if(po == null) {
				po = new StreetBallPO();
				po.setTeamId(teamId);
			}
			sb = new StreetBall(po);
			streetBallMap.put(teamId, sb);
			GameSource.checkGcData(teamId);
		}
		return sb;
	}
	
	@Override
	public void offline(long teamId) {
		streetBallMap.remove(teamId);
	}

    @Override
    public void dataGC(long teamId) {
        streetBallMap.remove(teamId);
    }

    /**
	 *  
	 */
	@ClientMethod(code = ServiceCode.StreetBallManager_showView)
	public void showView() {
		// 返回各个关卡最新的挑战关卡数
		StreetBall sb = getTeamStreetBall(getTeamId());
		sendMessage(getStreetBallData(sb));
	}
	
	private StreetBallPB.StreetBallData getStreetBallData(StreetBall sb) {
		List<StreetBallPB.StreetBallStage> datalist = Lists.newArrayList();
		for(int type=1; type<=5; type++) {
			datalist.add(getStreetBallStage(sb.getTeamId(), type, sb.getTypeStage(type)));
		}
		return StreetBallPB.StreetBallData.newBuilder().addAllStageList(datalist).build();
	}
	
	
	private StreetBallPB.StreetBallStage getStreetBallStage(long teamId, int type, int stage) {
		return StreetBallPB.StreetBallStage.newBuilder()
				.setType(type)
				.setStageMax(stage)
				.setFightNum(getMaxFightCount(teamId) - getTodayFight(teamId, type))
				.build();
	}
	
	/**
	 * 挑战的关卡数
	 * @param id
	 */
	@ClientMethod(code = ServiceCode.StreetBallManager_challenge)
	public void challenge(int id) {
		long teamId = getTeamId();
		StreetBallBean bean = StreetBallConsole.getStrBallBean(id);
		if(bean == null) {
			log.debug("没有找到对应关卡{},", id);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_7.code).build());
			return;
		}
		// 判断时间
		int dayOfWeek = DateTime.now().getDayOfWeek();
		if(Arrays.stream(bean.getWeekOfDays()).noneMatch(n-> n == dayOfWeek)) {
			log.debug("该副本[{}]开通时间周{},周{}不开通,", id, bean.getWeekOfDays(), dayOfWeek);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_6.code).build());
			return;
		}
		Team team = teamManager.getTeam(getTeamId());
		if(team.getLevel() < bean.getNeedLv()) {
			log.debug("等级不足,", id);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Level.code).build());
			return;
		}
		if(teamManager.getTeamCap(teamId) < bean.getNeedCap()) {
			log.debug("战力不足,", id);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_5.code).build());
			return;
		}
		// 是否跨关
		StreetBall sb = getTeamStreetBall(teamId);
		if(bean.getStage()-1 > sb.getTypeStage(bean.getType())) {
			log.debug("未解锁关卡{}", id);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_4.code).build());
			return;
		}
		// 挑战次数
		if(getMaxFightCount(teamId) - getTodayFight(teamId, bean.getType()) < 1) {
			log.debug("挑战次数不足{}", getTodayFight(teamId, bean.getType()));
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_5.code).build());
			return;
		}
		// 开启比赛；
		startPKTask(teamId, bean.getNpcId(), bean.getDrop(), bean.getId());
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	/**
	 * VIP有加成
	 * @param teamId
	 * @return
	 */
	public int getMaxFightCount(long teamId) {
		// 街球赛挑战次数加成
		return StreetBallConsole.Every_Day_Count + buffManager.getBuffSet(teamId, EBuffType.街球赛每天挑战次数加X).getValueSum();
	}
	
	/**
	 * 开始比赛
	 * @param teamId
	 * @param npcId
	 * @param dropId
	 */
	private void startPKTask(long teamId, long npcId, int dropId, int stageId) {
		BattleSource bs = localBattleManager.buildBattle(EBattleType.街球副本,
				teamId, npcId, DropConsole.getDrop(dropId), DropConsole.getDrop(dropId),0);
		// 比赛参数
		BattleAttribute ba = new BattleAttribute(0);
		ba.addVal(EBattleAttribute.街球赛挑战关卡, stageId);
		bs.addBattleAttribute(ba);
		localBattleManager.start(bs,this.stageBattleEnd);
	}
	
	/**
	 * 扫荡，直接领奖励
	 */
	@ClientMethod(code = ServiceCode.StreetBallManager_sweep)
	public void sweep(int id) {
		long teamId= getTeamId();
		StreetBallBean bean = StreetBallConsole.getStrBallBean(id);
		StreetBall sb = getTeamStreetBall(teamId);
		// 是否可以扫荡关卡
		if(sb.getTypeStage(bean.getType()) < bean.getStage()) {
			log.debug("未通关不能扫荡{}", id);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 扫荡次数
		if(getMaxFightCount(teamId) - getTodayFight(teamId, bean.getType()) < 1) {
			log.debug("挑战次数不足{}", getTodayFight(teamId, bean.getType()));
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		
		List<PropSimple> list = DropConsole.getDrop(bean.getDrop()).roll();
		BattleBean battle = BattleConsole.getBattle(EBattleType.街球副本);
		//街球扫荡,加上胜利的奖励
		if (battle.getWinDrop() != null && battle.getWinDrop() != "") {
			String[] winDropIdArray = battle.getWinDrop().split(StringUtil.ST);
			for (String winDropIdStr : winDropIdArray) {
				list.addAll(DropConsole.getDrop(Integer.valueOf(winDropIdStr)).roll());
			}
		}
		
		// 直接发奖
		endPK(id, sb, list, false);
        taskManager.updateTask(teamId, ETaskCondition.完成一场比赛, 1,
                1 + "," + 1 + "," + 2 + "," + 1 + "," + EBattleType.街球副本.getId());

		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	/**
	 * 剩余挑战次数
	 * 今天挑战次数
	 * @return
	 */
	public int getTodayFight(long teamId, int type) {
		String key = RedisKey.getDayKey(teamId, RedisKey.StreetBall_Day);
		Integer value = redis.hget(key, type);
		log.debug("街球副本{}今天挑战次数：{}", teamId, value);
		if(value == null) {
			value = 0;
		}
		return value;
	}
	
	/**
	 * 自增挑战次数
	 * @param bean 
	 */
	public void addTodayFight(long teamId, int type) {
		setTodayFight(teamId, type, getTodayFight(teamId, type) + 1);
	}
	
	/**
	 * 保存挑战次数
	 * @param teamId
	 * @param num
	 */
	public void setTodayFight(long teamId, int type, int num) {
		String key = RedisKey.getDayKey(teamId, RedisKey.StreetBall_Day);
		redis.putMapValueExp(key, type, num);
	}
	

}
