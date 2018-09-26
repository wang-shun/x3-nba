package com.ftkj.x3.client.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.function.Function;

/**
 * @author luch
 */
public class ClientTeamPlayer {
    // 队伍球员列表. map[ClientPlayer.id, ClientPlayer]
    private ConcurrentNavigableMap<Integer, ClientPlayer> lineups;
    // 仓库 map[ClientPlayer.id, ClientPlayer]
    private ConcurrentNavigableMap<Integer, ClientPlayer> storagePlayers;
    // 自由市场冻结球员
    private ConcurrentNavigableMap<Integer, ClientPlayer> marketPlayers;

    private Map<Integer, ClientPlayerGrade> playerGrades = new ConcurrentHashMap<>();

    public ConcurrentNavigableMap<Integer, ClientPlayer> getLineups() {
        return lineups;
    }

    public ClientPlayer getLineup(Integer pid) {
        return lineups.get(pid);
    }

    public ClientPlayer getLineupByPrid(Integer prid) {
        for (ClientPlayer pr : lineups.values()) {
            if (pr.getPlayerRid() == prid) {
                return pr;
            }
        }
        return null;
    }

    public int getLineupSize() {
        return lineups != null ? lineups.size() : 0;
    }

    public void setLineups(ConcurrentNavigableMap<Integer, ClientPlayer> lineups) {
        this.lineups = lineups;
    }

    public void addLineups(ClientPlayer p) {
        lineups.put(p.getId(), p);
    }

    public ConcurrentNavigableMap<Integer, ClientPlayer> getStoragePlayers() {
        return storagePlayers;
    }

    public int getStoragePlayerSize() {
        return storagePlayers != null ? storagePlayers.size() : 0;
    }

    public void setStoragePlayers(ConcurrentNavigableMap<Integer, ClientPlayer> storagePlayers) {
        this.storagePlayers = storagePlayers;
    }

    public void addStoragePlayers(ClientPlayer player) {
        if (storagePlayers != null) {
            storagePlayers.put(player.getId(), player);
        }
    }

    public ClientPlayer getStoragePlayer(int pid) {
        return storagePlayers.get(pid);
    }

    public ClientPlayer getStoragePlayerByPrid(int prid) {
        for (ClientPlayer pr : storagePlayers.values()) {
            if (pr.getPlayerRid() == prid) {
                return pr;
            }
        }
        return null;
    }

    public ClientPlayer getPlayer(int pid) {
        return storagePlayers.getOrDefault(pid,
                lineups.get(pid));
    }

    public ClientPlayer getPlayerByRid(int prid) {
        ClientPlayer pr = getStoragePlayerByPrid(prid);
        return pr != null ? pr : getLineupByPrid(prid);
    }

    public ConcurrentNavigableMap<Integer, ClientPlayer> getMarketPlayers() {
        return marketPlayers;
    }

    public void setMarketPlayers(ConcurrentNavigableMap<Integer, ClientPlayer> marketPlayers) {
        this.marketPlayers = marketPlayers;
    }

    public void addPlayerGrade(ClientPlayerGrade pg) {
        playerGrades.put(pg.getPlayerId(), pg);
    }

    public void addPlayer(Map<Integer, ClientPlayer> players, ClientPlayer p) {
        players.put(p.getId(), p);
    }

    public void removePlayer(int pid) {
        storagePlayers.remove(pid);
        lineups.remove(pid);
    }

    public boolean hasStoragePlayer() {
        return storagePlayers != null && storagePlayers.size() > 0;
    }

    public ClientPlayerGrade getGrade(int playerRid) {
        return playerGrades.get(playerRid);
    }

    public ClientPlayerGrade addGrade(ClientPlayerGrade pg) {
        return playerGrades.put(pg.getPlayerId(), pg);
    }

    public ClientPlayerGrade getGradeOrDefault(int playerRid, Function<Integer, ClientPlayerGrade> defaultV) {
        ClientPlayerGrade pg = playerGrades.get(playerRid);
        return pg != null ? pg : defaultV.apply(playerRid);
    }

}
