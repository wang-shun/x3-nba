package com.ftkj.server.rpc.zk;

import com.ftkj.tool.zookeep.ZookeepServer;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tim.huang
 * 2017年4月13日
 * 节点监听
 */
public class NodeWatch implements Watcher {
    private static final Logger log = LoggerFactory.getLogger(NodeWatch.class);
	
	private ZookeepServer zk;
	
	public NodeWatch(ZookeepServer zk){
		this.zk = zk;
	}
	
	@Override
	public void process(WatchedEvent event) {
//		log.err("逻辑节点变动，触发监听[{}]", event.getPath());
		if(event.getType() == EventType.None) return;
		
//		if(event.getType() == EventType.NodeChildrenChanged){//子节点变动
//			
//		}
		
		
		log.error("触发了监听类型为:"+event.getType());
//		
		log.error("逻辑节点变动，触发监听["+event.getPath()+"]");
//		
	}
	
	

}
