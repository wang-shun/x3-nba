package com.ftkj.proxy.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RPCInvocationHandler implements InvocationHandler{
    private static final Logger logger = LoggerFactory.getLogger(RPCInvocationHandler.class);
	static AtomicInteger invokeCount=new AtomicInteger();
	
	List<ServerNode> nodeList;
	Object obj;
	
	public RPCInvocationHandler(List<ServerNode> nodeList){
		this.nodeList = nodeList;		
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		ServerNode node = getNode();
		//logger.info(node.getServer());
		
		if(!node.getClient().isConnected()){
			node.getClient().connect();
		}
		Object o = node.getClient().invoke(method.getDeclaringClass(), method, args);
		return o;
	}
	
	private ServerNode getNode(){
		int nodeSize = nodeList.size();

		if(nodeSize==1)return nodeList.get(0);

		int index = invokeCount.getAndIncrement();	    
		if (nodeSize == 0)
			return null;

		index %= nodeSize;	    
		ServerNode node = nodeList.get(index);
		if (node.isAvailable())
			return node;

		for (int i = 0; i < nodeSize; ++i) {
			node = nodeList.get(i);
			if (node.isAvailable())
				return node;
		}
		return nodeList.get(0);
	}
	
}
