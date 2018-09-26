package com.ftkj.server.socket.handler;

import com.ftkj.manager.User;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServerStat;
import com.ftkj.server.ServiceManager;
import com.ftkj.server.proto.Request;
import com.ftkj.server.socket.GameServerManager;
import com.ftkj.server.socket.ServerMethod;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameSocketHandler extends IoHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(GameSocketHandler.class);

    private ISessionClose sessionClose;

    public GameSocketHandler() {
    }

    public void appendClose(ISessionClose sessionClose) {
        this.sessionClose = sessionClose;
    }

    public void sessionOpened(IoSession session) {
        log.debug("sessionOpened {}", session);
        ServerStat.incOnline();
    }

    public void sessionClosed(IoSession session) {
        Object teamId = session.getAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID);
        log.debug("sessionClosed {}, tid {}", session, teamId);
        if (teamId != null) {
            ServiceManager.offline((Long) teamId);
            User user = GameSource.offline((Long) teamId, session);
            if (user != null && sessionClose != null) {
                sessionClose.close(user);
            }
        }
        ServerStat.decOnline();
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        try {
            if (message instanceof Request) {
                ServerStat.incReq();
                Request req = (Request) message;
                if (GameSource.isOpen && ServerMethod.REJECT_METHOD.getKeyCode() != req.getMethodCode()) {
                    //服务器开启时才接收处理客户端请求
                    //废弃的客户端请求
                    GameServerManager.addRequest(req);
                }
            }
        } catch (Exception e) {
            log.error("messageReceived:" + e.getMessage(), e);
        }
    }

    public void exceptionCaught(IoSession session, Throwable cause) {
        log.debug("session exceptionCaught : {}, conntion exception:[{}]", session, cause);
    }

}



