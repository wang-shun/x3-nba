package com.ftkj.server.syn;

import com.ftkj.server.MessageManager;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.ServerStat;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.server.proto.Request;
import com.ftkj.server.rpc.RPCSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理逻辑线程
 *
 * @author tim.huang
 * 2015年12月9日
 */
public class SynInvokeThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SynInvokeThread.class);
    private final Request req;

    public SynInvokeThread(Request req) {
        this.req = req;
    }

    @Override
    public void run() {
        try {
            //执行逻辑前，初始化当前玩家数据源
            req.start();
            if (req instanceof RPCSource) {
                RPCMessageManager.initSource((RPCSource) req);
            } else {
                MessageManager.initTeam(req);
            }

            if (req.getTeamId() > 0 || InstanceFactory.get().isUnCheck(req.getMethodCode())) {//还没登录
                if (log.isDebugEnabled()) {
                    log.debug("req << tid {} code {} reqid {} len {} method {} params [{}]",
                            req.getTeamId(), req.getMethodCode(), req.getReqId(), req.getMsgLength(),
                            req.getServerMethod() == null ? req.getMethodCode() : req.getServerMethod().getName(), req.getAttrsSimpleString());
                }
                req.invoke();
            }
            //记录请求消耗时间
            ServerStat.make(req);
        } catch (Exception e) {
            log.error(String.format("SynInvokeThread code : %s msg %s", req.getMethodCode(), e.getMessage()), e);
        }
    }

}
