package com.ftkj.manager.active.task;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.TaskParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.PropManager;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.task.ITaskActiveRefrush;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * 字牌活动
 * @author Jay
 * @time:2018年2月5日 下午5:46:46
 */
@EventRegister({EEventType.活动任务完成})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.做任务赢字牌兑奖, clazz=AtvTodayFinishTask.class)
public class AtvWordTaskManager extends ActiveBaseManager implements ITaskActiveRefrush {

	/**
	 * 每天要完成的任务数量
	 */
	private static int TASK_COUNT = 3;
	/**
	 * 任务集
	 */
	private static List<Integer> taskSet;
	/**
	 * 今日任务
	 */
	private static List<Integer> todayTaskList;
	
	
	@Override
	public void instanceAfter() {
		super.instanceAfter();
		TASK_COUNT = getConfigInt("taskCount", TASK_COUNT);
		// 活动ID列表
		String[] taskIds = getConfigStr("taskIds").split("_");
		taskSet = Arrays.stream(taskIds).filter(s-> !s.equals("")).mapToInt(i-> Integer.valueOf(i)).boxed().collect(Collectors.toList());
		todayTaskList = Lists.newArrayList();
		initTodayTask();
	}
	
	/**
	 * 刷新任务
	 */
	private void initTodayTask() {
		todayTaskList.clear();
		if(taskSet.size()>0) {
			List<Integer> taskIdx = RandomUtil.getRandomBySeed(DateTime.now().getDayOfYear() * 123456L, taskSet.size(), TASK_COUNT, false);
			todayTaskList.addAll(taskIdx.stream().mapToInt(i-> taskSet.get(i)).boxed().collect(Collectors.toList()));
		}
	}
	
	/**
	 * 每天0点刷新任务
	 */
	@Override
	public void everyDayStart(DateTime time) {
		super.everyDayStart(time);
		initTodayTask();
		EventBusManager.post(EEventType.活动任务刷新, getRefrushTask());
	}
	
	/**
	 * 完成任务回调
	 * @param param
	 */
	@Subscribe
	public void finishTask(TaskParam param) {
		if(getStatus() != EActiveStatus.进行中) {
			return;
		}
		int taskId = param.taskId;
		if(!todayTaskList.contains(taskId)) {
			return;
		}
		if(param.status != EStatus.TaskFinish.getId()) {
			return;
		}
		AtvTodayFinishTask atvObj = getTeamDataRedisDay(param.teamId);
		if(atvObj.getFinishStatus().containsValue(taskId)) {
			return;
		}
		// 任务进度
		int taskIndex = todayTaskList.indexOf(taskId);
		atvObj.getTaskPlan().setValueAdd(taskIndex, param.val);
		if(param.status == EStatus.TaskFinish.getId() && !atvObj.getFinishStatus().containsValue(taskIndex)) {
			atvObj.getFinishStatus().addValue(taskIndex);
			// 直接完成
			atvObj.getAwardStatus().addValue(taskIndex);
			// 直接发奖
			sendAward(param.teamId, atvObj, taskIndex);
		}
		saveDataReidsDay(param.teamId, atvObj);
		//
		redPointPush(param.teamId);
	}
	
	@Override
	public int redPointNum(long teamId) {
		ActiveBase atvObj = getTeamDataRedisDay(teamId);
		int num = atvObj.getAwardStatus().sum() - atvObj.getFinishStatus().sum();
		return num;
	}
	
	@Override
	public void showView() {
		long teamId = getTeamId();
		AtvTodayFinishTask atvObj = getTeamDataRedisDay(teamId);
		sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
				.setAtvId(this.getId())
				// 每天任务
				.addAllOtherStatus(todayTaskList)
				// 任务进度
				.setExtend(atvObj.getTaskPlan().getValueStr())
				// 任务完成
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				// 是否已领
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.build());
	}
	
	/**
	 * 购买完成
	 */
	@Override
	public void buyFinish(int tp) {
		long teamId = getTeamId();
		ActiveBase atvObj = getTeamDataRedisDay(teamId);
		TeamMoney teamMoney = moneyManager.getTeamMoney(teamId);
		ErrorCode code = checkBuyFinish(atvObj, teamMoney, tp);
		List<PropSimple> awardList = Lists.newArrayList();
		if(code == ErrorCode.Success) {
			// 直接完成
			atvObj.getFinishStatus().addValue(tp);
			atvObj.getAwardStatus().addValue(tp);
			saveDataReidsDay(teamId, atvObj);
			// 直接发奖
			awardList = sendAward(teamId, atvObj, tp);
		}
		sendMessage(AtvAwardData.newBuilder()
				.setAtvId(this.getId())
				.setAwardId(tp)
				.setCode(code.code)
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.addAllAwardList(PropManager.getPropSimpleListData(awardList))
				.build());
	}
	
	/**
	 * 自动发奖，不手动领取
	 * 领取字牌
	 */
	@Override
	public void getAward(int id) {
		long teamId = getTeamId();
		//
		ActiveBase atvObj = getTeamDataRedisDay(teamId);
		// 校验
		ErrorCode code = checkGetAward(teamId, atvObj, id);
		if(checkTodo(code != ErrorCode.Success, code.code)) {
			return;
		}
		// 保存数据
		atvObj.getAwardStatus().addValue(id);
		saveDataReidsDay(teamId, atvObj);
		//
		ActiveBase convertData = getTeamData(teamId);
		// 发奖
		List<PropSimple> awardList = sendAward(teamId, convertData, id);
		sendMessage(AtvAwardData.newBuilder()
				.setAtvId(this.getId())
				.setAwardId(id)
				.setCode(ErrorCode.Success.code)
				.addAllAwardList(PropManager.getPropSimpleListData(awardList))
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.build());
		// 领奖后处理
		getAwardFinish(teamId, atvObj, id);
		// 红点逻辑处理
		redPointPush(teamId);
	}

	@Override
	public Map<Integer, DateTime> getRefrushTask() {
		Map<Integer, DateTime> map = Maps.newHashMap();
		if(getStatus() != EActiveStatus.进行中) {
			return map;
		}
		DateTime end = DateTimeUtil.getTodayEndTime();
		for(int taskId : todayTaskList) {
			map.put(taskId, end);
		}
		return map;
	}
	
}
