package com.ftkj.util;

public class CastUtil {

	/**
	 * 字符串转int，空默认0
	 * @param value
	 * @return
	 */
	public static int StringCastInt(String value) {
		if(value == null || value.trim().equals("")) return 0;
		return Integer.valueOf(value);
	}
	
}
