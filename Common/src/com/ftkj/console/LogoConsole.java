package com.ftkj.console;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.enums.EConfigKey;
import com.ftkj.manager.logo.cfg.LogoLvBean;
import com.ftkj.manager.logo.cfg.LogoQuaBean;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Maps;

/**
 * 
 * @Description:荣誉头像控制台
 * @author Jay
 * @time:2017年3月15日 下午7:46:20
 */
public class LogoConsole {
	
	/**
	 * 单次合成消耗碎片数量
	 */
	public static int DEBRIS_NUM;
	/**
	 * 保底20次出橙色
	 */
	public static int SAFE_COUNT;
	/**
	 * 头像碎片单价（球券）
	 */
	public static int DEBRIS_PRICE;
	/**
	 * 每次碎片合成累计的幸运值
	 */
	public static int LUCKY_EACH;
	/**
	 * 幸运值100必出品质
	 */
	public static int LUCKY_QUALITY;
	/**
	 * 头像转移卡道具ID
	 */
	public static int TRAN_TID;
	/**
	 * 头像碎片道具ID
	 */
	public static int DEBRIS_TID;
	/**
	 * 最大等级
	 */
	public static int MAX_LV; 
	
	/**
	 * 最大品质
	 */
	public static int MAX_QUA;
	
	/**
	 * 头像荣誉；  (等级：配置)
	 */
	private static Map<Integer, LogoLvBean> logoLvBeanMap;
	/**
	 * 荣耀点道具ID
	 */
	public static int QuaProp = 1204;
	
	/**
	 * 球员的头像品质配置, (品质：配置)
	 */
	private static Map<Integer, LogoQuaBean> logoQuaBeanMap;
	
	/**
	 * 可以有头像的球员ID列表
	 */
	private static Map<Integer, String> playerMap;
	
	/**
	 * 碎片合成头像概率
	 */
	public static Map<Integer, Integer> quaRateMap;
	
	
	public static void init(List<LogoLvBean> logoLvList, List<LogoQuaBean> logoQuaList, Map<Integer, String> playerListMap) {
		// 
		DEBRIS_NUM = ConfigConsole.getIntVal(EConfigKey.DEBRIS_NUM);
		SAFE_COUNT = ConfigConsole.getIntVal(EConfigKey.SAFE_COUNT);
		DEBRIS_PRICE = ConfigConsole.getIntVal(EConfigKey.DEBRIS_PRICE);
		LUCKY_EACH = ConfigConsole.getIntVal(EConfigKey.LUCKY_EACH);
		LUCKY_QUALITY = ConfigConsole.getIntVal(EConfigKey.LUCKY_QUALITY);
		TRAN_TID = ConfigConsole.getIntVal(EConfigKey.TRAN_TID);
		DEBRIS_TID = ConfigConsole.getIntVal(EConfigKey.DEBRIS_TID);
		//
		Map<Integer, LogoLvBean> map2 = logoLvList.stream().collect(Collectors.toMap(LogoLvBean::getLv, bean-> bean));
		logoLvBeanMap = map2;
		//
		Map<Integer, LogoQuaBean> map1 = logoQuaList.stream().collect(Collectors.toMap(LogoQuaBean::getQuality, bean -> bean));
		logoQuaBeanMap = map1;
		// 
		playerMap = playerListMap;
		MAX_LV = logoLvBeanMap.size();
		MAX_QUA = logoQuaBeanMap.size();
		// 碎片合成概率
		quaRateMap = LogoConsole.getLogoQuaBeanList().stream().collect(
				Collectors.toMap(LogoQuaBean::getQuality, bean-> Float.valueOf(bean.getCombRate() * 1000).intValue()));
		
	}
	
	/**
	 * 是否有头像的球员
	 * @param playerId
	 * @return
	 */
	public static boolean checkPlayer(int playerId) {
		return playerMap.containsKey(playerId);
	}
	
	public static LogoLvBean getLogoLv(int lv) {
		return logoLvBeanMap.get(lv);
	}
	
	public static LogoQuaBean getLogoQua(int qua) {
		return logoQuaBeanMap.get(qua);
	}
	
	public static Collection<LogoQuaBean> getLogoQuaBeanList() {
		return logoQuaBeanMap.values();
	}
	
	/**
	 * 碎片合成头像随机品质
	 * @return
	 */
	public static int randQuality(Map<Integer, Integer> adjustMap) {
		// 随机品质,千分比
		Map<Integer, Integer> rateMap = Maps.newHashMap(quaRateMap);
		return RandomUtil.randMap(rateMap, adjustMap);
	}
	
}
