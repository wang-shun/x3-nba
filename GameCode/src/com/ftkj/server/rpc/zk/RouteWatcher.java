package com.ftkj.server.rpc.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tim.huang
 * 2017年3月29日
 * 观察路由节点
 */
public class RouteWatcher implements Watcher {
    private static final Logger log = LoggerFactory.getLogger(RouteWatcher.class);
	
	
	
	
	@Override
	public void process(WatchedEvent event) {
//		log.err("路由节点变动，触发监听[{}]", event.getPath());
		
		log.error("路由节点变动，触发监听["+event.getPath()+"]");
	}

}
