/**
 * 
 */
package com.ftkj;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
/**
 * @author Marc.Wang 2011-9-22 下午06:21:55
 * 功能：
 */
public class ClientHandlerTestCase extends IoHandlerAdapter{

	public ClientHandlerTestCase() {
		
	}
	//
	//
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		
		System.out.println("client receive:"+message.toString());
		
		System.out.println("b:"+System.currentTimeMillis());
	}
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
	}
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	
	}
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		session.removeAttribute("session");
	}
	@Override
	public void sessionCreated(IoSession session) throws Exception {
	
	}
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		
	}
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		
	}
}
