package com.ftkj.cfg;

import java.util.Map;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

/**
 * 开服7天乐
 * @author zehong.he
 *
 */
public class HappySevenDayBean extends ExcelBean{

  private int taskId;
  
  private int day;
  
  private int type;
  
  private String condition;
  
  private String reward;
  
  /**
   * 条件
   */
  private Map<String, String> conditionMap;
  
  public int getConditionInt(String key) {
    try {
      String value = conditionMap.get(key);
      if(value == null) {
        return 0;
      }
      return Integer.parseInt(value);
    }catch (Exception e) {
      return 0;
    }
  }
  
  @Override
  public void initExec(RowData row) {
    String config = row.get("condition");
    if(config!=null && !config.trim().equals("")) {
      String[] s = config.trim().split(",");
      this.conditionMap = Maps.newHashMap();
      for(String c : s) {
        String[] k = c.split("=");
        if(k.length < 2){
            continue;
                }
        this.conditionMap.put(k[0], k[1]);
      }
    }
  }

  public int getTaskId() {
    return taskId;
  }

  public void setTaskId(int taskId) {
    this.taskId = taskId;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public String getReward() {
    return reward;
  }

  public void setReward(String reward) {
    this.reward = reward;
  }

}
