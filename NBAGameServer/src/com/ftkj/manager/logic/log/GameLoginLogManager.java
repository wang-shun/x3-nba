package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2017年11月27日
 * 游戏登录日志
 */
public class GameLoginLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GameLoginLogManager");
	
	private static void initContext(){
		ThreadContext.put("login", "1");
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameLoginLogTag", ELogVersion.登录.getLogSyslog());
		ThreadContext.put("GameLoginLogVersion", ""+ELogVersion.登录.getLogVersion());
	}
	
	public static void Log(long teamId) {
		if(!ThreadContext.containsKey("login")){
			initContext();
		}
	}
	
	
	
}
