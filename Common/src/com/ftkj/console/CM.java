package com.ftkj.console;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.joda.time.DateTime;

import com.ftkj.annotation.ExcelBean;
import com.ftkj.cfg.AllStarBean;
import com.ftkj.cfg.ArchiveBean;
import com.ftkj.cfg.EmailViewBean;
import com.ftkj.cfg.GroupWarSeasonBean;
import com.ftkj.cfg.GroupWarTierBean;
import com.ftkj.cfg.GroupWarWeekAwardBean;
import com.ftkj.cfg.HappySevenDayBean;
import com.ftkj.cfg.ItemConvertBean;
import com.ftkj.cfg.KnockoutMatchBean;
import com.ftkj.cfg.LeagueAppointBean;
import com.ftkj.cfg.LeagueScoreRewardBean;
import com.ftkj.cfg.LeagueUpgradeBean;
import com.ftkj.cfg.MMatchConditionBean;
import com.ftkj.cfg.MMatchDivisionBean;
import com.ftkj.cfg.MMatchLevBean;
import com.ftkj.cfg.MatchRankAwardBean;
import com.ftkj.cfg.ModuleBean;
import com.ftkj.cfg.NpcMirrorBean;
import com.ftkj.cfg.PlayerCutsBean;
import com.ftkj.cfg.PlayerGradeBean;
import com.ftkj.cfg.PlayerPowerBean;
import com.ftkj.cfg.PlayerShopBean;
import com.ftkj.cfg.PlayerStarBean;
import com.ftkj.cfg.RankedMatchBean.FirstAwardBean;
import com.ftkj.cfg.RankedMatchBean.RankedMatchSeasonBean;
import com.ftkj.cfg.RankedMatchBean.RatingConvertBean;
import com.ftkj.cfg.RankedMatchBean.WinningStreakBean;
import com.ftkj.cfg.RankedMatchMedalBean;
import com.ftkj.cfg.RankedMatchTierBean;
import com.ftkj.cfg.SignMonthBean;
import com.ftkj.cfg.SignPeriodBean;
import com.ftkj.cfg.SkillLevelBean;
import com.ftkj.cfg.SkillPositionBean;
import com.ftkj.cfg.StreetBallBean;
import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.cfg.TacticsCapBean;
import com.ftkj.cfg.TacticsPowerBean;
import com.ftkj.cfg.TacticsStudyBean;
import com.ftkj.cfg.TacticsUpBean;
import com.ftkj.cfg.TeamExpBean;
import com.ftkj.cfg.TeamNumBean;
import com.ftkj.cfg.TeamPriceMoneyBean;
import com.ftkj.cfg.TrainPlayerBean;
import com.ftkj.cfg.VipBean;
import com.ftkj.cfg.battle.AIBean;
import com.ftkj.cfg.battle.AIBean.AIPlayerRuleBuilder;
import com.ftkj.cfg.battle.AIBean.AIPlayerRuleRespBuilder;
import com.ftkj.cfg.battle.AIBean.CoachRuleBuilder;
import com.ftkj.cfg.battle.AIRuleActionConditionBean;
import com.ftkj.cfg.battle.BaseBattleActionBean;
import com.ftkj.cfg.battle.BaseSubActionBean;
import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.cfg.battle.BattleBean.ActionGroupBuilder;
import com.ftkj.cfg.battle.BattleCustomBean;
import com.ftkj.cfg.battle.BattleCustomBean.CustomRoundBuilder;
import com.ftkj.cfg.battle.BattleCustomBean.CustomSubActionBean.Builder;
import com.ftkj.cfg.battle.BattleCustomBean.PlayerStatBuilder;
import com.ftkj.cfg.battle.BattleHintBean;
import com.ftkj.cfg.battle.BattleHintBean.GroupBuilder;
import com.ftkj.cfg.battle.BattlePlayerPowerBean;
import com.ftkj.cfg.battle.BattleSkillPowerBean;
import com.ftkj.cfg.card.PlayerCardCompositeBean;
import com.ftkj.cfg.card.PlayerCardGradeCap;
import com.ftkj.cfg.card.PlayerCardGroupBean;
import com.ftkj.cfg.card.PlayerCardStarLvExpBean;
import com.ftkj.cfg.honor.HonorDivBean;
import com.ftkj.cfg.honor.HonorLevBean;
import com.ftkj.db.domain.bean.AnswerQuestionBeanVO;
import com.ftkj.db.domain.bean.BDMoneyShopBeanVO;
import com.ftkj.db.domain.bean.ConfigBeanVO;
import com.ftkj.db.domain.bean.DraftRoomVO;
import com.ftkj.db.domain.bean.DropBeanVO;
import com.ftkj.db.domain.bean.LeagueDailyTaskBeanVO;
import com.ftkj.db.domain.bean.LeagueHonorBeanVO;
import com.ftkj.db.domain.bean.LeagueShopBeanVO;
import com.ftkj.db.domain.bean.MoneyShopBeanVO;
import com.ftkj.db.domain.bean.NPCBeanVO;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.db.domain.bean.PlayerMoneyBeanPO;
import com.ftkj.db.domain.bean.PropBeanVO;
import com.ftkj.db.domain.bean.TaskBeanVO;
import com.ftkj.db.domain.bean.TaskConditionBeanVO;
import com.ftkj.manager.cdkey.ConverCodeBean;
import com.ftkj.manager.coach.CoachBean;
import com.ftkj.manager.coach.CoachSkillBean;
import com.ftkj.manager.common.CacheManager;
import com.ftkj.manager.equi.cfg.EquiBean;
import com.ftkj.manager.equi.cfg.EquiClothesBean;
import com.ftkj.manager.equi.cfg.EquiRefreshBean;
import com.ftkj.manager.equi.cfg.EquiUpLvBean;
import com.ftkj.manager.equi.cfg.EquiUpQuaBean;
import com.ftkj.manager.equi.cfg.EquiUpStrBean;
import com.ftkj.manager.gym.ArenaBean;
import com.ftkj.manager.gym.ArenaConstructionBean;
import com.ftkj.manager.gym.ArenaRollItem;
import com.ftkj.manager.league.leagueArean.AreanRewardBean;
import com.ftkj.manager.league.leagueArean.PostionScoreBean;
import com.ftkj.manager.logo.cfg.LogoLvBean;
import com.ftkj.manager.logo.cfg.LogoQuaBean;
import com.ftkj.manager.player.PlayerPriceBean;
import com.ftkj.manager.skill.SkillBean;
import com.ftkj.manager.skill.buff.SkillBufferVO;
import com.ftkj.manager.stage.cfg.StageBean;
import com.ftkj.manager.starlet.DualMeetBean;
import com.ftkj.manager.starlet.DualMeetRadixBean;
import com.ftkj.manager.starlet.StarletRankAwardBean;
import com.ftkj.manager.system.NBABattleGuessBean;
import com.ftkj.manager.task.StarBean;
import com.ftkj.manager.task.TaskStarAwardBean;
import com.ftkj.manager.train.LeagueTrainBean;
import com.ftkj.manager.train.TrainArenaBean;
import com.ftkj.manager.train.TrainBean;
import com.ftkj.manager.train.TrainNpcBean;
import com.ftkj.manager.train.TrainTypeBean;
import com.ftkj.server.RedisKey;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.tool.zookeep.ZookeepServer;
import com.ftkj.util.ByteUtil;
import com.ftkj.util.excel.ExcelConfigBean;
import com.ftkj.util.excel.ExcelRead;

