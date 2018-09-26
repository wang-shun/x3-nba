package com.ftkj.enums.battle;

import com.ftkj.cfg.CompareOp;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 1st 2nd 3rd 4th 5th
 */
public enum EBattleStep {
    /** 开场 */
    Start(0, "start", "开场"),
    /** 第1节. first period (quarter) */
    First_Period(1, "1st", "第1节"),
    /** 第2节. period (quarter) */
    Second_Period(2, "2nd", "第2节"),
    /** 第3节. period (quarter) */
    Thrid_Period(3, "3rd", "第3节"),
    /** 第4节. period (quarter) */
    Fourth_Period(4, "4th", "第4节"),
    /** 结束 */
    End(5, "end", "结束"),
    /** 加时1 */
    Overtime(6, "ot1", "加时1"),
    //    Overtime2(7, "ot2", "加时2"),
    //    Overtime3(8, "ot3", "加时3"),
    /** 关闭 */
    Closed(10, "closed", "关闭"),

    //
    ;
    private int type;
    private String stepName;
    private String comment;

    EBattleStep(int type, String stepName, String comment) {
        this.type = type;
        this.stepName = stepName;
        this.comment = comment;
    }

    public int getType() {
        return type;
    }

    public String getStepName() {
        return stepName;
    }

    @Override
    public String toString() {
        return stepName;
    }

    public boolean compare(CompareOp op, EBattleStep other) {
        return op.compare(type, other.type);
    }

    public static final Map<String, EBattleStep> cache = new HashMap<>();
    public static final Map<Integer, EBattleStep> typeCache = new HashMap<>();

    static {
        for (EBattleStep et : EBattleStep.values()) {
            cache.put(et.getStepName(), et);
            typeCache.put(et.getType(), et);
        }
    }

    public static EBattleStep convert(String stepName) {
        return cache.get(stepName);
    }

    public static EBattleStep convert(int type) {
        return typeCache.get(type);
    }

    public static void main(String[] args) {
        String str = "";
        java.util.List<EBattleStep> steps = java.util.Arrays.asList(EBattleStep.values());
        steps.sort(Comparator.comparingInt(o -> o.type));
        for (EBattleStep et : steps) {
            str += et.getStepName() + "\t" + et.comment + "\n";
        }
        System.out.println(str);
    }
}
