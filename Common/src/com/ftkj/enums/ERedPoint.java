package com.ftkj.enums;

public enum ERedPoint {

	// 活动默认返回
	默认(0),
	// 1000 + 活动ID == 对应活动的ID红点状态;
	活动(1000),
	// 2000 + 活动ID == 对应活动的ID红点状态;
	福利(2000),
		
	球馆(3000),
		球馆修补(3001),
		教练获得(3002),
		任务领取(3003),
    /** 竞技场 */
    Arena(3900),
    /**联盟*/
    LeagueDailyTask(3100),
    /** 联盟每日贡献礼包领取*/
    LeagueDailyGetReward(3101),
    /**主城目标*/
    MainTarget(3110),
    

    基础功能(4000),
		待签球员(4001),
		球员投资(4002),
		联盟申请(4003),

		
	
	// 5000开始就不用这样分段了
	
	;
	
	/**
	 * 以1000为单位分段
	 */
	private int id;

	private ERedPoint(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
}
