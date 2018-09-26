package com.ftkj.exception;

import java.io.Serializable;

public class Errors implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2732385080755207455L;
	int code;//错误码
	String info;//错误说明
	String temp;//附加信息
	
	public Errors(int code,String info){
		this.code = code;
		this.info = info;	
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	@Override
	public String toString() {
		return "Errors [code=" + code + ", info=" + info + ", temp=" + temp
				+ "]";
	}
	
	
}