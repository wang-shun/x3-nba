package com.ftkj.server.rpc;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.ftkj.server.proto.Request;
import com.ftkj.server.proto.Response;

public class RPCServerKeepAlive implements KeepAliveMessageFactory {
	static final RPCSource res = new RPCSource("","",null,Request.Socket_Server_KeepAlive_CallBack,0);
	@Override
	public Object getRequest(IoSession arg0) {
		return null;
	}

	@Override
	public Object getResponse(IoSession arg0, Object arg1) {
		return res;
	}

	@Override
	public boolean isRequest(IoSession arg0, Object arg1) {
		return (arg1 instanceof RPCSource && ((RPCSource)arg1).getMethodCode() == Request.Socket_Server_KeepAlive);
	}

	@Override
	public boolean isResponse(IoSession arg0, Object arg1) {
		return false;
	}

}
