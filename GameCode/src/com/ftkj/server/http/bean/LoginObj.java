package com.ftkj.server.http.bean;

public class LoginObj {
	private long userId;
	private String channelId;
	private String channelName;
	public String getChannelId() {
		return channelId;
	}
	public String getChannelName() {
		return channelName;
	}
	public long getPlfUserid() {
		return userId;
	}
	public void setPlfUserid(long plfUserid) {
		this.userId = plfUserid;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	@Override
	public String toString() {
		return "LoginObj [plfUserid=" + userId + ", channelId=" + channelId
				+ ", channelName=" + channelName + "]";
	}
}
