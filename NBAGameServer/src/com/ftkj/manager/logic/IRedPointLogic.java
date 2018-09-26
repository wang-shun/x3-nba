package com.ftkj.manager.logic;

import java.util.Collections;
import java.util.List;

import com.ftkj.event.param.RedPointParam;

/**
 * 红点提示逻辑实现本类
 * @author Jay
 * @time:2017年11月12日 下午3:25:04
 */
public interface IRedPointLogic {
	
	/**
	 * 红点逻辑
	 * @param teamId
	 * @return
	 */
	public abstract RedPointParam redPointLogic(long teamId);
	public default List<RedPointParam> redPoints(long teamId){
		return Collections.emptyList();
	}
	
}
