package com.ftkj.manager.trade;

import com.ftkj.db.domain.TradePO;

import org.joda.time.DateTime;

public class Trade implements OrderTrade {

	private TradePO tradePO;
	
	public Trade() {
	}
	public Trade(TradePO tradePO) {
		this.tradePO = tradePO;
	}
	
	public void save() {
		this.tradePO.save();
	}
	
	//---------------------------
	public int getId() {
		return this.tradePO.getId();
	}

	public String getTalentScore(){
		return this.tradePO.getTalent();
	}

	public long getTeamId() {
		return this.tradePO.getTeamId();
	}


	public int getPid() {
		return this.tradePO.getPid();
	}

	public int getPlayerId() {
		return this.tradePO.getPlayerId();
	}

	public int getPrice() {
		return this.tradePO.getPrice();
	}

	public int getMoney() {
		return this.tradePO.getMoney();
	}


	public int getStatus() {
		return this.tradePO.getStatus();
	}

	public void setStatus(int status) {
		this.tradePO.setStatus(status);
	}

	public DateTime getCreateTime() {
		return this.tradePO.getCreateTime();
	}

	public DateTime getEndTime() {
		return this.tradePO.getEndTime();
	}
	
	public long getEndTimeMillis() {
		return this.tradePO.getEndTime().getMillis();
	}

	public DateTime getDealTime() {
		return this.tradePO.getDealTime();
	}

	public void setDealTime(DateTime dealTime) {
		this.tradePO.setDealTime(dealTime);
	}
	
	public int getMarketPrice() {
		return this.tradePO.getMarketPrice();
	}
	
	public void setMarketPrice(int marketPrice) {
		this.tradePO.setMarketPrice(marketPrice);
	}
	
	public long getBuyTeam() {
		return this.tradePO.getBuyTeam();
	}

	public void setBuyTeam(long buyTeam) {
		this.tradePO.setBuyTeam(buyTeam);
	}
	
	public String getPosition() {
		return this.tradePO.getPosition();
	}

    public boolean isStickTop() {
        return tradePO.isStickTop();
    }

    public void setStickTop(boolean stickTop) {
        tradePO.setStickTop(stickTop);
    }

    @Override
	public String toString() {
		return "Trade [tradePO=" + tradePO + "]";
	}
	
	
	
}
