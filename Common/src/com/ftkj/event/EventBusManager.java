package com.ftkj.event;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import com.ftkj.util.ThreadPoolUtil;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

public class EventBusManager {

	public static Map<EEventType, EventBus> eventBusMap;
	public static ScheduledExecutorService executorService;
	
	static {
		// 根据枚举来初始化，自己订阅
		eventBusMap = Maps.newHashMap();
		executorService = ThreadPoolUtil.newScheduledPool("异步事件线程", 2);
		//
		for(EEventType type : EEventType.values()) {
			// AsyncEventBus 异步
			if(type.isAsync) {
				eventBusMap.put(type, new AsyncEventBus(executorService));
			}else {
				eventBusMap.put(type, new EventBus());
			}
		}
	}
	
	/**
	 * 如果想要支持多个参数，则需要
	 * @param object
	 */
	public static void post(EEventType type, Object object) {
		if(eventBusMap.containsKey(type)) {
			eventBusMap.get(type).post(object);
		}
	}
	
	/**
	 * 取得事件中心，用来注册
	 * @param type
	 * @return
	 */
	public static void register(EEventType type, Object object) {
		if(eventBusMap.containsKey(type)) {
			eventBusMap.get(type).register(object);
		}
	}
	
}
