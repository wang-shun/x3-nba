package com.ftkj.proxy.rpc;

import com.ftkj.exception.ConnectionException;
import com.ftkj.exception.RPCException;
import com.ftkj.invoker.Server;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;


public class ClientConnector {	
	private IoSession session;
	private NioSocketConnector connector;
	private RPCQueue queue;
	private Server server;
	
	private void init() {
		connector=new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
		ObjectSerializationCodecFactory code = new ObjectSerializationCodecFactory();
		code.setDecoderMaxObjectSize(Integer.MAX_VALUE);
		code.setEncoderMaxObjectSize(Integer.MAX_VALUE);
		IoFilter codecFilter = new ProtocolCodecFilter(code);
		connector.getFilterChain().addLast("codec", codecFilter);
		connector.setHandler(new RPCClientHandler(this,queue));		
	}
	//
	public ClientConnector(Server server,RPCQueue queue){
		this.server = server;
		this.queue=queue;
		init();
	}
		
	public void connect() throws ConnectionException{
		try{
			ConnectFuture future = connector.connect(new InetSocketAddress(server.getIp(), server.getPort()));
			future.awaitUninterruptibly();
			session = future.getSession();
			//System.out.println("-------session--"+session.getRemoteAddress()+" * "+session.getLocalAddress());
		}catch (Throwable e) {
			throw new ConnectionException("can not connect to server."+server);
		}
	}	
	public boolean isConnected(){
		if(session==null){
			return false;
		}
		return session.isConnected();
	}
	public RPCResponse sendRequest(RPCRequest req)throws RPCException, ConnectionException{
		if(!session.isConnected()){
			throw new ConnectionException("session not connected.");
		}
		return queue.sendRequest(req, session);
	}	
	
	public void sendRequestSyn(RPCRequest req)throws RPCException, ConnectionException{
		if(!session.isConnected()){
			throw new ConnectionException("session not connected.");
		}
		queue.sendRequestSyn(req, session);		
	}
	
	public void disConnect(){
		session.closeNow();
	}
	
	protected void sessionClosed(final IoSession session){
		queue.clearQueue(session);
	}
}
