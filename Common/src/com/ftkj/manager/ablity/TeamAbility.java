package com.ftkj.manager.ablity;

import com.ftkj.enums.AbilityType;

import java.util.Collection;

/**
 * @author tim.huang
 * 2017年3月27日
 * 球队基础
 */
public class TeamAbility extends BaseAbility {
    private static final long serialVersionUID = -4938573202731300127L;

    public TeamAbility(AbilityType type) {
        super(type);
    }

    public TeamAbility(AbilityType type, Collection<PlayerAbility> initData) {
        super(type);
        if (initData != null && initData.size() > 0) {
            initData.forEach(ab -> {
                ab.abilities.forEach((k, v) -> this.addAttr(k, v));
            });
        }
    }

    @Override
    public void clear() {
        this.abilities.clear();
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":" + type +
                ", \"attr\":" + abilities +
                '}';
    }

}
