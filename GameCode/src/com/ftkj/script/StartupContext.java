package com.ftkj.script;

import com.ftkj.db.conn.dao.ResourceType;
import com.ftkj.server.GameSource;

public interface StartupContext {
	
	void addResource(ResourceType resName,Object obj);
	
	void setShardid(int shardid);
	
	void setCharset(String charset);
	
	public void setZKPath(String path);
	
	public void setServerName(String serverName);
	public void setPool(String pool);
	public void setServerDate(String date);
	
	
}
