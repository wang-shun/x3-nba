package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.PropPO;

/**
 * @author tim.huang
 * 2017年3月13日
 *
 */
public interface IPropAO {
	
	public List<PropPO> getPropList(long teamId);
	
}
