package com.ftkj.x3.client.model;

import com.ftkj.db.domain.PlayerPO;
import com.ftkj.enums.EPlayerPosition;

/**
 * @author luch
 */
public class ClientPlayer extends PlayerPO {
    private int attack;
    private int defend;

    private EPlayerPosition pos;
    private EPlayerPosition lineupPos;

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefend() {
        return defend;
    }

    public void setDefend(int defend) {
        this.defend = defend;
    }

    public EPlayerPosition getPos() {
        return pos;
    }

    public void setPos(EPlayerPosition pos) {
        this.pos = pos;
    }

    public EPlayerPosition getLineupPos() {
        return lineupPos;
    }

    public void setLineupPos(EPlayerPosition lineupPos) {
        this.lineupPos = lineupPos;
    }

    @Override
    public void save() {
    }

    @Override
    public void del() {
    }

}
