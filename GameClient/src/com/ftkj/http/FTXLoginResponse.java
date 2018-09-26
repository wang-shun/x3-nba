package com.ftkj.http;

public class FTXLoginResponse {
	
	private int code;
	private String message;
	private LoginObj data;
	class LoginObj{
		private String plfUserid;
		private String channelId;
		private String channelName;
		public String getPlfUserid() {
			return plfUserid;
		}
		public void setPlfUserid(String plfUserid) {
			this.plfUserid = plfUserid;
		}
		public String getChannelId() {
			return channelId;
		}
		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}
		public String getChannelName() {
			return channelName;
		}
		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}
		
	}
	
	public static final int OK = 200;
	
	public String getUserId() {
		if(this.data == null) return "";
		return this.data.plfUserid;
	}
	public String getChannelId() {
		if(this.data == null) return "";
		return this.data.channelId;
	}
	public String getChannelName() {
		if(this.data == null) return "";
		return this.data.channelName;
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
	
}
