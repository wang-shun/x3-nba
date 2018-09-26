package com.ftkj.manager.team;

import java.util.List;

import com.ftkj.manager.prop.PropSimple;

/**
 * 球队等级
 * @author Jay
 * @time:2017年3月28日 上午11:18:37
 */
public class TeamGrade {
	// 当前等级
	private int grade;
	// 剩余经验
	private int exp;
	//工资帽
	private int price;
	private List<PropSimple> awardList;
	
	public TeamGrade(int grade, int exp,int price, List<PropSimple> awardList) {
		super();
		this.grade = grade;
		this.exp = exp;
		this.price = price;
		this.awardList = awardList;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}

	public List<PropSimple> getAwardList() {
		return awardList;
	}

	public void setAwardList(List<PropSimple> awardList) {
		this.awardList = awardList;
	}
	
}