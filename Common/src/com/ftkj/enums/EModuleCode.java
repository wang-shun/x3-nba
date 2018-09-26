package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年3月8日
 * 模块枚举
 */
public enum EModuleCode {
    比赛(1, "Battle"),
    球队(2, "Team"),
    球探(3, "Scout"),
    待签(4, "BeSign"),
    道具(5, "Prop"),
    解雇(6, "Fire"),
    联盟(7, "League"),
    好友(8, "Friend"),
    选秀(9, "Draft"),
    主线赛程(10, "Stage"),
    战术(11, "Tactics"),
    多人赛(12, "KnockoutMatch"),
    装备(13, "Equi"),
    球星卡(14, "PlayerCard"),
    荣誉头像(15, "PlayerLogo"),
    商城(16, "Shop"),
    任务(17, "Task"),
    训练馆(18, "Train"),
    签到(19, "Sign"),
    VIP(20, "VIP"),
    教练(21, "Coach"),
    球员升星(22, "PlayerStar"),
    球员升级(23, "PlayerLevel"),
    球员训练(24, "PlayerTrain"),
    球员技能(25, "PlayerSkill"),
    球馆转盘(26, "ArenaRoll"),
    球馆建筑升级(27, "ArenaConLevel"),
    多人赛总冠军(28, "KnockoutMatchWin"),
    排行榜(29, "Rank"),
    联盟赛球馆赛(30, "LeagueArena"),
    兑换(31, "TranProp"),
    活动(32, "Active"),
    街球副本(33, "Streetball"),
    邮件(34, "Emaill"),
    充值福利(35, "Fuli"),
    球队升级(36, "TeamLevel"),
    球队替补(37, "TeamLineup"),
    球员投资(38, "PlayerInvestment"),
    球员回收(39, "PlayerExchangeRecycle"),
    球员天赋(40, "PlayerAbility"),
    高中新星(41, "PlayerBid"),
    身价变动(42, "PlayerPrice"),
    招募(43, "ScoutPlayer"),
    联盟组队赛(44, "LeagueGroupWar"),
    自由交易(45, "Trade"),
    球员兑换(46, "PlayerExchange"),
    全明星赛本服击杀奖(47, "Npc"),
    全明星赛本服积分奖(48, "PersonalAward"),
    
    球星荣耀(49, "HonorAward"),
    
    /**答题活动*/
    AtcAnswerQuestion(50, "ActAnswerQuestion"),
    /**开服7天乐*/
    Happy7day(51, "Happy7day"),

    /** 主线赛程 */
    MainMatch(360, "MainMatch"),
    /** 天梯赛 */
    RankedMatch(370, "RankedMatch"),
    /** 挑战全明星 */
    AllStar(380, "AllStar"),
    /** 竞技场. 个人排名竞技场. */
    Arena(390, "Arena"),
    /** 新秀. 竞技赛. */
    Starlet(400, "Starlet"),
    /**极限挑战 */
    LimitChallenge(410, "limitChallenge"),

    CDKey(60, "CDKey"),
    临时(70, "Buff"),
    新手引导(80, "Help"),
    充值(98, "Recharge Pay"),
    GM(99, "GM"),
    ;

    private int id;
    private String name;

    private EModuleCode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //通过ID，取对应的战术枚举
    public static final Map<Integer, EModuleCode> moduleEnumMap = new HashMap<>();
    public static final Map<String, EModuleCode> names = new HashMap<>();

    static {
        for (EModuleCode et : EModuleCode.values()) {
            moduleEnumMap.put(et.getId(), et);
            names.put(et.getName().toLowerCase(), et);
        }
    }

    public static EModuleCode getEModuleCode(int id) {
        return moduleEnumMap.get(id);
    }

    public static EModuleCode convert(String name) {
        return names.get(name.toLowerCase());
    }

}
