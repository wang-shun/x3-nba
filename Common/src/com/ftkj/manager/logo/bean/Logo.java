package com.ftkj.manager.logo.bean;

import com.ftkj.db.domain.LogoPO;

public class Logo {
	
	private LogoPO logoPO;
	
	public Logo(LogoPO logoPO) {
		this.logoPO = logoPO;
	}
	
	public void save() {
		this.logoPO.save();
	}
	
	public void del() {
		this.logoPO.del();
	}
	
	public int getLogoId() {
		return this.logoPO.getId();
	}
	
	public int getPlayerId() {
		return this.logoPO.getPlayerId();
	}

	
	public int getQuality() {
		return this.logoPO.getQuality();
	}
	
}
