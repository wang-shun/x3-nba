package com.ftkj.console;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.db.domain.bean.ConfigBeanVO;
import com.ftkj.enums.EConfigKey;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

/**
 * @author tim.huang 2017年3月3日 配置控制台
 */
public class ConfigConsole extends AbstractConsole implements ValidateBean {
    private static final Logger log = LoggerFactory.getLogger(ConfigConsole.class);
    private static Map<EConfigKey, ConfigConsole.Config> configMap = Maps.newHashMap(); // gm节点不调用init()，所以要先实例化
    private static GlobalBean globalBean = new GlobalBean(Collections.emptyMap());

    public static GlobalBean global() {
        return globalBean;
    }

    public static GlobalBean getGlobal() {
        return globalBean;
    }

    public static String getVal(EConfigKey key) {
        ConfigConsole.Config c = configMap.get(key);
        return c == null ? "" : c.getVal();
    }

    public static int getIntVal(EConfigKey key) {
        ConfigConsole.Config c = configMap.get(key);
        return c == null ? 0 : c.getIntVal();
    }

    public static int getIntVal(EConfigKey key, int defaultVal) {
        ConfigConsole.Config c = configMap.get(key);
        return c == null ? defaultVal : c.getIntVal();
    }

    public static void init(List<ConfigBeanVO> list) {
        Map<EConfigKey, ConfigConsole.Config> beans = Maps.newHashMap();
        Map<String, ConfigBeanVO> vos = list.stream().collect(Collectors.toMap(ConfigBeanVO::getKey, (k) -> k));
        // 通用
        for (EConfigKey eck : EConfigKey.values()) {
            if (eck.getKey() != null && !eck.getKey().isEmpty()) {
                continue;
            }
            ConfigBeanVO cbv = vos.get(eck.name());
            if (cbv == null) {
                continue;
            }
            beans.put(eck, new Config(cbv.getVal()));
        }
        // 特殊
        GlobalBean gb = new GlobalBean(vos);
        ConfigConsole.configMap = beans;
        ConfigConsole.globalBean = gb;

        log.info("global config major version {}", gb.versionMajor);
    }

    @Override
    public void validate() {
        GlobalBean g = ConfigConsole.globalBean;
        int mmStopTime = g.mMatchCsStopTime;
        int mmResetTime = g.mMatchCsResetTime;
        if (mmResetTime < mmStopTime) {
            throw exception("主线赛程. 不允许开始新赛前多人训练赛的时间 %s 要小于更新至未开始状态时间 %s", mmStopTime, mmResetTime);
        }
        if (Float.compare(g.overSalary, 0) <= 0) {
            throw exception("全局配置. 超帽削减比例 %s <= 0", g.overSalary);
        }
    }

    /**
     * SecondOfDay 毫秒数
     *
     * @param str
     * @return
     */
    public static int parseTimeToMillis(String str) {
        LocalTime lt = LocalTime.parse(str, DateTimeFormatter.ISO_LOCAL_TIME);
        return lt.toSecondOfDay() * 1000;
    }

    public static final class GlobalBean {

