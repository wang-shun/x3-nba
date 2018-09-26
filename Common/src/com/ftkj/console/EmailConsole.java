package com.ftkj.console;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.ftkj.cfg.EmailViewBean;

public class EmailConsole {

	private static Map<Integer,EmailViewBean> viewMap;
	
	/**
	 * 初始化
	 */
	public static void init(List<EmailViewBean> list) {
		viewMap = list.stream().collect(Collectors.toMap(EmailViewBean::getId, (e) -> e));
	}
	
	public static EmailViewBean getEmailView(int id) {
		return viewMap.get(id);
	}
	
}
