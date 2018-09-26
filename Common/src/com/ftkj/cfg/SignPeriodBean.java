package com.ftkj.cfg;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

public class SignPeriodBean extends ExcelBean {

	private int period;
	private int day;
	private List<PropSimple> awardList;
	
	@Override
	public void initExec(RowData row) {
		 awardList = PropSimple.getPropBeanByStringNotConfig(row.get("award"));
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
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

}