/**
 * 配置加载器
 *
 * @author Jay
 */
public class CM {
    private static final Logger log = LogManager.getLogger(CM.class);
    private static final String AI = "AI";
    /** Battle.xlsx */
    public static final String Battle = "Battle";
    /** MainMatch.xlsx */
    private static final String MainMatch = "MainMatch";
    /** HornoMatch.xlsx */
    private static final String HonorMatch = "HonorMatch";

    /** 全局配置表 */
    @ExcelBean(name = "Global", sheet = "Config", clazz = ConfigBeanVO.class)
    public static List<ConfigBeanVO> GLOBAL_CONFIGS;
    //
    @ExcelBean(name = "Equi", sheet = "Equi", clazz = EquiBean.class)
    public static List<EquiBean> EQUI_LIST;
    @ExcelBean(name = "Equi", sheet = "UpLv", clazz = EquiUpLvBean.class)
    public static List<EquiUpLvBean> EQUI_UP_LV_LIST;
    @ExcelBean(name = "Equi", sheet = "UpStrLv", clazz = EquiUpStrBean.class)
    public static List<EquiUpStrBean> EQUI_UP_STRLV_LIST;
    @ExcelBean(name = "Equi", sheet = "UpQuality", clazz = EquiUpQuaBean.class)
    public static List<EquiUpQuaBean> EQUI_UP_QUA_LIST;
    @ExcelBean(name = "Equi", sheet = "ClothesQua", clazz = EquiClothesBean.class)
    public static List<EquiClothesBean> EQUI_CLOTHES_QUA_LIST;
    @ExcelBean(name = "Equi", sheet = "Refresh", clazz = EquiRefreshBean.class)
    public static List<EquiRefreshBean> EQUI_Refresh_List;

    @ExcelBean(name = "LogoPlayer", sheet = "Lv", clazz = LogoLvBean.class)
    public static List<LogoLvBean> logoLvList;
    @ExcelBean(name = "LogoPlayer", sheet = "Quality", clazz = LogoQuaBean.class)
    public static List<LogoQuaBean> logoQuaList;
    @ExcelBean(name = "LogoPlayer", sheet = "Player", clazz = Map.class, key = "playerId", value = "name")
    public static Map<Integer, String> playerMap;

