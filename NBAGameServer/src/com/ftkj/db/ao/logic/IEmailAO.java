package com.ftkj.db.ao.logic;

import java.util.List;
import java.util.Map;

import com.ftkj.db.domain.EmailPO;

public interface IEmailAO {

	public List<EmailPO> getTeamEmailList(long teamId);
	
	public Map<Long, Integer> getTeamEmailSeqMap();
	
	public List<Integer> getTeamEmailSendList();
}
