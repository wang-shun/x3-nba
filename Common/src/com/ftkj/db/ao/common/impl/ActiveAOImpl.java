package com.ftkj.db.ao.common.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.common.IActiveAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.common.ActiveDAO;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.SystemActivePO;

public class ActiveAOImpl extends BaseAO implements IActiveAO {

	@IOC
	private ActiveDAO dao;
	
	@Override
	public ActiveBasePO getActiveShareData(int shardId, int atvId) {
		return dao.getActiveShareData(shardId, atvId);
	}
	
	@Override
	public ActiveBasePO getActiveBase(int shardId, int atvId, long teamId) {
		return dao.getActiveBase(shardId, atvId, teamId);
	}

	@Override
	public List<ActiveBasePO> queryActiveDataOrderByLastTime(int shardId, int atvId, int size) {
		return dao.queryActiveDataOrderByLastTime(shardId, atvId, size);
	}
	
	@Override
	public List<ActiveBasePO> queryActiveDataByValue(int shardId, int atvId, int value) {
		return dao.queryActiveDataByValue(shardId, atvId,  value);
	}
	
	@Override
	public List<ActiveBasePO> queryActiveDataOrderByRank(int shardId, int atvId, int size) {
		return dao.queryActiveDataOrderByRank(shardId, atvId, size);
	}

	@Override
	public List<SystemActivePO> getSystemActiveList(int shardId) {
		return dao.getSystemActiveList(shardId);
	}

	@Override
	public List<ActiveBasePO> getActiveBaseListBeforeDay(int shardId, int atvId, long teamId, int day) {
		return dao.getActiveBaseListBeforeDay(shardId, atvId, teamId, day);
	}
	
	@Override
	public boolean  createActiveDataTable() {
		return dao.createActiveDataTable();
	}

	@Override
	public void clearActiveData(int atvId) {
		dao.clearActiveData(atvId);
	}
	
	@Override
	public void clearActiveData(long teamId, int atvId) {
		dao.clearActiveData(teamId, atvId);
	}

  @Override
  public List<ActiveBasePO> queryLimitChanllenge(int title,int startIndex, int limit,int atv_id) {
    return dao.queryLimitChanllenge(title,startIndex, limit,atv_id);
  }
  
  public void addLimitChallenge(ActiveBasePO po) {
    dao.addLimitChallenge(po);
  }
  
  public void updateLimitChallenge(ActiveBasePO po) {
    dao.updateLimitChallenge(po);
  }

  @Override
  public ActiveBasePO getLimitChanllenge(long teamId,int atv_id) {
    return dao.getLimitChanllenge(teamId,atv_id);
  }
  
  public void deleteLimitChallenge(int atvId) {
     dao.deleteLimitChallenge(atvId);
  }
}