    // 球星卡
    @ExcelBean(name = "PlayerCollect", sheet = "CardGroup", clazz = PlayerCardGroupBean.class)
    public static List<PlayerCardGroupBean> Player_Card_Group;
//    @ExcelBean(name = "PlayerCollect", sheet = "CollectCap", clazz = PlayerCardCapBean.class)
//    public static List<PlayerCardCapBean> Player_Card_Cap;
    @ExcelBean(name = "PlayerCollect", sheet = "Composite", clazz = PlayerCardCompositeBean.class)
    public static List<PlayerCardCompositeBean> Player_Card_Composite;
    @ExcelBean(name = "PlayerCollect", sheet = "StarLvExp", clazz = PlayerCardStarLvExpBean.class)
    public static List<PlayerCardStarLvExpBean> Player_Card_StarLvExp;
//    @ExcelBean(name = "PlayerCollect", sheet = "CardQuality", clazz = PlayerCardQuaBean.class)
//    public static List<PlayerCardQuaBean> Player_Card_Quality;
//    @ExcelBean(name = "PlayerCollect", sheet = "CardCap", clazz = PlayerCardCapBean2.class)
//    public static List<PlayerCardCapBean2> Player_Card_Cap2;
    @ExcelBean(name = "PlayerCollect", sheet = "CardPlayerGradeCap", clazz = PlayerCardGradeCap.class)
    public static List<PlayerCardGradeCap> Player_Card_Grade_Cap;

    @ExcelBean(name = "GameNPC", sheet = "GameNpc", clazz = NPCBeanVO.class)
    public static List<NPCBeanVO> NPC = Collections.emptyList();
    @ExcelBean(name = "GameNPC", sheet = "Mirrors", clazz = NpcMirrorBean.Builder.class)
    public static List<NpcMirrorBean.Builder> NPC_Mirrors = Collections.emptyList();

    @ExcelBean(name = "Drop", sheet = "ItemDrops", clazz = DropBeanVO.class)
    public static List<DropBeanVO> DROP;

    @ExcelBean(name = "MainSchedule", sheet = "Main", clazz = StageBean.class)
    public static List<StageBean> stageList;

    @ExcelBean(name = "Upgrade", sheet = "Exp", clazz = TeamExpBean.class)
    public static List<TeamExpBean> teamExpList;

    @ExcelBean(name = "Team", sheet = "Num", clazz = TeamNumBean.Builder.class)
    public static List<TeamNumBean.Builder> teamNums = Collections.emptyList();

    @ExcelBean(name = "Items", sheet = "Items", clazz = PropBeanVO.class)
    public static List<PropBeanVO> propList;
    @ExcelBean(name = "Items", sheet = "Drop", clazz = PropBeanVO.class)
    public static List<PropBeanVO> itemDrops;
    @ExcelBean(name = "Items", sheet = "Convert", clazz = ItemConvertBean.class)
    public static List<ItemConvertBean> itemConerts;

    @ExcelBean(name = "PlayerCuts", sheet = "PlayerCuts", clazz = PlayerCutsBean.class)
    public static List<PlayerCutsBean> playerCutsBeanList;
    
    //降薪球员配置数据
    @ExcelBean(name = "PlayerUpdate", sheet = "Player", clazz = PlayerBeanVO.class)
    public static List<PlayerBeanVO> playerBeanList;

    // 战术
    @ExcelBean(name = "Tactics", sheet = "UpLv", clazz = TacticsUpBean.class)
    public static List<TacticsUpBean> tacticsUpBeanList;
    @ExcelBean(name = "Tactics", sheet = "Addition", clazz = TacticsCapBean.class)
    public static List<TacticsCapBean> tacticsCaps;
    @ExcelBean(name = "Tactics", sheet = "Learn", clazz = TacticsStudyBean.class)
    public static List<TacticsStudyBean> tacticsStudyBeanList;
    @ExcelBean(name = "Tactics", sheet = "Power", clazz = TacticsPowerBean.class)
    public static List<TacticsPowerBean> tacticsPowerBeanList;

    @ExcelBean(name = "Draft", sheet = "Room", clazz = DraftRoomVO.class)
    public static List<DraftRoomVO> draftList;

    @ExcelBean(name = Battle, sheet = "Battle", clazz = BattleBean.Builder.class)
    public static List<BattleBean.Builder> battles = Collections.emptyList();
    @ExcelBean(name = Battle, sheet = "ActionGroup", clazz = ActionGroupBuilder.class)
    public static List<ActionGroupBuilder> battleActionGroups = Collections.emptyList();
    @ExcelBean(name = Battle, sheet = "Action", clazz = BaseBattleActionBean.Builder.class)
    public static List<BaseBattleActionBean.Builder> battleActions = Collections.emptyList();
    @ExcelBean(name = Battle, sheet = "SubAction", clazz = BaseSubActionBean.Builder.class)
    public static List<BaseSubActionBean.Builder> battleSubActions = Collections.emptyList();
    @ExcelBean(name = Battle, sheet = "SkillPower", clazz = BattleSkillPowerBean.Builder.class)
    public static List<BattleSkillPowerBean.Builder> battleSkillPowers = Collections.emptyList();
    @ExcelBean(name = Battle, sheet = "PlayerPower", clazz = BattlePlayerPowerBean.Builder.class)
    public static List<BattlePlayerPowerBean.Builder> battlePlayerPowers = Collections.emptyList();
    @ExcelBean(name = Battle, sheet = "HintGroup", clazz = GroupBuilder.class)
    public static List<GroupBuilder> battleHintGroups = Collections.emptyList();
    @ExcelBean(name = Battle, sheet = "Hint", clazz = BattleHintBean.Builder.class)
    public static List<BattleHintBean.Builder> battleHints = Collections.emptyList();

