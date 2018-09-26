package com.ftkj.manager.logic.happy7day;

import com.ftkj.cfg.HappySevenDayBean;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;

public class Happy7Day4 extends Happy7dayManager{

  @Override
  public Happy7DayTaskType getTaskType() {
    return Happy7DayTaskType.type4;
  }

  @Override
  public ETaskCondition[] conditionTyps() {
    return new ETaskCondition[] {ETaskCondition.make_card};
  }

  public  int completeNum(HappySevenDayBean task) {
    return task.getConditionInt(Happy7DayConfigKey.star);
  }
  
  @Override
  public void setProgress(SevenDayTask sevenDayTask, int valInt) {
    sevenDayTask.setProgress(sevenDayTask.getProgress() + 1);
  }

}
