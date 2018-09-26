package com.ftkj.cfg;

/**
 * 联盟升级所需经验配置
 * @author mr.lei
 * 2018年9月15日11:23:03
 */
public class LeagueUpgradeBean {
	/**等级*/			
	private int lv;
	
	/**升到下一级需要的经验*/			
	private int need;
	
	/**升到下一级级总共需要的经验*/			
	private int total;

	public LeagueUpgradeBean() {
		super();
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public int getNeed() {
		return need;
	}

	public void setNeed(int need) {
		this.need = need;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}
