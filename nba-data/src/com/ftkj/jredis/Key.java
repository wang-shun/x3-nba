package com.ftkj.jredis;

import java.util.Calendar;
import java.util.Date;

import com.ftkj.util.UtilDateTime;

public class Key {

	public static int ZERO							= 	0;
	public static int MINUTE						=	60;
	public static int HOUR							=	60*60;
	public static int DAY							=	24*60*60;
	public static int HOUR3							=	3*60*60;

	public static String nba_info					= 	"nba_info";
	public static String nba_data					= 	"nba_data_";
	public static String nba_data_id				=	"nba_data_id";
	public static String guess_player_rank			=	"game_guess_rank_";

	public static String team_talent_211_num		= 	"team_talent_211_";//嘉奖--每天登陆时，额外赠送10万经费和10点声望。

	public static String team_draft_num				=	"team_draft_";//选秀次数
	public static String team_new_draft_num			=   "team_new_draft_";//新秀选秀次数
	public static String team_vote_list				=	"team_vote_";//投票
	public static String team_up_equi_num			=	"team_upequi_";//强化
	public static String team_draft_new_help_step	=	"team_draft_new_help_step_";//新手引导选秀获得的球员


	public static String team_activity_object		=	"team_activity_";//活跃信息

	public static String team_guess_player_log		= 	"team_guess_";//球员竞猜
	public static String team_guess_match_log		=	"team_match_";//球队 竞猜
	public static String team_mission_list			= 	"team_mission_";//任务列表 ***1
	public static String team_position_str			=	"team_position_";//阵容("1:9,2:10,3:11,4:25,5:26")
	public static String team_player_list 		   	=	"team_player_";//球员列表 ***1
	public static String team_prop_list				=	"team_prop_";//道具列表 ***1
	public static String team_equi_list				=	"team_equi_";//装备列表 ***1
	public static String team_tactics_list			= 	"team_tactics_";//战术列表***1
	public static String team_object				= 	"team_info_";//球队对像
	public static String team_friend_list			=	"team_friend_";//好友列表***1
	public static String log_team_friend_list		=	"log_team_friend_";//好友列表***1
	public static String team_talent_list			=	"team_talent_";//天赋列表 ***1
	public static String team_market_object			=	"team_market_";//自由市场
	public static String team_answer_object			=	"team_answer_";//答题
	public static String team_npc_league_object     = 	"team_npc_league_";//联赛对像
	public static String team_leager_object			=	"team_leager_";//用户所在联盟对像
	public static String team_service_list			=	"team_service_";//球队服务***1
	public static String team_player_service_list	=	"team_play_service_";//球队服务
	public static String team_chat_list				= 	"team_chat_";//私聊
	public static String team_honor_list			=	"team_honor_";//荣誉 ***1
	public static String team_sns_list				=	"team_sns_";//分享
	public static String team_group_object			=	"team_group_";//梦幻组合
	public static String team_guess_object			= 	"team_guess_";//竞猜
	public static String team_group_list			=	"team_groups_";//梦幻组合列表
	public static String team_work_group_num		=	"team_work_group_";//训练次数
	public static String team_stat_list				= 	"team_stat_";//球员平均数据列表 ***1
	public static String team_login_day				=	"team_login_";//连续登录
	public static String team_skill_list			= 	"team_skill_";//技能列表
	public static String team_rand_skill			=	"team_rand_skill_";//技能洗练
	public static String login_day					=	"login_day_";//连续登录
	public static String team_sign_friend_num		=	"team_sign_friend_";//签约好友球员次数
	public static String log_market_fd_list			=	"log_market_fd_list_";//好友偷球员日志列表
	public static String team_gem_list				=	"team_gem_list_";//球队宝石列表
	public static String team_player_logo_list		=	"team_player_logo_list_";//球员自定义LOGO列表
	
	
	public static String team_player_store			=	"team_player_store_";//球员仓库
	public static String team_player_store_data_list=	"team_player_store_data_list_";//球员仓库列表

	public static String team_pk_list				= 	"team_pk_";//pk列表
	public static String team_pk_type_2_num			=   "team_pk_type_2_";//即时PK数
	public static String team_pk_score				=	"team_score_pk_";//pk分数列表***1
	public static String team_pk_step				=	"team_step_pk_";//pk小节列表***1

	public static String team_pknpc_num				=	"team_pknpc_";//pknpc次数
	public static String team_pknpc_list			= 	"team_pknpcs_";//pk npc 列表
	public static String team_pknpc_star_list		=	"team_pknpc_star_list_";//联赛星级列表

