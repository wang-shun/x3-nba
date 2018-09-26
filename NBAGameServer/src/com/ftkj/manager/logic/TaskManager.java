package com.ftkj.manager.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.HappySevenDayBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.HappySevenDayConsole;
import com.ftkj.console.TaskConsole;
import com.ftkj.db.dao.logic.HappySevenDayDao;
import com.ftkj.db.dao.logic.TaskDAO;
import com.ftkj.db.domain.TaskConditionPO;
import com.ftkj.db.domain.TaskPO;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERedPoint;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ETaskType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.LoginParam;
import com.ftkj.event.param.RedPointParam;
import com.ftkj.event.param.TaskParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.activity.HappySevenDay;
import com.ftkj.manager.activity.HappySevenDay.SevenDayBox;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;
import com.ftkj.manager.equi.TeamEqui;
import com.ftkj.manager.equi.bean.Equi;
import com.ftkj.manager.logic.happy7day.DateUtil;
import com.ftkj.manager.logic.happy7day.Happy7dayManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.logic.taskpool.AbstractTask;
import com.ftkj.manager.logic.taskpool.TaskPool;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.task.ITaskActiveRefrush;
import com.ftkj.manager.task.Task;
import com.ftkj.manager.task.TaskActive;
import com.ftkj.manager.task.TaskBean;
import com.ftkj.manager.task.TaskCondition;
import com.ftkj.manager.task.TaskConditionBean;
import com.ftkj.manager.task.TaskDay;
import com.ftkj.manager.task.TaskDayBean;
import com.ftkj.manager.task.TaskStarAwardBean;
import com.ftkj.manager.task.TeamTask;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.HappySevenDayPB.BoxReward;
import com.ftkj.proto.HappySevenDayPB.DayHappyTask;
import com.ftkj.proto.HappySevenDayPB.HappyTask;
import com.ftkj.proto.HappySevenDayPB.MyHappySevenDay;
import com.ftkj.proto.HappySevenDayPB.UpdateBox;
import com.ftkj.proto.HappySevenDayPB.UpdateHappyTask;
import com.ftkj.proto.TaskPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * @author tim.huang
 * 2017年6月7日
 * 任务
 */
public class TaskManager extends BaseManager implements OfflineOperation, IRedPointLogic {
    private static final Logger log = LoggerFactory.getLogger(TaskManager.class);
    @IOC
    private PropManager propManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private TaskDAO taskAO;
    @IOC
    private BuffManager buffManager;
    @IOC
    private EquiManager equiManager;
    @IOC
    private RedPointManager redPointManager;

    private Map<Long, TeamTask> taskMap;

    private List<ITaskActiveRefrush> taskActiveRefrushList;
    
    private Map<Long,HappySevenDay> happySevenDayMap = new ConcurrentHashMap<>();

    private static String dayTaskVersion;

    @IOC
    private HappySevenDayDao happySevenDayDao;
    
    public int createTeamDayForHappy7Day(long teamId) {
      int d = createTeamDay0(teamId);
      if(d > 7) {
        d = 7;
      }
      return d;
    }
    
    /**
     * 创号第几个自然天数
     * @param teamId
     * @return
     */
    public int createTeamDay0(long teamId) {
      Team team = teamManager.getTeam(teamId);
      if(team != null) {
        long oneDay = 24 * 60 * 60 * 1000;
        DateTime createTime = team.getCreateTime();
        long createZeroTime = DateUtil.getZero(createTime.getMillis());
        long l = System.currentTimeMillis() - createZeroTime;
        long h = l / 1000 / 60 / 60;
        int d = (int)(h / 24);
        if(l % oneDay != 0) {
          d += 1;
        }
        if(d == 0) {
          d = 1;
        }
        return d;
      }
      return 1;
    }
    
    /**
     * 获得任务列表
     */
    @ClientMethod(code = ServiceCode.TaskManager_showTaskMain)
    public void showTaskMain() {
        long teamId = getTeamId();
        TeamTask teamTask = getTeamTask(teamId);
        //
        List<TaskPB.TaskData> trunkList = Lists.newArrayList();
        Map<Integer, Task> ccMap = teamTask.getTrunkList(ETaskType.所有任务).stream().collect(Collectors.toMap((key) -> key.getTid(), (val) -> val));

        teamTask.getTrunkList(ETaskType.所有任务).stream()
            .filter(task -> task.getStatus() != EStatus.TaskClose.getId())
            .filter(task -> {
                // 前置任务判断
                TaskBean taskBean = TaskConsole.getTaskBean(task.getTid());
                if (taskBean == null) { return false; }
                Task t = ccMap.get(taskBean.getLimitTid());
                if (t != null && (t.getStatus() != EStatus.TaskClose.getId())) { return false; }

                return true;
            }).forEach(task -> trunkList.add(getTaskData(task)));

        List<TaskPB.TaskData> dayList = Lists.newArrayList();
        teamTask.getDayMap().values().stream().filter(taskDay -> {
            // 前置任务判断
            TaskBean taskBean = TaskConsole.getTaskBean(taskDay.getTid());
            if (taskBean == null) { return false; }
            Task task = ccMap.get(taskBean.getLimitTid());
            if (task != null && (task.getStatus() != EStatus.TaskClose.getId())) { return false; }
            if (taskDay.getStatus() == EStatus.TaskClose.getId() && taskBean.getDayType() != 1) { return false; }

            return true;
        }).forEach(task -> dayList.add(getTaskData(task)));

        sendMessage(TaskPB.TaskMain.newBuilder()
            .addAllDayList(dayList)
            .addAllTrunkList(trunkList)
            .setStarNum(teamTask.getTaskDay().getTaskStarSum())
            .addAllStarAwardId(teamTask.getTaskDay().getStatus())
            .build());
    }

