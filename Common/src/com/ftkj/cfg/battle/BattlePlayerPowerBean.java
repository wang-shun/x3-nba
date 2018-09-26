package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ParseListColumnUtil;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple2;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple2;
import com.ftkj.enums.EActionType;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** 回合体力变化配置 */
public final class BattlePlayerPowerBean implements Serializable {
    private static final long serialVersionUID = -5978759821128380972L;
    /** 体力配置组id */
    private final int groupId;
    /** 上阵球员回合体力变化比率 */
    private final float lineupPower;
    /** 替补球员回合体力变化比率 */
    private final float subPower;
    /** 子行为技能能量变化 */
    private final ImmutableMap<EActionType, Float> actPowers;

    public BattlePlayerPowerBean(int groupId, float lineupPower, float subPower, ImmutableMap<EActionType, Float> actPowers) {
        this.groupId = groupId;
        this.lineupPower = lineupPower;
        this.subPower = subPower;
        this.actPowers = actPowers;
    }

    public int getGroupId() {
        return groupId;
    }

    public float getLineupPower() {
        return lineupPower;
    }

    public float getSubPower() {
        return subPower;
    }

    public ImmutableMap<EActionType, Float> getActPowers() {
        return actPowers;
    }

    public float getActPowers(EActionType type, float defaultValue) {
        return actPowers.getOrDefault(type, defaultValue);
    }

    @Override
    public String toString() {
        return "{" +
                "\"groupId\":" + groupId +
                ", \"lineupPower\":" + lineupPower +
                ", \"subPower\":" + subPower +
                ", \"actPowers\":" + actPowers +
                '}';
    }

    /** 回合体力变化配置 */
    public static final class Builder extends AbstractExcelBean {
        /** 体力配置组id */
        private int groupId;
        /** 上阵球员回合体力变化比率 */
        private float lineupPower;
        /** 替补球员回合体力变化比率 */
        private float subPower;
        /** 子行为技能能量变化 */
        private ImmutableMap<EActionType, Float> actPowers;

        public int getGroupId() {
            return groupId;
        }

        @Override
        public void initExec(RowData row) {
            this.actPowers = parseActAndPower(row);
        }

        public BattlePlayerPowerBean build() {
            return new BattlePlayerPowerBean(groupId, lineupPower, subPower, actPowers);
        }

        protected static ImmutableMap<EActionType, Float> parseActAndPower(RowData row) {
            //            act2	power2
            //            子行为名称	球员体力
            Map<EActionType, Float> actConditions = new EnumMap<>(EActionType.class);
            IDListTuple2<String, String, Float> ltp =
                    ParseListColumnUtil.parse(row, toStr, "act", toStr, "power", toFloat);
            for (IDTuple2<String, String, Float> tp : ltp.getTuples().values()) {
                if (tp.getE1() == null || tp.getE1().isEmpty()) {
                    continue;
                }
                EActionType act = EActionType.convertByName(tp.getE1());
                Objects.requireNonNull(act, "act name : " + tp.getE1());
                actConditions.put(act, tp.getE2());
            }
            return ImmutableMap.copyOf(actConditions);
        }
    }
}
