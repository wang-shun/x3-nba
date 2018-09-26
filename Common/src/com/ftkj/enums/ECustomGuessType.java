package com.ftkj.enums;

import com.ftkj.manager.custom.guess.CustomGuessDF;
import com.ftkj.manager.custom.guess.CustomGuessFG;
import com.ftkj.manager.custom.guess.CustomGuessFG2;
import com.ftkj.manager.custom.guess.CustomGuessFQ;
import com.ftkj.manager.custom.guess.CustomGuessLB;
import com.ftkj.manager.custom.guess.CustomGuessZG;
import com.ftkj.manager.custom.guess.ICustomGuessBattle;

/**
 * @author tim.huang
 * 2017年8月2日
 * 竞猜类型
 */
public enum ECustomGuessType {
	双方得分总和(new CustomGuessDF()),
	双方篮板总和(new CustomGuessLB()),
	双方助攻总和(new CustomGuessZG()),
	双方犯规单双(new CustomGuessFG()),
	双方犯规总次数(new CustomGuessFG2()),
//	比赛最后一次得分(new CustomGuessDF()),
	双方罚球得分(new CustomGuessFQ()),
//	参与竞猜人数总和(new CustomGuessDF()),
	双方胜负(new CustomGuessDF());
	
	private ICustomGuessBattle battle;
	
	ECustomGuessType(ICustomGuessBattle battle){
		this.battle = battle;
	}
	
	public ICustomGuessBattle getBattle(){
		return this.battle;
	}
	
	
}
