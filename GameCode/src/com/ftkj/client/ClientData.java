package com.ftkj.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClientData {
	
	private static AtomicInteger autoId = new AtomicInteger(1);
	private int rid;
	private int serviceCode; 
	private List<String> values;
	/** 
	 *  是都需要等待回包
	 */
	private boolean callBack;
	
	public ClientData(int serviceCode){
		this(serviceCode,true);
	}
	
	public ClientData(int serviceCode, boolean callBack) {
		this.serviceCode = serviceCode;
		this.values = new ArrayList<String>();
		this.callBack = callBack;
		this.rid = autoId.incrementAndGet();
	}
	
	public ClientData appendValues(String val){
		this.values.add(val);
		return this;
	}
	public ClientData appendValues(long val){
		this.values.add(""+val);
		return this;
	}
	public ClientData appendValues(int val){
		this.values.add(""+val);
		return this;
	}
	public int getRid() {
		return this.rid;
	}
	public boolean isCallBack() {
		return callBack;
	}
	public int getServiceCode() {
		return serviceCode;
	}
	public List<String> getValues() {
		return values;
	}
	
}