    @Override
    public RedPointParam redPointLogic(long teamId) {
        TeamTask teamTask = getTeamTask(teamId);
        boolean red = teamTask.getTrunkList(ETaskType.所有任务).stream().filter(task -> task.getStatus() == EStatus.TaskFinish.getId()).findFirst().isPresent();
        if (!red) {
            red = teamTask.getDayMap().values().stream().filter(task -> task.getStatus() == EStatus.TaskFinish.getId()).findFirst().isPresent();
        }
        RedPointParam rpp = new RedPointParam(teamId, ERedPoint.任务领取.getId(), red ? 1 : 0);
        EventBusManager.post(EEventType.奖励提示, rpp);
        return rpp;
    }
    

    @Override
	public List<RedPointParam> redPoints(long teamId) {
    	TeamTask teamTask = getTeamTask(teamId);
        Team team = teamManager.getTeam(teamId);
        Task task = teamTask.getTask(TaskConsole.getTaskStepId(team.getTaskStep()));
        boolean red = false;
        
        if (task != null && task.getStatus() == EStatus.TaskFinish.getId()) {
			red = true;
		}
        
        List<RedPointParam> list = Lists.newArrayList();
        list.add(new RedPointParam(teamId, ERedPoint.MainTarget.getId(), red ? 1 : 0));
		return list;
	}

	/**
     * 领取任务奖励
     *
     * @param tid
     */
    @ClientMethod(code = ServiceCode.TaskManager_getTaskAward)
    public void getTaskAward(int tid) {
        ErrorCode ret = getTaskAward0(tid);
        if (ret.isError()) {
            sendMessage(ret);
        }
    }

    private ErrorCode getTaskAward0(int tid) {
        long teamId = getTeamId();
        TeamTask teamTask = getTeamTask(teamId);
        Task task = teamTask.getTask(tid);
        if (task == null || task.getStatus() != EStatus.TaskFinish.getId()) {//任务未完成
            return ErrorCode.Fail;
        }
        TaskBean taskBean = TaskConsole.getTaskBean(tid);
        if (taskBean == null) {
            return ErrorCode.Fail;
        }

        task.updateStatus(EStatus.TaskClose, taskBean.getType());
        List<PropSimple> props = taskBean.getProps();
        ModuleLog mc = ModuleLog.getModuleLog(EModuleCode.任务, taskBean.getName() + "[" + tid + "]");
        propManager.addPropList(teamId, props, true, mc);

        //将已领取奖励的任务替换
        Task newTask = teamTask.getTask(tid);
        if (taskBean.getType() == ETaskType.日常任务) {
            if (taskBean instanceof TaskDayBean) {
                TaskDayBean dd = (TaskDayBean) taskBean;
                float rate = buffManager.getBuffSet(teamId, EBuffType.Task_Day_Exp).getValueSum() / 1000.0f;
                Team team = teamManager.getTeam(teamId);
                int exp = Math.round(dd.getExp(team.getLevel()) * (1 + rate));
                if (log.isDebugEnabled()) {
                    log.debug("task award daily. tid {} mid {} rate {} exp {} {}", teamId, tid, rate, dd.getExp(team.getLevel()), exp);
                }
                teamMoneyManager.updateTeamMoney(teamId, 0, 0, exp, 0, true, mc);
                // 累计星数
                teamTask.getTaskDay().setTaskStarSum(dd.getStar());
            }
            // 刷新新的日常任务
            newTask = refreshTaskDay(teamId, teamTask, task);
            //日常任务推送新的任务
            sendMessage(teamId, TaskPB.TaskPushData.newBuilder()
                .addTaskList(getTaskData(newTask)).build(), ServiceCode.Push_Task_Day_GetAward);
        }

        sendMessage(DefaultPB.DefaultData.newBuilder()
            .setCode(ErrorCode.Success.code)
            .setMsg(newTask == null ? "" : "" + newTask.getTid())
            .build());

        // 清理任务相关条件数据 
        task.getConditionList().clear();
        taskAO.deleteTeamTaskCondition(teamId, tid);

        // 检查任务目标s
        checkNextTaskTarget(teamId);
        updateDayTask(teamTask,teamId);
        
        sendMainTargetRedPoint(teamId);
        
        return ErrorCode.Success;
    }

    public void checkNextTaskTarget(long teamId) {
        TeamTask tt = getTeamTask(teamId);
        Team team = teamManager.getTeam(teamId);
        int currStep = team.getTaskStep();
        if (currStep != 0) {
            int taskId = TaskConsole.getTaskStepId(currStep);
            if (tt.getTask(taskId) != null && tt.getTask(taskId).getStatus() != EStatus.TaskClose.getId()) {
                return;
            }
        }
        // 取下一个任务ID
        int nextStep = getNextTaskTarget(tt, currStep);
        if (nextStep != 0) {
            team.setTaskStep(nextStep);
            team.save();
        }
    }

