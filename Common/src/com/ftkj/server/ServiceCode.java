package com.ftkj.server;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议接口，从10000开始
 *
 * @author tim.huang
 */
public class ServiceCode {

/*
通讯协议文档.xlsx
*/

    /**
     * 20000    玩家名称是否存在
     * 20031    交换阵容位置
     * 20010    球队升级推包
     * <p>
     * 球员仓库模块
     * 20004    解雇球员
     * 20005    仓库操作
     * 20006    仓库球员变动推包
     * 20008    仓库收集球星卡
     * <p>
     * 装备模块
     * 31002    升阶（进化）
     * 31003    强化
     * 31004    升级
     * 31005    转移
     * 31006    装备推送
     * 31007    转换推送
     * 31008    球衣进阶
     * 31009    升阶（同级刷新属性）
     * <p>
     * 头像模块
     * 31401    头像列表
     * 31402    更换头像
     * 31403    进阶点亮
     * 31404    等级转移
     * 31405    碎片合成
     * 31406    头像分解
     * 31407    四合一
     * 31408    单个头像推送
     * 31409    荣誉头像推送
     * <p>
     * 道具
     * 20020    使用
     * 20021    出售
     * 20025    兑换主界面
     * 20024    兑换
     * 21000    改名
     * <p>
     * 球探
     * 31201    主界面
     * 31202    签约
     * 31203    刷新
     * 31204    制作球星卡
     * 31205    制作球员之魂
     * 31206    显示招募界面
     * 31207    招募球员
     * 31210    球员兑换界面
     * 31211    兑换签约
     * 31212    刷新
     * 31213    兑换刷新推送
     * <p>
     * 待签
     * 31501    列表
     * 31502    放弃待签
     * 31503    待签签约
     * 31504    一键收集球星卡
     * 31505    制作驻守球员
     * 31506    一键回收
     * 31507    收集球星卡
     * 31508    待签批量制卡
     * <p>
     * 主线赛程
     * 31301    主界面
     * 31302    新的征程
     * 31303    挑战
     * <p>
     * 球星卡
     * 制作：31204,31504
     * 31601    一件转换
     * 31602    品质提升
     * 31603    自动收集后推包，变动包
     * 31604    自动添加卡库推包
     * 31601    主界面
     * 31602    合成
     * 31603    升星
     * <p>
     * 战术
     * 31650    战术列表
     * 31651    学习战术
     * 31652    战术升级
     * 31653    战术突破
     * 31654    设置默认
     * <p>
     * 选秀
     * 31700    选秀主界面显示
     * 31701    加入选秀房间
     * 31702    选秀房间内界面显示
     * 31703    等待玩家阶段房间界面
     * 31704    抽签阶段房间界面
     * 31705    选人阶段房间界面
     * 31706    打开抽签牌
     * 31707    签约球员
     * 31708    退出选秀列表界面
     * 31709    退出选秀房间
     * 100081    选秀主界面数据变动
     * 100082    选秀房间内准备界面数据变动
     * 100083    选秀房间内抽签阶段卡牌变动
     * 100084    选秀阶段变动
     * 100085    选秀选人阶段操作变动
     * <p>
     * 多人赛
     * 31671    主界面多人赛列表
     * 31672    主界面某场比赛推包
     * 31673    上届排名
     * 31674    历史最佳
     * 31675    退出主界面
     * 31676    进入详细界面
     * 31677    退出详细界面
     * 31678    对战列表某场比赛推送
     * 31679    报名
     * 31680    比赛结果
     * 31681    赛前通知
     * 31682    刷新宝箱
     * 31683    抢宝箱
     * 31684    抢宝箱推送
     * <p>
     * 联盟
     * 31800    创建联盟
     * 31801    显示联盟列表界面
     * 31802    申请加入联盟
     * 31803    显示玩家当前联盟主界面
     * 31804    显示玩家已申请加入联盟列表
     * 31805    显示玩家入盟邀请列表
     * 31806    取消申请联盟
     * 31807    接受联盟邀请
     * 31808    编辑联盟宣言
     * 31809    编辑联盟公告
     * 31810    显示联盟日志
     * 31811    显示申请联盟列表
     * 31812    退出联盟
     * 31813    踢出玩家
     * 31814    确认联盟申请
     * 31815    拒绝联盟申请
     * 31816    捐献球卷
     * 31817    显示联盟贡献列表
     * 31818    显示联盟成员列表
     * 31819    任命
     * 31820    显示联盟详细信息
     * 31821    修改联盟商城限制
     * 31822    邀请玩家加入联盟
     * 31823    接受玩家邀请
     * 100260    通知玩家被踢出联盟
     * 100261    通知玩家联盟职位变更
     * 100262    通知玩家加入联盟信息
     * 100263    邀请玩家推送信息
     * <p>
     * 31850    显示联盟成就界面
     * 31851    捐赠成就勋章
     * 31852    升级成就
     * 31853    激活成就
     * <p>
     * 31880    显示联盟日常任务列表
     * 31881    刷新任务
     * 31882    开启任务
     * 31883    捐献荣誉
     * 31884    领取任务奖励
     * 31885    捐献混合荣誉
     * <p>
     * 商城
     * 32000    显示联盟商城界面
     * 32001    购买联盟商城物品
     * 32002    显示球卷商城界面
     * 32003    购买球卷商城物品
     * <p>
     * 排行榜
     * 32101    显示球队等级排行榜
     * 32102    显示球队攻防排行榜
     * 32103    显示联盟等级排行榜
     * 32104    查询联盟排行榜联盟
     * <p>
     * 聊天
     * 32130    世界聊天
     * 100090    世界聊天内容推送
     * 32131    联盟聊天
     * 32133    私聊
     * 32134    获取玩家离线信息
     * <p>
     * 邮件
     * 31681    读取
     * 31682    领取附件
     * 31683    一件领取
     * 31684    删除
     * 31685    一键删除
     * 31686    新邮件推包
     * <p>
     * 好友
     * 32201    添加
     * 32202    删除
     * 32203    黑名单添加
     * 32204    黑名单删除
     * 32205    查询球队名称
     * 32206    好友推荐列表
     * 100093    好友数据推送
     * <p>
     * 任务
     * 32160    任务列表
     * 32161    领取任务奖励
     * 32162    开启日常任务
     * 32163    放弃任务
     * 32164    刷新任务
     * 100091    领取奖励日常任务推送
     * 100092    刷新日常任务列表推送
     * 32165    领取日常活跃度奖励
     * <p>
     * 球员
     * 20013    获得球员历史身价
     * <p>
     * 球队
     * 20070    修改赛前配置信息
     * 20071    获得赛前配置信息
     * 20072    购买工资帽
     * 20073    显示球队攻防
     * 20074    显示球队信息
     * 20007    仓库球员制作球员之魂
     * 20075    更新新手引导
     * 20077    查看球队信息
     * 20078    查看阵容明细
     * <p>
     * 比赛
     * 30015    修改赛前道具
     * 30016    修改赛前战术
     * 30006    移除比赛监听
     * 30018    使用教练技能
     * 30019    暂停比赛
     * 30020    快速结束比赛
     * 100281    教练技能使用推送
     * <p>
     * 自由交易
     * 33001    交易列表
     * 33002    上架
     * 33003    购买/下架
     * 33004    我的交易记录
     * 33005    指定球员出售记录
     * 33006    上架，下架推包
     * <p>
     * 球馆
     * 32220    显示球馆转盘主界面
     * 32221    显示球馆建筑
     * 32222    升级修复建筑
     * 32223    上阵守护球员
     * 32224    升级球馆
     * 32225    roll转盘
     * 32226    偷取玩家球馆
     * 32227    显示复仇列表
     * 32228    攻击球馆
     * 32229    显示玩家球馆信息并指定攻击目标
     * 32230    显示偷取的球馆信息
     * 32231    显示球员之魂列表
     * 100200    攻击球馆玩家信息推送
     * 100201    防守球馆消息推送
     * 100202    进攻球馆消息推送
     * 100203    偷取球馆玩家信息推送
     * 100204    防守球馆被偷取消息推送
     * 100205    偷取球馆消息推送
     * <p>
     * 训练馆
     * 32250    获取球队训练馆数据(主界面)
     * 32251    训练
     * 32252    抢夺
     * 32253    领取训练奖励
     * 32254    获取(刷新)训练馆数据(抢夺列表)
     * 32255    球队训练馆数据改变推送(主界面)
     * 32256    训练馆抢夺列表改变推送
     * 32257    获取训练馆枪夺记录信息
     * 32258    训练馆枪夺记录推送
     * <p>
     * 街球赛
     * 32211    主界面
     * 32212    挑战
     * 32213    扫荡
     * 32214    挑战
     * 32215    扫荡后推送
     * <p>
     * 联盟球馆赛
     * 31901    获取联盟塞馆数据
     * 31902    获取联盟赛历史排名数据
     * 31903    获取周贡献排名数据
     * 31904    获取联盟赛总积分排行
     * 31905    获取联盟赛球队积分排行
     * 31906    占领
     * 31907    挑战
     * 31908    加速
     * 31909    球馆状态变更推包
     * 319010   比赛结束推包
     * <p>
     * 擂台赛
     * 32280    擂台赛界面请求
     * 32281    擂台赛界面数据返回
     * 32282    创建房间
     * 32283    创建房间数据返回
     * 32284    加入房间
     * 32285    加入房间数据返回
     * 32286    打开房间注册监听
     * 32287    关闭房间移除监听
     * 32288    退出房间
     * 32289    房主关闭房间
     * 32290    查找房间
     * 32291    按条件查找房间
     * 32292    按条件查找房间数据返回
     * 32293    开启比赛
     * 32294    开启自动开启比赛
     * 32295    显示竞猜默认界面
     * 32296    竞猜默认界面数据返回
     * 32297    显示竞猜个人界面
     * 32298    竞猜个人界面数据返回
     * 32299    显示竞猜比赛界面
     * 32300    竞猜比赛界面数据返回
     * 32301    竞猜
     * 100230    加入房间推送
     * 100231    退出房间推送
     * 100232    房间详细信息推送
     * 100233    关闭房间推送
     * 100234    加入房间失败错误码推送
     * <p>
     * 教练
     * 32321    显示教练主界面
     * 32322    更改默认教练
     * <p>
     * 技能
     * 34120    技能界面显示
     * 34121    技能升级
     * 34122    技能装备
     * <p>
     * 签到
     * 33301    主界面
     * 33302    月签到
     * 33303    月补签
     * 33304    7天签到领奖
     * VIP
     * 34101    推送
     * 34102    购买专属礼包
     * 34103    VIP签到奖励
     * <p>
     * 活动-巨星之路
     * 40000    主界面
     * 40001    领奖
     * 40002    排名领奖
     * <p>
     * 活动
     * 39991    所有活动
     * 39992    活动状态变更推包，开始，结束
     * 39993    活动通用主界面
     * 39994    活动通用领奖1
     * 39995    活动通用领奖2，每天类型奖励
     * 39996    球券购买完成
     * 39997    活动数据主动推包
     * 39998    抽奖
     * 八天登录
     * 40010    八天登录福利领奖
     * 40011    八天登录主界面
     * 成长基金
     * 40020    成长基金领奖
     * 40021    主界面
     * 月卡周卡
     * 40025    主界面
     * 40026    领取
     * 每日充值特惠
     * 40030    主界面
     * 首冲双倍
     * 40031    主界面
     * 首冲是送礼包
     * 40040    主界面
     * 40041    领取奖励
     * <p>
     * 答题活动
     * 40050	显示玩家所有答题数据
     * 40051	答题	
     * <p>
     * 模拟充值接口
     * 89007    充值
     * 890010    充值成功推包
     * <p>
     * 球员升级
     * 34140    球员升级
     * 34141    球员生星
     * 100300    球员碎片推送
     * <p>
     * 球员训练
     * 列表
     * 34150    训练
     * 34151    推送
     * <p>
     * 34110    buff推送
     * 34115    红点推送
     * <p>
     * 球员图鉴
     * 35000    列表
     * 35001    转移
     * <p>
     * 目标提醒
     * 20014    推送列表
     * <p>
     * 球员投资
     * 34161    显示投资信息
     * 34162    购买球员
     * 34163    出售球员
     * 34164    购买最大上限
     * 34165    购买货币
     * 34166    提取货币
     * <p>
     * 球员天赋
     * 20017    刷新天赋
     * 20018    确认天赋
     * <p>
     * 球员竞猜
     * 20029    显示竞猜界面
     * 20030    竞猜球队
     * <p>
     * 球员竞价
     * 34200    显示竞价前置界面
     * 34201    显示竞价竞价主界面
     * 34202    显示竞价球员界面
     * 34203    竞价球员
     * 34204    显示竞价训练馆
     * 34205    升级竞价训练馆球员
     * 34206    领取训练馆球员
     * 34207    开启训练球员
     * <p>
     * 联盟战队赛
     * 34300    主界面
     * 34301    创建战队
     * 34302    解散
     * 34303    申请
     * 34304    同意/拒绝
     * 34305    退出
     * 34306    替换位置
     * 34307    任命队长
     * 34308    队员准备
     * 34309    开始比赛
     * 34310    入队申请列表
     * 34311    创建战队推送
     * 34312    踢出队伍
     * 34313    队伍人数变动
     * 34316    申请列表推送
     * 34317    任命队长推包
     * 34318    准备
     * 34319    战队状态
     * 34320    本盟战队
     * 34321    查看战队明细
     * 34322    战队排行
     * 34323    联盟排行
     * <p>
     * 切磋
     * 21101    发起切磋邀请
     * 21102    发起切磋推送
     * 21103    切磋请求处理
     * 21104    切磋信息处理推送
     * 21105    获取切磋信息
     */

