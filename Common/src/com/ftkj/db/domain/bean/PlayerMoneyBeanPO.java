package com.ftkj.db.domain.bean;

import org.joda.time.DateTime;

import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.excel.RowData;

public class PlayerMoneyBeanPO {
    /** 球员ID*/
	private int playerId;
	/** 球员底薪*/
	private int money;
	/** 时间*/
	private DateTime moneyTime;
	
	public void initExec(RowData row){
		this.moneyTime = DateTimeUtil.getDateTime(row.get("date"));
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public DateTime getMoneyTime() {
		return moneyTime;
	}
	public void setMoneyTime(DateTime moneyTime) {
		this.moneyTime = moneyTime;
	}
}
