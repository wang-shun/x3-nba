package com.ftkj.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

	/**
	 * 创建一个填充0的数组
	 * @param size
	 * @return
	 */
	public static List<Integer> createList(int size) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<size; i++) {
			list.add(0);
		}
		return list;
	}
	
}
