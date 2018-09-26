package com.ftkj.cfg;

/**
 * 球员兑换配置
 * @author Jay
 * @time:2018年3月9日 下午4:30:05
 */
public class PlayerShopBean {

	private String grade;
	private int recovery; // 回收数量
	private int cash; // 兑换数量
	
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public int getRecovery() {
		return recovery;
	}
	public void setRecovery(int recovery) {
		this.recovery = recovery;
	}
	public int getCash() {
		return cash;
	}
	public void setCash(int cash) {
		this.cash = cash;
	}
	
}
