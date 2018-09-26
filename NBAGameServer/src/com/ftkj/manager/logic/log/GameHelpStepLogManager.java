package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2017年11月27日
 * 新手引导数据
 */
public class GameHelpStepLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GameHelpStepLogManager");
	
	private static void initContext(){
			ThreadContext.put("helpStep","1");
			ThreadContext.put("shardId", ""+GameSource.shardId);
	        ThreadContext.put("ip", GameSource.serverName);
	        ThreadContext.put("platform", ""+GameSource.platform);
			ThreadContext.put("GameHelpStepLogTag", ELogVersion.新手引导.getLogSyslog());
			ThreadContext.put("GameHelpStepLogVersion", ""+ELogVersion.新手引导.getLogVersion());
	}
	
	public static void Log(long teamId,String helpStep){
		if(!ThreadContext.containsKey("helpStep")){
			initContext();
		}
		log.trace("{} {} {} ",teamId,helpStep,System.currentTimeMillis());
	}
	
	
}
