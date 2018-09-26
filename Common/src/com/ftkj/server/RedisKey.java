package com.ftkj.server;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

import org.joda.time.DateTime;

import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.tool.redis.Jredis;
import com.ftkj.util.DateTimeUtil;

/**
 * @author tim.huang 2016年3月28日 redis的键集合
 */
public interface RedisKey {

    int HOUR = 60 * 60;
    int _6HOUR = 6 * HOUR;
    int _2HOUR = 2 * HOUR;
    int DAY = 24 * HOUR;
    int DAY2 = 2 * DAY;
    int DAY3 = 3 * DAY;
    int DAY7 = 7 * DAY;
    int WEEK = 7 * DAY;
    int MONTH = 30 * DAY;
    int MINUTE = 60;
    int _5MINUTE = 5 * MINUTE;

    String Demo = "Demo_";
    /** 本地策划配置缓存 */
    String Local_Config = "Local_Config_v6_";
    // 用户设置配置
    String Team_Config = "Team_Config_";
    // 每日统计
    String Team_Day_Statistics = "Team_Day_Statistics_";
    /** 每日次数 */
    String Team_Day_Num = "Team_Day_Num_";
    /** 球队次数信息. key = prefix + tid */
    String Team_Num = "Team_Num_";
    /** 球队次数信息. key = prefix + tid */
    String Team_Num_Day = "Team_Num_Day_";
    // 比赛结束数据
    String Battle_End_Source = "Battle_End_Source_";
    String Team_Scout = "Team_Scout_";
    String Team_Battle_Config = "Team_Battle_Config_v2_";
    String Prop_Day = "Prop_Day_";
    String Team_Draft_Status = "Team_Draft_Status_";

    // 球星卡经验值
    String Player_Card_Exp = "Player_Card_Exp_";

    // 多人赛历史最佳
    String Match_History_Best = "Match_History_Best_";
    // 多人赛上届排名
    String Match_Last_Rank = "Match_Last_Rank_";

    final String League_Group_Apply = "League_Group_Apply_";

    String Cross_Battle_Id = "Cross_Battle_Id";

    String Player_Logo_Lucky_Value = "Player_Logo_Lucy_Value_";

    String Team_Status = "Team_Status_";

    String League_Team_Join = "League_Team_Join_";
    String League_Join_History = "League_Join_History_";
    String League_Team_Invite = "League_Team_Invite_";
    String League_Rank = "League_Rank";
    String League_Log = "League_Log_";
    String League_Donate_Log = "League_Donate_Log_";
    String League_Team_Info = "League_Team_Info_";

    String Shop_League_Prop = "Shop_League_Prop_";
    String Shop_Money_Prop = "Shop_Money_Prop_";
    String Shop_BDMoney_Prop = "Shop_BDMoney_Prop_";

    String League_Task = "League_Task_";
    String League_Task_Team = "League_Task_Team_";

    String Rank_Sort = "Rank_Sort_";
    String Rank_Info = "Rank_Info_";

    String Team_Friend_Blask_List = "Team_Friend_Blask_List_";
    
    /** 极限挑战今日挑战发奖次数*/
    String Limit_Challenge_reward_Day = "Limit_Challenge_reward_Day_";
    /** 极限挑战今日挑战次数*/
    String Limit_Challenge_Num_Day = "Limit_Challenge_Num_Day_";
    /** 极限挑战-开始计时恢复次数时间戳(秒)*/
    String Limit_Challenge_recovery_time = "Limit_Challenge_recovery_time_Day_";
    /** 极限挑战-npc*/
    String Limit_Challenge_npcId_Day = "Limit_Challenge_npcId_Day_";
    /** 球鞋荣耀今天挑战次数*/
    String HonorMatch_challenge_day = "HonorMatch_challenge_day_";
    
    String Task_Day = "Task_Day_";
    /** 每日主线赛程挑战次数 */
    String Stage_Day = "Stage_Day_";
    /** 奖杯超快赛每天报名次数*/
    String Fast_cup_Day = "Fast_cup_Day_";
    /** 全明星赛推荐球员*/
    String All_Star_Tj_Day = "All_Star_Tj_Day_";
    /** 全明星赛挑战次数*/
    String All_Star_Challenge_Day = "All_Star_Challenge_Day_";
    /** 全明星赛击杀*/
    String ALL_STAR_KILL_RANK_DAY = "All_Star_Kill_Rank_Day_";
    /** 全明星赛全服击杀奖励领取状态*/
    String All_Star_Kill_Reward_Day = "All_Star_Kill_Reward_Day_";
    /** 全明星赛个人积分领取状态*/
    String All_Star_Score_Reward_Day = "All_Star_Score_Reward_Day_";
    /** 全明星球队积分*/
    String All_Star_Team_Score = "All_Star_Team_Score_";
    /** 每日街球副本挑战次数 */
    String StreetBall_Day = "StreetBall_Day_";
    /** 每日可出售球员数量 */
    String Trade_Max_Day_ = "Trade_Max_Day_";
    /** 每周最大购买交易额度 */
    String Trade_Week_Max_Buy_Money = "Trade_Week_Max_Buy_Money_";

