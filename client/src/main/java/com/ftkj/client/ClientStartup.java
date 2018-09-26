package com.ftkj.client;

import com.ftkj.client.robot.ZGameRobot;
import com.ftkj.util.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 客户端压力测试入口.
 * <p>
 * <p>vm optionals</p>
 * <pre>{@code
 * -XX:+UseG1GC
 * -Xms2g
 * -Xmx2g
 * -XX:MetaspaceSize=128m
 * -XX:MaxMetaspaceSize=256m
 * -Xss1m
 * -XX:+HeapDumpOnOutOfMemoryError
 * -XX:HeapDumpPath=log
 * -XX:-OmitStackTraceInFastThrow
 * -verbose:gc
 * -XX:+PrintGCDetails
 * -XX:+PrintGCTimeStamps
 * -XX:+PrintGCDateStamps
 * -XX:+UseGCLogFileRotation
 * -XX:NumberOfGCLogFiles=100
 * -XX:GCLogFileSize=10M
 * -Xloggc:gc-client-bm.log
 * -Dlog4j.configurationFile=sub/benchmark/log4j2-bm.xml
 * -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
 * -Dcom.sun.management.jmxremote
 * -Dcom.sun.management.jmxremote.authenticate=false
 * -Dcom.sun.management.jmxremote.ssl=false
 * -Dcom.sun.management.jmxremote.port=10103
 * -Djava.rmi.server.hostname=127.0.0.1
 * }</pre>
 */
public class ClientStartup {
    private static final Logger log = LoggerFactory.getLogger(ClientStartup.class);

    public static void main(String[] args) throws Throwable {
        ActionConsole.initAction();
        //11000000002l
        //		AtomicLong id = new AtomicLong(0l);
        //		List<XGameRobot> robots = Stream.generate(()->new XGameRobot(id.incrementAndGet())).limit(30).collect(Collectors.toList());
        //
        //		robots.parallelStream().forEach(robot -> {
        //			robot.conn("121.10.118.38", 8038);
        //			robot.run();
        //		});
        //		ZGameRobot rt = new ZGameRobot(100005);
        //		rt.conn("192.168.10.181", 8038);
        //		rt.conn("192.168.12.84", 8038);
        //		CM.init(false);

        long tid = 10120002000000l;
        for (int i = 0; i < 2000; i++) {
            ZGameRobot rt2 = new ZGameRobot(tid++);
            log.info("init team {}", rt2.getTeamId());
            rt2.conn("192.168.10.181", 8038);
            //			rt2.conn(IPUtil.localIP, 8038);
        }

        ThreadPoolUtil.newScheduledPool("ClientStatThread", 1)
                .scheduleAtFixedRate(() -> log.info("{}", ClientStat.methodInfo()),
                        1000, 5000, TimeUnit.MILLISECONDS);

        //		ActionConsole.getAction(-99).run(rt2);

        //		XGameRobot robot = new XGameRobot(1l);
        //		robot.conn("121.10.118.25",8038);
        //		sendMail(robot);
        //		List<PropSimple> gift = new ArrayList<>();
        //		gift.add(new PropSimple(1003, 500));
        //		String props = PropSimple.getPropStringByList(gift);
        //		ActionConsole.getAction(ServiceCode.GMManager_SendMail).run(robot,0,"欢迎来到百将江湖","百将江湖，感谢您的支持!",props);
        //		int type,String title,String context,String props
        //		robot.run();
    }

    //	public static void sendMail(XGameRobot robot){
    //		List<PropSimple> gift = new ArrayList<>();
    //		gift.add(new PropSimple(1003, 5000));
    //		String props = PropSimple.getPropStringByList(gift);
    //		ActionConsole.getAction(ServiceCode.GMManager_SendMail).run(robot,0,"欢迎来到百将江湖","百将江湖，感谢您的支持!",props);
    //	}

}