        /** 大版本. “;”分隔. 大版本不兼容. 客户端写死 */
        public final ImmutableSet<String> versionMajor;
        /** 小版本. 客户端必须>=服务器 */
        public final int versionMinor;
        /** 好友.切磋请求过期时间(秒) */
        public final int friendCompareNotesDealTime;
        /** 好友.拒绝切磋请求时间(秒) */
        public final int friendCompareNotesRefreshTime;
        /** 球队.改名卡道具ID */
        public final int teamChangeNamePropId;
        /** 球队.加帽卷道具ID */
        public final int teamAddPriceItem;
        /** 球队.降低工资道具ID */
        public final int teamLowerPriceItem;
        /** 球队.降低工资消耗道具数量 */
        public final int teamLowerPriceItemNum;
        /** 聊天.单个目标玩家留言最大上限（条） */
        public final int chatOfflineMsgLimit;
        /** 聊天.信息最大长度上限 */
        public final int chatMsgWroldCountLimit;
        /** 球队.更换主球星道具ID */
        public final int teamChangeXplayerPropId;
        /** 联盟.修改联盟公告消耗球卷 */
        public final int editLeagueNoticePrice;
        /** 超帽削减比例 */
        public final float overSalary;
        /** 球员招募. Int. 待签球员最大数量 */
        public final int scoutBeSignMaxNum;
        /** 主线赛程. Int. 默认开启的关卡配置id */
        public final int mMatchDefaultOpenLev;
        /** 主线赛程. Int. 最大星级 */
        public final int mMatchMaxStar;
        /** 主线赛程. Int. 扫荡要求最小星级 */
        public final int mMatchQuickMinStar;
        /** 主线赛程. Int. 挑战次数初始次数 */
        public final int mMatchNumInit;
        /** 主线赛程. Int. 挑战次数最大次数 */
        public final int mMatchNumMax;
        /** 主线赛程. Int. 挑战次数恢复cd(秒) */
        public final int mMatchNumCd;
        /** 主线赛程. Int. 购买挑战次数花费的球卷 */
        // public final int mMatchNumMoney;
        /** 主线赛程. Int. 装备经验道具id */
        public final int mMatchEquipExpItem;
        /** 主线赛程. Int. 装备经验结算时的最大秒数, 防止出现巨量经验bug(默认一周) */
        public final int mMatchEquipExpMaxTime;
        /** 主线赛程. String. 锦标赛. 不允许开始新赛前多人训练赛的时间. 格式: HH:mm 或 HH:mm:ss */
        public final int mMatchCsStopTime;
        /** 主线赛程. String. 锦标赛. 强制结算奖励时间. 格式: HH:mm 或 HH:mm:ss */
        public final int mMatchCsForceEndTime;
        /** 主线赛程. String. 锦标赛. 更新至未开始状态时间. 格式: HH:mm 或 HH:mm:ss */
        public final int mMatchCsResetTime;

        // =====================================================
        // 天梯赛
        // =====================================================
        // /** 天梯赛.赛季教练技能.逗号分隔 */
        // public final ImmutableSet<Integer> rMatchSkills;
        // /** 天梯赛.赛季教练技能,可使用数量 */
        // public final int rMatchSkillUseNum;
        /** 天梯赛. 阵容最少球员数量 */
        public final int rMatchLineupMinPlayerNum;
        /** 天梯赛. 每场比赛奖励掉落包(不管输赢) */
        public final int rMatchDrop;
        /** 天梯赛. 每天前N场比赛有附加奖励(不管输赢) */
        public final int rMatchDropAdditionNum;
        /** 天梯赛. 每天前N场比赛的附加奖励掉落包(不管输赢) */
        public final int rMatchAdditionDrop;
        /** 天梯赛. 每日可匹配时间段. “,”和”;”分隔 */
        public final ImmutableList<TupleTime> rMatchTime;
        /** 天梯赛. 获得赛季奖励需要的最少比赛场数 */
        public final int rMatchSeasonAwardMinMatch;
        /** 天梯赛. 匹配成功时要求的最大分差 */
        public final int rMatchMmGap;
        /** 天梯赛. 匹配时每轮时间(秒) */
        public final int rMatchMmRoundTime;
        /** 天梯赛. 匹配时的最大轮数 */
        public final int rMatchMmMaxNum;
        /** 天梯赛. 第一次加入天梯时的初始分数 */
        public final int rMatchInitRating;
        /** 天梯赛. 比赛失败后扣分的最低评分 */
        public final int rMatchMinRating;
        /** 天梯赛. 若玩家N天内不进行比赛时,扣掉分数(秒) */
        public final int rMatchDecreTime;
        /** 天梯赛. 若玩家N天内不进行比赛，则直接扣掉的分数 */
        public final int rMatchDecreRating;
        /** 天梯赛. 若玩家N天内不进行比赛，扣掉分数后的最低分数 */
        public final int rMatchDecreMinRating;
        /** 天梯赛. 积分降低时,段位是否降级. 0不降级,1降级 */
        public final boolean rMatchDegradeTier;

