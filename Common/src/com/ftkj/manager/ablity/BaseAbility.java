package com.ftkj.manager.ablity;

import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.cap.Cap;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年2月24日
 * 战斗属性属性
 */
public class BaseAbility implements Serializable {
    private static final long serialVersionUID = 1L;
    protected AbilityType type;
    /** 能力信息. 详细属性，得分篮板等 */
    protected Map<EActionType, Float> abilities;
    //	//进攻战力
    //	protected float attackCap;
    //	//防守战力
    //	protected float defendCap;

    public BaseAbility(AbilityType type) {
        this.type = type;
        this.abilities = Maps.newHashMap();
    }

    public void clear() {
    }

    public AbilityType getType() {
        return type;
    }

    public float getAttrData(EActionType type) {
        return abilities.getOrDefault(type, 0f);
    }

    public float get(EActionType type) {
        return abilities.getOrDefault(type, 0f);
    }

    public int getInt(EActionType type) {
        return abilities.getOrDefault(type, 0f).intValue();
    }

    public void addSameInfo(PlayerAbility ability) {
        if (ability != null) {
            ability.abilities.forEach(this::addAttr);
        }
    }

    /**
     * @param attackCap
     * @param defendCap
     */
    public void sumCap(float attackCap, float defendCap) {
        addAttr(EActionType.ocap, attackCap);
        addAttr(EActionType.dcap, defendCap);
        //		super.attackCap+=attackCap;
        //		super.defendCap+=defendCap;
    }

    /**
     * 如果有，则自增
     *
     * @param type
     * @param val
     */
    public void addAttr(EActionType type, float val) {
        Float tmp = this.abilities.get(type);
        if (tmp != null) {
            val = val + tmp;
        }
        this.abilities.put(type, val);
    }

    //	public float getAttackCap() {
    //		return attackCap;
    //	}
    //
    //	public float getDefendCap() {
    //		return defendCap;
    //	}
    //

    /**
     * 覆盖
     *
     * @param type
     * @param val
     */
    public Float setAttr(EActionType type, float val) {
        return abilities.put(type, val);
    }

    public int getTotalCap() {
        return (int) Math.floor(getAttrData(EActionType.ocap) + getAttrData(EActionType.dcap));
    }

    public Cap getCap() {
        return new Cap(getAttrData(EActionType.ocap), getAttrData(EActionType.dcap));
    }
}
