package com.ftkj.enums;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum ErrorCode {

    Success(0, "成功"),
    NULL(100, "后台没有找到错误对应错误码返回"),
    OK(101, "使用成功"),
    Fail(102, "操作失败"),
    Error(103, "系统错误"),
    OtherError(104, "非法操作"),
    ParamError(105, "参数错误"),
    RPC_EXCEPTION_LOCAL(106, "rpc 异常"),
    RPC_TIMEOUT(107, "rpc timeout"),
    RPC_EXCEPTION_REMOTE(108, "rpc 异常"),
    RPC_EXCEPTION_Interrupted(109, "rpc 中断"),
    Version_Major(110, "客户端版本过低"),
    Version_Minor(111, "客户端版本过低"),
    UserLock(120, "封号"),
    ChatLock(121, "禁言"),
    GmDisable(20100, "gm 命令未开启"),
    GmClient(20101, "gm 命令未开启"),
    Gm_Unknown(20102, "无法识别的gm命令"),
    /** 该时间段不开通 */
    Common_0(1100001),
    /** 球队名含有非法词汇 */
    Common_1(1100002),
    /** 非法字符 */
    Common_2(1100003),
    /** 已经是最高等级 */
    Common_3(1100004),
    /** 条件不满足 */
    Common_4(1100005),
    /** 经验值不足 */
    Common_5(1100006),
    /** 数量不足 */
    Common_6(1100007),
    /** 不在领奖时间内 */
    Common_7(1100008),
    /** 次数不足 */
    Common_8(1100009),
    /** 角色名称已存在 */
    Team_Exist(1000101),
    /** 找不到该球队名称 */
    Team_1(1000102),
    /** 球队名重复，请更换一个新的球队名 */
    Team_2(1000103),
    /** 对方不在线 */
    Team_Offline(1000104),
    /** 球队等级不足 */
    Team_Level(1000105),
    /** 球队战力不足 */
    Team_5(1000106),
    Team_Npc(1000107, "球队是npc"),
    Team_Null(1000108, "球队不存在"),
    /** 比赛不存在 */
    Battle_1(1000201),
    /** 正在匹配或比赛中，无法重复匹配 */
    Battle_2(1000202),
    /** 已在比赛中 */
    Battle_In(1000203, "已在比赛中"),
    /** 未解锁关卡 */
    Battle_4(1000204),
    /** 挑战次数不足 */
    Battle_5(1000205),
    /** 该时间段不可挑战 */
    Battle_6(1000206),
    /** 没找到关卡 */
    Battle_7(1000207),
    /** 比赛类型错误 */
    Battle_Type(1000208, "比赛类型错误"),
    Battle_Member(1000209, "球队没有参与此场比赛"),
    Battle_End(1000210, "比赛已经结束"),
    /** 对方正在比赛中 */
    Battle_Target_In(1000211, "对方正在比赛中"),
    Battle_Target_Self(1000212, "对手是自己"),
    Battle_Quick_Disable(1000213, "无法快速结束"),
    //====  pkUpdateTactics
    /** 比赛当前阶段不是PK阶段 */
    Battle_Stage_Pk(1030013, "比赛当前阶段不是PK阶段"),
    /** 比赛当前被封印了战术更换操作 */
    Battle_Up_Tactics_CD(1030014, "比赛当前被封印了战术更换操作"),
    /** 比赛中更换的战术类型不是对应的进攻或防守战术 */
    Battle_Up_Tactics_Type(1030015, "比赛中更换的战术类型不是对应的进攻或防守战术"),
    Battle_Up_Tactics_Null(1030016, "战术不存在"),
    Battle_Tactic_Null(1030017, "战术不存在"),

    Battle_Skill_Null(1030020, "技能不存在"),
    Battle_Skill_Used(1030021, "技能已经使用"),
    Battle_Skill_Timeout(1030022, "技能使用超时"),
    Battle_Skill_Type(1030023, "技能类型错误"),
    Battle_Skill_Power(1030024, "技能所需能量不足"),

    Battle_CoachSkill_Bean(1030030, "教练技能不存在"),
    Battle_CoachSkill_CD(1030031, "教练技能冷却中"),
    Battle_CoachSkill_Num(1030032, "教练技能已经达到最大次数"),

    Battle_UpPos_Cd(1030040, "更换位置cd还未冷却"),
    Battle_UpPos_Pf(1030041, "更换的球员犯规次数过多"),
    Battle_UpPos_Null(1030042, "更换的球员错误"),

    /** 场上存在相同球员，无法获得 */
    Player_0(1000301),
    /** 球员达到上限 */
    Player_1(1000302),
    /** 球员不存在 */
    Player_Null(1000303),
    /** X球员不支持该操作 */
    Player_3(1000304),
    /** 首发阵容不支持该操作 */
    Player_4(1000305),
    /** 阵容有重复球员 */
    Player_5(1000306),
    /** 球员仓库已达到上限 */
    Player_Bean_Null(1000308, "球员配置不存在"),
    Player_Storage_Full(1000307, "仓库位置已满"),
    Player_Prop_Bean_Null(1000312, "球员道具配置不存在"),
    /** 球员仓库已达到上限 */
    Player_7(1000309, "无效降薪"),
    /** 球员不在仓库中 */
    Player_10(1000310, "球员不在仓库中"),
    /** 球员已绑定 */
    Player_Bind(1000311, "球员已绑定"),
    /** 新秀球员等级不是N不能降薪 */
    Player_Startlet_Grade_Not_N(1000313, "新秀球员等级不是N不能降薪"),
    /** 新秀球员不能降薪 */
    Player_Cant_Not_Lower_Salary(1000314, "该球员不能降薪"),
    /** 新秀球员薪资已经降到最低 */
    Player_Price_Is_Lowest_Salary(1000315, "新秀球员薪资已经降到最低"),
    /** 待签的球员不存在 */
    Besign_1(1000401),
    /** 待签的球员已过期 */
    Besign_2(1000402),
    /** 道具数量不足 */
    Prop_0(1000501),
    /** 超出当天使用上限 */
    Prop_1(1000502),
    /** 该道具不能使用 */
    Prop_2(1000503),
    /** 找不到道具 */
    Prop_3(1000504),
    /** 超过最大购买上限 */
    Prop_4(1000505),
    /** 经费不足 */
    Money_0(1000551, "经费不足"),
    /** 球券不足 */
    Money_1(1000552, "球券不足"),
    /** 建设费不足 */
    Money_2(1000553, "建设费不足"),
    /** 绑定球券不足 */
    Money_3(1000554, "绑定球券不足"),
    /** 荣誉点不足 */
    Money_4(1000555, "荣誉点不足"),
    Money_5(1000556, "货币不足"),
    /** 经验不足 */
    Money_Exp(1000557, "经验不足"),
    /** 货币不足 */
    Money_Common(1000558, "货币不足"),
    Team_Num_Bean_Null(1000570, "球队次数配置错误"),
    Team_Number_Remain(1000571, "功能次数不足"),
    Team_Number_Max(1000572, "次数已经达到上限"),
    Team_Number_Type(1000573, "次数类型错误"),
    /** 装备类型不存在 */
    Equi_0(1000601),
    /** 无法操作，装备已满 */
    Equi_1(1000602),
    /** 该装备不能强化 */
    Equi_2(1000603),
    /** 经验不足，不能升级 */
    Equi_3(1000604),
    /** 强化失败 */
    Equi_4(1000605),
    /** 和其他装备相差大于两阶 */
    Equi_5(1000606),
    /** 已经发过好友请求，请稍后再试 */
    Friend_0(1000701),
    /** 已经是好友 */
    Friend_1(1000702),
    /** 对方已将你列入黑名单 */
    Friend_2(1000703),
    /** 超过好友最大数量上限 */
    Friend_3(1000704),
    /** 列表没有找到该球队 */
    Friend_4(1000705),
    Friend_5(1000706, "不是好友"),
    Friend_6(1000707, "对方设置了五分钟不在接受邀请"),
    /** 已经在选秀房间中，无法重复进入 */
    Draft_0(1000801),
    /** 该签已被抽取，无法重复抽取 */
    Draft_1(1000802),
    /** 选秀房人已满，无法进入 */
    Draft_2(1000803),
    /** 等级不足，无法报名 */
    League_0(1000901),
    /** 等级不足，无法创建联盟 */
    League_1(1000902),
    /** 联盟名字已经存在 */
    League_2(1000903),
    /** 已经申请过该联盟 */
    League_3(1000904),
    /** 加入联盟未满24小时 */
    League_4(1000905),
    /** 未加入联盟 */
    League_5(1000906),
    /** 职位不能执行该操作 */
    League_6(1000907),
    /** 球队已加入过联盟 */
    League_7(1000908),
    /** 联盟不存在 */
    League_8(1000909),
    /** 联盟已达到最大成员上限 */
    League_9(1000910),
    /** 并没有受到联盟邀请 */
    League_10(1000911),
    /** 超出捐献上限 */
    League_11(1000912),
    /** 加入联盟时间不够 */
    League_12(1000913),
    /** 只有盟主有任命权限 */
    League_13(1000914),
    /** 联盟名含有非法字符 */
    League_14(1000915),
    /** 联盟成员累计捐赠的贡献值没有达到指定数量则不能领取礼包奖励 */
    League_15(1000916, "联盟成员累计捐赠的贡献值没有达到指定数量则不能领取礼包奖励"),
    /** 联盟当日累计贡献值没有达到不能领取礼包奖励 */
    League_16(1000917, "联盟当日累计贡献值没有达到不能领取礼包奖励"),
    /** 联盟当日累计贡献礼包奖励以经领取过 */
    League_17(1000918, "联盟当日累计贡献礼包奖励以经领取过"),
    /** 联盟赛球馆赛.挑战CD冷却中，请稍后再挑战 */
    League_Arena_0(1000950, "联盟赛球馆赛.挑战CD冷却中，请稍后再挑战"),
    /** 联盟赛球馆赛.正在挑战中 */
    League_Arena_1(1000951, "联盟赛球馆赛.正在挑战中"),
    /** 联盟赛球馆赛.赛馆类型不匹配 */
    League_Arena_2(1000952, "联盟赛球馆赛.赛馆类型不匹配"),
    /** 联盟赛球馆赛.联盟赛期间,无法退出联盟 */
    League_Arena_3(1000953, "联盟赛球馆赛.联盟赛期间,无法退出联盟"),
    /** 联盟赛球馆赛.非法位置 */
    League_Arena_4(1000954, "联盟赛球馆赛.非法位置"),
    /** 联盟赛球馆赛.联盟赛未开始 */
    League_Arena_5(1000955, "联盟赛球馆赛.联盟赛未开始"),
    /** 联盟赛球馆赛.没有参赛资格 */
    League_Arena_6(1000956, "联盟赛球馆赛.没有参赛资格"),
    /** 联盟赛球馆赛.联盟赛期间 */
    League_Arena_7(1000957, "联盟赛球馆赛.无法获取上届历史排行"),
    /** 联盟赛球馆赛.无法(挑战)占领自己的位置 */
    League_Arena_8(1000958, "联盟赛球馆赛.无法重复自己直接的位置"),
    /** 联盟赛球馆赛.赛馆位无人占领, 无法挑战 */
    League_Arena_9(1000959, "联盟赛球馆赛.赛馆位无人占领, 无法挑战"),
    /** 联盟赛球馆赛.位置已被占领*/
    League_Arena_10(1000960, "联盟赛球馆赛.位置已有人，无法占领"),
    /** 联盟赛球馆赛.无法挑战同盟占领的位置*/
    League_Arena_11(1000961, "联盟赛球馆赛.无法挑战同盟占领的位置"),
    /** 联盟赛球馆赛.无法占领当前联盟训练馆位*/
    League_Arena_12(1000962, "联盟赛球馆赛.无法占领当前联盟训练馆位"),
    /** 联盟赛球馆赛.不在挑选球馆时间段内额*/
    League_Arena_13(1000963, "联盟赛球馆赛.不在挑选球馆时间段内额"),
    /** 联盟赛球馆赛.球馆已被别的联盟挑选*/
    League_Arena_14(1000964, "联盟赛球馆赛.球馆已被别的联盟挑选"),
    /** 联盟赛球馆赛.稍等一下，现在还未轮到你挑选球馆额*/
    League_Arena_15(1000965, "联盟赛球馆赛.稍等一下，现在还未轮到你挑选球馆额"),
    /** 每天最多可出售5个球员 */
    trade_0(1001011),
    /** 球员已下架 */
    trade_1(1001012),
    /** 每天只能在交易留言板留言一次 */
    trade_2(1001013, "每天只能在交易留言板留言一次 "),
    /** 聊天内容超过35个字符 */
    chat_1(1001101),
    /** 等级太低，无法发送聊天内容 */
    chat_2(1001102),
    /** 你说话太快了 */
    chat_3(1001103),
    /** 已是最高品质球星卡 */
    Card_1(1001201),
    /** 前置等级张数不够 */
    Card_2(1001202),
    /** 星级不够 */
    Card_3(1001203),
    /** 需要升阶道具不足 */
    Card_4(1001204),
    /** [color=#ff0000]提升失败，请再接再厉！[/color] */
    Card_5(1001205),
    /** 未收集过该球员球星卡 */
    Card_6(1001206),
    /** 没有该球员头像 */
    Logo_0(1001211),
    /** 该头像已装备 */
    Logo_1(1001212),
    /** 该头像不能转移 */
    Logo_2(1001213),
    /** 该比赛报名人数已满 */
    Match_1(1001221),
    /** 比赛没找到 */
    Match_2(1001222),
    /** 报名已结束 */
    Match_3(1001223),
    /** 不能重复报名 */
    Match_4(1001224),
    /** 任务还没有完成 */
    Task_1(1001231),
    /** 没有该级别的建筑信息 */
    Arena_1(1001241),
    /** 上阵守护球员不存在 */
    Arena_2(1001242),
    /** 球员位置与守护的建筑不匹配 */
    Arena_3(1001243),
    /** 建筑已经满级了 */
    Arena_4(1001244),
    /** 没有偷取次数 */
    Arena_5(1001245),
    /** 偷取的玩家不在可偷取列表中 */
    Arena_6(1001246),
    /** 该玩家不存在！ */
    Train_0(1001247),
    /** 今天已经签到! */
    Sign_1(1001301),
    /** 签到奖励配置异常! */
    Sign_2(1001302),
    /** 没有补签次数 */
    Sign_3(1001303),
    /** 当月补签次数已达到上限 */
    Sign_4(1001304),
    /** 活动未开始 */
    Active_1(1002001),
    /** 活动已结束 */
    Active_2(1002002),
    /** 没有找到奖励类型！ */
    Active_3(1002003),
    /** 当前时间不能兑换奖励！ */
    Active_4(1002004),
    /** 不满足兑换条件！ */
    Active_5(1002005),
    /** 排名未上榜！ */
    Active_6(1002006),
    /** 您已领取过该奖励！ */
    Active_7(1002007),
    /** 低品质球员不能替换高品质球员 */
    Arena_7(1002008),

    /** 出价低于当前最高价格 */
    PlayerBid_0(1002200),
    /** 跑马灯内容！ */
    RollTip_1(2000001),
    /** 太棒了，{}把{}强化到{}级，土豪求你把我带走吧 */
    RollTip_2(2000002),
    /** 坐稳了，{}开启了暴走状态，把{}强化到了{}级 */
    RollTip_3(2000003),
    /** {}把{}进阶到了{}，马上要与太阳肩并肩了 */
    RollTip_4(2000004),
    /** {}把{}强化到5，赶超乔丹的愿望不远了，加油吧骚年 */
    RollTip_5(2000005),
    /** 恭喜{}合成了{}球衣，凌晨三点穿着{}球衣在洛杉矶训练真是棒极了 */
    RollTip_6(2000006),
    /** 整个联盟都颤抖了，{}竟然合成了{}球衣，真是昊的存在….. */
    RollTip_7(2000007),

    Scount_BeSign_Num(1031207, "待签球员达到上限"),
    
    //=========================
    //     身价投资或者球员投资
    //=========================
    /**卖出手数不足*/
    Player_Investment_Total_Not_Enough(1033200, "卖出手数不足"),
    
    /** 球员竞价 */
    PrBid_Price(1034200, "出价不能比上一轮出价低"),
    PrBid_Priced(1034201, "每轮只能选一个，不可修改"),
    PrBid_Start(1034202, "还未开启，无法竞价"),
    PrBid_Price_Lower(1034203, "出价异常，比上一轮价格低"),
    //=========================
    //     主线赛程
    //=========================
    MMatch_TMM_Null(1036000, "无法获取赛程数据"),
    MMatch_MM_Null(1036001, "无法获取球队赛程数据"),
    MMatch_Lev_Type(1036002, "关卡类型错误"),
    MMatch_Num_Max(1036004, "挑战次数已满"),
    MMatch_Num_Max_1(1036005, "挑战次数已满."),
    MMatch_Num_Time_Stop(1036006, "休赛期"),
    MMatch_Num_Beyond_Max(1036007, "您当前挑战次数充足，请稍后领取！"),
    MMatch_Num_Rest_GetError(1036008, "不在领取时间内：喝咖啡时间12：00-14：00，18：00-20：00！"),

    MMatch_Div_Null(1036020, "赛区还未开启"),
    MMatch_Div_Bean(1036021, "赛区配置不存在"),
    MMatch_Div_Bean_Star(1036022, "赛区星级礼包配置不存在"),
    MMatch_Div_Star_Received(1036023, "赛区星级礼包已经领取"),
    MMatch_Div_Star_Num(1036024, "赛区星级礼包领取失败, 星数不足"),
    MMatch_Div_Pre_Null(1036025, "前置赛区未开启"),
    MMatch_Div_Pre_Star(1036026, "前置赛区星级不满足要求"),
    MMatch_Div_Pre_Regular(1036027, "前置赛区常规赛没有全部开启"),

    MMatch_Lev_Null(1036030, "关卡不存在"),
    MMatch_Lev_Bean(1036031, "关卡配置不存在"),
    MMatch_Lev_Pre_Null(1036032, "关卡的前置关卡未开启"),
    MMatch_Lev_Pre_Star(1036033, "关卡的前置关卡星级不满足要求"),

    MMatch_Match_Num(1036040, "挑战次数不足"),
    MMatch_Match_Target(1036042, "没有找到合适的对手"),

    MMatch_Quick_Star(1036050, "无法扫荡, 没有达到需要的星级"),
    MMatch_Quick_Championship(1036051, "无法扫荡, 锦标赛进行中"),
    MMatch_Championship_Lev_Type(1036060, "当前不是锦标赛关卡"),

    //=========================
    //     天梯赛
    //=========================
    RMatch_Season_Bean_Null(1037001, "无法比赛, 赛季休赛期"),
    RMatch_Match_Time(1037002, "无法比赛, 休赛期"),
    RMatch_Lineup_Pr_Num(1037003, "当前阵容球员数量不满足要求"),
    RMatch_Joined(1037004, "已经开始匹配"),

    RMatch_Daily_Award(1037005, "每日奖励已经领取"),
    RMatch_Tier_Bean(1037006, "段位配置不存在"),
    RMatch_Medal_Bean(1037007, "段位配置错误"),
    RMatch_FirstAward_Bean(1037008, "首次奖励配置错误"),
    RMatch_Team(1037009, "没有赛季信息"),
    RMatch_FirstAward(1037010, "首次奖励已经领取"),
    RMatch_Season_Curr_Null(1037011, "没有当前赛季信息"),
    RMatch_Daily_Reward_Match_Count(1037012, "领取每日奖励必须先进行一场比赛"),
    //=========================
    //     天梯赛
    //=========================
    AllStar_Bean_Null(1038001, "全明星配置错误"),
    AllStar_Npc_Null(1038002, "全明星npc配置错误"),
    AllStar_Null(1038003, "全明星信息错误"),
    AllStar_Match_Time(1038004, "全明星不在比赛时间内"),
    AllStar_Npc_Max_Lev(1038005, "最高难度已经被打败"),
    AllStar_Challenge_Num(1038006, "全明星挑战次数不足"),
    AllStar_Kill_Reward_Level(1038007, "该等级全明星球队未击杀，不能领取奖励"),
    AllStar_Kill_Reward_Has_Get(1038008, "该等级全明星球队击杀奖励已领取过"),
    AllStar_Kill_Reward_Base_Score(1038009, "没到达保底积分，不能领取奖励"),
    AllStar_Score_Reward_Has_Get(1038010, "该个人积分奖励已领取"),
    AllStar_Have_No_Score_Reward(1038011, "没有个人积分奖励可领取"),
    AllStar_Can_Not_Buy_Time_Limit(1038012, "今日挑战已结束，不能购买"),
    AllStar_Can_Not_Jili_Time_Limit(1038013, "今日挑战已结束，不能激励"),
    //=========================
    //     联盟战队赛
    //=========================
    League_Group_Name_Repeat(1038101, "战队名称重复"),
    League_Group_Max(1038102, "超过战队最大房间数"),
    League_Group_Find_Null(1038103, "没找到该战队"),
    League_Group_Not_Limit(1038104, "没有操作权限"),
    League_Group_Not_Apply(1038105, "没有找到该申请"),
    League_Group_Max_Team(1038106, "战队成员数已满"),
    League_Group_In_Team(1038107, "成员已在战队中"),
    League_Group_Not_Team(1038108, "没有找到该成员"),
    League_Group_Battle(1038109, "正在比赛中"),
    League_Group_Battle_Start(1038110, "该时间段不能开始比赛"),
    League_Group_Battle_Ready(1038111, "还有队员没有准备"),
    League_Group_Joined(1038112, "已经加入别的战队"),
    //=========================
    //     个人排名竞技场
    //=========================
    Arena_Disable(1039000, "等级不足"),
    Arena_Target_Null(1039001, "对手不存在"),
    //    Arena_Opponent_Null(1039002, "对手不存在"),
    Arena_Target_Chnage(1039003, "对手发生变化"),
    Arena_Target_Rank_Range(1039004, "和对手排名差距过大"),
    Arena_New_Target_Null0(1039005, "新对手不存在"),
    Arena_New_Target_Null(1039006, "新对手不存在"),
    Arena_Greater_Rank_Cd(1039007, "挑战排名高于自己的玩家时冷却时间未到"),
    Arena_Match_Num(1039008, "比赛次数不足"),
    Arena_Refresh_Time(1039009, "刷新对手频率太快"),

    //=========================
    //     训练馆
    //=========================
    Train_Not_Exist(1040001, "训练馆不存在"),
    Train_Grob_Count_Not_Enough(1040002, "训练馆抢夺次数不足"),
    Train_Resour_Not_Grob(1040003, "训练馆资源无法再被抢夺"),
    Train_Player_Training(1040004, "球员已在训练中"),
    Train_Grob_CD(1040005, "离上次抢夺时间不足3分钟"),
    Train_Player_Exist(1040006, "训练位已有球员"),
    Train_Reward_Not_Can_Get(1040007, "训练位奖励不可领"),
    Train_Not_Have_Player(1040008, "训练位没有球员,不可抢夺"),
    Train_Refresh_list_CD(1040009, "离上次抢夺列表刷新时间不足3秒钟"),
    Train_Player_Not_Have_Cap(1040010, "训练位球员取最大攻防数据为nil"),
    Train_Storage_Player_Is_Null(1040011, "玩家仓库球员为nil"),
    
    //=========================
    //     新秀赛
    //=========================
    Starlet_Not_Have(1050000, "没有新秀(N)球员"),
    Starlet_Not_Start_Match(1050001, "新秀比赛未开启"),
    Starlet_In_Battle(1050002, "新秀对抗赛.正在挑战中"),
    Starlet_Count_Not_Enough(1050003, "新秀对抗赛.挑战次数不足"),
    Starlet_Player_Is_Null(1050004, "新秀对抗赛.新秀阵容玩家为空"),
    Starlet_Is_Null_Have_Rank(1050005, "新秀排位赛.新秀没有排位数据"),
    Starlet_Rank_Data_Is_Null(1050006, "新秀排位赛.排位数据为空"),
    Starlet_Rank_low(1050007, "新秀排位赛.不能挑戰比自己低的排名"),
    Starlet_Not_In_Battle(1050008, "新秀对抗赛.未在新秀对抗赛挑战中"),
    Starlet_Not_Have_Cap(1050009, "新秀对抗赛.没有战力,无法参加排位赛"),
    Starlet_Rank_Count_Not_Enough(1050010, "新秀排位赛.挑战次数不足"),
    Battle_update_Tactic_num_limit(1050011, "本场比赛更换战术次数已用完"),
    Team_daily_times_limit(1050012, "今日报名次数已用完"),
    
    
    //=========================
    //     极限挑战
    //=========================
    limit_challenge_num(1050050, "挑战次数不足"),
    limit_challenge_time(1050051, "不在挑战时间内"),
    limit_challenge_buy_num(1050052, "当前还有挑战次数，无需购买"),
    limit_challenge_3star(1050053, "满星不可再挑战"),
    
    //=========================
    //     球星荣耀
    //=========================
    honor_no_div(1050060, "章节不存在"),
    honor_no_lv(1050061, "关卡不存在"),
    honor_can_not_challenge_1(1050062, "上一关没通关，不能挑战本关卡"),
    honor_can_not_challenge_2(1050063, "满星不能挑战"),
    honor_can_not_open_box(1050064, "星级不够不能领取宝箱奖励"),
    honor_can_not_open_box1(1050065, "宝箱已领取"),
    honor_can_not_saodang(1050066, "不可扫荡"),
    honor_team_level(1050067, "等级不足"),
    honor_team_buy_vip(1050068, "vip等级不足"),
    honor_team_buy_limit(1050069, "购买次数已用完"),
    
    //=========================
    //     开服七天乐
    //=========================
    happy7day_no_task(1050100, "任务不存在"),
    happy7day_can_not_get_reward1(1050101, "任务未完成"),
    happy7day_can_not_get_reward2(1050102, "奖励已领取"),
    happy7day_can_not_get_box1(1050103, "礼包不存在"),
    happy7day_can_not_get_box2(1050104, "完成任务个数不够，礼包未能领取"),
    happy7day_can_not_get_box3(1050105, "宝箱已领取"),
    ;
    
    /** 正常 */
    public static final int Ret_Success = 0;
    public int code;
    private final String tip;
    private static Map<Integer, ErrorCode> codeMap;

    ErrorCode(int code) {
        this(code, "");
    }

    ErrorCode(int code, String tip) {
        this.code = code;
        this.tip = tip;
    }

    public int getCode() {
        return code;
    }

    public String getTip() {
        return tip;
    }

    public boolean isSuccess() {
        return code == Ret_Success;
    }

    public boolean isError() {
        return code != Ret_Success;
    }

    @Override
    public String toString() {
        return "{" + "\"code\":" + code
            + ", \"tip\":\"" + ((tip == null || tip.isEmpty()) ? name() : tip) + "\""
            + '}';
    }

    public static void main(String[] args) {
        for (ErrorCode e : values()) {
            System.out.printf("%s\t%s\t%s\n", e.code, e.name(), e.getTip());
        }
        //        printPythonStyle();
    }

    static {
        Map<Integer, ErrorCode> codeMap = new HashMap<>();
        for (ErrorCode t : values()) {
            if (codeMap.containsKey(t.getCode())) {
                throw new Error("duplicate ret code :" + t.getCode());
            }
            codeMap.put(t.getCode(), t);
        }
        
        ErrorCode.codeMap = codeMap;
    }

    private static void printPythonStyle() {
        StringBuilder sb = new StringBuilder();
        Set<String> set = new HashSet<>();
        Set<Integer> ids = new HashSet<>();
        //        sb.append("{\n");
        for (ErrorCode e : values()) {
            String msg = e.tip;
            if (msg == null || msg.isEmpty()) {
                continue;
            }
            if (ids.contains(e.getCode())) {
                throw new IllegalStateException("重复的retcode : " + e.getCode());
            }
            ids.add(e.getCode());

            if (set.contains(msg)) {
                msg = e.tip + ":" + e.code;
            }
            sb.append(String.format("%1$-5s\t1\t\t%2$s\n", e.code, msg));
            set.add(msg);
        }

        //        sb.append("}");
        System.out.println(sb.toString());
    }

	public static Map<Integer, ErrorCode> getCodeMap() {
		return codeMap;
	}
    
}
