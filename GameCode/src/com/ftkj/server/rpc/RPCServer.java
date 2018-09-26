package com.ftkj.server.rpc;

import com.ftkj.server.GameSource;
import com.ftkj.util.JsonUtil;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年3月16日
 * 服务器信息
 */
public class RPCServer implements Serializable, RpcMsg {
    private static final Logger log = LoggerFactory.getLogger(RPCServer.class);
    private static final long serialVersionUID = 2L;
    /** 分布式池名称 */
    private String pool;
    /** 节点名称 */
    private String serverName;
    /** 节点所在IP */
    private String ip;
    /** 节点端口 */
    private int port;
    /** 是否开启   0关闭，1开启 */
    private int open;
    /** 是否主服务器 */
    private boolean master;
    /** 服务器id */
    private int sid;
    /** 会话标识 */
    private transient IoSession session;

    public RPCServer() {
    }

    public RPCServer(String pool, String serverName, String ip, int port, int open, int sid) {
        super();
        this.pool = pool;
        this.serverName = serverName;
        this.ip = ip;
        this.port = port;
        this.open = open;
        this.sid = sid;
        this.master = false;
    }

    public String getAddress() {
        if (GameSource.net) {
            return serverName + "." + pool + "." + GameSource.topAddress;
        } else {
            return ip;
        }

    }

    public void reset(RPCServer server) {
        this.open = server.getOpen();
        this.master = server.isMaster();
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public void send(Serializable obj) {
        try {
            this.session.write(obj);
        } catch (Exception e) {
            log.error("跨服协议发送异常{}", obj.toString());
        }
    }

    public int getSid() {
        return sid;
    }

    public boolean isOpen() {
        return this.open == 1;
    }

    public boolean isActive() {
        boolean active = this.session != null ? this.session.isActive() : false;
        log.debug("[{}]--[{}]", this.serverName, active);
        return isOpen() && active;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public String toJson() {
        return JsonUtil.toJson(this);
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getServerName() {
        return serverName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getOpen() {
        return open;
    }

    @Override
    public String toString() {
        return "{" +
            "\"pool\":\"" + pool + "\"" +
            ", \"serverName\":\"" + serverName + "\"" +
            ", \"ip\":\"" + ip + "\"" +
            ", \"port\":" + port +
            ", \"open\":" + open +
            ", \"master\":" + master +
            ", \"sid\":" + sid +
            ", \"session\":" + session +
            '}';
    }

}
