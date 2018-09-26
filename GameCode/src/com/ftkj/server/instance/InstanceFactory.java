package com.ftkj.server.instance;

import com.ftkj.annotation.IOC;
import com.ftkj.db.conn.ao.AOInvocationHandler;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.conn.ao.SynAO;
import com.ftkj.db.conn.dao.Database;
import com.ftkj.enums.ERPCType;
import com.ftkj.manager.CloseOperation;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.script.ScriptEngine;
import com.ftkj.script.StartupContextImpl;
import com.ftkj.server.GameSource;
import com.ftkj.server.rpc.IZKMaster;
import com.ftkj.server.rpc.RPCServerMethod;
import com.ftkj.server.socket.ServerMethod;
import com.ftkj.tool.redis.JRedisServer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 服务器对象工厂
 *
 * @author tim.huang
 * 2015年11月27日
 */
public class InstanceFactory {
    private static final Logger log = LoggerFactory.getLogger(InstanceFactory.class);
    //	private static Logger log = LoggerFactory.getLogger(InstanceFactory.class);
    /** 服务器反射用方法 */
    private Map<Integer, ServerMethod> METHODS = Maps.newHashMap();
    private Map<String, Database> DATABASES = Maps.newHashMap();
    /** 接口的所有实现类 */
    private Map<String, Object> IOCS = Maps.newHashMap();
    /** 停服操作 */
    private List<CloseOperation> CLOSE = Lists.newArrayList();
    /** 无需登录可以调用的协议 */
    private List<Integer> CODES = Lists.newArrayList();
    /** 离线操作 */
    private List<OfflineOperation> OFFLINES = Lists.newArrayList();
    /** 实例操作 */
    private List<InstanceOperation> instanceOperation = Lists.newArrayList();

    private InstanceFactory() {
    }

    public static InstanceFactory get() {
        return ObjectFactoryInstance.instance;
    }

    private static class ObjectFactoryInstance {
        private static InstanceFactory instance = new InstanceFactory();
    }

    void executAfter() {
        List<Object> instanceList = new ArrayList<>(IOCS.values());
        instanceOperation = new ArrayList<>();
        //增加ao层的动态代理
        for (Object obj : instanceList) {
            if (obj instanceof BaseAO) {
                AOInvocationHandler handler = new AOInvocationHandler((BaseAO) obj);
                Class<?>[] ifs = obj.getClass().getInterfaces();
                Arrays.stream(ifs).forEach(cla -> {
                    Object go = obj;
                    if (cla.getSuperclass() != null && cla.getSuperclass().getSimpleName().equals(SynAO.class.getSimpleName())) {
                        //@param 一个ClassLoader对象，定义了由哪个ClassLoader对象来对生成的代理对象进行加载
                        //@param一个Interface对象的数组，表示的是我将要给我需要代理的对象提供一组什么接口，如果我提供了一组接口给它，那么这个代理对象就宣称实现了该接口(多态)，这样我就能调用这组接口中的方法了
                        //@param handler 一个InvocationHandler对象，表示的是当我这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上
                        go = Proxy.newProxyInstance(obj.getClass().getClassLoader()
                            , new Class<?>[]{cla}, handler);
                    }
                    InstanceFactory.get().put(cla.getSimpleName(), go);
                });
            }
        }

        List<IZKMaster> materInitList = Lists.newArrayList();
        //注入各个类的成员变量属性
        for (Object obj : instanceList) {
            if (obj instanceof InstanceOperation) {
                instanceOperation.add((InstanceOperation) obj);
            }

            if (obj instanceof IZKMaster) {
                materInitList.add((IZKMaster) obj);
            }

            Class<?> cla = obj.getClass();
            for (; cla != Object.class; cla = cla.getSuperclass()) {
                Field[] fields = cla.getDeclaredFields();
                for (Field field : fields) {
                    IOC ioc = field.getAnnotation(IOC.class);
                    if (ioc == null) {
                        continue;
                    }
                    try {
                        //启用和禁用访问安全检查的开关
                        field.setAccessible(true);
                        Object val = InstanceFactory.get().getInstance(field.getType());
                        field.set(obj, val);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        log.error("instance error:", e);
                    }
                }
            }
        }
        //
        instanceOperation.sort(Comparator.comparingInt(InstanceOperation::getOrder));
        CLOSE.sort(Comparator.comparingInt(CloseOperation::getOrder));
        int maxTime = 3000;
        for (InstanceOperation io : instanceOperation) {
            long start = System.currentTimeMillis();
            io.instanceAfter();
            long initTime = System.currentTimeMillis() - start;
            if (initTime > maxTime) {
                log.info("class {} instanceAfter method elapsed time {}ms > {}ms",
                    io.getClass().getCanonicalName(), initTime, maxTime);
            }
            io.initConfig();
            long cfgTime = System.currentTimeMillis() - start - initTime;
            if (cfgTime > maxTime) {
                log.info("class {} initConfig method elapsed time {}ms > {}ms",
                    io.getClass().getCanonicalName(), initTime, maxTime);
            }
        }
        for (IZKMaster master : materInitList) {
            master.masterInit(GameSource.serverName);
}

    }

public void resetConfig() {
    instanceOperation.stream().forEach(io -> io.initConfig());
}

