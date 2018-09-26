package com.ftkj.manager.investment;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年12月29日
 * 玩家投资球员数据
 */
public class TeamPlayerInvestment extends AsynchronousBatchDB {
	private long teamId;
	private int playerId;
	private int total;
	private DateTime buyTime;
	private float basePrice;
	
	public TeamPlayerInvestment(long teamId, int playerId, int total,
			DateTime buyTime, float basePrice) {
		super();
		this.teamId = teamId;
		this.playerId = playerId;
		this.total = total;
		this.buyTime = buyTime;
		this.basePrice = basePrice;
	}
	
	
	public float getRate(){
		int day = getBuyDay();
		if(day>=15) {
			return 0.15f;
		} else if(day<=3) {
			return 0.01f;
		}
		float rate = (day)/100f;
		return rate;
	}
	
	public void sale(int num,int price){
		int oldTotal = getTotal();
		addCount(-num);
		float rate = getRate();
		float basePrice = 0;
		if(getTotal()>0){
			basePrice = (getBasePrice() * oldTotal - num * price * (1f - rate))/(getTotal() + 0f);
		}
		setBasePrice(basePrice);
		save();
	}
	
	public void buy(int num,int price){
		if(this.total<=0){
			this.buyTime = DateTime.now();
		}
		int oldTotal = getTotal();
		addCount(num);
		float rate = getRate();
		float basePrice = (getBasePrice() * oldTotal + num * price  + (float)Math.ceil(num * price * rate))/(getTotal()+0f);
		setBasePrice(basePrice);
		save();
	}
	
	public TeamPlayerInvestment() {
	}
	
	public boolean isEnd(){
		int day = getBuyDay();
		return this.total>0 && day>=16;
	}
	
	public void addCount(int val){
		this.total += val;
		this.save();
	}

	public DateTime getBuyTime() {
		return buyTime;
	}

	public int getBuyDay(){
		return DateTimeUtil.getDaysBetweenNum(buyTime, DateTime.now(), 0) + 1;
	}
	
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public void setBuyTime(DateTime buyTime) {
		this.buyTime = buyTime;
	}
	
	public float getBasePrice() {
		return basePrice;
	}


	public void setBasePrice(float basePrice) {
		this.basePrice = basePrice;
	}


	public void setBasePrice(int basePrice) {
		this.basePrice = basePrice;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.playerId,this.total,this.basePrice,this.buyTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, player_id, total, price, buy_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.playerId,this.total,this.basePrice,this.buyTime);
    }

	@Override
	public String getTableName() {
		return "t_u_player_inv";
	}

	@Override
	public void del() {
		
	}


}
