package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IFriendAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.FriendDAO;
import com.ftkj.db.domain.FriendPO;

public class FriendAOImpl extends BaseAO implements IFriendAO {

	@IOC
	private FriendDAO dao;
	
	@Override
	public List<FriendPO> getFriendByTeam(long teamId) {
		return dao.getFriendByTeam(teamId);
	}

}
