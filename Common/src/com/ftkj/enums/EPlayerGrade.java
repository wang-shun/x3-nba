package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年3月7日
 * 球员等级枚举
 */
public enum EPlayerGrade {
    D1("D-"),
    D("D"),
    D2("D+"),
    C1("C-"),
    C("C"),
    C2("C+"),
    B1("B-"),
    B("B"),
    B2("B+"),
    A1("A-"),
    A("A"),
    A2("A+"),
    S1("S-"),
    S("S"),
    S2("S+"),

    X("X"),
    N("N");
    private String grade;

    EPlayerGrade(String grade) {    	
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    //通过ID，取对应的战术枚举
    public static final Map<String, EPlayerGrade> gradeCaches = new HashMap<>();

    static {
        for (EPlayerGrade et : EPlayerGrade.values()) {
            gradeCaches.put(et.getGrade(), et);
        }
    }

    public static EPlayerGrade convertByName(String grade) {
        return gradeCaches.get(grade);
    }

    /**
     * A+，A- 取A
     *
     * @param grade
     * @return
     */
    public static EPlayerGrade getGrade(EPlayerGrade grade) {
        return EPlayerGrade.valueOf(grade.getGrade().substring(0, 1));
    }

}
