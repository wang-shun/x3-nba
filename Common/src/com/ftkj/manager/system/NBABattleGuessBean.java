package com.ftkj.manager.system;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Lists;

/**
 * @author tim.huang 2018年3月20日 比赛竞猜
 */
public class NBABattleGuessBean extends ExcelBean {

	private int gameId;
	private DateTime startTime;
	private DateTime endTime;
	private List<PropSimple> giftList;
	private DateTime battleStartTime;
	
	@Override
	public void initExec(RowData row) {
		this.startTime = DateTimeUtil.getDateTime(row.get("start"));
		this.endTime = DateTimeUtil.getDateTime(row.get("end"));
		this.battleStartTime = DateTimeUtil.getDateTime(row.get("battleTime"));
		this.giftList = PropSimple.getPropBeanByStringNotConfig(row.get("gifts"));
		this.gameId = Integer.parseInt(row.get("gameIds"));
	}

	public DateTime getBattleStartTime() {
		return battleStartTime;
	}

	public void setBattleStartTime(DateTime battleStartTime) {
		this.battleStartTime = battleStartTime;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public List<PropSimple> getGiftList() {
		return giftList;
	}

	public void setGiftList(List<PropSimple> giftList) {
		this.giftList = giftList;
	}

}