	public static String team_money_fk_list			= 	"team_money_fk_";//财务报表
	public static String team_money_fp_list			= 	"team_money_fp_";//财务报表
	public static String team_money_rp_list			= 	"team_money_rp_";//财务报表
	public static String team_player_log_list		= 	"team_player_log_";//球员报表
	public static String team_props_log_list		= 	"team_props_log_";//道具报表
	public static String team_equi_log_list			=	"team_equi_log_";//装备报表
	public static String team_active_log_list		= 	"team_active_log_list_";//活跃报表
	public static String player_card_log_list		=	"player_card_log_list_";//球星卡报表
	public static String stadium_jsf_log_list		=	"stadium_jsf_log_list_";//建设费报表
	public static String player_low_col_list		=	"player_low_col_list_";//球队球员底薪收集列表
	public static String player_low_down_list		=	"player_low_down_list_";//球队球员底薪收集列表
	
	public static String team_rank_list				=	"team_rank_list";//球队排行榜
	public static String team_top_list				=	"team_top_list";//名人堂
	public static String answer_rank_list			=	"answer_rank_list";//积分排行榜
	public static String trade_log_list				=	"trade_";//自由买卖交易记录

	public static String league_rank_list			= 	"league_rank_list";//联盟排行榜

	public static String league_service_list		=	"league_service_";//联盟服务列表
	public static String league_team_list			= 	"league_team_";//联盟成员
	public static String league_sign_list			= 	"league_sign_";//联盟申请列表
	public static String league_msg_list			=	"league_msg_";//联盟日志
	public static String league_object				=	"league_info_";//联盟对像
	public static String league_pk_list				=   "league_pk_";//本周盟战列表***1
	public static String league_pkinfo_list			=   "league_pkinfo_";//盟战参与者***1
	public static String league_pk_history_list		=   "league_pk_history_";//盟战历史列表
	public static String league_exit_num			=	"league_exit_";//逐出联盟次數
	public static String league_service_type_list	=	"league_service_type_list_";//联盟中某种建筑列表
	public static String league_invite_num			=	"league_invite_num_";//联盟发送邀请入盟次数
	public static String league_prop_list			=	"league_prop_list_";//联盟道具列表
	public static String league_cf_shopping_set		=	"league_cf_shopping_set_";//联盟财富商城等级设置
	public static String league_cf_shopping_buy_num =	"league_cf_shopping_buy_num_";//联盟财富商城购买道具个数
	
	public static String cup_curr_object			=   "cup_curr_info";//最近一场杯赛
	public static String cup_curr_gift_list			=   "cup_curr_info_gift";//杯赛奖品
	public static String cup_history_list			=	"cup_history_list";//往届回顾
	public static String cup_rank_list				=	"cup_rank_list_";//排行榜
	public static String cup_vs_list				=	"cup_vs_list_";//对战列表
	public static String cup_team_list				=	"cup_team_list_";//今日已比赛
	public static String cup_team_group				=	"cup_team_group_";//用户所在组

	public static String pk_group_list				=	"pk_group_list";//进行中的比赛
	public static String pk_group_join_list			=	"pk_group_join_";//参与者***1
	public static String pk_group_result_list 		=	"pk_group_result_";//战绩
	public static String pk_group_vs_list			=	"pk_group_vs_";//对战列表

	public static String pk_ladders_object          =   "pk_ladders_object_";//天梯赛赛事对象
	public static String team_pkladders				=	"team_pkladders_";//球队赛事详细
	public static String pk_ladders_rank_list		= 	"pk_ladders_rank_list_";//球队赛事排名
	public static String pk_ladders_result			=	"pk_ladders_result_";//赛事战报
	public static String pk_ladders_next_object		=	"pk_ladders_next_object_";//下一个赛事对象

	public static String system_active_list			=	"system_active_list";//系统活动列表
	public static String day_fall_stats_prop 		= 	"day_fall_prop_";//一天掉落道具个数
	public static String day_fall_stats_equi 		= 	"day_fall_equi_";//一天掉落装备个数


	public static String team_bid_player_money_list =	"team_bid_player_money_list_";//球员竞价列表

	public static String team_pwd_object			=	"team_pwd_object_";//球队密码对象

	public static String team_system_active_award	=	"team_system_active_award_";//活动奖励对象

