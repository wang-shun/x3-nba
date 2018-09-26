package com.ftkj.manager.ablity;

import java.util.List;
import java.util.Map;

import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;

/**
 * @author tim.huang 2017年3月7日
 */
public class PlayerAbility extends BaseAbility {
    private static final long serialVersionUID = 1L;
    
    protected int playerId;

    public PlayerAbility(AbilityType type, int playerId) {
        super(type);
        this.playerId = playerId;
    }

    public PlayerAbility(AbilityType type, List<PlayerAbility> initData, float positionCap) {
        super(type);
        if (initData != null && initData.size() > 0) {
            initData.forEach(ab ->
                    ab.abilities.forEach(this::addAttr));
        }
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public void clear() {
        // super.attackCap=0;
        // super.defendCap=0;
        // this.power=0;
        this.abilities.clear();
    }

    //    /**
    //     * replaced by {@link BattlePlayer#sum(com.ftkj.manager.ablity.PlayerAbility, com.ftkj.manager.ablity.PlayerAbility, float, float, float, float)}
    //     *
    //     * @param ability
    //     * @param power   当前体力
    //     */
    //    @Deprecated
    //    public void sum(PlayerAbility ability, float power, float positionCap, float tacticsjg, float tacticsfs) {
    //        // this.power+=ability.getPower();
    //        // ability.getInfoMap().forEach((k,v)->this.infoMap.put(k,
    //        // getAttrData(k)+v));
    //        //
    //        if (power >= 60) {
    //            power = Math.round((100 + power) * 1.0f / 2.0f);
    //        } else {
    //            power = Math.round(4 * power * 1.0f / 3.0f);
    //        }
    //        if (power < 10) {
    //            power = 10;
    //        }
    //
    //        sumCap(ability.getAttrData(EActionType.attack_cap) * power * positionCap * tacticsjg / 10000000.0f,
    //                ability.getAttrData(EActionType.guard_cap) * power * positionCap * tacticsfs / 10000000.0f);
    //    }

    // public float getAttackCap() {
    // return attackCap;
    // }
    // public float getDefendCap() {
    // return defendCap;
    // }

    public Map<EActionType, Float> getAttrs() {
        return abilities;
    }

    @Override
    public String toString() {
        return "{" +
                "\"pid\":" + playerId +
                ", \"type\":" + type +
                ", \"attr\":" + abilities +
                '}';
    }
}
