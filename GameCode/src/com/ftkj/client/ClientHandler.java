package com.ftkj.client;

import com.ftkj.client.robot.BaseRobot;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends IoHandlerAdapter{
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
	private BaseRobot robot;
	public ClientHandler(BaseRobot robot){
		this.robot = robot;
	}
	
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error("----------------------------------------------------------{}",cause);
	}
	

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if(message instanceof ClientResponse){
			ClientResponse cr = (ClientResponse)message;
//			log.info("接受到服务器返回协议[{}]-耗时[{}]",cr.getKey(),robot.getAction().end());
			ClientStat.make(cr.getKey(), robot.getAction().end());
			if(robot.getAction().getServiceCode()==cr.getKey())
				robot.getAction().callback(cr, robot);
//			DemoPB.DemoDataMain dm = DemoPB.DemoDataMain.parseFrom(cr.getData());
//			log.info("callback message Name:[{}],Num:[{}]",dm.getName(),dm.getNum());
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.error("----------------------------Close------------------------------");
		super.sessionClosed(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}


	public BaseRobot getRobot() {
		return robot;
	}
	
	
}
