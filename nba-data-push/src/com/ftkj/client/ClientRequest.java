package com.ftkj.client;

public class ClientRequest {
	private int reqId;
	private int serviceCode;
	private String data;
	
	public ClientRequest(int reqId, int serviceCode, String data) {
		super();
		this.reqId = reqId;
		this.serviceCode = serviceCode;
		this.data = data;
	}
	public int getReqId() {
		return reqId;
	}
	public int getServiceCode() {
		return serviceCode;
	}
	public String getData() {
		return data;
	}
	
	
	
}
