package com.ftkj.manager;

import com.ftkj.annotation.RPCMethod;
import com.ftkj.annotation.UnCheck;
import com.ftkj.enums.ErrorCode;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.MessageManager;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.server.instance.InstanceOperation;
import com.ftkj.tool.redis.JedisUtil;

import com.google.protobuf.GeneratedMessage;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * 逻辑管理基类
 *
 * @author tim.huang
 * 2015年11月27日
 */
public abstract class BaseManager implements InstanceOperation {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    public static final int DEFAULT_INIT_AND_SHUTDOWN_ORDER = 100_0000;
    protected JedisUtil redis;

    protected BaseManager() {
        this(true);
    }

    protected BaseManager(boolean init) {
        if (init) {
            redis = InstanceFactory.get().getInstance(JedisUtil.class);
            //		redis = JedisUtil.getInstance();
            initMethod();
        }
    }

    /**
     * 初始化客户端接口
     */
    private void initMethod() {
        //
        //		String objectName = this.getClass().getSimpleName();
        for (Method method : getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            ClientMethod cm = method.getAnnotation(ClientMethod.class);
            if (cm != null) {
                //				String key = objectName+PathUtil.POINT+method.getName();
                InstanceFactory.get().putMethod(cm.code(), method, this);
                UnCheck uc = method.getAnnotation(UnCheck.class);
                if (uc != null) {
                    InstanceFactory.get().putUnCheck(cm.code());
                }
            }
            //初始化跨服节点接口实现类
            RPCMethod rm = method.getAnnotation(RPCMethod.class);
            if (rm != null) {
                InstanceFactory.get().putRPCMethod(rm.code(), method, this, rm.pool(), rm.type());
            }
        }
        //
    }

    @Override
    public int getOrder() {
        return DEFAULT_INIT_AND_SHUTDOWN_ORDER;
    }

    protected long getTeamId() {
        return MessageManager.getTeamId();
    }

    protected IoSession getSession() {
        return MessageManager.getSession();
    }

    protected int getRid() {
        return MessageManager.getReqId();
    }

    protected void sendMessage(GeneratedMessage data) {
        MessageManager.sendMessage(data);
    }

    protected void sendMessage(ErrorCode code) {
        MessageManager.sendMessage(DefaultData.newBuilder().setCode(code.code).build());
    }

    protected void sendErrorCode(ErrorCode code) {
        MessageManager.sendMessage(DefaultData.newBuilder().setCode(code.code).build());
    }

    protected void sendMsg(ErrorCode ret) {
        MessageManager.sendMessage(ret);
    }

    @Deprecated
    protected void sendMessage(GeneratedMessage data, int serviceCode) {
        MessageManager.sendMessage(data, serviceCode);
    }

    protected void sendMessage(long teamId, GeneratedMessage data, int serviceCode) {
        MessageManager.sendMessage(teamId, data, serviceCode);
    }

    protected void sendMessage(long teamId, GeneratedMessage data, int serviceCode, int rid) {
        MessageManager.sendMessage(teamId, data, serviceCode, rid);
    }

    protected void sendMessage(Collection<User> users, GeneratedMessage data, int serviceCode) {
        MessageManager.sendMessage(users, data, serviceCode);
    }

    protected void sendMessageTeamIds(List<Long> teamIds, GeneratedMessage data, int serviceCode) {
        MessageManager.sendMessageTeamIds(teamIds, data, serviceCode);
    }

    protected void sendMessage(String service, GeneratedMessage data, int serviceCode) {
        MessageManager.sendMessage(service, data, serviceCode);
    }

    protected void sendMessage(long teamId, String service, GeneratedMessage data, int serviceCode) {
        MessageManager.sendMessage(teamId, service, data, serviceCode);
    }

    protected void sendMessage(String service, List<Long> filterTeam, GeneratedMessage data, int serviceCode) {
        MessageManager.sendMessage(service, filterTeam, data, serviceCode);
    }
}
