package com.ftkj.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数组类型的封装
 * 方便存储几天的数据类型，逗号字符串分割，想：1,1,1,1,1,1,1,1
 */
public class DBList implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Integer> list;
	/**
	 * 当前最大值下标，适用于不指定index加值的方式
	 */
	private int index = 0;
	
	/**
	 * 不指定大小，不初始化0值，只能往后新增值
	 */
	public DBList() {
		list = new ArrayList<Integer>();
	}
	
	public DBList(int size) {
		list = new ArrayList<Integer>();
		for(int i=0; i<size; i++) {
			list.add(0);
		}
	}

	public DBList(String strSplit) {
		strSplit = strSplit==null?"":strSplit;
		converStr(strSplit.split(","));
	}
	
	public DBList(String[] valSet) {
		converStr(valSet);
	}
	
	private void converStr(String[] split) {
		list = new ArrayList<Integer>();
		for(String s : split) {
			if(!s.equals("")) {
				list.add(Integer.parseInt(s));
				index++;
			}
		}
	}
	/**
	 * 往下标里放值，下标会往后加1
	 * @param value
	 */
	public void addValue(int value) {
		this.list.add(value);
		this.index++;
	}
	
	public void setValueAdd(int index, int value) {
		this.list.set(index, getValue(index) + value);
	}
	
	public void setValue(int index, int value) {
		this.list.set(index, value);
	}
	
	public int getValue(int index) {
		return this.list.get(index);
	}
	
	public String getValueStr() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<this.list.size(); i++) {
			sb.append(this.getValue(i)).append(",");
		}
		if(sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	/**
	 * 值汇总
	 * @return
	 */
	public int count() {
		int count = 0;
		for(int i : list) {
			count += i;
		}
		return count;
	}
	
	/**
	 * 是否包含值
	 * @param value
	 * @return
	 */
	public boolean containsValue(int value) {
		return this.list.contains(value);
	}

	public List<Integer> getList() {
		return list;
	}

	
	
}
