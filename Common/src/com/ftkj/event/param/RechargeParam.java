package com.ftkj.event.param;

import org.joda.time.DateTime;

import com.ftkj.console.GameConsole;
import com.ftkj.enums.EPayType;

/**
 * 充值参数
 * @author Jay
 * @time:2018年2月5日 下午5:33:18
 */
public class RechargeParam {

	/** 球队ID*/
	public long teamId;
	/** 充值类型*/
	public EPayType type;
	/** 充值金额*/
	public int fk;
	/** 充值时间*/
	public DateTime time;
	
	public RechargeParam(long teamId, EPayType type, int fk, long millis) {
		this.teamId = teamId;
		this.type = type;
		this.fk = fk;
		if(millis < GameConsole.Min_Date.getMillis()) {
			time = DateTime.now();
		}else {
			this.time = new DateTime(millis);
		}
	}

}
