package com.ftkj.manager.custom;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年8月30日
 *  竞猜结果返回
 */
public class CustomGuessResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int roomId;
	
	private boolean A;
	
	private float rate;
	
	private String homeName;
	private String awayName;
	
	public CustomGuessResult(int roomId) {
		super();
		this.roomId = roomId;
	}

	public String getHomeName() {
		return homeName;
	}

	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}

	public String getAwayName() {
		return awayName;
	}

	public void setAwayName(String awayName) {
		this.awayName = awayName;
	}

	public boolean isA() {
		return A;
	}

	public void setA(boolean a) {
		A = a;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
}
