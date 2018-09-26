package com.ftkj.manager.custom;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.enums.EStatus;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年8月3日
 *
 */
public class CustomTeamGuess extends AsynchronousBatchDB {

	private long teamId;
	private int moneyA;
	private int moneyB;
	private int roomId;
	private int status;
	private DateTime createTime;
	
	public static CustomTeamGuess createCustomTeamGuess(long teamId,int roomId){
		CustomTeamGuess ctg = new CustomTeamGuess();
		ctg.setCreateTime(DateTime.now());
		ctg.setTeamId(teamId);
		ctg.setMoneyA(0);
		ctg.setMoneyB(0);
		ctg.setRoomId(roomId);
		ctg.setStatus(EStatus.Open.getId());
		ctg.save();
		return ctg;
	}
	
	public void updateMoneyA(int val){
		this.moneyA +=val;
		this.save();
	}
	public void updateMoneyB(int val){
		this.moneyB +=val;
		this.save();
	}
	
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getMoneyA() {
		return moneyA;
	}

	public void setMoneyA(int moneyA) {
		this.moneyA = moneyA;
	}

	public int getMoneyB() {
		return moneyB;
	}

	public void setMoneyB(int moneyB) {
		this.moneyB = moneyB;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.roomId,this.moneyA,
				this.moneyB,this.status,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, room_id, money_A, money_B, `status`, create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.roomId,this.moneyA,
            this.moneyB,this.status,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_custom_guess";
	}

	@Override
	public void del() {

	}


}
