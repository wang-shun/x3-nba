package com.ftkj.db.ao.logic;

import com.ftkj.db.domain.SignPO;

public interface ISignAO {

	public SignPO getSignMonth(long teamId);
	
	public SignPO getSignPeriod(long teamId);
	
}
