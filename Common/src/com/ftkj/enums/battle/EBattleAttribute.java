package com.ftkj.enums.battle;

/**
 * @author tim.huang
 * 2017年3月9日
 * 比赛额外参数
 */
public enum EBattleAttribute {
    比赛时间,
    Room_Id,

    街球赛挑战关卡,
    联盟球馆挑战类型,

    擂台赛房间,
    Before_Data,
    /** 主线赛程. 关卡配置id */
    Main_Match_LevRid,
    /** 主线赛程. 锦标赛时的防守方替身数据 */
    Main_Match_Away_Npc_Avatar,
    /** 是否通过gm命令触发了强制结束比赛 */
    GM_FORCE_END_MATCH,
    /** 是否通过gm命令触发了强制结束比赛, 并且主场胜利 */
    GM_FORCE_END_MATCH_HOME_WIN,

    /** 全明星. 配置id */
    All_Star_Rid,
    /** 全明星. 难度 */
    All_Star_Lev,
    /** 全明星. 比赛结束信息 */
    All_Star_Match_End,
    /** 全明星. 比赛初始化数据 */
    All_Star_Battle_Attr,
    加时,
    //    分差,
    比赛次数统计,
    NPCBUF,
    /** 主线赛程. 比赛结果 星级 */
    Main_Match_Star,
    /** 天梯赛 房间 */
    //    Ranked_Match_Room,
    /** 天梯赛 比赛结束信息 */
    Ranked_Match_End,

    /** 竞技场. 对手球队id */
    Arena_Target_Tid,
    /** 竞技场. 对手球队排名. 比赛结束后, 排名变化前的排名 */
    Arena_Target_Rank,
    /** 竞技场. 比赛结束信息 */
    Arena_Match_End,
    
    /** 新秀对抗赛. 比赛结束信息 */
    Starlet_Match_End,
    /** 球星荣耀关卡ID*/
    honorMatch_lv_id,
    
    ;
}
