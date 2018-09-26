package com.ftkj.server.rpc.route;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.ftkj.enums.ERPCType;
import com.ftkj.manager.RPCManager;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.RPCServerMethod;
import com.ftkj.server.rpc.RPCSource;
import com.ftkj.tool.zookeep.ZookeepServer;
import com.ftkj.util.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * @author tim.huang
 * 2017年4月6日
 * 路由服务器管理
 */
public class RouteServerManager {

    public final static Logger log = LogManager.getLogger(RouteServerManager.class);

    //节点map
    private static Map<String, RPCServer> clientMap = Maps.newConcurrentMap();
    //池map
    private static Map<String, List<RPCServer>> poolMap = Maps.newConcurrentMap();
    //主节点Map
    private static Map<String, RPCServer> masterMap = Maps.newConcurrentMap();

    private static AtomicLong index = new AtomicLong();

    private static ZookeepServer zk;

    private static BlockingQueue<RPCSource> taskQueue = Queues.newLinkedBlockingQueue();

    //
    private static Disruptor<DataEvent> dis;

    public static EventTranslatorOneArg<DataEvent, RPCSource> sourceTR;

    /**
     * 开启disruptor
     */
    @SuppressWarnings("unchecked")
    public static void start() throws Exception {
        dis = new Disruptor<DataEvent>(() -> new DataEvent(),
                1024, (run) -> {
            Thread th = new Thread(run);
            th.setName("Disruptor Thread");
            return th;
        }, ProducerType.MULTI, new YieldingWaitStrategy());

        sourceTR = new EventTranslatorOneArg<DataEvent, RPCSource>() {

            @Override
            public void translateTo(DataEvent event, long sequence, RPCSource arg0) {
                event.setSource(arg0);
            }
        };

        dis.handleEventsWith((a, b, c) -> RouteServerManager.write(a.getSource()));
        dis.start();
    }

    static class DataEvent {
        private RPCSource source;

        public DataEvent() {
            super();
        }

        public RPCSource getSource() {
            return source;
        }

        public void setSource(RPCSource source) {
            this.source = source;
        }

        @Override
        public String toString() {
            return "DataEvent [source=" + source + "]";
        }

    }

    public static void submit(RPCSource source) {
        if (dis == null) {
            log.error("disruptor 未启动");
            return;
        }
        dis.publishEvent(sourceTR, source);
    }

    /**
     * 节点连接成功后将自身信息添加进路由节点列表
     *
     * @param server
     */
    public static void putRPCServer(RPCServer server) {
        log.info("节点注册开始--->{}", server.toString());
        clientMap.put(server.getServerName(), server);
        resetServer();
        Watcher logicWatcher = new NodeWatcher(server);

        zk.exists(ZookeepServer.getLogicPath() + "/" + server.getServerName(), logicWatcher);
        log.debug("刷新节点信息成功");
    }

    private static class NodeWatcher implements Watcher {

        public RPCServer server;

