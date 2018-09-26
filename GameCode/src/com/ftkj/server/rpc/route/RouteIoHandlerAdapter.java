package com.ftkj.server.rpc.route;

import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.RPCSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @author tim.huang
 * 2017年4月7日
 */
public class RouteIoHandlerAdapter extends IoHandlerAdapter {

    public final static Logger log = LogManager.getLogger(RouteIoHandlerAdapter.class);

    @Override
    public void messageReceived(IoSession session, Object message) {
        if (message instanceof RPCSource) {//跨服节点数据类型
            //将数据放入消息处理线程，增加路由吞吐量
            RouteServerManager.submit((RPCSource) message);
        } else if (message instanceof RPCServer) {//
            RPCServer server = (RPCServer) message;
            server.setSession(session);
            RouteServerManager.putRPCServer(server);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error("Session Exception " + session, cause);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info("Session Closed [{}]", session);
        super.sessionClosed(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
        throws Exception {

        super.sessionIdle(session, status);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.info("Session Opened [{}]", session);
        super.sessionOpened(session);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.info("Session Created [{}]", session);
    }

}