    public static final int Login_Out = 10000;
    /** 推送 */
    public static final int Push_Window = 100000;
    /** 货币变动 */
    public static final int Push_Money = 100001;
    /** 球员变动 */
    public static final int Push_Player = 100002;

    /** 连接中断 */
    public static final int Close_Connect = 100003;

    /** 加载球员变动信息 */
    public static final int GameManager_loadNBAPlayer = 100010;
    public static final int GameManager_loadNBAPKMain = 100011;
    public static final int GameManager_loadNBAPKInfoMain = 100012;
    
    /** 选秀主界面数据变动*/
    public static final int Push_Draft_Main = 100081;
    /** 选秀房间内准备界面数据变动*/
    public static final int Push_Draft_Room_Read = 100082;
    /** 选秀房间内抽签阶段卡牌变动*/
    public static final int Push_Draft_Room_Order = 100083;
    /** 选秀阶段变动*/
    public static final int Push_Draft_Room_Stage = 100084;
    /** 选秀选人阶段操作变动*/
    public static final int Push_Draft_Room_Player = 100085;
    /** 选秀结束*/
    public static final int Push_Draft_Room_End = 100086;

    /** 聊天推送 */
    public static final int Push_Chat_World = 100090;

    /** 领取奖励日常任务推送 */
    public static final int Push_Task_Day_GetAward = 100091;
    /** 刷新日常任务列表推送 */
    public static final int Push_Task_Day_Refresh = 100092;

    /** 好友数据推送*/
    public static final int Push_Friend = 100093;
    /** 日常任务完成状态推送*/
    public static final int Push_Task_Day_Finish = 100094;
    /** 比赛结束 */
    /** 比赛结束推送*/
    public static final int Push_Battle_End = 100100;

    /** 攻击球馆玩家信息推送*/
    public static final int Push_Arena_Attack_Team = 100200;
    /** 防守球馆消息推送*/
    public static final int Push_Arena_Defend_Tip = 100201;
    /** 进攻球馆消息推送*/
    public static final int Push_Arena_Attack_Tip = 100202;
    /** 偷取球馆玩家信息推送*/
    public static final int Push_Arena_Steal_Team = 100203;
    /** 防守球馆被偷取消息推送*/
    public static final int Push_Arena_Steal_Tip = 100204;
    /** 偷取球馆消息推送*/
    public static final int Push_Arena_Steal_Attack_Tip = 100205;
    /** 比赛结束推送*/
    public static final int Push_Arena_Player_Tip = 100206;
    /** 比赛结束推送*/
    public static final int Push_Arena_Player_Die_Tip = 100207;

    /** 加入房间推送*/
    public static final int Push_Custom_Join_Room = 100230;
    /** 退出房间推送*/
    public static final int Push_Custom_Exit_Room = 100231;
    /** 房间详细信息推送*/
    public static final int Push_Custom_Room = 100232;
    /** 关闭房间推送送*/
    public static final int Push_Custom_Close_Room = 100233;
    /** 加入房间失败错误码推送*/
    public static final int Push_Custom_Condition_Room = 100234;
    /** 房间金钱改变推送*/
    public static final int Push_Custom_Money = 100235;
    /** 加入房间推送*/
    public static final int Push_Custom_Open_Room = 100236;

