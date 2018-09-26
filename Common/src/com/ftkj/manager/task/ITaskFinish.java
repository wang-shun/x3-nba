package com.ftkj.manager.task;

/**
 * @author tim.huang
 * 2017年6月7日
 * 任务完成判断
 */
public interface ITaskFinish {
    
   // void accept(Task task);
	
	boolean finish(int sourceInt,String sourceStr,int valInt,String valStr);
	
	default boolean checkVal(int sourceInt, String sourceStr,int valInt,String valStr){
		return true;
	}
	
}
