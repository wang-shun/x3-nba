package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

public enum ETaskCondition {
    点击界面(0,true,true),
    好友(1,true,true),
    球探签约(2,true,true),
    即时PK(3,true,true),
    商城购买(4,true,true),
    打比赛(5,true,true),
    解雇球员(6,true,true),
    球队等级(7,true,true),
    多人赛(8,true,true),
    强化装备(9,true,true),
    进阶装备(10,true,true),
    升级装备(11,true,true),
    充值RMB(12,true,true),
    获得球员(13,true,true),
    联盟(14,true,true),
    联盟聊天(15,true,true),
    联盟捐献经费(16,true,true),
    联盟捐献勋章(17,true,true),
    球星卡全套卡制作(18,true,true),
    球星卡升级(19,true,true),
    合成荣誉头像(20,true,true),
    分解荣誉头像(21,true,true),
    累计胜场(22,true,true),
    球馆转盘(23,true,true),
    招募球员(24,true,true),
    任命教练(25,true,true),
    球员升星(26,true,true),
    球员升级(27,true,true),
    球员训练(28,true,true),
    球员技能(29,true,true),
    球馆建筑升级(30,true,true),
    多人赛总冠军(31,true,true),
    选秀(32,true,true),
    主线赛程胜利(33,true,true),
    合成球衣(34,true,true),
    学习战术(35,true,true),
    升级战术(36,true,true),
    解锁教练(37,true,true),
    球馆球员上阵(38,true,true),
    球馆升级(39,true,true),
    选秀选人(40,true,true),
    街球赛(41,true,true),
    Main_Match(42,true,true),
    完成一场比赛(43,true,true),
    刷新球队天赋(44,true,true),
    多人赛名次(45,true,true),
    训练馆占领成功(46,true,true),
    比赛胜利次数(47,true,true),
    训练馆抢夺xxx次(48,true,true),
    训练馆升至xxx级(49,true,true),
    训练馆训练xxx次(50,true,true),
    
    登录(51,true,false),
    make_card(52,true,false),
    更新天梯积分(53,true,false),
    装备升星(55,true,false),
    充值(56,true,true),
    战力(57,true,true),
    
    
    ;

  /**
   * 是否是开服7天乐条件
   */
    private boolean isHappy7dayCondition;

    /**
     * 是否是主线任务条件
     */
    private boolean isMainTaskCondition;
  
    
    
    public boolean isHappy7dayCondition() {
      return isHappy7dayCondition;
    }

    public boolean isMainTaskCondition() {
      return isMainTaskCondition;
    }

    public static final Map<Integer, ETaskCondition> taskConditionEnumMap = new HashMap<Integer, ETaskCondition>();

    static {
        for (ETaskCondition et : ETaskCondition.values()) {
            taskConditionEnumMap.put(et.getType(), et);
        }
    }

    public static ETaskCondition getETaskCondition(int key) {
        ETaskCondition title = taskConditionEnumMap.get(key);
        return title;
    }

    private int type;

    private ETaskCondition(int type,boolean isHappy7dayCondition,boolean isMainTaskCondition) {
        this.type = type;
        this.isHappy7dayCondition = isHappy7dayCondition;
        this.isMainTaskCondition = isMainTaskCondition;
    }

    public int getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }
}
