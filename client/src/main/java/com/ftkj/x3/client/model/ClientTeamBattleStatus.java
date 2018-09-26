package com.ftkj.x3.client.model;

import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.team.TeamBattleStatus;

public class ClientTeamBattleStatus extends TeamBattleStatus {
    private static final long serialVersionUID = 1211925043938763108L;
    private EBattleStage stage;

    public ClientTeamBattleStatus() {
    }

    public ClientTeamBattleStatus(EBattleType type, long battleId, String node) {
        super(type, battleId, node);
    }

    public EBattleStage getStage() {
        return stage;
    }

    public void setStage(EBattleStage stage) {
        this.stage = stage;
    }

    public boolean inBattle() {
        return getBattleId() != 0;
    }
}
