package com.ftkj.x3.client;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class MetricsT {
    private static final Logger log = LoggerFactory.getLogger(MetricsT.class);
    static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) {
        new MetricsT().run();
    }

    private void run() {
        log.info("start Metrics");
        new GetStarted().run(metrics);
    }

    public static final class GetStarted {
        Slf4jReporter reporter;

        void run(MetricRegistry metrics) {
            startReport();
            Meter requests = metrics.meter("requests");
            requests.mark();

            wait5Seconds();
            reporter.report();
        }

        void startReport() {
            //            ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
            Slf4jReporter.Builder builder = Slf4jReporter.forRegistry(metrics)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS);

            builder.outputTo(log);

            reporter = builder.build();
            reporter.start(1, TimeUnit.SECONDS);
        }

        void wait5Seconds() {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static final class MetricCommon {

        public static MetricRegistry getMetricAndStartAllReport(String s0, String s1) {
            //            ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
            ConsoleReporter.Builder builder = ConsoleReporter.forRegistry(metrics)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS);

            //            builder.outputTo(log);

            ConsoleReporter reporter = builder.build();
            reporter.start(1, TimeUnit.SECONDS);
            return metrics;
        }
    }

    public static class MetersTest {
        MetricRegistry registry = MetricCommon.getMetricAndStartAllReport("nc110x.corp.youdao.com", "test.metrics");
        public static Random random = new Random();

        public static void main(String[] args) throws Exception {
            new MetersTest().testOne();
        }

        public void testOne() throws InterruptedException {
            Meter meterTps = registry.meter(MetricRegistry.name(MetersTest.class, "request", "tps"));
            while (true) {
                meterTps.mark();
                int rnd = random.nextInt(1000);
                System.out.println("sleep " + rnd);
                Thread.sleep(rnd);
            }
        }

        public void testMulti() throws InterruptedException {
            while (true) {
                int i = random.nextInt(100);
                int j = i % 10;
                Meter meterTps = registry.meter(MetricRegistry.name(MetersTest.class, "request", "tps", String.valueOf(j)));
                meterTps.mark();
                Thread.sleep(10);
            }
        }
    }

    public static class TimerTest {
        public static Random random = new Random();
        private static final MetricRegistry registry = MetricCommon.getMetricAndStartAllReport("nc110x.corp.youdao.com", "test.metrics");
        private static final Map<Integer, Timer> timerMap = new ConcurrentHashMap<>();

        public static void main(String[] args) throws Exception {
            new TimerTest().testOneTimer();
        }

        public void testOneTimer() throws InterruptedException {
            Timer timer = registry.timer(MetricRegistry.name(TimerTest.class, "get-latency"));
            Timer.Context ctx;
            while (true) {
                ctx = timer.time();
                Thread.sleep(random.nextInt(100));
                ctx.stop();
            }
        }

        public void testMultiTimer() throws InterruptedException {
            while (true) {
                int i = random.nextInt(100);
                int j = i % 10;
                Timer timer = registry.timer(MetricRegistry.name(TimerTest.class, "get-latency", String.valueOf(j)));
                Timer.Context ctx;
                ctx = timer.time();
                Thread.sleep(random.nextInt(1000));
                ctx.stop();
                Thread.sleep(1000);
            }
        }
    }
}
