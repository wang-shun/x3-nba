package com.ftkj.server.rpc.handler;

import com.ftkj.server.rpc.RPCSource;
import com.ftkj.server.socket.GameServerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @author tim.huang
 * 2017年3月16日
 * 服务提供者
 */
public class RPCHandler extends IoHandlerAdapter {
    public final static Logger log = LogManager.getLogger(RPCHandler.class);

    @Override
    public void messageReceived(IoSession session, Object message) {
        //收到请求服务消息，执行本地逻辑
        if (message instanceof RPCSource) {
            RPCSource source = (RPCSource) message;
            GameServerManager.addRequest(source);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        log.error("Session Exception [{}] {}", session.getServiceAddress(), cause.getMessage());
        //		super.exceptionCaught(session, cause);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info("Session Closed [{}]", session.getServiceAddress());
        super.sessionClosed(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        log.trace("Session Idle [{}]", session, status);
        super.sessionIdle(session, status);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.info("Session Opened {}", session.getServiceAddress());
        super.sessionOpened(session);
    }

    @Override
    public void sessionCreated(IoSession session) {
        log.info("Session Created {}", session.getServiceAddress());
    }

}
