package com.ftkj.util;

import java.io.Serializable;

/**
 * 区间信息.
 */
public class IntervalInt implements Comparable<IntervalInt>, Serializable {
    private static final long serialVersionUID = 2770970392164630402L;
    /** 下限 */
    private final int lower;
    /** 上限 */
    private final int upper;

    private int number = -1;

    public IntervalInt(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
        if (lower > upper) {
            throw new IllegalArgumentException("lower > upper. lower " + lower + " upper " + upper);
        }
    }

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }

    /** 获取区间值. 上限 - 下限 */
    public int getInterval() {
        if (number == -1) { number = upper - lower; }
        return number;
    }

    /**
     * 数值是否在区间中. 处理 NaN
     *
     * @return random >= i && i <= upper
     */
    public boolean match(int i) {
        return (Integer.compare(i, lower) >= 0) && (Integer.compare(i, upper) <= 0);
    }

    /** 比较用来排序的两个参数。根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数。 */
    public static int compare(IntervalInt o1, int o2) {
        if (o1.upper < o2) {
            return -1;
        } else {
            if (o1.match(o2)) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /** 比较用来排序的两个参数。根据第一个参数小于、等于或大于第二个参数分别返回负整数、零或正整数。 */
    public static int compare(int lower, int upper, int o2) {
        return compare((long) lower, (long) upper, (long) o2);
    }

    /** 比较用来排序的两个参数。根据第[1,2]个参数小于、等于或大于第3个参数分别返回负整数、零或正整数。 */
    public static int compare(long lower, long upper, long o2) {
        if (upper < o2) {
            return -1;
        } else {
            if ((Long.compare(o2, lower) >= 0) && (Long.compare(o2, upper) <= 0)) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    public int compareTo(IntervalInt o) {
        return compare(this, o.upper);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof IntervalInt)) { return false; }

        IntervalInt that = (IntervalInt) o;

        if (lower != that.lower) { return false; }
        if (upper != that.upper) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        int result = lower;
        result = 31 * result + upper;
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"lower\" : " + lower +
                ", \"upper\" : " + upper +
                '}';
    }
}
