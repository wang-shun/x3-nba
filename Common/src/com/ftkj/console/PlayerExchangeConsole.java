package com.ftkj.console;

import java.util.List;
import java.util.Map;

import com.ftkj.cfg.PlayerShopBean;
import com.ftkj.enums.EConfigKey;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import com.google.common.collect.Maps;

public class PlayerExchangeConsole {

	/**
	 * 刷新数量
	 */
	public static int VIEW_PLAYER_NUM = 4;
	

	/**
	 * 回收消耗道具ID
	 */
	public static int Recycling_PROP_ID = 1140;
	
	/**
	 * 普通兑换消耗道具ID
	 */
	private static int COMMON_PROP_ID = 1140;
	/**
	 * 高级兑换消耗ID
	 */
	private static int ADVANCE_PROP_ID = 1141;
	/**
	 * 普通刷新消耗
	 */
	private static PropSimple Normal_Refresh_Prop;
	/**
	 * 高级刷新消耗
	 */
	private static PropSimple Advanced_Refresh_Prop;

	/**
	 * 等级：回收数量
	 */
	private static Map<String, Integer> recoveryMap;
	/**
	 * 等级：兑换需要数量
	 */
	private static Map<String, Integer> cashMap; 
	
	/**
	 * 高级刷新降价的掉落
	 */
	public static DropBean advancedLowPriceDrop;
	
	/**
	 * 刷新类型
	 * @author Jay
	 * @time:2018年3月3日 下午4:32:58
	 */
	public enum ERefreshType {
			普通刷新(1),
			高级刷新(2),
			;
			int type;
			private ERefreshType(int type) {
				this.type = type;
			}
			public int getType() {
				return type;
			}
	}
	
	public static void init(List<PlayerShopBean> playerExchangeCfg) {
		String[] drops = getDropsConfig();
		VIEW_PLAYER_NUM = drops.length;
		// 兑换配置
		recoveryMap = Maps.newHashMap();
		cashMap = Maps.newHashMap();
		playerExchangeCfg.stream().forEach(s-> {
			recoveryMap.put(s.getGrade(), s.getRecovery());
			cashMap.put(s.getGrade(), s.getCash());
		});
		//
		advancedLowPriceDrop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Advanced_Refresh_Player_Price_Drop));
	}
	
	public static String[] getDropsConfig() {
		String[] drops = ConfigConsole.getVal(EConfigKey.Scout_Exchange_Drops).split(",");
		Normal_Refresh_Prop = PropSimple.getPropSimpleByString(ConfigConsole.getVal(EConfigKey.Normal_Refresh_Player_Need));
		Advanced_Refresh_Prop = PropSimple.getPropSimpleByString(ConfigConsole.getVal(EConfigKey.Advanced_Refresh_Player_Need));
		COMMON_PROP_ID = ConfigConsole.getIntVal(EConfigKey.Scout_Exchange_Need);
		Recycling_PROP_ID = ConfigConsole.getIntVal(EConfigKey.Player_Recycling);
		return drops;
	}
	
	/**
	 * 兑换消耗
	 * @param type
	 * @return
	 */
	public static int getPropIdByType(int type) {
		if(type == ERefreshType.普通刷新.type) {
			return COMMON_PROP_ID;
		}
		return ADVANCE_PROP_ID;
	}
	
	/**
	 * 刷新消耗
	 * @param type
	 * @return
	 */
	public static PropSimple getRefreshPropNeed(int type) {
		if(type == ERefreshType.普通刷新.type) {
			return Normal_Refresh_Prop;
		}
		return Advanced_Refresh_Prop;
	}
	
	/**
	 * 根据球员等级取回收数量
	 * @param grade
	 * @return
	 */
	public static int getRecoveryNum(String grade) {
		if(!recoveryMap.containsKey(grade)) {
			return 1;
		}
		return recoveryMap.get(grade);
	}
	
	/**
	 * 根据球员等级取兑换数量
	 * @param grade
	 * @return
	 */
	public static int getCashNum(String grade) {
		if(!cashMap.containsKey(grade)) {
			return 1;
		}
		return cashMap.get(grade);
	}
}