    @ExcelBean(name = "BattleCustom", sheet = "Custom", clazz = BattleCustomBean.CustomBuilder.class)
    public static List<BattleCustomBean.CustomBuilder> battleCustoms = Collections.emptyList();
    @ExcelBean(name = "BattleCustom", sheet = "Round", clazz = CustomRoundBuilder.class)
    public static List<CustomRoundBuilder> bcRounds = Collections.emptyList();
    @ExcelBean(name = "BattleCustom", sheet = "SubAction", clazz = Builder.class)
    public static List<Builder> bcSubActs = Collections.emptyList();
    @ExcelBean(name = "BattleCustom", sheet = "TeamStat", clazz = BattleCustomBean.TeamStatBuilder.class)
    public static List<BattleCustomBean.TeamStatBuilder> bcTeamStats = Collections.emptyList();
    @ExcelBean(name = "BattleCustom", sheet = "PlayerStat", clazz = PlayerStatBuilder.class)
    public static List<PlayerStatBuilder> bcPrStats = Collections.emptyList();

    @ExcelBean(name = "Match", sheet = "Match", clazz = KnockoutMatchBean.class)
    public static List<KnockoutMatchBean> matchList;

    @ExcelBean(name = "Match", sheet = "RankAward", clazz = MatchRankAwardBean.class)
    public static List<MatchRankAwardBean> matchRankAwardList;

    @ExcelBean(name = "League", sheet = "Task", clazz = LeagueDailyTaskBeanVO.class)
    public static List<LeagueDailyTaskBeanVO> leagueDailyTaskList;

    @ExcelBean(name = "League", sheet = "Honor", clazz = LeagueHonorBeanVO.class)
    public static List<LeagueHonorBeanVO> leagueHonorList;

    @ExcelBean(name = "League", sheet = "Prop", clazz = LeagueAppointBean.class)
    public static List<LeagueAppointBean> leagueAppointBeanList;
    
    @ExcelBean(name = "League", sheet = "Level", clazz = LeagueUpgradeBean.class)
    public static List<LeagueUpgradeBean> leagueUpgradeBeanList;
    
    @ExcelBean(name = "League", sheet = "ActiveReward", clazz = LeagueScoreRewardBean.class)
    public static List<LeagueScoreRewardBean> leagueScoreRewardBeanList;

    @ExcelBean(name = "League", sheet = "ArenaReward", clazz = AreanRewardBean.class)
    public static List<AreanRewardBean> matchRewardBeanList;

    @ExcelBean(name = "League", sheet = "PostionScore", clazz = PostionScoreBean.class)
    public static List<PostionScoreBean> postionScoreBeanList;

    @ExcelBean(name = "Shop", sheet = "League", clazz = LeagueShopBeanVO.class)
    public static List<LeagueShopBeanVO> leagueShopList;

    @ExcelBean(name = "Shop", sheet = "Money", clazz = MoneyShopBeanVO.class)
    public static List<MoneyShopBeanVO> moneyShopList;

    @ExcelBean(name = "Shop", sheet = "BDMoney", clazz = BDMoneyShopBeanVO.class)
    public static List<BDMoneyShopBeanVO> bdmoneyShopList;

    @ExcelBean(name = "Shop", sheet = "Price", clazz = TeamPriceMoneyBean.class)
    public static List<TeamPriceMoneyBean> teamPriceMoneyList;

    @ExcelBean(name = "Task", sheet = "Star", clazz = StarBean.class)
    public static List<StarBean> starList;
    
    @ExcelBean(name = "Task", sheet = "Task", clazz = TaskBeanVO.class)
    public static List<TaskBeanVO> taskList;
    
    @ExcelBean(name = "Task", sheet = "StarArard", clazz = TaskStarAwardBean.class)
    public static List<TaskStarAwardBean> taskStarAwardList;

    @ExcelBean(name = "Task", sheet = "Condition", clazz = TaskConditionBeanVO.class)
    public static List<TaskConditionBeanVO> taskConditionList;

    @ExcelBean(name = "PlayerMoney", sheet = "PlayerMoney", clazz = PlayerMoneyBeanPO.class)
    public static List<PlayerMoneyBeanPO> playerMoneyBeanPOList;
    
    @ExcelBean(name = "PlayerPrice", sheet = "PlayerPrice", clazz = PlayerPriceBean.class)
    public static List<PlayerPriceBean> playerPriceBeanList;

    @ExcelBean(name = "Mail", sheet = "Mail", clazz = EmailViewBean.class)
    public static List<EmailViewBean> emailViewBeanList;