    /** 通知玩家被踢出联盟 */
    public static final int Push_League_Quit = 100260;
    /** 通知玩家联盟职位变更 */
    public static final int Push_League_Appoint = 100261;
    /** 通知玩家加入联盟信息 */
    public static final int Push_League_Join = 100262;
    /** 邀请玩家推送信息 */
    public static final int Push_League_Invite = 100263;
    /** 聯盟商城限制推送信息 */
    public static final int Push_League_Limit = 100264;
    /** 聯盟榮譽值更新推送信息 */
    public static final int Push_League_Honor_Update = 100265;
    /** 戰鬥技能推送信息 */
    public static final int Push_Battle_Skill = 100280;
    /** 使用教练技能 */
    public static final int Push_Battle_Coach = 100281;

    public static final int Push_Player_Star = 100300;
    public static final int Push_Price = 100301;

    public static final int Push_Player_Price = 100306;
    // public static final int Push_Battle_Skill_Use = 100281;

    /** 名称是否存在 */
    public static final int GameManager_checkTeamName = 20000;
    /** 随机球员 */
    public static final int GameManager_ranPlayer = 20001;
    /** 创建球队 */
    public static final int GameManager_createTeam = 20002;
    /** load玩家信息，主界面信息 */
    public static final int GameManager_loadData = 20003;
    /** 解雇球员 or 回收*/
    public static final int PlayerManager_Fire = 20004;
    /** 仓库操作 */
    public static final int Player_Tran_Store = 20005;
    /** 仓库球员变更 */
    public static final int Player_Storage_Change_Push = 20006;
    /** 仓库制作球星之魂 */
    public static final int PlayerManager_makeArenaPlayer = 20007;
    /** 仓库球员制卡 */
    public static final int Player_Storage_MakeCard = 20008;
    /**
     * 仓库球员删除
     */
    public static final int Player_Storage_del_push = 20009;

    /** 球队升级 */
    public static final int Team_Up_Level = 20010;
    /** 更换队徽 */
    public static final int Team_Change_Logo = 20011;
    /** 更换小秘书 */
    public static final int Team_Change_Sec = 20012;
    /** 获得球员历史身价 */
    public static final int PlayerManager_showPlayerMoneyList = 20013;

    /** 我的目标 */
    public static final int GameManager_topicTarget = 20014;
    public static final int GameManager_converCDKey = 20015;

    public static final int GameManager_showMaxPlayerAvgData = 20016;

    /** 刷新天赋 */
    public static final int PlayerManager_lockAbility = 20017;
    /** 确认天赋 */
    public static final int PlayerManager_confirmPlayerTalent = 20018;
    /** 取消天赋 */
    public static final int PlayerManager_cancelPlayerTalent = 20019;

    /** ------------------------------道具模块------------------------- */

    public static final int Prop_Use = 20020;
    public static final int Prop_Sale = 20021;
    public static final int Prop_Add = 20022;
    public static final int Prop_Change = 20023;
    public static final int PropManager_convertProp = 20024;
    public static final int PropManager_showConvertMain = 20025;
    public static final int PlayerManager_delPlayerAvg = 20026;
    public static final int PlayerManager_showPlayerAvg = 20027;
    
    /** 谈判 */
    public static final int PlayerManager_replacePlayerPrice = 20028;
    /**竞猜活动比赛数据*/
    public static final int PlayerManager_showNBABattleGuessListMain = 20029;
    /**竞猜球队*/
    public static final int PlayerManager_guessNBABattle = 20030;
    public static final int PropManager_showConvertItem = 20040;

    /**
     * 调整整容位置
     */
    public static final int Player_Tran_Position = 20031;
    /**双位置转换*/
    public static final int PlayerManager_updatePlayerPosition = 20032;

    public static final int NodeManager_getNodeToken = 20050;
    public static final int NodeManager_loginNode = 20051;
    public static final int NodeManager_loadServerTime = 20052;

    /** ---------------------------------球队------------------------- */
    /** 修改赛前配置信息 */
    public static final int TeamManger_updateTeamBattleConfig = 20070;
    /** 获得赛前配置信息 */
    public static final int TeamManger_showTeamBattleConfig = 20071;
    /** 购买工资帽 */
    public static final int TeamManger_buyTeamPriceMoney = 20072;
    /** 显示球队攻防 */
    public static final int TeamManger_showTeamAllAbility = 20073;
    /** 显示球队信息 */
    public static final int TeamManger_showTeamInfo = 20074;
    /** 更新新手引导 */
    public static final int TeamManger_updateHelp = 20075;
    /** 查看阵容明细 */
    public static final int TeamManger_buyLineupCount = 20076;
    /** 查看球队信息 */
    public static final int TeamManger_viewTeamInfo = 20077;
    /** 查看阵容明细 */
    public static final int TeamManger_viewPlayerDetail = 20078;
    /** 更换当家球星 */
    public static final int PlayerManager_changeXplayer = 20079;
    /** 降低球员工资 */
    public static final int PlayerManager_lowerPlayerPrice = 20080;
    /** 球队目标信息获取 */
    public static final int TeamManger_getTargetData = 20081;
    /** 新秀降薪*/
    public static final int PlayerManager_startletLowerSalary = 20082;
    
    /** 更改球队名称 */
    public static final int TeamManager_teamChangeName = 21000;
   
    /** gm命令 */
    public static final int GM = 20100;

    //
    //    去掉的协议
    //         30001
    //        30002
    //    废除的协议
    //      30010
    //        32302
    //        32303
    //    有新比赛推送
    //      30000
    //    由客户端判断是否正式开始比赛
    //            协议号你那边看看用哪个
    //    接受到新比赛推送
    //      30008
    //        30003

    /** 比赛开启 */
    public static final int Battle_Start_Push = 30001;
    /** 比赛信息. 加入监听 */
    public static final int Battle_All = 30000;
    //    /** 战斗阶段变换推送 */
    //    public static final int Battle_Stage_Push = 30001;
    //    /** 赛前阶段推送 */
    //    public static final int Battle_Before_Stage_Main_Push = 30002;
    /** 比赛信息 推送 */
    public static final int Battle_All_Push = 30003;
    /** 比赛回合数据推送 */
    public static final int Battle_Round_Push = 30004;
    /** 比赛结束推送 */
    public static final int Battle_PK_Stage_Round_Main_End = 30005;
    /** 移除比赛监听 */
    public static final int Battle_Remove_Listener = 30006;
    /**
     * 快速比赛，结束数据推送
     */
    public static final int Battle_PK_Quick_End = 30007;
    /** 赛前数据信息 */
    public static final int Battle_Before_Team_Data = 30008;
    public static final int Battle_showHalfTimeSource = 30009;
    //    /** 赛前阶段，准备操作 */
    //    public static final int Battle_PK_Ready = 30010;

    public static final int Battle_PK_pkUseProp = 30011;
    public static final int Battle_PK_pkUpdatePlayer = 30012;
    public static final int Battle_PK_pkUpdateTactics = 30013;
    /** 比赛数据统计 */
    public static final int Battle_PK_showPlayerSource = 30014;
    public static final int Battle_PK_updateBattleEquProp = 30015;
    public static final int Battle_PK_updateBattleEquTactics = 30016;
    public static final int Battle_PK_useSkill = 30017;
    public static final int Battle_PK_pkUseCoach = 30018;
    public static final int Battle_PK_pauseBattle = 30019;
    /** 快速结束比赛 */
    public static final int Battle_Quick_End = 30020;

    /**
     * 匹配比赛
     */
    public static final int BattlePVPManager_match = 30200;
    public static final int BattlePVPManager_showMain = 30201;

    /** ------------------------------装备接口------------------------- */
    /** 获取装备列表 */
    public static final int EquiManager_showEquiList = 31001;
    /** 升阶（进化） */
    public static final int EquiManager_upQuality = 31002;
    /** 强化 */
    public static final int EquiManager_upStrLv = 31003;
    /** 升级 */
    public static final int EquiManager_upLv = 31004;
    /** 转移 */
    public static final int EquiManager_equiTransfer = 31005;
    /** 装备推送 */
    public static final int EquiManager_Change_Topic = 31006;
    /** 转换推送 */
    public static final int EquiManager_Suit_Change_Topic = 31007;
    /** 球衣进阶 */
    public static final int EquiManager_upQuaClothes = 31008;
    /** 升阶（同级刷新属性） */
    public static final int EquiManager_refreshAttr = 31009;

    /** ----------------------------球探------------------------------------------ */

