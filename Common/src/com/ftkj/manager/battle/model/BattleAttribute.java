package com.ftkj.manager.battle.model;

import com.ftkj.enums.battle.EBattleAttribute;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年3月9日
 * 比赛额外属性
 */
public class BattleAttribute implements Serializable {
    private static final long serialVersionUID = -2252795349050298428L;
    private long teamId;//假如teamId为0，则表示全局额外属性
    private Map<EBattleAttribute, Serializable> attributeMap;

    public BattleAttribute(long teamId) {
        super();
        this.teamId = teamId;
        this.attributeMap = Maps.newHashMap();
    }

    public long getTeamId() {
        return teamId;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getVal(EBattleAttribute key) {
        Object val = attributeMap.get(key);
        return val != null ? (T) val : null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getVal(EBattleAttribute key, T defaultValue) {
        Object val = attributeMap.get(key);
        return val != null ? (T) val : defaultValue;
    }

    public <T extends Serializable> BattleAttribute addVal(EBattleAttribute key, T val) {
        this.attributeMap.put(key, val);
        return this;
    }

    public boolean hasAttr(EBattleAttribute attr) {
        return attributeMap.containsKey(attr);
    }
}
