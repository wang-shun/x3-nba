package com.ftkj.client;

import com.ftkj.client.coder.ClientGoogleCodecFactory;
import com.ftkj.tool.log.LoggerManager;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * 客户端
 * @author tim.huang
 * 2015年12月14日
 */
public class GameClient {
	private IoConnector connector;
	private IoSession session;
	public static final String S = "Ω";
	public static final String D = "=";
	
	public void conn(String ip,int port,IoHandlerAdapter handler){
		connector = new NioSocketConnector();
		connector.setHandler(handler);
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ClientGoogleCodecFactory()));
		
		ConnectFuture connFuture = connector.connect(new InetSocketAddress(ip, port));
		connFuture.awaitUninterruptibly();
		session = connFuture.getSession();
		LoggerManager.info("client is conn,ip[{}],port[{}]",ip,port);
	}
	
	public void send(ClientRequest data){
		session.write(data);
	}
	
	public void close(){
		LoggerManager.info("client is dispose,info[{}]",session.getLocalAddress());
		session = null;
		connector.dispose();
	}
	
	public boolean isConn(){
		return session!=null;
	}
	
	
	
	
	
	
	
	
}
