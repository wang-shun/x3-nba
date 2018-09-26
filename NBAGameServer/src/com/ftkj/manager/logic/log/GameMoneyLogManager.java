package com.ftkj.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.ftkj.enums.EMoneyType;
import com.ftkj.enums.log.ELogVersion;
import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2017年7月27日
 * 游戏货币日志
 */
public class GameMoneyLogManager {
//	{"version": "2.4", "fields": ["log_level","shard_id","team_id","money_type","val","module"]}
	private static final Logger log = LogManager.getLogger("GameMoneyLogManager");
	
	private static void initContext(){
		ThreadContext.put("money","1");
		ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameMoneyLogTag", ELogVersion.货币统计.getLogSyslog());
		ThreadContext.put("GameMoneyLogVersion", ELogVersion.货币统计.getLogVersion());
	}
	
	public static void Log(long teamId,EMoneyType type,int val,int cur,ModuleLog module){
		if(!ThreadContext.containsKey("money")){
			initContext();
		}
		log.trace("{} {} {} {} {} {} {} {},{} ",teamId,type.ordinal(),val,cur,module.getId(),module.getDetail(),System.currentTimeMillis()
				,ThreadContext.get("GameMoneyLogTag"),ThreadContext.get("GameMoneyLogVersion"));
		
	}
	
}
