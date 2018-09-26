package com.ftkj.proxy.rpc;

import com.ftkj.invoker.ResourceCache;
import com.ftkj.invoker.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ServerNode {
    private static final Logger logger = LoggerFactory.getLogger(ServerNode.class);
	private boolean isAvailable;
	private RPCClient client;
	private Server server;

	public ServerNode(Server server,RPCClient client){
		this.server = server;
		this.client = client;
		this.isAvailable = true;

		CheckAvailableScheduled schedule = new CheckAvailableScheduled(this);
		ResourceCache.get().getScheduleExecutorService().scheduleAtFixedRate(schedule, 30,30, TimeUnit.SECONDS);
	}

	private void checkAvailable() {
		//logger.info("check"+server);
		try {
			if (this.client.isConnected()) {
				this.isAvailable = true;
			} else {
				this.client.connect();
				this.isAvailable = true;
			}
		} catch (Throwable e) {			
			try {
				this.client.connect();
				this.isAvailable = true;
			} catch (Exception e1) {
				this.isAvailable = false;
				logger.error("node:" + server + " 连接失败");
			}
		}
	}

	public boolean isAvailable() {
		return isAvailable;
	}
	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public RPCClient getClient() {
		return client;
	}
	public void setClient(RPCClient client) {
		this.client = client;
	}
	public Server getServer() {
		return server;
	}
	public void setServer(Server server) {
		this.server = server;
	}

	static class CheckAvailableScheduled implements Runnable{
		ServerNode node;
		public CheckAvailableScheduled(ServerNode node){
			this.node = node;
		}
		@Override
		public void run() {
			node.checkAvailable();			
		}

	}
}
