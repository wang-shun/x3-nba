package com.ftkj.enums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EConfigKey {
    Drop_Scout_Player,
    Max_Player_Count,
    SCOUT_NUM,
    Team_Init_Price,
    Battle_Day_Count,
    DEBRIS_NUM,
    SAFE_COUNT,
    DEBRIS_PRICE,
    LUCKY_EACH,
    LUCKY_QUALITY,
    DEBRIS_TID,
    TRAN_TID,

    /*
     * 废弃 MAX_LV, MAX_STRONGLV, MAX_QUALITY,
     */

    MAX_EQUI_NUM, EQUI_INIT_LIST, BLESS_PROP, tactics_default,

    League_Create_Level, // 创建所需等级
    League_Create_Money, // 创建所需球卷
    League_Create_Prop, // 创建所需道具
    League_Max_FMZ, // 最大副盟主数量
    League_Max_LS, // 最大理事数量
    League_Max_JY, // 最大精英数量
    League_Max_CY, // 最大成员数量
    League_Create_Log, // 创建联盟日志文字描述
    League_Quit_Log, // 退出联盟日志文字描述
    League_Join_Log, // 加入联盟日志文字描述
    League_Donate_Money_Log, // 捐献球卷联盟日志文字描述
    League_Donate_Medal_Log, // 捐献球卷联盟日志文字描述
    League_Level_Up_Log, // 捐献球卷联盟日志文字描述
    League_Honor_Level_Up_Log, // 捐献球卷联盟日志文字描述
    League_Appotion_Log, // 捐献球卷联盟日志文字描述
    League_Notice_Log, // 捐献球卷联盟日志文字描述
    League_Declaration_Log, // 捐献球卷联盟日志文字描述

    Team_Arena_Power_Second, // 球馆能量恢复间隔时间
    Team_Arena_Power_Max, // 球馆能量最大上限

    STAGE_TODAY_MAX_NUM, // 主线副本每日挑战次数
    EQUI_EXP, // 装备经验的道具ID
    StreetBall_EveryDay_Num, // 街球副本每天可挑战次数
    /** 街球赛赢的比分>30 */
    StreetBall_Win_Point,
    // Text_Shield,//屏蔽文字

    //
    /** 球员仓库上限 */
    MAX_STORAGE_SIZE,
    /** 交易许可证 */
    Trade_Prop_ID,
    /** 交易费率 */
    Trade_Money_Rate,
    /** 每周交易上限 */
    Trade_Max_Money_week,
    /** 每天可出售上限 */
    Trade_Max_EveryDay,
    /** 交易.留言板最大条数上限 */
    Trade_Max_Level_Message("Trade_Max_Level_Message"),
    /** 联盟赛积分ID */
    League_Arena_Jf,
    /** 联盟赛排名奖励1 */
    League_Battle_Award1,
    /** 联盟赛排名奖励2 */
    League_Battle_Award2,
    /** 联盟赛排名奖励3 */
    League_Battle_Award3,
    /** 联盟赛排名奖励3 */
    League_Battle_Award4,
    /** 补签消耗球券 */
    Sign_Patch_Fk,

    Custom_Max_Room, // 玩家可创建房间上限
    Custom_Total_Room, // 服务器房间上限

    /** 训练馆.训练馆初始NpcId */
    Train_Init_Npc("Train_Init_Npc"),
    /** 训练馆.最大抢夺次数 */
    Train_Grab_Count_Max("Train_Grab_Count_Max"),
    /** 训练馆.不可抢总产出资源的(百分比) */
    Train_Resource_Surplus("Train_Resource_Surplus"),
    /** 训练馆.掠夺对方总产出资源的（百分比）*/
    Train_Grab_Resource_Surplus("Train_Grab_Resource_Surplus"),
    /** 训练馆.训练的玩家中随机的攻防范围 */
    Train_Player_Cap_Range("Train_Player_Cap_Range"),
    /** 训练馆.抢夺次数刷新时间间隔(分) */
    Train_Grab_Count_Refresh("Train_Grab_Count_Refresh"),
    /** 训练馆.个人训练位个数*/
    Train_Team_Count("Train_Team_Count"),   
    /** 训练馆.联盟训练位个数*/
    Train_League_Count("Train_League_Count"), 
    /** 训练馆.抢夺CD时间间隔(分)*/
    Train_GrabCD_Refresh("Train_GrabCD_Refresh"),
    
    happy7dayBox("happy7dayBox"),
    /**训练馆.刷新抢夺列表时间间隔(分)*/
    Train_RefreshList_CD("Train_RefreshList_CD"),                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
    /**训练馆.仓库球员为空，初始列表的攻防*/
    Train_Storage_Null_Init_Cap("Train_Storage_Null_Init_Cap"),

    Scout_Roll_Times_A,
    Scout_Roll_Times_C,

    Scout_Roll_Drop_A,
    Scout_Roll_Drop_B,
    Scout_Roll_Drop_C,
    Scout_Roll_Drop_S,
    Scout_Roll_Drop_S2,
    Scout_Roll_Drop_S3,
    Scout_Roll_Drop_A_Must,
    Scout_Roll_Drop_C_Must,
    Scout_Roll_Times_A_Must,
    Scout_Roll_Times_C_Must,
    Scout_Roll_Drop_Help,
    Scout_Exchange_Drops,
    Normal_Refresh_Player_Need, // 1040:1
    Advanced_Refresh_Player_Need, // 1041:1
    Scout_Exchange_Need,
    Player_Recycling,
    Advanced_Refresh_Player_Price_Drop, // 3003
    Player_Level_Max_Grade,
    Player_Star_X_Common,
    Player_Star_X_Prop,
    Scout_Roll_Prop_A,
    Scout_Roll_Prop_B,
    Scout_Roll_Prop_C,
    
    //----新增的顶级招募一次相关配置数据----
    /**顶级招募一次掉落ID（绑定球员）*/
    Scout_Roll_Drop_C_1,
    /**顶级招募一次需要绑定球员的品质要求*/
    Scout_Roll_Drop_C_1_Bind_Grade,
    /**顶级招募一次必中绑定底薪需要的累计次数（必中之后是额外获得）*/
    Scout_Roll_Times_C_1,
    /**顶级招募一次必中绑定底薪的掉落ID*/
    Scout_Roll_Drop_S2_1,
    /**顶级招募一次必中S+底薪的必中底薪次数*/
    Scout_Roll_Times_C_Must_1,
    /**顶级招募一次必中S+底薪的掉落ID*/
    Scout_Roll_Drop_C_Must_1,

    Draft_Day_Count,
    Lineup_Count_Money,
    Chat_Level,
    Team_Arena_Power_Price,
    Draft_Auto_Npc,

    /** 大版本. “;”分隔. 大版本不兼容. 客户端写死 */
    Version_Major("Version_Major"),
    /** 小版本. 客户端必须>=服务器 */
    Version_Minor("Version_Minor"),
    /** 好友.切磋请求过期时间(秒) */
    FriendCompareNotesDealTime("FriendCompareNotesDealTime"),
    /** 好友.拒绝切磋请求时间(秒) */
    FriendCompareNotesRefreshTime("FriendCompareNotesRefreshTime"),
    /** 球队.改名卡道具ID */
    TeamChangeNamePropId("TeamChangeNamePropId"),
    /** 球队.更换主球星道具ID */
    TeamChangeXplayerPropId("TeamChangeXplayerPropId"),
    /** 球队.工资帽道具ID */
    Team_Add_Price_Item("Team_Add_Price_Item"),
    /** 球队.降低工资道具ID */
    Team_Lower_Price_Item("Team_Lower_Price_Item"),
    /** 球队.降低工资道具消耗数量 */
    Team_Lower_Price_Item_Num("Team_Lower_Price_Item_Num"),
    /** 聊天.单个目标玩家留言最大上限（条） */
    ChatOfflineMsgLimit("ChatOfflineMsgLimit"),
    /** 聊天.信息最大长度上限 */
    Chat_Msg_Wrold_Count_Limit("Chat_Msg_Wrold_Count_Limit"),

    /** 联盟.修改联盟公告消耗球卷 */
    EditLeagueNoticePrice("EditLeagueNoticePrice"),

    ExceedPriceCap("ExceedPriceCap"),
    /** 球员招募. Int. 待签球员最大数量 */
    Scout_BeSign_Max_Num("Scout_BeSign_Max_Num"),
    /** 主线赛程. Int. 默认开启的关卡配置id */
    MMatch_DEFAULT_OPEN_LEV("MAIN_MATCH_DEFAULT_OPEN_LEV"),
    /** 主线赛程. Int. 最大星级 */
    MMatch_MAX_STAR("MAIN_MATCH_MAX_STAR"),
    /** 主线赛程. Int. 扫荡要求最小星级 */
    MMatch_QUICK_MIN_STAR("MAIN_MATCH_QUICK_MIN_STAR"),
    /** 主线赛程. Int. 挑战次数初始次数 */
    MMatch_NUM_INIT("MAIN_MATCH_NUM_INIT"),
    /** 主线赛程. Int. 挑战次数最大次数 */
    MMatch_NUM_MAX("MAIN_MATCH_NUM_MAX"),
    /** 主线赛程. Int. 挑战次数恢复cd(秒) */
    MMatch_NUM_CD("MAIN_MATCH_NUM_CD"),
    // /** 主线赛程. Int. 购买挑战次数花费的球卷 */
    // MMatch_NUM_MONEY("MAIN_MATCH_NUM_MONEY"),
    /**
     * 主线赛程. String. 锦标赛. 不允许开始新赛前多人训练赛的时间. 格式: HH:mm 或 HH:mm:ss
     * <p>
     * 每日23：30不允许开始新赛前多人训练赛的赛程 每日23：50强制结算奖励，如23：50正在比赛玩家，比赛结束之后强制结束结算奖励
     * 每日00：00更新至未开始状态 4种状态，其中，23：30未报名，为“休赛期状态”；23：30前报名但该轮并未结束，则为加时状态
     */
    MMatch_CS_STOP_TIME("MAIN_MATCH_CS_STOP_TIME"),
    /** 主线赛程. String. 锦标赛. 强制结算奖励时间. 格式: HH:mm 或 HH:mm:ss */
    MMatch_CS_FORCE_END_TIME("MAIN_MATCH_CS_FORCE_END_TIME"),
    /** 主线赛程. String. 锦标赛. 更新至未开始状态时间. 格式: HH:mm 或 HH:mm:ss */
    MMatch_CS_RESET_TIME("MAIN_MATCH_CS_RESET_TIME"),
    /** 主线赛程. Int. 装备经验道具id */
    MMatch_EQUIP_EXP_ITEM("MAIN_MATCH_EQUIP_EXP_ITEM"),
    /** 主线赛程. Int. 装备经验结算时的最大秒数, 防止出现巨量经验bug(默认一周) */
    MMatch_EQUIP_EXP_MAX_TIME("MAIN_MATCH_EQUIP_EXP_MAX_TIME"),

    // =====================================================
    // 天梯赛
    // =====================================================
    // /** 天梯赛.赛季教练技能.逗号分隔 */
    // RMatch_Skills("RMatch_Skills"),
    // /** 天梯赛.赛季教练技能,可使用数量 */
    // RMatch_Skill_Use_Num("RMatch_Skill_Use_Num"),
    /** 天梯赛.阵容最少球员数量 */
    RMatch_Lineup_Min_Player_Num("RMatch_Lineup_Min_Player_Num"),
    /** 天梯赛.每场比赛奖励掉落包(不管输赢) */
    RMatch_Drop("RMatch_Drop"),
    /** 天梯赛.每天前N场比赛有附加奖励(不管输赢) */
    RMatch_Drop_Addition_Num("RMatch_Drop_Addition_Num"),
    /** 天梯赛.每天前N场比赛的附加奖励掉落包(不管输赢) */
    RMatch_Addition_Drop("RMatch_Addition_Drop"),
    /** 天梯赛.每日可匹配时间段. “,”和”;”分隔 */
    RMatch_Time("RMatch_Time"),
    /** 天梯赛.获得赛季奖励需要的最少比赛场数 */
    RMatch_Season_Award_Min_Match("RMatch_Season_Award_Min_Match"),
    /** 天梯赛.匹配成功时要求的最大分差 */
    RMatch_MM_Gap("RMatch_MM_Gap"),
    /** 天梯赛.匹配时每轮时间(秒) */
    RMatch_MM_Round_Time("RMatch_MM_Round_Time"),
    /** 天梯赛.匹配时的最大轮数 */
    RMatch_MM_Max_Num("RMatch_MM_Max_Num"),
    /** 天梯赛.第一次加入天梯时的初始分数 */
    RMatch_Init_Rating("RMatch_Init_Rating"),
    /** 天梯赛. 比赛失败后扣分的最低评分 */
    RMatch_Min_Rating("RMatch_Min_Rating"),
    /** 天梯赛. 若玩家N天内不进行比赛时,扣掉分数(秒) */
    RMatch_Decre_Time("RMatch_Decre_Time"),
    /** 天梯赛. 若玩家N天内不进行比赛，每天扣掉的分数 */
    RMatch_Decre_Rating("RMatch_Decre_Rating"),
    /** 天梯赛. 若玩家N天内不进行比赛，扣掉分数后的最低分数 */
    RMatch_Decre_Min_Rating("RMatch_Decre_Min_Rating"),
    /** 天梯赛. 积分降低时, 层级是否降级. 0不降级,1降级 */
    RMatch_Degrade("RMatch_Degrade"),

    /** 战力计算. 战术, 进攻克制系数. 普通克制 */
    Cap_Tactic_Offense_Restrain_Rate("Cap_Tactic_Offense_Restrain_Rate"),
    /** 战力计算. 战术, 进攻克制系数. 我方进攻战术为跑轰战术，系数 */
    Cap_Tactic_Offense_Full_Rate("Cap_Tactic_Offense_Full_Rate"),
    /** 战力计算. 战术, 防守克制系数. 普通克制 */
    Cap_Tactic_Defense_Restrain_Rate("Cap_Tactic_Defense_Restrain_Rate"),
    /** 战力计算. 战术, 防守克制系数. 我方防守战术为全场紧逼或对方进攻战术是炮轰战术 */
    Cap_Tactic_Defense_Full_Rate("Cap_Tactic_Defense_Full_Rate"),
    /** 战力计算. 士气攻防系数 */
    Cap_Morale_Rate("Cap_Morale_Rate"),

    /** 比赛. 换人时犯规次数限制. 默认6次 */
    Battle_Uppos_Max_Pf("Battle_Uppos_Max_Pf"),
    /** 比赛. 计算父行为权重时的调整系数A */
    Battle_Act_Weight_A("Battle_Act_Weight_A"),
    /** 比赛. 计算父行为权重时的调整系数B */
    Battle_Act_Weight_B("Battle_Act_Weight_B"),

    /** 新星. String. 竞价开始时间. 格式: HH:mm 或 HH:mm:ss */
    NewStar_Bid_StartTime("NewStar_Bid_StartTime"),
    /** 新星. String. 竞价结束时间. 格式: HH:mm 或 HH:mm:ss */
    NewStar_Bid_EndTime("NewStar_Bid_EndTime"),

    /** 竞技场. 每日免费比赛次数 */
    Arena_Free_Match_Num("Arena_Free_Match_Num"),
    /** 竞技场. 挑战排名高于自己的玩家的冷却时间(秒) */
    Arena_Match_Greater_Rank_CD("Arena_Match_Greater_Rank_CD"),
    /** 竞技场. 挑战非目标对手时的最大排名差距. 默认30 */
    Arena_Non_Target_Rank_Range("Arena_Non_Target_Rank_Range"),
    // /** 竞技场. 比赛是否快速结束. 0:普通, 1:快速 */
    // Arena_Match_Quick("Arena_Match_Quick"),
    /** 竞技场. 奖励发放时间. 格式: HH:mm 或 HH:mm:ss 默认 21:00 */
    Arena_Award_Time("Arena_Award_Time"),
    /**
     * 竞技场. 在发放排名奖励时如果最后一次比赛时间离当前时间大于几分钟, 不发放排名奖励. 单位为秒. 默认 3天. 不活跃的用户不发放奖励
     */
    Arena_Rank_Award_Disable_Time("Arena_Rank_Award_Disable_Time"),
    /** 竞技场. 最高排名每1名次奖励的道具id */
    Arena_Max_Rank_Curr_Item("Arena_Max_Rank_Curr_Item"),
    /**竞技场，战力差超过填表值，直接判战力高的胜利*/
    Arena_Total_Cap_Sub("Arena_Total_Cap_Sub"),
    
    /** 联盟球馆赛.开始时间*/
    League_Arena_Start_Time("League_Arena_Start_Time"),
    /** 联盟球馆赛.结束时间*/
    League_Arena_End_Time("League_Arena_End_Time"),
    /** 联盟球馆赛.周几开启*/
    League_Arena_Open_Weekday("League_Arena_Open_Weekday"),
    /** 联盟球馆赛.发奖时间*/
    League_Send_Reward_time("League_Send_Reward_time"),
    /** 联盟球馆赛.周贡献排行榜显示条数*/
    League_Week_Score_Rank_Count("League_Week_Score_Rank_Count"),
    /** 联盟球馆赛.周贡献根据类型显示的条数*/
    League_WS_Rank_Type_Count("League_WS_Rank_Type_Count"),
    /** 联盟球馆赛.挑战CD*/
    League_Arena_Challenge_CD("League_Arena_Challenge_CD"),
    /** 联盟球馆赛.各类型参赛名额*/
    League_Arena_Quota("League_Arena_Quota"),
    /** 联盟球馆赛.比赛结束后，联盟选取占领训练馆球队的开始时间*/
    League_Choise_Team_Start_Time("League_Choise_Team_Start_Time"),
    /** 联盟训练馆.选球队持续时间(s秒)*/
    League_Choise_Team_Sustain_Time("League_Choise_Team_Sustain_Time"),
    /**联盟每日捐献进度礼包，领取时对个人贡献值的最低要求*/
    League_Active_Limit("League_Active_Limit"),
    /**联盟捐献，球券每日捐献可获得经验和荣誉的最大值，超过该值捐献球券不再获得经验和荣誉*/
    League_Donate_Max("League_Donate_Max"),
    /** 每日挑战次数*/
    Fast_cup_Count("Fast_cup_Count"),
    
    
    
    Main_Win_Exp("Main_Win_Exp"),
    Main_Lost_Exp("Main_Lost_Exp"),
    Main_Win_Tactics("Main_Win_Tactics"),
    Main_Lost_Tactics("Main_Lost_Tactics"),
    
    /** 战术次数*/
    Fast_cup_tactics_Count("Fast_cup_tactics_Count"),
  
    /** 新秀对抗赛.每日最大挑战次数 */
    Dual_Meet_Init_Count("Dual_Meet_Init_Count"),
    /** 新秀排位赛.每日最大挑战次数 */
    Dual_Meet_Rank_Init_Count("Dual_Meet_Rank_Init_Count"),
    /** 新秀排位赛.记录最大条数 */
    Starlet_Rank_Callange_Info_Count("Starlet_Rank_Callange_Info_Count"),
    /** 全明星赛挑战时间段 */
    All_Star_Time("All_Star_Time"),
    /** 全明星赛发奖时间 */
    All_Star_Send_Reward_Time("All_Star_Send_Reward_Time"),
    /** 全明星赛推荐球员攻防加成(填20,20%加成)*/
    All_Star_Rec_Player_Rate("All_Star_Rec_Player_Rate"),
    /** 全明星赛激励攻防加成(填20,20%加成)*/
    All_Star_Excitation_Rate("All_Star_Excitation_Rate"),
    /** 全明星赛赠送次数*/
    All_Star_Send_Challenge_Num("All_Star_Send_Challenge_Num"),
    /** 全明星赛击杀奖保底积分*/
    All_Star_Kill_Reward_Base_Score("All_Star_Kill_Reward_Base_Score"),
    /** 全明星推荐球员，每天从所填写球员品质区间中的每个品质随机一名球员(id对应的球员品质信息可在item表的drop分页查找）*/
    All_Star_Rec_Player("All_Star_Rec_Player"),
    
    //答题活动
    /**答题正确获得经验*/
    Answer_Win_Exp("Answer_Win_Exp"),
    /**答题错误获得经验*/
    Answer_Loss_Exp("Answer_Loss_Exp"),
    /**答题正确掉落Id*/
    Answer_Win_Drop("Answer_Win_Item"),
    /**答题错误掉落Id*/
    Answer_Loss_Drop("Answer_Loss_Item"),
    
    /** 球星荣耀. Int. 挑战次数初始次数*/
    HONOR_MATCH_NUM_INIT("HONOR_MATCH_NUM_INIT"),
    /** 球星荣耀. Int. 挑战次数默认可购买次数*/
    HONOR_MATCH_NUM_BUY("HONOR_MATCH_NUM_BUY"),


    Draft_Need_1,
    Recruit_Need_1,
    Recruit_Need_10,
    Top_Recruit_Need_1,
    Top_Recruit_Need_10,
    Normal_Refresh_Talent_Drop,
    Advanced_Refresh_Talent_Drop,
    Normal_Refresh_Talent_Need,
    Advanced_Refresh_Talent_Need,
    Normal_Lock_Talent_Need,
    Advanced_Lock_Talent_Need,
    Build_Refresh_Talent_Drop,
    Normal_Refresh_Talent_Recycling,
    Advanced_Refresh_Talent_Recycling,
    Normal_Lock_Talent_Recycling,
    Advanced_Lock_Talent_Recycling,
    Frist_Game,
    Player_Replace_Price_Prop,
    Training_1,
    Training_2,
    Training_3,
    Training_num,
    Draft_Frist_Drop,
    Trade_Frist_Drop,
    // 新手交易步骤购买球员的天赋
    Zero_Talent,
    // 战队赛相关
    Group_War_Start_Time,
    Group_War_End_Time,
    Group_War_DayAward_Pkmin,
    Group_War_Pair_Abs,
    Group_War_Pair_Time,
    Group_War_Pair_Count,
    Group_War_No_Ready_Tier,;
    private final String key;

    EConfigKey() {
        this(null);
    }

    EConfigKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static final Map<String, EConfigKey> cache = new HashMap<>();

    static {
        for (EConfigKey et : EConfigKey.values()) {
            cache.put(et.getKey(), et);
        }
    }

    public static EConfigKey convert(String key) {
        return cache.get(key);
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (EConfigKey e : EConfigKey.values()) {
            sb.append(e.key != null ? e.key : e.name()).append("\n");
        }
        System.out.println(sb.toString());
    }
}
