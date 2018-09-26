package com.ftkj.manager.match;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 主线赛程系统关卡信息
 */
public class MainMatchSystemLev implements Serializable {
    private static final long serialVersionUID = 1758999104551457519L;
    /** 关卡配置id */
    private final int levRid;
    /** 锦标赛排行榜玩家. map[levRid常规赛关卡配置id, SysLevRank]. 按 WinData.value 排序 */
    private final ConcurrentMap<Integer, SysLevRank> csRanks = new ConcurrentHashMap<>();

    public MainMatchSystemLev(int levRid) {
        this.levRid = levRid;
    }

    public int getLevRid() {
        return levRid;
    }

    @Override
    public String toString() {
        return "{" +
                "\"rid\":" + levRid +
                ", \"ranks\":" + csRanks +
                '}';
    }

    public SysLevRank getLevRank(int levRid) {
        return csRanks.get(levRid);
    }

    public SysLevRank getOrCreateLevRank(int levRid) {
        SysLevRank lr = csRanks.get(levRid);
        if (lr == null) {
            lr = new SysLevRank(levRid);
            SysLevRank old = csRanks.putIfAbsent(levRid, lr);
            if (old != null) {
                lr = old;
            }
        }
        return lr;
    }

    public int getRankSize() {
        int size = 0;
        for (SysLevRank lr : csRanks.values()) {
            size += lr.getTeams().size();
        }
        return size;
    }

    public ConcurrentMap<Integer, SysLevRank> getCsRanks() {
        return csRanks;
    }

    /** 关卡排行榜 */
    public static final class SysLevRank implements Serializable {
        private static final long serialVersionUID = -7723091358422890393L;
        /** 关卡配置id */
        private final int levRid;
        /** 排行榜. 按 WinData.value 排序 */
        private List<SysChampionshipTeam> teams = new ArrayList<>();

        public SysLevRank(int levRid) {
            this.levRid = levRid;
        }

        public int getLevRid() {
            return levRid;
        }

        public List<SysChampionshipTeam> getTeams() {
            return teams;
        }

        //        public void fillTeamsNull(int rankNumCfg) {
        //            for (int i = 0; i < rankNumCfg - teams.size(); i++) {
        //                teams.add(null);
        //            }
        //        }

        public SysChampionshipTeam getChampionshipTeam(long teamId) {
            for (SysChampionshipTeam team : teams) {
                if (team.getTeamId() == teamId) {
                    return team;
                }
            }
            return null;
        }

        public void addLastChampionshipTeam(SysChampionshipTeam newteam) {
            teams.add(newteam);
        }

        public int resizeTeams(int newSize) {
            int oldsize = teams.size();
            if (oldsize > newSize) {
                teams = new ArrayList<>(teams.subList(0, newSize));
            }
            return oldsize;
        }

        /** 对排行榜重新排序. 按比赛数据倒序排序 */
        public void sort(Comparator<Integer> wdComp) {
            teams.sort((o1, o2) -> {
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return wdComp.compare(o1.getRankScore(), o2.getRankScore());
            });
        }

        @Override
        public String toString() {
            return "{" +
                    "\"levRid\":" + levRid +
                    ", \"tsize\":" + teams.size() +
                    ", \"teams\":" + teams +
                    '}';
        }

    }

    /** 锦标赛. 排行榜玩家 */
    public static final class SysChampionshipTeam implements Serializable {
        private static final long serialVersionUID = -6343537588075603202L;
        /** 球队id */
        private long teamId;
        /** 星级比赛数据的评分, 用于排行榜 */
        private int rankScore;
        /** 上榜时间 */
        private long firstTime;
        /** 最后更新时间 */
        private long lastUpTime;

        public SysChampionshipTeam() {
        }

        public SysChampionshipTeam(long teamId) {
            this.teamId = teamId;
        }

        public SysChampionshipTeam(long teamId, int rankScore, long firstTime) {
            this.teamId = teamId;
            this.rankScore = rankScore;
            this.firstTime = firstTime;
        }

        public long getTeamId() {
            return teamId;
        }

        public long getFirstTime() {
            return firstTime;
        }

        public int getRankScore() {
            return rankScore;
        }

        public void setRankScore(int rankScore) {
            this.rankScore = rankScore;
        }

        public long getLastUpTime() {
            return lastUpTime;
        }

        public void setLastUpTime(long lastUpTime) {
            this.lastUpTime = lastUpTime;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"tid\":" + teamId +
                    ", \"rs\":" + rankScore +
                    //                    ", \"ct\":" + firstTime +
                    //                    ", \"ut\":" + lastUpTime +
                    '}';
        }
    }

}
