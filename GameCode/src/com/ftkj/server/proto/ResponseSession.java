package com.ftkj.server.proto;

import org.apache.mina.core.session.IoSession;

import com.ftkj.manager.User;

public class ResponseSession {
	private IoSession session;
	private Response response;
	
	public ResponseSession(IoSession session,Response res){
		this.session = session;
		this.response = res;
	}
	
	public ResponseSession(User user,Response response){
		this.session = user.getSession();
		this.response = response;
	}
	
	public IoSession getSession() {
		return session;
	}
	public void setSession(IoSession session) {
		this.session = session;
	}
	public Response getResponse() {
		return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "ResponseSession [session=" + session + ", response=" + response
				+ "]";
	}
	
	
}
