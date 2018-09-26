package com.ftkj.cfg;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * 周奖励
 * @author lin.lin
 *
 */
public class GroupWarWeekAwardBean  extends ExcelBean {

	private int startRank;
	private int endRank;
	private int priceCap; // 工资帽奖励
	private List<PropSimple> awardList; // 其他道具奖励
	
	@Override
	public void initExec(RowData row) {
		awardList = PropSimple.getPropBeanByStringNotConfig(row.get("award"));
	}
	public int getStartRank() {
		return startRank;
	}
	public void setStartRank(int startRank) {
		this.startRank = startRank;
	}
	public int getEndRank() {
		return endRank;
	}
	public void setEndRank(int endRank) {
		this.endRank = endRank;
	}
	public int getPriceCap() {
		return priceCap;
	}
	public void setPriceCap(int priceCap) {
		this.priceCap = priceCap;
	}
	public List<PropSimple> getAwardList() {
		return awardList;
	}
	public void setAwardList(List<PropSimple> awardList) {
		this.awardList = awardList;
	}
	@Override
	public String toString() {
		return "GroupWarWeekAwardBean [startRank=" + startRank + ", endRank=" + endRank + ", priceCap=" + priceCap
				+ "]";
	}
	
	
}