	public static String team_week_statistisc_object=	"team_week_statistisc_object_";//球队统计对象
	public static String team_day_statistisc_object =	"team_day_statistisc_object_";//球队每天统计对象
	public static String team_exclusive_jf_object	=	"team_exclusive_jf_object_";//球队每天兑换卡对象
	
	
	public static String pk_auction_object			=	"pk_auction_object_";//竞赛赛事对象
	public static String pk_auction_team			=	"pk_auction_team_";//球队竞赛对象
	public static String pk_auction_fall_book		=	"pk_auction_fall_book_";//竞技赛每天掉落申请书数量
	public static String xz_shopping_buy_num 		=	"xz_shopping_buy_num";//勋章商城购买道具个数
	
	public static String guess_match_team_object	=	"guess_match_team_object_";//球队竞猜对象

	public static String team_pk_maze_object		=	"team_pk_maze_object_";//球队挑战迷宫对象
	public static String pk_maze_rank_list			=	"pk_maze_rank_list_";//挑战迷宫最快速度排名

	public static String team_coach_list			=	"team_coach_list_";//球队教练列表

	public static String account_invite_list		=	"account_invite_list_";//邀请好友列表

	public static String team_plot_object			=	"team_plot_object_";//球队剧情对象
	public static String atv_olympic_object        	=	"atv_olympic_object_";//奥运活动对象
	public static String atv_valentine_rank        	=	"atv_valentine_rank_";//七夕活动人气送称谓
	public static String atv_valentine_equi        	=	"atv_valentine_equi_";//七夕活动装备限制 
	public static String atv_valentine_pk			= 	"atv_valentine_pk_";//七夕活动比赛记录
	public static String atv_day_exchg_object		=	"atv_day_exchg_object_";//比赛每日得好礼
	public static String atv_latest_object			=	"atv_latest_object_";//合区对象
	public static String atv_config					= 	"atv_config_";//系统活动配置缓存
	public static String team_come_back				= 	"team_come_back_";//老玩家回归
	public static String atv_yell_object			=	"atv_yell_object_";//球队呐喊对象
	public static String atv_team_day_sign_in		=	"atv_team_day_sign_in_";//球队长期每日签到
	public static String atv_team_month_sign_in		=	"atv_team_month_sign_in_";//球队长期每月签到
	public static String atv_team_day_pk            =   "atv_team_day_pk_";//球队长期每日比赛
	public static String atv_share_count  	 		= 	"atv_share_count_";//分享活动领取礼包数量
	public static String atv_login_gift 			= 	"atv_login_gift_";
	public static String atv_tr_bug					=	"atv_tr_bug_";//土耳其维护对象
	public static String atv_school_pk 				=	"atv_school_pk_";
	public static String atv_school_rechange 		=	"atv_school_rechange_";
	public static String atv_mission   				= 	"atv_mission_";//周年任务活动
	public static String atv_login_day				=	"atv_login_day_";
	public static String atv_login_day_time			=	"atv_login_day_time_";
	public static String atv_yellow_diamond_lott	= 	"atv_yellow_diamond_lott_";
	public static String sys_atv_teacher			=	"sys_atv_teacher_";//教师节活动球队信息
	public static String sys_atv_national_amount	=	"sys_atv_national_amount_";//国庆积分对象
	public static String sys_atv_national_convert	=	"sys_atv_national_convert_";//国庆兑换状态
	public static String atv_hallowean_lott			= 	"atv_hallowean_lott_";
	public static String halloween_effect_list		=	"halloween_effect_list_";//变脸效果列表
	public static String sys_atv_bachelor			=	"sys_atv_bachelor_";  //2013光棍节数据
	public static String sys_atv_thanks_day			=	"sys_atv_thanks_day_";  //2013感恩节数据
	public static String sys_atv_thanks_rp			=	"sys_atv_thanks_rp_";  //2013光棍节数据
	public static String sys_atv_back_buy_prop_list	=	"sys_atv_back_buy_prop_list_";//消费返送道具最多花费者对象
	public static String sys_atv_anniversary_score	=	"sys_atv_anniversary_score_";  //2013周年庆积分
	public static String sys_atv_christmas			=  	"sys_atv_christmas_";	//2013圣诞节数据
	public static String sys_atv_atvnewyear32_team	=	"sys_atv_atvnewyear32_team_";//2014元旦节球队永登高峰列表
	public static String sys_atv_atvnewy_today_rank	=	"sys_atv_atvnewy_today_rank_";//2014元旦节永登高峰今天排行列表
	public static String sys_atv_atvnewy_yester_rank=	"sys_atv_atvnewy_yester_rank_";//2014元旦节永登高峰昨天排行列表
	public static String sys_atv_atvnewyear33_obj	=	"sys_atv_atvnewyear33_obj_";//2014元旦节球队寻宝对象
	public static String sys_atv_atvHongbao_obj		=	"sys_atv_atvHongbao_obj_";//2014春节红包活动
	public static String sys_atv_atvFxgz_obj		=	"sys_atv_atvFxgz_obj_";//2014春节福星高照活动
	public static String sys_atv_atvSqsm_obj		=	"sys_atv_atvSqsm_obj_";//2014春节十全十美活动
	public static String sys_atv_atvFztx_obj		=	"sys_atv_atvFztx_obj_";//2014春节福泽天下活动
	public static String sys_atv_atvStadiumScore	=	"sys_atv_atvStadiumScore_";//球馆-荣誉无限活动
	public static String sys_atv_atvStadiumfans		=	"sys_atv_atvStadiumfans_";//球馆-召集球迷活动
	public static String sys_atv_atvLeagueCF		=	"sys_atv_atvLeagueCF_";//财富比拼-联盟
	public static String sys_atv_atvLeagueTeamCF	=	"sys_atv_atvLeagueTeamCF_";//财富比拼-球队
	public static String sys_atv_atvLeagueCallCF    =   "sys_atv_atvLeagueCallCF_";//财富比拼-召唤次数
	public static String sys_atv_stadium_jsf		=	"sys_atv_stadium_jsf_";	//建设费优惠活动
	public static String sys_atv_league_distribution=   "sys_atv_league_distribution_"; //联盟捐献活动
	public static String sys_atv_stadium_zengyi		=   "sys_atv_stadium_zengyi_"; //增益活动
    public static String sys_atv_summary_exchange        =   "sys_atv_summary_exchange_";//初夏缤纷活动-兑换道具数量
    public static String sys_atv_summary_exchange_jf        =   "sys_atv_summary_exchange_jf";//初夏缤纷活动-兑换积分数量
    public static String sys_atv_summary_free_equi        =   "sys_atv_summary_free_equi_";//初夏缤纷活动-免费装备
    public static String sys_atv_mid_summary       =   "sys_atv_mid_summary_";//仲夏火焰节-璀璨大放送
    public static String sys_atv_fanli_day       		 =   "sys_atv_fanli_day_";//超级返利每天
    public static String sys_atv_fanli       			 =   "sys_atv_fanli_";//超级返利
    public static String sys_atv_pk_get_award             =   "sys_atv_pk_get_award_";//比赛赢奖励
    public static String sys_atv_score            	=   "sys_atv_score_";//活动积分 id = 2;
    public static String sys_atv_jq_score_obj		=	"sys_atv_jq_score_obj_";//
    public static String sys_atv_char_and_cons		=	"sys_atv_char_and_cons_";
    public static String sys_atv_pay_send_money_obj =	"sys_atv_pay_send_money_obj_";
    public static String sys_atv_active_gift_obj	=	"sys_atv_active_gift_obj_";
    public static String sys_atv_old_user_back_pay	=	"sys_atv_old_user_back_pay_";
    public static String sys_atv_merge_area_obj		=	"sys_atv_merge_area_obj_";
    public static String sys_atv_nqws_rank			=	"sys_atv_nqws_rank_";
    public static String sys_atv_hbyjx_obj			=	"sys_atv_hbyjx_obj_";
    public static String sys_atv_day_pay_obj		=	"sys_atv_day_pay_obj_";
    public static String sys_atv_day_pay_obj2		=	"sys_atv_day_pay_obj2_";
    public static String sys_atv_day_pay_obj2_count	=	"sys_atv_day_pay_obj2_count_";
    public static String sys_atv_day_recharge_obj	=	"sys_atv_day_recharge_obj_";
    public static String sys_atv_day_recharge_limit	=	"sys_atv_day_recharge_limit_";
    public static String sys_atv_qhyh_obj			=	"sys_atv_qhyh_obj_";
    public static String sys_atv_zdyjl_obj			=	"sys_atv_zdyjl_obj_";
    public static String sys_atv_zdyjl2_obj			=	"sys_atv_zdyjl2_obj_";
    public static String sys_atv_guess_riddles		=	"sys_atv_guess_riddles_";
    public static String sys_atv_online_gift_obj	=	"sys_atv_online_gift_obj_";
    public static String sys_atv_chest_key_obj		=	"sys_atv_chest_key_obj_";
    public static String sys_atv_mission_injuries_	=	"sys_atv_mission_injuries_";
    public static String sys_atv_lucky_guy_obj		=	"sys_atv_lucky_guy_obj_";
    public static String sys_atv_wish_tree_obj		=	"sys_atv_wish_tree_obj_";
    public static String sys_atv_wish_tree_rank		=	"sys_atv_wish_tree_rank_";
    public static String sys_atv_callup_friend_back =	"sys_atv_call_up_friend_back_";
    public static String sys_atv_callup_by_fri_back =	"sys_atv_callup_by_fri_back_";
    public static String sys_atv_first_pay_obj		=	"sys_atv_first_pay_obj_";
    public static String sys_atv_lucky_guy2_obj		=	"sys_atv_lucky_guy2_obj_";
    public static String sys_atv_lucky_star_obj		=	"sys_atv_lucky_star_obj_";
    public static String sys_atv_lucky_star_team	=	"sys_atv_lucky_star_team_";
    public static String sys_atv_lxld_obj			=	"sys_atv_lxld_obj_";
    public static String sys_atv_charge_lotte		=	"sys_atv_charge_lotte_";
    public static String sys_atv_limit_num			=	"sys_atv_limit_num_";
    public static String sys_atv_group_buy_list		=	"sys_atv_group_buy_list_";
    public static String sys_atv_laba_obj			=	"sys_atv_laba_obj_";
    public static String sys_atv_laba_luck_list		=	"sys_atv_laba_luck_list_";
    public static String sys_atv_xf_rank_obj		=	"sys_atv_xf_rank_obj_";
    public static String sys_atv_recharge_rank_obj	=	"sys_atv_recharge_rank_obj_";
    public static String sys_atv_dreambless			=	"sys_atv_dreambless_obj_";
    public static String sys_atv_bless				=	"sys_atv_bless_obj_";
    public static String sys_atv_rosecharm_obj		=	"sys_atv_rosecharm_obj_";
    public static String sys_atv_dzxy_obj			=	"sys_atv_dzxy_obj_";
    public static String sys_atv_signaward_obj		=	"sys_atv_signaward_obj_";
    public static String sys_atv_basket_obj			=	"sys_atv_basket_obj_";
    public static String sys_atv_sale_props_obj		=	"sys_atv_sale_props_obj_";
    public static String sys_atv_jf_award_obj		=	"sys_atv_jf_award_obj_";
    public static String sys_atv_active_award_obj	=	"sys_atv_active_award_obj_";
    public static String sys_atv_account_info		=	"sys_atv_account_info_";
    public static String sys_atv_honor_fight		=	"sys_atv_honor_fight_";
    public static String sys_atv_five_anni			=	"sys_atv_five_anni_";
    public static String sys_atv_rich_fruit			=	"sys_atv_rich_fruit_";
    public static String sys_atv_ladder_pk			=	"sys_atv_ladder_pk_";
    public static String sys_atv_pay_jf_obj			=	"sys_atv_pay_jf_obj_";
    public static String sys_atv_sign_player_obj	=	"sys_atv_sign_player_obj_";
    public static String sys_atv_challenge_peak_obj	=	"sys_atv_challenge_peak_obj_";
    public static String sys_atv_card_game_obj		=	"sys_atv_card_game_obj_";
    public static String sys_atv_mvp_obj			=	"sys_atv_mvp_obj_";
    public static String sys_atv_lucky_lottety_obj	=	"sys_atv_lucky_lottety_obj_";
    public static String sys_atv_addmoney_award_obj	=	"sys_atv_addmoney_award_obj_";
    public static String sys_atv_task_award_obj		=	"sys_atv_task_award_obj_xXx_";
    public static String sys_atv_word_card_obj		=	"sys_atv_word_card_obj_";
    
