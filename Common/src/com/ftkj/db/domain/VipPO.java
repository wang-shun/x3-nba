package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * VIP
 * @author Jay
 * @time:2017年9月18日 下午4:08:37
 */
public class VipPO extends AsynchronousBatchDB {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long teamId;
	private int addMoney;
	private int exp;
	private int level;
	/**
	 * VIP等级礼包的购买状态，0未买，1已买
	 */
	private String buyStatus;
	private DateTime createTime;
	private DateTime updateTime;
	
	public VipPO() {
	}
	
	public VipPO(long teamId) {
		super();
		this.teamId = teamId;
		this.createTime = DateTime.now();
		this.createTime = DateTime.now();
	}


	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getAddMoney() {
		return addMoney;
	}

	public void setAddMoney(int addMoney) {
		this.addMoney = addMoney;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId, this.addMoney, this.exp, this.level, this.buyStatus, this.createTime, this.updateTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, add_money, exp, level, buy_status, create_time, update_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.addMoney, this.exp, this.level, this.buyStatus, this.createTime, this.updateTime);
    }

	@Override
	public String getTableName() {
		return "t_u_vip";
	}

	@Override
	public void del() {
	}

	public String getBuyStatus() {
		return buyStatus;
	}

	public void setBuyStatus(String buyStatus) {
		this.buyStatus = buyStatus;
	}


}
