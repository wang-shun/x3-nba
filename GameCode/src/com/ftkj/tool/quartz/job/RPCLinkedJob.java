package com.ftkj.tool.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ftkj.manager.RPCManager;

/**
 * @author tim.huang
 * RPC异步调用链，超时处理
 */
public class RPCLinkedJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		RPCManager.clearTimeOut();
	}

}
