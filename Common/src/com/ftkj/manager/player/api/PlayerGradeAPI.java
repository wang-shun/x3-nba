package com.ftkj.manager.player.api;

import com.ftkj.cfg.PlayerGradeBean;
import com.ftkj.console.PlayerConsole;
import com.ftkj.manager.player.PlayerGrade;

/**
 * @author tim.huang
 * 2017年9月28日
 *
 */
public class PlayerGradeAPI {
	
	public static boolean levelUpPlayerGrade(PlayerGrade playerGrade,int addExp){ 
		boolean levelUp = false;
		int curLevel = playerGrade.getGrade();
		int exp = playerGrade.getExp() + addExp;
		PlayerGradeBean bean =null;
		for(int i = 0 ;i<200;i++){//最高连续升200级
			bean = PlayerConsole.getPlayerGradeBean(curLevel);
			if(bean == null) {
				break;
			}
			if(exp<bean.getNeedExp()) {
				break;
			}
			
			//扣除升级经验，增加等级
			exp-=bean.getNeedExp();
			curLevel++;
			levelUp = true;
		}
		playerGrade.setExp(exp);
		playerGrade.setGrade(curLevel);
		return levelUp;
	}
	
}
