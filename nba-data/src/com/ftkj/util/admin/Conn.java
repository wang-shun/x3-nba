package com.ftkj.util.admin;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class Conn {

	public static IoSession getSession(String ip,int port){
		NioSocketConnector connector = new NioSocketConnector(1);
		connector.getSessionConfig().setTcpNoDelay(true);
		connector.getFilterChain().addLast("codec",new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
		connector.setHandler(new ClientHandler());
		ConnectFuture cf = connector.connect(new InetSocketAddress(ip, port));
		cf.awaitUninterruptibly();
		return cf.getSession();
	}

	static class ClientHandler extends IoHandlerAdapter{

		public ClientHandler() {

		}

		@Override
		public void messageReceived(IoSession session, Object message)throws Exception {			
			if(message.toString().indexOf("</cross-domain-policy>")==-1)System.out.println(session.getRemoteAddress()+"-->"+message.toString());
		}
		@Override
		public void exceptionCaught(IoSession session, Throwable cause)throws Exception {}
		@Override
		public void messageSent(IoSession session, Object message) throws Exception {}
		@Override
		public void sessionClosed(IoSession session) throws Exception {}
		@Override
		public void sessionCreated(IoSession session) throws Exception {}
		@Override
		public void sessionIdle(IoSession session, IdleStatus status)throws Exception {}
		@Override
		public void sessionOpened(IoSession session) throws Exception {}
	}
}