    @ExcelBean(name = "Arena", sheet = "Construction", clazz = ArenaConstructionBean.class)
    public static List<ArenaConstructionBean> arenaConstructionBeanList;

    @ExcelBean(name = "Arena", sheet = "Arena", clazz = ArenaBean.class)
    public static List<ArenaBean> arenaBeanList;

    @ExcelBean(name = "Arena", sheet = "Roll", clazz = ArenaRollItem.class)
    public static List<ArenaRollItem> arenaRollItemList;

    // 街球
    @ExcelBean(name = "StreetBall", sheet = "StreetBall", clazz = StreetBallBean.class)
    public static List<StreetBallBean> streetBallList;

    // 训练馆
    @ExcelBean(name = "Train", sheet = "Train", clazz = TrainBean.class)
    public static List<TrainBean> trainBeanList = Collections.emptyList();
    @ExcelBean(name = "Train", sheet = "TrainType", clazz = TrainTypeBean.class)
    public static List<TrainTypeBean> trainTypeBeanList = Collections.emptyList();
    @ExcelBean(name = "Train", sheet = "LeagueTrain", clazz = LeagueTrainBean.class)
    public static List<LeagueTrainBean> leagueTrainBeanList = Collections.emptyList();
    @ExcelBean(name = "Train", sheet = "TrainArean", clazz = TrainArenaBean.class)
    public static List<TrainArenaBean> trainAreanBeanList = Collections.emptyList();
    @ExcelBean(name = "Train", sheet = "Npc", clazz = TrainNpcBean.class)
    public static List<TrainNpcBean> trainNpcBeanList = Collections.emptyList();
    
    // 签到
    @ExcelBean(name = "Sign", sheet = "month", clazz = SignMonthBean.class)
    public static List<SignMonthBean> signMonthList;
    @ExcelBean(name = "Sign", sheet = "week", clazz = SignPeriodBean.class)
    public static List<SignPeriodBean> signPeriodList;

    // 活动
    @ExcelBean(name = "SystemActive", sheet = "Active", clazz = SystemActiveBean.class)
    public static List<SystemActiveBean> systemActiveList;
    @ExcelBean(name = "SystemActive", sheet = "Config", clazz = SystemActiveCfgBean.class)
    public static List<SystemActiveCfgBean> systemActiveCfgList;
    //答题活动
    @ExcelBean(name = "ActivityQuestionConfig", sheet = "ActQuestion", clazz = AnswerQuestionBeanVO.class)
    public static List<AnswerQuestionBeanVO> answerQuestionCfgList;

    // VIP
    @ExcelBean(name = "Vip", sheet = "level", clazz = VipBean.class)
    public static List<VipBean> vipCfgList;

    @ExcelBean(name = "Skill", sheet = "skill", clazz = SkillBean.class)
    public static List<SkillBean> skillBeanList = Collections.emptyList();
    @ExcelBean(name = "Skill", sheet = "buff", clazz = SkillBufferVO.class)
    public static List<SkillBufferVO> skillBuffers = Collections.emptyList();

    @ExcelBean(name = "Skill", sheet = "position", clazz = SkillPositionBean.class)
    public static List<SkillPositionBean> skillPositionList;
    @ExcelBean(name = "Skill", sheet = "player", clazz = SkillPositionBean.class)
    public static List<SkillPositionBean> skillPlayerPositionList;
    @ExcelBean(name = "Skill", sheet = "level", clazz = SkillLevelBean.class)
    public static List<SkillLevelBean> skillLevelList;

    @ExcelBean(name = "PlayerPhysical", sheet = "Physical", clazz = PlayerPowerBean.class)
    public static List<PlayerPowerBean> playerPowerBeanList;

    @ExcelBean(name = "Coach", sheet = "coach", clazz = CoachBean.class)
    public static List<CoachBean> coachBeanList;
    @ExcelBean(name = "Coach", sheet = "skill", clazz = CoachSkillBean.class)
    public static List<CoachSkillBean> coachSkills = Collections.emptyList();

    @ExcelBean(name = "LevelUp", sheet = "playerGrade", clazz = PlayerGradeBean.class)
    public static List<PlayerGradeBean> playerGradeList;

    @ExcelBean(name = "LevelUp", sheet = "playerStar", clazz = PlayerStarBean.class)
    public static List<PlayerStarBean> playerStarList;

    // 分区开服时间配置
    //    @ExcelBean(name = "ServerShard", sheet = "Shard", clazz = ServerShardBean.class)
    //    public static List<ServerShardBean> shardBeanList;

    @ExcelBean(name = "TrainPlayer", sheet = "Lv", clazz = TrainPlayerBean.class)
    public static List<TrainPlayerBean> trainPlayerList;

    @ExcelBean(name = "PlayerArchive", sheet = "Archive", clazz = ArchiveBean.class)
    public static List<ArchiveBean> archiveList;

