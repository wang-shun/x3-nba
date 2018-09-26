package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.enums.EActionType;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;

/** 回合技能能量变化配置 */
public final class BattleSkillPowerBean implements Serializable {
    private static final long serialVersionUID = 242639916423697770L;
    /** 技能能量配置组id */
    private final int groupId;
    /** 每回合技能能量变化 */
    private final int roundPower;
    /** 子行为技能能量变化 */
    private final ImmutableMap<EActionType, Float> actPowers;

    public BattleSkillPowerBean(int groupId, int roundPower, ImmutableMap<EActionType, Float> actPowers) {
        this.groupId = groupId;
        this.roundPower = roundPower;
        this.actPowers = actPowers;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getRoundPower() {
        return roundPower;
    }

    public ImmutableMap<EActionType, Float> getActPowers() {
        return actPowers;
    }

    public int getActPowers(EActionType type, float defaultValue) {
        return actPowers.getOrDefault(type, defaultValue).intValue();
    }

    @Override
    public String toString() {
        return "{" +
                "\"groupId\":" + groupId +
                ", \"roundPower\":" + roundPower +
                ", \"actPowers\":" + actPowers +
                '}';
    }

    /** 回合技能能量变化配置 */
    public static final class Builder extends AbstractExcelBean {
        /** 技能能量配置组id */
        private int groupId;
        /** 每回合技能能量变化 */
        private int roundPower;
        /** 子行为技能能量变化 */
        private ImmutableMap<EActionType, Float> actPowers;

        public int getGroupId() {
            return groupId;
        }

        @Override
        public void initExec(RowData row) {
            this.actPowers = BattlePlayerPowerBean.Builder.parseActAndPower(row);
        }

        public BattleSkillPowerBean build() {
            return new BattleSkillPowerBean(groupId, roundPower, actPowers);
        }
    }
}
