package com.ftkj.manager.task.finish;

import com.ftkj.manager.task.ITaskFinish;

/**
 * @author tim.huang
 * 2017年6月9日
 * 根据目标判断是否达成
 */
public class TaskTagFinish implements ITaskFinish {

	@Override
	public boolean finish(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		return valInt == sourceInt  && sourceStr.equals(valStr);
	}

	@Override
	public boolean checkVal(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		return sourceStr==null || "".equals(sourceStr)?true:sourceStr.equals(valStr);
	}
	
	

}
