package com.ftkj.manager.equi.cfg;

import com.ftkj.util.excel.RowData;

/**
 * 装备强化配置表
 * @author lin.lin
 *
 */
public class EquiUpStrBean {

	/**
	 * 当前等级
	 */
	private int lv;
	private int showLv;
	private int showStar;
	/**
	 * 强化所需金币
	 */
	private int money;
	/**
	 * 属性增强比
	 */
	private float add;
	/**
	 * 强化类型
	 * 强化1
	 * 进阶2
	 */
	private int type;
	/**
	 * 强化道具配置
	 */
	private EquiPropsBean propsBean;
	/**
	 * 强化失败后会累计概率
	 */
	private float addProbability;
	/**
	 * 扩展解析方法
	 * @param
	 */
	public void initExec(RowData row) {
		propsBean = new EquiPropsBean(row.get("propsCfg"), row.get("probability"));
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public float getAdd() {
		return add;
	}

	public void setAdd(float add) {
		this.add = add;
	}

	public EquiPropsBean getPropsBean() {
		return propsBean;
	}

	public void setPropsBean(EquiPropsBean propsBean) {
		this.propsBean = propsBean;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getAddProbability() {
		return addProbability;
	}

	public void setAddProbability(float addProbability) {
		this.addProbability = addProbability;
	}

	public int getShowLv() {
		return showLv;
	}

	public void setShowLv(int showLv) {
		this.showLv = showLv;
	}

	public int getShowStar() {
		return showStar;
	}

	public void setShowStar(int showStar) {
		this.showStar = showStar;
	}

}
