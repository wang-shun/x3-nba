package com.main.test;

import java.util.ArrayList;
import java.util.List;

import com.ftkj.console.CM;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.EquiConsole;
import com.ftkj.console.GameConsole;
import com.ftkj.db.domain.EquiPO;
import com.ftkj.manager.equi.bean.Equi;


public class EquiCapTest {
	
	/**
	 * 装备攻防模块测试
	 * @param args
	 */
	public static void main(String[] args) {
		//CM.init(false);
		ConfigConsole.init(CM.GLOBAL_CONFIGS);
		//PlayerConsole.init(CM.playerBeanList);
		//EquiConsole.init(CM.EQUI_LIST, CM.EQUI_UP_STRLV_LIST, CM.EQUI_UP_LV_LIST, CM.EQUI_UP_QUA_LIST, CM.EQUI_CLOTHES_QUA_LIST);
		List<Equi> equiList = new ArrayList<Equi>();
		//
		EquiConsole.EQUI_INIT_LIST.stream().forEach(equId-> equiList.add(instanceEqui(0, equId, 6583)));
		//
//		EquiCap eCap = new EquiCap(6583, equiList);
//		System.err.println(eCap.getAbility());
//		System.err.println(eCap.getCapMap());
	}

	private static Equi instanceEqui(int seqId, int equId, int playerId) {
		EquiPO po = new EquiPO();
		po.setId(seqId);
		po.setType(EquiConsole.getEquiTypeByEid(equId));
		po.setTeamId(0);
		po.setEquId(equId);
		po.setStrLv(5);
		po.setPlayerId(playerId);
		po.setEndTime(GameConsole.Max_Date);
		// 
		Equi equi = new Equi(po);
		return equi;
	}
}
