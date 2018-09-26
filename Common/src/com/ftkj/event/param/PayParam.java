package com.ftkj.event.param;

import org.joda.time.DateTime;

import com.ftkj.enums.EModuleCode;

/**
 * 消费参数
 * @author Jay
 * @time:2018年2月5日 下午5:33:29
 */
public class PayParam {

	public long teamId;
	public EModuleCode module;
	/**
	 * 正值
	 */
	public int fk;
	public DateTime time;
	
	/**
	 * 消费
	 * @param teamId
	 * @param module
	 * @param fk 正数
	 * @param createTime
	 */
	public PayParam(long teamId, EModuleCode module, int fk, DateTime createTime) {
		this.teamId = teamId;
		this.module = module;
		this.fk = fk;
		this.time = createTime;
	}

}