    /** 交易.留言信息 */
    String Trade_Chat_Info = "Trade_Chat_Info";

    String Battle_Day_Count = "Battle_Day_Count_";

    String Arena_Vengeance = "Arena_Vengeance_";
    String Train_Team = "Train_Team_";
    String Train_All_Log = "Train_All_Log_";
    String Train_Team_Log = "Train_Team_Log_";
    //
    String League_Arena = "League_Arena_";
    String League_Arena_League_Rank = "League_Arena_League_Rank_";
    String League_Arena_History = "League_Arena_History_";
    String League_Arena_History_AwardTime = "League_Arena_History_AwardTime_";
    // 活动数据
    String AtvDataObject = "AtvDataObject_";
    String VIP_Every_Day_Gift = "VIP_Every_Day_Gift_";
    String Lottery_Lucky_Data_Day = "Lottery_Lucky_Data_Day_";
    String Lottery_Lucky_Team_Day = "Lottery_Lucky_Team_Day_";

    String ItemConvert_Limit = "ItemConvert_Limit_";
    // vip
    String VIP_Scount_Free_Frush_Count = "VIP_Scount_Free_Frush_Count_";
    /** 每天使用的vip选秀次数 */
    String VIP_Draft_Day_Count = "VIP_Draft_Day_Count_";

    /** 球星卡升阶累计概率 */
    String PlayerCard_Quality_Add_Rate = "PlayerCard_Quality_Add_Rate_";

    /** 球星卡升阶累计概率 */
    String Skill_Level_Fail = "Skill_Level_Fail_";

    /** 球星卡升阶累计概率 */
    String Scout_Roll_Count = "Scout_Roll_Count_";
    String Scout_Roll_Not_Garde_Times = "Scout_Roll_Not_Garde_Times_";// 不中次数
    String Scout_Free = "Scout_Free_";

    /** 每天使用的免费选秀次数 */
    String Draft_Free = "Draft_Free_";
    String PlayerInvestment_Buy = "PlayerInvestment_Buy_";

    String PlayerTalent_Temp = "PlayerTalent_Temp_";
    /** 球员兑换刷新出来的 */
    String PlayerExchange_RollList = "PlayerExchange_RollList_";
    /** 球员兑换刷新出来的 */
    String Battle_Guess_Gift = "Battle_Guess_Gift_";
    String Task_Active_Map = "Task_Active_Map_";
    String Player_Bid_Guess = "Player_Bid_Guess_";
    String Help_Trade_Talent = "Help_Trade_Talent_";
    
    //竞猜活动
    /**竞猜活动,所有玩家竞猜结果*/
    String Nba_Game_Guess= "NBA_PLAYER_GUESS_";

    /** 主线赛程. 系统关卡排行信息. key=pre */
    String Main_Match_Sys = "mmatch:sys:";
    /** 主线赛程. 每日限制掉落道具信息 */
    String Main_Match_Daily_Prop = "mmatch:lprop:";

    /** 天梯赛. 当前排行榜. key = pre + 段位id. value redis sorted set */
    String Ranked_Match_Medal_Rank = "rmatch_rank_m_";
    /** 天梯赛. 段位排行榜最后更新时间. key . value timestamp */
    String Ranked_Match_Medal_Rank_Up_Time = "rmatch_rank_uptime";
    /** 天梯赛. 排行榜赛季历史. key = pre + seasonId + "m_" + 段位id. value redis sorted set */
    String Ranked_Match_Medal_Rank_Season_His = "rmatch_rank_s_";
    /** 天梯赛. 排行榜每日历史. key = pre + yyyyMMdd + "m_" + 段位id. value redis sorted set */
    String Ranked_Match_Medal_Rank_Day_His = "rmatch_rank_d_";
    /** 天梯赛. 球队每日信息. Key = pre + yyyyMMdd + tid */
    String Ranked_Match_Daily = "rmatch_daily_";
    /** 天梯赛. 球队天梯赛信息. Key = pre + tid */
    String Ranked_Match_Team = "rmatch_t_";
    /** 天梯赛. 球队基本信息. Key = pre + tid */
    String Ranked_Match_Team_Simple_Data = "rmatch_tsd_";

    /** 全明星. 排行榜. key = pre + 全明星id. value redis sorted set */
    String All_Star_Rank = "allstar_rank_";
    /** 全明星. 系统信息. key = pre + 全明星id. value SysAllStar */
    String All_Star_Sys = "allstar_sys_";
    /** 竞技场. 个人排名竞技. 排名信息. key = pre + teamId. value Arena */
    String Arena = "arena_";