    public static String league_fight_info_pk_list	=	"league_fight_info_pk_list_";//本周所有联盟切磋赛列表
	public static String league_fight_signup_list	=	"league_fight_signup_list_";//报名列表
	public static String league_fight_rank_list		=	"league_fight_rank_list_";//排名列表
	public static String league_fight_team_object	=	"league_fight_team_object_";//盟内切磋赛球队对象

	public static String league_kick_rank			=	"league_kick_rank_";//联盟挑战赛球队排名
	public static String league_kick_pk_log			=	"league_kick_pk_log_";//联盟挑战赛联盟战报日志

	public static String halloween_pk  				=	"halloween_pk_";//万圣节比赛
	public static String halloween_mask_day   		= 	"halloween_mask_day_";//万圣节变脸
	public static String halloween_pk_count  		=	"halloween_pk_count_";//万圣节比赛

	public static String player_collect_list		=	"player_collect_list_";//球队球星收集列表
	public static String player_card_list			=	"player_card_list_";//球队球星卡列表

	public static String team_spetrain_boss_object	=	"team_spetrain_boss_object_";//球队特训BOSS对象

	public static String pk_analogy_object			=	"pk_analogy_object_";//模拟赛对象
	public static String pk_analogy_nba_sign_list	=	"pk_analogy_nba_sign_list_";//模拟赛NBA报名列表
	public static String pk_analogy_team_sign		=	"pk_analogy_team_sign_";//球队报名对象
	public static String pk_analogy_team_list		=	"pk_analogy_team_list_";//球队支持的NBA队列列表
	public static String pk_analogy_today_vs_list	=	"pk_analogy_today_vs_list_";//球队今日赛程
	public static String pk_analogy_plan_vs_list	=	"pk_analogy_plan_vs_list_";//球队排期赛程
	public static String pk_analogy_live_rank_list	=	"pk_analogy_live_rank_list_";//积分排名
	public static String pk_analogy_result_list		=	"pk_analogy_result_list_";//查询最近获得前几名的列表
	public static String pk_analogy_group_vs_list	=	"pk_analogy_group_vs_list_";//查询季后赛分组的赛程
	public static String pk_analogy_score_rank_list =	"pk_analogy_score_rank_list_";//选拨赛胜分排名
	public static String pk_analogy_all_result_list =	"pk_analogy_all_result_list_";//选拨赛冠亚军往届回顾列表