        private NodeWatcher(RPCServer server) {
            this.server = server;
        }

        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == EventType.NodeDataChanged) {//数据变更，覆盖数据
                log.debug("逻辑节点，数据修改--" + event.getPath());
                RPCServer tmp = clientMap.get(server.getServerName());
                RPCServer n = JsonUtil.toObj(zk.get(event.getPath()), RPCServer.class);
                tmp.reset(n);
                resetServer();
                zk.exists(event.getPath(), this);
            } else if (event.getType() == EventType.NodeDeleted) {
				log.info("逻辑节点删除,节点名称:{}, path:{}",server.getServerName(), event.getPath());
				clientMap.remove(server.getServerName());
				resetServer();
            }
        }
    }

    /**
     * zk列表发生变动，重新刷新节点列表初始数据
     *
     * @param serverList
     */
    public static void updateRPCServer(List<RPCServer> serverList) {
        clientMap = serverList.stream().map(server -> clientMap.get(server.getServerName())).filter(server -> server != null).collect(Collectors.toMap(RPCServer::getServerName, (val) -> val));
        resetServer();
    }

    public static void checkRPCServerLive() {
        clientMap.values()
                .stream()
                .filter(rpcServer -> !rpcServer.getSession().isActive())
                .peek(node -> log.error("Node Connecton is Close ->[{}]", node.getServerName()))
                .forEach(node -> node.setOpen(0));
        //		closeList.stream().forEach(node->{
        //			String zkPath = ZookeepServer.getLogicPath()+"/"+node.getServerName();
        //			RPCServer n =  JsonUtil.toObj(zk.get(zkPath), RPCServer.class);
        //			node.reset(n);
        //			resetServer();
        //			Watcher logicWatcher = new NodeWatcher(node);
        //			zk.exists(zkPath,logicWatcher);
        //		});
    }

    /**
     * 重置服务列表
     */
    private static synchronized void resetServer() {
        Map<String, List<RPCServer>> tp = Maps.newHashMap();
        Map<String, RPCServer> mp = Maps.newHashMap();
        clientMap.values().forEach(rpc -> tp.computeIfAbsent(rpc.getPool(), (key) -> Lists.newArrayList()).add(rpc));
        clientMap.values().stream().filter(rpc -> rpc.isMaster()).forEach(rpc -> mp.put(rpc.getPool(), rpc));
        poolMap = tp;
        masterMap = mp;
    }

    /**
     * 启动路由消息线程
     */
    public static void startRoute() {
        try {
            start();
        } catch (Exception e) {
            log.error("启动报错{}", e);
            log.error(e.getMessage(), e);
        }
        //		Runnable codeJob = ()->{
        //			RPCSource source = null;
        //			while(true){
        //				try {
        //					source = taskQueue.take();
        //					RouteServerManager.write(source);
        //				} catch (InterruptedException e) {
        //					log.err("<--------消息队列出错------>");
        //				} catch(Exception ex){
        //					ex.printStackTrace();
        //					log.err("<--------路由转发消息异常------>{}",source.getMethodCode());
        //				}
        //			}
        //		};
        //		//
        //		int threadCount = Runtime.getRuntime().availableProcessors()+2;
        //		for(int i = 0 ; i < threadCount ; i ++){
        //			Executors.newSingleThreadExecutor().execute(codeJob);
        //		}
    }

    public static void setZK(ZookeepServer zk) {
        RouteServerManager.zk = zk;
    }

    /**
     * 消息派发
     *
     * @param source
     */
    private static void write(RPCSource source) throws Exception {
        RPCServerMethod method = null;
        ERPCType type = null;
        try {
            if (source.getMethodCode() == RPCManager.CallBack) {
                type = ERPCType.NONE;
            } else {
                method = InstanceFactory.get().getServerMethodByCode(source.getMethodCode());
                type = method.getType();
            }
            long ind = index.incrementAndGet();
            source.setTeamId(ind);
            if (type == ERPCType.ALL) {//池全推送
                List<RPCServer> nodes = poolMap.get(method.getPool());
                if (nodes != null && nodes.size() > 0) {//池中没有存活的节点
                    nodes.stream().filter(node -> node.isOpen()).forEach(node -> node.send(source));
                    return;
                }
            } else if (type == ERPCType.MASTER) {//主推送
                RPCServer node = masterMap.get(method.getPool());
                if (node != null && node.isOpen()) {
                    node.send(source);
                    return;
                }
            } else if (type == ERPCType.NONE && source.getReceive() == null) {//负载推送
                List<RPCServer> nodes = poolMap.get(method.getPool()).stream()
                        .filter(node -> node.isOpen())
                        .collect(Collectors.toList());//取出存活的节点
                if (nodes.size() > 0) {
                    RPCServer node = nodes.get((int) ind % (nodes.size()));
                    node.send(source);
                    return;
                }
            } else if (type == ERPCType.NONE) {//指定节点推送
            	List<RPCServer> nodes = source.getReceive().stream()
                        .map(rec -> clientMap.get(rec))
                        .filter(node -> node != null)
                        .filter(node -> node.isOpen())
                        .collect(Collectors.toList());
            	if (nodes.size() > 0) {
            		nodes.stream().filter(node -> node.isActive()).forEach(node -> node.send(source));
                    return;
                }
            } else if (type == ERPCType.ALLNODE) {//所有池中的所有节点
                List<RPCServer> nodes = Lists.newArrayList(clientMap.values());
                if (nodes != null && nodes.size() > 0) {//池中没有存活的节点
                    nodes.stream().filter(node -> node.isActive()).forEach(node -> node.send(source));
                    return;
                }
            }
        } catch (Exception e) {
            log.error(String.format("远程调用异常 source %s , mc %s msg %s", source, source.getMethodCode(), e.getMessage()), e);
        }

        log.error("无可用节点，导致请求抛出{} mc {} {}", source.toString(),
                source.getMethodCode(), source.getServerMethod());

    }

}
