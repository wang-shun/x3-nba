package com.ftkj.enums;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 加成类型
 *
 * @author Jay
 * @time:2017年9月22日 下午5:36:36
 */
public enum EBuffType {

    //VIP
    //	多人赛报名免费次数(1, true),
    //	球星卡一键转换暴击3倍概率(2, true),
    //	制作5星球星卡的概率提高概率(3, true),
    //	球星卡提升品质成功后有概率返回的经验(4, true),
    //	合成橙色头像的概率提高(5, true),
    //	头像进阶消耗荣耀值的概率减少(6, true),
    //	球探免费刷新次数(9, true),
    //common
    工资帽(1, true),
    比赛获得战术点提高概率(2, true),
    即使PK赛次数(3, true),
    日常任务免费刷新次数(4, true),
    每日登陆球馆获得点能量(5, true),
    每日免费开启擂台次数(6, true),
    球员仓库上限增加(7, true),
    每日选秀高级选秀免费次数(8, true),
    //
    街球赛每天挑战次数加X(10, true),
    训练馆每天免费占领次数加X(11, true),
    训练馆每天免费抢夺次数加X(12, true),
    训练馆每天可抢夺上限提高X(13, true),
    训练馆资源收益提高X(14, true),
    交易手续费降低X(15, true),
    购买经费每天免费次数加X(16, true),
    /** 训练馆产出加成X（(千分比)） */
    Train_Award(17, true),
    /** 喝咖啡恢复挑战次数X */
    Main_Match_Num(18, true),
    /** 日常任务经验加成X（千分比） */
    Task_Day_Exp(19, true),
    /** 交易中心发布置顶 */
    Trade_Sell_Stick(20, true),
    /** 球星荣耀购买次数*/
    honorBuy(21,true),

    // 月卡类的buff加成未实现，貌似策划那边还未定。
    装备升级X概率经验返还X比例的装备经验(1000, false),
    训练有X概率返还X比例的训练点(1001, false),
    主线赛程快速结束比赛(1002, false),;
    /**
     * buffID
     */
    private int id;
    private boolean isVip;

    /**
     * buffID
     *
     * @param id
     */
    private EBuffType(int id, boolean isVip) {
        this.id = id;
        this.isVip = isVip;
    }

    public int getId() {
        return id;
    }

    public boolean isVip() {
        return this.isVip;
    }

    protected static Map<Integer, EBuffType> keyMap;

    static {
        keyMap = Maps.newHashMap();
        for (EBuffType t : EBuffType.values()) {
            keyMap.put(t.getId(), t);
        }
    }

    public static EBuffType valueOfKey(int startID) {
        return keyMap.get(startID);
    }
}
