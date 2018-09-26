package com.ftkj.console;

import com.ftkj.enums.EPlayerGrade;
import com.ftkj.manager.gym.ArenaConstructionBean;
import com.ftkj.manager.gym.ArenaRoll;
import com.ftkj.manager.gym.ArenaRollItem;
import com.ftkj.manager.gym.ArenaBean;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 玩家球馆
 * @author tim.huang
 * 2017年7月5日
 *
 */
public class GymConsole {
	private static Map<Integer,ArenaBean> arenaMap;
	
	private static Map<Integer,ArenaRoll> arenaRollMap;
	
	private static Map<EPlayerGrade,Integer> playerAttackBuffer;
	private static Map<EPlayerGrade,Integer> playerDefendBuffer;
	
	
	public static void init(){
		//初始化转盘配置
		Map<Integer,List<ArenaRollItem>> tmpRoll = CM.arenaRollItemList.stream().collect(Collectors.groupingBy(v->v.getRid(),Collectors.toList()));
		Map<Integer,ArenaRoll> arenaRollMapTmp = Maps.newHashMap();
		tmpRoll.forEach((k,v)->arenaRollMapTmp.put(k,new ArenaRoll(k,v)));
		//初始化球馆配置
		Map<Integer,List<ArenaConstructionBean>> tmpC = CM.arenaConstructionBeanList.stream().collect(Collectors.groupingBy(v->v.getLevel(),Collectors.toList()));
		Map<Integer,ArenaBean> arenaMapTmp = CM.arenaBeanList.stream().collect(Collectors.toMap(ArenaBean::getLevel, v->v));
		arenaMapTmp.forEach((k,v)->v.initConstructionBean(tmpC.get(k)));
		//
		playerAttackBuffer = Maps.newHashMap();
		playerAttackBuffer.put(EPlayerGrade.A, 980);
		playerAttackBuffer.put(EPlayerGrade.A1, 980);
		playerAttackBuffer.put(EPlayerGrade.A2, 980);
		playerAttackBuffer.put(EPlayerGrade.B, 890);
		playerAttackBuffer.put(EPlayerGrade.B1, 890);
		playerAttackBuffer.put(EPlayerGrade.B2, 890);
		playerAttackBuffer.put(EPlayerGrade.S, 960);
		playerAttackBuffer.put(EPlayerGrade.S1, 970);
		playerAttackBuffer.put(EPlayerGrade.S2, 950);
		
		playerDefendBuffer = Maps.newHashMap();
		playerDefendBuffer.put(EPlayerGrade.A, 980);
		playerDefendBuffer.put(EPlayerGrade.A1, 980);
		playerDefendBuffer.put(EPlayerGrade.A2, 980);
		playerDefendBuffer.put(EPlayerGrade.B, 890);
		playerDefendBuffer.put(EPlayerGrade.B1, 890);
		playerDefendBuffer.put(EPlayerGrade.B2, 890);
		playerDefendBuffer.put(EPlayerGrade.S, 960);
		playerDefendBuffer.put(EPlayerGrade.S1, 970);
		playerDefendBuffer.put(EPlayerGrade.S2, 950);
		
		arenaRollMap = arenaRollMapTmp;
		arenaMap = arenaMapTmp;
	}
	
	public static int getPlayerAttackBuffer(EPlayerGrade grade){
		return playerAttackBuffer.getOrDefault(grade, 1000);
	}
	
	public static int getPlayerDefendBuffer(EPlayerGrade grade){
		return playerDefendBuffer.getOrDefault(grade, 1000);
	}
	/**
	 * 根据级别和类型取配置
	 * @param level
	 * @param ctype
	 * @return
	 */
	public static ArenaBean getArenaBean(int level){
		ArenaBean bean = arenaMap.get(level);
		return bean;
	}
	
	public static ArenaRoll getArenaRoll(int rid){
		return arenaRollMap.get(rid);
	}
}
