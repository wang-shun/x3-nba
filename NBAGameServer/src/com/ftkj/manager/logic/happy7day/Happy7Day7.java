package com.ftkj.manager.logic.happy7day;

import com.ftkj.cfg.HappySevenDayBean;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;

public class Happy7Day7 extends Happy7dayManager{

  @Override
  public Happy7DayTaskType getTaskType() {
    return Happy7DayTaskType.type7;
  }

  @Override
  public ETaskCondition[] conditionTyps() {
    return new ETaskCondition[] {ETaskCondition.强化装备};
  }

  
  @Override
  public void setProgress(SevenDayTask sevenDayTask, int valInt) {
    sevenDayTask.setProgress(sevenDayTask.getProgress() + 1);
  }
  
  @Override
  public boolean checked(long teamId, ETaskCondition condition, int valInt, String valStr,HappySevenDayBean task) {
    if(valStr == null || "".equals(valStr)) {
      return false;
    }
    String[] params = valStr.split(",");
    if(params.length < 1) {
      return false;
    }
    int level0 = Integer.parseInt(params[0]);
    int level1 = task.getConditionInt(Happy7DayConfigKey.lv);
    return level0 == level1;
  }
}
