package com.ftkj.server.socket;

import com.ftkj.manager.CloseOperation;
import com.ftkj.server.ServerStat;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.server.proto.Request;
import com.ftkj.server.proto.Response;
import com.ftkj.server.proto.ResponseSession;
import com.ftkj.server.rpc.RPCSource;
import com.ftkj.server.syn.SynInvokeThread;
import com.ftkj.util.ThreadPoolUtil;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GameServerManager {
    private static final Logger log = LoggerFactory.getLogger(GameServerManager.class);
    private static final int MAX_THREAD = Math.max(8, Runtime.getRuntime().availableProcessors() * 2);
    /** 客户端与服务器间的逻辑处理线程池 */
    private static ExecutorService[] synRequestPool = new ExecutorService[MAX_THREAD];
    /** 服务器间的逻辑处理线程池 */
    private static ExecutorService rpcRequestPool = ThreadPoolUtil.newFixedThreadPool(MAX_THREAD, "rpc_req");
    /** 客户端请求队列 */
    private static BlockingQueue<Request> requests = new LinkedBlockingQueue<>(5000 * 50);
    /** 服务器响应队列 */
    private static BlockingQueue<ResponseSession> responses = new LinkedBlockingQueue<>(5000 * 50);

    public GameServerManager() {
    }

    public static void start(String synPoolName) {
        for (int i = 0; i < MAX_THREAD; i++) {
            synRequestPool[i] = ThreadPoolUtil.newFixedThreadPool(1, synPoolName + "_syn_req");
        }
        new SynRequestThread(synPoolName + "_req").start();
        new ResponseThread(synPoolName + "_resp").start();
//        InstanceFactory.get().addCloseOperationList(() -> DBManager.run(true));
        //钩子, 在jvm关闭的时候执行,业务线程的完整性
        Runtime.getRuntime().addShutdownHook(new ServerCloseThread());
        
        try {
//			TimeUnit.SECONDS.sleep(60);
//			System.exit(0);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }

    /** 获得等待运行的请求数量 */
    public static int getWaitRunReqSize() {
        int num = 0;
        for (ExecutorService es : synRequestPool) {
            ThreadPoolExecutor tp = (ThreadPoolExecutor) es;
            num += tp.getQueue().size();
        }
        return num;
    }

    /** 获得等待运行的响应数量 */
    public static int getWaitRunRespSize() {
        return responses.size();
    }

    public static void addRequest(Request req) {
        try {
            requests.put(req);
        } catch (InterruptedException e) {
            log.error("GameServerManager put error:" + e.getMessage(), e);
        }
    }

    public static void addResponse(Response res, IoSession session) {
        try {
            ServerStat.incResp(res.getServiceCode(), res.getBytesSize());
            responses.put(new ResponseSession(session, res));
        } catch (InterruptedException e) {
            log.error("GameServerManager put error:" + e.getMessage(), e);
        }
    }

    // 异步处理
    private static class SynRequestThread extends Thread {

        SynRequestThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Request req = requests.take();
                    // 开启线程处理客户端的业务逻辑请求
                    if (req instanceof RPCSource) {
                        rpcRequestPool.submit(new SynInvokeThread(req));
                    } else {
                        synRequestPool[(int) (req.getTeamId() % MAX_THREAD)]
                            .submit(new SynInvokeThread(req));
                    }
                } catch (Exception e) {
                    log.error("SynRequestThread " + e.getMessage(), e);
                }
            }
        }
    }

    // 下行线程
    private static class ResponseThread extends Thread {

        ResponseThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ResponseSession res = responses.take();
                    if (res.getSession() != null && res.getSession().isConnected()) {
                        res.getSession().write(res.getResponse());
                    }
                } catch (Exception e) {
                    log.error("ResponseThread" + e.getMessage(), e);
                }
            }
        }
    }

    private static class ServerCloseThread extends Thread {
        public ServerCloseThread() {
        }

        @Override
        public void run() {
            List<CloseOperation> closeList = InstanceFactory.get().getCloseOperationList();
            log.info("Close size {}", closeList.size());
            for (CloseOperation obj : closeList) {
                try {
                    log.info("Close Operation Class -> [{}]", obj.getClass().getSimpleName());
                    obj.close();
                    log.info("Close Operation Class done -> [{}]", obj.getClass().getSimpleName());
                } catch (Exception e) {
                	log.error("Server Close Error:" + e.getMessage(), e);
                }catch (Throwable e) {
                	log.error("Server Close Error:{}", e.getMessage());
				}
            }
        }

    }

}