        /** 战力计算. 战术, 进攻克制系数. 普通克制 */
        public final float capTacticOffenseRestrainRate;
        /** 战力计算. 战术, 进攻克制系数. 我方进攻战术为跑轰战术，系数 */
        public final float capTacticOffenseFullRate;
        /** 战力计算. 战术, 防守克制系数. 普通克制 */
        public final float capTacticDefenseRestrainRate;
        /** 战力计算. 战术, 防守克制系数. 我方防守战术为全场紧逼或对方进攻战术是炮轰战术 */
        public final float capTacticDefenseFullRate;
        /** 战力计算. 士气攻防系数 */
        public final float capMoraleRate;

        /** 比赛. 换人时犯规次数限制. 默认6次 */
        public final int battleUpPosMaxPf;
        /** 比赛. 计算父行为权重时的调整系数A */
        public final float battleActWeightA;
        /** 比赛. 计算父行为权重时的调整系数B */
        public final float battleActWeightB;

        /** 新星. String. 竞价开始时间. 格式: HH:mm 或 HH:mm:ss */
        public final int newStartBidStartTime;
        /** 新星. String. 竞价结束时间. 格式: HH:mm 或 HH:mm:ss */
        public final int newStartBidEndTime;

        /** 竞技场. 每日免费比赛次数 */
        public final int arenaFreeMatchNum;
        /** 竞技场. 挑战排名高于自己的玩家的冷却时间(毫秒) */
        public final int arenaMatchGreaterRankCd;
        /** 竞技场. 挑战非目标对手时的最大排名差距. 默认30 */
        public final int arenaNonTargetRankRange;
        // /** 竞技场. 比赛是否快速结束 */
        // public final boolean arenaMatchQuick;
        /** 竞技场. 奖励发放时间. 格式: HH:mm 或 HH:mm:ss 默认 21:00 */
        public final int arenaAwardTime;
        /**
         * 竞技场. 在发放排名奖励时如果最后一次比赛时间离当前时间大于几分钟, 不发放排名奖励. 单位为毫秒. 默认 3天. 不活跃的用户不发放奖励
         */
        public final int arenaRankAwardDisableTime;
        /** 竞技场. 最高排名每1名次奖励的道具id */
        public final int arenaMaxRankCurrItemId;
        /**竞技场，战力差超过填表值，直接判战力高的胜利*/
        public final int arenaTotalCapSub;
        /** 交易.留言板最大条数上限 */
        public final int tradeMaxLevelMessage;

        /** 训练馆.训练馆初始NpcId */
        public final String trainInitNpc;
        /** 训练馆.最大抢夺次数 */
        public final int trainGrabCountMax;
        /** 训练馆.训练馆最大可抢夺次数 */
        public final int trainResourceSurplus;
        /** 训练馆.掠夺对方总产出资源的（百分比） */
        public final int trainGrabResourceSurplus;
        /** 训练馆.训练的玩家中随机的攻防范围 */
        public final int trainPlayerCapRange;
        /** 训练馆.抢夺次数刷新时间间隔(分) */
        public final int trainGrabCountRefresh;
        /** 训练馆.个人训练位个数 */
        public final int trainTeamCount;
        /** 训练馆.联盟训练位个数 */
        public final int trainLeagueCount;
        /** 训练馆.抢夺CD时间间隔(分) */
        public final int trainGrabCDRefresh;
        /** 训练馆.抢夺列表刷新时间间隔(分) */
        public final int trainRefreshListCD;
        /** 训练馆.仓库球员为空，初始列表的攻防 */
        public final int trainStorageNullInitCap;

