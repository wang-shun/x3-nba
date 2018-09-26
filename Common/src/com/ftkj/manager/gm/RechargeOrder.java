package com.ftkj.manager.gm;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;

import java.util.List;

/**
 * @author tim.huang
 * 2017年11月24日
 * 充值订单
 */
public class RechargeOrder extends AsynchronousBatchDB {

	
	
	private int mid;
	private long userId;
	private int shardId;
	private int money;
	private int rmb;
	private int status;
	private DateTime createTime;
	private DateTime overTime;
	
	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getShardId() {
		return shardId;
	}

	public void setShardId(int shardId) {
		this.shardId = shardId;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getRmb() {
		return rmb;
	}

	public void setRmb(int rmb) {
		this.rmb = rmb;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public DateTime getOverTime() {
		return overTime;
	}

	public void setOverTime(DateTime overTime) {
		this.overTime = overTime;
	}

	@Override
	public String getSource() {
		return null;
	}

	@Override
	public String getRowNames() {
		return null;
	}


    @Override
    public List<Object> getRowParameterList() {
        return null;
    }

	@Override
	public String getTableName() {
		return null;
	}

	@Override
	public void del() {
		
	}

}
