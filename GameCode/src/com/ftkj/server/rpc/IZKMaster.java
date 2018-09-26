package com.ftkj.server.rpc;


/**
 * @author tim.huang
 * 2017年4月24日
 * 节点注册为主节点后，会调用该方法。
 * manager实现该接口，表示当节点注册为主节点后，会调用该方法初始化。
 * 
 */
public interface IZKMaster {
	
	public void masterInit(String nodeName);
	
}
