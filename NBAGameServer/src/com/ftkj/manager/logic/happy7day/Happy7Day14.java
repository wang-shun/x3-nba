package com.ftkj.manager.logic.happy7day;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.activity.HappySevenDay.SevenDayTask;

public class Happy7Day14 extends Happy7dayManager{

  private static final Logger log = LoggerFactory.getLogger(Happy7Day14.class);
  
  @Override
  public Happy7DayTaskType getTaskType() {
    return Happy7DayTaskType.type14;
  }

  @Override
  public ETaskCondition[] conditionTyps() {
    return new ETaskCondition[] {ETaskCondition.完成一场比赛};
  }
  
  @Override
  public void setProgress(SevenDayTask sevenDayTask, int valInt) {
    sevenDayTask.setProgress(sevenDayTask.getProgress() + 1);
  }
  
  @Override
  public void updateTask(long teamId, ETaskCondition condition, int valInt, String valStr) {
    if(valStr == null || "".equals(valStr)) {
      log.error("Happy7Day14 updateTask valStr is null");
      return;
    }
    String[] arr = valStr.split(",");
    if(arr.length < 5) {
      log.error("Happy7Day14 updateTask valStr leng < 5");
      return;
    }
    int battleType = Integer.parseInt(arr[4]);
    if(battleType == EBattleType.Ranked_Match.getId()) {
      super.updateTask(teamId, condition, valInt, valStr);
    }
  }
}
