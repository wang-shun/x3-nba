package com.ftkj.manager.match;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 球队主线赛程信息
 */
public class MainMatch extends AsynchronousBatchDB {
    private static final long serialVersionUID = -8880661633316536070L;
    /** 球队id */
    private long teamId;
    /** 当前挑战次数 */
    private int matchNum;
    /** 挑战次数最后更新时间(ms) */
    private long matchNumLastUpTime;

    /** 最后一场比赛的关卡. 用于计算提供的装备经验 */
    private int lastLevelRid;
    /** 最后一场比赛结束时间. 用于计算提供的装备经验 */
    private long lastMatchEndTime;

    /** 锦标赛. 当日最后一场比赛开始时间 */
    private long championshipLastMatchStartTime;
    /** 锦标赛. 当日最后一场比赛开始时间 */
    private LocalDate championshipLastMatchStartDate;
    /** 锦标赛. 随机种子 */
    private long championshipRndSeed;
    /** 锦标赛. 当前关卡 */
    private int championshipLevelRid;
    /** 锦标赛. 比赛胜利次数(进度) */
    private int championshipWinNum;
    /** 锦标赛. 当前对手列表 */
    private List<ChampionshipTarget> championshipTargets = Collections.emptyList();
    //临时数据
    /** 锦标赛, 每一层获胜的球队(不包括本球队), 所以内层 index 为 0 的就是当前对手 */
    private transient List<List<ChampionshipMatchTemp>> tempChampionshipWins = Collections.emptyList();

    public MainMatch() {
    }

    public MainMatch(long teamId) {
        this.teamId = teamId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getMatchNum() {
        return matchNum;
    }

    public void setMatchNum(int matchNum) {
        this.matchNum = matchNum;
    }

    public long getMatchNumLastUpTime() {
        return matchNumLastUpTime;
    }

    public void setMatchNumLastUpTime(long matchNumLastUpTime) {
        this.matchNumLastUpTime = matchNumLastUpTime;
    }

    public int getLastLevelRid() {
        return lastLevelRid;
    }

    public void setLastLevelRid(int lastLevelRid) {
        this.lastLevelRid = lastLevelRid;
    }

    public long getLastMatchEndTime() {
        return lastMatchEndTime;
    }

    public void setLastMatchEndTime(long lastMatchEndTime) {
        this.lastMatchEndTime = lastMatchEndTime;
    }

    public long getChampionshipLastMatchStartTime() {
        return championshipLastMatchStartTime;
    }

    public void setChampionshipLastMatchStartTime(long championshipLastMatchStartTime) {
        this.championshipLastMatchStartTime = championshipLastMatchStartTime;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(championshipLastMatchStartTime);
        this.championshipLastMatchStartDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    public LocalDate getChampionshipLastMatchStartDate() {
        return championshipLastMatchStartDate;
    }

    public long getChampionshipRndSeed() {
        return championshipRndSeed;
    }

    public void setChampionshipRndSeed(long championshipRndSeed) {
        this.championshipRndSeed = championshipRndSeed;
    }

    public int getChampionshipLevelRid() {
        return championshipLevelRid;
    }

    public void setChampionshipLevelRid(int championshipLevelRid) {
        this.championshipLevelRid = championshipLevelRid;
    }

    public int getChampionshipWinNum() {
        return championshipWinNum;
    }

    public void setChampionshipWinNum(int championshipWinNum) {
        this.championshipWinNum = championshipWinNum;
    }

    public List<ChampionshipTarget> getChampionshipTargets() {
        return championshipTargets;
    }

    private String getChampionshipTargetStr() {
        StringBuilder sb = new StringBuilder();
        for (ChampionshipTarget npc : championshipTargets) {
            sb.append(npc.getTeamId()).append(StringUtil.ST);
        }
        return sb.toString();
    }

    public void setChampionshipTargets(String str) {
        long[] ids = StringUtil.toLongArray(str, StringUtil.ST);
        this.championshipTargets = Arrays.stream(ids).mapToObj(teamId -> new ChampionshipTarget(teamId))
                .collect(Collectors.toList());
    }

    public void setChampionshipTargets(List<ChampionshipTarget> championshipTargets) {
        this.championshipTargets = championshipTargets;
    }

    public int getChampionshipTargetSize() {
        return championshipTargets != null ? championshipTargets.size() : 0;
    }

    public List<List<ChampionshipMatchTemp>> getTempChampionshipWins() {
        return tempChampionshipWins;
    }

    public void setTempChampionshipWins(List<List<ChampionshipMatchTemp>> tempChampionshipWins) {
        this.tempChampionshipWins = tempChampionshipWins;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(teamId, matchNum, matchNumLastUpTime,
                lastLevelRid, lastMatchEndTime,
                championshipLastMatchStartTime, championshipRndSeed, championshipLevelRid, championshipWinNum, getChampionshipTargetStr());
    }

    @Override
    public String getRowNames() {
        return "team_id, m_num, m_num_time," +
                " last_lev_rid, last_match_time," +
                " cs_time, cs_seed, cs_lev_rid, cs_win, cs_targets";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(teamId, matchNum, matchNumLastUpTime,
            lastLevelRid, lastMatchEndTime,
            championshipLastMatchStartTime, championshipRndSeed, championshipLevelRid, championshipWinNum, getChampionshipTargetStr());
    }

    @Override
    public String getTableName() {
        return "t_u_mmatch_t";
    }

    @Override
    public void del() {
    }

    @Override
    public synchronized void save() {
        super.save();
    }



    /** 锦标赛对手信息(包括自己) */
    public static final class ChampionshipTarget implements Serializable {
        private static final long serialVersionUID = 808857352000944579L;
        /** npc或者上榜的球队id */
        private long teamId;
        /** 真实npcid */
        private long npc;

        public ChampionshipTarget() {
        }

        public ChampionshipTarget(long teamId) {
            this.teamId = teamId;
        }

        public ChampionshipTarget(long teamId, long npc) {
            this.teamId = teamId;
            this.npc = npc;
        }

        public long getTeamId() {
            return teamId;
        }

        public void setTeamId(long teamId) {
            this.teamId = teamId;
        }

        public long getNpc() {
            return npc;
        }

        public void setNpc(long npc) {
            this.npc = npc;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"tid\":" + teamId +
                    ", \"npc\":" + npc +
                    '}';
        }
    }

    /** 锦标赛每场比赛双方信息 */
    public static final class ChampionshipMatchTemp {
        /** 主场 */
        private final ChampionshipTarget home;
        /** 客场 */
        private final ChampionshipTarget away;

        public ChampionshipMatchTemp(ChampionshipTarget home, ChampionshipTarget away) {
            this.home = home;
            this.away = away;
        }

        public ChampionshipTarget getHome() {
            return home;
        }

        public ChampionshipTarget getAway() {
            return away;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"home\":" + home +
                    ", \"away\":" + away +
                    '}';
        }
    }
}
