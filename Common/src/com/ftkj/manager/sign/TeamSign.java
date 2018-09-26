package com.ftkj.manager.sign;

import org.joda.time.DateTime;

import com.ftkj.db.domain.SignPO;
import com.ftkj.db.domain.active.base.DBList;

public class TeamSign {

	private SignPO signPO;
	/**
	 * 奖励状态
	 */
	private DBList awardStatus;
	
	public TeamSign(SignPO signMonthPO) {
		this.signPO = signMonthPO;
		this.awardStatus = new DBList(signMonthPO.getStatus());
	}
	
	/**
	 * 7天签到专用
	 * @param signMonthPO
	 * @param day
	 */
	public TeamSign(SignPO signMonthPO, int day) {
		this.signPO = signMonthPO;
		if(signMonthPO.getStatus() != null) {
			this.awardStatus = new DBList(signMonthPO.getStatus());
		}else {
			this.awardStatus = new DBList(day);
		}
	}
	
	public int getType() {
		return this.signPO.getType();
	}
	
	public int getPeriod() {
		return this.signPO.getPeriod();
	}
	
	public void setPeriod(int period) {
		this.signPO.setPeriod(period);
	}
	
	public DateTime getLastSignTime() {
		return this.signPO.getLastSignTime();
	}
	
	public void addSignNum() {
		this.signPO.setSignNum(this.signPO.getSignNum()+1);
	}
	
	public void addPatchNum() {
		this.signPO.setPatchNum(this.signPO.getPatchNum()+1);
	}

	public void addTotalSign() {
		this.signPO.setTotalSign(this.signPO.getTotalSign()+1);
	}
	
	public void addTotalPatch() {
		this.signPO.setTotalPatch(this.signPO.getTotalPatch()+1);
	}
	
	public void setLastSignTime(DateTime time) {
		this.signPO.setLastSignTime(time);
	}
	
	public int getSignNum() {
		return this.signPO.getSignNum();
	}
	
	public int getPatchNum() {
		return this.signPO.getPatchNum();
	}
	
	/**
	 * 7天签到清空：清空签到，补签，和领奖状态
	 */
	public void clearSignNum() {
		this.signPO.setSignNum(0);
		this.signPO.setPatchNum(0);
		this.awardStatus.setAllValue(0);
	}
	
	public void save() {
		this.signPO.setStatus(this.awardStatus.getValueStr());
		this.signPO.save();
	}

	@Override
	public String toString() {
		return "TeamSign [signPO=" + signPO.toString() + "]";
	}

	public DBList getAwardStatus() {
		return awardStatus;
	}
	
	public String getStatus() {
		return this.signPO.getStatus();
	}
	
	public void setStatus(String status) {
		this.signPO.setStatus(status);
	}
}