    @ExcelBean(name = MainMatch, sheet = "Div", clazz = MMatchDivisionBean.class)
    public static List<MMatchDivisionBean> mMatchDivs = Collections.emptyList();
    @ExcelBean(name = MainMatch, sheet = "Lev", clazz = MMatchLevBean.class)
    public static List<MMatchLevBean> mMatchLevs = Collections.emptyList();
    @ExcelBean(name = MainMatch, sheet = "WinCondition", clazz = MMatchConditionBean.class)
    public static List<MMatchConditionBean> mMatchWcs = Collections.emptyList();
    //-------荣耀-----------
    @ExcelBean(name = HonorMatch, sheet = "Div", clazz = HonorDivBean.class)
    public static List<HonorDivBean> hMatchDivs = Collections.emptyList();
    @ExcelBean(name = HonorMatch, sheet = "Lev", clazz = HonorLevBean.class)
    public static List<HonorLevBean> hMatchLevs = Collections.emptyList();
    @ExcelBean(name = HonorMatch, sheet = "WinCondition", clazz = MMatchConditionBean.class)
    public static List<MMatchConditionBean> hMatchWcs = Collections.emptyList();
    @ExcelBean(name = "HappySevenDay", sheet = "HappySevenDay", clazz = HappySevenDayBean.class)
    public static List<HappySevenDayBean> happySevenDayBeans = Collections.emptyList();

    @ExcelBean(name = "MatchRanked", sheet = "Medal", clazz = RankedMatchMedalBean.class)
    public static List<RankedMatchMedalBean> rMatchMedals = Collections.emptyList();
    @ExcelBean(name = "MatchRanked", sheet = "Tier", clazz = RankedMatchTierBean.class)
    public static List<RankedMatchTierBean> rMatchTiers = Collections.emptyList();
    @ExcelBean(name = "MatchRanked", sheet = "Season", clazz = RankedMatchSeasonBean.class)
    public static List<RankedMatchSeasonBean> rMatchSeasons = Collections.emptyList();
    @ExcelBean(name = "MatchRanked", sheet = "FirstAward", clazz = FirstAwardBean.class)
    public static List<FirstAwardBean> rMatchFirstAwards = Collections.emptyList();
    @ExcelBean(name = "MatchRanked", sheet = "RatingConvert", clazz = RatingConvertBean.class)
    public static List<RatingConvertBean> rMatchRatingConverts = Collections.emptyList();
    @ExcelBean(name = "MatchRanked", sheet = "WinningStreak", clazz = WinningStreakBean.class)
    public static List<WinningStreakBean> rMatchWinningStreaks = Collections.emptyList();

    @ExcelBean(name = "CDKey", sheet = "Code", clazz = ConverCodeBean.class)
    public static List<ConverCodeBean> converCodeBeanList = Collections.emptyList();

    @ExcelBean(name = "PlayerShop", sheet = "PlayerShop", clazz = PlayerShopBean.class)
    public static List<PlayerShopBean> playerExchangeCfg = Collections.emptyList();

    @ExcelBean(name = "NBABattleGuess", sheet = "guess", clazz = NBABattleGuessBean.class)
    public static List<NBABattleGuessBean> guessBeanList = Collections.emptyList();

    @ExcelBean(name = "AllStar", sheet = "AllStar", clazz = AllStarBean.AllStarBuilder.class)
    public static List<AllStarBean.AllStarBuilder> allStars = Collections.emptyList();
    @ExcelBean(name = "AllStar", sheet = "Npc", clazz = AllStarBean.AllStarNpcBuilder.class)
    public static List<AllStarBean.AllStarNpcBuilder> allStarNpcs = Collections.emptyList();
    @ExcelBean(name = "AllStar", sheet = "Award", clazz = AllStarBean.AllStarAwardBuilder.class)
    public static List<AllStarBean.AllStarAwardBuilder> allStarAwards = Collections.emptyList();
    @ExcelBean(name = "AllStar", sheet = "PersonalAward", clazz = AllStarBean.PersonalAwardBuilder.class)
    public static List<AllStarBean.PersonalAwardBuilder> allStarPersonalAward = Collections.emptyList();

    @ExcelBean(name = AI, sheet = "Group", clazz = AIBean.AIGroupBuilder.class)
    public static List<AIBean.AIGroupBuilder> aiGroups = Collections.emptyList();
    //    @ExcelBean(name = AI, sheet = "SkillRule", clazz = AIPlayerRuleBuilder.class)
    public static List<AIPlayerRuleBuilder> aiSkillRules = Collections.emptyList();
    //    @ExcelBean(name = AI, sheet = "SkillResp", clazz = AIPlayerRuleRespBuilder.class)
    public static List<AIPlayerRuleRespBuilder> aiSkillResps = Collections.emptyList();

    @ExcelBean(name = AI, sheet = "CoachRule", clazz = CoachRuleBuilder.class)
    public static List<CoachRuleBuilder> aiCoachRules = Collections.emptyList();
    //    @ExcelBean(name = AI, sheet = "CoachCond", clazz = AIRuleActionConditionBean.Builder.class)
    public static List<AIRuleActionConditionBean.Builder> aiCoachActConds = Collections.emptyList();
    @ExcelBean(name = AI, sheet = "CoachResp", clazz = AIBean.CoachRuleBuilder.RespBuilder.class)
    public static List<AIBean.CoachRuleBuilder.RespBuilder> aiCoachResps = Collections.emptyList();

