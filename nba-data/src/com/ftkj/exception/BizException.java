package com.ftkj.exception;

public class BizException extends Exception{
	private static final long serialVersionUID = -7266874590112867154L;
	
	
	protected Errors error;	
	
	public BizException(Errors error) {
		super(error.info);
		this.error=error;
	}	
	public BizException(Errors error,Throwable e){
		super(e);
		this.error=error;
	}	
	
	public BizException(Errors error,String msg){
		super(error.info+"："+msg);
		error.setTemp(msg);
		this.error=error;		
	}	
	public BizException(Errors error,String msg,Throwable e){
		super(error.info+"："+msg, e);
		error.setTemp(msg);
		this.error=error;		
	}	
	
	public Errors getError() {
		return error;
	}
}
