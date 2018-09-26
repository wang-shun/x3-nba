package com.ftkj.server;

import com.ftkj.db.conn.ao.AOSynManager;
import com.ftkj.server.proto.Request;
import com.ftkj.server.socket.GameServerManager;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 服务器状态对象
 *
 * @author tim.huang
 * 2015年11月28日
 */
public class ServerStat {
    private static Logger log = LoggerFactory.getLogger(ServerStat.class);

    /** 请求数量 */
    private static AtomicLong reqSize = new AtomicLong(0);
    /** 响应数量 */
    private static AtomicLong respSize = new AtomicLong(0);
    /** 在线人数 */
    private static AtomicInteger onlinePlayerSize = new AtomicInteger(0);
    /** 请求时间 */
    private static AtomicLong[] reqTime = new AtomicLong[]{new AtomicLong(0), new AtomicLong(0),
            new AtomicLong(0), new AtomicLong(0), new AtomicLong(0)};

    /** 方法状态集合 */
    private static Map<Integer, MethodStat> methodMap = Maps.newConcurrentMap();
    /** 消息状态集合 */
    private static MessageStat messageStat;

    public static void make(Request req) {
        long endTime = req.end();
        if (GameSource.stat) {
            //			if(endTime>=500){
            //			}
            methodMap.computeIfAbsent(req.getMethodCode(), key -> new MethodStat(key))
                    .update(endTime);
        }
        if (messageStat != null && req.getMethodCode() > 0) {
            messageStat.addReceivedMsgStat(req.getMethodCode(), req.getMsgLength(), (int) endTime);
        }
    }

    public static void setMessageStat(MessageStat messageStat) {
        ServerStat.messageStat = messageStat;
    }

    public static class MethodStat {
        private int serviceId;
        /** 调用次数 */
        private AtomicInteger count;
        /** 总耗时 */
        private AtomicLong totalMill;
        /** 最长耗时 */
        private long maxMill;

        public MethodStat(int serviceId) {
            super();
            this.serviceId = serviceId;
            this.count = new AtomicInteger();
            this.totalMill = new AtomicLong();
        }

        public void update(long mill) {
            if (mill > this.maxMill) {
                this.maxMill = mill;
            }
            count.incrementAndGet();
            totalMill.addAndGet(mill);
        }

        public int getServiceId() {
            return serviceId;
        }

        public AtomicInteger getCount() {
            return count;
        }

        public AtomicLong getTotalMill() {
            return totalMill;
        }

        public long getMaxMill() {
            return maxMill;
        }

        public long getAverageMill() {
            return this.totalMill.get() / this.count.get();
        }

    }

    public static void incReq() {
        reqSize.incrementAndGet();
    }

    public static void incResp(int code, int bytesSize) {
        respSize.incrementAndGet();
        if (messageStat != null) {
            messageStat.addSendMsgStat(code, bytesSize);
        }
    }

    public static void incOnline() {
        onlinePlayerSize.incrementAndGet();
    }

    public static void decOnline() {
        onlinePlayerSize.decrementAndGet();
    }

    public static void saveTime(long time) {
        if (time < 10) {
            reqTime[0].incrementAndGet();
        } else if (time >= 10 && time < 50) {
            reqTime[1].incrementAndGet();
        } else if (time >= 50 && time < 100) {
            reqTime[2].incrementAndGet();
        } else if (time >= 100 && time < 500) {
            reqTime[3].incrementAndGet();
        } else if (time >= 500) {
            reqTime[4].incrementAndGet();
        }
    }

    private static String memoryInfo() {
        Runtime rt = Runtime.getRuntime();
        return StringUtil.formatString("空闲内存:[{}mb],总内存:[{}mb],最大内存:[{}mb],已占用内存:[{}mb]\n"
                        + "============================分割线============================\n",
                rt.freeMemory() / 1024 / 1024, rt.totalMemory() / 1024 / 1024, rt.maxMemory() / 1024 / 1024
                , (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024);
    }

    private static String methodInfo() {
        StringBuffer sb = new StringBuffer();
        methodMap.forEach((key, val) ->
                sb.append(StringUtil.formatString("{}:调用次数[{}],最长耗时[{}],平均耗时[{}]\n"
                        , key, val.getCount(), val.getMaxMill(), val.getAverageMill())));
        return sb.toString();
    }

    public static void print() {
        log.info(StringUtil.formatString("\n请求数量总数:[{}],当前剩余执行的请求数量:[{}]\n"
                        + "响应数量总数:[{}],当前剩余执行的响应数量:[{}]\n"
                        + "AO异步请求总数:[{}],当前剩余执行的AO执行数量:[{}]\n"
                        + "当前在线玩家人数[{}]\n"
                        + "请求时间 0-10:[{}],10-50[{}],50-100[{}],100-500[{}],500以上[{}]\n"
                        + memoryInfo()
                        + methodInfo()
                , reqSize.get(), GameServerManager.getWaitRunReqSize()
                , respSize.get(), GameServerManager.getWaitRunRespSize()
                , AOSynManager.getAOTotalSize(), AOSynManager.getAOSize()
                , onlinePlayerSize.get(), reqTime[0].get(), reqTime[1].get(), reqTime[2].get()
                , reqTime[3].get(), reqTime[4].get()));
    }

}
