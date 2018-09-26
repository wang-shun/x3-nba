package com.ftkj.manager.shop;

import com.ftkj.manager.prop.bean.PropBean;

/**
 * @author tim.huang
 * 2017年5月24日
 * 联盟商城配置
 */
public class LeagueShopBean {
	private PropBean prop;
	private int num;
	private int dayLimit;
	private int feats;
	private int levelLimit;
	
	
	public LeagueShopBean(PropBean prop, int num, int dayLimit, int feats,
			int levelLimit) {
		super();
		this.prop = prop;
		this.num = num;
		this.dayLimit = dayLimit;
		this.feats = feats;
		this.levelLimit = levelLimit;
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
	public int getFeats() {
		return feats;
	}
	public int getLevelLimit() {
		return levelLimit;
	}
	
}
