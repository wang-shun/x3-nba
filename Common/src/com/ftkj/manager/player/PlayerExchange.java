package com.ftkj.manager.player;

import org.joda.time.DateTime;

import com.ftkj.db.domain.PlayerExchangePO;
import com.ftkj.util.DateTimeUtil;

public class PlayerExchange {
	
	private PlayerExchangePO po;
	
	public PlayerExchange(PlayerExchangePO po) {
		this.po = po;
	}
	
	public int getPlayerId() {
		return this.po.getPlayerId();
	}

	public int getExchangeNum() {
		return this.po.getExchangeNum();
	}

	public DateTime getCreateDate() {
		return this.po.getCreateDate();
	}
	
	public String getCreateDateStr() {
		return DateTimeUtil.getString(this.po.getCreateDate());
	}
	
	public void save() {
		this.po.save();
	}
	
	public void addExchangeNum(int num) {
		this.po.setExchangeNum(this.po.getExchangeNum() + num);
	}
	
}
