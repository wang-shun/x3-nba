package com.ftkj.db.ao.common;

import java.util.List;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.SystemActivePO;

public interface IActiveAO {

	public List<SystemActivePO> getSystemActiveList(int shardId);
	
	public ActiveBasePO getActiveShareData(int shardId, int atvId);
	
	public ActiveBasePO getActiveBase(int shardId, int atvId, long teamId);
	
	public List<ActiveBasePO> getActiveBaseListBeforeDay(int shardId, int atvId, long teamId, int day);
	
	public List<ActiveBasePO> queryActiveDataOrderByLastTime(int shardId, int atvId, int size);
	
	public List<ActiveBasePO> queryActiveDataOrderByRank(int shardId, int atvId, int size);
	
	public List<ActiveBasePO> queryActiveDataByValue(int shardId, int atvId, int value);

	public boolean createActiveDataTable();
	
	public void clearActiveData(int atvId);
	
	public void clearActiveData(long teamId, int atvId);

	public List<ActiveBasePO> queryLimitChanllenge(int title,int startIndex,int limit,int atvId);
	
	public ActiveBasePO getLimitChanllenge(long teamId,int atvId);
	
	public void addLimitChallenge(ActiveBasePO po);
	
	public void updateLimitChallenge(ActiveBasePO po);
	
	public void deleteLimitChallenge(int atvId);
	
}