    /** 训练馆.枪夺列表刷新数据 */
    String Train_Refrush_List = "Train_Refrush_List";
    /** 训练馆.枪夺记录列表数据 key = pre + 目标球队id */
    String Train_Grab_Info_List = "Train_Grab_Info_List_";

    /** 聊天. 离线信息. key = pre + 目标球队id + 发送者球队ID */
    String Chat_Offline_Msg = "chat:msg:";
    /** 聊天. 离线信息. key = pre + targetTeamId, value发送者球队ID列表 */
    String Chat_Offline_sendTeamIds = "chat:senders:";
    /**聊天，离线消息是否读取(1未读，0已读)。key = pre_目标球队id_发送者球队ID*/
    String Chat_Offline_Msg_Is_Read = "Chat_Offline_Msg_Is_Read_";

    /** 好友. 切磋目标. key = pre + teamId, value (对手球队ID) */
    String Friend_Compare_Notes_target = "friend_icn_target_";
    /** 联盟组队赛每日奖励 */
    String League_Group_War_Day_Award = "League_Group_War_Day_Award_";
    
  /** 联盟每日累计贡献,每天0点重置,key = pre + yyyymmdd(当天时间)+联盟Id */
  public static String League_Day_Total_Score = "League_Day_Total_Score_";
  /** 联盟成员每日领取的贡献奖励,每天0点重置,key = pre + yyyymmdd(当天时间)+联盟Id+teamId */
  public static String League_Day_Score_Reward = "League_Day_Score_Reward_";
  /** 联盟成员每日捐赠的球券数量,每天0点重置,key = pre + yyyymmdd(当天时间)+联盟Id+teamId*/
  public static String League_Day_Donate_Score_Num = "League_Day_Donate_Score_Num_";
  /** 联盟成员每日捐贡献,每天0点重置,key = pre + yyyymmdd(当天时间)+联盟Id+teamId */
  public static String League_Player_Day_Donate_Score = "League_Player_Day_Donate_Score_";
  
  /** 联盟赛.每周贡献值 */
  public static String League_Week_Score_Sum = "la:League_Week_Score_Sum";
  /** 联盟赛.赛区内赛馆占位数据, key = pre + type (球馆类型), HASH [key = posId, value = teamId + leagueId]*/
  public static String League_Postion_By_Type = "la:League_Postion_By_";
  /** 联盟赛.赛区内球队占位数据 key = pre + teamID, value = type (球馆类型) _ posId (球馆位置ID)*/
  public static String League_Postion_Team = "la:League_Postion_Team_";
  /** 联盟赛.赛区内球队比赛数据 key = pre + type (球馆类型), HASH [key = teamId, value = LeagueAreanTeam]*/
  public static String League_Match_Team_By_Type = "la:League_Arena_Team_By_";
  /** 联盟赛.赛区内联盟历史排名数据 key = pre + type (球馆类型), value = leagueId + score*/
  public static String League_History_Rank_By_Type = "la:League_History_Rank_By_";
  /** 联盟赛.赛区内球队排名数据 key = pre + type (球馆类型), value = teamId + score*/
  public static String League_Team_Rank_By_Type = "la:League_Team_Rank_By_";
  /** 联盟赛.赛区内联盟 key = pre + type (球馆类型), value = id...*/
  public static String League_Arena_Ids_By_Type = "la:League_Arena_Ids_By_";
  /** 联盟赛.赛区内参赛联盟刷新状态key = pre, value = state*/
  public static String League_Arena_Ids_Refresh_State = "la:League_Arena_Ids_Refresh_State";
  
  /** 联盟训练馆.当前可选球队的联盟训练馆 key = pre, value = leagueTreainId(联盟训练馆配置ID) + choiseEndTime (可选结束时间)*/
  public static String League_Train_Chiose_Ids_Time = "League_Train_Chiose_Ids_Time";
  
  /** 新秀排位赛.挑战记录列表数据 key = pre + 目标球队id */
  public static String Starlet_Rank_Info_List = "Starlet_Rank_Info_List_";
  
  /**答题活动.key= pre + 目标球队id*/
  public static String Atc_Answer_Question = "Atc_Answer_Question_";

    static String getKey(long teamId, String key) {
        return key + teamId;
    }

    static String getDayKey(long teamId, String key) {
        return key + DateTimeUtil.getString(DateTime.now()) + "_" + teamId;
    }

    static String getDayKey(long teamId, String key, DateTime now) {
        return key + DateTimeUtil.getString(now) + "_" + teamId;
    }

    static String getMoonKey(long teamId, String key) {
        return key + DateTime.now().getMonthOfYear() + "_" + teamId;
    }

