package com.ftkj.script;

import com.ftkj.invoker.ResourceCache;
import com.ftkj.invoker.ResourceType;
import com.ftkj.invoker.Server;
import com.ftkj.util.charset.Keys;


public class StartupContextImpl implements StartupContext{
	
	private ResourceCache cache;	
	public StartupContextImpl(ResourceCache cache) {
		this.cache=cache;
	}
	
	public void addServer(Server server) {
		cache.addServer(server);
	}
	public void addResource(ResourceType resName,Object obj){
		cache.addResource(resName, obj);
	}
	public void setShardid(int shardid){
		cache.setShardid(shardid);
	}
	public void setCharset(String charset) {
		cache.setCharset(charset);
		Keys.setCharset(charset);		
	}
	
}
