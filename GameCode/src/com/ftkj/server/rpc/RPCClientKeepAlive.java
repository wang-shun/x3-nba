package com.ftkj.server.rpc;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.ftkj.server.proto.Request;
import com.ftkj.server.proto.Response;

public class RPCClientKeepAlive implements KeepAliveMessageFactory {
	static final RPCSource req = new RPCSource("","",null,Request.Socket_Server_KeepAlive,0);
	@Override
	public Object getRequest(IoSession arg0) {
		return req;
	}

	@Override
	public Object getResponse(IoSession arg0, Object arg1) {
		return null;
	}

	@Override
	public boolean isRequest(IoSession arg0, Object arg1) {
		return false;
	}
	
	@Override
	public boolean isResponse(IoSession arg0, Object arg1) {
		return (arg1 instanceof RPCSource && ((RPCSource)arg1).getMethodCode() == Request.Socket_Server_KeepAlive_CallBack);
	}

}
