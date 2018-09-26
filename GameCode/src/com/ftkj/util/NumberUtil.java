package com.ftkj.util;

public class NumberUtil {

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    public static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

    /**
     * Returns a power of two size for the given target capacity.
     */
    public static int powerOfTwo(int val) {
        if (isPowerOfTwo(val)) {
            return val;
        }
        int n = val - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

}
