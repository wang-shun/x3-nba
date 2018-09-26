package com.ftkj.manager.player;

import org.joda.time.DateTime;

import com.ftkj.db.domain.PlayerLowerSalaryLogPO;

/**
 * 
 * @author mr.lei
 * @time 2018年8月6日17:18:59
 */
public class PlayerLowerSalaryLog {
	
	private PlayerLowerSalaryLogPO po;

	public PlayerLowerSalaryLog(PlayerLowerSalaryLogPO po) {
		super();
		this.po = po;
	}
	
	/**
	 * 生成一条新秀球员降薪日志记录,并且保存到数据库.
	 * @param plsId
	 * @param teamId
	 * @param playerId
	 * @param beforeSalary
	 * @param afterSalary
	 * @param amount
	 * @param createTime
	 * @return
	 */
	public static PlayerLowerSalaryLog createPlayerLowerSalaryLog(long plsId, long teamId, int playerId,
			int beforeSalary, int afterSalary, int amount, DateTime createTime){
		PlayerLowerSalaryLogPO po = new PlayerLowerSalaryLogPO(plsId, teamId, 
				playerId, beforeSalary, afterSalary, amount, createTime);
		po.save();
		return new PlayerLowerSalaryLog(po);
	}
}
