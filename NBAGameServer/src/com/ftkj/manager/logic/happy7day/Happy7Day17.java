package com.ftkj.manager.logic.happy7day;

import com.ftkj.cfg.HappySevenDayBean;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;

public class Happy7Day17 extends Happy7dayManager{

  @Override
  public Happy7DayTaskType getTaskType() {
    return Happy7DayTaskType.type17;
  }

  @Override
  public ETaskCondition[] conditionTyps() {
    return new ETaskCondition[] {ETaskCondition.充值};
  }
  
  @Override
  public void setProgress(SevenDayTask sevenDayTask, int valInt) {
    sevenDayTask.setProgress(valInt + sevenDayTask.getProgress());
  }
  
  public  int completeNum(HappySevenDayBean task) {
    return task.getConditionInt(Happy7DayConfigKey.singlFk);
  }
}