    /** 球探.主界面 */
    public static final int ScoutManager_showScoutMain = 31201;
    /** 球探.签约 */
    public static final int ScoutManager_signPlayer = 31202;
    /** 球探.刷新 */
    public static final int ScoutManager_refreshScout = 31203;
    /** 球探.制作球星卡 */
    public static final int ScoutManager_MakeCard = 31204;
    /** 球探.制作球员之魂 */
    public static final int ScoutManager_makeArenaPlayer = 31205;
    /** 球探.显示招募界面 */
    public static final int ScoutManager_showRollPlayer = 31206;
    /** 球探.招募球员 */
    public static final int ScoutManager_rollPlayer = 31207;
    /** 球探.球员兑换界面 */
    public static final int ExchangePlayerManager_showView = 31210;
    /** 球探.兑换签约 */
    public static final int ExchangePlayerManager_signPlayer = 31211;
    /** 球探.主动刷新 */
    public static final int ExchangePlayerManager_refresh = 31212;
    /** 球探.兑换刷新推送 */
    public static final int ExchangePlayerManager_pushlist = 31213;
    /** 球探.兑换刷新测试接口 */
    public static final int ExchangePlayerManager_testRefresh = 31214;

    /** 切磋.发起切磋 */
    public static final int FriendManager_InitiateCompareNotes = 21101;
    /** 切磋.发起切磋推送 */
    public static final int FriendManager_CompareNotesPush = 21102;
    /** 切磋.切磋信息处理 */
    public static final int FriendManager_CompareNotesDeal = 21103;
    /** 切磋.切磋信息处理推送 */
    public static final int FriendManager_CompareNotesDealPush = 21104;
    /** 切磋.切磋信息获取 */
    public static final int FriendManager_CompareNotesInfo = 21105;

    /**
     * 主线赛程
     */
    public static final int Main_Stage_show = 31301;
    public static final int Main_Stage_Next_Scene = 31302;
    public static final int Main_Stage_Fight = 31303;

    // 荣誉头像
    public static final int Player_Logo_List = 31401;
    public static final int Player_Logo_Change = 31402;
    public static final int Player_Logo_Farword = 31403;
    public static final int Player_Logo_Tran = 31404;
    public static final int Player_Logo_Comb = 31405;
    public static final int Player_Logo_Resolve = 31406;
    public static final int Player_Logo_Quality = 31407;
    public static final int Player_Logo_Add_Push = 31408;
    public static final int Player_Logo_Hero_Update = 31409;

    // 待签
    public static final int BeSignManager_List = 31501;
    public static final int BeSignManager_recycle = 31502;
    public static final int BeSignManager_Sign = 31503;//签约
    public static final int BeSignManager_oneKeyMakeCard = 31504;
    public static final int BeSignManager_makeArenaPlayer = 31505;
    public static final int BeSignManager_recycleAll = 31506;
    public static final int BeSignManager_makeCard = 31507;
    public static final int BeSignManager_makeCardBatch = 31508;

    // 球星卡
    public static final int PlayerCardManager_View = 31601;
    public static final int PlayerCardManager_composite = 31602;
    public static final int PlayerCardManager_upStarLv = 31603;
    /**突破*/
    public static final int PlayerCardManager_breakup = 31604;
    /**突破吞噬底薪球员*/
    public static final int PlayerCardManager_breakupCostPlayers = 31605;

    /** 战术列表 */
    public static final int Tactics_List = 31650;
    /** 学习战术 */
    public static final int Tactics_Study = 31651;
    /** 战术升级 */
    public static final int Tactics_Up = 31652;
    /** 战术突破 */
    public static final int Tactics_Buff = 31653;
    /** 战术默认设置 */
    public static final int Tactics_Setting = 31654;

    /** 多人赛列表，主界面 */
    public static final int Match_List = 31671;
    /** 主界面某场比赛推包 */
    public static final int Match_Topic = 31672;
    /** 上届排名 */
    public static final int Match_Last_Rank = 31673;
    /** 历史最佳 */
    public static final int Match_History_Best = 31674;
    /** 退出主界面 */
    public static final int Match_Main_View_Exit = 31675;
    /** 进入详细界面 */
    public static final int Match_Detai_View_Join = 31676;
    /** 退出详细界面 */
    public static final int Match_Detail_View_Exit = 31677;
    /** 详细对战列表推送 */
    public static final int Match_Detail_Topic = 31678;
    /** 报名 */
    public static final int Match_Sign = 31679;
    /** 比赛结果 */
    public static final int Match_Report = 31680;
    /** 赛前通知 */
    public static final int Match_Before_Topic = 31681;
    /** 刷新宝箱 */
    public static final int Match_Boxes_Topic = 31682;
    /** 抢宝箱 */
    public static final int Match_Rob_Boxes = 31683;
    /** 抢宝箱推送 */
    public static final int Match_Rob_Boxes_Topic = 31684;

    /**
     * 发送邮件
     */
    public static final int Email_List = 32680;
    public static final int Email_Read = 32681;
    public static final int Email_Receive = 32682;
    public static final int Email_Receive_All = 32683;
    public static final int Email_Delete = 32684;
    public static final int Email_Delete_All = 32685;
    // 新邮件推
    public static final int Email_New_Topic = 32686;
    // 发邮件，测试接口
    public static final int Email_Send = 32687;

    /**
     * 交易
     */
    public static final int TradeManager_QueryTradeList = 33001;
    public static final int TradeManager_SellPlayer = 33002;
    public static final int TradeManager_BuyPlayer = 33003;
    public static final int TradeManager_MyTranList = 33004;
    public static final int TradeManager_PlayerTradeHist = 33005;
    /**
     * 推送单个交易结果或者下架
     */
    public static final int TradeManager_push_Trade = 33006;
    public static final int TradeManager_getHelpPlayer = 33007;
    public static final int TradeManager_buyHelpPlayer = 33008;
    /** 获取交易留言板信息*/
    public static final int TradeManager_getTradeMsgList = 33009;  
    /** 交易留言*/
    public static final int TradeManager_levelMessage =33010;   
    //-----求购交易
    public static final int TradeP2PManager_addBuying =33020;   
    public static final int TradeP2PManager_buying_push =33021;   
    public static final int TradeP2PManager_trade_list =33022;   
    public static final int TradeP2PManager_myBuying =33023;   
    public static final int TradeP2PManager_delBuying =33024;   
    public static final int TradeP2PManager_sellPlayer =33025;   
    public static final int TradeP2PManager_sellHistory =33026;   


    public static final int LocalDraftManager_showDraftMain = 31700;
    public static final int LocalDraftManager_joinDraft = 31701;
    public static final int LocalDraftManager_showDraftRoomMain = 31702;
    public static final int LocalDraftManager_showDraftRoomReadyMain = 31703;
    public static final int LocalDraftManager_showDraftRoomOrderMain = 31704;
    public static final int LocalDraftManager_showDraftRoomPlayerMain = 31705;
    public static final int LocalDraftManager_opendCard = 31706;
    public static final int LocalDraftManager_signPlayer = 31707;
    public static final int LocalDraftManager_quitDraftMain = 31708;
    public static final int LocalDraftManager_quitDraftRoom = 31709;
    /** 签约新手引导球员 */
    public static final int LocalDraftManager_signHelpPlayer = 31710;
    public static final int LocalDraftManager_showHelpPlayers = 31711;
    /** 当前轮到玩家选秀提示消息*/
    public static final int LocalDraftManager_currPlayerDraftTip = 31712;

    /** 创建联盟 */
    public static final int LeagueManager_createLeague = 31800;
    /** 显示联盟列表界面 */
    public static final int LeagueManager_showLeagueListMain = 31801;
    /** 申请加入联盟 */
    public static final int LeagueManager_aceeptJoinLeague = 31802;
    /** 显示玩家当前联盟主界面 */
    public static final int LeagueManager_showLeagueMain = 31803;
    /** 显示玩家已申请加入联盟列表 */
    public static final int LeagueManager_showJoinLeaugeMain = 31804;
    /** 显示玩家入盟邀请列表 */
    public static final int LeagueManager_showLeagueInviteMain = 31805;
    /** 取消申请联盟 */
    public static final int LeagueManager_cancelJoinLeague = 31806;
    /** 接受联盟邀请 */
    public static final int LeagueManager_acceptLeagueInvite = 31807;
    /** 编辑联盟宣言 */
    public static final int LeagueManager_editLeagueTip = 31808;
    /** 编辑联盟公告 */
    public static final int LeagueManager_editLeagueNotice = 31809;
    /** 显示联盟日志 */
    public static final int LeagueManager_showLeagueLog = 31810;
    /** 显示申请联盟列表 */
    public static final int LeagueManager_showLeagueAceeptTeamMain = 31811;
    /** 退出联盟 */
    public static final int LeagueManager_quitLeague = 31812;
    /** 踢出玩家 */
    public static final int LeagueManager_kickTeam = 31813;
    /** 确认联盟申请 */
    public static final int LeagueManager_confirmLeagueAccept = 31814;
    /** 拒绝联盟申请 */
    public static final int LeagueManager_refuseLeagueAceept = 31815;
    /** 捐献球卷 */
    public static final int LeagueManager_donateMoney = 31816;
    /** 显示联盟贡献列表 */
    public static final int LeagueManager_showDonateLogMain = 31817;
    /** 显示联盟成员列表 */
    public static final int LeagueManager_showLeagueTeamListMain = 31818;
    /** 任命 */
    public static final int LeagueManager_appoint = 31819;
    /** 显示联盟详细信息 */
    public static final int LeagueManager_showLeagueInfo = 31820;
    /** 修改联盟商城限制 */
    public static final int LeagueManager_updateLeagueLimit = 31821;
    /** 邀请玩家加入联盟 */
    public static final int LeagueManager_inviteTeam = 31822;
    /** 接受玩家邀请 */
    public static final int LeagueManager_acceptLeague = 31823;
    /** 转让盟主 */
    public static final int LeagueManager_appointLeader = 31824;