    /**
     * 取下一个未完成的主线
     */
    private int getNextTaskTarget(TeamTask teamTask, int currStep) {
        int nextStep = currStep;
        int i = 0;
        do {
            i++;
            nextStep = TaskConsole.getNextTaskStep(nextStep);
            if (nextStep != 0) {
                Task task = teamTask.getTask(TaskConsole.getTaskStepId(nextStep));
                if (task == null) {
                    continue;
                }
                if (task.getStatus() == EStatus.TaskClose.getId()) {
                    continue;
                }
                return nextStep;
            }
        } while (nextStep != 0 && i < 50);
        return 0;
    }

    /**
     * 领取日常活跃奖励
     *
     * @param index
     */
    @ClientMethod(code = ServiceCode.TaskManager_getTaskStarAward)
    public void getTaskStarAward(int rewardId) {
        long teamId = getTeamId();
        TeamTask teamTask = getTeamTask(teamId);
        TaskDay taskDay = teamTask.getTaskDay();

        // 领奖条件不足
        TaskStarAwardBean taskStarAwardBean = TaskConsole.getTaskStarAwardBean(rewardId);
        if (taskDay.getTaskStarSum() < taskStarAwardBean.getStarNum()) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_4.code).build());
            return;
        }

        //已经领取过奖励了
        if (taskDay.getStatus().contains(rewardId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Active_7.code).build());
            return;
        }

        taskDay.getStatus().add(rewardId);

        propManager.addPropList(teamId, taskStarAwardBean.getRewardList(), true, ModuleLog.getModuleLog(EModuleCode.任务, "活跃奖励"));
        updateDayTask(teamTask,teamId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    private void updateDayTask(long teamId, TeamTask teamTask, List<TaskDayBean> taskDayBeanList) {
        List<Task> newList = Lists.newArrayList();
        taskDayBeanList.forEach(taskDayBean -> {
            if (taskDayBean.getLimitTid() == 0) {
                Task task = createTask(teamId, taskDayBean);
                newList.add(task);
            }
        });

        teamTask.updateDayTask(newList);
    }

    /**
     * 升级后，根据球队等级生成可用主线任务数据
     * @param teamId
     * @param startLevel	起始等级
     * @param endLevel		球队当前的等级
     */
    public void levelup(long teamId, int startLevel, int endLevel) {
        TeamTask teamTask = getTeamTask(teamId);
        if (teamTask == null) {
            return;
        }
        synchronized (teamTask) {
            List<TaskBean> newTaskList = Lists.newArrayList();
            startLevel++;
            for (; startLevel <= endLevel; startLevel++) {
                newTaskList.addAll(TaskConsole.getTaskTrunkBeanByLevel(startLevel));
            }
            if (newTaskList.size() <= 0) {
                return;
            }
            //
            Set<Integer> hasTas = teamTask.getTrunkList(ETaskType.所有任务).stream().mapToInt(t -> t.getTid()).boxed().collect(Collectors.toSet());
            List<Task> taskList = newTaskList.stream()
                .filter(taskBean -> !hasTas.contains(taskBean.getTid()))
                .map(t -> createTask(teamId, t))
                .collect(Collectors.toList());
            // 主线任务才保存
            taskList.forEach(t -> {
                t.updateStatus(EStatus.TaskOpen, TaskConsole.getTaskBean(t.getTid()).getType());
                t.save();
            });
            //
            teamTask.getTrunkList(ETaskType.所有任务).addAll(taskList);
        }
    }

    /**
     * 执行任务更新
     *
     * @param teamId
     * @param condition
     * @param valInt
     * @param valStr
     */
    public void updateTask(long teamId, ETaskCondition condition, int valInt, String valStr) {
        try {
            log.trace("task up. tid {} cond {} vali {} vals {}", teamId, condition, valInt, valStr);
            if(condition.isMainTaskCondition()) {
              updateTask0(teamId, condition, valInt, valStr);
            }
            if(condition.isHappy7dayCondition()) {
              int d = createTeamDay0(teamId);
              if(d <= 7) {
                Happy7dayManager.updateTask0(teamId, condition, valInt, valStr);
              }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void updateTask0(long teamId, ETaskCondition condition, int valInt, String valStr) {
        if (GameSource.isNPC(teamId)) {
            return;
        }
        TeamTask teamTask = getTeamTask(teamId);
        List<Integer> uList = Lists.newArrayList();//有变动的任务ID

        //主线任务，成就任务----都归为通用任务
        try {
            updateTask(teamId, teamTask, teamTask.getTrunkList(ETaskType.所有任务), condition, valInt, valStr, ETaskType.主线任务, uList);
            //日常任务
            updateTask(teamId, teamTask, Lists.newArrayList(teamTask.getDayMap().values()), condition, valInt, valStr, ETaskType.日常任务, uList);
            //活动任务，活动关心的任务，和上面可能是重用的。
            updateTask(teamId, teamTask, teamTask.getActiveTaskList(), condition, valInt, valStr, ETaskType.活动任务, uList);
            updateDayTask(teamTask,teamId);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("更新任务状态有误--{}", e);
        }

    }

    /**
     * @param teamId     球队ID
     * @param TeamTask   球队任务
     * @param taskList   目标任务列表
     * @param condition  触发的条件
     * @param valInt     条件1
     * @param valStr     条件2
     * @param type       任务类型
     * @param updateList 改变的任务列表
     */
    private void updateTask(long teamId, TeamTask teamTask, List<Task> taskist,
                            ETaskCondition condition, int valInt, String valStr, ETaskType type,
                            List<Integer> updateList) throws InstantiationException, IllegalAccessException {

        List<Integer> finishList = Lists.newArrayList();

        // 整合和触发类型相同的所有任务条件类型
        List<TaskCondition> conditionlist = taskist.stream()
            .filter(task -> task.getStatus() == EStatus.TaskOpen.getId())
            .map(task -> task.getConditionList())
            .reduce(Lists.newArrayList(), (A, B) -> {A.addAll(B); return A;})
            .stream()
            .filter(con -> con.getCid() == condition.getType())
            .collect(Collectors.toList());

        // 检测任务条件是否达成
        conditionlist.forEach(con -> {
            TaskConditionBean tcb = TaskConsole.getTaskConditionBean(TaskConsole.getTaskConditionId(con));
            if (tcb == null) {
//                log.debug("出现了未配置条件的任务. TaskID {}, type {}", con.getTid(), con.getCid());
                return;
            }
            if (!tcb.getFinish().checkVal(tcb.getValInt(), tcb.getValString(), valInt, valStr)) {
                return;
            }

            // 更新条件数据
            con.updateVal(valInt, valStr, tcb.getIsSetInt(), tcb.getIsSetStr(), type.isSaveDb());
            if (tcb.getFinish().finish(tcb.getValInt(), tcb.getValString(), con.getValInt(), con.getValStr())) {
                finishList.add(con.getTid());
            }

            updateList.add(con.getTid());
        });

        // log.debug("task done. tid {} taskids {}", teamId, finishList);
        finishList.forEach(tid -> {
            Task task = teamTask.getTask(tid);
            if (task == null) { return; }
            boolean has = task.getConditionList().stream().anyMatch(con -> {//查看所有条件是否达成，有未达成的跳出来
                TaskConditionBean tcb = TaskConsole.getTaskConditionBean(TaskConsole.getTaskConditionId(con));
                return !tcb.getFinish().finish(tcb.getValInt(), tcb.getValString(), con.getValInt(), con.getValStr());
            });
            //
            if (!has) {//所有条件达成，改变任务状态
                this.finishTask(teamId, tid);
            }
        });

        // 活动任务检查
        if (type == ETaskType.活动任务) {
            updateList.forEach(tid -> {
                Task task = teamTask.getTask(tid);
                if (task != null) {
                    int conut = task.getConditionList().stream().mapToInt(s -> s.getValInt()).sum();
                    //log.warn("完成活动任务 teamId {}, tid {}", teamId, task.getTid());
                    EventBusManager.post(EEventType.活动任务完成, new TaskParam(teamId, task.getTid(), type, condition, conut, task.getStatus()));
                }
            });
        }
    }

    /** 刷新日常任务 */
    private Task refreshTaskDay(long teamId, TeamTask teamTask, Task task) {
        TaskDayBean tmp = TaskConsole.getNextDayTask(task.getTid());
        if (tmp == null) {
            log.debug("refresh task is null---->{}", teamId);
            return task;
        }

        //替换已领取奖励的任务
        Task newTask = createTask(teamId, tmp);
        teamTask.replaceDayTask(task, newTask);
        return newTask;
    }
    
    public void updateDayTask(TeamTask teamTask,long teamId) {
      String v = getTaskDayVersion();
      String key = getTaskDayRedisKey(teamId, v);
      redis.set(key, teamTask.getTaskDay(), RedisKey.DAY2);
    }

    public TeamTask getTeamTask(long teamId) {
        String v = getTaskDayVersion();

        TeamTask teamTask = taskMap.get(teamId);
        if (teamTask == null) {//从db拉取数据   
            teamTask = new TeamTask();
            taskMap.put(teamId, teamTask);

            List<TaskPO> taskPOList = taskAO.getTeamTaskList(teamId);
            List<Integer> tidList = taskPOList.stream().map(t -> t.getTid()).collect(Collectors.toList());

            List<TaskConditionPO> conditionPOList = taskAO.getTeamTaskConditionList(teamId, tidList);
            Map<Integer, List<TaskCondition>> conditionMap = Maps.newHashMap();
            conditionPOList.forEach(con -> {
                conditionMap.computeIfAbsent(con.getTid(), k -> Lists.newArrayList())
                    .add(new TaskCondition(con));
            });

            //主线任务   
            List<Task> trunkList = Lists.newArrayList();
            taskPOList.forEach(t -> trunkList.add(new Task(t, conditionMap.get(t.getTid()))));

            //日常任务
            TaskDay taskDay = redis.getObj(getTaskDayRedisKey(teamId, v));
            log.info("---------------------getTeamTask1---------------------teamId:"+teamId);
            if (taskDay == null) {
              log.info("---------------------getTeamTask2---------------------teamId:"+teamId);
                taskDay = new TaskDay();

                //初始日常任务
                updateDayTask(teamId, teamTask, TaskConsole.getDayTask());
            }
            if(teamId == 1011000026664l) {
              log.info("test day : " + taskDay.toString());
            }
            log.debug("i want konw taskDay is : " + taskDay.toString());

            teamTask.setTrunkList(trunkList);
            teamTask.setTaskDay(taskDay);

            //活动任务
            Map<Integer, TaskActive> tempActiveMap = redis.getMapAllKeyValues(RedisKey.getKey(teamId, RedisKey.Task_Active_Map));
            if (tempActiveMap != null && tempActiveMap.size() > 0) {
                teamTask.setTaskActiveMap(tempActiveMap);
            }
        }

        if (!v.equals(teamTask.getTaskVersion())) {
            log.debug("i want konw v is : " + v + ", teamTask.getTaskVersion() is :" + teamTask.getTaskVersion());
            teamTask.setTaskVersion(v);
            teamTask.clearTaskDay();
            updateDayTask(teamId, teamTask, TaskConsole.getDayTask());
        }

        // 拉取刷新的任务
        Map<Integer, DateTime> tempActiveTaskMap = getInitActiveTask();
        for (int taskid : tempActiveTaskMap.keySet()) {
            TaskActive at = teamTask.getTaskActiveMap().get(taskid);
            if (at != null && at.getEndTime().isAfterNow()) {
                continue;
            }
            TaskBean taskBean = TaskConsole.getTaskBean(taskid);
            if (taskBean == null) { continue; }
            TaskActive ta = new TaskActive(taskid, tempActiveTaskMap.get(taskid), this.createTask(teamId, taskBean));
            teamTask.getTaskActiveMap().put(taskid, ta);
        }
        return teamTask;
    }

    private TaskPB.TaskData getTaskData(Task task) {
        List<TaskPB.TaskConditionData> conditionList = Lists.newArrayList();
        task.getConditionList().forEach(con -> conditionList.add(getTaskConditionData(con)));

        return TaskPB.TaskData.newBuilder()
            .setTid(task.getTid())
            .setStatus(task.getStatus())
            .addAllConditions(conditionList)
            .build();
    }

    private TaskPB.TaskConditionData getTaskConditionData(TaskCondition condition) {

        return TaskPB.TaskConditionData.newBuilder()
            .setCid(condition.getCid())
            .setValInt(condition.getValInt())
            .setValStr(condition.getValStr())
            .build();
    }

    public void updateTaskVersion() {
        dayTaskVersion = DateTimeUtil.getNow();
        log.debug("更新当前任务版本信息[{}]", getTaskDayVersion());
    }

    @Override
    public void offline(long teamId) {
        log.info("---------------------------task excute offline------------------------------teamId:"+teamId);
        TeamTask teamTask = taskMap.remove(teamId);
        if (teamTask == null) { return; }
        String v = getTaskDayVersion();
        String key = getTaskDayRedisKey(teamId, v);
        redis.set(key, teamTask.getTaskDay(), RedisKey.DAY2);
        // 清理过期的任务
        teamTask.clearActiveTask();
        redis.hmset(RedisKey.getKey(teamId, RedisKey.Task_Active_Map), teamTask.getTaskActiveMap());
        //------------------
        happySevenDayMap.remove(teamId);
    }
    
    public HappySevenDay getHappySevenDay(long teamId) {
      HappySevenDay happySevenDay = happySevenDayMap.get(teamId);
      if(happySevenDay == null) {
        happySevenDay = happySevenDayDao.getHappySevenDayByTeam(teamId);
        if(happySevenDay == null) {
          happySevenDay = new HappySevenDay();
          happySevenDay.setTeamId(teamId);
          happySevenDay.setCreateTime(new DateTime());
          happySevenDay.setUpdateTime(new DateTime());
          happySevenDayDao.addHappySevenDay(happySevenDay);
        }
        happySevenDayMap.put(teamId, happySevenDay);
      }
      return happySevenDay;
    }
    
    @Override
    public void dataGC(long teamId) {
    }

    private String getTaskDayRedisKey(long teamId, String v) {
        String key = RedisKey.Task_Day + v + "_" + teamId;
        return key;
    }

    private String getTaskDayVersion() {
        return dayTaskVersion;
    }

    public void clearDailyDate() {
      log.info("-----------------------clearDailyDate------------------------");
        updateTaskVersion();
        for (TeamTask teamTask : taskMap.values()) {
            teamTask.clearTaskDay();
        }
    }

    @Override
    public void initConfig() {

    }

    @Override
    public void instanceAfter() {
        taskMap = Maps.newConcurrentMap();
        updateTaskVersion();
        // 活动任务关心的Manager管理
        taskActiveRefrushList = InstanceFactory.get().getInstanceList(ITaskActiveRefrush.class);
        //
        EventBusManager.register(EEventType.登录, this);
    }

    /**
     * 登录回调 </BR>
     * 同步球队等级，刷新新增的任务 </BR>
     * 和等级相关的任务，要触发一次完成 </BR>
     *
     * @param param
     */
    @Subscribe
    public void login(LoginParam param) {
        Team team = teamManager.getTeam(param.teamId);
        levelup(team.getTeamId(), 0, team.getLevel());
        // 检查等级
        updateTask(team.getTeamId(), ETaskCondition.球队等级, team.getLevel(), EModuleCode.球队.getName());
        updateTask(team.getTeamId(), ETaskCondition.登录,1, EModuleCode.球队.getName());
    }
    
    /**
     * 主城界面的目标小红点提示.
     * @param teamId
     */
    private void sendMainTargetRedPoint(long teamId){
        TeamTask teamTask = getTeamTask(teamId);
        Team team = teamManager.getTeam(teamId);
        Task task = teamTask.getTask(TaskConsole.getTaskStepId(team.getTaskStep()));
        boolean red = false;
        
        if (task != null && task.getStatus() == EStatus.TaskFinish.getId()) {
			red = true;
		}
        
        // 联盟日常任务完成就发送小红点
        redPointManager.sendRedPointTip(new RedPointParam(teamId, ERedPoint.MainTarget.getId(), red ? 1 : 0));
    }

    /**
     * 完成任务
     *
     * @param tid
     */
    public void finishTask(long teamId, int tid) {
        TeamTask teamTask = getTeamTask(teamId);
        Task task = teamTask.getTask(tid);
        TaskBean taskBean = TaskConsole.getTaskBean(tid);
        task.updateStatus(EStatus.TaskFinish, taskBean.getType());
        EventBusManager.post(EEventType.奖励提示, new RedPointParam(teamId, ERedPoint.任务领取.getId(), 1));
        sendMainTargetRedPoint(teamId);
        sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(tid).build(), ServiceCode.Push_Task_Day_Finish);
        updateDayTask(teamTask,teamId);
    }

    /**
     * taskID:time
     * 初始化活动关心的任务
     * 创建TeamTask对象的时候调用
     * 考虑怎么存redis和怎么初始出来
     */
    private Map<Integer, DateTime> getInitActiveTask() {
        Map<Integer, DateTime> taskMap = Maps.newHashMap();
        for (ITaskActiveRefrush m : taskActiveRefrushList) {
            Map<Integer, DateTime> temp = m.getRefrushTask();
            if (temp == null || temp.size() == 0) { continue; }
            for (int taskId : temp.keySet()) {
                DateTime endTime = temp.get(taskId);
                if (taskMap.containsKey(taskId)) {
                    DateTime endTime2 = taskMap.get(taskId);
                    endTime = endTime.isAfter(endTime2) ? endTime : endTime2;
                }
                taskMap.put(taskId, endTime);
            }
        }
        return taskMap;
    }

    /**
     * 创建任务，不保存任务和条件，有需要自己返回后保存
     *
     * @param teamId
     * @param tb
     * @return
     */
    public Task createTask(long teamId, TaskBean tb) {
        Task task = new Task();
        TaskPO po = new TaskPO();
        po.setCreateTime(DateTime.now());
        po.setStatus(EStatus.TaskOpen.getId());
        po.setTeamId(teamId);
        po.setTid(tb.getTid());
        List<TaskCondition> cons = tb.getConditions().values().stream()
            .map(con -> new TaskCondition(new TaskConditionPO(teamId, tb.getTid(), con.getCondition().getType())))
            .collect(Collectors.toList());
        task.setTask(po);
        try {
            // 检测所有任务条件是否达成
            int finishCount = 0;
            for (TaskCondition taskCondition : cons) {
                TaskConditionBean taskContionBean = TaskConsole.getTaskConditionBean(TaskConsole.getTaskConditionId(taskCondition));
                Class<? extends AbstractTask> clz = TaskPool.getTask(taskCondition.getCid());
                if (clz == null) {
                    continue;
                }
                AbstractTask abstractTask = clz.newInstance();
                if (abstractTask.acceptTask(teamId, taskContionBean, taskCondition)) {
                    finishCount++;
                }
            }
            // 检查任务是否完成
            if (finishCount == cons.size()) {
                po.setStatus(EStatus.TaskFinish.getId());
                return task;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        task.setConditionList(cons);
        return task;
    }
    
    /**
     * 创建玩家的某个任务条件,并且初始化这个任务已经完成的进度.
     * @param teamId	球队Id
     * @param taskId	任务Id
     * @param con		任务枚举
     * @return
     */
    private TaskConditionPO createTaskConditionPO(long teamId, int taskId, TaskConditionBean con){
    	int cid = con.getCondition().getType();
    	int valInt = initTaskProgressVal(teamId, taskId, con.getCondition());
    	return new TaskConditionPO(teamId, taskId, cid, valInt, con.getValString());
    }
    
    /**
     * 获取玩家,新任务玩出的初始进度.
     * @param teamId			球队Id
     * @param taskId			任务Id
     * @param eTaskCondition	完成任务条件的枚举
     * @return
     */
    private int initTaskProgressVal(long teamId, int taskId, ETaskCondition eTaskCondition){
		int val = 0;
		switch (eTaskCondition) {
		case 进阶装备:
			val = getEquipQaulityVal(teamId, taskId, eTaskCondition.getType());
			break;

		default:
			break;
		}
		
		return val;
    }
    
    /**
     * 统计玩家所有球员的装备的品质有多少件达到了成就任务要求的品质.
     * @param teamId	球队Id
     * @param taskId	任务Id
     * @param cid		条件Id(进阶装备)
     * @return
     */
    private int getEquipQaulityVal(long teamId, int taskId, int cid){
    	TaskConditionBean taskConditionBean = 
    			TaskConsole.getTaskConditionBean(TaskConsole.getTaskConditionId(taskId, cid));
    	TeamEqui teamEqui = equiManager.getTeamEqui(teamId);
    	int cQualityLevel = Integer.valueOf(taskConditionBean.getValString());
    	Collection<Equi> teamAllEqui = teamEqui.getTeamAllEqui();
    	int num = 0;
    	for (Equi equi : teamAllEqui) {
    		// 装备品质大于等于成就任务要求的品质,则num加1
    		if (equi.getQuality() >= cQualityLevel) {
    			num++;
    		}
		}
    	
    	return num;
    }
    	
    //--------------------------------------------happy7days------------------------------
    @ClientMethod(code = ServiceCode.happy_seven_day_info)
    public void getHappy7days() {
      long teamId = getTeamId();
      ErrorCode ret = getHappy7days0(teamId);
      sendMsg(ret);
      log.debug("getHappy7days info. ret {}", teamId, ret);
    }
    
    @ClientMethod(code = ServiceCode.happy_seven_day_get_reward)
    public void happy_seven_day_get_reward(int taskId) {
      long teamId = getTeamId();
      ErrorCode ret = happy_seven_day_get_reward0(teamId,taskId);
      sendMsg(ret);
      log.debug("happy_seven_day_get_reward info. ret {}", teamId, ret);
    }
    
    @ClientMethod(code = ServiceCode.happy_seven_day_get_box)
    public void happy_seven_day_get_box(int boxId) {
      long teamId = getTeamId();
      ErrorCode ret = happy_seven_day_get_box0(teamId,boxId);
      sendMsg(ret);
      log.debug("happy_seven_day_get_box info. ret {}", teamId, ret);
    }
    
    public void updateHappy7dayBox(long teamId) {
      HappySevenDay happySevenDay = getHappySevenDay(teamId);
      Map<Integer,SevenDayBox> boxMap = happySevenDay.getBoxMap();
      List<Integer> list = new ArrayList<>();
      for(Integer id : Happy7dayManager.boxId) {
        SevenDayBox box = boxMap.get(id);
        if(box != null && box.getState() != SevenDayTask.no_complete) {
          continue;
        }
        list.add(id);
      }
      if(list.size() > 0) {
        Map<Integer,SevenDayTask> map =happySevenDay.getTaskMap();
        int day = createTeamDayForHappy7Day(teamId);
        int completeNum = 0;
        for(SevenDayTask t : map.values()) {
          if(t.getState() != SevenDayTask.no_complete) {
            HappySevenDayBean bean = HappySevenDayConsole.getHappySevenDayBean(t.getTaskId());
            if(bean != null) {
              if(bean.getDay() <= day) {
                completeNum++;
              }
            }
          }
        }
        if(completeNum > 0) {
          for(Integer id : list) {
            String happy7dayBox = ConfigConsole.getGlobal().happy7dayBox;
            String[] arr = happy7dayBox.split(";");
            String boxStr = arr[id - 1];
            String[] boxItem = boxStr.split("_");
            int needNum = Integer.parseInt(boxItem[0]);
            if(completeNum >= needNum) {
              SevenDayBox box = boxMap.get(id);
              if(box != null && box.getState() == SevenDayTask.no_complete) {
                box.setState(SevenDayTask.complete_and_have_no_get_reward);
              }
              if(box == null) {
                box = SevenDayBox.valueOf(id);
                box.setState(SevenDayTask.complete_and_have_no_get_reward);
                boxMap.put(box.getBoxId(), box);
                happySevenDay.setBox(boxMap);
                happySevenDay.save();
              }
            }
          }
        }
      }
    }
    
    public ErrorCode happy_seven_day_get_box0(long teamId,int boxId) {
      if(boxId > Happy7dayManager.boxId.length && boxId <= 0) {
        return ErrorCode.happy7day_can_not_get_box1;
      }
      HappySevenDay happySevenDay = getHappySevenDay(teamId);
      Map<Integer,SevenDayBox> boxMap = happySevenDay.getBoxMap();
      SevenDayBox box = boxMap.get(boxId);
      if(box == null) {
        return ErrorCode.happy7day_can_not_get_box2;
      }
      if(box.getState() == SevenDayTask.no_complete) {
        return ErrorCode.happy7day_can_not_get_box2;
      }
      if(box.getState() == SevenDayTask.complete_and_get_reward) {
        return ErrorCode.happy7day_can_not_get_box3;
      }
      String happy7dayBox = ConfigConsole.getGlobal().happy7dayBox;
      String[] arr = happy7dayBox.split(";");
      String boxStr = arr[boxId - 1];
      String[] boxItem = boxStr.split("_");
      String reward = boxItem[1];
      List<PropSimple> props = PropSimple.parseItems(reward);
      propManager.addPropList(teamId, props, true,
          ModuleLog.getModuleLog(EModuleCode.Happy7day, "开服7天乐"));
      box.setState(SevenDayTask.complete_and_get_reward);
      happySevenDay.setBox(boxMap);
      happySevenDay.save();
      
      UpdateBox.Builder builder = UpdateBox.newBuilder();
      BoxReward.Builder bbuilder = boxRewardBuiler(box);
      builder.setBox(bbuilder);
      sendMessage(teamId, builder.build(), ServiceCode.happy_seven_day_push_box_update);
      return ErrorCode.Success;
    }
    
    public ErrorCode happy_seven_day_get_reward0(long teamId,int taskId) {
      HappySevenDay happySevenDay = getHappySevenDay(teamId);
      Map<Integer,SevenDayTask> map =happySevenDay.getTaskMap();
      SevenDayTask task = map.get(taskId);
      if(task == null) {
        return ErrorCode.happy7day_no_task;
      }
      if(task.getState() == SevenDayTask.no_complete) {
        return ErrorCode.happy7day_can_not_get_reward1;
      }
      if(task.getState() == SevenDayTask.complete_and_get_reward) {
        return ErrorCode.happy7day_can_not_get_reward2;
      }
      HappySevenDayBean bean = HappySevenDayConsole.getHappySevenDayBean(taskId);
      if(bean == null) {
        return ErrorCode.happy7day_no_task;
      }
      List<PropSimple> props = PropSimple.parseItems(bean.getReward());
      propManager.addPropList(teamId, props, true,
          ModuleLog.getModuleLog(EModuleCode.Happy7day, "开服7天乐"));
      task.setState(SevenDayTask.complete_and_get_reward);
      happySevenDay.setTask(map);
      happySevenDay.save();
      push(teamId,task,bean.getDay());
      return ErrorCode.Success;
    }
    
    public void gmCompelete(long teamId) {
      HappySevenDay happySevenDay = getHappySevenDay(teamId);
      Map<Integer,SevenDayTask> map = happySevenDay.getTaskMap();
      Map<Integer,SevenDayBox> boxmap = happySevenDay.getBoxMap();
      SevenDayBox box1 = new  SevenDayBox();
      box1.setBoxId(1);
      box1.setState(1);
      SevenDayBox box2 = new  SevenDayBox();
      box2.setBoxId(1);
      box2.setState(1);
      SevenDayBox box3 = new  SevenDayBox();
      box3.setBoxId(1);
      box3.setState(1);
      boxmap.put(box1.getBoxId(), box1);
      boxmap.put(box2.getBoxId(), box2);
      boxmap.put(box3.getBoxId(), box3);
      happySevenDay.setBox(boxmap);
      
      Map<Integer, HappySevenDayBean> happySevenDayConfigMap = HappySevenDayConsole.getHappySevenDayConfigMap();
      for(HappySevenDayBean bean : happySevenDayConfigMap.values()) {
        SevenDayTask task = new SevenDayTask();
        task.setTaskId(bean.getTaskId());
        task.setState(1);
        map.put(task.getTaskId(), task);
      }
      happySevenDay.setTask(map);
      happySevenDay.save();
    }
    
    /**
     * 推送任务更新
     * @param teamId
     * @param sevenDayTask
     * @param day
     */
    public void push(long teamId,SevenDayTask sevenDayTask,int day) {
      UpdateHappyTask.Builder builder = UpdateHappyTask.newBuilder();
      builder.setDay(day);
      builder.setTasks(Happy7dayManager.happyTaskBuilder(sevenDayTask));
      int completeNum = getCompleteNum(teamId);
      builder.setCompleteNum(completeNum);
      sendMessage(teamId, builder.build(), ServiceCode.happy_seven_day_update_task);
    }
    
    private ErrorCode getHappy7days0(long teamId) {
      //取数据前的一些特殊处理
      updateHappy7dayBox(teamId);
      TeamAbility ability = teamManager.getTeamAllAbility(teamId);
      updateTask(teamId, ETaskCondition.战力,ability.getTotalCap(), "");
      //
      MyHappySevenDay.Builder builder = MyHappySevenDay.newBuilder();
      HappySevenDay happySevenDay = getHappySevenDay(teamId);
      Map<Integer,SevenDayTask> map = happySevenDay.getTaskMap();
      int day = createTeamDayForHappy7Day(teamId);
      Map<Integer, List<HappySevenDayBean>> happySevenDayConfig_day_Map = HappySevenDayConsole.getHappySevenDayConfig_day_Map();
      int completeNum = 0;
      for(Entry<Integer, List<HappySevenDayBean>> entry : happySevenDayConfig_day_Map.entrySet()) {
        int day0 = entry.getKey();
        List<HappySevenDayBean> tasks = entry.getValue();
        DayHappyTask.Builder dayBuilder = DayHappyTask.newBuilder();
        dayBuilder.setDay(day0);
        for(HappySevenDayBean bean : tasks) {
          SevenDayTask sevenDayTask = map.get(bean.getTaskId());
          if(sevenDayTask == null) {
            sevenDayTask = SevenDayTask.valueOf(bean.getTaskId());
          }else {
            if(sevenDayTask.getState() != 0 && bean.getDay() <= day) {
              completeNum++;
            }
          }
          HappyTask.Builder taskBuilder = Happy7dayManager.happyTaskBuilder(sevenDayTask);
          dayBuilder.addTasks(taskBuilder);
        }
        builder.addDayHappyTask(dayBuilder);
        builder.setCompleteNum(completeNum);
      }
      Map<Integer,SevenDayBox> boxMap = happySevenDay.getBoxMap();
      for(Integer bId : Happy7dayManager.boxId) {
        SevenDayBox b = boxMap.get(bId);
        if(b == null) {
          b = SevenDayBox.valueOf(bId);
        }
        BoxReward.Builder bBuider = boxRewardBuiler(b);
        builder.addBoxs(bBuider);
      }
      sendMessage(teamId, builder.build(), ServiceCode.happy_seven_day_push_info);
      return ErrorCode.Success;
    }
    
    public BoxReward.Builder boxRewardBuiler(SevenDayBox sevenDayBox){
      BoxReward.Builder builder = BoxReward.newBuilder();
      builder.setBoxId(sevenDayBox.getBoxId());
      builder.setState(sevenDayBox.getState());
      return builder;
    }
    
    public int getCompleteNum(long teamId) {
      HappySevenDay happySevenDay = getHappySevenDay(teamId);
      Map<Integer,SevenDayTask> map = happySevenDay.getTaskMap();
      int day = createTeamDayForHappy7Day(teamId);
      int completeNum = 0;
      for(SevenDayTask t : map.values()) {
        HappySevenDayBean bean = HappySevenDayConsole.getHappySevenDayBean(t.getTaskId());
        if(t.getState() != 0 && bean.getDay() <= day) {
          completeNum++;
        }
      }
      return completeNum;
    }
}