    @ExcelBean(name = AI, sheet = "TacticRule", clazz = AIBean.TacticRuleBuilder.class)
    public static List<AIBean.TacticRuleBuilder> aiTacticRules = Collections.emptyList();
    @ExcelBean(name = AI, sheet = "TacticResp", clazz = AIBean.TacticRuleBuilder.RespBuilder.class)
    public static List<AIBean.TacticRuleBuilder.RespBuilder> aiTacticResps = Collections.emptyList();
    @ExcelBean(name = AI, sheet = "Substitute", clazz = AIBean.SubsituteRuleBuilder.class)
    public static List<AIBean.SubsituteRuleBuilder> aiSubRules = Collections.emptyList();

    @ExcelBean(name = "LeagueGroupWar", sheet = "Season", clazz = GroupWarSeasonBean.class)
    public static List<GroupWarSeasonBean> leagueGroupWarSeasonList;
    @ExcelBean(name = "LeagueGroupWar", sheet = "Tier", clazz = GroupWarTierBean.class)
    public static List<GroupWarTierBean> leagueGruopTierList;
    @ExcelBean(name = "LeagueGroupWar", sheet = "WeekAward", clazz = GroupWarWeekAwardBean.class)
    public static List<GroupWarWeekAwardBean> leagueGruopWeekAwardList;

    @ExcelBean(name = "RankArena", sheet = "Arena", clazz = com.ftkj.cfg.ArenaBean.RankAwardBean.Builder.class)
    public static List<com.ftkj.cfg.ArenaBean.RankAwardBean.Builder> arenaRanks = Collections.emptyList();
    @ExcelBean(name = "RankArena", sheet = "Npc", clazz = com.ftkj.cfg.ArenaBean.NpcRankBean.Builder.class)
    public static List<com.ftkj.cfg.ArenaBean.NpcRankBean.Builder> arenaNpcs = Collections.emptyList();

    @ExcelBean(name = "Modules", sheet = "Level", clazz = ModuleBean.Builder.class)
    public static List<ModuleBean.Builder> modules = Collections.emptyList();
    
    /** 新秀对抗赛,新秀排位赛*/
    @ExcelBean(name = "Starlet", sheet = "RankMatchAward", clazz = StarletRankAwardBean.class)
    public static List<StarletRankAwardBean> rankAwardBeanList = Collections.emptyList();
    @ExcelBean(name = "Starlet", sheet = "DualMeetRadix", clazz = DualMeetRadixBean.class)
    public static List<DualMeetRadixBean> dualMeetRadixBeanList = Collections.emptyList();
    @ExcelBean(name = "Starlet", sheet = "DualMeet", clazz = DualMeetBean.class)
    public static List<DualMeetBean> dualMeetBeanList = Collections.emptyList();

