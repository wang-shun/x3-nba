package com.ftkj.console;

import com.ftkj.enums.ETaskType;
import com.ftkj.manager.task.ITaskFinish;
import com.ftkj.manager.task.StarBean;
import com.ftkj.manager.task.TaskBean;
import com.ftkj.manager.task.TaskCondition;
import com.ftkj.manager.task.TaskConditionBean;
import com.ftkj.manager.task.TaskDayBean;
import com.ftkj.manager.task.TaskStarAwardBean;
import com.ftkj.manager.task.finish.TaskBattleWinGradeFinish;
import com.ftkj.manager.task.finish.TaskBetweenCheckFinish;
import com.ftkj.manager.task.finish.TaskBetweenCheckFinish2;
import com.ftkj.manager.task.finish.TaskPlayerGradeFinish;
import com.ftkj.manager.task.finish.TaskTagFinish;
import com.ftkj.manager.task.finish.TaskTweenFinish;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年6月8日
 */
public class TaskConsole {

    private static Map<Integer, TaskBean> taskMap;
    private static Map<Integer, TaskConditionBean> taskConditionMap;
    /** 日常任务基础表 */
    private static List<TaskDayBean> dayTaskList;
    /** 日常任务经验系数基础表 */
    private static Map<Integer, StarBean> starMap;
    /** 日常星级奖励 */
    private static Map<Integer, TaskStarAwardBean> taskStarAwardMap;
    /** 通用任务 */
    private static List<TaskBean> trunkTask;

    /**
     * 目标任务列表
     * step : taskId
     */
    private static Map<Integer, Integer> targetTaskMap;

    public static void init() {

        Map<Integer, List<TaskConditionBean>> conMaps = Maps.newHashMap();
        Map<Integer, TaskBean> taskMapTmp = Maps.newHashMap();
        Map<Integer, TaskConditionBean> taskConditionMapTmp = Maps.newHashMap();
        List<TaskDayBean> dayTaskTmp = Lists.newArrayList();
        List<TaskDayBean> activeTaskTmp = Lists.newArrayList();
        List<TaskBean> trunkTaskTmp = Lists.newArrayList();
        taskStarAwardMap = Maps.newHashMap();

        // 日常任务经验系数
        starMap = CM.starList.stream().collect(Collectors.toMap(StarBean::getStar, star -> star));

        CM.taskConditionList.forEach(con -> {//初始化所有条件
            TaskConditionBean tcb = new TaskConditionBean(con, getFinish(con.getFid()));
            conMaps.computeIfAbsent(con.getTid(), key -> Lists.newArrayList())
                .add(tcb);
            taskConditionMapTmp.put(getTaskConditionId(con.getTid(), con.getCid()), tcb);
        });

        CM.taskList.forEach(task -> {
            // 主线任务和成就任务，都做通用任务处理
            if (ETaskType.getETaskType(task.getType()).isSaveDb()) {
                TaskBean tb = new TaskBean(task, conMaps.get(task.getTid()));
                taskMapTmp.put(tb.getTid(), tb);
                trunkTaskTmp.add(tb);
            } else if (task.getType() == ETaskType.日常任务.getType()) {
                TaskDayBean tb = new TaskDayBean(task, conMaps.get(task.getTid()));
                taskMapTmp.put(tb.getTid(), tb);
                dayTaskTmp.add(tb);
            } else if (task.getType() == ETaskType.活动任务.getType()) {
                TaskDayBean tb = new TaskDayBean(task, conMaps.get(task.getTid()));
                taskMapTmp.put(tb.getTid(), tb);
                activeTaskTmp.add(tb);
            }
        });

        // 日常任务星级奖励
        taskStarAwardMap = CM.taskStarAwardList.stream().collect(Collectors.toMap(TaskStarAwardBean::getId, taskStarAward -> taskStarAward));

        //
        taskMap = taskMapTmp;

        taskConditionMap = taskConditionMapTmp;

        dayTaskList = dayTaskTmp;

        trunkTask = trunkTaskTmp;

        targetTaskMap = trunkTaskTmp.stream().filter(t -> t.getTeamPurpose() > 0).sorted(new Comparator<TaskBean>() {
            @Override
            public int compare(TaskBean o1, TaskBean o2) {
                return o1.getTeamPurpose() - o2.getTeamPurpose();
            }
        }).collect(Collectors.toMap(TaskBean::getTeamPurpose, t -> t.getTid()));
    }

    /**
     * 取下一个任务目标
     *
     * @param currStep
     * @return
     */
    public static int getNextTaskStep(int currStep) {
        int nextStep = targetTaskMap.keySet().stream().filter(step -> step > currStep).findFirst().orElse(0);
        return nextStep;
    }

    /**
     * 取该步的任务ID
     *
     * @param step 步数
     * @return 任务ID
     */
    public static int getTaskStepId(int step) {
        if (!targetTaskMap.containsKey(step)) {
            return 0;
        }
        return targetTaskMap.get(step);
    }

    public static int getTaskConditionId(TaskCondition con) {
        return getTaskConditionId(con.getTid(), con.getCid());
    }

    public static int getTaskConditionId(int tid, int cid) {
        return tid * 1000 + cid;
    }

    private static ITaskFinish getFinish(int fid) {
        if (fid == 0) {//必须相同
            return new TaskTagFinish();
        } else if (fid == 1) {//数值大于等于所需
            return new TaskTweenFinish();
        } else if (fid == 2) {//球员等级大于条件值
            return new TaskPlayerGradeFinish();
        } else if (fid == 3) {//比赛胜利，并且等级比对方低
            return new TaskBattleWinGradeFinish();
        } else if (fid == 4) {//选秀球员等级
            return new TaskBetweenCheckFinish();
        } else if (fid == 5) {//
            return new TaskBetweenCheckFinish2();
        }
        return null;
    }

    public static TaskBean getTaskBean(int tid) {
        return taskMap.get(tid);
    }

    public static TaskConditionBean getTaskConditionBean(int cid) {
        return taskConditionMap.get(cid);
    }

    /**
     * @param finshTaskId 完成的每日任务Id
     * @return
     */
    public static TaskDayBean getNextDayTask(int finshTaskId) {
        return dayTaskList.stream().filter(taskDayBean -> taskDayBean.getLimitTid() == finshTaskId).findFirst().orElse(null);
    }

    public static List<TaskBean> getTaskTrunkBeanByLevel(int level) {
        return trunkTask.stream().filter(task -> task.getLimitLevel() == level).collect(Collectors.toList());
    }

    /**
     * 获取奖励基础数据
     *
     * @param rewardId 奖励唯一Id
     * @return
     */
    public static TaskStarAwardBean getTaskStarAwardBean(int rewardId) {
        return taskStarAwardMap.get(rewardId);
    }

    /**
     * 获取日常任务基础数据
     *
     * @return
     */
    public static List<TaskDayBean> getDayTask() {
        return dayTaskList;
    }

    public static Map<Integer, StarBean> getStarMap() {
        return starMap;
    }
}
