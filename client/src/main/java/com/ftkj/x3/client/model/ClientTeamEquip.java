package com.ftkj.x3.client.model;

import java.util.Map;

/**
 * @author luch
 */
public class ClientTeamEquip {

    /**
     * 保证每个球员只能有一套装备， 这里面不包括球衣
     * playerId : PlayerEqui
     */
    private Map<Integer, ClientPlayerEquip> equips;

    /**
     * 球队衣服
     * playerId : playerEqui
     */
    private Map<Integer, ClientPlayerEquip> clothes;

    public Map<Integer, ClientPlayerEquip> getEquips() {
        return equips;
    }

    public void setEquips(Map<Integer, ClientPlayerEquip> equips) {
        this.equips = equips;
    }

    public Map<Integer, ClientPlayerEquip> getClothes() {
        return clothes;
    }

    public void setClothes(Map<Integer, ClientPlayerEquip> clothes) {
        this.clothes = clothes;
    }
}
