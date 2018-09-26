package com.ftkj.enums;

/**
 * 活动状态
 * @author Jay
 * @time:2017年8月30日 下午8:45:17
 */
public enum EActiveStatus {
	
	未开始(0),
	进行中(1),
	已结束(2),
	维护(3),
	停止(4),
	;
	
	private int status;
	
	EActiveStatus(int status) {
		this.status = status;
	}
	
	public int getStatusCode() {
		return this.status;
	}
}