	public static String team_vip_day_reward		=	"team_vip_day_reward_";//VIP每日领取奖励
	public static String vip_shopping_list			=	"vip_shopping_list_";//VIP商城配置列表
	public static String team_vip_shopping_list		=	"team_vip_shopping_list_";//球队购买的商品列表
	public static String team_vip_sign_obj			=	"team_vip_sign_obj_";//VIP签到对象
	
	public static String pk_back_peak_team_object	=	"pk_back_peak_team_object_";//球队梦回巅峰对象
	public static String pk_back_peak_shard_rank	=	"pk_back_peak_shard_rank_";//梦回巅峰区排名
	public static String pk_back_peak_rank_list		=	"pk_back_peak_rank_list_";//梦回巅峰全服排名

	public static String stadium_build_list			=	"stadium_build_list_";//体育场建筑列表
	public static String stadium_cap_list			=	"stadium_cap_list_";//科技中心加成列表
	public static String pk_qualify_team_object		=	"pk_qualify_team_object_";//排位赛球队
	public static String pk_qualify_log_list		=	"pk_qualify_log_list_";//挑战战报列表
	public static String pk_qualify_vs_list			=	"pk_qualify_vs_list_";//对战列表
	public static String pk_qualify_result_list		=	"pk_qualify_result_list_";//季后赛排名

