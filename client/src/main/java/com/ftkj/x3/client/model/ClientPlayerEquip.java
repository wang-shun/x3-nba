package com.ftkj.x3.client.model;

import java.util.Map;

/**
 * @author luch
 */
public class ClientPlayerEquip {
    /**
     * 球员
     */
    private int playerId;
    /**
     * 装备列表，部位：装备
     */
    private Map<Integer, ClientEquip> equips;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public Map<Integer, ClientEquip> getEquips() {
        return equips;
    }

    public void setEquips(Map<Integer, ClientEquip> equips) {
        this.equips = equips;
    }
}
