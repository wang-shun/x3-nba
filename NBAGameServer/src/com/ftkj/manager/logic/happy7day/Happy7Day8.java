package com.ftkj.manager.logic.happy7day;

import com.ftkj.enums.ETaskCondition;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;

public class Happy7Day8 extends Happy7dayManager{

  @Override
  public Happy7DayTaskType getTaskType() {
    return Happy7DayTaskType.type8;
  }

  @Override
  public ETaskCondition[] conditionTyps() {
    return new ETaskCondition[] {ETaskCondition.装备升星};
  }
  
  @Override
  public void setProgress(SevenDayTask sevenDayTask, int valInt) {
    sevenDayTask.setProgress(sevenDayTask.getProgress() + 1);
  }

}
