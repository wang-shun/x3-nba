package com.ftkj.proxy.rpc;

import org.apache.mina.core.session.IoSession;


public class RPCResponseSession {
	private IoSession session;
	private RPCResponse response;
	public RPCResponseSession(RPCResponse response,IoSession session) {
		this.response = response;
		this.session = session;
	}
	public IoSession getSession() {
		return session;
	}
	public void setSession(IoSession session) {
		this.session = session;
	}
	public RPCResponse getResponse() {
		return response;
	}
	public void setResponse(RPCResponse response) {
		this.response = response;
	}
	
}
