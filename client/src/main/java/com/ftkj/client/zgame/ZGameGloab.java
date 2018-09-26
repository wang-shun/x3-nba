package com.ftkj.client.zgame;

import java.util.Map;

import com.ftkj.proto.GameLoadPB;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年8月26日
 * 机器人全局数据
 */
public class ZGameGloab {
	
	//登录数据
	public static Map<Long,GameLoadPB.GameLoadDataMain> loadDataMap = Maps.newConcurrentMap();
	
	
	
	
}
