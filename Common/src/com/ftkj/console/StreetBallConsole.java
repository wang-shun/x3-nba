package com.ftkj.console;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.cfg.StreetBallBean;
import com.ftkj.enums.EConfigKey;

/**
 * 街球控制
 * @author Jay
 * @time:2017年7月17日 下午4:35:01
 */
public class StreetBallConsole {
	
	/**
	 * 每天最大挑战次数
	 */
	public static int Every_Day_Count = 2;
	public static int Win_least_Score = 30;
	private static Map<Integer, StreetBallBean> strBallMaps;
	
	
	public static void init(List<StreetBallBean> list) {
		strBallMaps = list.stream().collect(Collectors.toMap(StreetBallBean::getId, b-> b));
		Every_Day_Count = ConfigConsole.getIntVal(EConfigKey.StreetBall_EveryDay_Num);
		Win_least_Score = ConfigConsole.getIntVal(EConfigKey.StreetBall_Win_Point);
	}
	
	public static StreetBallBean getStrBallBean(int id) {
		return strBallMaps.get(id);
	}

}
