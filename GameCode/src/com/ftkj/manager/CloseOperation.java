package com.ftkj.manager;

/**
 * @author tim.huang
 * 2015年12月30日
 * 停服操作
 */
public interface CloseOperation {
	public void close()throws Exception;

	default int getOrder(){
		return 0;
	}
}
