package com.ftkj.event;

public enum EEventType {

	任务调度每分钟(false),
	活动定时器(false),
	登录(false),
	球队升级(false),
	主线赛程通关(false),
	装备强化(false),
	//装备升级(false),s
	装备进化(false),
	球星卡制卡(false),
	球星卡品质(false),
	全队攻防(false),
	球队工资帽(false),
	球队现有工资(false),
	VIP升级(false),
	球员升级(false),
	球员升星(false),
	
	//添加Buff
	Buff变更(false),
	
	//消费
	消费(true),
	//充值相关
	充值(true),
	GM充值(false),
	// 充值活动统计后触发
	充值活动统计事件(false),
	// 购买福利
	购买每日充值特惠(false),
	购买成长基金(false),
	购买装备强化周卡(false),
	购买装备经验月卡(false),
	购买头像碎片周卡(false),
	购买球星卡经验月卡(false),
	购买荣耀点月卡(false),
	购买经验手册月卡(false),
	购买训练点月卡(false),
	
	//
	奖励提示(false),
	活动任务完成(false),
	得到球员(false),
	招募(false),
	
	//
	比赛结束(true),
	//
	活动任务刷新(false),
	
	服务器节点注册(false),
	
	身价更新(false),
	;
	/**
	 * 是否异步
	 */
	public boolean isAsync = false;
	
	private EEventType(boolean isAsync) {
		this.isAsync = isAsync;
	}	
}
