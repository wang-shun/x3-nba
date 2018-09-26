package com.ftkj.manager.task.finish;

import com.ftkj.manager.task.ITaskFinish;

/**
 * @author tim.huang
 * 2017年6月9日
 * 根据数据是否在目标范围以上达成
 */
public class TaskTweenFinish implements ITaskFinish {

	@Override
	public boolean finish(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		return valInt>=sourceInt;
	}

}
