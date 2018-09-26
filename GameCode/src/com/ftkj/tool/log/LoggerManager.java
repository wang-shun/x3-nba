package com.ftkj.tool.log;

import com.ftkj.server.GameSource;
import org.apache.logging.log4j.ThreadContext;



/**
 * @author tim.huang
 * 2017年3月7日
 * 
 */
public class LoggerManager {
	public static void initThreadContext(){
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
	}

}
