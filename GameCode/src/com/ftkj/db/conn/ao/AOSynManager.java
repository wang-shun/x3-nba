package com.ftkj.db.conn.ao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.server.socket.LogicMethod;

/**
 * AO层异步管理
 * @author tim.huang
 * 2015年12月7日
 */
public class AOSynManager {
    private static final Logger log = LoggerFactory.getLogger(AOSynManager.class);
//	private static BlockingQueue<LogicMethod>  aoMap  = new LinkedBlockingQueue<LogicMethod>(5000*50);//AO层异步处理队列
	private static AtomicLong aoSize = new AtomicLong(0);
	/** 类似一个线程池*/
	private static ExecutorService es;
	
	public static void start(){
//		int size = Runtime.getRuntime().availableProcessors()/2-1;
		int size =1;
		es = Executors.newFixedThreadPool(size>0?size:1);
//		new Thread(new AOSynThread(), "AOSynManager").start();
	}	
	
	public static void put(LogicMethod method){
		try {
			aoSize.incrementAndGet();
			es.execute(method);
//			aoMap.put(method);
		} catch (Exception e) {
			log.error("AOSynManager error:{}",e);
		}
	}
	
	public static int getAOSize(){
		return ((ThreadPoolExecutor)es).getPoolSize();
	}
	
	public static long getAOTotalSize(){
		return aoSize.get();
	}
	
//	static class AOSynThread implements Runnable{
//
//		@Override
//		public void run() {
//			LogicMethod method = null;
//			while(true){
//				try {
//					method = aoMap.take();
//					method.invoke();
//				} catch (Exception e) {
//					log.error("AOSynManager error:{}",e);
//				}
//			}
//		}
//		
//	}
	
	
}
