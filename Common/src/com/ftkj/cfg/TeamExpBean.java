package com.ftkj.cfg;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * @Description:球队升级经验配置
 * @author Jay
 * @time:2017年3月28日 上午10:59:27
 */
public class TeamExpBean extends ExcelBean {

	/**
	 * 等级
	 */
	private int lv;
	/**
	 * 本级所需经验
	 */
	private int need;
	/**
	 * 到本级总经验
	 */
	private int total;
	/**
	 * 工资帽
	 */
	private int price;
	/**
	 * 仓库大小
	 */
	private int storage;
	
	private List<PropSimple> awardList;
	
	@Override
	public void initExec(RowData row) {
		awardList = PropSimple.getPropBeanByStringNotConfig(row.get("awardCfg"));
	}
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
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
	public int getStorage() {
		return storage;
	}
	public void setStorage(int storage) {
		this.storage = storage;
	}

	public List<PropSimple> getAwardList() {
		return awardList;
	}

	public void setAwardList(List<PropSimple> awardList) {
		this.awardList = awardList;
	}
	
}
