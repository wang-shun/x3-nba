package com.ftkj.db.ao.logic;

import java.util.Date;
import java.util.List;

import com.ftkj.db.domain.PlayerAvgInfo;
import com.ftkj.db.domain.PlayerExchangePO;
import com.ftkj.db.domain.PlayerPO;
import com.ftkj.manager.player.PlayerMinPrice;
import com.ftkj.manager.player.PlayerTalent;

/**
 * @author tim.huang
 * 2017年3月8日
 *
 */
public interface IPlayerAO {
	public List<PlayerPO> getPlayerList(long teamId);
	public List<PlayerMinPrice> getPlayerMinPriceList();
	public List<PlayerAvgInfo> getPlayerAvgList(long teamId);
	public List<PlayerTalent> getPlayerTalentList(long teamId);
	public List<PlayerExchangePO> getPlayerExchangeList(int beforeDay);
	public List<PlayerExchangePO> getPlayerExchangeListByDate(Date date);	
}
