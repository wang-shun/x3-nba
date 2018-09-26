package com.ftkj.proxy.rpc;

import com.ftkj.exception.RPCException;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;


public class RPCQueue {
    private static final Logger logger = LoggerFactory.getLogger(RPCQueue.class);
	private AtomicLong requestId;
	private static final int REQUEST_QUEUE_SIZE = 5000*100;
	private static final long DEFAULT_TIMEOUT = 1000*30;
	private long timeout;
	private Map<Long, ResponseLock> syncLockMap;
	private LinkedBlockingQueue<RPCRequestSession> requestQueue;
	private LinkedBlockingQueue<RPCResponseSession> responseQueue;	
	private Stats stat = new Stats();
	//
	public RPCQueue() {
		
		requestId=new AtomicLong();
		syncLockMap = new ConcurrentHashMap<Long, ResponseLock>();
		timeout = DEFAULT_TIMEOUT;
		requestQueue = new LinkedBlockingQueue<RPCRequestSession>(REQUEST_QUEUE_SIZE);
		responseQueue = new LinkedBlockingQueue<RPCResponseSession>();

		ReceiveThread receiveThread = new ReceiveThread(responseQueue, syncLockMap);
		receiveThread.start();

		logger.error("ReveiveThread start");

		SendThread sendThread = new SendThread(requestQueue);
		sendThread.start();

		logger.error("SendThread start");
				
		//ResourceCache.get().getScheduleExecutorService().scheduleAtFixedRate(new Scheduled(this), 10,10, TimeUnit.SECONDS);
	}
	
	public void setStat(Stats stat) {
		stat.setQueue(requestQueue, responseQueue, syncLockMap);
		this.stat = stat;
	}
	
	
	
	public long getTimeout() {
		return timeout;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	//
	public void reveiveResponse(RPCResponse response,IoSession session){
		try {
			stat.mt();
			responseQueue.put(new RPCResponseSession(response,session));
		} catch (InterruptedException e) {
			logger.error("reveiveResponse:", e);
		}
	}
	
	public void sendRequestSyn(RPCRequest req,IoSession session) throws RPCException{		
		req.setId(requestId.incrementAndGet());
		req.setAsync(true);
		req.setStartTime(System.currentTimeMillis());
		try {
			requestQueue.put(new RPCRequestSession(session,req));
		} catch (InterruptedException e) {
			logger();
			throw new RPCException("Request Queue full." +req.getId()+" send timeout");
		}
	}

	//
	public RPCResponse sendRequest(RPCRequest req,IoSession ioSession) throws RPCException{
		req.setId(requestId.incrementAndGet());
		req.setAsync(false);
		req.setStartTime(System.currentTimeMillis());		
		ResponseLock lock=new ResponseLock();
		lock.setSessionId(ioSession.getId());
		syncLockMap.put(req.getId(),lock);
		stat.mo();
		try {
			requestQueue.put(new RPCRequestSession(ioSession,req));
		} catch (InterruptedException e1) {
			syncLockMap.remove(req.getId());
			logger();
			throw new RPCException("Request Queue full." +req.getId()+" send timeout");
		} 
		synchronized (lock) {
			try {
				/*
				if(req.getMethodName().equals("createTeam")){
					while(lock.getResponse()==null){
						lock.wait(timeout*10);
						long endTime=System.currentTimeMillis();
						if(endTime-req.getStartTime()>timeout*10 &&lock.getResponse()==null){
							throw new RPCException(">>1----------"+req);
						}
					}
				}else{*/
					while(lock.getResponse()==null){
						lock.wait(timeout);
						long endTime=System.currentTimeMillis();
						if(endTime-req.getStartTime()>timeout &&lock.getResponse()==null){
							logger();
							throw new RPCException(">>1----------"+req);
						}
					}
				//}
			} catch (InterruptedException e) {
				logger();
				throw new RPCException(">>2----------"+req);
			} finally{
				syncLockMap.remove(req.getId());
				//logger.error("=="+req);
			}
			return lock.getResponse();
		}
	}

	//-------------------------------------------------------------------------
	//
	static class SendThread extends Thread{
		private LinkedBlockingQueue<RPCRequestSession>requestQueue;
		private boolean isStop;
		public SendThread(LinkedBlockingQueue<RPCRequestSession>requestQueue) {
			this.requestQueue=requestQueue;
			setName("RPCClientSendThread");
			isStop=false;
		}
		@Override
		public void run() {
			while(!isStop){
				try {
					RPCRequestSession req=requestQueue.take();
					req.getSession().write(req.getRequest());
				} catch (Exception e) {
					logger.error("SendThread:",e);
				}
			}
		}
		//
		public void stopThread(){
			isStop=true;
		}
	}
	//
	static class ReceiveThread extends Thread{
		private LinkedBlockingQueue<RPCResponseSession>responseQueue;
		private Map<Long,ResponseLock>syncLockMap;
		private boolean isStop;
		public ReceiveThread(LinkedBlockingQueue<RPCResponseSession>responseQueue,Map<Long,ResponseLock>syncLockMap) {
			this.responseQueue=responseQueue;
			this.syncLockMap=syncLockMap;
			setName("RPCClientReveiveThread");
			isStop=false;
		}
		@Override
		public void run() {
			while(!isStop){
				try {
					RPCResponseSession rsp=responseQueue.take();					
					ResponseLock lock=syncLockMap.get(rsp.getResponse().getId());
					if(lock==null){
						logger.error("bad response:"+rsp.getResponse());
					}else{
						//logger.info("<<----------"+rsp.getResponse());
						synchronized (lock) {
							lock.setResponse(rsp.getResponse());
							lock.notifyAll();
						}
					}

				} catch (Exception e) {
					logger.error("ReceiveThread:", e);					
				}
			}
		}
		public void stopThread(){
			isStop=true;
		}
	}
	//
	public void clearQueue(IoSession session){		
		logger.error("response queue of session:"+session.getId()+" clear.");
		int count=0;
		for(ResponseLock lock:syncLockMap.values()){
			if(lock.getSessionId()==session.getId()){
				synchronized (lock) {
					lock.setResponse(null);
					lock.notifyAll();
				}
				count++;
			}
		}
		logger.error("notify all waiting thread for session:"+session.getId()+" total:"+count);
	}
	
	private void logger(){
		logger.error("-------RpcQueuesx-------requ:"+requestQueue.size()+",resp:"+responseQueue.size());
	}
	/*
	private static class Scheduled implements Runnable{
		RPCQueue rpc;
		public Scheduled(RPCQueue rpc){
			this.rpc = rpc;
		}
		@Override
		public void run() {
			rpc.logger();
		}
	}
	*/
}
