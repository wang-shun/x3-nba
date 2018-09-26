package com.ftkj.manager.custom.guess;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.custom.CustomGuessResult;
import com.ftkj.manager.custom.CustomPVPRoom;

/**
 * @author tim.huang
 * 2017年8月30日
 * 双方犯规总和
 */
public class CustomGuessFG2 implements ICustomGuessBattle {

    @Override
    public CustomGuessResult getCustomGuessResult(BattleSource bs) {
        int home = pfSum(bs.getHome());
        int away = pfSum(bs.getAway());
        CustomPVPRoom room = bs.getAttributeMap(0).getVal(EBattleAttribute.擂台赛房间);
        CustomGuessResult result = new CustomGuessResult(room.getRoomId());
        result.setHomeName(bs.getHome().getName());
        result.setAwayName(bs.getAway().getName());
        result.setA(home > away);
        return result;
    }

    static int pfSum(BattleTeam bt) {
        return bt.getPlayers().stream()
                .mapToInt(player -> player.getRealTimeActionStats().getIntValue(EActionType.pf)).sum();
    }

}
