package com.ftkj.server.socket;

import com.ftkj.server.GameSource;
import com.ftkj.server.instance.InitServer;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.server.proto.Request;
import com.ftkj.server.proto.Response;
import com.ftkj.server.proto.coder.GoogleCodecFactory;
import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.RPCServerKeepAlive;
import com.ftkj.server.rpc.coder.RPCCodecFactory;
import com.ftkj.server.rpc.route.RouteIoHandlerAdapter;
import com.ftkj.server.rpc.route.RouteServerManager;
import com.ftkj.server.socket.handler.GameSocketHandler;
import com.ftkj.server.socket.handler.ISessionClose;
import com.ftkj.tool.zookeep.ZookeepServer;
import com.ftkj.util.IPUtil;
import com.ftkj.util.X3ThreadFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.Executors;

public class GameSocketServer {
    private static final Logger log = LoggerFactory.getLogger(GameSocketServer.class);
    private static final int DEFAULT_RECEIVE_BUFFER_SIZE = 1024;

    private static final int DEFAULT_SEND_BUFFER_SIZE = 2 * 1024;

    private static final int IDELTIMEOUT = 30;

    private static final int HEARTBEATRATE = 10;

    //	static Logger logger = LoggerFactory.getLogger(GameSocketServer.class);

    private SocketServerConfig serverConfig;

    public GameSocketServer(SocketServerConfig serverConfg) {
        this.serverConfig = serverConfg;
    }

    /**
     * 开启普通逻辑服务器
     *
     * @throws Exception
     */
    public void start() throws Throwable {
        //执行配置脚本
        InstanceFactory.get().runInitScript(serverConfig.getJSScriptPath());
        main_init_socket(new GameSocketHandler(), new GoogleCodecFactory(), false, false);
    }

    /**
     * 开启分布式消费者服务器
     *
     * @throws Exception
     */
    public void startComsumption() throws Exception {
        //		main_init_socket();
    }

    /**
     * 开启分布式路由服务武器
     *
     * @throws Exception
     */
    public void startRoute() throws Throwable {
        InstanceFactory.get().runInitScript(serverConfig.getJSScriptPath());
        main_init_socket(new RouteIoHandlerAdapter(), new RPCCodecFactory(), true, true);

        Thread.sleep(2000);
        RPCServer server = new RPCServer(GameSource.pool, GameSource.serverName, "0.0.0.0".equals(serverConfig.getIP()) ? IPUtil.getLocalIp() : serverConfig.getIP()
                , serverConfig.getPort(), GameSource.isOpen ? 1 : 0, GameSource.shardId);
        ZookeepServer zk = ZookeepServer.createRouteZK(ZookeepServer.createZK(server));
        RouteServerManager.setZK(zk);
        RouteServerManager.startRoute();
    }

    /**
     * 开启分布式提供者服务器
     *
     * @throws Exception
     */
    public void startNode() throws Throwable {
        startNode(user -> {});
    }

    /**
     * 开启分布式提供者服务器
     *
     * @throws Exception
     */
    public void startNode(ISessionClose sessionClose) throws Throwable {
        long curr = System.currentTimeMillis();
        log.info("start node");
        //执行配置脚本
        InstanceFactory.get().runInitScript(serverConfig.getJSScriptPath());
//        Thread.sleep(2000);
        regiterNodeZK();
        //等待zk注册路由成功，避免在manager初始调用中没取到路由数据
//        Thread.sleep(2000);
        GameSocketHandler handler = new GameSocketHandler();
        handler.appendClose(sessionClose);
        main_init_socket(handler, new GoogleCodecFactory(), false, false);
        log.info("node启动完毕, 监听端口 {}. 耗时 {}", serverConfig.getPort(), Duration.ofMillis(System.currentTimeMillis() - curr));
    }

    public SocketServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(SocketServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    private void main_init_socket(IoHandlerAdapter handler, ProtocolCodecFactory codec, boolean isRoute, boolean keepalive) throws Throwable {
        //		String classPath = GameSocketServer.class.getResource("/").getPath();
        //		System.err.println("***" + classPath);
        //		PropertyConfigurator.configure(classPath + "log4j.properties");

        String ip = serverConfig.getIP();
        int port = serverConfig.getPort();
        InitServer.init(serverConfig, isRoute);

        //可用处理器的Java虚拟机的数量
        int cpusize = Runtime.getRuntime().availableProcessors();
        // 接收客服端请求
        NioSocketAcceptor acceptor = new NioSocketAcceptor(cpusize + 1);
        SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
        // 注册解码器
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(codec));
        // 注册线程池
        acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newFixedThreadPool(cpusize * 2,
                new X3ThreadFactory(serverConfig.getPoolName() + "_worker"))));
        if (keepalive) {//心跳
            KeepAliveMessageFactory kamfi = isRoute ? new RPCServerKeepAlive() : new KeepAliveMessageFactoryImpl();
            KeepAliveFilter kaf = new KeepAliveFilter(kamfi, IdleStatus.BOTH_IDLE);
            //idle事件回发  当session进入idle状态的时候 依然调用handler中的idled方法
            kaf.setForwardEvent(true);
            // 心跳检测间隔
            kaf.setRequestInterval(HEARTBEATRATE);
            // 心跳检测注册
            acceptor.getFilterChain().addLast("heart", kaf);
            // 设置IdleTime空闲时间
            sessionConfig.setIdleTime(IdleStatus.BOTH_IDLE, IDELTIMEOUT);
        }

        acceptor.setHandler(handler); // 注册消息处理器
        //        sessionConfig.setReuseAddress(true); // 避免重启时提示地址被占用
        sessionConfig.setTcpNoDelay(false);  // 设置为非延迟发送
        sessionConfig.setSoLinger(0);
        sessionConfig.setKeepAlive(true); // 检测链接是否有效
        //		sessionConfig.setMinReadBufferSize(DEFAULT_SEND_BUFFER_SIZE);
        //        sessionConfig.setReceiveBufferSize(DEFAULT_RECEIVE_BUFFER_SIZE);// 设置输入缓冲区的大小
        //        sessionConfig.setSendBufferSize(DEFAULT_SEND_BUFFER_SIZE);// 设置输出缓冲区的大小
        try {
            acceptor.bind(new InetSocketAddress(ip, port));
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        log.info("Game Server is Listing on " + ip + ":" + port);
    }

    private static final Response req = new Response(Request.Socket_Server_KeepAlive, new byte[0], false, 0);
    private static final Response res = new Response(Request.Socket_Server_KeepAlive_CallBack, new byte[0], false, 0);

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

    private void regiterNodeZK() {
        RPCServer server = new RPCServer(GameSource.pool, GameSource.serverName, "0.0.0.0".equals(serverConfig.getIP()) ? IPUtil.getLocalIp() : serverConfig.getIP()
                , serverConfig.getPort(), GameSource.isOpen ? 1 : 0, GameSource.shardId);
        ZookeepServer.createNodeZK(ZookeepServer.createZK(server));

    }

}
