package com.ftkj.manager.battle.handle;

/**
 * @author tim.huang
 * 2017年4月25日
 * 即时PK赛API
 */
public class BattleCommonAPI {
	
	/**
	 * 取即时PK赛，玩家等级所在分组
	 * @param teamLeveL
	 * @return
	 */
	public static int getBattleRoomGroup(int teamLeveL){
		if(teamLeveL>=1 && teamLeveL<=20){
			return 1;
		}else if(teamLeveL>=21 && teamLeveL<=30){
			return 2;
		}else if(teamLeveL>=31 && teamLeveL<=40){
			return 3;
		}else if(teamLeveL>=41 && teamLeveL<=50){
			return 4;
		}else if(teamLeveL>=51 && teamLeveL<=60){
			return 5;
		}else if(teamLeveL>=61 && teamLeveL<=70){
			return 6;
		}else if(teamLeveL>=71 && teamLeveL<=80){
			return 7;
		}else if(teamLeveL>=81 && teamLeveL<=100){
			return 8;
		}  
		return 1;
		
		
	}
	
	
	
}
