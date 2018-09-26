package com.ftkj.manager;

import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RPCManager {

    public final static Logger log = LogManager.getLogger(RPCManager.class);

    public static final int CallBack = -1;

    private static Map<Integer, RPCLinkedTask> taskMap = Maps.newConcurrentMap();

    private static AtomicInteger id = new AtomicInteger();

    public static void requestExceute(int id, Serializable[] args) {
        RPCLinkedTask task = taskMap.get(id);
        if (task == null) { return; }
        task.next().exceute(args);
        if (task.isEnd()) {
            taskMap.remove(id);
        }
    }

    public static void putAndExceute(RPCLinkedTask task, Serializable... objects) {
        taskMap.put(task.getId(), task);
        task.next().exceute(objects);
    }

    public static int getId() {
        if (id.get() >= 10000000) { id.set(0); }
        return id.incrementAndGet();
    }

    public static void clearTimeOut() {
        Set<Integer> keys = taskMap.keySet();
        for (int key : keys) {
            if (taskMap.get(key).isTimeOut()) {
                log.error("清除未处理完成的异步回调--[{}]", taskMap.get(key).getId());
                taskMap.remove(key);
            }
        }
    }

}
