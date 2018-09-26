package com.ftkj.cfg.card;

/**
 * 球星卡攻防加成配置
 * @author Jay
 * @time:2018年3月7日 下午5:00:48
 */
public class PlayerCardCapBean {

	private int type;
	private int starLv;
	private int startNum;
	private int endNum;
	private int atkCap;
	private int defCap;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStarLv() {
		return starLv;
	}
	public void setStarLv(int starLv) {
		this.starLv = starLv;
	}
	public int getStartNum() {
		return startNum;
	}
	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}
	public int getEndNum() {
		return endNum;
	}
	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}
	public int getAtkCap() {
		return atkCap;
	}
	public void setAtkCap(int atkCap) {
		this.atkCap = atkCap;
	}
	public int getDefCap() {
		return defCap;
	}
	public void setDefCap(int defCap) {
		this.defCap = defCap;
	}
	@Override
	public String toString() {
		return "PlayerCardCapBean [type=" + type + ", starLv=" + starLv + ", startNum=" + startNum + ", endNum="
				+ endNum + ", atkCap=" + atkCap + ", defCap=" + defCap + "]";
	}
	
}
