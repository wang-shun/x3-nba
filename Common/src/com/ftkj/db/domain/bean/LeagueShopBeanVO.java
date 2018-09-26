package com.ftkj.db.domain.bean;

/**
 * @author tim.huang
 * 2017年5月24日
 * 联盟商城配置
 */
public class LeagueShopBeanVO {
	private int propId;
	private int num;
	private int dayLimit;
	private int feats;
	private int levelLimit;
	
	
	public LeagueShopBeanVO() {
		super();
	}
	public int getPropId() {
		return propId;
	}
	public void setPropId(int propId) {
		this.propId = propId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getDayLimit() {
		return dayLimit;
	}
	public void setDayLimit(int dayLimit) {
		this.dayLimit = dayLimit;
	}
	public int getFeats() {
		return feats;
	}
	public void setFeats(int feats) {
		this.feats = feats;
	}
	public int getLevelLimit() {
		return levelLimit;
	}
	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}
	
}
