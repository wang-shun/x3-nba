package com.ftkj.client.http;

public class FTXLogin {
	private int appId;
	private int packageId;
	private String token;
	private String userId;
	private String exInfo;

	public FTXLogin(int appId, int packageId, String token, String userId,
			String exInfo) {
		super();
		this.appId = appId;
		this.packageId = packageId;
		this.token = token;
		this.userId = userId;
		this.exInfo = exInfo;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getExInfo() {
		return exInfo;
	}

	public void setExInfo(String exInfo) {
		this.exInfo = exInfo;
	}

}
