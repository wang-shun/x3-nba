package com.ftkj.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link ThreadFactory} implementation with a simple naming rule.
 *
 * @author luch
 */
public class X3ThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public X3ThreadFactory(String poolName) {
        if (poolName == null) {
            throw new NullPointerException("poolName");
        }
        
        // 安全管理对象
        SecurityManager sm = System.getSecurityManager();
        group = (sm != null) ? sm.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = poolName + "-" + poolNumber.getAndIncrement() + "-t-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
