package com.ftkj.client.vo;

public class HeroClient {
	private int oId;
	private int hId;
	
	
	
	public HeroClient(int oId, int hId) {
		super();
		this.oId = oId;
		this.hId = hId;
	}
	protected int getoId() {
		return oId;
	}
	protected void setoId(int oId) {
		this.oId = oId;
	}
	protected int gethId() {
		return hId;
	}
	protected void sethId(int hId) {
		this.hId = hId;
	}
	
}