        /** 联盟球馆赛.开始时间 */
        public final int leagueArenaStartTime;
        /** 联盟球馆赛.结束时间 */
        public final int leagueArenaEndTime;
        /** 联盟球馆赛.周几开启 */
        public final int leagueArenaOpenWeekday;
        /** 联盟球馆赛.发奖时间 */
        public final int leagueSendRewardtime;
        /** 联盟球馆赛.周贡献排行榜显示条数 */
        public final int leagueWeekScoreRankCount;
        /** 联盟球馆赛.周贡献根据类型显示的条数 */
        public final int leagueWSRankTypeCount;
        /** 联盟球馆赛.挑战CD */
        public final int leagueArenaChallengeCD;
        /** 联盟球馆赛.各类型参赛名额 */
        public final String leagueArenaQuota;
        /** 联盟球馆赛.比赛结束后，联盟选取占领训练馆球队的开始时间 */
        public final int leagueChoiseTeamStartTime;
        /** 联盟训练馆.选球队持续时间(s秒)*/
        public final int leagueChoiseTeamSustainTime;
        /**联盟每日捐献进度礼包，领取时对个人贡献值的最低要求*/
        public final int leagueActiveLimit;
        /**联盟捐献，球券每日捐献可获得经验和荣誉的最大值，超过该值捐献球券不再获得经验和荣誉*/
        public final int leagueDonateMax;
        
        /** 主线赛程胜利战斗经验奖励：12*lv*/
        public final float Main_Win_Exp;
        /** 主线赛程失败战斗经验奖励：8*lv*/
        public final float Main_Lost_Exp;
        /** 主线赛程胜利战术点奖励：1*lv*/
        public final float Main_Win_Tactics;
        /** 主线赛程失败战术点奖励：int（0.8*lv）*/
        public final float Main_Lost_Tactics;
        /** 开服7天乐初级礼包、中级礼包，高级礼包奖励*/
        public final String happy7dayBox;
        /** 每日挑战次数*/
        public final int fast_cup_Count;
        /** 每日球星荣耀. Int. 挑战次数初始次数*/
        public final int honorChallengeCount;
        /** 球星荣耀. Int. 挑战次数默认可购买次数*/
        public final int honorbuyCount;
        /** 战术次数*/
        public final int fast_cup_tactics_Count;
        /** 新秀对抗赛.每日最大挑战次数 */
        public final int dualMeetInitCount;
        /** 新秀排位赛.每日最大挑战次数 */
        public final int dualMeetRankInitCount;
        /**  新秀排位赛.记录最大条数 */
        public final int starletRankCallangeInfoCount;
        /**全明星赛挑战时间段*/
        public final String allStarTime;
        /**全明星赛发奖时间*/
        public final String allStarSendRewardTime;
        /**全明星赛推荐球员攻防加成(填20,20%加成)*/
        public final int allStarRecPlayerRate;
        /** 全明星赛激励攻防加成(填20,20%加成)*/
        public final int allStarExcitationRate;
        /** 全明星赛赠送次数*/
        public final String allStarSendChallengeNum;
        /** 全明星赛击杀奖保底积分*/
        public final int allStarKillRewardBaseScore;
        /** 全明星推荐球员，每天从所填写球员品质区间中的每个品质随机一名球员(id对应的球员品质信息可在item表的drop分页查找）*/
        public final String allStarRecPlayer;
        //答题活动
        /**答题正确获得经验*/
        public final int answerWinExp;
        /**答题错误获得经验*/
        public final int answerLossExp;
        /**答题正确掉落Id*/
        public final List<Integer> answerWinDropList;
        /**答题错误掉落Id*/
        public final List<Integer> answerLossDropList;
        
