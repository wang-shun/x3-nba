package com.ftkj.manager.match;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.proto.RankedMatchPb.RMatchSeasonResp;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 球队跨服天梯赛信息 */
public class RankedMatch extends AsynchronousBatchDB implements Serializable {
    private static final long serialVersionUID = 3309963139903261683L;
    private long teamId;
    private String nodeName;

    /** 当前赛季. */
    private Season currSeason;
    /** 最后参与的赛季(不包括当前赛季). 赛季结束时更新 */
    private Season lastSeason;

    /** 首次奖励领取状态 */
    private long firstAward;
    /** 总比赛次数 */
    private int totalMatchCount;
    /** 总比赛胜利次数 */
    private int totalWinCount;
    /** 当前连胜次数 */
    private int winningStreak;
    /** 最后比赛结束时间 */
    private long lastMatchTime;

    //临时信息
    /** 已经加入到匹配池 */
    private boolean tempInPool;

    public RankedMatch() {
    }

    public RankedMatch(long teamId) {
        this.teamId = teamId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Season getCurrSeason() {
        return currSeason;
    }

    public void setCurrSeason(Season currSeason) {
        this.currSeason = currSeason;
    }

    public Season getLastSeason() {
        return lastSeason;
    }

    public void setLastSeason(Season lastSeason) {
        this.lastSeason = lastSeason;
    }

    public long getFirstAward() {
        return firstAward;
    }

    public void setFirstAward(long firstAward) {
        this.firstAward = firstAward;
    }

    public int getTotalMatchCount() {
        return totalMatchCount;
    }

    public void setTotalMatchCount(int totalMatchCount) {
        this.totalMatchCount = totalMatchCount;
    }

    public int getTotalWinCount() {
        return totalWinCount;
    }

    public void setTotalWinCount(int totalWinCount) {
        this.totalWinCount = totalWinCount;
    }

    public long getLastMatchTime() {
        return lastMatchTime;
    }

    public void setLastMatchTime(long lastMatchTime) {
        this.lastMatchTime = lastMatchTime;
    }

    public int getWinningStreak() {
        return winningStreak;
    }

    public void setWinningStreak(int winningStreak) {
        this.winningStreak = winningStreak;
    }

    /** 赛季切换时清除数据 */
    public void resetBySeason() {
        winningStreak = 0;
    }

    public boolean isTempInPool() {
        return tempInPool;
    }

    public void setTempInPool(boolean tempInPool) {
        this.tempInPool = tempInPool;
    }

    @Override
    public String getSource() {
        List<Object[]> args = new ArrayList<>();
        args.add(new Object[]{teamId, nodeName, firstAward, totalMatchCount, totalWinCount,
                winningStreak, lastMatchTime});

        args.add(currSeason != null ? seasonRow(currSeason) : seasonNull());
        args.add(lastSeason != null ? seasonRow(lastSeason) : seasonNull());
        return StringUtil.formatSQL(args);
    }

    @Override
    public String getRowNames() {
        return ROW_NAMES;
    }

    @Override
    public List<Object> getRowParameterList() {
        List<Object> args = new ArrayList<>();

        List<Object> o1 = Arrays.asList(new Object[]{teamId, nodeName, firstAward, totalMatchCount, totalWinCount,
            winningStreak, lastMatchTime});
        args.addAll(o1);

        List<Object> o2 = Arrays.asList(currSeason != null ? seasonRow(currSeason) : seasonNull());
        List<Object> o3 = Arrays.asList(lastSeason != null ? seasonRow(lastSeason) : seasonNull());
        args.addAll(o2);
        args.addAll(o3);
        return args;
    }


    @Override
    public String getTableName() {
        return "t_u_rmatch_t";
    }

    private static final String ROW_NAMES = "team_id, node_name, first_award, t_m_c, t_w_c, w_s, last_time,"
            + seasonRowNames("") + "," + seasonRowNames("pre_");

    private static String seasonRowNames(String pre) {
        return pre + "s_id, " +
                pre + "tier, " +
                pre + "rank, " +
                pre + "rating, " +
                pre + "m_c, " +
                pre + "w_c, " +
                pre + "w_s_max";
    }



    private static Object[] seasonRow(Season s) {
        return new Object[]{s.getId(), s.getTierId(), s.getRank(), s.getRating(), s.getMatchCount(),
                s.getWinCount(), s.getWinningStreakMax()};
    }

    private static Object[] seasonNull() {
        return new Object[]{0, 0, 0, 0, 0, 0, 0};
    }

    @Override
    public void del() {
    }

    @Override
    public String toString() {
        return "{" +
                "\"tid\":" + teamId +
                ", \"node\":\"" + nodeName + "\"" +
                ", \"cs\":" + currSeason +
                ", \"ls\":" + lastSeason +
                ", \"fa\":" + firstAward +
                ", \"tmc\":" + totalMatchCount +
                ", \"twc\":" + totalWinCount +
                ", \"ws\":" + winningStreak +
                ", \"lt\":" + lastMatchTime +
                '}';
    }



    /** 赛季数据 */
    public static final class Season implements Serializable {
        private static final long serialVersionUID = -3675373532238083401L;
        /** 赛季id */
        private int id;
        /** 层级id */
        private int tierId;
        /** 段位的排名(定时更新). <= 0 为排名未定 */
        private int rank;
        /** 评分 */
        private int rating;
        /** 参与比赛次数 */
        private int matchCount;
        /** 胜利次数 */
        private int winCount;
        /** 最大连胜次数 */
        private int winningStreakMax;

        public Season() {
        }

        public Season(Season season) {
            this.id = season.id;
            this.tierId = season.tierId;
            this.rank = season.rank;
            this.rating = season.rating;
            this.matchCount = season.matchCount;
            this.winCount = season.winCount;
            this.winningStreakMax = season.winningStreakMax;
        }

        /** 赛季切换时清除数据 */
        public void resetBySeason() {
            rank = 0;
            matchCount = 0;
            winCount = 0;
            winningStreakMax = 0;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTierId() {
            return tierId;
        }

        public void setTierId(int tierId) {
            this.tierId = tierId;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public void addRating(int num) {
            this.rating += num;
        }

        public int getMatchCount() {
            return matchCount;
        }

        public void setMatchCount(int matchCount) {
            this.matchCount = matchCount;
        }

        public int getWinCount() {
            return winCount;
        }

        public void setWinCount(int winCount) {
            this.winCount = winCount;
        }

        public int getWinningStreakMax() {
            return winningStreakMax;
        }

        public void setWinningStreakMax(int winningStreakMax) {
            this.winningStreakMax = winningStreakMax;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    ", \"tier\":" + tierId +
                    ", \"rank\":" + rank +
                    ", \"rating\":" + rating +
                    ", \"mc\":" + matchCount +
                    ", \"wc\":" + winCount +
                    ", \"wsmax\":" + winningStreakMax +
                    '}';
        }

    }

    /** 赛季历史数据 */
    public static class SeasonHistory extends AsynchronousBatchDB implements Serializable {
        private static final long serialVersionUID = -8718861477294660013L;
        /** db id */
        private long seqid;
        /** 球队id */
        private long teamId;
        private Season season;
        /** 总比赛次数 */
        private int totalMatchCount;
        /** 当前连胜次数 */
        private int winningStreak;

        public SeasonHistory() {
        }

        public SeasonHistory(long seqid, RankedMatch rm, Season season) {
            this.seqid = seqid;
            this.teamId = rm.getTeamId();
            this.season = new Season(season);
            this.totalMatchCount = rm.getTotalMatchCount();
            this.winningStreak = rm.getWinningStreak();
        }

        public long getSeqid() {
            return seqid;
        }

        public void setSeqid(long seqid) {
            this.seqid = seqid;
        }

        public long getTeamId() {
            return teamId;
        }

        public void setTeamId(long teamId) {
            this.teamId = teamId;
        }

        public Season getSeason() {
            return season;
        }

        public void setSeason(Season season) {
            this.season = season;
        }

        public int getTotalMatchCount() {
            return totalMatchCount;
        }

        public void setTotalMatchCount(int totalMatchCount) {
            this.totalMatchCount = totalMatchCount;
        }

        public int getWinningStreak() {
            return winningStreak;
        }

        public void setWinningStreak(int winningStreak) {
            this.winningStreak = winningStreak;
        }

        @Override
        public String getSource() {
            List<Object[]> args = new ArrayList<>();
            args.add(new Object[]{seqid, teamId, totalMatchCount, winningStreak});
            args.add(season != null ? seasonRow(season) : seasonNull());
            return StringUtil.formatSQL(args);
        }

        @Override
        public String getRowNames() {
            return "id, team_id, t_m_c, w_s," + seasonRowNames("");
        }

        @Override
        public List<Object> getRowParameterList() {
            List<Object> args = new ArrayList<>();
            List<Object> o1 = Arrays.asList(new Object[]{seqid, teamId, totalMatchCount, winningStreak});
            args.addAll(o1);
            List<Object> o2 = Arrays.asList(season != null ? seasonRow(season) : seasonNull());
            args.addAll(o2);
            return args;
        }

        @Override
        public String getTableName() {
            return "t_u_rmatch_s_his";
        }

        @Override
        public void del() {
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + seqid +
                    ", \"tid\":" + teamId +
                    ", \"s\":" + season +
                    ", \"tmc\":" + totalMatchCount +
                    ", \"es\":" + winningStreak +
                    '}';
        }


    }

    public static RMatchSeasonResp seasonResp(Season s) {
        return seasonResp(s, s.getRank());
    }

    public static RMatchSeasonResp seasonResp(Season s, int rank) {
        return RMatchSeasonResp.newBuilder()
                .setId(s.getId())
                .setTierId(s.getTierId())
                .setRank(rank)
                .setRating(s.getRating())
                .setMatchCount(s.getMatchCount())
                .setWinCount(s.getWinCount())
                .setWinningStreakMax(s.getWinningStreakMax())
                .build();
    }
}
