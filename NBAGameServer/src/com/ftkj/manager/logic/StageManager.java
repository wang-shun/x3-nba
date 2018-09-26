package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.BattleConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.StageConsole;
import com.ftkj.db.ao.logic.IStageAO;
import com.ftkj.db.domain.StagePO;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.StagePassParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleStep;
import com.ftkj.manager.stage.bean.Stage;
import com.ftkj.manager.stage.cfg.StageBean;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.TargetPB;
import com.ftkj.proto.TeamStagePB;
import com.ftkj.proto.TeamStagePB.TeamStageData;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:主线赛程
 * @author Jay
 * @time:2017年3月24日 上午11:19:28
 */
public class StageManager extends BaseManager implements OfflineOperation {

	private Map<Long, Stage> stageMap;

	@IOC
	private IStageAO stageAO;

	@IOC
	private LocalBattleManager localBattleManager;

	@IOC
	private TeamManager teamManager;

	@IOC
	private PropManager propManager;

	@IOC
	private TaskManager taskManager;
	@IOC
	private TeamStatusManager teamStatusManager;

	private BattleEnd stageBattleEnd;

	private BattleStep helpStep;

	@Override
	public void instanceAfter() {
		stageMap = Maps.newConcurrentMap();
		/*
		 * 比赛结束回调监听
		 */
		stageBattleEnd = new BattleEnd() {
			@Override
			public void end(BattleSource source) {
				EndReport report = source.getEndReport();
				long winId = report.getWinTeamId();
				// 胜利
				if(!GameSource.isNPC(winId)) {
					Stage stage = getTeamStage(winId);
					// 通关触发事件
					EventBusManager.post(EEventType.主线赛程通关, new StagePassParam(stage.getTeamId(), stage.getStageId()));
					// 更新关卡数
					stage.endPkReport(report);
					//记录挑战次数
					addTodayFight(winId);
					sendMessage(winId, getStagePBData(stage), ServiceCode.Main_Stage_show);
					taskManager.updateTask(winId, ETaskCondition.主线赛程胜利, 1, report.getAwayTeamId()+"");
				}
				localBattleManager.sendEndMain(source,true);
			}
		};
		helpStep = BattleConsole.getBattleOrDefault(EBattleType.主线赛程).getBaseBean().getSteps();
	}

	/**
	 * 取关卡数据
	 * @param teamId
	 * @return
	 */
	public Stage getTeamStage(long teamId) {
		Stage stage = stageMap.get(teamId);
		if(stage == null) {
			StagePO  po = stageAO.getStageByTeam(teamId);
			if(po == null) {
				po = new StagePO(teamId);
			}
			stage = new Stage(po);
			stageMap.put(teamId, stage);
			GameSource.checkGcData(teamId);
		}
		return stage;
	}

	public TargetPB.MainScheduleData getTargetData(long teamId) {
		return TargetPB.MainScheduleData.newBuilder().setCurrStageId(getTeamStage(teamId).getStageId()).build();
	}

	@Override
	public void offline(long teamId) {
		stageMap.remove(teamId);
	}

    @Override
    public void dataGC(long teamId) {
        stageMap.remove(teamId);
    }

    // 主界面
	@ClientMethod(code = ServiceCode.Main_Stage_show)
	public void showStage() {
		long teamId = getTeamId();
		Stage stage = getTeamStage(teamId);
		sendMessage(getStagePBData(stage));
	}

	private TeamStagePB.TeamStageData getStagePBData(Stage stage) {
		return TeamStageData.newBuilder()
				.setScene(stage.getScene())
				.setStage(stage.getStageId())
				.setTodayNum(getTodayFight(stage.getTeamId()))
				.addAllScoreList(stage.getMatchScore().stream().filter(s-> !s.equals("")).collect(Collectors.toList()))
				.build();
	}

