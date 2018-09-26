package com.ftkj.server;

public class CrossCode {
    //注册节点到本地
    public static final int NodeManager_registerNode = 2000000;

    //跨服取战斗ID
    public static final int BattleManager_getBattleId = 2100001;

    //跨服取战斗ID多个
    public static final int BattleManager_getBattleIdList = 2100002;
    //回调注册节点
    public static final int NodeManager_callBackRegisterNode = 2100003;

    public static final int TeamManager_getTeamNodeInfo = 2100004;

    public static final int CDKeyManager_conver = 2100005;

    public static final int Team_Simple_Data = 2100006;

    public static final int Match_Force_End = 2100007;

    /**
     * 即时pk赛，匹配
     */
    public static final int CrossPVPManager_math = 2100102;
    public static final int CrossPVPManager_loadTeamSource = 2100103;
    public static final int CrossPVPManager_createRoom = 2100104;
    public static final int CrossPVPManager_againMatch = 2100105;
    public static final int CrossPVPManager_showRoom = 2100106;
    public static final int CrossPVPManager_updateRoomStatus = 2100107;
    public static final int CrossPVPManager_loadCustomTeamSource = 2100108;

    public static final int DraftManager_showDraftMain = 2100200;
    public static final int DraftManager_joinDraft = 2100201;
    public static final int DraftManager_showDraftRoomMain = 2100202;
    public static final int DraftManager_openCard = 2100203;
    public static final int DraftManager_signPlayer = 2100204;
    public static final int DraftManager_getDraftRoomList = 2100205;

    public static final int CrossCustomPVPManager_getCustomRoom = 2100220;
    public static final int CrossCustomPVPManager_createCustomRoom = 2100221;
    public static final int CrossCustomPVPManager_joinCustomRoom = 2100222;
    public static final int CrossCustomPVPManager_exitRoom = 2100223;
    public static final int CrossCustomPVPManager_closeRoom = 2100224;
    public static final int CrossCustomPVPManager_seachRoom = 2100225;
    public static final int CrossCustomPVPManager_seachConditionRoom = 2100226;
    public static final int CrossCustomPVPManager_startPK = 2100227;
    public static final int CrossCustomPVPManager_autoStart = 2100228;
    public static final int CrossCustomPVPManager_updateCustomMoneyCross = 2100229;
    public static final int CrossCustomPVPManager_getCustomGuessRoom = 2100230;
    public static final int CrossCustomPVPManager_getCustomMainByIds = 2100231;
    public static final int CrossCustomPVPManager_getCustomGuessInfo = 2100232;
    public static final int CrossCustomPVPManager_updateCustoGuessInfo = 2100233;
    public static final int CrossCustomPVPManager_topCloseGuess = 2100234;
    public static final int CrossCustomPVPManager_updatePKRoomInfo = 2100235;
    public static final int CrossCustomPVPManager_pkend = 2100236;
    public static final int CrossLimitChallengeUpdateNotice = 2100237;

    
    public static final int LocalDraftManager_tipDraft = 3100000;
    public static final int LocalDraftManager_tipDraftCard = 3100001;
    public static final int LocalDraftManager_tipDraftStage = 3100002;
    public static final int LocalDraftManager_tipDraftPlayer = 3100003;
    public static final int LocalDraftManager_autoSignPlayer = 3100004;
    public static final int LocalDraftManager_tipDraftEnd = 3100005;
    public static final int LocalDraftManager_roomTimeoutEnd = 3100006;

    public static final int LocalArenaManager_ranTeamArena = 3100020;
    public static final int LocalArenaManager_updateAttackArenaTeamGold = 3100021;
    public static final int LocalArenaManager_getTeamArena = 3100022;
    public static final int LocalArenaManager_updateStealArenaTeamGold = 3100023;
    public static final int LocalArenaManager_playerDie = 3100024;

    public static final int LocalCustomPVPManager_joinCustomRoomCallBack = 3100050;
    public static final int LocalCustomPVPManager_pushJoinCustomRoom = 3100051;
    public static final int LocalCustomPVPManager_pushExitCustomRoom = 3100052;
    public static final int LocalCustomPVPManager_pushCloseCustomRoom = 3100053;
    public static final int LocalCustomPVPManager_pushConditionCustomRoom = 3100054;
    public static final int LocalCustomPVPManager_pushCloseCustomGuess = 3100055;
    public static final int LocalCustomPVPManager_guessEnd = 3100056;

    public static final int LocalBattleManager_getBattleTeam = 3100100;
    public static final int GetBattleTeam = 3100102;

