package com.ftkj.manager.buff;

import org.joda.time.DateTime;

import com.ftkj.db.domain.BuffPO;
import com.ftkj.db.domain.active.base.DBList;

public class Buff {

	private BuffPO buffPO;
	private DBList values;
	
	public Buff() {
	}
	public Buff(BuffPO buffPO) {
		this.buffPO = buffPO;
		this.values = new DBList(buffPO.getParams());
	}
	
	public void save() {
		this.buffPO.setParams(this.values.getValueStr());
		this.buffPO.save();
	}
	
	public DateTime getEndTime() {
		return this.buffPO.getEndTime();
	}
	
	public String getParams() {
		return this.buffPO.getParams();
	}
	
	public void setParams(String params) {
		this.values = new DBList(params);
		this.buffPO.setParams(params);
	}
	
	public void setEndTime(DateTime endTime) {
		this.buffPO.setEndTime(endTime);
	}
	
	public Integer getId() {
		return this.buffPO.getId();
	}
	public long getTeamId() {
		return this.buffPO.getTeamId();
	}
	
}
