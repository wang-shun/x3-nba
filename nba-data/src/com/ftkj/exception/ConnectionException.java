package com.ftkj.exception;

public class ConnectionException extends Exception{

	private static final long serialVersionUID = 7985127180290954112L;
	public ConnectionException() {
	
	}
	public ConnectionException(String msg){
		super(msg);
	}
}