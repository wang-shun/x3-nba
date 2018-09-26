package com.ftkj.tool.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.server.rpc.route.RouteServerManager;

/**
 * @author tim.huang
 * 2017年11月23日
 * 节点存活定时扫描
 */
public class RouteNodeLiveJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		RouteServerManager.checkRPCServerLive();
	}

}
