package com.ftkj.server.rpc;

import com.ftkj.enums.ErrorCode;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** rpc(跨服)请求 */
public class RpcTask {
    private static final Logger log = LoggerFactory.getLogger(RpcTask.class);
    public static final int DEFAULT_TIMEOUT = 5;

    /** rpc 响应. 应答当前rpc请求 */
    public static <T extends Serializable> void resp(RpcResp<T> resp) {
        RPCMessageManager.responseMessage(resp);
    }

    /** rpc 响应. 应答当前rpc请求 */
    public static <T extends Serializable> void resp(T resp) {
        RPCMessageManager.responseMessage(new RpcResp<>(ErrorCode.Success, resp));
    }

    /** rpc 响应. 应答当前rpc请求 */
    public static <T extends Serializable> void resp(ErrorCode ret, T resp) {
        RPCMessageManager.responseMessage(new RpcResp<>(ret, resp));
    }

    /** rpc 响应. 应答当前rpc请求 */
    public static void resp(ErrorCode ret) {
        RPCMessageManager.responseMessage(new RpcResp<>(ret, null));
    }

    /** rpc 响应. 应答当前rpc请求 */
    public static void resp() {
        RPCMessageManager.responseMessage(new RpcResp<>(ErrorCode.Success, null));
    }

    /**
     * 发送一个有应答的跨服消息.
     *
     * @param xcode   跨服协议号
     * @param reqArgs 请求参数
     * @return T
     */
    public static <T> RpcResp<T> ask(int crossCode, Serializable... reqArgs) {
        return ask0(crossCode, null, DEFAULT_TIMEOUT, reqArgs);
    }

    /**
     * 发送一个有应答的跨服消息.
     *
     * @param xcode    跨服协议号
     * @param receiver 目标节点, 为 null 则按 code 随机选择目标节点
     * @param reqArgs  请求参数
     * @param <T>      期望返回
     * @return T
     */
    public static <T> RpcResp<T> ask(int crossCode, String receiver, Serializable... reqArgs) {
        return ask0(crossCode, receiver, DEFAULT_TIMEOUT, reqArgs);
    }

    /**
     * 发送一个有应答的跨服消息.
     *
     * @param xcode         跨服协议号
     * @param <T>           期望返回
     * @param receiver      目标节点, 为 null 则按 code 随机选择目标节点
     * @param timeOutSecond 要等待的最长时间(秒)
     * @param reqArgs       请求参数
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> RpcResp<T> ask0(int crossCode, String receiver, int timeOutSecond, Serializable... reqArgs) {
        ReqContext rc = new ReqContext<>(crossCode, receiver, reqArgs);

        RPCLinkedTask.build().appendTask((msgId, attachObj, args) -> {//req
            rc.msgId = msgId;
            log.trace("req rpc. xcode {} receiver {} reqid {} args {}", rc.crossCode, rc.receiver, rc.msgId, rc.reqArgs);
            RPCMessageManager.sendLinkedTaskMessage(rc.crossCode, rc.receiver, msgId, rc.reqArgs);
        }).appendTask((msgId, attachObj, resp) -> { // resp
            log.trace("resp rpc. xcode {} receiver {} reqid {} respid {}", rc.crossCode, rc.receiver, rc.msgId, msgId);
            // resp[0] RpcResp<T> or throwable
            try {
                rc.resp = (RpcResp<T>) resp[0];
            } catch (Exception e) {
                rc.cause = e;
            } finally {
                rc.latch.countDown();
            }
        }).start();

        try {
            boolean done = rc.latch.await(timeOutSecond, TimeUnit.SECONDS);
            if (rc.cause != null) {
                log.error(String.format("rpctask exception local: ctx %s msg %s ", rc, rc.cause.getMessage()), rc.cause);
                return RpcResp.create(ErrorCode.RPC_EXCEPTION_LOCAL, null);
            }
            if (!done) {
                log.error("rpctask timeout. code {} reqid {}", rc.crossCode, rc.msgId);
                return RpcResp.create(ErrorCode.RPC_TIMEOUT, null);
            }
            if (rc.resp != null && rc.resp.t instanceof Throwable) {
                rc.cause = (Throwable) rc.resp.t;
                log.error(String.format("rpctask exception remote: ctx %s msg %s ", rc, rc.cause.getMessage()), rc.cause);
                return RpcResp.create(ErrorCode.RPC_EXCEPTION_REMOTE, null);
            }
            return rc.resp;
        } catch (InterruptedException e) {
            log.warn("rpctask Interrupted {}", e.getMessage());
            return RpcResp.create(ErrorCode.RPC_EXCEPTION_REMOTE, null);
        }
    }

    /**
     * 发送一个无应答的跨服消息.
     *
     * @param xcode    跨服协议号
     * @param receiver 目标节点, 为 null 则按 code 随机选择目标节点
     * @param reqArgs  请求参数
     */
    public static void tell(int crossCode, String receiver, Serializable... reqArgs) {
        log.trace("req rpc. xcode {} receiver {} args {}", crossCode, receiver, reqArgs);
        RPCMessageManager.sendMessage(crossCode, receiver, reqArgs);
    }

    /** rpc 响应 */
    public static final class RpcResp<T> implements Serializable {
        private static final long serialVersionUID = 2092143493622997101L;
        public final ErrorCode ret;
        public final T t;

        public RpcResp(ErrorCode ret, T t) {
            this.ret = ret;
            this.t = t;
            if (t != null && !(t instanceof Serializable)) {
                throw new IllegalArgumentException("T must implements Serializable");
            }
        }

        public static <T> RpcResp create(ErrorCode ret, T t) {
            return new RpcResp<>(ret, t);
        }

        public static <T> RpcResp create(T t) {
            return new RpcResp<>(ErrorCode.Success, t);
        }

        public boolean isError() {
            return ret != ErrorCode.Success;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"ret\":" + ret +
                    '}';
        }
    }

    /** rpc 请求信息 */
    public static final class ReqContext<T> {
        private final CountDownLatch latch = new CountDownLatch(1);

        /** msgid {@link com.ftkj.manager.RPCManager#getId()} */
        private int msgId;
        /** {@link com.ftkj.server.CrossCode} */
        private final int crossCode;

        private final String receiver;
        private final Serializable[] reqArgs;
        //==== resp

        private RpcResp<T> resp;
        private Throwable cause;

        private ReqContext(int crossCode, String receiver, Serializable... reqArgs) {
            this.crossCode = crossCode;
            this.receiver = receiver;
            this.reqArgs = reqArgs;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"msgId\":" + msgId +
                    ", \"xcode\":" + crossCode +
                    ", \"receiver\":\"" + receiver + "\"" +
                    '}';
        }
    }
}