	public static String game_fund_config_list		=	"game_fund_config_list_";//得到基金配置列表
	public static String team_fund_object			=	"team_fund_object_";//得到球队存入基金列表

	public static String league_player_list			=	"league_player_list_";//联盟球员列表
	public static String league_player_cap_list		=	"league_player_cap_list_";//联盟球员Cap列表
	public static String league_player_equi_list	=	"league_player_equi_list_";//联盟装备列表
	public static String team_player_inherit_list 	=	"team_player_inherit_list_";//球队联盟球员继承列表

	public static String pk_all_star_team_object	=	"pk_all_star_team_object_";//球队全明星挑战赛对象

	public static String pk_club_matches_object		=	"pk_club_matches_object_";//俱乐部联赛对象
	public static String pk_club_team_ls_list		=	"pk_club_team_ls_list_";//俱乐部联赛球队列表
	public static String pk_club_ls_history_list	=	"pk_club_ls_history_list_";//俱乐部联赛上届冠亚晋军
	public static String pk_club_ls_result_list		=	"pk_club_ls_result_list_";//俱乐部联赛本届亚晋军
	public static String cross_club_result_list		=	"cross_club_result_list_";//
	
	public static String game_elite_pk_main			=	"game_elite_pk_main_";//跨服精英争霸赛赛事对象
	public static String game_elite_pk_result		=	"game_elite_pk_result_";//跨服精英争霸赛结果列表
	public static String game_elite_pk_jf_rank		=	"game_elite_pk_jf_rank_";//跨服精英争霸赛积分排行

	public static String team_merit_list			=	"team_merit_list_";//成就奖励对象

	public static String cross_pk_ladders_object    =   "cross_pk_ladders_object_";//跨服天梯赛赛事对象
	public static String cross_pk_ladders_next_obj	=	"cross_pk_ladders_next_obj_";//跨服下一个赛事对象

