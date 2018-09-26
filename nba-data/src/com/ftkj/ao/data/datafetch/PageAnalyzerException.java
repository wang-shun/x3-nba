package com.ftkj.ao.data.datafetch;

//
public class PageAnalyzerException extends Exception{
	
	private static final long serialVersionUID = -2947814020659453385L;
	//
	public PageAnalyzerException(String msg){
		super(msg);
	}
	//
	public PageAnalyzerException(String msg,Throwable e) {
		super(msg, e);
	}
}
