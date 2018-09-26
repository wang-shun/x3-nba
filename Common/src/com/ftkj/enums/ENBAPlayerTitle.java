package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年3月15日
 * NBA球员称号
 */
public enum ENBAPlayerTitle {
	全明星(2),
	周最佳(1),
	无名小卒(0)
	;
	
	private int tid;
	ENBAPlayerTitle(int tid){
		this.tid = tid;
	}
	public int getTid() {
		return tid;
	}

	//通过ID，取对应的战术枚举
	public static final Map<Integer,ENBAPlayerTitle> playerTitleMap = new HashMap<Integer, ENBAPlayerTitle>();
	static{
		for(ENBAPlayerTitle et : ENBAPlayerTitle.values()){
			playerTitleMap.put(et.getTid(), et);
		}
	}
	
	public static ENBAPlayerTitle getEPlayerTitle(int id){
		return playerTitleMap.get(id);
	}
}
