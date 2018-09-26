package com.ftkj.cfg.card;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * 球星卡攻防加成配置
 * @author Jay
 * @time:2018年3月7日 下午5:00:48
 */
public class PlayerCardCompositeBean extends ExcelBean {

	/**
	 * 卡组类型
	 */
	private int tab;
	/**
	 * 合成需要数量
	 */
	private PropSimple compositePro;
	
	public int getTab() {
		return tab;
	}
	public void setTab(int tab) {
		this.tab = tab;
	}
	public PropSimple getCompositePro() {
		return compositePro;
	}
	public void setCompositePro(PropSimple compositePro) {
		this.compositePro = compositePro;
	}
	@Override
	public void initExec(RowData row) {
		compositePro = PropSimple.getPropSimpleByString(row.get("composite"));
	}
}
