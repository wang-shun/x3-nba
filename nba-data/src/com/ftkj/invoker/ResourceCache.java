package com.ftkj.invoker;

import com.ftkj.proxy.rpc.RPCClient;
import com.ftkj.proxy.rpc.RPCInvocationHandler;
import com.ftkj.proxy.rpc.ServerNode;
import com.ftkj.proxy.rpc.SynRPCInvocationHandler;
import com.ftkj.script.ScriptEngine;
import com.ftkj.script.StartupContextImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ResourceCache {
	private static final Logger logger = LoggerFactory.getLogger(ResourceCache.class);
	private int shardid = 100;
	private String charset="zh";
	
	private static ResourceCache cache=new ResourceCache();
	
	private Map<ResourceType,Object> resource;
	private Map<ServerType,List<ServerNode>> servers;	
	
	public static ResourceCache get(){
		return cache;
	}
	
	private ResourceCache() {		
		resource = new HashMap<ResourceType, Object>();	
		servers  = new HashMap<ServerType, List<ServerNode>>();	
	}
		
	//
	public void addResource(ResourceType resName,Object obj) {
		resource.put(resName, obj);
	}	
	public Object getResource(ResourceType resName){
		Object o = resource.get(resName);
		if(o!=null)
			return o;
		else{
			logger.info("Resource not Find:"+resName.getResName());
			return null;
		}		
	}
	
	//
	public void addServer(Server server) {
		ServerNode node = RPCClient.addNode(server);		
		if(!servers.containsKey(server.getServerName()))
			servers.put(server.getServerName(), new ArrayList<ServerNode>());
		//存在就不放入了
		boolean flag = true;
		for(ServerNode T:servers.get(server.getServerName())){
			if(T.getServer().getId().equals(server.getId())){
				flag = false;
				break;
			}
		}
		if(flag)servers.get(server.getServerName()).add(node);
	}	
	public List<ServerNode> getServer(ServerType resName){
		return servers.get(resName);
	}
	
		
	//
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getCharset() {
		return charset;
	}
	
	//
	public void setShardid(int shardid){
		this.shardid = shardid;
	}	
	public int getShardid(){
		return shardid;
	}	
	
		
	//-------------------------------------------------------------------
	
	
	

	public void init(Object object){
		try {
			Field[] fields = object.getClass().getFields();
			for(Field f:fields){
				Resource res = f.getAnnotation(Resource.class);
				if(res==null)continue;
				Object co = get().getResource(res.value());
				f.set(object, co);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T createRemote(ServerType server,Class<?> actionInterface){
		RPCInvocationHandler hanlder = new RPCInvocationHandler(getServer(server));
		Object proxy = Proxy.newProxyInstance(actionInterface.getClassLoader(), new Class[] { actionInterface }, hanlder);
		return (T)proxy;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T createRemoteSyn(ServerType server,Class<?> actionInterface){
		SynRPCInvocationHandler hanlder = new SynRPCInvocationHandler(getServer(server));
		Object proxy = Proxy.newProxyInstance(actionInterface.getClassLoader(), new Class[] { actionInterface }, hanlder);
		return (T)proxy;
	}	
	
	public Object createProxy(Class<?> actionInterface, InvocationHandler theHandler){
		Object proxy = Proxy.newProxyInstance(actionInterface.getClassLoader(), new Class[] { actionInterface }, theHandler);
		return proxy;
	}	
	
	private void startupContainer(String rcFile)throws Exception{
		StartupContextImpl scc=new StartupContextImpl(cache);
		ScriptEngine se=new ScriptEngine();
		if(!new File(rcFile).exists()){
			logger.error("can not found config file:"+rcFile);
			return;
		}
		BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream(rcFile),"UTF-8"));  		
		//se.execute(new FileReader(rcFile), scc);
		se.execute(in, scc);
	}
	
	public void runInitScript(String rcFile){
		try {
			logger.error("run config file:"+rcFile);
			startupContainer(rcFile);
		} catch (Exception e) {
			logger.error("error when run command.["+rcFile+"]"+e.getMessage());
		}
	}
	
	static ScheduledExecutorService service = null;
	public void initScheduleExecutorService(int size){
		if(service==null){
			service = Executors.newScheduledThreadPool(size, new ThreadFactory() {
				private AtomicLong idFactory = new AtomicLong();
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "Ftjx-Thread-" + idFactory.incrementAndGet());
				}
			});
			logger.error("Start the ExecutorServicePool:"+size);
		}
	}
	
	public ScheduledExecutorService getScheduleExecutorService(){
		if(service==null)initScheduleExecutorService(1);				
		return service;
	}
}
