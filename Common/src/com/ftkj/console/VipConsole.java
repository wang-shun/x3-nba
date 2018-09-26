package com.ftkj.console;

import com.ftkj.cfg.VipBean;

import java.util.Map;
import java.util.stream.Collectors;

public class VipConsole {

	public static Map<Integer, VipBean> vipMap;
	private static int maxLev;
	/**
	 * VIP初始化
	 */
	public static void init() {
		//System.err.println(CM.vipCfgList);
		vipMap = CM.vipCfgList.stream().collect(Collectors.toMap(VipBean::getLevel, (b)-> b));
		maxLev = vipMap.keySet().stream().max(Integer::compareTo).orElse(0);
	}

	/**
	 * 取VIP等级配置
	 * @param level
	 * @return
	 */
	public static VipBean getVipLevelBean(int level) {
		return vipMap.get(level);
	}
	
	/**
	 * 根据充值得到对应的VIP等级
	 * @param addMoney
	 * @return
	 */
	public static int getLevelByAddMoney(int addMoney) {
		int level = 0;
		for(int i=1; i < vipMap.size()+1; i++) {
			VipBean bean = vipMap.get(i);
			if(addMoney < bean.getMoney()) {
				break;
			}
			level = bean.getLevel();
		}
		return level;
	}

	public static Map<Integer, VipBean> getVipMap() {
		return vipMap;
	}

	public static int getMaxLev(){
		return maxLev;
	}
}
