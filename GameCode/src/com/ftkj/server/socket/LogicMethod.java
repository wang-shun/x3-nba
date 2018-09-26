package com.ftkj.server.socket;

import com.ftkj.server.MessageManager;
import com.ftkj.server.proto.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 客户端请求逻辑方法
 *
 * @author tim.huang
 * 2015年11月28日
 */
public class LogicMethod implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(LogicMethod.class);
    private ServerMethod method;
    private Object[] attributeArray;

    public LogicMethod(ServerMethod method, Object[] attributeArray) {
        super();
        this.method = method;
        this.attributeArray = attributeArray;
    }

    public LogicMethod(Method method, Object instance, Object[] attributes, int serviceCode) {
        this.method = new ServerMethod(serviceCode, method, instance, instance.getClass().getSimpleName() + "-[" + method.getName() + "]");
        this.attributeArray = attributes;
    }

    public LogicMethod(Method method, Object instance, Object[] attributes) {
        this.method = new ServerMethod(0, method, instance, instance.getClass().getSimpleName() + "-[" + method.getName() + "]");
        this.attributeArray = attributes;
    }

    public ServerMethod getMethod() {
        return method;
    }

    public void run() {
        try {
            this.invoke();
        } catch (Exception e) {
            log.error("LogicMethod invoke error:" + e.getMessage(), e);
        }
    }

    /**
     * 执行Service层的逻辑
     *
     * @throws Exception
     */
    public void invoke() throws Exception {
        log.debug("req 玩家->[{}]调用方法->[{}]参数[{}]", MessageManager.getTeamId(), method.getName(), Request.attrsStr(attributeArray));
        method.getMethod().invoke(method.getInstanceObject(), attributeArray);
    }

    public Object invokeCallBack() throws Exception {
        return method.getMethod().invoke(method.getInstanceObject(), attributeArray);
    }

}