    static String getWeekKey(long teamId, String key) {
        return key + DateTime.now().getWeekOfWeekyear() + "_" + teamId;
    }

    static String getDayKey(long teamId, String key, LocalDate ld) {
        String month = ld.getMonthValue() < 10 ? ("0" + ld.getMonthValue()) : "" + ld.getMonthValue();
        String day = ld.getDayOfMonth() < 10 ? ("0" + ld.getDayOfMonth()) : "" + ld.getDayOfMonth();
        return key + ld.getYear() + month + day + "_" + teamId;
    }

    public static String getFriendCompareNotesTargetKey(long teamId) {
        return Friend_Compare_Notes_target + teamId;
    }

    static String getTrainGrabInfoListKey(long teamId) {
        return Train_Grab_Info_List + teamId;
    }
   
    public static String getLeaguePostionTeamKey(long teamId) {
       return League_Postion_Team + teamId;
    }
   
   public static String getLeaguePostionKey(int type) {
       return League_Postion_By_Type + type;
   }
   
   public static String getLeagueArenaTeamKey(int type) {
       return League_Match_Team_By_Type + type;
   }
   
   public static String getLeagueHistoryRankKey(int type) {
       return League_History_Rank_By_Type + type;
   }
   
   public static String getLeagueArenaIds(int type) {
       return League_Arena_Ids_By_Type + type;
   }
   
   public static String getLeagueMatchTeamRankKey(int type) {
       return League_Team_Rank_By_Type + type;
   }
   
   public static String getStarletInfoListKey(long teamId) {
       return Starlet_Rank_Info_List + teamId;
   }
   
   /**
    * 返回一个存储联盟每日累计的贡献值的key.
    * 格式:"League_Day_Total_Score_yyyyMMdd_leagueId"
    * @param leagueId	联盟的唯一ID
    * @return
    */
   public static String getLeagueDayTotalScoreKey(long leagueId){
	   return League_Day_Total_Score + DateTimeUtil.getString(DateTime.now()) + "_" + leagueId;
   }
   
   /**
    * 返回一个存储玩家答题的key值.
    * 格式:"Atc_Answer_Question_yyyyMMdd_teamId"
    * @param teamId
    * @return
    */
   public static String getAtcAnswerQuestionKey(long teamId){
	   return Atc_Answer_Question + DateTimeUtil.getString(DateTime.now()) + "_" + teamId;
   }
   
   /**
    * 返回一个存储联盟成员每日捐赠球券的key值.
    * 格式:"League_Day_Donate_Score_Num_yyyyMMdd_leagueId_teamId"
    * @param leagueId
    * @param teamId
    * @return
    */
   public static String getLeagueDayDonateScoreNumKey(int leagueId, long teamId){
	   return League_Day_Donate_Score_Num + DateTimeUtil.getString(DateTime.now()) + "_" + leagueId + "_" + teamId;
   }
   
   /**
    * 返回一个存储联盟成员领取每日贡献奖励的key值.<p>
    * 格式:"League_Day_Score_Reward_yyyyMMdd_leagueId_teamId"
    * @param leagueId
    * @param teamId
    * @return
    */
   public static String getLeagueDayScoreRewardKey(int leagueId, long teamId){
	   return League_Day_Score_Reward + DateTimeUtil.getString(DateTime.now()) + "_" + leagueId + "_" + teamId;
   }
   
   /**
    * 返回联盟成员每日捐的贡献值存储key.
    * 格式:League_Player_Day_Donate_Score_yyyyMMdd_leagueId_teamId
    * @param leagueId
    * @param teamId
    * @return
    */
   public static String getLeaguePlayerDayDonateScore(int leagueId, long teamId){
	   return League_Player_Day_Donate_Score + DateTimeUtil.getString(DateTime.now()) + "_" + leagueId + "_" + teamId;
   }

   
   /**
    * 打印所有的key,方便统计
    * @param args
 * @throws IllegalAccessException 
 * @throws IllegalArgumentException 
    */
   public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
	   for(Field f : RedisKey.class.getDeclaredFields()) {
		   int mod = f.getModifiers();
		   if(!Modifier.isStatic(mod)) {
			  continue;
		   }
		   if(f.getType() != String.class) {
			   continue;
		   }
		   System.err.println(f.get(null)+"*");
	   }
	   // 测试
	   Jredis j = new Jredis();
       j.setPort(6379);
       j.setConnectionCount(2);
       j.setDatabase(0);
       j.setHost("192.168.10.181");
       j.setPassword("zgame2017");
       JedisUtil util = new JedisUtil(j);
       //
    // System.err.println(util.getKeys("Task_Day*"));
       //
       util.delRedisCache("Task_Day*");
      // System.err.println(util.getKeys("Task_Day*"));
   }

}