	/**
	 * 新的征途，过渡动画
	 */
	@ClientMethod(code = ServiceCode.Main_Stage_Next_Scene)
	public void nextScene() {
		long teamId = getTeamId();
		Stage stage = getTeamStage(teamId);
		if(stage.getScene() != 1) {
			log.debug("不是本赛季最后一场！");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		stage.nextScene();
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}

	/**
	 * 挑战
	 */
	@ClientMethod(code = ServiceCode.Main_Stage_Fight)
	public void PK(int stageId) {
		long teamId = getTeamId();
		// 是否在比赛中
		TeamBattleStatus pkStatus = teamStatusManager.get(teamId).getBattle(EBattleType.主线赛程);
		if(pkStatus != null && pkStatus.getBattleId() != 0) {
			log.debug("正在比赛中{}", pkStatus.getBattleId());
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_1.code).build());
			return;
		}
		// 创建比赛
		Stage stage = getTeamStage(teamId);
		Team team = teamManager.getTeam(teamId);
		StageBean stageBean = StageConsole.getStageBean(stageId);
		if(team.getLevel() < stageBean.getLevel()) {
			// 等级不够
			log.debug("等级不足{}", team.getLevel());
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Level.code).build());
			return;
		}
		if(getTodayFight(teamId) >= StageConsole.TODAY_MAX_FIGHT) {
			// 没有次数
			log.debug("没有次数，已挑战{}", getTodayFight(teamId));
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_1.code).build());
			return;
		}
		if(stage.getStageId() > stageBean.getStageId()) {
			log.debug("不可重复挑战关卡:{}", stageId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 奖励由PK初始化时就穿过去。
		startPKTask(teamId,stageBean);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}

	/**
	 * 开始比赛任务
	 * @param stageBean
	 * @param stage
	 */
	private void startPKTask(long teamId,StageBean stageBean) {
		//
		BattleSource bs = localBattleManager.buildBattle(EBattleType.主线赛程,
				teamId, stageBean.getNpcId(), DropConsole.getDrop(stageBean.getDrop()), null,stageBean.getHomeTeamId());
		boolean isHelp = false;
		if(stageBean.getNpcId()==10001){
			bs.getAway().updateTactics(TacticId.外线投篮, TacticId.外线防守,bs.getHome().getPkTactics(TacticType.Offense),bs.getHome().getPkTactics(TacticType.Defense));
			isHelp = true;
		}else if(stageBean.getNpcId()==10002){
			bs.getAway().updateTactics(TacticId.外线投篮, TacticId.外线防守,bs.getHome().getPkTactics(TacticType.Offense),bs.getHome().getPkTactics(TacticType.Defense));
			isHelp = true;
		}else if(stageBean.getNpcId()==10003){
			bs.getAway().updateTactics(TacticId.外线投篮, TacticId.外线防守,bs.getHome().getPkTactics(TacticType.Offense),bs.getHome().getPkTactics(TacticType.Defense));
		}
		if(isHelp)
			localBattleManager.start(bs,this.stageBattleEnd,helpStep);
		else
			localBattleManager.start(bs,this.stageBattleEnd);
		//taskManager.updateTask(teamId, ETaskCondition.主线赛程, 1, EModuleCode.主线赛程.getName());
	}




	/**
	 * 今天挑战次数
	 * @return
	 */
	public int getTodayFight(long teamId) {
		String key = RedisKey.getDayKey(teamId, RedisKey.Stage_Day);
		int value = redis.getIntNullIsZero(key);
		log.debug("主线赛程{}今天挑战次数：{}", teamId, value);
		return value;
	}

	/**
	 * 自增挑战次数
	 */
	public void addTodayFight(long teamId) {
		setTodayFight(teamId, getTodayFight(teamId) + 1);
	}

	/**
	 * 保存挑战次数
	 * @param teamId
	 * @param num
	 */
	public void setTodayFight(long teamId, int num) {
		String key = RedisKey.getDayKey(teamId, RedisKey.Stage_Day);
		redis.set(key, ""+num, RedisKey.DAY);
	}

}
