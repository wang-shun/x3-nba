package com.ftkj.manager.vip;

import org.joda.time.DateTime;

import com.ftkj.console.VipConsole;
import com.ftkj.db.domain.VipPO;
import com.ftkj.db.domain.active.base.DBList;

public class TeamVip {
	
	private VipPO po;
	
	/**
	 * 购买状态
	 */
	 private DBList buyStatus;
	
	public TeamVip(VipPO po) {
		this.po = po;
		buyStatus = new DBList(this.po.getBuyStatus());
	}
	
	public TeamVip() {
		buyStatus = new DBList();
	}

	public int getLevel() {
		return this.po.getLevel();
	}
	
	/**
	 * 累计充值金额
	 * @param money
	 */
	public void addMoney(int money) {
		this.po.setAddMoney(this.po.getAddMoney() + money);
	}
	
	public void addExp(int exp) {
		this.po.setExp(this.po.getExp() + exp);
	}
	
	public int getAddMoney() {
		return this.po.getAddMoney();
	}
	
	public int getExp() {
		return this.po.getExp();
	}
	
	public int getTotalExp() {
		return this.po.getExp() + this.po.getAddMoney();
	}
	
	public long getTeamId() {
		return this.po.getTeamId();
	}

	/**
	 * 检查VIP等级是否更新
	 * @return true 升级
	 */
	public boolean updateLevel() {
		int oldLevel = this.getLevel();
		int newLevel = VipConsole.getLevelByAddMoney(this.po.getAddMoney() + this.po.getExp());
		boolean isUp = newLevel > oldLevel;
		if(isUp) {
			this.po.setLevel(newLevel);
			this.save();
		}
		return isUp;
	}
	
	public void save() {
		this.po.setBuyStatus(buyStatus.getValueStr());
		this.po.setUpdateTime(DateTime.now());
		this.po.save();
	}
	
	public DBList getBuyStatus() {
		return buyStatus;
	}

	/**
	 * 等级>=1是VIP
	 * @return
	 */
	public boolean isVip() {
		return this.getLevel() >=1;
	}
	
}
