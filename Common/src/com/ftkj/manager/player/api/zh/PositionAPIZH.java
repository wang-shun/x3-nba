package com.ftkj.manager.player.api.zh;

import java.util.HashMap;

import com.ftkj.enums.EPlayerPosition;
import com.ftkj.manager.player.api.PositionAPI;

/**
 * @author tim.huang
 * 2017年2月28日
 * 国内位置对应策略
 */
public class PositionAPIZH extends PositionAPI {
	
	@Override
	protected void initMap() {
		super.map = new HashMap<EPlayerPosition, PositionAPI.PlayerPositionCap>();
		
		
		super.map.put(EPlayerPosition.PG, new PlayerPositionCap()
											.push(EPlayerPosition.PG, 100f)
											.push(EPlayerPosition.SG, 80f)
											.push(EPlayerPosition.SF, 60f)
											.push(EPlayerPosition.PF, 40f)
											.push(EPlayerPosition.C, 20f)
											.push(EPlayerPosition.NULL, 100f));
		super.map.put(EPlayerPosition.SF, new PlayerPositionCap()
											.push(EPlayerPosition.SF, 100f)
											.push(EPlayerPosition.SG, 80f)
											.push(EPlayerPosition.PF, 80f)
											.push(EPlayerPosition.PG, 60f)
											.push(EPlayerPosition.C, 60f)
											.push(EPlayerPosition.NULL, 100f));
		super.map.put(EPlayerPosition.C, new PlayerPositionCap()
											.push(EPlayerPosition.C, 100f)
											.push(EPlayerPosition.PF, 80f)
											.push(EPlayerPosition.SF, 60f)
											.push(EPlayerPosition.SG, 40f)
											.push(EPlayerPosition.PG, 20f)
											.push(EPlayerPosition.NULL, 100f));
		super.map.put(EPlayerPosition.SG, new PlayerPositionCap()
											.push(EPlayerPosition.SG, 100f)
											.push(EPlayerPosition.SF, 80f)
											.push(EPlayerPosition.PG, 80f)
											.push(EPlayerPosition.PF, 60f)
											.push(EPlayerPosition.C, 40f)
											.push(EPlayerPosition.NULL, 100f));
		super.map.put(EPlayerPosition.PF, new PlayerPositionCap()
											.push(EPlayerPosition.PF, 100f)
											.push(EPlayerPosition.SF, 80f)
											.push(EPlayerPosition.C, 80f)
											.push(EPlayerPosition.SG, 60f)
											.push(EPlayerPosition.PG, 40f)
											.push(EPlayerPosition.NULL, 100f));
		
	}
	
	

}