    public static void reload() {
        final String readLocalConfig = System.getProperty("x3.excel.local");
        final boolean readZKConfig = (readLocalConfig == null ||
                readLocalConfig.isEmpty()
                || Boolean.parseBoolean(readLocalConfig) == Boolean.FALSE);

        // FIXME 监控excel配置变化，后面可以考虑细化到单个excel文件级别
        Watcher w = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    CacheManager manager = InstanceFactory.get().getInstance(CacheManager.class);
                    reloadConfig(readZKConfig);
                    manager.resetCache();
                } catch (Exception e) {
                    log.error("reload excel error " + e.getMessage(), e);
                }
                // reloadConfig();
                ZookeepServer.getInstance().exists(ZookeepServer.getConfigPath(), this);
            }
        };

        // 监听配置文件父节点
        ZookeepServer.getInstance().exists(ZookeepServer.getConfigPath(), w);
        reloadConfig(readZKConfig);
    }

    private static void reloadConfig(boolean readZKConfig) {

        // ZookeepServer.getInstance().getBytes(node)

        // // 读取excel配置
        // ExcelConsole.init();
        //
        init(readZKConfig);
    }

    public static void check() {
        // TODO 检查是否有没读取成功的字段；
    }

    /**
     * 调试excel配置文件路径
     */
    public static String debugExcelPath = CM.class.getResource("/").getPath()
            + "../excel";

    static {
        String excelPath = System.getProperty("x3.excel.local.path");
        if (excelPath != null && !excelPath.isEmpty()) {
            debugExcelPath = excelPath;
        }
    }

    /**
     * excel文件名称
     *
     * @param key
     * @param isZKServer 当false时读取本地，方便测试
     * @return
     */
    public static byte[] getExcelByte(boolean isZKServer, String key) {
        //从zk服务器取
        if (isZKServer) {
            return ZookeepServer.getInstance().getBytes(
                    ZookeepServer.getConfigPath() + "/" + key);
        }
        //本地配置
        try {
            String filename = getFileName(key);
            log.debug("读本地excel[{}]", filename);
            File file = new File(filename);
            if (!file.exists()) {
                log.error("配置文件不存在{}", filename);
                return new byte[0];
            }
            return ByteUtil.toByteArray(new File(filename));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /** 本地配置 */
    private static String getFileName(String key) {
        return debugExcelPath + File.separatorChar + key + ".xlsx";
    }

    public static void init(boolean readZKConfig) {
        log.info("init excel. zk {} debugpath {} zkpath {} redis {}", readZKConfig, debugExcelPath, ZookeepServer.getConfigPath(), jedisUtil != null);
        Field[] list = CM.class.getDeclaredFields();
        Map<String, byte[]> configSource = new HashMap<>(list.length);

        log.warn("excel convert to bean..");
        for (Field field : list) {
            if (!field.isAnnotationPresent(ExcelBean.class)) {
                continue;
            }
            ExcelBean anno = field.getAnnotation(ExcelBean.class);
            try {
                log.trace("读取 excel {} sheet {} 开始 {}", anno.name(), anno.sheet(), DateTime.now());
                ExcelConfigBean ecb;
                if (readZKConfig) {//from zk
                    byte[] fileBytes = configSource.computeIfAbsent(anno.name(), key -> getExcelByte(readZKConfig, key));
                    ecb = ExcelRead.readFile(fileBytes, anno.sheet(), anno.name());
                } else {//from local
                    ecb = readExcelFromLocal(configSource, anno);
                }

                log.trace("读取 excel {} sheet {} 结束 {} ecb {}", anno.name(), anno.sheet(), DateTime.now(), ecb);
                if (ecb == null) {
                    log.error("配置文件不存在{}-{}", anno.name(), anno.sheet());
                    continue;
                }
                readExcel(field, anno, ecb);
            } catch (Exception e) {
                log.error("配置文件读取报错" + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        log.warn("excel convert to bean done");
    }

    private static ExcelConfigBean readExcelFromLocal(Map<String, byte[]> configSource, ExcelBean anno) throws Exception {
        boolean readZKConfig = false;
        String filename = getFileName(anno.name());
        File f = new File(filename);
        String cacheKey = RedisKey.Local_Config + anno.sheet() + filename;
        long lastTime = f.lastModified();

        ExcelConfigBean ecb = null;
        JedisUtil jedisUtil = getJedisUtil();
        if (jedisUtil != null) {
            LocalConfigCache lcc = jedisUtil.getObj(cacheKey);
            if (lcc != null && lcc.getLastModifiedTime() == lastTime) {//没有变化, 直接返回缓存的内容, 加速启动
                ecb = lcc.getContent();
            }
        }
        if (ecb == null) {
            byte[] fileBytes = configSource.computeIfAbsent(anno.name(), key -> getExcelByte(readZKConfig, key));
            ecb = ExcelRead.readFile(fileBytes, anno.sheet(), anno.name());
            if (jedisUtil != null) {
                jedisUtil.set(cacheKey, new LocalConfigCache(lastTime, ecb), (int) TimeUnit.DAYS.toSeconds(1));
            }
        }
        return ecb;
    }

    /**
     * 读
     *
     * @param field
     * @param anno
     */
    @SuppressWarnings("rawtypes")
    private static void readExcel(Field field, ExcelBean anno,
                                  ExcelConfigBean bean) {
        // System.err.println("开始初始化"+anno.name()+"文件");
        field.setAccessible(true);
        boolean isNull = false;
        try {
            if (anno.clazz() != Map.class) {
                List list = bean.converToBeanList(anno.clazz());
                if (list == null) {
                    isNull = true;
                }
                field.set(List.class, list);
            } else {
                Map map = null;
                if ("".equals(anno.key()) || "".equals(anno.value())) {
                    map = bean.converToMap();
                } else {
                    map = bean.converToMap(anno.key(), anno.value());
                }
                if (map == null) {
                    isNull = true;
                }
                field.set(Map.class, map);
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        } finally {
            //            log.trace(field.getName());
            // System.err.println(field.getName());
        }
        //
        if (isNull) {
            log.error(field.getName() + "[" + anno.name() + "]:读取配置为空！");
        }

    }

    public static void clear() {
        // 加完完后要释放掉；

    }

    private static JedisUtil jedisUtil;

    public static void setJedisUtil(JedisUtil jedisUtil) {
        CM.jedisUtil = jedisUtil;
    }

    private static JedisUtil getJedisUtil() {
        return jedisUtil;
    }

    /** 本地策划配置缓存 */
    public static final class LocalConfigCache implements Serializable {
        private static final long serialVersionUID = 2;
        /** excel 最后修改时间 */
        private long lastModifiedTime;
        /** excel 内容 */
        private ExcelConfigBean content;

        public LocalConfigCache() {
        }

        public LocalConfigCache(long lastModifiedTime, ExcelConfigBean content) {
            this.lastModifiedTime = lastModifiedTime;
            this.content = content;
        }

        public long getLastModifiedTime() {
            return lastModifiedTime;
        }

        public ExcelConfigBean getContent() {
            return content;
        }
    }
}
