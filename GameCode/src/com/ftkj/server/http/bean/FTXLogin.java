package com.ftkj.server.http.bean;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FTXLogin {
    private static final Logger log = LoggerFactory.getLogger(FTXLogin.class);
	private int appId;
	private int packageId;
	private String token;
	private String userId;
	private String exInfo;
	private String sign;

	public FTXLogin(int appId, int packageId, String token, String userId,
			String exInfo,String sign) {
		super();
		this.appId = appId;
		this.packageId = packageId;
		try {
			if(!Strings.isNullOrEmpty(token)){
				token = URLEncoder.encode(token, "utf-8");
			}
			if(!Strings.isNullOrEmpty(userId)){
				userId = URLEncoder.encode(userId, "utf-8");
			}
			if(!Strings.isNullOrEmpty(exInfo)){
				exInfo = URLEncoder.encode(exInfo, "utf-8");
			}
		
		} catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
		}
		this.userId = userId;
		this.exInfo = exInfo;
		this.sign = sign;
		this.token = token;
	}

	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
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
