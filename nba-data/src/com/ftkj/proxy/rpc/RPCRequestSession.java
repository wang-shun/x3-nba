package com.ftkj.proxy.rpc;

import org.apache.mina.core.session.IoSession;

public class RPCRequestSession {
	private IoSession ioSession;
	private RPCRequest request;
	private long createTime;
	private long dispatchTime;
	private long endTime;
	public RPCRequestSession() {}
	public RPCRequestSession(IoSession session,RPCRequest req) {
		this.ioSession=session;
		this.request=req;
	}
	public IoSession getSession() {
		return ioSession;
	}
	public void setSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}
	public RPCRequest getRequest() {
		return request;
	}
	public void setRequest(RPCRequest request) {
		this.request = request;
	}	
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getDispatchTime() {
		return dispatchTime;
	}
	public void setDispatchTime(long dispatchTime) {
		this.dispatchTime = dispatchTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}	
	@Override
	public String toString() {
		return "RPCRequestSession [request=" + request + ", createTime="
				+ createTime + ", dispatchTime=" + dispatchTime + ", endTime="
				+ endTime + "]";
	}
	
}
