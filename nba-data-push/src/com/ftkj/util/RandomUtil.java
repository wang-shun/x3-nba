package com.ftkj.util;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Lists;

public class RandomUtil {

	private static final Random random = new Random();
	
	public static int ran(int max){
		return random.nextInt(max);
	}
	
	
	/**
	 * 1-10(x>=1&&x<10)
	 * @param min
	 * @param max
	 * @return
	 */
	public static int ran(int min,int max){
		if(min == max) return min;
		int num = ran(max-min);
		return min+num;
	}
	
	/**
	 * 是否命中
	 * @param max 最大范围
	 * @param rate 命中概率范围
	 * @return true命中
	 */
	public static boolean ranHit(int max, int rate) {
		int ran = random.nextInt(max) + 1;
		if(ran <= rate) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 随机命中map，随机多次结果
	 * @param max
	 * @param rateMap
	 * @param hitNum 需要随机次数
	 * @return
	 */
	public static List<Integer> ranMapHitNum(int max, Map<Integer, Integer> rateMap, int hitNum) {
		List<RanArea> list = mapToArea(max, rateMap);
		List<Integer> hitList = Lists.newArrayList();
		for(int i=0; i<hitNum; i++) {
			int hit = random.nextInt(max);
			for(RanArea a : list) {
				if(a.isArea(hit)) {
					hitList.add(a.id);
				}
			}
		}
		return hitList;
	}
	
	/**
	 * 命中map
	 * @param max 概率总值
	 * @param rateMap id:概率  例如： max=1000  1：100 2:200  也就是命中1概率100/1000,命中2是200/1000  
	 * @return 命中的id值， -1 是没有命中结果, 命中值不是满值时，会出现这种情况，也就是 id_1和id_2的总命中不满1000
	 */
	private static int randMap(int max, Map<Integer, Integer> rateMap) {
		int hit = random.nextInt(max);
		List<RanArea> list = mapToArea(max, rateMap);
		int hitID = -1;
		for(RanArea a : list) {
			if(a.isArea(hit)) {
				hitID = a.id;
				break;
			}
		}
		// debug 打印
		if(max != list.get(list.size()-1).max) {
			System.err.println("非满值概率配比");
		}
		System.err.println("area["+list.toString()+"]");
		System.err.println("命中：[max="+max+"]    hit["+hit+"]   hitID["+hitID+"]");
		return hitID;
	}
	
	/**
	 * 随机 
	 * @param rateMap  id:概率
	 * @return
	 */
	public static int randMap(Map<Integer, Integer> rateMap) {
		return randMap((int)rateMap.keySet().stream().count(), rateMap); 
	}
	
	/**
	 * 随机
	 * @param rateMap 原始概率
	 * @param adjustMap 调整概率，可以空
	 * @return
	 */
	public static int randMap(Map<Integer, Integer> rateMap, Map<Integer, Integer> adjustMap) {
		if(adjustMap != null) {
			for(int key : adjustMap.keySet()) {
				int addRate = adjustMap.get(key);
				if(rateMap.containsKey(key)) {
					addRate += rateMap.get(key);
				}
				rateMap.put(key, addRate);
			}
		}
		return randMap((int)rateMap.values().stream().count(), rateMap); 
	}
	
	public static List<RanArea> mapToArea(int total, Map<Integer, Integer> rateMap) {
		List<RanArea> list = Lists.newArrayList();
		int min = 0;
		int max = 0;
		for(int key : rateMap.keySet()) {
			max = min + rateMap.get(key);
			list.add(new RanArea(key, min, max));
			min = max;
		}
		// -1是没有命中指定id
		if(max < total) {
			list.add(new RanArea(-1, max, total));
		}
		return list;
	}
	
	/**
	 * 命中范围工具类
	 * @author Jay
	 * @time:2017年5月12日 下午6:44:30
	 */
	public static class RanArea {
		int id;
		int min;
		int max;
		
		public RanArea(int id, int min, int max) {
			super();
			this.id = id;
			this.min = min;
			this.max = max;
		}
		/**
		 * 命中数值是否在范围内
		 * @param hit
		 * @return
		 */
		public boolean isArea(int hit) {
			if(hit >= min && hit < max) {
				return true;
			}
			return false;
		}
		@Override
		public String toString() {
			return "RanArea [id=" + id + ", min=" + min + ", max=" + max + "]";
		}
	}
	
}
