package com.ftkj.tool.quartz;

import com.ftkj.server.GameSource;
import com.ftkj.tool.quartz.annotation.JobExpression;
import com.ftkj.tool.quartz.job.AsynchronousDBJob;
import com.ftkj.tool.quartz.job.RPCLinkedJob;
import com.ftkj.tool.quartz.job.RouteNodeLiveJob;
import com.ftkj.tool.quartz.job.SystemStatJob;
import com.ftkj.util.X3ThreadFactory;
import com.ftkj.util.concurrent.HandlingExceptionScheduledExecutor;
import org.joda.time.DateTime;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度服务
 *
 * @author tim.huang
 * 2015年12月14日
 */
public class QuartzServer {
    private static final Logger log = LoggerFactory.getLogger(QuartzServer.class);
    private SchedulerFactory sf = new StdSchedulerFactory();
    private ScheduledExecutorService scheduledExecutor = new HandlingExceptionScheduledExecutor(
            Runtime.getRuntime().availableProcessors() * 2, new X3ThreadFactory("x3-task"));

    public static QuartzServer get() {
        return QuartzServerInstance.instance;
    }

    public ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }

    /** @see ScheduledExecutorService#schedule(Runnable, long, TimeUnit) */
    public static ScheduledFuture<?> schedule(Runnable command,
                                              long delay, TimeUnit unit) {
        return get().getScheduledExecutor().schedule(() -> {
            try {
                command.run();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, delay, unit);
    }

    /** @see ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit) */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                         long initialDelay,
                                                         long period,
                                                         TimeUnit unit) {
        return get().getScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                command.run();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, initialDelay, period, unit);
    }

    /** @see ScheduledExecutorService#scheduleWithFixedDelay(Runnable, long, long, TimeUnit) */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                            long initialDelay,
                                                            long delay,
                                                            TimeUnit unit) {
        return get().getScheduledExecutor().scheduleWithFixedDelay(() -> {
            try {
                command.run();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, initialDelay, delay, unit);
    }

    /** @see ScheduledExecutorService#submit(Runnable) */
    public static Future<?> submit(Runnable task) {
        return get().getScheduledExecutor().submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    private static class QuartzServerInstance {
        private static QuartzServer instance = new QuartzServer();

        static {
            String _10sec = "0/10 * * * * ?";
            try {
                instance.addJob(JobBuilder.newJob(AsynchronousDBJob.class)
                                .withIdentity("AsynchronousDBJob", JobExpression.SYSTEM)
                                .build(),
                        TriggerBuilder.newTrigger()
                                .withIdentity("AsynchronousDBJob", JobExpression.SYSTEM)
                                .withSchedule(CronScheduleBuilder.cronSchedule(_10sec))
                                .build());

                instance.addJob(JobBuilder.newJob(RPCLinkedJob.class)
                                .withIdentity("RPCLinkedJob", JobExpression.SYSTEM)
                                .build(),
                        TriggerBuilder.newTrigger()
                                .withIdentity("RPCLinkedJob", JobExpression.SYSTEM)
                                .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?"))
                                .build());

                instance.addJob(JobBuilder.newJob(SystemStatJob.class)
                                .withIdentity("SystemStatJob", JobExpression.SYSTEM)
                                .build(),
                        TriggerBuilder.newTrigger()
                                .withIdentity("SystemStatJob", JobExpression.SYSTEM)
                                .withSchedule(CronScheduleBuilder.cronSchedule(_10sec))
                                .build());

                instance.addJob(JobBuilder.newJob(RouteNodeLiveJob.class)
                                .withIdentity("RouteNodeLiveJob", JobExpression.SYSTEM)
                                .build(),
                        TriggerBuilder.newTrigger()
                                .withIdentity("RouteNodeLiveJob", JobExpression.SYSTEM)
                                .withSchedule(CronScheduleBuilder.cronSchedule(_10sec))
                                .build());

                instance.getScheduledExecutor()
                        .scheduleAtFixedRate(GameSource::fullLog, 1, 60, TimeUnit.MINUTES); //1个小时
                instance.getScheduledExecutor()
                        .scheduleAtFixedRate(GameSource::onlineLog, 1, 1, TimeUnit.MINUTES); //1分钟
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
            }

        }

    }

    public void addJob(JobDetail job, Trigger trigger) throws SchedulerException {
        log.debug("QuartzServer addJob[{}]", job.getKey().getName());
        Scheduler sd = sf.getScheduler();
        sd.scheduleJob(job, trigger);
    }

    public void addJob(DateTime runTime, Class<? extends Job> cla, String name) {
        JobDetail jd = JobBuilder.newJob(cla)
                .withIdentity(name, JobExpression.GAME)
                .build();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(name, JobExpression.GAME)
                .startAt(runTime.toDate())
                .build();
        log.debug("QuartzServer addJob[{}]--->required", jd.getKey().getName());
        Scheduler sd;
        try {
            sd = sf.getScheduler();
            sd.scheduleJob(jd, trigger);
        } catch (SchedulerException e) {
            log.error("QuartzServer addJob error--->[{}]", name);
            log.error(e.getMessage(), e);
        }
    }

    public void start() throws SchedulerException {
        log.debug("QuartzServer is ready..");
        sf.getScheduler().start();
        log.debug("QuartzServer is start..");
    }

}
