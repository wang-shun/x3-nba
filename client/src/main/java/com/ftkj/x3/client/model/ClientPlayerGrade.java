package com.ftkj.x3.client.model;

import com.ftkj.manager.player.PlayerGrade;

/**
 * @author luch
 */
public final class ClientPlayerGrade extends PlayerGrade {

    public ClientPlayerGrade() {
        setGrade(1);
    }

    public ClientPlayerGrade(long teamId, int playerId) {
        super(teamId, playerId);
    }

    @Override
    public void save() {
    }

    @Override
    public void del() {
    }
}
