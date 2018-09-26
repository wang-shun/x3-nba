package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

public class PlayerPowerBean extends ExcelBean{
    /** 球员ID*/
	private int playerId;
	/** 球员体能*/
	private int rate;
	
	public PlayerPowerBean() {
	}
	public PlayerPowerBean(int playerId) {
		super();
		this.playerId = playerId;
		this.rate = 75;
	}
	@Override
	public void initExec(RowData row) {
		
	}
	public int getPlayerId() {
		return playerId;
	}
	public int getRate() {
		return rate;
	}
	
	
}
