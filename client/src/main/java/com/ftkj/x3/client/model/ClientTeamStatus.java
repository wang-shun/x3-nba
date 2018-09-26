package com.ftkj.x3.client.model;

import com.ftkj.enums.battle.EBattleType;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/** 玩家状态 {@link com.ftkj.manager.team.TeamStatus} */
public class ClientTeamStatus {
    private int draftRoomId;//选秀房间ID
    private Map<Long, ClientTeamBattleStatus> battles = new ConcurrentHashMap<>();
    private DateTime worldChat;

    public int getDraftRoomId() {
        return draftRoomId;
    }

    public void setDraftRoomId(int draftRoomId) {
        this.draftRoomId = draftRoomId;
    }

    public Map<Long, ClientTeamBattleStatus> getBattles() {
        return battles;
    }

    public ClientTeamBattleStatus getBattle(long battleId) {
        return battles.get(battleId);
    }

    public void setBattles(Map<Long, ClientTeamBattleStatus> battles) {
        this.battles = battles;
    }

    public DateTime getWorldChat() {
        return worldChat;
    }

    public void setWorldChat(DateTime worldChat) {
        this.worldChat = worldChat;
    }

    public List<ClientTeamBattleStatus> getBattles(EBattleType... type) {
        Set<EBattleType> types = type.length == 1 ? Collections.singleton(type[0]) : new HashSet<>(Arrays.asList(type));
        return battles.values().stream()
                .filter(b -> types.contains(b.getType()))
                .collect(Collectors.toList());
    }

    public boolean inBattle(EBattleType... type) {
        for (ClientTeamBattleStatus bs : getBattles(type)) {
            if (bs != null && bs.getBattleId() != 0) {
                return true;
            }
        }
        return false;
    }

    public ClientTeamBattleStatus getInBattle(EBattleType... type) {
        Set<EBattleType> types = type.length == 1 ? Collections.singleton(type[0]) : new HashSet<>(Arrays.asList(type));
        return battles.values().stream()
                .filter(b -> types.contains(b.getType()) && b.getBattleId() > 0)
                .findFirst()
                .orElse(null);
    }
}
