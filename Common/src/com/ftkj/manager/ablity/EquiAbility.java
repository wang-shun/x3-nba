package com.ftkj.manager.ablity;

import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;

import java.util.Map;

/**
 * 装备攻防
 *
 * @author Jay
 * @time:2017年3月14日 下午6:23:04
 */
public class EquiAbility extends PlayerAbility {
    private static final long serialVersionUID = 1L;

    /**
     * 某个球员的攻防加成
     * 重新计算
     *
     * @param playerId
     * @param equiList
     */
    public EquiAbility(int playerId, Map<EActionType, Float> capMap) {
        super(AbilityType.Equip, playerId);
        this.abilities.putAll(capMap);
    }

    /**
     * 计算公式
     *
     * @return
     */
    public float sum() {
        return 0;
    }

}