	public static String player_new_ballot_obj		=	"player_new_ballot_obj_";//球队新秀顺位抽签对象
	public static String player_new_card_list		=	"player_new_card_list_";//球队新秀顺位卡列表
	public static String player_new_team_list		=	"player_new_team_list_";//
	
	public static String game_sponsor_pk_log_list	=	"game_sponsor_pk_log_list_";//赞助商球队防守记录列表

	public static String pk_star_road_list          =   "pk_star_road_list_";//球星之路集合对象
	public static String league_mission_free        =   "league_mission_free_";//球星之路集合对象
	public static String team_specialize_obj   		=   "team_specialize_obj_";//专项特训数据
	public static String team_cap_obj   			=   "team_cap_obj_";//专项特训数据
	public static String team_specialize_ranklist   =   "team_specialize_rank_list";//专项特训排名榜
	public static String team_specialize_oldRanklist=   "team_specialize_oldRanklist";//专项特训排名榜
	
	public static String player_master_list	   		=   "player_master_list_";//球员专精
	

	public static String week_rankscore          	=   "week_rankscore_";//周最佳成绩
	public static String week_rank_stat          	=   "week_rank_stat_";//周最佳统计
	public static String week_rank_news          	=   "week_rank_news_";//周最佳新闻
	public static String week_rank_old_news      	=   "week_rank_old_news_";//周最佳新闻
	public static String pk_new_maze                =   "pk_new_maze_";//pk挑战迷宫
	public static String pk_new_maze_rank           =   "pk_new_maze_rank_";//pk挑战迷宫
	
	public static String pk_star_camp_obj			=	"pk_star_camp_obj_";//球星训练营对象
	
	public static String pk_mamba_sign_list			=	"pk_mamba_sign_list_";//挑战黑曼巴报名对象

	public static String player_run_history			=	"player_run_history_";//球员向前冲
	public static String player_run_gx_rank			=	"player_run_gx_rank_";//球员向前冲贡献排行榜
	
	public static String atv_summer_carnival_pk     =   "atv_summer_carnival_pk_";//暑期大狂欢  - 友谊永长存
	public static String atv_summer_carnival_active =   "atv_summer_carnival_active_";//暑期大狂欢  - 活跃有奖励

