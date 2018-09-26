package com.ftkj.event.param;

import com.ftkj.enums.battle.EBattleType;

/**
 * 比赛参数
 * @author Jay
 * @time:2018年1月16日 下午5:25:10
 */
public class PKParam {
	
	public EBattleType type;
	public long teamId;
	public boolean isWin;
	
	public PKParam(EBattleType type, long teamId, boolean isWin) {
		super();
		this.type = type;
		this.teamId = teamId;
		this.isWin = isWin;
	}

}
