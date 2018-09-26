package com.ftkj.util;

import com.google.gson.Gson;

/**
 * @author tim.huang
 * 2016年11月7日
 *
 */
public class JsonUtil {	
	
	private static Gson g = new Gson();
	
	public static String toJson(Object t){
		return g.toJson(t);
	}
	
	public static <T> T toObj(String son,Class<? extends T> c){
		return g.fromJson(son, c);
	}
	
}
