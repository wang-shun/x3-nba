package com.ftkj.proxy.rpc;

import java.io.Serializable;

public class RPCResponse implements Serializable {

	private static final long serialVersionUID = 9082848780680086400L;
	private long id;
	private Object returnObject;
	private Throwable exception;
	public RPCResponse() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	public Object getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	@Override
	public String toString() {
		return "RPCResponse [id=" + id + ", exception=" + exception + "]";
	}

	

	
	
}
