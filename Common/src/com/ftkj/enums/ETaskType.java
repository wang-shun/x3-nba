package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

public enum ETaskType {
	
	主线任务(1, true),
	日常任务(2, false),
	活动任务(3, false),
	成就任务(4, true),
	
	
	所有任务(0, false),  // 取DB任务时，不做过滤
	;
	
	public static final Map<Integer,ETaskType> taskTypeEnumMap = new HashMap<Integer, ETaskType>();
	static{
		for(ETaskType et : ETaskType.values()){
			taskTypeEnumMap.put(et.getType(), et);
		}
	}
	
	public static ETaskType getETaskType(int key){
		ETaskType title = taskTypeEnumMap.get(key);
		return title;
	}
	
	private int type;
	private boolean saveDb;
	
	private ETaskType(int type, boolean save) {
		this.type = type;
		this.saveDb = save;
	}
	
	public int getType() {
		return this.type;
	}

	public boolean isSaveDb() {
		return saveDb;
	}
	
}
