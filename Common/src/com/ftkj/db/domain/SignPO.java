package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.console.GameConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

public class SignPO extends AsynchronousBatchDB {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 1，月签到； 
	 * 2，7天签到;
	 */
	private int type; 
	private long teamId;
	private int period;
	/**
	 * 签到次数
	 */
	private int signNum;
	/**
	 * 补签次数，累计的总签到次数
	 */
	private int patchNum;
	/**
	 * 总次数
	 */
	private int totalSign;
	/**
	 * 总补签
	 */
	private int totalPatch;
	
	/**
	 * 预留
	 * 记录7天签到的充值日志yyyy-MM-dd
	 */
	private String status;
	
	private DateTime lastSignTime;
	
	public SignPO() {
		this.lastSignTime = GameConsole.Min_Date;
	}
	public SignPO(long teamId, int type, int period) {
		this.teamId = teamId;
		this.period = period;
		this.type = type;
		this.lastSignTime = GameConsole.Min_Date;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getSignNum() {
		return signNum;
	}

	public void setSignNum(int signNum) {
		this.signNum = signNum;
	}

	public int getPatchNum() {
		return patchNum;
	}

	public void setPatchNum(int patchNum) {
		this.patchNum = patchNum;
	}

	public DateTime getLastSignTime() {
		return lastSignTime;
	}

	public void setLastSignTime(DateTime lastSignTime) {
		this.lastSignTime = lastSignTime;
	}

	@Override
	public String getSource() {
		return  StringUtil.formatSQL(this.teamId, this.type, this.period, this.signNum, this.patchNum, this.totalSign, this.totalPatch, this.status, this.lastSignTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, type, period, sign_num, patch_num, total_sign, total_patch, status, last_sign_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.type, this.period, this.signNum, this.patchNum, this.totalSign, this.totalPatch, this.status, this.lastSignTime);
    }

	@Override
	public String getTableName() {
		return "t_u_sign";
	}
	
	public int getTotalSign() {
		return totalSign;
	}
	public void setTotalSign(int totalSign) {
		this.totalSign = totalSign;
	}
	
	public int getTotalPatch() {
		return totalPatch;
	}
	public void setTotalPatch(int totalPatch) {
		this.totalPatch = totalPatch;
	}
	@Override
	public void del() {

	}
	@Override
	public String toString() {
		return "SignPO [type=" + type + ", teamId=" + teamId + ", period=" + period + ", signNum=" + signNum
				+ ", patchNum=" + patchNum + ", status=" + status + ", lastSignTime=" + lastSignTime + "]";
	}


}
