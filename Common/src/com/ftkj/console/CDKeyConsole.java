package com.ftkj.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.ftkj.console.CDKeyConsole.ConverCodeBeanList;
import com.ftkj.manager.cdkey.ConverCodeBean;

public class CDKeyConsole {
	private static Map<String, ConverCodeBeanList> codeBeanMap;

	public static void init(){
		codeBeanMap  = new HashMap<>();
		if(CM.converCodeBeanList!=null) {
			CM.converCodeBeanList.stream().forEach(cb->
					codeBeanMap.computeIfAbsent(cb.getId(), (k)->new ConverCodeBeanList()).add(cb)
				);
		}
	}

	public static ConverCodeBean getConverCodeBean(String id, String plat) {
		return codeBeanMap.get(id).getCode(plat);
	}
	
	static class ConverCodeBeanList {
		private Map<String,ConverCodeBean> cMap;

		public ConverCodeBeanList() {
			cMap = new HashMap<>();
		}
		
		public void add(ConverCodeBean code){
			cMap.put(code.getPlat(), code);
		}
		
		public ConverCodeBean getCode(String plat){
			return cMap.get(plat);
		}

	}

}
