package com.ftkj.proxy.rpc;


public class ResponseLock {
	private long sessionId;
	private RPCResponse response;
	public ResponseLock() {	}
		
	public long getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public RPCResponse getResponse() {
		return response;
	}
	public void setResponse(RPCResponse response) {
		this.response = response;
	}
}

