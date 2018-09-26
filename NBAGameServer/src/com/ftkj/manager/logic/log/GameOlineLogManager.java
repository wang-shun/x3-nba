package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2017年11月27日
 * 在线时长数据
 */
public class GameOlineLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GameOlineLogManager");
	
	private static void initContext(){
		ThreadContext.put("oline","1");
		ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameOlineLogTag", ELogVersion.在线时长.getLogSyslog());
		ThreadContext.put("GameOlineLogVersion", ""+ELogVersion.在线时长.getLogVersion());
	}
	
	public static void Log(long teamId,long second,int level){
		if(!ThreadContext.containsKey("oline")){
			initContext();
		}
		log.trace("{} {} {} {} ",teamId,second,level,System.currentTimeMillis());
	}
	
	
}
