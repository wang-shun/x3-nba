package com.ftkj.cfg.card;

/**
 * 球星卡攻防加成配置
 * @author Jay
 * @time:2018年3月7日 下午5:00:48
 */
public class PlayerCardStarLvExpBean {

	/**
	 * 卡组类型
	 */
	private int type;
	/**
	 * 品质
	 */
	private int quality;
	/**
	 * 星级
	 */
	private int starLv;
	/**
	 * 升级总经验
	 */
	private int totalExp;
	/**
	 * 需要底薪个数
	 */
	private int needLowPrice;
	
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
	public int getTotalExp() {
		return totalExp;
	}
	public void setTotalExp(int totalExp) {
		this.totalExp = totalExp;
	}
	public int getNeedLowPrice() {
		return needLowPrice;
	}
	public void setNeedLowPrice(int needLowPrice) {
		this.needLowPrice = needLowPrice;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	
}
