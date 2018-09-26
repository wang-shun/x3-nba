package com.ftkj.util;

/**
 * @author luch
 */
public class MemUtil {

    public static long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static long memoryUsed(long before) {
        return before - Runtime.getRuntime().freeMemory();
    }
}
