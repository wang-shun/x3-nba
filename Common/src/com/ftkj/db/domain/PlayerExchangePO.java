package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * 球员兑换统计
 * @author Jay
 * @time:2018年3月3日 下午4:24:19
 */
public class PlayerExchangePO  extends AsynchronousBatchDB {

	
	private static final long serialVersionUID = 1L;
	
	private int playerId;
	/** 签约次数*/
	private int exchangeNum;
	private DateTime createDate;
	
	public PlayerExchangePO() {
	}
	public PlayerExchangePO(int playerId, DateTime createDate) {
		super();
		this.playerId = playerId;
		this.createDate = createDate.withMillisOfDay(0);
	}

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getExchangeNum() {
		return exchangeNum;
	}

	public void setExchangeNum(int exchangeNum) {
		this.exchangeNum = exchangeNum;
	}

	public DateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(DateTime createDate) {
		this.createDate = createDate;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.playerId, this.exchangeNum, this.createDate);
	}

	@Override
	public String getRowNames() {
		return "player_id, exchange_num, create_date";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.playerId, this.exchangeNum, this.createDate);
    }

	@Override
	public String getTableName() {
		return "t_c_player_exchange";
	}

	@Override
	public void del() {
		// 只保留近30天兑换数据
	}


}
