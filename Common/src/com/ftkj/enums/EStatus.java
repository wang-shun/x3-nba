package com.ftkj.enums;

/**
 * @author tim.huang
 * 2017年2月16日
 * 状态枚举
 */
public enum EStatus {
	Close(0),//通用关闭
	Open(1),
	
	ScoutDefault(0),//球探默认
	ScoutSign(1), //球探签约
	
	TaskDefault(0),//任务默认
	TaskOpen(1),//任务开启
	TaskFinish(2),//任务完成
	TaskClose(3),//任务关闭

	FriendAdd(1),//好友添加
	FriendDel(2),//好友删除
	
	False(0),
	True(1),

	
	;//通用开启
	
	
	private int id;

	private EStatus(int id) {
		this.id = id;
		
	}
	
	public int getId() {
		return id;
	}
}
