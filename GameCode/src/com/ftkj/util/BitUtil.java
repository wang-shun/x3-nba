package com.ftkj.util;

/**
 * bit op.
 *
 * @author luch
 */
public class BitUtil {

    /** 是否已经拥有此状态. */
    public static boolean hasBit(long src, long bit) {
        return bit == (src & bit);
    }

    /** 是否已经拥有此状态. */
    public static boolean hasBit(long src, int bit) {
        return bit == (src & bit);
    }

    /** 是否已经拥有此状态. */
    public static boolean hasBit(int src, int bit) {
        return bit == (src & bit);
    }

    /** 与运算. 添加此状态 */
    public static long addBit(long src, long bit) {
        return src | bit;
    }

    /** 与运算. 添加此状态 */
    public static long addBit(long src, int bit) {
        return src | bit;
    }

    /** 与运算. 添加此状态 */
    public static int addBit(int src, int bit) {
        return src | bit;
    }

    public static long removeBit(long src, long bit) {
        return src ^ bit;
    }

    public static int removeBit(int src, int bit) {
        return src ^ bit;
    }

}
