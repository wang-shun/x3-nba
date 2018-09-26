package com.ftkj.manager.task.finish;

import com.ftkj.manager.task.ITaskFinish;
import com.ftkj.util.StringUtil;

/**
 * @author tim.huang
 * 2017年6月12日
 * 比赛，胜利并且对方等级比己方高
 * 9,11
 * 对方等级高于己方，分数限制，是否必须获胜
 */
public class TaskBetweenCheckFinish implements ITaskFinish {

	@Override
	public boolean finish(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		return valInt>=sourceInt;
	}

	@Override
	public boolean checkVal(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		int gradeIndex = Integer.parseInt(valStr);
		int[] souceInfo = StringUtil.toIntArray(sourceStr, StringUtil.DEFAULT_ST);
		if(gradeIndex < souceInfo[0] || gradeIndex > souceInfo[1]){
			return false;
		}
		return true;
	}

	
}
