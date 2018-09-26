package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.FriendPO;

public interface IFriendAO {

	public List<FriendPO> getFriendByTeam(long teamId);
	
}
