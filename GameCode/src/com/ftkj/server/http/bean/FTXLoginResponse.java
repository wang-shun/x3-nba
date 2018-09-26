package com.ftkj.server.http.bean;

public class FTXLoginResponse {
	
	private int code;
	private String message;
	private LoginObj data;
	
	public static final int OK = 200;
	
	public long getUserId() {
		if(this.data == null) return 0;
		return this.data.getPlfUserid();
	}
	public String getChannelId() {
		if(this.data == null) return "";
		return this.data.getChannelId();
	}
	public String getChannelName() {
		if(this.data == null) return "";
		return this.data.getChannelName();
	}
	
	public void setData(LoginObj data) {
		this.data = data;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "FTXLoginResponse [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}
	
	
}