    public static final int BattlePVPManager_updateLocalBattleTeam = 3100040;
    public static final int BattlePVPManager_battleEnd = 3100041;

    public static final int PlayerBidManager_showBidBeforeMain = 3100200;
    //	public static final int PlayerBidManager_updatePlayerBidBeforeData = 3100201;
    public static final int PlayerBidManager_bidPlayer = 3100202;
    public static final int PlayerBidManager_initPlayerBidBeforeData = 3100203;

    public static final int LocalPlayerBidManagger_reloadBeforeMain = 3100250;
    public static final int localplayerbidmanagger_updateguessmain = 3100251;
    public static final int LocalPlayerBidManagger_endBid = 3100252;
    public static final int LocalGameManagger_limit_challenge_update = 3100253;

    //================================================================
    //  跨服天梯赛
    //================================================================
    /** 跨服天梯赛. 获取球队天梯赛信息 */
    public static final int XRanked_Info = 2137001;
    /** 跨服天梯赛. 开始比赛, 加入匹配池 */
    public static final int XRanked_Join_Pool = 2137003;
    /** 跨服天梯赛. 开启比赛推送 */
    public static final int XRaned_Start_Match_Push = 2137005;
    /** 跨服天梯赛. 中心服务器结束比赛 */
    public static final int XRaned_End_Match_Push = 2137007;
    /** 跨服天梯赛. 中心服务器发送赛季奖励排名奖励邮件 */
    public static final int XRaned_Send_Mail_Push = 2137009;
    /** 跨服天梯赛. 球队下线 */
    public static final int XRaned_Team_Offline = 2137011;
    /** 跨服天梯赛. 取消比赛, 离开匹配池 */
    public static final int XRanked_Cancel_Join = 2137013;

    /** 跨服天梯赛. 赛季历史信息 */
    public static final int XRaned_Season_His = 2137050;
    /** 跨服天梯赛. 段位排行榜 */
    public static final int XRaned_Rank = 2137051;
    /** 跨服天梯赛. 领取首次奖励 */
    public static final int XRaned_Receive_First_Award = 2137052;

    /** 跨服天梯赛. gm命令 刷新段位排名 */
    public static final int XRaned_Gm_Up_Rank = 2137080;
    /** 跨服天梯赛. gm命令 强制发放赛季奖励 */
    public static final int XRaned_Gm_Season_End_Award = 2137081;
    /** 跨服天梯赛. gm命令 修改评分 */
    public static final int XRaned_Gm_Up_Rating = 2137082;

    /** 重置多人赛*/
    public static final int WebManager_resetMatch = 7000001;

    /** 充值*/
    public static final int WebManager_addMoney = 8100001;
    /** 关服*/
    public static final int WebManager_closeServer = 8200002;
    /** 推送指定游戏节点的球员身价更新*/
    public static final int WebManager_reloadNBAData = 8200003;
    /** 推送指定游戏节点的球员身价更新*/
    public static final int WebManager_reloadAllNodeNBAData = 8200004;
    /** 推送所有游戏节点的球员身价更新*/
    public static final int WebManager_reloadAllNodeNBAPKData = 8200005;
    /** 球队状态设置(封号)*/
    public static final int WebManager_lockTeam = 8200006;
    /** 全服跑马灯*/
    public static final int WebManager_pushAllGame = 8200007;
    /** 发送全服邮件*/
    public static final int WebManager_sendEmail = 8200008;
    /** 全服世界聊天*/
    public static final int WebManager_chatController = 8200009;
    /** 发送竞猜活动奖励邮件*/
    public static final int WebManager_sendGameGuessEMail = 8200010;
    /** 更新竞猜活动配置数据*/
    public static final int WebManager_updateGameGuessData = 8200011;
    /** 客服回复玩家提的问题*/
    public static final int WebManager_customerResp = 8200012;
    /** 同步活动配置*/
    public static final int WebManager_syncAllSystemActive = 8300001;
    /** 批量修改活动时间*/
    public static final int WebManager_updateActiveTimeBatch = 8300002;
    /** 查询球队活动数据*/
    public static final int WebManager_queryActiveData = 8300003;
    /** 清空活动数据，新开活动，重开活动，限制一下，非结束活动，不能清*/
    public static final int WebManager_clearActiveData = 8300004;
    /** 清指定球队的活动数据*/
    public static final int WebManager_clearTeamActiveData = 8300005;
    /** 创建数据库表*/
    public static final int WebManager_createActiveDataTable = 8300006;
    /** 修改活动配置*/
    public static final int WebManager_updateActiveConfigBatch = 8300007;
    /** 清档*/
    public static final int WebManager_resetServerData = 9000000;

}
