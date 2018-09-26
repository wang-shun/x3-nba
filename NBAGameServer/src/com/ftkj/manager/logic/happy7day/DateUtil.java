package com.ftkj.manager.logic.happy7day;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

  
  /**
   * 获取零点时间
   * @param time
   * @return
   */
  public static long getZero(long time) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date(time));
    calendar.set(Calendar.HOUR, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    return calendar.getTime().getTime();
  }
}
