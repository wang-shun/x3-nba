package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/** 攻防类型 */
public enum AbilityType {
    Player_Base(1, "player_base", "球员基础"),
    Equip(2, "equip", "装备"),
    Card(3, "pr_card", "球星卡"),
    /** 战术 */
    Tactic(4, "tactic", "战术"),
    /** 战术进攻 */
    Tactic_Offense(5, "tactic_offense", "战术进攻"),
    /** 战术防守 */
    Tactic_Defense(6, "tactic_defense", "战术防守"),
    /** 战术克制 */
    Tactic_Retrain(7, "tactic_retrain", "战术克制"),
    /** 球员属性汇总 */
    Player(8, "pr", "球员"),
    Team(9, "team", "球队"),
    Avatar(10, "avatar", "头像"),
    Npc_Buff(11, "npc_buff", "NPC增幅"),
    //    Cap(12, "Cap", "士气"),
    League_Train(13, "League_Train", "联盟训练馆"),
    Player_Lev(14, "Player_Lev", "球员等级"),
    Player_Star(15, "Player_Star", "球员升星"),
    Player_Skill(16, "Player_Skill", "球员技能"),
    League(17, "League", "联盟"),
    Temp(18, "Temp", "临时"),
    Pr_Clothes(19, "Pr_Clothes", "球衣"),
    Pr_Talent(20, "Pr_Talent", "天赋"),
    Gym(21, "gym", "球馆"),

    //
    ;

    /** 类型id. 客户端服务器策划共用 */
    private final int type;
    /** 策划配置名称 */
    private final String configName;
    /** 注释 */
    private final String comment;

    AbilityType(int type, String configName, String comment) {
        this.type = type;
        this.configName = configName.toLowerCase();
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

    public static final Map<Integer, AbilityType> typeCaches = new HashMap<>();
    public static final Map<String, AbilityType> nameCaches = new HashMap<>();

    static {
        for (AbilityType t : values()) {
            if (typeCaches.containsKey(t.getType())) {
                throw new Error("duplicate action type :" + t.getType());
            }
            if (nameCaches.containsKey(t.getConfigName())) {
                throw new Error("duplicate action cfg name :" + t.getConfigName());
            }
            typeCaches.put(t.getType(), t);
            nameCaches.put(t.getConfigName(), t);
        }
    }

    public static AbilityType convertByType(int type) {
        return typeCaches.get(type);
    }

    public static AbilityType convertByName(String cfgName) {
        return nameCaches.get(cfgName);
    }

    public static void main(String[] args) {
        printShort();
    }

    private static void printShort() {
        String str = "";
        for (AbilityType et : values()) {
            str += et.getType() + "\t" + et.getComment() + "\n";
        }
        System.out.println(str);
    }
}
