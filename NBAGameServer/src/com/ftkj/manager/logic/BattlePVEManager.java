package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.BattleManager;
import com.ftkj.server.ManagerOrder;

/**
 * @author tim.huang
 * 2017年3月2日
 * PVE比赛管理
 */
public class BattlePVEManager extends BaseManager{

	@IOC
	private TeamManager teamManager;
	
	@IOC
	private BattleManager battleManager;
	@IOC
	private LocalBattleManager manager;
	
	private BattleRoundReport round;
	
	
	public BattlePVEManager() {
		super();
	}
	

	@Override
	public void instanceAfter() {
		
		
	}


	@Override
	public void initConfig() {
		
		
	}

	@Override
	public int getOrder() {
		return ManagerOrder.PVEBattle.getOrder();
	}

}