        public GlobalBean(Map<String, ConfigBeanVO> vos) {
            versionMajor = ImmutableSet
                .copyOf(StringUtil.toStringArray(getStr(vos, EConfigKey.Version_Major), StringUtil.SEMICOLON));
            versionMinor = getInt(vos, EConfigKey.Version_Minor);

            teamLowerPriceItemNum = getInt(vos, EConfigKey.Team_Lower_Price_Item_Num);
            teamLowerPriceItem = getInt(vos, EConfigKey.Team_Lower_Price_Item);
            teamAddPriceItem = getInt(vos, EConfigKey.Team_Add_Price_Item);
            teamChangeNamePropId = getInt(vos, EConfigKey.TeamChangeNamePropId);
            teamChangeXplayerPropId = getInt(vos, EConfigKey.TeamChangeXplayerPropId);
            friendCompareNotesDealTime = getInt(vos, EConfigKey.FriendCompareNotesDealTime);
            friendCompareNotesRefreshTime = getInt(vos, EConfigKey.FriendCompareNotesRefreshTime);
            chatOfflineMsgLimit = getInt(vos, EConfigKey.ChatOfflineMsgLimit);
            chatMsgWroldCountLimit = getInt(vos, EConfigKey.Chat_Msg_Wrold_Count_Limit);
            editLeagueNoticePrice = getInt(vos, EConfigKey.EditLeagueNoticePrice);

            overSalary = getInt(vos, EConfigKey.ExceedPriceCap) / 100.0f;

            scoutBeSignMaxNum = getInt(vos, EConfigKey.Scout_BeSign_Max_Num, 300);
            mMatchDefaultOpenLev = getInt(vos, EConfigKey.MMatch_DEFAULT_OPEN_LEV);
            mMatchMaxStar = getInt(vos, EConfigKey.MMatch_MAX_STAR, 3);
            mMatchQuickMinStar = getInt(vos, EConfigKey.MMatch_QUICK_MIN_STAR, 2);
            mMatchNumInit = getInt(vos, EConfigKey.MMatch_NUM_INIT, 8);
            mMatchNumMax = getInt(vos, EConfigKey.MMatch_NUM_MAX, 8);
            mMatchNumCd = getInt(vos, EConfigKey.MMatch_NUM_CD, DateTimeUtil.HOUR / 1000) * 1000;
            // mMatchNumMoney = getInt(vos, EConfigKey.MMatch_NUM_MONEY, 40);
            mMatchEquipExpItem = getInt(vos, EConfigKey.MMatch_EQUIP_EXP_ITEM);
            mMatchEquipExpMaxTime = getInt(vos, EConfigKey.MMatch_EQUIP_EXP_MAX_TIME, DateTimeUtil.WEEK / 1000);
            mMatchCsStopTime = parseTimeToMillis(getStr(vos, EConfigKey.MMatch_CS_STOP_TIME, "23:30"));
            mMatchCsForceEndTime = parseTimeToMillis(getStr(vos, EConfigKey.MMatch_CS_FORCE_END_TIME, "23:50"));
            mMatchCsResetTime = parseTimeToMillis(getStr(vos, EConfigKey.MMatch_CS_RESET_TIME, "23:59:59"));

            // rMatchSkills = ImmutableSet.of();
            // rMatchSkillUseNum = 0;
            // rMatchSkills = ImmutableSet.copyOf(StringUtil.toIntegerArray(getStr(vos,
            // EConfigKey.RMatch_Skills)));
            // rMatchSkillUseNum = getInt(vos, EConfigKey.RMatch_Skill_Use_Num, 1);
            rMatchLineupMinPlayerNum = getInt(vos, EConfigKey.RMatch_Lineup_Min_Player_Num, 9);
            rMatchDrop = getInt(vos, EConfigKey.RMatch_Drop);
            rMatchDropAdditionNum = getInt(vos, EConfigKey.RMatch_Drop_Addition_Num, 5);
            rMatchAdditionDrop = getInt(vos, EConfigKey.RMatch_Addition_Drop);
            rMatchTime = parseTupleTime(getStr(vos, EConfigKey.RMatch_Time, "11:00:00,14:00;19:00,22:00;"));
            rMatchSeasonAwardMinMatch = getInt(vos, EConfigKey.RMatch_Season_Award_Min_Match, 20);
            rMatchMmGap = getInt(vos, EConfigKey.RMatch_MM_Gap, 100);
            rMatchMmRoundTime = getInt(vos, EConfigKey.RMatch_MM_Round_Time, 20);
            rMatchMmMaxNum = getInt(vos, EConfigKey.RMatch_MM_Max_Num, 4);
            rMatchInitRating = getInt(vos, EConfigKey.RMatch_Init_Rating, 1200);
            rMatchMinRating = getInt(vos, EConfigKey.RMatch_Min_Rating, 800);
            rMatchDecreTime = getInt(vos, EConfigKey.RMatch_Decre_Time, 604800) * 1000;
            rMatchDecreRating = getInt(vos, EConfigKey.RMatch_Decre_Rating, 20);
            rMatchDecreMinRating = getInt(vos, EConfigKey.RMatch_Decre_Min_Rating, 1400);
            rMatchDegradeTier = getBool(vos, EConfigKey.RMatch_Degrade);

            capTacticOffenseRestrainRate = getFloat(vos, EConfigKey.Cap_Tactic_Offense_Restrain_Rate, 0.2f);
            capTacticOffenseFullRate = getFloat(vos, EConfigKey.Cap_Tactic_Offense_Full_Rate, 1);
            capTacticDefenseRestrainRate = getFloat(vos, EConfigKey.Cap_Tactic_Defense_Restrain_Rate, 4.6f);
            capTacticDefenseFullRate = getFloat(vos, EConfigKey.Cap_Tactic_Defense_Full_Rate, 1);
            capMoraleRate = getFloat(vos, EConfigKey.Cap_Morale_Rate, 0.004f);

            battleUpPosMaxPf = getInt(vos, EConfigKey.Battle_Uppos_Max_Pf, 6);
            battleActWeightA = getInt(vos, EConfigKey.Battle_Act_Weight_A, 1);
            battleActWeightB = getInt(vos, EConfigKey.Battle_Act_Weight_B, 1);

            newStartBidStartTime = parseTimeToMillis(getStr(vos, EConfigKey.NewStar_Bid_StartTime, "12:30"));
            newStartBidEndTime = parseTimeToMillis(getStr(vos, EConfigKey.NewStar_Bid_EndTime, "15:00"));

            arenaFreeMatchNum = getInt(vos, EConfigKey.Arena_Free_Match_Num, 5);
            arenaMatchGreaterRankCd = getInt(vos, EConfigKey.Arena_Match_Greater_Rank_CD, 180) * 1000;
            arenaNonTargetRankRange = getInt(vos, EConfigKey.Arena_Non_Target_Rank_Range, 30);
            // arenaMatchQuick = getBool(vos, EConfigKey.Arena_Match_Quick);
            arenaAwardTime = parseTimeToMillis(getStr(vos, EConfigKey.Arena_Award_Time, "21:00"));
            arenaRankAwardDisableTime = getInt(vos, EConfigKey.Arena_Rank_Award_Disable_Time,
                DateTimeUtil.DAILY * 3 / 1000) * 1000;
            arenaMaxRankCurrItemId = getInt(vos, EConfigKey.Arena_Max_Rank_Curr_Item);
            arenaTotalCapSub = getInt(vos, EConfigKey.Arena_Total_Cap_Sub);
            tradeMaxLevelMessage = getInt(vos, EConfigKey.Trade_Max_Level_Message);

            trainInitNpc = getStr(vos, EConfigKey.Train_Init_Npc);
            trainGrabCountMax = getInt(vos, EConfigKey.Train_Grab_Count_Max);
            trainResourceSurplus = getInt(vos, EConfigKey.Train_Resource_Surplus);
            trainGrabResourceSurplus = getInt(vos, EConfigKey.Train_Grab_Resource_Surplus);
            trainPlayerCapRange = getInt(vos, EConfigKey.Train_Player_Cap_Range);
            trainGrabCountRefresh = getInt(vos, EConfigKey.Train_Grab_Count_Refresh);
            trainTeamCount = getInt(vos, EConfigKey.Train_Team_Count);
            trainLeagueCount = getInt(vos, EConfigKey.Train_League_Count);
            trainGrabCDRefresh = getInt(vos, EConfigKey.Train_GrabCD_Refresh);
            happy7dayBox = getStr(vos, EConfigKey.happy7dayBox);
            trainRefreshListCD = getInt(vos, EConfigKey.Train_RefreshList_CD);
            trainStorageNullInitCap = getInt(vos, EConfigKey.Train_Storage_Null_Init_Cap);

            leagueArenaStartTime = parseTimeToMillis(getStr(vos, EConfigKey.League_Arena_Start_Time, "14:00"));
            leagueArenaEndTime = parseTimeToMillis(getStr(vos, EConfigKey.League_Arena_End_Time, "18:00"));
            leagueArenaOpenWeekday = getInt(vos, EConfigKey.League_Arena_Open_Weekday, 1);
            leagueSendRewardtime = parseTimeToMillis(getStr(vos, EConfigKey.League_Send_Reward_time, "18:10"));
            leagueWeekScoreRankCount = getInt(vos, EConfigKey.League_Week_Score_Rank_Count);
            leagueWSRankTypeCount = getInt(vos, EConfigKey.League_WS_Rank_Type_Count);
            leagueArenaChallengeCD = getInt(vos, EConfigKey.League_Arena_Challenge_CD);
            leagueArenaQuota = getStr(vos, EConfigKey.League_Arena_Quota);
            leagueChoiseTeamStartTime = parseTimeToMillis(getStr(vos, EConfigKey.League_Choise_Team_Start_Time, "21:15"));
            leagueChoiseTeamSustainTime = getInt(vos, EConfigKey.League_Choise_Team_Sustain_Time);
            leagueActiveLimit = getInt(vos, EConfigKey.League_Active_Limit);
            leagueDonateMax = getInt(vos, EConfigKey.League_Donate_Max);
            
            fast_cup_Count = getInt(vos, EConfigKey.Fast_cup_Count);
            
            Main_Win_Exp = getFloat(vos, EConfigKey.Main_Win_Exp);
            Main_Lost_Exp = getFloat(vos, EConfigKey.Main_Lost_Exp);
            Main_Win_Tactics = getFloat(vos, EConfigKey.Main_Win_Tactics);
            Main_Lost_Tactics = getFloat(vos, EConfigKey.Main_Lost_Tactics);
            
            fast_cup_tactics_Count = getInt(vos, EConfigKey.Fast_cup_tactics_Count);
            
            dualMeetInitCount = getInt(vos, EConfigKey.Dual_Meet_Init_Count);
            dualMeetRankInitCount = getInt(vos, EConfigKey.Dual_Meet_Rank_Init_Count);
            starletRankCallangeInfoCount = getInt(vos, EConfigKey.Starlet_Rank_Callange_Info_Count);
            
            allStarTime = getStr(vos, EConfigKey.All_Star_Time);
            allStarSendRewardTime = getStr(vos, EConfigKey.All_Star_Send_Reward_Time);
            allStarSendChallengeNum = getStr(vos, EConfigKey.All_Star_Send_Challenge_Num);
            
            allStarRecPlayerRate = getInt(vos, EConfigKey.All_Star_Rec_Player_Rate);
            allStarExcitationRate = getInt(vos, EConfigKey.All_Star_Excitation_Rate);
            
            allStarKillRewardBaseScore = getInt(vos, EConfigKey.All_Star_Kill_Reward_Base_Score);
            allStarRecPlayer = getStr(vos, EConfigKey.All_Star_Rec_Player);
            
            answerWinExp = getInt(vos, EConfigKey.Answer_Win_Exp);
            answerWinDropList = Lists.newArrayList(StringUtil.toIntegerArray(getStr(vos, EConfigKey.Answer_Win_Drop), StringUtil.COMMA));
            answerLossExp = getInt(vos, EConfigKey.Answer_Loss_Exp);
            answerLossDropList = Lists.newArrayList(StringUtil.toIntegerArray(getStr(vos, EConfigKey.Answer_Loss_Drop), StringUtil.COMMA));
            honorChallengeCount = getInt(vos, EConfigKey.HONOR_MATCH_NUM_INIT);
            honorbuyCount = getInt(vos, EConfigKey.HONOR_MATCH_NUM_BUY);
        }
        
        
        private boolean getBool(Map<String, ConfigBeanVO> vos, EConfigKey eck) {
            return getInt(vos, eck) == 1;
        }

