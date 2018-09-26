package com.ftkj.manager.bid;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;

import java.util.List;

/**
 * @author tim.huang 2018年3月20日 球员竞价训练对象
 */
public class PlayerBid extends AsynchronousBatchDB {

	private static final long serialVersionUID = 1L;
	
	private long teamId;
	private DateTime endTime;
	private int minPrice;
	private int maxPrice;
	private int status;
	private DateTime createTime;
	
	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
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
