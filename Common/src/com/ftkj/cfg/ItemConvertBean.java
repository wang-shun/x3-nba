package com.ftkj.cfg;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

public class ItemConvertBean extends ExcelBean {

	private int cId;
	/**
	 * 活动数据
	 */
	private List<PropSimple> needOtherList;
	/**
	 * 正常道具
	 */
	private List<PropSimple> needPropList;
	/**
	 * 兑换目标道具
	 */
	private List<PropSimple> targetPropList;
	private int view;// 是否显示兑换商城
	private int activeType = -1; // 跟活动走的结束时间
	private DateTime endTime;
	private int teamLevel;
	private int limit;
	private int totalLimit; //总限量
	/**
	 * vip 加成
	 * 等级 ： 上限次数加成
	 */
	private Map<Integer, Integer> vipLimit;
	
	@Override
	public void initExec(RowData row) {
		needOtherList = PropSimple.getPropBeanByStringNotConfig(row.get("needOther"));
		needPropList = PropSimple.getPropBeanByStringNotConfig(row.get("needProp"));
		targetPropList = PropSimple.getPropBeanByStringNotConfig(row.get("targetProp"));
		vipLimit = Maps.newHashMap(); 
		String vipStr = row.get("vipLimitCfg").toString();
		if(vipStr != null && !vipStr.equals("")) {
			String[] li = vipStr.split(",");
			for(String s : li) {
				String[] cfg = s.split(":");
				vipLimit.put(Integer.valueOf(cfg[0]), Integer.valueOf(cfg[1]));
			}
		}
	}
	
	/**
	 * VIP等级上限加成
	 * @param vipLevel
	 * @return
	 */
	public int getVipLimit(int vipLevel) {
		if(vipLimit.containsKey(vipLevel)) {
			return vipLimit.get(vipLevel);
		}
		return 0;
	}
	
	public int getcId() {
		return cId;
	}
	public void setcId(int cId) {
		this.cId = cId;
	}
	public int getView() {
		return view;
	}
	public void setView(int view) {
		this.view = view;
	}
	public DateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}
	public int getTeamLevel() {
		return teamLevel;
	}
	public void setTeamLevel(int teamLevel) {
		this.teamLevel = teamLevel;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getActiveType() {
		return activeType;
	}
	public List<PropSimple> getNeedOther() {
		return needOtherList;
	}
	public List<PropSimple> getNeedProp() {
		return needPropList;
	}
	public List<PropSimple> getTargetProp() {
		return targetPropList;
	}

	public int getTotalLimit() {
		return totalLimit;
	}
	
}
