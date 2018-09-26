package com.ftkj.enums;

import com.ftkj.enums.battle.TacticZone;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年2月24日
 * 战术
 */
public enum TacticId {
    强攻内线(11, TacticType.Offense, TacticZone.inside),
    外线投篮(12, TacticType.Offense, TacticZone.outside),
    挡拆进攻(13, TacticType.Offense, TacticZone.normal),
    老鹰进攻(14, TacticType.Offense, TacticZone.normal),
    跑轰战术(15, TacticType.Offense, TacticZone.normal),

    内线防守(21, TacticType.Defense, TacticZone.inside),
    外线防守(22, TacticType.Defense, TacticZone.outside),
    混合防守(23, TacticType.Defense, TacticZone.normal),
    联合防守(24, TacticType.Defense, TacticZone.normal),
    全场紧逼(25, TacticType.Defense, TacticZone.normal),;

    private int id;
    private TacticType type;
    private TacticZone zone;

    private TacticId(int id, TacticType type, TacticZone zone) {
        this.id = id;
        this.type = type;
        this.zone = zone;
    }

    public int getId() {
        return id;
    }

    public TacticType getType() {
        return type;
    }

    public TacticZone getZone() {
        return zone;
    }

    public int getTypeInt() {
        return type.getType();
    }

    public void setType(TacticType type) {
        this.type = type;
    }

    //通过ID，取对应的战术枚举
    public static final Map<Integer, TacticId> tacticsEnumMap = new HashMap<Integer, TacticId>();

    static {
        for (TacticId et : TacticId.values()) {
            tacticsEnumMap.put(et.getId(), et);
        }
    }

    public static TacticId convert(int id) {
        return tacticsEnumMap.get(id);
    }

    public static void main(String[] args) {
        String str = "";
        for (TacticId id : values()) {
            str += id.name() + "\t" + id.getZone().name() + "\n";
        }
        System.out.println(str);
    }

    /**
     * 是否包含战术id
     *
     * @param key
     * @return
     */
    public static boolean checkId(int key) {
        return tacticsEnumMap.containsKey(key);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
