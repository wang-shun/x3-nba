package com.ftkj.manager.equi.cfg;

/**
 * 装备升级配置
 * @author lin.lin
 *
 */
public class EquiUpLvBean {

	/**
	 * 当前等级
	 */
	private int lv;
	/**
	 * 升下一集需要经验
	 */
	private int need;
	/**
	 * 总经验
	 */
	private int exp;
	
	/**
	 * 属性增强
	 */
	private float add;
	
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
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}

	public float getAdd() {
		return add;
	}

	public void setAdd(float add) {
		this.add = add;
	}
	
}
