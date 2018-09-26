package com.ftkj.tool.redis;

import java.io.Serializable;
import java.util.List;

public class ListObj<T extends Serializable> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 815201461255709135L;
	
	private List<T> list;
	
	public ListObj(List<T> list){
		this.list = list;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
	

}
