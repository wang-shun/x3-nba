package com.ftkj.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.HappySevenDayBean;
import com.ftkj.cfg.base.ValidateBean;

/**
 * 开服7天乐
 * @author zehong.he
 *
 */
public class HappySevenDayConsole extends AbstractConsole implements ValidateBean {
  
    private static final Logger log = LoggerFactory.getLogger(HappySevenDayConsole.class);
    
    /** key:taskId*/
    private static Map<Integer, HappySevenDayBean> happySevenDayConfigMap = new HashMap<>();
    
    /** key:day*/
    private static Map<Integer, List<HappySevenDayBean>> happySevenDayConfig_day_Map = new HashMap<>();


    public static void init() {
      happySevenDayConfigMap.clear();
      happySevenDayConfig_day_Map.clear();
      
        for (HappySevenDayBean task : CM.happySevenDayBeans) {
          happySevenDayConfigMap.put(task.getTaskId(), task);
          if(!happySevenDayConfig_day_Map.containsKey(task.getDay())) {
            happySevenDayConfig_day_Map.put(task.getDay(), new ArrayList<>());
          }
          happySevenDayConfig_day_Map.get(task.getDay()).add(task);
        }
        log.debug("HappySevenDayBean size:", happySevenDayConfigMap.size());
    }
    
    
    
    public static Map<Integer, List<HappySevenDayBean>> getHappySevenDayConfig_day_Map() {
      return happySevenDayConfig_day_Map;
    }



    public static List<HappySevenDayBean> getTaskByDay(int day){
      return happySevenDayConfig_day_Map.get(day);
    }
    
    public static HappySevenDayBean getHappySevenDayBean(int taskId) {
      return happySevenDayConfigMap.get(taskId);
    }
    
    public static Map<Integer, HappySevenDayBean> getHappySevenDayConfigMap(){
      return happySevenDayConfigMap;
    }
    
    public static int maxDay() {
      return happySevenDayConfig_day_Map.size();
    }

    @Override
    public void validate() {
      
    }

}
