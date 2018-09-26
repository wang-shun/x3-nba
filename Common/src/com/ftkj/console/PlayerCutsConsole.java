package com.ftkj.console;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.cfg.PlayerCutsBean;

/**
 * 降薪球员配置数据控制类.
 * 
 * @author mr.lei
 * @time 2018-8-6 10:24:39
 */
public class PlayerCutsConsole {

	/**降薪球员配置数据,key=球员的配置Id, value=配置数据*/
	private static Map<Integer, PlayerCutsBean> playerCutsBeanMap;

	public static void init(List<PlayerCutsBean> list) {
		playerCutsBeanMap = list.stream().collect(Collectors.toMap(PlayerCutsBean::getPlayerId, bean -> bean));
	}

	public static Map<Integer, PlayerCutsBean> getPlayerCutsBeanMap() {
		return playerCutsBeanMap;
	}
	
	
}