        private int getInt(Map<String, ConfigBeanVO> vos, EConfigKey eck) {
            return getInt(vos, eck, 0);
        }

        private int getInt(Map<String, ConfigBeanVO> vos, EConfigKey eck, int defaultValue) {
            ConfigBeanVO vo = vos.get(eck.getKey());
            if (vo == null) {
                return defaultValue;
            }
            return Integer.parseInt(vo.getVal());
        }

        private float getFloat(Map<String, ConfigBeanVO> vos, EConfigKey eck) {
            return getFloat(vos, eck, 0);
        }

        private float getFloat(Map<String, ConfigBeanVO> vos, EConfigKey eck, float defaultValue) {
            ConfigBeanVO vo = vos.get(eck.getKey());
            if (vo == null) {
                return defaultValue;
            }
            return Float.parseFloat(vo.getVal());
        }

        private String getStr(Map<String, ConfigBeanVO> vos, EConfigKey eck) {
            return getStr(vos, eck, "");
        }

        private String getStr(Map<String, ConfigBeanVO> vos, EConfigKey eck, String defaultValue) {
            ConfigBeanVO vo = vos.get(eck.getKey());
            if (vo == null) {
                return defaultValue;
            }
            return vo.getVal();
        }

        private ImmutableList<TupleTime> parseTupleTime(String str) {
            if (str == null || str.trim().isEmpty()) {
                return ImmutableList.of();
            }
            ImmutableList.Builder<TupleTime> list = ImmutableList.builder();
            String[] arr = str.split(StringUtil.SEMICOLON);
            for (String str1 : arr) {
                String[] arr1 = str1.split(StringUtil.COMMA);
                LocalTime dt1 = LocalTime.parse(arr1[0], DateTimeFormatter.ISO_LOCAL_TIME);
                LocalTime dt2 = LocalTime.parse(arr1[1], DateTimeFormatter.ISO_LOCAL_TIME);
                list.add(new TupleTime(dt1, dt1.toSecondOfDay() * 1000, dt2, dt2.toSecondOfDay() * 1000));
            }
            return list.build();
        }
    }

    /** 时间对 */
    public static final class TupleTime {
        public final LocalTime localTime1;
        public final long timeMillis1;
        public final LocalTime localTime2;
        public final long timeMillis2;

        public TupleTime(LocalTime localTime1, long timeMillis1, LocalTime localTime2, long timeMillis2) {
            this.localTime1 = localTime1;
            this.timeMillis1 = timeMillis1;
            this.localTime2 = localTime2;
            this.timeMillis2 = timeMillis2;
        }
    }

    static class Config {
        private String val;
        private int intVal;

        Config(String val) {
            this.val = val;
            Integer iv = Ints.tryParse(val);
            this.intVal = iv == null ? 0 : iv;
        }

        public String getVal() {
            return this.val;
        }

        public int getIntVal() {
            return intVal;
        }

        @Override
        public String toString() {
            return "Config [val=" + val + ", intVal=" + intVal + "]";
        }

    }
}
