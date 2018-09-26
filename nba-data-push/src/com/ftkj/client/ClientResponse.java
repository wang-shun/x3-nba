package com.ftkj.client;

public class ClientResponse {
	private int key;
	private int rid;
	private byte[] data;
	
	public ClientResponse(int key, byte[] data,int rid) {
		super();
		this.key = key;
		this.data = data;
		this.rid = rid;
	}
	public int getKey() {
		return key;
	}
	public byte[] getData() {
		return data;
	}
	public int getRid() {
		return rid;
	}
	
}