    /** 显示联盟成就界面 */
    public static final int LeagueHonorManager_showHonorMain = 31850;
    /** 捐赠成就勋章 */
    public static final int LeagueHonorManager_appoint = 31851;
    /** 升级成就 */
    public static final int LeagueHonorManager_levelUp = 31852;
    /** 激活成就 */
    public static final int LeagueHonorManager_activate = 31853;
    /** 联盟成就捐赠推送联盟等级,经验,每日贡献变化消息 */
    public static final int LeagueHonorManager_push_msg = 31854;
    /** 领取联盟成就每日捐赠达到固定贡献值奖励 */
    public static final int LeagueHonorManager_get_reward = 31855;

    /** 显示联盟日常任务列表 */
    public static final int LeagueTaskManager_showLeagueTaskMain = 31880;
    public static final int LeagueTaskManager_refreshTask = 31881;
    public static final int LeagueTaskManager_openTask = 31882;
    public static final int LeagueTaskManager_appointTask = 31883;
    public static final int LeagueTaskManager_getTaskGift = 31884;
    public static final int LeagueTaskManager_appointSBTask = 31885;

    /** ***********************************联盟球馆赛 ********************************************** */
    /** 获取联盟赛馆占位数据 */
    public static final int LeagueArenaManager_GetLeaguePostions = 31901;
    /** 获取联盟赛历史排名数据 */
    public static final int LeagueArenaManager_GetLeagueHistoryRanks = 31902;
    /** 获取周贡献排名数据 */
    public static final int LeagueArenaManager_GetLeagueScoreRanks = 31903;
    /** 获取联盟赛总积分排行 */
    public static final int LeagueArenaManager_GetLeagueMatchRanks = 31904;
    /** 获取联盟赛球队积分排行 */
    public static final int LeagueArenaManager_GetLeagueMatchTeamRanks = 31905;
    /** 占领 */
    public static final int LeagueArenaManager_occupy = 31906;
    /** 挑战 */
    public static final int LeagueArenaManager_challenge = 31907;
    /** 加速 */
    public static final int LeagueArenaManager_speed_up = 31908;
    /** 获取球队比赛数据 */
    public static final int LeagueArenaManager_getLeagueMatchTeamData = 31909;
    /** 放弃占领 */
    public static final int LeagueArenaManager_give_up_occupy = 31910;
    /** 联盟赛馆数据推包*/
    public static final int LeagueArenaManager_push_leaguepostions = 31911;
    /** 联盟赛历史排名数据推包 */
    public static final int LeagueArenaManager_push_leagueHistoryRanks = 31912;
    /** 周贡献排名数据数据推包 */
    public static final int LeagueArenaManager_push_leagueScoreRanks = 31913;
    /** 联盟赛总积分排行推包 */
    public static final int LeagueArenaManager_push_leagueMatchRanks = 31914;
    /** 联盟赛球队胜利次数排行推包 */
    public static final int LeagueArenaManager_push_leagueMatchTeamRanks = 31915;
    /** 联盟赛球比赛数据推包 */
    public static final int LeagueArenaManager_push_leagueMatchTeam = 31916;
    /** 联盟赛挑战状态推送 */
    public static final int LeagueArenaManager_push_leaguePostionChallangeState = 31917;

    public static final int ShopManager_showLeagueShop = 32000;
    public static final int ShopManager_buyLeagueProp = 32001;
    public static final int ShopManager_showMoneyShop = 32002;
    public static final int ShopManager_buyMoneyProp = 32003;
    public static final int ShopManager_showBDMoneyShop = 32004;
    public static final int ShopManager_buyBDMoneyProp = 32005;

    public static final int RankManager_showTeamLevelRank = 32101;
    /** 玩家战力排行榜 */
    public static final int Rank_Team_Cap_Rank = 32102;
    public static final int RankManager_showLeagueRank = 32103;
    public static final int RankManager_seachLeagueRank = 32104;

    /** 世界聊天 */
    public static final int ChatManager_chatWorld = 32130;
    /** 联盟聊天 */
    public static final int ChatManager_chatLeague = 32131;
    /** 私聊 */
    public static final int ChatManager_chatPrivate = 32133;
    /** 获取玩家离线信息 */
    public static final int ChatManager_chatOffline = 32134;
    /** 玩家离线信息以读取 */
    public static final int ChatManager_chatMsgIsRead = 32135;
    /** 推送世界聊天 */
    public static final int Push_Window_All = 100305;

    /** 任务列表 */
    public static final int TaskManager_showTaskMain = 32160;
    /** 领取任务奖励 */
    public static final int TaskManager_getTaskAward = 32161;
    /** 开启日常任务 */
    public static final int TaskManager_openTaskDay = 32162;
    /** 放弃任务 */
    public static final int TaskManager_canceTaskDay = 32163;
    /** 刷新任务 */
    public static final int TaskManager_refreshTaskDay = 32164;
    /** 领取日常星级奖励 */
    public static final int TaskManager_getTaskStarAward = 32165; 

    // 好友
    public static final int Friend_nice_add = 32201;
    public static final int Friend_nice_del = 32202;
    public static final int Friend_balck_add = 32203;
    public static final int Friend_balck_del = 32204;
    public static final int Friend_nice_query = 32205;
    public static final int Friend_recommend = 32206;
    public static final int FriendManager_showOnlineFriend = 32207;

    // 街球
    public static final int StreetBallManager_showView = 32211;
    public static final int StreetBallManager_challenge = 32212;
    public static final int StreetBallManager_sweep = 32213;
    public static final int StreetBallManager_endPK_push = 32214;
    public static final int StreetBallManager_sweep_push = 32215;

    public static final int ArenaManager_showArenaMain = 32220;
    public static final int ArenaManager_showArenaConstructionMain = 32221;
    public static final int ArenaManager_levelupConstruction = 32222;
    public static final int ArenaManager_lineupPlayer = 32223;
    public static final int ArenaManager_levelupArena = 32224;
    public static final int ArenaManager_roll = 32225;
    public static final int ArenaManager_stealArena = 32226;
    public static final int ArenaManager_showVengeanceMain = 32227;
    public static final int ArenaManager_attackArena = 32228;
    public static final int ArenaManager_showAttackArena = 32229;
    public static final int ArenaManager_showStealArena = 32230;
    public static final int ArenaManager_showArenaPlayerTeamMain = 32231;
    public static final int ArenaManager_buyPower = 32232;
  
    /** 擂台赛界面请求 */
    public static final int LocalCustomPVPManager_showCustomMain = 32280;
    /** 擂台赛界面数据返回 */
    public static final int LocalCustomPVPManager_callBackShowCustomMain = 32281;
    /** 创建房间 */
    public static final int LocalCustomPVPManager_createCustomRoom = 32282;
    /** 创建房间数据返回 */
    public static final int LocalCustomPVPManager_callBackCreateCustomRoom = 32283;
    /** 加入房间 */ 
    public static final int LocalCustomPVPManager_joinCustomRoom = 32284;
    /** 加入房间数据返回 */
    public static final int LocalCustomPVPManager_callBackJoinCustomRoom = 32285;
    /** 打开房间注册监听*/
    public static final int LocalCustomPVPManager_openRoom = 32286;
    /** 关闭房间移除监听*/
    public static final int LocalCustomPVPManager_quitRoom = 32287;
    /** 退出房间 */
    public static final int LocalCustomPVPManager_exitRoom = 32288;
    /** 房主关闭房间*/
    public static final int LocalCustomPVPManager_closeRoom = 32289;
    /** 查找房间 */
    public static final int LocalCustomPVPManager_seachRoom = 32290;
    /** 按条件查找房间*/
    public static final int LocalCustomPVPManager_seachConditionRoom = 32291;
    /** 按条件查找房间数据返回*/
    public static final int LocalCustomPVPManager_seachConditionRoomCallBack = 32292;
    /** 开启比赛*/
    public static final int LocalCustomPVPManager_startPK = 32293;
    /** 开启自动开启比赛 */
    public static final int LocalCustomPVPManager_autoStart = 32294;

