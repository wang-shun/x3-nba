package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2017年7月27日
 * 游戏道具日志
 */
public class GamePropLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GamePropLogManager");
	
	private static void initContext(){
		ThreadContext.put("prop","1");
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GamePropLogTag", ELogVersion.道具统计.getLogSyslog());
		ThreadContext.put("GamePropLogVersion", ELogVersion.道具统计.getLogVersion());
	}
	
	public static void Log(long teamId,int propId,int num,int total,ModuleLog module){
		if(!ThreadContext.containsKey("prop")){
			initContext();
		}
		log.trace("{} {} {} {} {} {} {} ",teamId,propId,num,total,module.getId(),module.getDetail(),System.currentTimeMillis());
	}
	
}
