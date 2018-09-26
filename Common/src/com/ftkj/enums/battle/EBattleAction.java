package com.ftkj.enums.battle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年2月21日
 * 战斗行为
 */
public enum EBattleAction {

    A_2加1无助功(0, "2p+1p-ast", "2加1无助功"),
    A_2加1有助攻(1, "2p+1p+ast", "2加1有助攻"),
    A_2分投中无助功(2, "2p-ast", "2分投中无助功"),
    A_2分投中有助攻(3, "2p+ast", "2分投中有助攻"),
    A_2分不中被犯规(4, "2miss+fouled", "2分不中被犯规"),
    A_2分投篮被盖帽(5, "2p+blk", "2分投篮被盖帽"),
    A_2分投篮不中(6, "2miss", "2分投篮不中"),
    A_3加1无助攻(7, "3p+1p-ast", "3加1无助攻"),
    A_3加1有助攻(8, "3p+1p+ast", "3加1有助攻"),
    A_3分投中无助攻(9, "3p-ast", "3分投中无助攻"),
    A_3分投中有助攻(10, "3p+ast", "3分投中有助攻"),
    A_3分不中被犯规(11, "3miss+fouled", "3分不中被犯规"),
    A_3分投篮被盖帽(12, "3p+blk", "3分投篮被盖帽"),
    A_3分投篮不中(13, "3miss", "3分投篮不中"),
    A_失误(14, "to", "失误"),
    A_被抢断(15, "stl", "被抢断"),
    A_进攻犯规(16, "pf", "进攻犯规"),
    A_被防守犯规(17, "fouled", "被防守犯规"),
    A_开场(18, "start", "开场"),
    A_第一小节(19, "1st", "第一小节"),
    A_第二小节(20, "2nd", "第二小节"),
    A_第三小节(21, "3rd", "第三小节"),
    A_第四小节(22, "4th", "第四小节"),
    A_加时(23, "ot", "加时"),
    A_结束(24, "end", "结束"),
    A_关闭(25, "closed", "关闭"),
    //------------------------
    NULL(26, "null", "未知行为_空行为"),
    T_2分投篮不中补篮(27, "2miss+tip_in", "2分投篮不中补篮"),
    /** 释放球员技能, 会转换成其他 action */
    skill(28, "skill", "球员技能"),
    //
    ;

    /** 类型id. 客户端服务器策划共用 */
    private final int type;
    /** 策划配置名称 */
    private final String configName;
    /** 注释 */
    private final String comment;

    EBattleAction(int type, String configName, String comment) {
        this.type = type;
        this.configName = configName;
        this.comment = comment;
    }

    public int getType() {
        return type;
    }

    public String getConfigName() {
        return configName;
    }

    public String getComment() {
        return comment;
    }

    public static final Map<Integer, EBattleAction> typeCaches = new HashMap<>();
    public static final Map<String, EBattleAction> nameCaches = new HashMap<>();

    static {
        for (EBattleAction t : values()) {
            if (typeCaches.containsKey(t.getType())) {
                throw new Error("duplicate type :" + t.getType());
            }
            if (nameCaches.containsKey(t.getConfigName())) {
                throw new Error("duplicate cfg name :" + t.getConfigName());
            }
            typeCaches.put(t.getType(), t);
            nameCaches.put(t.getConfigName(), t);
        }
    }

    public static EBattleAction convertByType(int type) {
        return typeCaches.get(type);
    }

    public static EBattleAction convertByName(String cfgName) {
        return nameCaches.get(cfgName);
    }

    @Override
    public String toString() {
        return name();
    }

    public static void main(String[] args) {
        //        for (EBattleAction act : values()) {
        //            System.out.println(act.name() + "\t" + act.ordinal());
        //        }
        for (EBattleAction act : values()) {
            System.out.println(act.type + "\t" + act.configName + "\t" + act.comment);
        }
        //        for (EBattleAction act : values()) {
        //            System.out.println(act.configName + " : " + act.comment);
        //        }
    }
}
