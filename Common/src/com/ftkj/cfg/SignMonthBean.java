package com.ftkj.cfg;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * 每月签到配置
 * @author Jay
 * @time:2017年8月26日 上午11:04:49
 */
public class SignMonthBean extends ExcelBean {

	
	private int month;
	private int day;
	private List<PropSimple> awardList;
	private int vip;
	private int multiple;
	
	@Override
	public void initExec(RowData row) {
		awardList = PropSimple.getPropBeanByStringNotConfig(row.get("award"));
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public List<PropSimple> getAwardList() {
		return awardList;
	}

	public void setAwardList(List<PropSimple> awardList) {
		this.awardList = awardList;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}
	
	public SignMonthBean getObject() {
		return this;
	}
}
