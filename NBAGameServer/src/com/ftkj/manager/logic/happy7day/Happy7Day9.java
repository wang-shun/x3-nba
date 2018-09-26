package com.ftkj.manager.logic.happy7day;

import com.ftkj.cfg.HappySevenDayBean;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;

public class Happy7Day9 extends Happy7dayManager{

  @Override
  public Happy7DayTaskType getTaskType() {
    return Happy7DayTaskType.type9;
  }

  @Override
  public ETaskCondition[] conditionTyps() {
    return new ETaskCondition[] {ETaskCondition.装备升星};
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
    int star0 = Integer.parseInt(params[0]);
    int star1 = task.getConditionInt(Happy7DayConfigKey.star);
    return star0 == star1;
  }
}
