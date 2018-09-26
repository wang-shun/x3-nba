package com.ftkj.manager.logic;

import com.ftkj.manager.BaseManager;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.draft.RpcDraftRoom;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.server.RedisKey;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年5月8日
 * 球队状态管理
 */
public class TeamStatusManager extends BaseManager implements OfflineOperation {
    private Map<Long, TeamStatus> teamStatus;
    /** teamId : roomId */
    private Map<Integer, SimpleDraftRoom> draftRoomIdMap;

    public TeamStatus get(long teamId) {
        return getTeamStatus(teamId);
    }

    public TeamStatus getTeamStatus(long teamId) {
        TeamStatus ts = teamStatus.get(teamId);
        if (ts == null) {
            ts = redis.getObj(RedisKey.getKey(teamId, RedisKey.Team_Status));
            if (ts == null) {
                ts = new TeamStatus();
            }
            teamStatus.put(teamId, ts);
            //取数据清空超时比赛
            ts.getStates().stream()
                .filter(bat -> bat.isTimeOut())
                .forEach(bat -> bat.reset());
        }
        return ts;
    }

    @Override
    public void offline(long teamId) {
        offline0(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        offline0(teamId);
    }

    private void offline0(long teamId) {
        TeamStatus ts = teamStatus.get(teamId);
        if (ts != null) {
            saveTeamStatus(teamId, ts);
            teamStatus.remove(teamId);
        }
    }

    private void saveTeamStatus(long teamId, TeamStatus status) {
        redis.set(RedisKey.getKey(teamId, RedisKey.Team_Status), status);
    }

    @Override
    public void instanceAfter() {
        teamStatus = Maps.newConcurrentMap();
        draftRoomIdMap = Maps.newConcurrentMap();
        //重启服务器的时候清空所有玩家状态缓存
        redis.delRedisCache(RedisKey.Team_Status +"*");
    }

    /**
     * 保留房间列表，其他的删除
     */
    void syncRoomMap(List<RpcDraftRoom> rooms) {
        Map<Integer, SimpleDraftRoom> roomMap = Maps.newConcurrentMap();
        for (RpcDraftRoom room : rooms) {
            int roomId = room.getRoomId();
            Set<Long> teamIds = new HashSet<>(room.getTeamList());
            roomMap.put(roomId, new SimpleDraftRoom(roomId, teamIds));
        }
        this.draftRoomIdMap = roomMap;
        //
        log.warn("cross存活选秀房间：{}, 本区球队参与选秀房间：{}",
            rooms.stream().map(r -> r.getRoomId()).collect(Collectors.toList()),
            draftRoomIdMap.keySet());
    }

    void draftRoomEnd(int roomId) {
        draftRoomIdMap.remove(roomId);
    }

    private static class SimpleDraftRoom {
        private int roomId;
        private Set<Long> teamIds;

        private SimpleDraftRoom(int roomId) {
            super();
            this.roomId = roomId;
            this.teamIds = Sets.newConcurrentHashSet();
        }

        private SimpleDraftRoom(int roomId, Set<Long> teamIds) {
            super();
            this.roomId = roomId;
            this.teamIds = teamIds;
        }
    }

    int getDraftRoomId(long teamId) {
        SimpleDraftRoom sr = getDraftRoom(teamId);
        if (sr == null) { return 0; }
        return sr.roomId;
    }

    private SimpleDraftRoom getDraftRoom(long teamId) {
        for (SimpleDraftRoom room : draftRoomIdMap.values()) {
            if (room.teamIds.contains(teamId)) {
                return room;
            }
        }
        return null;
    }

    void addTeamToDraftRoom(Integer roomId, long teamId) {
        synchronized (roomId) {
            if (!draftRoomIdMap.containsKey(roomId)) {
                draftRoomIdMap.put(roomId, new SimpleDraftRoom(roomId));
            }
        }
        draftRoomIdMap.get(roomId).teamIds.add(teamId);
    }

}
