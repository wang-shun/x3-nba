package com.ftkj.exception;

public class RPCException extends Exception{

	private static final long serialVersionUID = 7985127180290954112L;
	public RPCException() {
	}
	public RPCException(String msg){
		super(msg);
	}
}
