package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.LeagueAppointBean;
import com.ftkj.console.LeagueConsole;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.LoginParam;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueHonor;
import com.ftkj.manager.league.LeagueTask;
import com.ftkj.manager.league.LeagueTaskBean;
import com.ftkj.manager.league.LeagueTaskTeam;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.LeaguePB;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * 联盟日常任务,需求变更为:
 * 联盟日常任务玩家之间独立完成,独立领取奖励,就和游戏中的日常任务一样.
 * 完成的日常进度也算到联盟成就当中去.
 * 2018-9-3 18:54:55
 * @author tim.huang
 * 2017年5月26日
 *
 */
public class LeagueTaskManager extends BaseManager implements OfflineOperation,IRedPointLogic{
	
	@IOC
	private LeagueManager leagueManager;
	@IOC
    private RankManager rankManager;
	@IOC
	private PropManager propManager;
	@IOC
	private TeamManager teamManager;
	@IOC
	private TaskManager taskManager;
	@IOC
	private LeagueHonorManager leagueHonorManager;
	@IOC
	private RedPointManager redPointManager;
	
	@Override
	public void instanceAfter() {
		EventBusManager.register(EEventType.登录, this);
	}
	
	@Override
	public void offline(long teamId) {
	}


	@Override
	public void dataGC(long teamId) {
		
	}
	
	/**
	 * 处理玩家跨零点在线,登录处理(玩家登录和定时器0点调用在线玩家).
	 * 跨天,联盟日常任务重置,提示小红点数量为0. 
	 */
    @Subscribe
    public void login(LoginParam param) {
    	DateTime now = DateTime.now();
		// 0点0秒
		if(!now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).equals(now)) {
			return;
		}
		
