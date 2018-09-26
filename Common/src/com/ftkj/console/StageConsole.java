package com.ftkj.console;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.enums.EConfigKey;
import com.ftkj.manager.stage.cfg.StageBean;

/**
 * @Description:主线赛程
 * @author Jay
 * @time:2017年3月24日 上午11:24:57
 */
public class StageConsole {

	private static Map<Integer, StageBean> stageMap;
	
	/**
	 * 每天最大可挑战次数
	 */
	public static int TODAY_MAX_FIGHT;
	/**
	 * 最大关卡数，把一个赛季的常规赛和季后赛分成两个关卡了
	 */
	public static int MAX_SCENE;
	/**
	 * 最大关卡数
	 */
	public static int MAX_STAGE;
	
	public StageConsole() {
	}
	
	public static void init(List<StageBean> stageList) {
		Map<Integer, StageBean> map = stageList.stream().collect(Collectors.toMap(StageBean::getStageId, bean-> bean));
		stageMap = map;
		// 读配置；
		TODAY_MAX_FIGHT = ConfigConsole.getIntVal(EConfigKey.STAGE_TODAY_MAX_NUM);
		MAX_STAGE = map.size();
		MAX_SCENE = map.values().stream().mapToInt(b-> b.getTab()).max().orElse(1);
	}
	
	/**
	 * 取关卡配置文件
	 * @param stageId
	 * @return
	 */
	public static StageBean getStageBean(int stageId) {
		return stageMap.get(stageId);
	}
	
	
	
	
	

}
