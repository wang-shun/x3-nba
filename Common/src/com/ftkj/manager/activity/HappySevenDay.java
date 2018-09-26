package com.ftkj.manager.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * 开服7天乐
 * @author zehong.he
 *
 */
public class HappySevenDay extends AsynchronousBatchDB {

  /**
   * 
   */
  private static final long serialVersionUID = 5653544117348836456L;

  public static final String Division_1 = "_";
  public static final String Division_2 = ",";
  public static final String Division_3 = ";";
  
  /**
   * 玩家ID
   */
  private long teamId;
  
  /**
   * taskID_进度_state(0-未完成，1-可领，2-已领)|...
   */
  private String task;
  
  /**
   * boxId_state(0-不可领取；1-可领取；2-已领取)|...
   */
  private String box;
  
  /**
   * 创建时间
   */
  private DateTime createTime;
  
  /**
   * 更新时间
   */
  private DateTime updateTime;
  
 
  public Map<Integer,SevenDayBox> getBoxMap(){
    Map<Integer,SevenDayBox> map = new HashMap<Integer,SevenDayBox>();
    if(box == null || "".equals(box)) {
      return map;
    }
    String[] taskArr = box.split(Division_3);
    for(String taskItem : taskArr) {
      String[] itemArr = taskItem.split(Division_1);
      SevenDayBox sevenDayBox = SevenDayBox.valueOf(itemArr);
      map.put(sevenDayBox.getBoxId(), sevenDayBox);
    }
    return map;
  }
  
  public void setBox(Map<Integer,SevenDayBox> map) {
    if(map != null && map.size() > 0) {
      StringBuffer sb = new StringBuffer();
      int index = 0;
      for(SevenDayBox sevenDayBox : map.values()) {
        index++;
        sb.append(sevenDayBox.obj2Str());
        if(index < map.size()) {
          sb.append(Division_3);
        }
      }
      box = sb.toString();
      this.setUpdateTime(new DateTime());
    }
  }
  
  public Map<Integer,SevenDayTask> getTaskMap(){
    Map<Integer,SevenDayTask> map = new HashMap<Integer,SevenDayTask>();
    if(task == null || "".equals(task)) {
      return map;
    }
    String[] taskArr = task.split(Division_3);
    for(String taskItem : taskArr) {
      String[] itemArr = taskItem.split(Division_1);
      SevenDayTask sevenDayTask = SevenDayTask.valueOf(itemArr);
      map.put(sevenDayTask.getTaskId(), sevenDayTask);
    }
    return map;
  }
  
  public void setTask(Map<Integer,SevenDayTask> map) {
    if(map != null && map.size() > 0) {
      StringBuffer sb = new StringBuffer();
      int index = 0;
      for(SevenDayTask sevenDayTask : map.values()) {
        index++;
        sb.append(sevenDayTask.obj2Str());
        if(index < map.size()) {
          sb.append(Division_3);
        }
      }
      task = sb.toString();
      this.setUpdateTime(new DateTime());
    }
  }
  
  public static class SevenDayBox{
    //宝箱ID
    private int boxId;
    //0-未完成，1-可领，2-已领
    private int state;
    
    public static SevenDayBox valueOf(int boxId) {
      SevenDayBox b = new SevenDayBox();
      b.boxId = boxId;
      return b;
    }
    
    public int getBoxId() {
      return boxId;
    }



    public void setBoxId(int boxId) {
      this.boxId = boxId;
    }



    public int getState() {
      return state;
    }



    public void setState(int state) {
      this.state = state;
    }


    public static SevenDayBox valueOf(String[] itemArr) {
      SevenDayBox box = new SevenDayBox();
      box.setBoxId(Integer.parseInt(itemArr[0]));
      box.setState(Integer.parseInt(itemArr[1]));
      return box;
    }
    
    public String obj2Str() {
      StringBuffer sb = new StringBuffer();
      sb.append(boxId).append(Division_1).append(state);
      return sb.toString();
    }
  }
  
  public static class SevenDayTask{
    //未完成
    public static final int no_complete = 0;
    //完成-可领奖
    public static final int complete_and_have_no_get_reward = 1;
    //完成-已领取奖励
    public static final int complete_and_get_reward = 2;
    //任务ID
    private int taskId;
    //进度
    private int progress;
    //0-未完成，1-可领，2-已领
    private int state;
    
    public static SevenDayTask valueOf(int taskId) {
      SevenDayTask task = new SevenDayTask();
      task.setTaskId(taskId);
      return task;
    }
    
    public static SevenDayTask valueOf(String[] itemArr) {
      SevenDayTask task = new SevenDayTask();
      task.setTaskId(Integer.parseInt(itemArr[0]));
      task.setProgress(Integer.parseInt(itemArr[1]));
      task.setState(Integer.parseInt(itemArr[2]));
      return task;
    }
    
    public String obj2Str() {
      StringBuffer sb = new StringBuffer();
      sb.append(taskId).append(Division_1).append(progress).append(Division_1).append(state);
      return sb.toString();
    }
    
    public int getTaskId() {
      return taskId;
    }

    public void setTaskId(int taskId) {
      this.taskId = taskId;
    }

    public int getProgress() {
      return progress;
    }

    public void setProgress(int progress) {
      this.progress = progress;
    }

    public int getState() {
      return state;
    }

    public void setState(int state) {
      this.state = state;
    }
    
    
  }
  

  @Override
  public String getSource() {
    return StringUtil.formatSQL(teamId,task,box,createTime,
        updateTime);
  }

  @Override
  public String getRowNames() {
    return "team_id, task,box, create_time," +
        " update_time";
  }

  @Override
  public String getTableName() {
    return "t_u_happy_sevenday";
  }

  @Override
  public synchronized void save() {
      super.save();
  }
  
  @Override
  public void del() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public List<Object> getRowParameterList() {
    return Lists.newArrayList(teamId, task,box, createTime,
        updateTime);
  }

  public long getTeamId() {
    return teamId;
  }

  public void setTeamId(long teamId) {
    this.teamId = teamId;
  }

  public String getTask() {
    return task;
  }

  public void setTask(String task) {
    this.task = task;
  }

  public DateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(DateTime createTime) {
    this.createTime = createTime;
  }

  public DateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(DateTime updateTime) {
    this.updateTime = updateTime;
  }
  
  
  public String getBox() {
    return box;
  }

  public void setBox(String box) {
    this.box = box;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  
}
