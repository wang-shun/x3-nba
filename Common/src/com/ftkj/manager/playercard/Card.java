package com.ftkj.manager.playercard;

import com.ftkj.db.domain.CardPO;

/**
 * 球星卡收集
 * @author Jay
 * @time:2018年3月7日 下午4:04:51
 */
public class Card {

	private CardPO cardPO;

	public Card() {
	}
	
	public Card(CardPO cardPO) {
		super();
		this.cardPO = cardPO;
	}

	public CardPO getCardPO() {
		return cardPO;
	}
	
	public int getQua() {
		return cardPO.getQua();
	}
	
	public void save() {
		this.cardPO.save();
	}

	public long getTeamId() {
		return this.cardPO.getTeamId();
	}

	public int getPlayerId() {
		return this.cardPO.getPlayerId();
	}

	public void setPlayerId(int playerId) {
		this.cardPO.setPlayerId(playerId);
	}

	public int getStarLv() {
		return this.cardPO.getStarLv();
	}

	public void setStarLv(int starLv) {
		this.cardPO.setStarLv(starLv);
	}
	
	public void setQuality(int quality) {
		this.cardPO.setQua(quality);
	}
	
	public int getQuality() {
		return this.cardPO.getQua();
	}
	
	public int getType() {
		return this.cardPO.getType();
	}

	public int getExp() {
		return this.cardPO.getExp();
	}
	
	/**
	 * 增加经验
	 * @param exp 
	 * @return 最新经验
	 */
	public int addExp(int exp) {
		int newExp = this.getExp() + exp;
		this.cardPO.setExp(newExp);
		return newExp;
	}
	
	public void del() {
		this.cardPO.del();
	}
	
	/**获取,突破已经被吞噬的底薪球员数量*/
	public int getCostNum() {
		return this.cardPO.getCostNum();
	}

	/**设置,突破已经被吞噬的底薪球员数量*/
	public void setCostNum(int costNum) {
		this.cardPO.setCostNum(costNum);
	}
	
}
