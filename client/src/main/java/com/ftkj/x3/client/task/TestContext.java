package com.ftkj.x3.client.task;

import com.ftkj.xxs.core.util.DateTimeUtil;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author luch
 */
public class TestContext {
    public static final int ROBOT_SLEEPMILLISMIN = 300;
    public static final int ROBOT_SLEEPMILLISMAX = 2000;

    public static TestParams robotParam(ThreadLocalRandom tlr) {
        return new TestParams(tlr, ROBOT_SLEEPMILLISMIN, ROBOT_SLEEPMILLISMAX);
    }

    public static TestParams robotParam() {
        return new TestParams(ROBOT_SLEEPMILLISMIN, ROBOT_SLEEPMILLISMAX);
    }

    public static TestParams moduleParam(ThreadLocalRandom tlr) {
        return new TestParams(tlr, 0, 0);
    }

    public static TestParams moduleParam() {
        return new TestParams();
    }

    public static final class TestParams {
        private final ThreadLocalRandom tlr;
        private final int sleepMillisMin;
        private final int sleepMillisMax;
        private final boolean noSleep;

        //stats
        private long totalSleepTime;
        private long starTime;
        private long endTime;

        public TestParams() {
            this(ThreadLocalRandom.current(), 0, 0);
        }

        public TestParams(int sleepMillisMin, int sleepMillisMax) {
            this(ThreadLocalRandom.current(), sleepMillisMin, sleepMillisMax);
        }

        public TestParams(ThreadLocalRandom tlr, int sleepMillisMin, int sleepMillisMax) {
            this.tlr = tlr;
            this.sleepMillisMin = sleepMillisMin;
            this.sleepMillisMax = sleepMillisMax;
            this.noSleep = sleepMillisMin == 0 && sleepMillisMax == 0;
        }

        public void star() {
            starTime = System.currentTimeMillis();
        }

        public void end() {
            endTime = System.currentTimeMillis();
        }

        public ThreadLocalRandom getTlr() {
            return tlr;
        }

        public int getSleepMillisMin() {
            return sleepMillisMin;
        }

        public int getSleepMillisMax() {
            return sleepMillisMax;
        }

        public int sleep() {
            if (noSleep) {
                return 0;
            }
            try {
                int millis = tlr.nextInt(sleepMillisMin, sleepMillisMax);
                totalSleepTime += millis;
                Thread.sleep(millis);
                return millis;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public String stats() {
            return "{" +
                    "\"totalSleep\":" + Duration.ofMillis(totalSleepTime) +
                    ", \"total\":" + Duration.ofMillis(endTime - starTime) +
                    ", \"star\":" + DateTimeUtil.millsToStr(starTime) +
                    ", \"end\":" + DateTimeUtil.millsToStr(endTime) +
                    '}';
        }
    }

    protected int sleep(TestParams params) {
        return params.sleep();
    }

}
