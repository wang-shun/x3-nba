package com.ftkj.manager.player.api;

import java.util.HashMap;
import java.util.Map;

import com.ftkj.enums.EPlayerPosition;

/**
 * @author tim.huang
 * 2017年2月28日
 * 位置加成API
 */
public abstract class PositionAPI {
	
	protected Map<EPlayerPosition,PlayerPositionCap> map;
	
	public PositionAPI() {
		super();
		initMap();
	}

	/**
	 * 初始化配置
	 */
	protected abstract void initMap();
	
	public float getCap(EPlayerPosition position,EPlayerPosition playerPosition){
		//处理异常情况
		if(EPlayerPosition.NULL == position || EPlayerPosition.NULL == playerPosition) {
			return 100f;
		}
		return map.get(position).getCap(playerPosition);
	}
	
	public float getCap(EPlayerPosition position,EPlayerPosition[] playerPosition){
		//处理异常情况
		if(EPlayerPosition.NULL == position) {
			return 100f;
		}
		float a = map.get(position).getCap(playerPosition[0]);
		float b = map.get(position).getCap(playerPosition[1]);
		return Float.max(a, b);
	}
	
	
	/**
	 * @author tim.huang
	 * 2017年2月28日
	 * 球员位置与所在位置发挥的战力百分比对应
	 */
	protected static class PlayerPositionCap{
		private Map<EPlayerPosition,Float> positionMap;
		public PlayerPositionCap() {
			super();
			this.positionMap = new HashMap<EPlayerPosition, Float>();
		}
		
		public PlayerPositionCap push(EPlayerPosition position,float val){
			this.positionMap.put(position, val);
			return this;
		}
		
		public float getCap(EPlayerPosition position){
			return positionMap.get(position);
		}
		
	}
	
}
