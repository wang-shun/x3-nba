package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年3月22日
 *
 */
public class PlayerMoneyPO extends AsynchronousBatchDB {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int playerId;
	private int price;
	private DateTime createTime;
	private DateTime updateTime;
	
	
	
	
	public PlayerMoneyPO() {
		super();
	}

	public PlayerMoneyPO(int playerId, int price) {
		super();
		this.playerId = playerId;
		this.price = price;
		this.createTime = DateTime.now();
		this.updateTime = this.createTime;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public DateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(DateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.playerId,this.price,this.updateTime,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "player_id,price,update_time,create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.playerId,this.price,this.updateTime,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_c_player_money";
	}

	@Override
	public void del() {
		
	}


}
