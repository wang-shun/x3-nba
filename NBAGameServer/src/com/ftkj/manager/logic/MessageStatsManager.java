package com.ftkj.manager.logic;

import com.codahale.metrics.MetricRegistry;
import com.ftkj.manager.BaseManager;
import com.ftkj.server.MessageStat;
import com.ftkj.server.ServerStat;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceCode.Code;
import com.ftkj.x3.net.X3MessageMetric;
import com.ftkj.xxs.core.util.XxsThreadFactory;
import com.ftkj.xxs.net.stats.AlignFormat;
import com.ftkj.xxs.net.stats.MessageSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消息统计.
 *
 * @author luch
 */
public class MessageStatsManager extends BaseManager {
    private static final Logger log = LoggerFactory.getLogger(MessageStatsManager.class);
    private static final Logger logMsgStats = LoggerFactory.getLogger("msgstats");
    public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();
    private X3MessageMetric messageMetric;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new XxsThreadFactory("log-metric"));

    public void shutdown() {
        log.info("graceful shutdown message stats");
        messageMetric.gracefulShutdownNow(10);
        //        if (GameSource.MESSAGE_STATS) {
        MessageSnapshot ms = messageMetric.getSnapshot();
        String stat = AlignFormat.newDefaultFormat().formatString(ms);
        log.info("msg stats\n{}", stat);
        //        }
    }

    @Override
    public void instanceAfter() {
        log.info("init message stats");
        messageMetric = new X3MessageMetric(METRIC_REGISTRY,
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new XxsThreadFactory("metric"))) {
            @Override
            protected Iterator<ClientCodeStat> initAllCodeStats() {
                Collection<Code> codes = Code.getAllCode();
                log.info("code num {}", codes.size());
                Iterator<Code> it = codes.iterator();
                return new Iterator<ClientCodeStat>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public ClientCodeStat next() {
                        Code code = it.next();
                        String name = ServiceCode.simpleName(code.getCodeName());
                        return new ClientCodeStat(code.getCode(), name);
                    }
                };
            }
        };
        messageMetric.init();

        ServerStat.setMessageStat(new MessageStatDelegate());
        executorService.scheduleAtFixedRate(this::printStat, 1, 60, TimeUnit.MINUTES);
    }

    private void printStat() {
        log.info("log message stats");
        MessageSnapshot ms = messageMetric.getSnapshot();
        String stat = AlignFormat.newDefaultFormat().formatString(ms);
        logMsgStats.info("\n{}", stat);
    }

    public class MessageStatDelegate implements MessageStat {
        @Override
        public void addSendMsgStat(int code, int sendLength) {
            messageMetric.addSendMsgStat(code, sendLength);
        }

        @Override
        public void addReceivedMsgStat(int code, int receivedLength) {
            messageMetric.addReceivedMsgStat(code, receivedLength);
        }

        @Override
        public void addReceivedMsgStat(int code, int receivedLength, int time) {
            messageMetric.addReceivedMsgStat(code, receivedLength, time);
        }
    }

    public X3MessageMetric getMessageMetric() {
        return messageMetric;
    }
}
