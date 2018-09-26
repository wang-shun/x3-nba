package com.ftkj.enums;

/**
 * 多人赛状态
 * @Description:
 * @author Jay
 * @time:2017年5月17日 下午8:43:20
 */
public enum EMatchStatus {

	开始报名(1),
	比赛中(2),
	结束(3),
	异常(3),
	;
	
	public int status;

	private EMatchStatus(int id) {
		this.status = id;
	}
}
