package com.ftkj.tool.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ftkj.tool.log.LoggerManager;


/**
 * 任务调度服务
 * @author tim.huang
 * 2015年12月14日
 */
public class QuartzServer {

	private SchedulerFactory sf = new StdSchedulerFactory();
	
	public static QuartzServer get(){
		return QuartzServerInstance.instance;
	}

	private static class QuartzServerInstance{
		private static QuartzServer instance = new QuartzServer();
		static {
			//10秒钟同步一次数据库
			JobDetail job = JobBuilder.newJob(PlayerPriceJob.class)
					.withIdentity("PlayerPriceJob", "system")
					.build();
			
			Trigger trigger = TriggerBuilder
			.newTrigger()
			.withIdentity("PlayerPriceJob","system")
			.withSchedule(CronScheduleBuilder.cronSchedule("0 0 16 * * ?"))
			.build();
			
			// 赛程
			JobDetail job2 = JobBuilder.newJob(NBAPKJob.class)
					.withIdentity("NBAPKJob", "system")
					.build();
			
			Trigger trigger2 = TriggerBuilder
			.newTrigger()
			.withIdentity("NBAPKJob","system")
			.withSchedule(CronScheduleBuilder.cronSchedule("0 0/10 9,10,11,12,13,14,15 * * ? "))
			.build();
			
			try {
				instance.addJob(job, trigger);
				instance.addJob(job2, trigger2);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	public void addJob(JobDetail job,Trigger trigger) throws SchedulerException{
		LoggerManager.debug("QuartzServer addJob[{}]",job.getKey().getName());
		Scheduler sd = sf.getScheduler();
		sd.scheduleJob(job, trigger);
	}
	
	public void start() throws SchedulerException{
		LoggerManager.debug("QuartzServer is ready..");
		sf.getScheduler().start();
		LoggerManager.debug("QuartzServer is start..");
	}

}