    /** 显示竞猜默认界面*/
    public static final int LocalCustomPVPManager_showCustomGuessMain = 32295;
    /** 竞猜默认界面数据返回*/
    public static final int LocalCustomPVPManager_callBackShowCustomGuessMain = 32296;
    /** 显示竞猜个人界面*/
    public static final int LocalCustomPVPManager_showGustomGuessMyMain = 32297;
    /** 竞猜个人界面数据返回*/
    public static final int LocalCustomPVPManager_callBackShowGustomGuessMyMain = 32298;
    /** 显示竞猜比赛界面*/
    public static final int LocalCustomPVPManager_showGustomGuessPKMain = 32299;
    /** 竞猜比赛界面数据返回*/
    public static final int LocalCustomPVPManager_callBackShowGustomGuessPKMain = 32300;
    /** 竞猜*/
    public static final int LocalCustomPVPManager_guess = 32301;
    //    public static final int LocalCustomPVPManager_showGustomGuessPKMain2 = 32302;
    //    public static final int LocalCustomPVPManager_callBackShowGustomGuessPKMain2 = 32303;
    public static final int LocalCustomPVPManager_getCustomMoney = 32304;

    /** 显示教练主界面 */
    public static final int CoachManager_showCoachMain = 32321;
    /** 更改默认教练 */
    public static final int CoachManager_signCoach = 32322;

    // 签到
    public static final int SignManager_getTeamSignData = 33301;
    public static final int SignManager_signMonth = 33302;
    public static final int SignManager_signMonthPatch = 33303;
    public static final int SignManager_signPeriod = 33304;

    // VIP
    /** 推送 */
    public static final int VipManager_push_vip = 34101;
    /** 购买专属礼包 */
    public static final int VipManager_buy_gift = 34102;
    /** VIP签到奖励 */
    public static final int VipManager_sign_gift = 34103;

    // Buff推送
    public static final int BuffManager_push_buff = 34110;
    public static final int BuffManager_buff_list = 34111;// 红点提示变更的推送
    public static final int RedPointManager_push_Change = 34115;//红点提示推送
    public static final int RedPointManager_push_All = 34116;
    public static final int RedPointManager_login = 34117;

    public static final int SkillManager_showSkillMain = 34120;
    public static final int SkillManager_levelUp = 34121;
    public static final int SkillManager_equPlayerSkill = 34122;

    public static final int PlayerGradeManager_levelUP = 34140;
    public static final int PlayerGradeManager_levelupStar = 34141;

    // 联盟战队赛
    public static final int LeagueGroupWarManager_getGroupWarMain = 34300;
    public static final int LeagueGroupWarManager_createGroup = 34301;
    public static final int LeagueGroupWarManager_dissolveGroup = 34302;
    public static final int LeagueGroupWarManager_joinApply = 34303;
    public static final int LeagueGroupWarManager_accept = 34304;
    public static final int LeagueGroupWarManager_exit = 34305;
    public static final int LeagueGroupWarManager_exchangePos = 34306;
    public static final int LeagueGroupWarManager_appointLeader = 34307;
    public static final int LeagueGroupWarManager_ready = 34308;
    public static final int LeagueGroupWarManager_startMatch = 34309;
    public static final int LeagueGroupWarManager_getGroupApplyList = 34310;
    public static final int LeagueGroupWarManager_push_createGroup = 34311;
    public static final int LeagueGroupWarManager_kickout = 34312;
    //
    public static final int LeagueGroupWarManager_push_groupDetail = 34313;
    public static final int LeagueGroupWarManager_push_applyList = 34316;
    public static final int LeagueGroupWarManager_push_leaderTeam = 34317;
    public static final int LeagueGroupWarManager_push_ready = 34318;
    public static final int LeagueGroupWarManager_push_groupStatus = 34319;
    public static final int LeagueGroupWarManager_getLeagueGroupMain = 34320;
    public static final int LeagueGroupWarManager_getGruopTeamDetail = 34321;
    public static final int LeagueGroupWarManager_showGroupRank = 34322;
    public static final int LeagueGroupWarManager_showLeagueRank = 34323;
    public static final int LeagueGroupWarManager_push_match_detail = 34324;
    public static final int LeagueGroupWarManager_push_match_report = 34325;
    public static final int LeagueGroupWarManager_get_day_award = 34326;

    /****************************************训练馆*****************************************************/
    
    /**  获取球队训练馆数据(主界面)*/
    public static final int TrainManager_GetTeamTrainData = 32250;   
    /**  训练*/
    public static final int TrainManager_Training = 32251;    
    /**  抢夺*/
    public static final int TrainManager_TrainGrab = 32252;   
    /**  领取训练奖励*/
    public static final int TrainManager_GetTrainReward = 32253;    
    /**  获取(刷新)训练馆数据(抢夺列表)*/
    public static final int TrainManager_RefrushTrainList = 32254;    
    /** 球队训练馆数据改变推送(主界面)*/
    public static final int TrainManager_Push_TeamTrainData = 32255;
    /** 训练馆抢夺列表改变推送*/
    public static final int TrainManager_Push_ChangeTrainData = 32256; 
    /** 获取训练馆枪夺记录信息*/
    public static final int TrainManager_GetTrainGrabInfoList = 32257; 
    /** 训练馆枪夺记录推送*/
    public static final int TrainManager_Push_TrainGrabInfo = 32258; 
    /** 训练馆次数信息改变推送*/
    public static final int TrainManager_Push_ChangeTeamTrainData = 32259;    
    
    /** 联盟训练馆.获取主界面数据 */
    public static final int TrainManager_getLeagueTrainData = 32260;
    /** 联盟训练馆.选择球队 */
    public static final int TrainManager_choiseTeam = 32262;    
    /** 联盟训练馆.获取主界面数据回包 */
    public static final int TrainManager_push_getLeagueTrainData = 32261;
    /** 联盟训练馆.选择球队回包 */
    public static final int TrainManager_push_choiseTeam = 32263;

    public static final int PlayerInvestmentManager_showPlayerInvestmentMain = 34161;
    public static final int PlayerInvestmentManager_buyPlayer = 34162;
    public static final int PlayerInvestmentManager_salePlayer = 34163;
    public static final int PlayerInvestmentManager_buyMaxTotal = 34164;
    public static final int PlayerInvestmentManager_buyMoney = 34165;
    public static final int PlayerInvestmentManager_getMoney = 34166;

    public static final int LocalPlayerBidManager_showPlayerBidGuessBeforeMain = 34200;
    public static final int LocalPlayerBidManager_showPlayerBidGuessMain = 34201;
    public static final int LocalPlayerBidManager_showPlayerBidGuessPlayerMain = 34202;
    public static final int LocalPlayerBidManager_bidPlayer = 34203;
    public static final int LocalPlayerBidManager_showPlayerBidTrainMain = 34204;
    public static final int LocalPlayerBidManager_levelUpPlayer = 34205;
    public static final int LocalPlayerBidManager_getBidPlayer = 34206;
    public static final int LocalPlayerBidManager_startTrainPlayer = 34207;
    public static final int LocalPlayerBidManager_Info = 34208;

