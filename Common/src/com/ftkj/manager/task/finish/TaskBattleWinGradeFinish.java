package com.ftkj.manager.task.finish;

import com.ftkj.enums.EStatus;
import com.ftkj.manager.task.ITaskFinish;
import com.ftkj.util.StringUtil;

/**
 * @author tim.huang
 * 2017年6月12日
 * 比赛，胜利并且对方等级比己方高
 * 1，0，1
 * 对方等级高于己方，分数限制，是否必须获胜
 */
public class TaskBattleWinGradeFinish implements ITaskFinish {

	@Override
	public boolean finish(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		return valInt>=sourceInt;
	}

	@Override
	public boolean checkVal(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		int[] info = StringUtil.toIntArray(valStr, StringUtil.DEFAULT_ST);
		int[] souceInfo = StringUtil.toIntArray(sourceStr, StringUtil.DEFAULT_ST);
		if(souceInfo[0]==EStatus.True.getId()
				&& info[0]>=info[1]){//对方等级需要高于我
			return false;
		}
		if(info[2]<souceInfo[1]){//分数限制
			return false;
		}
		if(souceInfo[2]==EStatus.True.getId()
				&& info[2]<info[3]){//是否获胜
			return false;
		}
		if(souceInfo[3]!=-1 && souceInfo[3] != info[4] ){//比赛类型是否相符
			return false;
		}
		return true;
	}

	
}
