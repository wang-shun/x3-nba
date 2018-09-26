package com.ftkj.manager.task.finish;

import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EStatus;
import com.ftkj.manager.task.ITaskFinish;
import com.ftkj.util.StringUtil;
import com.google.common.primitives.Ints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tim.huang
 * 2017年6月12日
 * 球员级别限制
 * D-,2500,1
 * 球员等级，身价限制，是否底薪
 */
public class TaskPlayerGradeFinish implements ITaskFinish {
    private static final Logger log = LoggerFactory.getLogger(TaskPlayerGradeFinish.class);
	@Override
	public boolean finish(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		return valInt>=sourceInt;
	}

	@Override
	public boolean checkVal(int sourceInt, String sourceStr, int valInt,
			String valStr) {
		String [] info = StringUtil.toStringArray(valStr, StringUtil.DEFAULT_ST);
		String [] sourceInfo = StringUtil.toStringArray(sourceStr, StringUtil.DEFAULT_ST);
		if(info.length<3 || sourceInfo.length<3) {
			log.error("有个任务配置配错了[{}]-[{}]",sourceStr,valStr);
			return true;
		}
		//判断级别是否符合需求
		EPlayerGrade limitGrade = EPlayerGrade.convertByName(sourceInfo[0]);
		if(limitGrade == null) {
			return true;
		}
		EPlayerGrade valGrade = EPlayerGrade.convertByName(info[0]);
		if(valGrade == null) {
			return true;
		}
		if(valGrade.ordinal()<limitGrade.ordinal()) {
			return false;
		} 
		//判断身价是否在需求以下
		if(Ints.tryParse(sourceInfo[1])>Ints.tryParse(info[1])) {
			return false;
		}
		int basePrice = Ints.tryParse(sourceInfo[2]);
		int basePrice2 = Ints.tryParse(info[2]);
		//判断是否需要底薪
		if(basePrice == EStatus.True.getId() && basePrice2 == EStatus.False.getId()) {
			return false;
		}
		return true;
	}

	
	

}
