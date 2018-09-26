package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

/**
 * 球员变动
 * @author Jay
 * @time:2018年5月3日 下午4:10:01
 */
public class GamePlayerLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GamePlayerLogManager");
	
	private static void initContext(){
		ThreadContext.put("player","1");
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GamePlayerLogTag", ELogVersion.球队管理.getLogSyslog());
		ThreadContext.put("GamePlayerLogVersion", ""+ELogVersion.球队管理.getLogVersion());
	}
	
	public static void Log(long teamId,int pid, int playerId, int price, int flag, ModuleLog module){
		if(!ThreadContext.containsKey("player")){
			initContext();
		}
		log.trace("{} {} {} {} {} {} {} {} ",teamId,pid,playerId,price,flag,module.getId(),module.getDetail(),System.currentTimeMillis());
	}
	
	
}
