package com.ftkj.db.domain;

import org.joda.time.DateTime;

/**
 * 竞猜活动的比赛数据.
 * @author mr.lei
 *
 */
public class NBAGameGuess {
    /** 比赛ID*/
	private int id;
	/** 主场球队名字*/
	private String homeName;
	/** 客场球队名字*/
	private String roadName;
	/**比赛结果:0平局,1主队赢,2客队赢*/
	private int gameResult;
	/**是否发奖:0没有,1奖励已发*/
	private int sendReward;
	/**奖励配置数据*/
	private String rewardConfig;
	/** 开始时间*/
	private DateTime startDateTime;	
	/** 结束时间*/
	private DateTime endDateTime;	
	/**是否取消:0正常,1取消*/
	private int cancel;
	
	public NBAGameGuess() {
	
	}
	
	/** 获取比赛ID*/
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	/** 获取主场球队名字*/
	public String getHomeName() {
		return homeName;
	}
	
	public void setHomeName(String homeName) {
		this.homeName = homeName;
	}
	
	/** 获取客队球队名字*/
	public String getRoadName() {
		return roadName;
	}
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	
	/** 获取比赛结果:0平局,1主队赢,2客队赢*/
	public int getGameResult() {
		return gameResult;
	}
	public void setGameResult(int gameResult) {
		this.gameResult = gameResult;
	}
	
	/** 获取是否发奖:0没有,1奖励已发*/
	public int getSendReward() {
		return sendReward;
	}
	public void setSendReward(int sendReward) {
		this.sendReward = sendReward;
	}
	
	/** 获取奖励的配置数据*/
	public String getRewardConfig() {
		return rewardConfig;
	}
	public void setRewardConfig(String rewardConfig) {
		this.rewardConfig = rewardConfig;
	}
	
	/** 获取开始时间*/
	public DateTime getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(DateTime startDateTime) {
		this.startDateTime = startDateTime;
	}
	
	/** 获取结束时间*/
	public DateTime getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(DateTime endDateTime) {
		this.endDateTime = endDateTime;
	}
	
	/** 获取是否取消:0正常,1取消*/
	public int getCancel() {
		return cancel;
	}
	public void setCancel(int cancel) {
		this.cancel = cancel;
	}
	
	
	@Override
	public String toString() {
		return "NBAGameGuess [id=" + id + ", homeName=" + homeName
				+ ", roadName=" + roadName + ", gameResult=" + gameResult
				+ ", sendReward=" + sendReward + ", rewardConfig="
				+ rewardConfig + ", startDateTime=" + startDateTime
				+ ", endDateTime=" + endDateTime + ", cancel=" + cancel + "]";
	}
	
}
