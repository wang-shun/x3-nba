package com.ftkj.manager.friend.bean;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * 好友请求
 * @author Jay
 * @time:2017年4月26日 下午3:12:39
 */
public class FriendReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long teamId;
	
	private long friendTeamId;
	
	private String msg;
	// 0,请求， 1同意， 2，拒绝
	private int status;
	
	private DateTime createTime;
	
	public FriendReq(long teamId, long friendTeamId, String msg) {
		this.teamId = teamId;
		this.friendTeamId = friendTeamId;
		this.msg = msg;
		this.status = 0;
		this.createTime = DateTime.now();
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public long getFriendTeamId() {
		return friendTeamId;
	}

	public void setFriendTeamId(long friendTeamId) {
		this.friendTeamId = friendTeamId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}
	
	
}
