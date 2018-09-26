package com.ftkj.console;

import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.manager.league.leagueArean.AreanRewardBean;
import com.ftkj.manager.league.leagueArean.PostionScoreBean;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Maps;

/**
 * 球馆赛配置管理
 * @author Jay
 * @time:2017年7月25日 下午5:13:23
 */
public class LeagueArenaConsole {
	/** 周几开放 */
	public static int OpenDayOfWeek = 7;
	/** 开始时间 */
	public static long StartTime;
	/** 结束时间 */
	public static long EndTime;	
	/** 发奖时间 */
    public static long SendRewardTime; 
    /** 刷新贡献榜的时间 */
    public static long refreshScoreRankWeek = 7;
    /** 重置贡献榜时间 */
    public static long resetScoreRankWeek = 1; 
   

	/** 排行奖励配置表*/
	public static Map<Integer, Map<Integer, AreanRewardBean>> matchRewardMap;
	
	/** 赛馆位置配置表*/
	public static Map<Integer, PostionScoreBean> postionScoreMap;
	
	public static Map<Integer, Map<Integer, AreanRewardBean>> getMatchRewardMap() {
        return matchRewardMap;
    }

    public static void setMatchRewardMap(Map<Integer, Map<Integer, AreanRewardBean>> matchRewardMap) {
        LeagueArenaConsole.matchRewardMap = matchRewardMap;
    }

    public static Map<Integer, PostionScoreBean> getPostionScoreMap() {
        return postionScoreMap;
    }

    public static void setPostionScoreMap(Map<Integer, PostionScoreBean> postionScoreMap) {
        LeagueArenaConsole.postionScoreMap = postionScoreMap;
    }

    /**
	 * 配置初始化
	 */
	public static void init() {
		// 初始化完，写任务调度
	    matchRewardMap = Maps.newHashMap();
	    for(AreanRewardBean matchRewardBean : CM.matchRewardBeanList) {
	        Map<Integer, AreanRewardBean> map = matchRewardMap.get(matchRewardBean.getType());
	        if(map == null) {
	            map = Maps.newHashMap();
	            matchRewardMap.put(matchRewardBean.getType(), map);
	        }
	        
	        map.put(matchRewardBean.getRank(), matchRewardBean);
	    }
	    
		// 联盟赛积分ID
	    postionScoreMap =  CM.postionScoreBeanList.stream().collect(Collectors.toMap(PostionScoreBean::getId, (bean)->  bean));	   
	    OpenDayOfWeek =  ConfigConsole.getGlobal().leagueArenaOpenWeekday;
	}
	
	/**
	 * 当前届开始时间,周一，零点清理
	 * @return
	 */
	public static DateTime currWeekStartTime() {
		return DateTime.now().withDayOfWeek(1).withHourOfDay(0)
				.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
	}

	/**
	 * 是否在比赛进行时
	 * 比赛开启时间
	 * @return
	 */
	public static boolean isPKingTime(DateTime now) {
		int dayOfWeek = now.getDayOfWeek();
		if(OpenDayOfWeek != dayOfWeek) {
			return false;
		}
		
		long mid = DateTimeUtil.midnight();
	    StartTime = mid + ConfigConsole.getGlobal().leagueArenaStartTime;
	    EndTime = mid + ConfigConsole.getGlobal().leagueArenaEndTime;
	    
		long nowtime = now.getMillis();
		if(nowtime < StartTime || nowtime > EndTime) {
			return false;
		}
		return true;
	}
	
	/**
	 * 是否是已结束
	 * @return
	 */
	public static boolean isEnd(DateTime now) {
		int dayOfWeek = now.getDayOfWeek();
		if(OpenDayOfWeek != dayOfWeek) {
			return false;
		}
		long nowtime = now.getMillis();
		if(nowtime > EndTime) {
			return true;
		}
		return false;
	}
	
	/**
     * 是否开始选球队
     * @return
     */
    public static boolean isChoiseTeam(DateTime now) {
        int dayOfWeek = now.getDayOfWeek();
        if(OpenDayOfWeek != dayOfWeek) {
            return false;
        }
        
        long mid = DateTimeUtil.midnight();
        long choiseTime = mid + ConfigConsole.getGlobal().leagueChoiseTeamStartTime;
        long nowtime = now.getMillis();
        if(nowtime > choiseTime) {
            return true;
        }
        return false;
    }
	
	/**
     * 发奖时间结束
     * @return
     */
    public static boolean isSendRewardEnd(DateTime now) {
        int dayOfWeek = now.getDayOfWeek();
        if(OpenDayOfWeek != dayOfWeek) {
            return false;
        }
        long nowtime = now.getMillis();
        long mid = DateTimeUtil.midnight();
        SendRewardTime = mid + ConfigConsole.getGlobal().leagueSendRewardtime;
        
        if(nowtime > SendRewardTime) {
            return true;
        }
        return false;
    }    
    
	public static void main(String[] args) {
		DateTime now = DateTimeUtil.getDateTime("2017-11-15 21:00:01");
		System.err.println(isEnd(now));
	}	
	
}