    // 球员图鉴
    public static final int PlayerArchiveManager_showPlayerList = 35000;
    public static final int PlayerArchiveManager_transPlayer = 35001;
    // 跑马灯推送
    public static final int RollTip_Msg_Push = 35100;
    // ================================================================
    // 主线赛程
    // ================================================================
    /** 主线赛程. 赛程信息 */
    public static final int MMatch_Info = 36000;
    /** 主线赛程. 赛程信息推送 */
    public static final int MMatch_Info_Push = 36001;
    /** 主线赛程. 购买挑战次数 */
    public static final int MMatch_Buy_Match_Num = 36003;
    /** 主线赛程. 购买挑战次数成功推送 */
    public static final int MMatch_Buy_Match_Num_Push = 36004;
    /** 主线赛程. 领取星级礼包 */
    public static final int MMatch_Receive_Star_Award = 36005;
    /** 主线赛程. 领取星级礼包推送 */
    public static final int MMatch_Receive_Star_Award_Push = 36006;
    /** 主线赛程. 开始比赛 */
    public static final int MMatch_Start_Match = 36007;
    /** 主线赛程. 扫荡 */
    public static final int MMatch_Quick_Match = 36009;
    /** 主线赛程. 扫荡推送 */
    public static final int MMatch_Quick_Match_Push = 36010;
    /** 主线赛程. 获取锦标赛信息 */
    public static final int MMatch_Championship_Info = 36011;
    /** 主线赛程. 获取锦标赛信息推送 */
    public static final int MMatch_Championship_Info_Push = 36012;
    /** 主线赛程. 获得的装备模块的经验推送 */
    public static final int MMatch_Equip_Exp_Push = 36020;
    /** 主线赛程. 比赛获胜推送 */
    public static final int MMatch_Match_Win_Push = 36022;
    // ================================================================
    // 跨服天梯赛
    // ================================================================
    /** 跨服天梯赛. 获取天梯赛信息 */
    public static final int RMatch_Info = 37000;
    /** 跨服天梯赛. 天梯赛信息 推送 */
    public static final int RMatch_Info_Push = 37001;
    /** 跨服天梯赛. 赛季历史信息 */
    public static final int RMatch_Season_His = 37002;
    /** 跨服天梯赛. 赛季历史信息 推送 */
    public static final int RMatch_Season_His_Push = 37003;
    /** 跨服天梯赛. 段位排行榜 */
    public static final int RMatch_Rank = 37004;
    /** 跨服天梯赛. 段位排行榜 推送 */
    public static final int RMatch_Rank_Push = 37005;
    /** 跨服天梯赛. 比赛历史信息 */
    public static final int RMatch_Match_His = 37006;
    /** 跨服天梯赛. 比赛历史信息 推送 */
    public static final int RMatch_Match_His_Push = 37007;

    /** 跨服天梯赛. 开始比赛 */
    public static final int RMatch_Join_Pool = 37022;
    /** 跨服天梯赛. 取消加入 */
    public static final int RMatch_Cancel_Join = 37024;

    /** 跨服天梯赛. 领取每日奖励 */
    public static final int RMatch_Receive_Daily_Award = 37040;
    /** 跨服天梯赛. 领取每日奖励 推送 */
    public static final int RMatch_Receive_Daily_Award_Push = 37041;
    /** 跨服天梯赛. 领取首次奖励 */
    public static final int RMatch_Receive_First_Award = 37042;
    /** 跨服天梯赛. 领取首次奖励 推送 */
    public static final int RMatch__Receive_First_Award_Push = 37043;
    // ================================================================
    // 挑战全明星
    // ================================================================
    /** 挑战全明星. 获取信息 */
    public static final int AllStar_Info = 38000;
    /** 挑战全明星. 信息推送 */
    public static final int AllStar_Info_Push = 38001;
    /** 挑战全明星. 开始比赛 */
    public static final int AllStar_Start_Match = 38002;
    /** 挑战全明星. 比赛结束推送 */
    public static final int AllStar_End_Match_Push = 38003;
    /** 挑战全明星. 获取npc状态 */
    public static final int AllStar_Npc = 38004;
    /** 挑战全明星. npc状态推送 */
    public static final int AllStar_Npc_Push = 38005;
    /** 今日挑战的全明星队 。获取*/
    public static final int AllStar_Today_Npc = 38006;
    /** 今日挑战的全明星队.推送 */
    public static final int AllStar_Today_Npc_Push = 38007;
    /** 获取我的全明星赛击杀奖励信息*/
    public static final int AllStar_Kill_Reward = 38008;
    /** 推送获取我的全明星赛击杀奖励信息*/
    public static final int AllStar_Kill_Reward_Push = 38009;
    /** 全明星赛-领取击杀奖励*/
    public static final int AllStar_Get_Kill_Reward = 38010;
    /** 全明星赛-领取个人积分奖励*/
    public static final int AllStar_Get_Score_Reward = 38011;
    /** 获取我的全明星赛个人积分奖励信息*/
    public static final int AllStar_Score_Reward = 38012;
    /** 推送-全明星赛-领取个人积分奖励*/
    public static final int AllStar_Score_Reward_Push = 38013;
    /** 全明星赛-购买挑战次数*/
    public static final int AllStar_Buy_Challenge_Num = 38014;
    /** 全明星赛-激励次数*/
    public static final int AllStar_Jili_Num = 38015;
    // ================================================================
    // 竞技场
    // ================================================================
    /** 竞技场. 获取天梯赛信息 */
    public static final int Arena_Info = 39000;
    /** 竞技场. 天梯赛信息 推送 */
    public static final int Arena_Info_Push = 39001;
    /** 竞技场. 开始比赛 */
    public static final int Arena_Start_Match = 39002;
    /** 竞技场. 对手发生变化 */
    public static final int Arena_Target_Change_Push = 39003;
    // /** 竞技场. */
    // public static final int Arena_39004 = 39004;
    /** 竞技场. 比赛结束 推送 */
    public static final int Arena_End_Match_Push = 39005;
    /** 竞技场. 购买比赛次数 */
    public static final int Arena_Buy_Match_Num = 39006;
    /** 竞技场. 购买比赛次数 推送 */
    public static final int Arena_Buy_Match_Num_Push = 39007;
    /** 竞技场. 排行榜 */
    public static final int Arena_Rank = 39008;
    // /** 个人排名竞技场. 段位排行榜 推送 */
    // public static final int Arena_Rank_Push = 37009;
    /** 竞技场. 比赛历史信息 */
    public static final int Arena_Match_His = 39010;
    // /** 竞技场. 比赛历史信息 推送 */
    // public static final int Arena_Match_His_Push = 37011;
    /** 竞技场. 刷新对手 */
    public static final int Arena_Refresh_Target = 37012;
    /** 竞技场. 刷新对手 推送 */
    public static final int Arena_Refresh_Target_Push = 37013;
    /** 竞技场. 重置比赛cd */
    public static final int Arena_Reset_Match_Cd = 37014;

    // 活动相关，40000开始
    public static final int SystemActiveManager_getAllActive = 39991;
    public static final int SystemActiveManager_Push_Active = 39992;
    public static final int SystemActiveManager_ShowView = 39993;
    public static final int SystemActiveManager_GetAward = 39994;
    public static final int SystemActiveManager_GetDayAward = 39995;
    public static final int SystemActiveManager_BuyFinish = 39996;
    public static final int SystemActiveManager_push_data = 39997;
    public static final int SystemActiveManager_lottery = 39998;
    // 巨星之路通用
    public static final int AtvStarLoadManager_showView = 40000;
    public static final int AtvStarLoadManager_getAward = 40001;
    public static final int AtvStarLoadManager_getRankAward = 40002;

    //
    public static final int FLDaysLoginManager_getAward = 40010;
    public static final int FLDaysLoginManager_showView = 40011;
    public static final int FLGroupUpFundManager_getAward = 40020;
    public static final int FLGroupUpFundManager_showView = 40021;

    public static final int AtvCardAwardManager_showView = 40025;
    /** 领取 atvId 月卡周卡类活动ID */
    public static final int AtvCardAwardManager_getAward = 40026;

    // 每日特惠充值
    /** 主界面 */
    public static final int AtvEveryDayRechargeManager_showView = 40030;
    public static final int AtvFirstRechargeDBManager_rechargeView = 40031;

    // 首冲送礼包
    public static final int AtvFirstRechargeManager_showView = 40040;
    /** 获取首充奖励 */
    public static final int AtvFirstRechargeManager_getAward = 40041;
    
    //答题活动
    /**获取答题活动的题目*/
    public static final int AtvAnswerQuestionManager_showView = 40050;
    /**答题*/
    public static final int AtvAnswerQuestionManager_answer = 40051;
    /**修改答题活动的状态:1开始答题,2结束答题*/
    public static final int AtvAnswerQuestionManager_updateAtvState = 40052;
    
    //客服工单
    /**获取所有客服工单数据*/
    public static final int CustomerServiceManager_getPlayerCustomerServiceData = 41000;
    /**玩家提交客服工单数据*/
    public static final int CustomerServiceManager_playerCommitGameProblem = 41001;
    /**客服有最新回复通知*/
    public static final int CustomerServiceManager_respNotice = 41002;
    /**玩家已读客服回复*/
    public static final int CustomerServiceManager_playerReadCustomerServiceResp = 41003;
    /**删除玩家提的问题*/
    public static final int CustomerServiceManager_playerDeleteCustomerServiceData = 41004;
    
