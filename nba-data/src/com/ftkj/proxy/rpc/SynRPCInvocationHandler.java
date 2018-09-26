package com.ftkj.proxy.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SynRPCInvocationHandler implements InvocationHandler{
    private static final Logger logger = LoggerFactory.getLogger(SynRPCInvocationHandler.class);
	static AtomicInteger invokeCount=new AtomicInteger();
	
	List<ServerNode> nodeList;
	Object obj;
	
	public SynRPCInvocationHandler(List<ServerNode> nodeList){
		this.nodeList = nodeList;		
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		ServerNode node = getNode();
		//logger.info(node.getServer());
		
		if(!node.getClient().isConnected()){
			node.getClient().connect();
		}
		node.getClient().invokeSyn(method.getDeclaringClass(), method, args);
		return null;
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
