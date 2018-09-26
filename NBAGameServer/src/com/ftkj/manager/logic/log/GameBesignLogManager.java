package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

/**
 * 待签变动
 * @author Jay
 * @time:2018年5月3日 下午4:10:01
 */
public class GameBesignLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GameBesignLogManager");
	
	private static void initContext(){
		ThreadContext.put("besign","1");
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameBesignLogTag", ELogVersion.待签球员.getLogSyslog());
		ThreadContext.put("GameBesignLogVersion", ""+ELogVersion.待签球员.getLogVersion());
	}
	
	public static void Log(long teamId,int id, int playerId, int price, int flag, ModuleLog module){
		if(!ThreadContext.containsKey("besign")){
			initContext();
		}
		log.trace("{} {} {} {} {} {} {} {} ",teamId,id,playerId,price,flag,module.getId(),module.getDetail(),System.currentTimeMillis());
	}
	
	
}
