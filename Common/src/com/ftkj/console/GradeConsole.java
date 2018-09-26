package com.ftkj.console;

import com.ftkj.cfg.TeamExpBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:球队等级控制
 * @author Jay
 * @time:2017年3月28日 上午11:21:07
 */
public class GradeConsole {

	/**
	 * 球队最大等级
	 */
	public static int MAX_LV;
	
	/**
	 * 等级经验配置
	 */
	private static Map<Integer, TeamExpBean> expMap;
	
	
	public static void init(List<TeamExpBean> expList) {
		// 最大等级
		MAX_LV = expList.stream().mapToInt(exp-> exp.getLv()).max().orElse(0);
		// 各等级经验
		expMap = expList.stream().collect(Collectors.toMap(TeamExpBean::getLv, bean-> bean));
	}
	
	public static TeamExpBean getTeamExpBean(int lv) {
		return expMap.get(lv);
	}
	
	// 根据总经验取配置等级
	public static TeamExpBean getTeamExpBeanByTotal(int total) {
		for(TeamExpBean t:expMap.values()) {
			if(total < t.getTotal()) {
				return t;
			}
		}
		// 最高等级
		return expMap.get(MAX_LV);
	}

	public static int getMaxLv() {
		return MAX_LV;
	}
}