	public static String league_counterpart_num     =   "league_counterpart_num_";//联盟副本 - 开启副本次数
	public static String league_counterpart_pk_num  =   "league_counterpart_pk_num_";//联盟副本-球队前置副本挑战次数
	public static String atv_midautumn_dh_equi      =   "atv_midautumn_dh_equi_";//活动月饼兑换装备
	public static String atv_midautumn_pkwin_award  =   "atv_midautumn_pkwin_award_";//活动比赛赢奖
	public static String atv_midautumn_upgrade_jz   =   "atv_midautumn_upgrade_jz_";//戒指大优惠
	public static String atv_stage_recharge_atv     =   "atv_stage_recharge_atv_";//戒指大优惠
	public static String atv_nation2014_fireworks   =   "atv_nation2014_fireworks_";//烟花对象
	public static String atv_nation2014_word     	=   "atv_nation2014_word_";//自牌对象
    public static String atv_callFans     			=   "atv_callFans_";//自牌对象
	public static String atv_national_recharge_log  =   "atv_national_recharge_log_";//充值送戒指
    public static String atv_stadium_sale  			=   "atv_stadium_sale_";//球馆大优惠
    public static String atv_consume_lottery  		=   "atv_consume_lottery_";//消费抽奖
    public static String atv_skill_lottery  		=   "atv_skill_lottery_";//技能抽奖
    public static String atv_halloween_pk 			= 	"atv_halloween_pk_";//技能选择卡活动
    public static String atv_helloween_transfer 	= 	"atv_helloween_transfer_";// 装备转化
    public static String atv_singles_pk_1 			= 	"atv_singles_pk_1_";//即时pk发放奖励数量
    public static String atv_singles_pk_2 			= 	"atv_singles_pk_2_";//职业联赛发放奖励数量
    public static String atv_singles_login 			= 	"atv_singles_login_";//光棍节登录奖励
    public static String atv_singles_consumer 		= 	"atv_singles_consumer_";//光棍节消费送礼
    public static String atv_player_card_lott 		= 	"atv_player_card_lott_";//球星卡抽卡
    public static String atv_thankday_turkey 		= 	"atv_thankday_turkey_";//感恩节  火鸡兑奖励
    public static String atv_thankday_bless 		= 	"atv_thankday_bless_";//感恩节  祝福增实力
    public static String atv_recharge_fk 			= 	"atv_recharge_fk_";//感恩节  充值送球券
	public static String atv_thanks_mission 		= 	"atv_thanks_mission_";//感恩节2014 任务得奖励
	public static String atv_thanks_props_num 		= 	"atv_thanks_props_num_";//感恩节2014 道具数量
	public static String atv_single_status 			= 	"atv_single_status_";//单次领取,活动期间只领取一次
	public static String atv_anni_login 			= 	"atv_anni_login_";//2014周年庆活动 登录领奖
	public static String atv_christmas_gifts 		= 	"atv_christmas_gifts_";//2014圣诞节 
	public static String atv_christmas_gifts_list 	= 	"atv_christmas_gifts_list_";//2014圣诞节 
	public static String atv_new_yaer_pk 			= 	"atv_new_yaer_pk_";//2014圣诞节
	public static String atv_la_ba_1_obj			= 	"atv_la_ba_1_obj_";//腊八节活动-在线领奖励
	public static String atv_laba_pk_total 			= 	"atv_laba_pk_total_";//2014腊八 比赛总积分
	public static String atv_laba_pk_info 			= 	"atv_laba_pk_info_";//2014腊八每日积分
	public static String atv_la_ba_2_obj 			=	"atv_la_ba_2_obj_";//腊八节活动-球星总动员
	public static String atv_valantine_rose_info 	=	"atv_valantine_rose_info_";//2015情人节-玫瑰好礼
	public static String atv_valantine_rose_count 	=	"atv_valantine_rose_count_";//2015情人节-玫瑰数量
	public static String atv_valantine_bless_count 	=	"atv_valantine_bless_count_";//2015情人节-领取次数
	public static String atv_valantine_bless_free 	=	"atv_valantine_bless_free_";//2015情人节-免费次数
	public static String atv_valantine_bless_info 	=	"atv_valantine_bless_info_";//2015情人节-
	public static String atv_valantine_online_obj	=	"atv_valantine_online_obj_";//2015情人节-登录领奖励对象
	public static String atv_valantine_prop_obj		=	"atv_valantine_prop_obj_";//2015情人节-寻找玫瑰道具限量对象
	public static String atv_limit_prop_buy			=	"atv_limit_prop_buy_";//积分兑道具，限制数量对象
	public static String atv_peak_info				=	"atv_peak_info_";//春节活动信息
	public static String atv_peak_info_day			=	"atv_peak_info_day_";//春节每日信息
	public static String atv_recharge_single		=	"atv_recharge_single_";//一次性充值
	public static String atv_stadium_clear_info_day	=	"atv_stadium_clear_info_day_";//一次性充值
	public static String sys_atv_parkour			=	"sys_atv_parkour_";//大富翁
	public static String atv_charge_lotte2_obj		=	"atv_charge_lotte2_obj_";
	public static String atv_buy1give1_obj			=	"atv_buy1give1_obj_"; //买一送一限量
	public static String atv_equi_grade				=	"atv_equi_grade_";//装备强化进阶
	public static String pk_group_currLuckLogo		=	"pk_group_currLuckLogo_";//当前随机幸运号
	public static String pk_group_getAward			=	"pk_group_getAward_";//进阶石比赛冠军奖励,0:不能领取,1:可以领取,2:已领取
	public static String atv_jf_limit				=	"atv_jf_limit_";//积分兑道具，限制数量对象
	public static String player_potency_list		=	"player_potency_list_";//球员潜能列表
	public static String atv_equi_achi				=	"atv_equi_achi_";//装备强化成就
	public static String atv_new_area				=	"atv_new_area_";//新区活动
	public static String atv_login_online			=	"atv_login_online_";//登录在线有奖

	public static String getTime(){
		return UtilDateTime.toDateString(new Date(), UtilDateTime.YYYYMMDD)+"_";
	}

	public static String getTime(String time){
		return time.replace("-", "")+"_";
	}

	public static int getHour(){
		Calendar calendar  = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getMinute(){
		Calendar calendar  = Calendar.getInstance();
		return calendar.get(Calendar.MINUTE);
	}
	

	public static int getHour(Date date){
		Calendar calendar  = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static String getToday(Date date){
		return UtilDateTime.toDateString(date, UtilDateTime.YYYYMMDD);
	}

	public static void main(String[] args) {
		System.err.println(UtilDateTime.toDateString(new Date(), UtilDateTime.YYYYMMDD)+"_");
	}
}