    /** 新秀对抗赛.获取数据 */
    public static final int StarletManager_getDualMeetData = 50000;
    /** 新秀对抗赛.挑战 */
    public static final int StarletManager_dualMeetChallange = 50001;
    /** 新秀对抗赛.购买挑战次数 */
    public static final int StarletManager_dualMeetBuyNum = 50002;
    /** 新秀排位赛.阵容数据 */
    public static final int StarletManager_rankMatchTeamData = 50003;
    /** 新秀排位赛.购买挑战次数 */
    public static final int StarletManager_rankMatchBuyNum = 50004;
    /** 新秀排位赛.出战阵容设定 */
    public static final int StarletManager_rankMatchTeamSet = 50005;
    /** 新秀排位赛.获取主界面数据 */
    public static final int StarletManager_rankMatchGetData = 50006;
    /** 新秀排位赛.挑战 */
    public static final int StarletManager_rankMatchCallange = 50007;
    /** 新秀排位赛.排行榜 */
    public static final int StarletManager_rankMatchRanks = 50008;
    /** 新秀排位赛.获取排位赛挑战记录 */
    public static final int StarletManager_getStarletRankInfoList = 50009;
    /** 新秀对抗赛.获取正在比赛中的类型 */
    public static final int  StarletManager_getDualMeetType = 50011;
    /** 新秀对抗赛.获取对抗赛获得的基数信息 */
    public static final int  StarletManager_getStarletTeamRedix = 50014;
    
    /** 新秀对抗赛.获取数据回包 */
    public static final int StarletManager_push_getDualMeetData = 50010;  
    /** 新秀对抗赛.挑战回包 */
    public static final int StarletManager_push_dualMeetChallange = 50012;
    /** 新秀排位赛.阵容数据回包 */
    public static final int StarletManager_push_rankMatchTeamData = 50013;   
    /** 新秀排位赛.主界面数据 */
    public static final int StarletManager_push_rankMatchGetData = 50016;  
    /** 新秀对抗赛.获取对抗赛获得的基数信息回包 */
    public static final int  StarletManager_push_getStarletTeamRedix = 50017;
    /** 新秀排位赛.排行榜回包 */
    public static final int StarletManager_push_rankMatchRanks = 50018;
    /** 新秀排位赛.排位赛挑战记录回包 */
    public static final int StarletManager_push_getStarletRankInfoList = 50019;
    /** 新秀排位赛.挑战改变排行榜数据 */
    public static final int StarletManager_push_rankMatchChengeRanks = 50020;
    
    //------------------------------------极限挑战50030~50040-----------------------------
    /** 极限挑战-获取主界面信息 */
    public static final int Limit_Challenge_get_view = 50030;
    /** 极限挑战-推送主界面信息 */
    public static final int Limit_Challenge_push_view = 50031;
    /** 极限挑战. 开始比赛 */
    public static final int Limit_Challenge_Start_Match = 50032;
    /** 极限挑战. 比赛结束推送 */
    public static final int Limit_Challenge_End_Match_Push = 50033;
    /** 极限挑战-购买挑战次数*/
    public static final int Limit_Challenge_buy_Challenge_Num = 50034;
    /** 打开宝箱获得球员*/
    public static final int open_box_get_player = 50035;
    
    //------------------------------------球星荣耀50041~50100-----------------------------
    /** 球星荣耀-请求章节信息 */
    public static final int honor_match_div = 50041;
    /** 球星荣耀-推送章节信息 */
    public static final int honor_match_push_div = 50042;
    /** 球星荣耀-请求挑战 */
    public static final int honor_match_start = 50043;
    /** 球星荣耀-推送挑战 结束*/
    public static final int honor_match_end = 50044;
    /** 球星荣耀-打开星级宝箱*/
    public static final int honor_match_open_box_reward = 50045;
    /** 球星荣耀-更新宝箱信息 */
    public static final int honor_update_box_reward_info = 50046;
    /** 球星荣耀-更新关卡信息 */
    public static final int honor_update_lev_info = 50047;
    /** 球星荣耀-扫荡*/
    public static final int honor_Quick_Match = 50048;
    /** 球星荣耀. 扫荡推送 */
    public static final int honor_Match_Quick_Match_Push = 50049;
    /** 球星荣耀. 请求当前章节 */
    public static final int honor_Match_Curr_div_id = 50050;
    /** 球星荣耀. 推送当前章节 */
    public static final int honor_Match_push_Curr_div_id = 50051;
    /** 球星荣耀-购买挑战次数*/
    public static final int honor_Match_buy_Challenge_Num = 50052;
    
    
    //------------------------------------开服7天乐500101~50120-----------------------------
    /** 开服7天乐-请求任务信息 */
    public static final int happy_seven_day_info = 500101;
    /** 开服7天乐-推送任务信息 */
    public static final int happy_seven_day_push_info = 500102;
    /** 开服7天乐-更新任务信息 */
    public static final int happy_seven_day_update_task = 500103;
    /** 开服7天乐-领取奖励 */
    public static final int happy_seven_day_get_reward = 500104;
    /** 开服7天乐-领取宝箱奖励*/
    public static final int happy_seven_day_get_box = 500105;
    /** 开服7天乐-推送宝箱奖励更新*/
    public static final int happy_seven_day_push_box_update = 500106;
    
    
    /**
     * 测试环境
     */
    public static final int GameManager_debugLogin = 88888;
    public static final int GameManager_debugLoginTeam = 88887;
    public static final int GameManager_debugCreateTeam = 88889;
    public static final int GameManager_debugStartBattle = 88890;
    public static final int GameManager_login = 88891;
    public static final int GameManager_loginPC = 88892;
    public static final int GameManager_getPlayerVersion = 88893;

    public static final int GMManager_seachTeamName = 89000;
    public static final int GMManager_addMoney = 89001;
    public static final int GMManager_addPlayer = 89002;
    public static final int GMManager_addProp = 89003;
    public static final int GMManager_makeCard = 89004;
    public static final int GMManager_clearRedis = 89005;
    public static final int GMManager_sendEmail = 89006;
    /** gm 充值命令 */
    public static final int GMManager_addMoneyCallback = 89007;
    public static final int GMManager_lockTeam = 89008;
    public static final int GMManager_addPlayerStar = 89009;
    /** 充值成功推包 */
    public static final int GMManager_addMoneyPush = 89010;

    /**
     * 重置周期类型多人赛
     */
    public static final int GMManager_resetMatchPk = 89011;
    public static final int GMManager_chatController = 89012;

    // 活动相关
    public static final int GMManager_syncAllSystemActive = 89101;
    public static final int GMManager_updateActiveTimeBatch = 89102;
    public static final int GMManager_queryActiveData = 89103;
    public static final int GMManager_clearActiveData = 89104;
    public static final int GMManager_clearTeamActiveData = 89105;
    public static final int GMManager_createActiveDataTable = 89106;
    /**
     * 清档
     */
    public static final int GMManager_resetServerData = 90000;

    public static final int GMManager_closeServer = 90001;
    public static final int GMManager_Besign_Player_Add = 90002;
    public static final int GMManager_Match_Sign_Npc = 90003;
    public static final int GMManager_Cap_Test = 90004;
    /** gm命令. 比赛. 强制结束比赛 */
    public static final int GMManager_End_Match = 90005;
    /** gm命令. 字符串(易于打字输入的)gm命令 */
    public static final int GM_By_Type = 90006;

    // 测试
    public static final int DemoManager_dbTest = 999999;

    public static void main(String[] args) {
    }

    public static String simpleName(String codeName) {
        if (codeName.contains("Manager")) {
            return codeName.replaceAll("Manager", "");
        }
        return codeName;
    }

    /**
     * @author luch
     */
    public static class Code {

        private final int code;
        private final String name;

        public Code(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getCodeName() {
            return name;
        }

        public String getName() {
            return name;
        }

        public static Collection<Code> getAllCode() {
            return Holder.cache.values();
        }

        @Override
        public String toString() {
            return "{" + "\"code\":" + code + ", \"name\":\"" + name + "\"" + '}';
        }

        private static final class Holder {
            private static final Map<Integer, Code> cache = new HashMap<>();

            static {
                // if (!cache.isEmpty()) {
                // return;
                // }
                ServiceCode code;
                try {
                    code = ServiceCode.class.newInstance();
                    for (Field f : ServiceCode.class.getDeclaredFields()) {
                        // System.out.println("f " + f.getName() + " v " + f.get(code));
                        if (f.getType().equals(int.class) || f.getType().equals(Integer.class)) {
                            int v = f.getInt(code);
                            String name = f.getName();
                            cache.put(v, new Code(v, name));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            public static void main(String[] args) {

            }
        }

        public static Code convert(int code) {
            return Holder.cache.get(code);
        }

        public static void main(String[] args) {
            for (Code code : Holder.cache.values()) {
                System.out.println(code);
            }
        }
    }
}
