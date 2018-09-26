package com.ftkj.server;

import com.ftkj.manager.RPCManager;
import com.ftkj.server.proto.Request;
import com.ftkj.server.proto.Response;
import com.ftkj.server.rpc.RPCClientKeepAlive;
import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.RPCSource;
import com.ftkj.server.rpc.coder.RPCCodecFactory;
import com.ftkj.server.rpc.handler.RPCHandler;

import com.google.common.collect.Sets;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RPCMessageManager {
    private static final Logger log = LoggerFactory.getLogger(RPCMessageManager.class);
    private static ThreadLocal<RPCSource> source = new ThreadLocal<>();

    private static List<RPCServer> routeServerList = new CopyOnWriteArrayList<>();
    private static AtomicLong index = new AtomicLong(0);

    public static void addRouteServer(RPCServer server, RPCServer local, boolean replace) {
        try {
            addRouteServer0(server, local, replace);
        } catch (Exception e) {
            log.error(String.format("RPCServer add. server %s local %s msg %s", server, local, e.getMessage()), e);
        }
    }

    private static void addRouteServer0(RPCServer server, RPCServer local, boolean replace) {
        if (!server.isOpen()) {
            return;
        }
        RPCServer old = routeServerList.stream()
            .filter(s -> s.getServerName().equals(server.getServerName()))
            .findFirst()
            .orElse(null);

        if (old == null || old.getSession() == null || !old.getSession().isActive()) {//与路由节点断开，重新创建路由的session连接
            if (old != null) {
                routeServerList.remove(old);
            }
            IoConnector connector = new NioSocketConnector();
            connector.setHandler(new RPCHandler());
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new RPCCodecFactory()));
            KeepAliveFilter kaf = new KeepAliveFilter(new RPCClientKeepAlive(), IdleStatus.READER_IDLE, new KeepAliveRequestTimeoutHandler() {
                @Override
                public void keepAliveRequestTimedOut(KeepAliveFilter arg0, IoSession session) {
                    log.info("RPCServer retry server {} local {} session {}", server, local, session);
                    addRouteServer(server, local, replace);
                }
            });
            kaf.setForwardEvent(true);
            kaf.setRequestInterval(5);
            kaf.setRequestTimeout(20);
            connector.getFilterChain().addLast("heart", kaf);
            ConnectFuture connFuture = connector.connect(new InetSocketAddress(server.getIp(), server.getPort()));
            connFuture.awaitUninterruptibly();
            IoSession session = connFuture.getSession();
            log.info("RPCServer new conn host {}:{}", server.getIp(), server.getPort());
            server.setSession(session);
            old = server;
            routeServerList.add(old);
        }

        log.info("RPCServer register server {} local {}", server, local);
        old.getSession().write(local);
    }

    static final Response req = new Response(Request.Socket_Server_KeepAlive, new byte[0], false, 0);
    static final Response res = new Response(Request.Socket_Server_KeepAlive_CallBack, new byte[0], false, 0);

    class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {

        @Override
        public Object getRequest(IoSession arg0) {
            return req;
        }

        @Override
        public Object getResponse(IoSession arg0, Object arg1) {
            return res;
        }

        @Override
        public boolean isRequest(IoSession arg0, Object arg1) {
            return (arg1 instanceof Request && ((Request) arg1).getMethodCode() == Request.Socket_KeepAlive);
        }

        @Override
        public boolean isResponse(IoSession arg0, Object arg1) {
            return (arg1 instanceof Request && ((Request) arg1).getMethodCode() == Request.Socket_KeepAlive_CallBack);
        }

    }

    public static void checkRouteServer(List<String> serverName) {
        routeServerList = routeServerList.stream()
            .filter(route -> serverName.contains(route.getServerName()))
            .collect(Collectors.toList());
    }

    public static void initSource(RPCSource s) {
        source.set(s);
    }

    public static RPCSource get() {
        return source.get();
    }

    /**
     * 发送请求，到指定节点，无返回
     */
    public static void sendMessage(int code, String node, Serializable... args) {
        RPCServer route = getRouteServer();
        if (route == null) {
            log.error("[无法获取存活的路由节点] call code = {} , getRouteServer is null", code);
            return;
        }
        RPCSource source = new RPCSource(GameSource.pool, GameSource.serverName, node == null ? null : Sets.newHashSet(node), code, 0, args);
        route.getSession().write(source);
    }

    /**
     * 发送请求，到指定节点，无返回
     */
    public static void sendMessageNodes(int code, Set<String> nodes, Serializable... args) {
        RPCServer route = getRouteServer();
        if (route == null) { return; }
        RPCSource source = new RPCSource(GameSource.pool, GameSource.serverName, nodes, code, 0, args);
        route.getSession().write(source);
    }

    public static void sendLinkedTaskMessage(int code, String receiver, int reqId, Serializable... args) {
        RPCServer route = getRouteServer();
        if (route == null) {
            return;
        }
        Set<String> receive = receiver == null || receiver.isEmpty() ? null : Sets.newHashSet(receiver);
        RPCSource source = new RPCSource(GameSource.pool, GameSource.serverName, receive, code, reqId, args);
        route.getSession().write(source);
    }

    /**
     * 发送请求，到指定节点，
     * 节点间响应请求，也使用该方法进行响应
     */
    public static void responseMessage(Serializable... args) {
        RPCServer route = getRouteServer();
        if (route == null) { return; }
        RPCSource local = get();
        if (local == null) { return; }
        RPCSource source = new RPCSource(GameSource.pool, GameSource.serverName, Sets.newHashSet(local.getSender()), RPCManager.CallBack, local.getReqId(), args);
        log.info("resp rpc >> code {} reqid {} sender {}", local.getMethodCode(), local.getReqId(), local.getSender());
        route.getSession().write(source);
    }

    public static void register(RPCServer local) {
        //		routeServerList.stream().filter(route->route.isActive()).forEach(route->);
    }

    //递归取存活的路由服务器
    private static RPCServer getRouteServer() {
        if (routeServerList.size() == 0) { return null; }
        int i = (int) (index.incrementAndGet() % routeServerList.size());
        RPCServer s = routeServerList.get(i);
        if (!s.isOpen()) {//判断该路由是否有效
            return getRouteServer();
        }
        return s;
    }

}
