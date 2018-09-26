package com.ftkj.server.instance;

import com.ftkj.db.conn.ao.AOSynManager;
import com.ftkj.db.conn.dao.BatchDataHelper;
import com.ftkj.server.socket.GameServerManager;
import com.ftkj.server.socket.SocketServerConfig;
import com.ftkj.tool.quartz.QuartzServer;
import com.ftkj.tool.quartz.annotation.JobExpression;
import com.ftkj.util.PathUtil;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 服务器初始化
 *
 * @author tim.huang
 * 2015年11月27日
 */
public class InitServer {
    private static final Logger log = LoggerFactory.getLogger(InitServer.class);

    public static void init(SocketServerConfig config, boolean isRoute) throws Throwable {
        String osName = System.getProperties().getProperty("os.name");
        log.debug("系统环境[{}] cfg {}", osName, config);
        AOSynManager.start();

        //add db batch
        BatchDataHelper.init();

        //加载Service层所有对象
        initInstance(config.getDAOPackgePath(), "BaseDAO", config.getPath(), config.getClassLoader());
        initInstance(config.getAOPackgePath(), "BaseAO", config.getPath(), config.getClassLoader());
        initInstance(config.getManagerPackgePath(), "BaseManager", config.getPath(), config.getClassLoader());
        initInstance(config.getActiveManagerPackgePath(), "BaseManager", config.getPath(), config.getClassLoader());
        //通用包加载
        initInstance(config.getCommonPackgePath() + ".manager.common", "BaseManager", config.getPath(), config.getClassLoader());
        initInstance(config.getCommonPackgePath() + ".db.ao.common", "BaseAO", config.getPath(), config.getClassLoader());
        initInstance(config.getCommonPackgePath() + ".db.dao.common", "BaseDAO", config.getPath(), config.getClassLoader());
        initJob(config.getJobPackgePath(), config.getPath(), config.getClassLoader());
        //
        if (!isRoute) {
            InstanceFactory.get().executAfter();
            QuartzServer.get().start();
        }
        GameServerManager.start(config.getPoolName());

    }

    /**
     * 初始化
     *
     * @param servicePath
     * @throws Exception
     */
    private static void initInstance(String servicePath, String superClassName, String path, ClassLoader cl) throws Exception {
        if ("".equals(servicePath)) {
            return;
        }
        try {
            //
            log.debug("初始化包路径:[{}],项目路径:[{}]", servicePath, path);
            List<String> cmdName = PathUtil.getAllName(servicePath);
            log.debug("实例化路径:[{}]", cmdName);
            String replaceStr = getReplaceStr(path);
            for (String cmd : cmdName) {
                try {
                    cmd = cmd.replace(replaceStr, "").replace("/", ".");
                    Class<?> cla = cl.loadClass(cmd);
                    Class<?> father = cla.getSuperclass();
                    //			log.error("----------------->"+cmd);
                    while (father != null) {
                        if (father != null && father.getSimpleName().equals(superClassName) && !Modifier.isAbstract(cla.getModifiers())) {
                            InstanceFactory.get().put(cla.newInstance());
                            break;
                        } else {
                            father = father.getSuperclass();
                        }
                    }
                } catch (Throwable e) {
                    throw new RuntimeException("init class " + cmd, e);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException("init path " + servicePath, e);
        }
    }

    private static void initJob(String servicePath, String path, ClassLoader cl) throws Exception {
        if ("".equals(servicePath)) { return; }
        //
        log.debug(servicePath);
        List<String> cmdName = PathUtil.getAllName(servicePath);
        String replaceStr = getReplaceStr(path);
        for (String cmd : cmdName) {
            cmd = cmd.replace(replaceStr, "").replace("/", ".");
            Class<?> cla = cl.loadClass(cmd);
            Class<?> father = cla.getSuperclass();
            JobExpression expression = cla.getAnnotation(JobExpression.class);
            if (expression != null && "BaseJob".equals(father.getSimpleName())) {
                @SuppressWarnings("unchecked")
                JobDetail job = JobBuilder.newJob((Class<? extends Job>) cla)
                        .withIdentity(expression.name(), expression.group())
                        .build();
                Trigger trigger = TriggerBuilder
                        .newTrigger()
                        .withIdentity(expression.name(), expression.group())
                        .withSchedule(CronScheduleBuilder.cronSchedule(expression.expression()))
                        .build();
                QuartzServer.get().addJob(job, trigger);

            }
        }
    }

    private static String getReplaceStr(String source) {
        String osName = System.getProperties().getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") != -1) { //windows环境去掉开头的/
            source = source.substring(1, source.length());
        }
        return source;
    }

}
