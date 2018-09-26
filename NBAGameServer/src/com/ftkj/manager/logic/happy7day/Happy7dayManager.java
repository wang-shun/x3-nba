package com.ftkj.manager.logic.happy7day;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.HappySevenDayBean;
import com.ftkj.console.HappySevenDayConsole;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.activity.HappySevenDay;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;
import com.ftkj.manager.logic.TaskManager;
import com.ftkj.proto.HappySevenDayPB.HappyTask;
import com.ftkj.proto.HappySevenDayPB.UpdateHappyTask;
import com.ftkj.server.ServiceCode;

/**
 * 开服7天爽
 * @author zehong.he
 *
 */
public abstract class Happy7dayManager extends BaseManager{
  
  public static final int[] boxId = new int[] {1,2,3};

  public static Map<ETaskCondition,List<Happy7dayManager>> taskExcuterMap = new ConcurrentHashMap<>();
  
  public static Map<Integer,Happy7dayManager> happy7dayManagerMap = new ConcurrentHashMap<>();
  
  @IOC
  TaskManager taskManager;
  
  /**
   * 任务类型
   * @return
   */
  public abstract Happy7DayTaskType getTaskType();
  
  /**
   * 触发条件
   * @return
   */
  public abstract ETaskCondition[] conditionTyps();

  public abstract void setProgress(SevenDayTask sevenDayTask,int valInt);
  
  public boolean checked(long teamId, ETaskCondition condition, int valInt, String valStr,HappySevenDayBean task) {
    return true;
  }
  
  /**
   * 完成总数
   * @param task
   * @return
   */
  public int completeNum(HappySevenDayBean task) {
    return task.getConditionInt(Happy7DayConfigKey.num);
  }
  
  public static void updateTask0(long teamId, ETaskCondition condition, int valInt, String valStr) {
    List<Happy7dayManager> list = taskExcuterMap.get(condition);
    if(list != null) {
      for(Happy7dayManager taskExcuter : list) {
        taskExcuter.updateTask(teamId, condition, valInt, valStr);
      }
    }
  }
  
  public void updateTask(long teamId, ETaskCondition condition, int valInt, String valStr) {
    Map<Integer, HappySevenDayBean> happySevenDayConfigMap = HappySevenDayConsole.getHappySevenDayConfigMap();
    HappySevenDay happySevenDay = taskManager.getHappySevenDay(teamId);
    Map<Integer,SevenDayTask> map = happySevenDay.getTaskMap();
    boolean save = false;
    boolean hashCompelete = false;
    for(HappySevenDayBean task : happySevenDayConfigMap.values()) {
      if(task.getType() == getTaskType().getCode()) {
        if(checked(teamId, condition, valInt, valStr, task) == false) {
          continue;
        }
        SevenDayTask sevenDayTask = map.get(task.getTaskId());
        if(sevenDayTask == null) {
          sevenDayTask = new SevenDayTask();
          sevenDayTask.setTaskId(task.getTaskId());
          map.put(task.getTaskId(), sevenDayTask);
        }
        if(sevenDayTask.getState() == SevenDayTask.no_complete) {
          setProgress(sevenDayTask, valInt);
          save = true;
          if(sevenDayTask.getProgress() >= completeNum(task)) {
            hashCompelete = true;
            sevenDayTask.setState(SevenDayTask.complete_and_have_no_get_reward);
          }
          //push
          push(teamId,sevenDayTask,task.getDay());
        }
      }
    }
    if(save) {
      happySevenDay.setTask(map);
      happySevenDay.save();
      if(hashCompelete) {
        taskManager.updateHappy7dayBox(teamId);
      }
    }
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
    builder.setTasks(happyTaskBuilder(sevenDayTask));
    int completeNum = taskManager.getCompleteNum(teamId);
    builder.setCompleteNum(completeNum); 
    sendMessage(teamId, builder.build(), ServiceCode.happy_seven_day_update_task);
  }
  
  public static HappyTask.Builder happyTaskBuilder(SevenDayTask sevenDayTask){
    HappyTask.Builder builder = HappyTask.newBuilder();
    builder.setTaskId(sevenDayTask.getTaskId());
    builder.setProgress(sevenDayTask.getProgress());
    builder.setState(sevenDayTask.getState());
    HappySevenDayBean happySevenDayBean = HappySevenDayConsole.getHappySevenDayBean(sevenDayTask.getTaskId());
    if(happySevenDayBean != null) {
      Happy7dayManager happy7dayManager = happy7dayManagerMap.get(happySevenDayBean.getType());
      if(happy7dayManager != null) {
        builder.setTotalNum(happy7dayManager.completeNum(happySevenDayBean));
      }
    }
    return builder;
  }
  
  public void init() {
    ETaskCondition[] conditions = conditionTyps();
    if(conditions != null) {
      happy7dayManagerMap.put(getTaskType().getCode(), this);
      for(ETaskCondition condition : conditionTyps()) {
        if(!taskExcuterMap.containsKey(condition)) {
          taskExcuterMap.put(condition, new ArrayList<Happy7dayManager>());
        }
        taskExcuterMap.get(condition).add(this);
      }
    }
  }
  
  @Override
  public void instanceAfter() {
    init();
  }
}
