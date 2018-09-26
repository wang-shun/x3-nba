package com.ftkj.db.ao.logic.impl;

import java.util.List;
import java.util.Map;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IEmailAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.EmailDAO;
import com.ftkj.db.domain.EmailPO;

public class EmailAOImpl extends BaseAO implements IEmailAO {

	@IOC
	private EmailDAO emailDAO;
	
	public List<EmailPO> getTeamEmailList(long teamId) {
		return emailDAO.getTeamEmailList(teamId);
	}
	
	public Map<Long, Integer> getTeamEmailSeqMap() {
		return emailDAO.getTeamEmailSeqMap();
	}

	@Override
	public List<Integer> getTeamEmailSendList() {
		return emailDAO.getTeamEmailSendList();
	}
}
