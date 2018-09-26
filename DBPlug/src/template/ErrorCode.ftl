package com.ftkj.enums;

public enum ErrorCode {
	<#list items as x> 
	${x} 
	</#list> 
	;
	public int code;
	private ErrorCode(int code) {
		this.code = code;
	}
	public int getCode() {
		return code;
	}
}
