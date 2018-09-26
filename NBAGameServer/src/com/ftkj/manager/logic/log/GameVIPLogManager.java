package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

public class GameVIPLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GameVIPLogManager");
	
	private static void initContext(){
		ThreadContext.put("vip", "1");
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameVIPLogTag", ELogVersion.会员.getLogSyslog());
		ThreadContext.put("GameVIPLogVersion", ""+ELogVersion.会员.getLogVersion());
	}
	
	public static void Log(long teamId,String helpStep){
		if(!ThreadContext.containsKey("vip")){
			initContext();
		}
		log.trace("{} {} {} ",teamId,helpStep,System.currentTimeMillis());
	}
	
	
}
