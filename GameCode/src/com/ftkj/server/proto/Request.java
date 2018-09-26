package com.ftkj.server.proto;

import com.ftkj.manager.RPCManager;
import com.ftkj.server.GameSource;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.server.rpc.RPCSource;
import com.ftkj.server.socket.ServerMethod;
import com.ftkj.util.StringUtil;
import com.google.protobuf.TextFormat;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Request implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Request.class);
    private static final long serialVersionUID = 2L;

    private IoSession session;
    private long startTime;
    private long teamId = 0;
    private int reqId;
    private int methodCode;
    //	LogicMethod method;
    private ServerMethod serverMethod = null;
    private Serializable[] attributeArray = null;
    /** 消息长度 */
    private transient int msgLength;

    //	public static final String SERVICE_CODE_KEY = "servicecode";
    private static final String INT = int.class.getSimpleName();
    private static final String FLOAT = float.class.getSimpleName();
    private static final String LONG = long.class.getSimpleName();

    //客户端心跳请求
    public static final int Socket_KeepAlive = -99;

    //客户端心跳响应
    public static final int Socket_KeepAlive_CallBack = -100;

    //服务器心跳请求
    public static final int Socket_Server_KeepAlive = -97;

    //服务器心跳响应
    public static final int Socket_Server_KeepAlive_CallBack = -98;

    public Request() {
    }

    public Request(int methodCode, int reqId, Serializable[] attributeArray) {
        this.methodCode = methodCode;
        this.attributeArray = attributeArray;
        this.reqId = reqId;
    }

    public Request(IoSession session, String data, boolean isInit, int reqId, int serviceCode, int msgLength) {
        setSession(session);
        this.reqId = reqId;
        this.methodCode = serviceCode;
        //在放入队列前，初始化所需信息，降低吞吐量，提高业务处理速度
        if (isInit && serviceCode != Socket_KeepAlive) {
            setData(data, serviceCode);
        }
        this.msgLength = msgLength;
    }

    private void setData(String data, int serviceCode) {
        try {
            setData0(data, serviceCode);
        } catch (Exception e) {
            log.error("set date tid {} code {} data {} session {}", teamId, serviceCode, data, session);
        }
    }

    private void setData0(String data, int serviceCode) {
        serverMethod = InstanceFactory.get().getServerMethodByCode(serviceCode);
        if (serverMethod.getKeyCode() == 0) {
            return;
        }
        int argLen = serverMethod.getAttributeType().length;
        if (argLen <= 0) {
            return;
        }
        String[] s = StringUtil.toStringArray(data, "Ω");
        if (s.length != argLen) {
            log.warn("set date tid {} code {} data {} length {} {} session {}", teamId, serviceCode, data,
                    s.length, argLen, session);
            return;
        }
        attributeArray = new Serializable[argLen];
        for (int i = 0; i < s.length; i++) {
            attributeArray[i] = getAttirbute(serverMethod.getAttributeType()[i], s[i]);
        }
    }

    public void invoke() {
        if (serverMethod == null) {//RPC推送，method默认为空，从本地注册的方法中重新取出来
            serverMethod = InstanceFactory.get().getServerMethodByCode(methodCode);
        }

        //本地调用，RPC推送都没找到，异常code
        if (methodCode == RPCManager.CallBack && this instanceof RPCSource) {//路由转发过来的链接异步响应
            RPCManager.requestExceute(getReqId(), getAttributeArray());
        } else {
            if (serverMethod == null || serverMethod.getMethod() == null || serverMethod.getInstanceObject() == null) {
                log.error("unknown code {}", methodCode);
                return;
            }
            final Object[] args = attributeArray;
            try {
                serverMethod.getMethod().invoke(serverMethod.getInstanceObject(), args);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Throwable ex = e;
                if (e instanceof InvocationTargetException) {
                    ex = ((InvocationTargetException) e).getTargetException();
                }
                log.error(String.format("method invoke tid %s code %s method %s argslen %s args %s msg %s session %s",
                        teamId, getMethodCode(), serverMethod.getMethod(), args != null ? args.length : 0,
                        getAttrsSimpleString(), ex != null ? ex.getMessage() : "ex null", session), ex);
            }
        }
    }

    private Serializable getAttirbute(Class<?> cla, String val) {
        String name = cla.getSimpleName();
        if (name.equals(Request.INT)) {
            return Integer.parseInt(val);
        } else if (name.equals(Request.FLOAT)) {
            return Float.parseFloat(val);
        } else if (name.equals(Request.LONG)) {
            return Long.parseLong(val);
        } else { return val; }
    }

    private void setSession(IoSession session) {
        this.session = session;
        Long teamid = (Long) session.getAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID);
        if (teamid != null) {
            this.teamId = teamid;
        }
        //		else if(GameSource.isDebug){
        //			teamid = Math.abs(new Random().nextLong());
        //			this.teamId = teamid;
        //			session.setAttribute("teamId",teamid);
        //		}
    }

    public int getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(int methodCode) {
        this.methodCode = methodCode;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public IoSession getSession() {
        return session;
    }

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public ServerMethod getServerMethod() {
        return serverMethod;
    }

    public Serializable[] getAttributeArray() {
        return attributeArray;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public long end() {
        return System.currentTimeMillis() - this.startTime;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public String getAttrsSimpleString() {
        return attrsStr(attributeArray);
    }

    private static final int STR_MAX_ELEMENT = 12;

    public static String attrsStr(Object[] attrs) {
        if (attrs == null || attrs.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = Math.min(attrs.length, STR_MAX_ELEMENT);
        for (int i = 0; i < len; i++) {//pre 12
            elementToStr(sb, attrs[i]);
            if (i < len - 1) {
                sb.append(',').append(' ');
            }
        }
        return sb.toString();
    }

    private static void elementToStr(StringBuilder sb, Object obj) {
        if (obj instanceof List) {
            collectionToString(sb, (List<?>) obj);
        } else if (obj instanceof Set) {
            collectionToString(sb, (Set<?>) obj);
        } else if (obj instanceof com.google.protobuf.Message) {
            sb.append(TextFormat.shortDebugString((com.google.protobuf.Message) obj));
        } else {
            sb.append(obj);
        }
    }

    private static void collectionToString(StringBuilder sb, Collection<?> c) {
        Iterator<?> it = c.iterator();
        if (!it.hasNext()) {
            sb.append("[]");
            return;
        }

        sb.append('[');
        int count = 0;
        while (it.hasNext() && count < STR_MAX_ELEMENT) {
            Object e = it.next();
            if (e == c) {
                sb.append("(this Collection)");
            } else {
                elementToStr(sb, e);
            }
            if (!it.hasNext()) {
                sb.append(']');
                return;
            }
            sb.append(',').append(' ');
            count++;
        }
        if (it.hasNext()) {
            sb.append("and other ").append(c.size() - STR_MAX_ELEMENT).append(" elements");
        }
    }

}
