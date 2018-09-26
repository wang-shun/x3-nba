package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.db.domain.BeSignPlayerPO;

public interface IBeSignPlayerAO {

	List<BeSignPlayerPO> getBeSignPlayerList(long teamId);

}
