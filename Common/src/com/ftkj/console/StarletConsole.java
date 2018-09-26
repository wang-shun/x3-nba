package com.ftkj.console;

import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.manager.starlet.DualMeetBean;
import com.ftkj.manager.starlet.DualMeetRadixBean;
import com.ftkj.manager.starlet.StarletRankAwardBean;

/**
 * 训练馆数据初始化
 * @author qin.jiang
 */
public class StarletConsole {
	
    /** 对抗赛挑战次数系数配置*/
    private static Map<Integer, DualMeetRadixBean> dualMeetRadixMap;
    /** 对抗赛类型配置*/
    private static Map<Integer, DualMeetBean> dualMeetBeanMap; 
    /** 排位赛奖励配置*/
    private static Map<Integer, StarletRankAwardBean> rankAwardBeanMap; 
    /** 排位赛排行榜显示条数*/
    private static int totalNum = 10;  
    
	public static void init(){	
	    dualMeetRadixMap = CM.dualMeetRadixBeanList.stream().collect(Collectors.toMap(DualMeetRadixBean::getNum, dualMeetRadixBean -> dualMeetRadixBean));
	    dualMeetBeanMap = CM.dualMeetBeanList.stream().collect(Collectors.toMap(DualMeetBean::getType, dualMeetBean -> dualMeetBean));
	    setRankAwardBeanMap(CM.rankAwardBeanList.stream().collect(Collectors.toMap(StarletRankAwardBean::getRank, rankMatchAwardBean -> rankMatchAwardBean)));
	}

    public static Map<Integer, DualMeetRadixBean> getDualMeetRadixMap() {
        return dualMeetRadixMap;
    }

    public static void setDualMeetRadixMap(Map<Integer, DualMeetRadixBean> dualMeetRadixMap) {
        StarletConsole.dualMeetRadixMap = dualMeetRadixMap;
    }

    public static Map<Integer, DualMeetBean> getDualMeetBeanMap() {
        return dualMeetBeanMap;
    }

    public static void setDualMeetBeanMap(Map<Integer, DualMeetBean> dualMeetBeanMap) {
        StarletConsole.dualMeetBeanMap = dualMeetBeanMap;
    }

    public static Map<Integer, StarletRankAwardBean> getRankAwardBeanMap() {
        return rankAwardBeanMap;
    }

    public static void setRankAwardBeanMap(Map<Integer, StarletRankAwardBean> rankAwardBeanMap) {
        StarletConsole.rankAwardBeanMap = rankAwardBeanMap;
    }

    public static int getTotalNum() {
        return totalNum;
    }

    public static void setTotalNum(int totalNum) {
        StarletConsole.totalNum = totalNum;
    }

}
