package com.ftkj.manager.shop;

import com.ftkj.manager.prop.bean.PropBean;

public class MoneyShopBean {
	private PropBean prop;
	private int num;
	private int dayLimit;
	private int money;
	
	public MoneyShopBean(PropBean prop, int num, int dayLimit, int money) {
		super();
		this.prop = prop;
		this.num = num;
		this.dayLimit = dayLimit;
		this.money = money;
	}
	public PropBean getProp() {
		return prop;
	}
	public int getNum() {
		return num;
	}
	public int getDayLimit() {
		return dayLimit;
	}
	public int getMoney() {
		return money;
	}
	
	
}
