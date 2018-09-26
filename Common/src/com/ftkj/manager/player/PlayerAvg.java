package com.ftkj.manager.player;

import com.ftkj.db.domain.PlayerAvgInfo;
import com.ftkj.enums.EActionType;
import com.ftkj.util.lambda.LBFloat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2018年1月24日
 */
public class PlayerAvg {
    private long teamId;
    private Map<Integer, PlayerAvgInfo> playerMap;

    public PlayerAvg(long teamId, List<PlayerAvgInfo> list) {
        this.teamId = teamId;
        this.playerMap = list.stream().collect(Collectors.toMap(PlayerAvgInfo::getPlayerId, val -> val));
    }

    public PlayerAvgInfo getPlayerAvgInfo(int prid) {
        return this.playerMap.get(prid);
    }

    public synchronized void updatePlayerAvg(int prid, Map<EActionType, LBFloat> sourceMap) {
        PlayerAvgInfo avg = playerMap.get(prid);
        if (avg == null) {
            avg = new PlayerAvgInfo(teamId, prid);
            playerMap.put(prid, avg);
        }
        avg.update(sourceMap);
    }

    public void delPlayer(int prid) {
        PlayerAvgInfo avg = playerMap.get(prid);
        if (avg != null) {
            avg = new PlayerAvgInfo(teamId, prid);
            playerMap.replace(prid, avg);
            avg.save();
        }
    }

    public Map<Integer, PlayerAvgInfo> getPlayerMap() {
        return playerMap;
    }

    public long getTeamId() {
        return teamId;
    }

}
	
