package com.ftkj.script;

import com.ftkj.invoker.ResourceType;
import com.ftkj.invoker.Server;


public interface StartupContext {
	
	void addServer(Server server);
	
	void addResource(ResourceType resName,Object obj);
	
	void setShardid(int shardid);
	
	void setCharset(String charset);
}
