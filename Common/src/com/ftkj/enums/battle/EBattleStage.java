package com.ftkj.enums.battle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年2月16日
 * 比赛阶段枚举
 */
public enum EBattleStage {
    Before(1),//赛前
    TipTeam(2),//玩家准备完毕，通知玩家进入比赛阶段
    PK(3),//比赛阶段
    End(4),//结束
    Close(5)//关闭

    ;

    public int type;

    EBattleStage(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static final Map<Integer, EBattleStage> caches = new HashMap<>();

    static {
        for (EBattleStage et : EBattleStage.values()) {
            caches.put(et.getType(), et);
        }
    }

    public static EBattleStage convert(int type) {
        return caches.get(type);
    }
}
