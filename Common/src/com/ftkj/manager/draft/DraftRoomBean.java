package com.ftkj.manager.draft;

import java.util.List;

import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;

/**
 * @author tim.huang
 * 2017年5月4日
 * 选秀房间配置
 */
public class DraftRoomBean {
	private int roomLevel;
	private int maxPlayerCount;
	private int limitMinLevel;
	private int limitMaxLevel;
	private DropBean drop; 
	private List<PropSimple> consumeProp;
	/**
	 * 并发存在的最大房间
	 */
	private int maxRoom;
	/**
	 * 开启小时数，持续一个小时
	 */
	private String time;
	/**
	 * 冷却秒数
	 */
	private int cdTime;
	
	public DraftRoomBean(int roomLevel, int maxPlayerCount, int limitMinLevel,
			int limitMaxLevel, DropBean drop, List<PropSimple> consumeProp, int maxRoom, int cdTime, String time) {
		super();
		this.roomLevel = roomLevel;
		this.maxPlayerCount = maxPlayerCount;
		this.limitMinLevel = limitMinLevel;
		this.limitMaxLevel = limitMaxLevel;
		this.drop = drop;
		this.consumeProp = consumeProp;
		this.cdTime = cdTime;
		this.maxRoom = maxRoom;
		this.time = time;
	}
	
	public int getRoomLevel() {
		return roomLevel;
	}
	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}
	public int getLimitMinLevel() {
		return limitMinLevel;
	}
	public int getLimitMaxLevel() {
		return limitMaxLevel;
	}
	public DropBean getDrop() {
		return drop;
	}
	public List<PropSimple> getConsumeProp() {
		return consumeProp;
	}

	public int getMaxRoom() {
		return maxRoom;
	}

	public String getTime() {
		return time;
	}

	public int getCdTime() {
		return cdTime;
	}
	
}
