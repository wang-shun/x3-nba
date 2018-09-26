package com.ftkj.manager.task.finish;

import com.ftkj.enums.EStatus;
import com.ftkj.manager.task.ITaskFinish;
import com.ftkj.util.StringUtil;

/**
 * @author tim.huang
 * 2017年6月12日
 * 比赛，胜利并且对方等级比己方高
 * 9,11
 * 对方等级高于己方，分数限制，是否必须获胜
 */
public class TaskBetweenCheckFinish2 implements ITaskFinish {

	@Override
	public boolean finish(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		return valInt>=sourceInt;
	}

	@Override
	public boolean checkVal(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		int[] gradeIndex = StringUtil.toIntArray(valStr, StringUtil.DEFAULT_ST);
		int[] souceInfo = StringUtil.toIntArray(sourceStr, StringUtil.DEFAULT_ST);
		if(gradeIndex[0]!=souceInfo[2]){
			return false;
		}
		
		if(gradeIndex[1] < souceInfo[0] || gradeIndex[1] > souceInfo[1]){
			return false;
		}
		return true;
	}

	
}
