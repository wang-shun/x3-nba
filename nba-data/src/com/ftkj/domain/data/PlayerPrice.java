package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Date;
public class PlayerPrice implements Serializable{
	private static final long serialVersionUID = -7785894570833379606L;
	private int playerId;
	private Date time;
	private int price;
	private int bizNum;
	
	public PlayerPrice(){}
	
	public PlayerPrice(int playerId,int price,Date time){
		this.playerId = playerId;
		this.price = price;
		this.time = time;
	}
	
	public int getBizNum() {
		return bizNum;
	}
	public void setBizNum(int bizNum) {
		this.bizNum = bizNum;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "PlayerPrice [bizNum=" + bizNum + ", playerId=" + playerId
				+ ", price=" + price + ", time=" + time + "]";
	}
}
