package com.ftkj.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tim.huang
 * 2017年2月17日
 * 线程池工具
 */
public class ThreadPoolUtil {

    private static Map<String, ScheduledExecutorService> poolMap = new HashMap<>();

    public static ScheduledExecutorService getScheduledPool(String name) {
        return poolMap.get(name);
    }

    public static ScheduledExecutorService newScheduledPool(String name, int corePoolSize) {
        poolMap.put(name, Executors.newScheduledThreadPool(corePoolSize, new ThreadPoolFactory(name, corePoolSize)));
        return getScheduledPool(name);
    }

    public static ExecutorService newFixedThreadPool(int poolSize, String poolName) {
        return Executors.newFixedThreadPool(poolSize, new X3ThreadFactory(poolName));
    }

    public static class ThreadPoolFactory implements ThreadFactory {
        private AtomicInteger num;
        private String name;
        private int initSize;

        public ThreadPoolFactory(String name, int initSize) {
            super();
            this.name = name;
            this.initSize = initSize;
            this.num = new AtomicInteger();
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, this.name + "-" + this.initSize + "-" + num.incrementAndGet());
        }

    }
}
