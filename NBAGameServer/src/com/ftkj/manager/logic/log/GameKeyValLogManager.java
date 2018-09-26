package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2017年7月27日
 * 游戏键值数据日志
 */
public class GameKeyValLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GameKeyValLogManager");
	
	public final static String 在线 = "Online";
	public final static String 聊天 = "Chat";
	
	private static void initContext(){
		ThreadContext.put("keyVal","1");
		ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameKeyValLogTag", ELogVersion.键值对.getLogSyslog());
		ThreadContext.put("GameKeyValLogVersion", ELogVersion.键值对.getLogVersion());
	}
	
	public static void Log(String key,String val){
		if(!ThreadContext.containsKey("keyVal")){
			initContext();
		}
		log.trace("{} {} {} ",key,val,System.currentTimeMillis());
	}
	
	public static void Log(String key,int val){
		if(!ThreadContext.containsKey("money")){
			initContext();
		}
		log.trace("{} {} {} ",key,val,System.currentTimeMillis());
	}
}
