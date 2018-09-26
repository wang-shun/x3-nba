package com.ftkj.proxy.rpc;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class Stats {
	AtomicLong moId = new AtomicLong();
	AtomicLong mtId = new AtomicLong();
	
	LinkedBlockingQueue<RPCRequestSession> requestQueue;
	LinkedBlockingQueue<RPCResponseSession> responseQueue;
	Map<Long, ResponseLock> syncLockMap;
		
	public void setQueue(LinkedBlockingQueue<RPCRequestSession> requestQueue,
			LinkedBlockingQueue<RPCResponseSession> responseQueue,
			Map<Long, ResponseLock> syncLockMap){
		this.requestQueue = requestQueue;
		this.responseQueue = responseQueue;
		this.syncLockMap = syncLockMap;
	}
	
	public void mo(){
		moId.incrementAndGet();
	}
	public void mt(){
		mtId.incrementAndGet();
	}

	@Override
	public String toString() {
		return "DaoStat[mo="+moId.get()+",mt="+mtId.get()+",wait="+syncLockMap.size()+",requestQueue="+requestQueue.size()+",responseQueue="+responseQueue.size()+"]";
	}
	
	
	
	
	
}
