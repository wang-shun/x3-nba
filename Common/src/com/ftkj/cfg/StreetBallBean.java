package com.ftkj.cfg;

import java.util.Arrays;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

/**
 * 街球配置
 * @author Jay
 * @time:2017年7月17日 下午4:31:51
 */
public class StreetBallBean extends ExcelBean {

	private int id;
	private int type;
	private int stage;
	private int[] weekOfDays;
	private int needCap;
	private int needLv;
	private int npcId;
	private int drop;
	
	@Override
	public void initExec(RowData row) {
		weekOfDays = Arrays.stream(row.get("weekOfDay").toString().split(",")).mapToInt(i->new Integer(i)).toArray();
	}
	
	public int[] getWeekOfDays() {
		return weekOfDays;
	}

	public void setWeekOfDays(int[] weekOfDays) {
		this.weekOfDays = weekOfDays;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public int getNeedCap() {
		return needCap;
	}
	public void setNeedCap(int needCap) {
		this.needCap = needCap;
	}
	public int getNeedLv() {
		return needLv;
	}
	public void setNeedLv(int needLv) {
		this.needLv = needLv;
	}
	public int getNpcId() {
		return npcId;
	}
	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}
	public int getDrop() {
		return drop;
	}
	public void setDrop(int drop) {
		this.drop = drop;
	}
	
}
