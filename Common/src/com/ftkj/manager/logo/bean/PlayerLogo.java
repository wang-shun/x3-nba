package com.ftkj.manager.logo.bean;

import com.ftkj.db.domain.PlayerLogoPO;

/**
 * 荣誉头像
 * @author Jay
 * @time:2017年3月16日 上午10:11:44
 */
public class PlayerLogo {

	private PlayerLogoPO playerLogoPO;
	
	
	public PlayerLogo(PlayerLogoPO logo) {
		this.playerLogoPO = logo;
	}

	public int getPlayerId() {
		return this.playerLogoPO.getPlayerId();
	}
	
	public int getLogoId() {
		return this.playerLogoPO.getLogoId();
	}
	
	/**
	 * 升级，清空大星脉数和小星脉数
	 */
	public void upLv() {
		this.playerLogoPO.setLv(getLv()+1);
		this.setStep(0);
		this.setStarLv(0);
	}
	
	/**
	 * 更换头像设置
	 * @param logoId
	 */
	public void setLogoId(int logoId) {
		this.playerLogoPO.setLogoId(logoId);
	}
	
	public void save() {
		this.playerLogoPO.save();
	}
	
	/**
	 * 点亮小星脉，步数自增1
	 */
	public int forwardStep() {
		int step = this.playerLogoPO.getStep() + 1;
		this.playerLogoPO.setStep(step);
		return step;
	}
	
	public void setStep(int step) {
		this.playerLogoPO.setStep(step);
	}
	
	public void setStarLv(int starLv) {
		this.playerLogoPO.setStarLv(starLv);
	}
	
	public void setLv(int lv) {
		this.playerLogoPO.setLv(lv);
	}
	
	public int getStep() {
		return this.playerLogoPO.getStep();
	}
	
	public int getLv() {
		return this.playerLogoPO.getLv();
	}
	
	public int getStarLv() {
		return this.playerLogoPO.getStarLv();
	}
}
