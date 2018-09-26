package com.ftkj.manager.custom.guess;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.custom.CustomGuessResult;
import com.ftkj.manager.custom.CustomPVPRoom;

/**
 * @author tim.huang
 * 2017年8月30日
 * 双方助攻总和
 */
public class CustomGuessZG implements ICustomGuessBattle{
	
	@Override
	public CustomGuessResult getCustomGuessResult(BattleSource bs) {
		int home = bs.getHome().getPlayers().stream()
				.mapToInt(player->(int)player.getRealTimeActionStats().getValue(EActionType.ast)).sum();
		int away = bs.getAway().getPlayers().stream()
				.mapToInt(player->(int)player.getRealTimeActionStats().getValue(EActionType.ast)).sum();
		int total = home + away;
		CustomPVPRoom room = bs.getAttributeMap(0).getVal(EBattleAttribute.擂台赛房间);
		CustomGuessResult result = new CustomGuessResult(room.getRoomId());
		result.setHomeName(bs.getHome().getName());
		result.setAwayName(bs.getAway().getName());
		result.setA(total%2!=0);
		return result;
	}
	
	
}
