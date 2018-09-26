package com.ftkj.proxy.rpc;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPCClientHandler extends IoHandlerAdapter{
	private RPCQueue queue;
	private ClientConnector connector;
	private static final Logger logger = LoggerFactory.getLogger(RPCClientHandler.class);
	//
	public RPCClientHandler(ClientConnector connector,RPCQueue queue) {
		this.queue=queue;
		this.connector=connector;
	}
	
	//
	@Override
	public void messageReceived(IoSession session, Object message)throws Exception {
		try{
			if(message instanceof RPCResponse){
				RPCResponse rsp=(RPCResponse) message;
				queue.reveiveResponse(rsp,session);			
			}else{
				logger.error("Y$$$"+session.getRemoteAddress()+"/"+message);
			}
		}catch(Throwable e){
			logger.error("RPC收包异常"+session.toString(),e);
		}
	}
	//
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		
	}
	//
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.error("断开RPC连接"+session.getRemoteAddress());
		connector.sessionClosed(session);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)throws Exception {
		logger.error("RPC连接异常"+session.toString(),cause);
	}
}	
