package com.ftkj.manager.friend.bean;

import java.io.Serializable;

import com.ftkj.db.domain.FriendPO;

public class Friend implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private FriendPO friendPO;
	
	public FriendPO getFriendPO() {
        return friendPO;
    }

    public Friend(long teamId, long friendId, int type) {
		this.friendPO = new FriendPO(teamId, friendId, type);
	}
	
	public Friend(FriendPO po) {
		this.friendPO = po;
	}

	public long getFriendTeamId() {
		return this.friendPO.getFriendTeamId();
	}

	public void setFriendPO(FriendPO friendPO) {
		this.friendPO = friendPO;
	}
	
	public void save() {
		this.friendPO.save();
	}
	
	public void del() {
		this.friendPO.del();
	}

}