    //
    public void put(Object service) {
        put(service.getClass().getSimpleName(), service);
    }

    public void put(String key, Object service) {
        log.debug("对象已经加载成功:[{}]", key);
        IOCS.put(key, service);
        //		log.debug("=======================================================");
        if (service instanceof CloseOperation) {
            log.info("添加服务器停服钩子操作[{}]", key);
            CLOSE.add((CloseOperation) service);
        }
        if (service instanceof OfflineOperation) {
            log.debug("添加玩家离线操作[{}]", key);
            OFFLINES.add((OfflineOperation) service);
            OFFLINES.sort(Comparator.comparingInt(OfflineOperation::offlineOrder));
        }
    }

    public void putMethod(int key, Method method, Object instance) {
        String name = instance.getClass().getSimpleName() + "-[" + method.getName() + "]";
        log.debug("Service接口加载成功:" + name + "-code->" + key);
        METHODS.put(key, new ServerMethod(key, method, instance, name));
    }

    public void putRPCMethod(int key, Method method, Object instance, String pool, ERPCType type) {
        String name = instance.getClass().getSimpleName() + "-[" + method.getName() + "]";
        log.debug("Service接口加载成功:" + name + "-code->" + key);
        METHODS.put(key, new RPCServerMethod(key, method, instance, name, pool, type));
    }

    public void putUnCheck(int key) {
        log.debug("无需验证接口加载成功:" + key);
        CODES.add(key);
    }

    @SuppressWarnings("unchecked")
    public <T extends ServerMethod> T getServerMethodByCode(int keyCode) {
        return (T) METHODS.getOrDefault(keyCode, ServerMethod.REJECT_METHOD);
    }

    public Database getDataBaseByKey(String keyCode) {
        return DATABASES.get(keyCode);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstanceWithoutNew(Class<T> cla) {
        return (T) IOCS.get(cla.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> cla) {
        T t = (T) IOCS.get(cla.getSimpleName());
        if (t == null) {
            try {
                //				if(cla.getInterfaces()[0] != null && cla.getInterfaces()[0].equals(IRPCService.class)){//RPC接口动态代理实例化
                //					RPCInvocationHandler handler = new RPCInvocationHandler();
                //					t = (T)Proxy.newProxyInstance(IRPCService.class.getClassLoader(), new Class<?>[] { cla }, handler);
                //				}else{//走正常实例化
                t = cla.newInstance();
                //				}
                put(t);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("InstanceFactory-getInstance: " + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return t;
    }

    /**
     * 取某个接口的所有实现类
     *
     * @param cal
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getInstanceList(Class<T> cal) {
        List<T> list = Lists.newArrayList();
        for (Object o : IOCS.values()) {
            // 自己接口
            if (Arrays.stream(o.getClass().getInterfaces()).anyMatch(i -> i == cal)) {
                list.add((T) o);
            }
            // 父类接口
            if (Arrays.stream(o.getClass().getSuperclass().getInterfaces()).anyMatch(i -> i == cal)) {
                list.add((T) o);
            }
        }
        return list;
    }

    public List<CloseOperation> getCloseOperationList() {
        return this.CLOSE;
    }

    public List<CloseOperation> addCloseOperationList(CloseOperation co) {
        CLOSE.add(co);
        CLOSE.sort(Comparator.comparingInt(CloseOperation::getOrder));
        return this.CLOSE;
    }

    public List<OfflineOperation> getOfflineList() {
        return this.OFFLINES;
    }

    public void addDataBaseResource(String key, Object obj) {
        if (obj instanceof JRedisServer) {
            put(obj);
        } else if (obj instanceof Database) {
            DATABASES.put(key, (Database) obj);
        } else {
            put(obj);
        }
    }

    private void startupContainer(String rcFile) throws Exception {
        StartupContextImpl scc = new StartupContextImpl();
        ScriptEngine se = new ScriptEngine();
        if (!new File(rcFile).exists()) {
            log.error("can not found config file:" + rcFile);
            return;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rcFile), "UTF-8"));
        se.execute(in, scc);
    }

    public void runInitScript(String rcFile) {
        try {
            log.debug("run config file:[{}]", rcFile);
            startupContainer(rcFile);
            //初始化好服务器基础属性，初始化日志全局属性
            //			log.initThreadContext();
        } catch (Exception e) {
            log.error("error when run command.[{}]", e.getMessage());
        }
    }

    /**
     * 是否无需验证
     *
     * @param key
     * @return
     */
    public boolean isUnCheck(int key) {
        return this.CODES.contains(key);
    }

}
