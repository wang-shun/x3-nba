package com.ftkj.proxy.rpc;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ftkj.exception.ConnectionException;
import com.ftkj.invoker.Server;


public class RPCClient {
	private static Map<String, ServerNode> clientCache=(new ConcurrentHashMap<String, ServerNode>());
	
	private static RPCQueue queue=new RPCQueue();
	private ClientConnector clientConnector;
	
	public static ServerNode addNode(Server server){
		String key = server.getId();
		if(clientCache.containsKey(key)){
			return clientCache.get(key);
		}
		ServerNode node = new ServerNode(server,new RPCClient(server));
		clientCache.put(key,node);
		return node;
	}
	
	public static void setStats(Stats stat){
		queue.setStat(stat);
	}
	
	private RPCClient(Server server) {
		clientConnector=new ClientConnector(server,queue);		
	}
	
	public synchronized void connect() throws ConnectionException{
		clientConnector.connect();	
	}
	
	public synchronized void disConnnect(){
		clientConnector.disConnect();
	}
	
	public synchronized boolean isConnected() {
		return clientConnector.isConnected();
	}
	
	public Object invoke(Class<?>clazz,Method method,Object[]parameters)throws Throwable{
		RPCResponse rsp=clientConnector.sendRequest(getRequest(clazz, method, parameters));
		if(rsp.getException()!=null){
			throw rsp.getException();
		}
		return rsp.getReturnObject();
	}
	
	public void invokeSyn(Class<?>clazz,Method method,Object[]parameters)throws Throwable{
		clientConnector.sendRequestSyn(getRequest(clazz, method, parameters));		
	}

	private RPCRequest getRequest(Class<?>clazz,Method method,Object[] parameters){
		RPCRequest req=new RPCRequest();
		req.setTargetClass(clazz.getName());
		req.setMethodName(method.getName());
		Class<?>pp[]=method.getParameterTypes();
		String str[]=new String[pp.length];
		for(int i=0;i<str.length;i++){
			str[i]=pp[i].getName();
		}
		req.setParameterTypes(str);
		req.setParameters(parameters);
		return req;
	}


}
