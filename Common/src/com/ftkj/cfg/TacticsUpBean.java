package com.ftkj.cfg;

import java.util.List;

import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

public class TacticsUpBean {

	private int lv;
	
	private List<PropSimple> needPropList;
	
	/**
	 * 扩展解析方法
	 * @param
	 */
	public void initExec(RowData row) {
		needPropList = PropSimple.getPropBeanByStringNotConfig(row.get("need"));
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public List<PropSimple> getNeedPropList() {
		return needPropList;
	}

	public void setNeedPropList(List<PropSimple> needPropList) {
		this.needPropList = needPropList;
	}
	
}
