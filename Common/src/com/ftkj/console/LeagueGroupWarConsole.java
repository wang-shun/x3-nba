package com.ftkj.console;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.cfg.GroupWarSeasonBean;
import com.ftkj.cfg.GroupWarTierBean;
import com.ftkj.cfg.GroupWarWeekAwardBean;
import com.ftkj.enums.EConfigKey;

public class LeagueGroupWarConsole {
	
	private static List<GroupWarSeasonBean> seasonList;
	private static List<GroupWarTierBean> tierList;
	private static List<GroupWarWeekAwardBean> weekRankList;
	public static int StartSecondOfDay;
	public static int EndSecondOfDay;
	public static int DayAwardMinPKCount = 2;
	public static int PairAbsScore = 100; //匹配分差
	public static int PairEachSeconde = 20; // 匹配秒数一轮
	public static int PairMaxCount = 3; // 匹配最大轮数，否则释放
	public static int NoReaderTier = 3; // 不用准备的段位数
	public static int Week_Award_Send_Hour = 23; // 周日23点发周奖励
	
	public static void init() {
		seasonList = CM.leagueGroupWarSeasonList;
		tierList = CM.leagueGruopTierList;
		weekRankList = CM.leagueGruopWeekAwardList;
		//
		StartSecondOfDay = ConfigConsole.parseTimeToMillis(ConfigConsole.getVal(EConfigKey.Group_War_Start_Time));
		EndSecondOfDay = ConfigConsole.parseTimeToMillis(ConfigConsole.getVal(EConfigKey.Group_War_End_Time));
		Week_Award_Send_Hour = ConfigConsole.parseTimeToMillis("23:00");//TODO 
		DayAwardMinPKCount = ConfigConsole.getIntVal(EConfigKey.Group_War_DayAward_Pkmin, 2);
		PairAbsScore = ConfigConsole.getIntVal(EConfigKey.Group_War_Pair_Abs, 100);
		PairEachSeconde = ConfigConsole.getIntVal(EConfigKey.Group_War_Pair_Time, 20);
		PairMaxCount = ConfigConsole.getIntVal(EConfigKey.Group_War_Pair_Count, 3);
		NoReaderTier = ConfigConsole.getIntVal(EConfigKey.Group_War_No_Ready_Tier, 3);
	}
	
	/**
	 * 取每周奖励
	 * @param rank
	 * @return
	 */
	public static GroupWarWeekAwardBean getWeekAwardByRank(int rank) {
		return weekRankList.stream().filter(b-> rank >= b.getStartRank() && rank <= b.getEndRank()).findFirst().orElse(weekRankList.get(weekRankList.size()-1));
	}
	
	/**
	 * 根据当前时间获取最新赛季
	 * @param now
	 * @return
	 */
	public static GroupWarSeasonBean getTheSeasonByTime(DateTime now) {
		return seasonList.stream()
			.filter(s-> now.isAfter(s.getStartTime()) && now.isBefore(s.getEndTime()))
			.findFirst().orElse(null);
	}
	
	/**
	 * 根据赛季ID查找
	 * @param id
	 * @return
	 */
	public static GroupWarSeasonBean getTheSeasonById(int id) {
		return seasonList.stream()
				.filter(s-> s.getId() == id)
				.findFirst().orElse(null);
	}
	
	
	/**
	 * 根据分数得到段位
	 * @param score
	 * @return
	 */
	public static GroupWarTierBean getTierByScore(int score) {
		int id = 0;
		for(int i=0; i < tierList.size(); i++) {
			if(score > tierList.get(i).getMinRating()) {
				id = i;
			}
		}
		return tierList.get(id);
	}
	
	/**
	 * 取段位
	 * @param id
	 * @return
	 */
	public static GroupWarTierBean getTierById(int id) {
		return tierList.stream().filter(d-> d.getId() == id)
				.findFirst().orElse(tierList.get(0));
	}
}
