package com.ftkj.manager.trade;

import com.ftkj.db.domain.TradeP2PPO;

import org.joda.time.DateTime;

public class TradeP2P implements OrderTrade {

    private TradeP2PPO tradePO;

    public TradeP2P() {
    }

    public TradeP2P(TradeP2PPO tradePO) {
        this.tradePO = tradePO;
    }

    public void save() {
        this.tradePO.save();
    }

    //---------------------------
    public int getId() {
        return this.tradePO.getId();
    }

    public int getTalent() {
        return this.tradePO.getMinTalent();
    }

    public long getTeamId() {
        return this.tradePO.getTeamId();
    }

    public int getPlayerId() {
        return this.tradePO.getPlayerId();
    }

    public int getPrice() {
        return this.tradePO.getPrice();
    }

    public int getMoney() {
        return this.tradePO.getBuyMoney();
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

    //	public int getMarketPrice() {
    //		return this.tradePO.getMarketPrice();
    //	}
    //
    //	public void setMarketPrice(int marketPrice) {
    //		this.tradePO.setMarketPrice(marketPrice);
    //	}
    //
    public long getBuyTeam() {
        return this.tradePO.getBuyTeam();
    }

    public void setBuyTeam(long buyTeam) {
        this.tradePO.setBuyTeam(buyTeam);
    }

    public String getPosition() {
        return this.tradePO.getPosition();
    }

    public int getMarketPrice() {
        return this.tradePO.getMarketPrice();
    }

    public void setMarketPrice(int marketPrice) {
        this.tradePO.setMarketPrice(marketPrice);
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
