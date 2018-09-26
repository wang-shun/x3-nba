package com.ftkj.manager.logic.happy7day;

import com.ftkj.enums.ETaskCondition;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;

@Deprecated
public class Happy7Day13 extends Happy7dayManager{

  @Override
  public Happy7DayTaskType getTaskType() {
    return Happy7DayTaskType.type13;
  }

  @Override
  public ETaskCondition[] conditionTyps() {
    return new ETaskCondition[] {ETaskCondition.刷新球队天赋};
  }

  @Override
  public void setProgress(SevenDayTask sevenDayTask, int valInt) {
    sevenDayTask.setProgress(sevenDayTask.getProgress() + 1);
  }
}
