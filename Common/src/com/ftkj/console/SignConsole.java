package com.ftkj.console;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.cfg.SignMonthBean;
import com.ftkj.cfg.SignPeriodBean;
import com.ftkj.enums.EConfigKey;

/**
 * 签到控制台
 * @author Jay
 * @time:2017年8月26日 上午11:26:35
 */
public class SignConsole {

	private static Map<Integer, List<SignMonthBean>> signMonthMap;
	private static Map<Integer, List<SignPeriodBean>> signPeriodMap;
	public static int PeriodSize = 3;
	public static int Sign_Patch_Fk = 10;
	
	public static void init() {
		signMonthMap = CM.signMonthList.stream().collect(Collectors.groupingBy(SignMonthBean::getMonth, Collectors.toList()));
		signPeriodMap = CM.signPeriodList.stream().collect(Collectors.groupingBy(SignPeriodBean::getPeriod, Collectors.toList()));
		PeriodSize = signPeriodMap.size();
		Sign_Patch_Fk = ConfigConsole.getIntVal(EConfigKey.Sign_Patch_Fk);
	}
	
	/**
	 * 取每月签到奖励
	 * @param month 1开始
	 * @param day 天数 1~31
	 * @return
	 */
	public static SignMonthBean getSignMonthBean(int month, int day) {
		return signMonthMap.get(month).get(day - 1);
	}
	
	/**
	 * 取7天累计签到，周期性
	 * @param period 0~2 开始
	 * @param day 1开始
	 * @return
	 */
	public static SignPeriodBean getSignPeriodBean(int period, int day) {
		return signPeriodMap.get(period + 1).get(day - 1);
	}
	
	/**
	 * 7天签到的大小
	 * @param period
	 * @return
	 */
	public static int getSignPeriodSize(int period) {
		int index = period + 1;
		if(signPeriodMap.containsKey(index)) {
			return signPeriodMap.get(index).size();
		}
		return 0;
	}
}