		//任务数据重置了,发送小红点提示为数量为0,没有奖励领取
		this.redPointManager.sendRedPointTip(
			new RedPointParam(param.teamId, ERedPoint.LeagueDailyTask.getId(), 0));
		//每日累计贡献重置,发送小红点提示为数量为0,没有奖励领取
		this.redPointManager.sendRedPointTip(
				new RedPointParam(param.teamId, ERedPoint.LeagueDailyGetReward.getId(), 0));
    }
	
	/**
	 * 联盟日常任务有奖励没有领取的小红点提示.
	 */
	@Override
	public RedPointParam redPointLogic(long teamId) {
		LeagueTeam l = leagueManager.getLeagueTeam(teamId);
        boolean isBreak = false;
        LeagueTaskTeam taskTeam = getLeagueTaskTeam(teamId);
        if (l.getLeagueId() <= 0) {
			return null;
		}
        
        Map<Integer,LeagueTask> taskMap = getDayTaskMap(teamId);
        for (LeagueTask obj : taskMap.values()) {
        	LeagueTaskBean taskBean = LeagueConsole.getLeagueTaskBean(obj.getId());
        	//特殊联盟日常任务Id100,所有联盟日常任务完成它才算完成
        	if (obj.getId() == 100) {
    			LeagueTask totalTask = getDayTask(teamId, 100);
    			//如果有奖励领取,则推送小红点
    			if (totalTask.getCounts()[0] >= (getDayTaskMap(teamId).size() - 1) 
    					&& !taskTeam.getGiftStatus(100)) {
    				isBreak = true;
    				break;
    			}
			}else {
				int size = taskBean.getTaskProps().size();
	        	for(int i =0 ; i<size ; i++){
	        		if (obj.getCounts()[i] >= taskBean.getTaskProps().get(i).getNum() 
	        				&& !taskTeam.getGiftStatus(obj.getId())) { // 任务奖励还没有领取
	        			isBreak = true;
	        			break;
	        		}
	        	}
	        	
	        	if (isBreak) {
	        		break;
	        	}
			}
        	
        }
        
        RedPointParam rpp = null;
        if (isBreak) {
        	rpp = new RedPointParam(l.getTeamId(), ERedPoint.LeagueDailyTask.getId(), 1);
		}
        
        return rpp;
	}
	
	
	/**
	 * 显示联盟日常任务列表
	 */
	@ClientMethod(code = ServiceCode.LeagueTaskManager_showLeagueTaskMain)
	public void showLeagueTaskMain(){
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
		if(lt == null || lt.getLeagueId()<=0) {
			return;
		}
		
		Map<Integer,LeagueTask> taskMap = getDayTaskMap(teamId);
		List<LeaguePB.LeagueTaskData> taskDatas = Lists.newArrayList();
		LeagueTaskTeam taskTeam = getLeagueTaskTeam(teamId);
		
		taskMap.values().forEach(task->{
			taskDatas.add(getLeagueTaskData(task,taskTeam.getGiftStatus(task.getId()),taskTeam.getHonor(task.getId())));
		});
		sendMessage(LeaguePB.LeagueTaskMain.newBuilder().addAllTasks(taskDatas).build());
	}
	
	/**
	 * 刷新任务
	 * @param tid
	 */
	@ClientMethod(code = ServiceCode.LeagueTaskManager_refreshTask)
	public void refreshTask(int tid){
//		long teamId = getTeamId();
//		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
//		if(lt == null || lt.getLeagueId()<=0) {
//			return;
//		}
//		int leagueId = lt.getLeagueId();
//		//
//		LeagueTask task = getDayTask(leagueId,tid);
//		if(task == null || task.getStatus() == EStatus.Open.getId()){//重置的任务不存在,或者任务已经开启
//			return;
//		}
//		
//		LeagueTaskBean taskBean  = LeagueConsole.getLeagueTaskBean(tid);
//		LeagueTaskBean newTaskBean = LeagueConsole.getRanLeagueTaskBean(taskBean.getType(), leagueManager.getLeague(leagueId).getLeagueLevel());
//		
//		LeagueTask newTask = LeagueTask.createLeagueTask(newTaskBean);
//		redis.removeMapValue(RedisKey.getDayKey(leagueId, RedisKey.League_Task), task.getId());
//		redis.putMapValueExp(RedisKey.getDayKey(leagueId, RedisKey.League_Task), newTask.getId(), newTask);
//		//
//		sendMessage(LeaguePB.LeagueTaskMain.newBuilder().addTasks(getLeagueTaskData(newTask,false,0)).build());
	}
	
	/**
	 * 开启任务
	 * @param tid
	 */
	@ClientMethod(code = ServiceCode.LeagueTaskManager_openTask)
	public void openTask(int tid){
//		long teamId = getTeamId();
//		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
//		if(lt == null || lt.getLeagueId()<=0) {
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
//			return;
//		}
//		int leagueId = lt.getLeagueId();
//		//
//		LeagueTask task = getDayTask(leagueId,tid);
//		if(task == null){//参数异常
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
//			return;
//		}
//		
//		if(!task.isStart() || task.getStatus() != EStatus.Close.getId()){//不在任务时间内,或者任务已经开启
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
//			return;
//		}
//		task.setStatus(EStatus.Open.getId());
//		setDayTask(leagueId, task);
//		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	
	/**
	 * 玩家日常任务捐献荣誉，勋章
	 * @param index
	 */
	@ClientMethod(code = ServiceCode.LeagueTaskManager_appointTask)
	public void appointTask(int tid, int index){
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
		if(lt == null || lt.getLeagueId()<=0) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
			return;
		}
		
//		int leagueId = lt.getLeagueId();
		LeagueTask task = getDayTask(teamId, tid);
		if(task == null || index < 0 || index >= task.getCounts().length){//参数异常
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
			return;
		}
		

		LeagueTaskBean taskBean = LeagueConsole.getLeagueTaskBean(task.getId());
		PropSimple ps = taskBean.getTaskProps().get(index);
		
//		if (DateTimeUtil.hoursBetween(lt.getCreateTime()) < 24) {
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_4.code).build());
//			return;
//		}
		
		if(propManager.delProp(teamId, ps.getPropId(), 1, true, false) == null){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		
		int pid = ps.getPropId();
		appointGift(teamId, tid, pid, lt);
		task.getCounts()[index] = task.getCounts()[index] + 1;
		setDayTask(teamId, task);
		// 联盟日常任务完成可以领取奖励,则推送小红点
		if (task.getCounts()[index] >= taskBean.getTaskProps().get(index).getNum()) {
			redPointManager.sendRedPointTip(new RedPointParam(teamId, ERedPoint.LeagueDailyTask.getId(), 1));
		}
		
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		// 联盟成就同样统计捐赠
    	LeagueHonor lh = this.leagueHonorManager.getLeagueHonor(lt.getLeagueId());
    	synchronized (lh) {
            lh.appendHonorProp(pid, 1);
    	}
    	
		// 触发事件
		taskManager.updateTask(teamId, ETaskCondition.联盟捐献勋章, 1, EModuleCode.联盟.getName());
		
		// 如果任务完成了,则:完成所有联盟日常任务数加1
		int size = taskBean.getTaskProps().size();
		for (int i = 0; i < size; i++) {
			if (task.getCounts()[i] < taskBean.getTaskProps().get(i).getNum()) {// 任务还没完成
				return;
			}
		}
		
		// 完成所有联盟日常数加1,这个任务只有一个类型为-1
		LeagueTaskBean totalTaskBean = LeagueConsole.getRandomLeagueTaskBean(-1);
		if (totalTaskBean == null) {
			return;
		}
		
		LeagueTask totalTask = getDayTask(teamId, totalTaskBean.getTid());
		totalTask.getCounts()[0] += 1;
		// 如果联盟所有日常任务有奖励领取,则推送小红点
		if (totalTask.getCounts()[0] >= (getDayTaskMap(teamId).size() - 1)) {
			redPointManager.sendRedPointTip(new RedPointParam(teamId, ERedPoint.LeagueDailyTask.getId(), 1));
		}
		
		setDayTask(teamId, totalTask);
	}
	
	@ClientMethod(code = ServiceCode.LeagueTaskManager_appointSBTask)
	public void appointSBTask(int tid,int pid){
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
		if(lt == null || lt.getLeagueId()<=0) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
			return;
		}
		int leagueId = lt.getLeagueId();
		//
		LeagueTask task = getDayTask(leagueId,tid);
		if(task == null){//参数异常
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
//		if(!task.isStart() || task.getStatus() != EStatus.Open.getId()){//不在任务时间内
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
//			return;
//		}
//		//
//		LeagueTaskBean taskBean = LeagueConsole.getLeagueTaskBean(task.getId());
//		if(!taskBean.checkPid(pid)){
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
//			return;
//		}
		
//		if(task.getTotal() >=taskBean.getTotalNum()){//超过捐献上限
//			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
//			return;
//		}
		
		if(DateTimeUtil.hoursBetween(lt.getCreateTime())<24){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_4.code).build());
			return;
		}
		
		
		if(propManager.delProp(teamId, pid, 1, true, false) == null){
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
			return;
		}
		
		appointGift(teamId, tid, pid, lt);
		task.setTotal(task.getTotal() + 1);
		setDayTask(leagueId, task);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	/**
	 *  领取任务奖励
	 * @param tid
	 */
	@ClientMethod(code = ServiceCode.LeagueTaskManager_getTaskGift)
	public void getTaskGift(int tid){
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
		if(lt == null || lt.getLeagueId()<=0) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
			return;
		}
		
		LeagueTask task = getDayTask(teamId,tid);
		if(task == null){//参数异常
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
			return;
		}
		
		LeagueTaskBean taskBean = null;
		//完成所有联盟日常任务的任务
		if (tid == 100) {
			taskBean = LeagueConsole.getLeagueTaskBean(100);
			LeagueTask totalTask = getDayTask(teamId, 100);
			if (totalTask.getCounts()[0] < (getDayTaskMap(teamId).size() - 1)) {
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Task_1.code).build());
				return;
			}
		}else {
			taskBean = LeagueConsole.getLeagueTaskBean(task.getId());
			int size = taskBean.getTaskProps().size();
			for(int i =0 ; i<size ; i++){
				if(task.getCounts()[i]<taskBean.getTaskProps().get(i).getNum()){//任务还没完成
					sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Task_1.code).build());
					return;
				}
			}
		}

		LeagueTaskTeam taskTeam = getLeagueTaskTeam(teamId);
		if(taskTeam.getGiftStatus(tid)){//任务奖励已领取或者没有达到最低领取荣誉限制
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build());
			return;
		}
		
		propManager.addPropList(teamId, taskBean.getGifts(), true, ModuleLog.getModuleLog(EModuleCode.联盟, "任务奖励"));
		taskTeam.updateGiftStatus(tid, true);
		setLeagueTaskTeam(teamId, taskTeam);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	
	/**
	 * 捐献后获得的奖励
	 * @param teamId
	 */
	private void appointGift(long teamId,int tid,int pid,LeagueTeam lt){
		//奖励一些东西
		LeagueAppointBean  lab = LeagueConsole.getLeagueAppointBean(pid); 
		if(lab != null){
			League league = leagueManager.getLeague(lt.getLeagueId());
			synchronized (league) {
				lt.updateFeats(lab.getFeats());
				leagueManager.updateLeagueTeamScore(league.getLeagueId(), lt, lab.getScore());
				league.updateLeagueHonor(lab.getHonor());
				league.updateLeagueScore(lab.getScore());//联盟贡献
				rankManager.updateLeagueRank(league, 
						leagueHonorManager.getLeagueHonor(league.getLeagueId()).getAllLevel());
				//增加荣誉值
				LeagueTaskTeam taskTeam = getLeagueTaskTeam(teamId);
				taskTeam.updateHonor(tid, lab.getHonor());
				setLeagueTaskTeam(teamId, taskTeam);
				
				leagueHonorManager.appointLeagueDonte(league, lab, 1);
			}
		}else{
			log.debug("有一个勋章没有配置--->{}",pid);
		}

	}
	
	              
	private LeaguePB.LeagueTaskData getLeagueTaskData(LeagueTask task,boolean gift,int honor){
		int second = 0;//DateTimeUtil.secondBetween(DateTime.now(), task.getEndTime());
//		second = second<0?0:second;
		List<Integer> counts =Arrays.stream(task.getCounts()).boxed().collect(Collectors.toList());
		return LeaguePB.LeagueTaskData.newBuilder()
				.setTid(task.getId())
				.setStatus(task.getStatus())
				.setGift(gift)
				.setEndSecond(second)
				.addAllCounts(counts)
				.setHonor(honor)
				.setTotal(task.getTotal())
				.build();
	}
	
	
	/**
	 * 返回玩家的联盟日常任务数据.任务数据保存在Redis中,key值生成规则是:
	 * "key + DateTimeUtil.getString(DateTime.now()) + "_" + teamId",
	 * 所以每过一天都会不一样,这样就实现了每日数据的重置功能.
	 * @param teamId
	 * @return
	 */
	private Map<Integer,LeagueTask> getDayTaskMap(long teamId){
		Map<Integer,LeagueTask> taskMap = 	redis.getMapAllKeyValues(RedisKey.getDayKey(teamId, RedisKey.League_Task));
		if(taskMap == null || taskMap.size()<=0){//未初始化
			taskMap = Maps.newHashMap();
			for(Integer taskType : LeagueConsole.getLeagueTaskTypeSet()){
				LeagueTaskBean ltb = LeagueConsole.getRandomLeagueTaskBean(taskType);
				taskMap.put(ltb.getTid(), LeagueTask.createLeagueTask(ltb));
				setDayTask(teamId, LeagueTask.createLeagueTask(ltb));
			}
			
		}
		return taskMap;
	}
	
	private LeagueTask getDayTask(long teamId, int tid){
		LeagueTask task = redis.hget(RedisKey.getDayKey(teamId, RedisKey.League_Task), tid);
		return task;
	}
	
	public static LeagueTask getDayTask(JedisUtil redis, long teamId, int tid){
		LeagueTask task = redis.hget(RedisKey.getDayKey(teamId, RedisKey.League_Task), tid);
		return task;
	}
	
	/**
	 * 保存一个新的联盟日常任务到Redis中.
	 * @param teamId
	 * @param task
	 */
	private void setDayTask(long teamId,LeagueTask task){
		int tid = task.getId();
		redis.putMapValueExp(RedisKey.getDayKey(teamId, RedisKey.League_Task), tid, task);		
	}
	
	public static void setDayTask(JedisUtil redis, long teamId, LeagueTask task){
		int tid = task.getId();
		redis.putMapValueExp(RedisKey.getDayKey(teamId, RedisKey.League_Task), tid, task);	
	}
	
	private LeagueTaskTeam getLeagueTaskTeam(long teamId){
		LeagueTaskTeam taskTeam = redis.getObj(RedisKey.getDayKey(teamId, RedisKey.League_Task_Team));
		if(taskTeam == null){
			taskTeam = new LeagueTaskTeam();
			setLeagueTaskTeam(teamId, taskTeam);
		}
		return taskTeam;
	}
	
	private void setLeagueTaskTeam(long teamId,LeagueTaskTeam taskTeam){
		redis.set(RedisKey.getDayKey(teamId, RedisKey.League_Task_Team), taskTeam, RedisKey.DAY);
	}
	
	/**
	 * 玩家在联盟成就捐勋章,也会统计到联盟日常任务的进度.
	 * @param teamId
	 * @param pid	捐的物品Id
	 * @param num   数量
	 */
	public void addAppointDailyTask(long teamId, int pid, int num){
		int taskId = LeagueConsole.getLeagueTaskId(pid);

		if (taskId == 0) {
			return;
		}
		
		LeagueTaskBean taskBean = LeagueConsole.getLeagueTaskBean(taskId);
		final int finalType = taskBean.getType();
		Map<Integer,LeagueTask> taskMap = getDayTaskMap(teamId);
		LeagueTask objLeagueTask = taskMap.values().stream()
			.filter(obj -> LeagueConsole.getLeagueTaskBean(obj.getId()).getType() == finalType)
			.findFirst().orElse(null);
		LeagueTask task = getDayTask(teamId, objLeagueTask.getId());
		if (task == null) {
			return;
		}
		
		task.getCounts()[0] = task.getCounts()[0] + num;
		setDayTask(teamId, task);
		taskBean =  LeagueConsole.getLeagueTaskBean(task.getId());
		
		//如果任务完成了,则:完成所有联盟日常数加1
		int size = taskBean.getTaskProps().size();
		for (int i = 0; i < size; i++) {
			if (task.getCounts()[i] < taskBean.getTaskProps().get(i).getNum()) {// 任务还没完成
				return;
			}
		}
		
		// 完成所有联盟日常数加1,这个任务只有一个类型为-1
		LeagueTaskBean totalTaskBean = LeagueConsole.getRandomLeagueTaskBean(-1);
		if (totalTaskBean == null) {
			return;
		}
		
		LeagueTask totalTask = getDayTask(teamId, totalTaskBean.getTid());
		totalTask.getCounts()[0] += 1;
		setDayTask(teamId, totalTask);
		
	}

}
