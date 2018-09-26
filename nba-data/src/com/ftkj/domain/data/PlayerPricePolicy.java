package com.ftkj.domain.data;

public class PlayerPricePolicy {
	private int id;	
	private String grade;	
	private int minMarketPrice;	
	private int maxMarketPrice;	
	private int num;	
	private int top;
	
	public PlayerPricePolicy(){}
	
	public PlayerPricePolicy(String grade,int top){
		this.grade = grade;
		this.top = top;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public int getMinMarketPrice() {
		return minMarketPrice;
	}
	public void setMinMarketPrice(int minMarketPrice) {
		this.minMarketPrice = minMarketPrice;
	}
	public int getMaxMarketPrice() {
		return maxMarketPrice;
	}
	public void setMaxMarketPrice(int maxMarketPrice) {
		this.maxMarketPrice = maxMarketPrice;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}

	@Override
	public String toString() {
		return "PlayerPricePolicy [grade=" + grade + ", id=" + id
				+ ", maxMarketPrice=" + maxMarketPrice + ", minMarketPrice="
				+ minMarketPrice + ", num=" + num + ", top=" + top + "]";
	}
}
