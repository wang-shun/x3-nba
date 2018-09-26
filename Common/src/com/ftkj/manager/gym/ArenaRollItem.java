package com.ftkj.manager.gym;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EArenaRollType;
import com.ftkj.util.excel.RowData;

public class ArenaRollItem extends ExcelBean{
	private int id;
	private int rid;
	private EArenaRollType giftType;
	private int p;
	private int gold;
	private int defend;
	private int power;
	
	@Override
	public void initExec(RowData row) {
		int t = row.get("type");
		this.giftType = EArenaRollType.values()[t];
	}
	
	public int getId() {
		return id;
	}

	public int getRid() {
		return rid;
	}
	public int getPower() {
		return power;
	}
	public EArenaRollType getType() {
		return giftType;
	}
	public int getP() {
		return p;
	}
	public int getGold() {
		return gold;
	}
	public int getDefend() {
		return defend;
	}
}
