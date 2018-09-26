package com.ftkj.manager.arena;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 竞技场. 个人排名竞技.
 *
 * @author luch
 */
public class Arena extends AsynchronousBatchDB implements Serializable {
    private static final long serialVersionUID = -9018280024551799209L;
    /** 竞技场排名信息只在内存中保留固定个数, 节约内存. 大于此个数的没有排名 */
    public static final int Max_Rank_Size = 5000;
    /** 对手前N位固定 */
    public static final int Target_Top_Fixed_Num = 10;
    private long teamId;
    /** 竞技场排名. 越小越大 */
    private int rank;
    /** 历史最大排名. 越小越大 */
    private int maxRank;
    //    /** 已经使用免费战斗次数 */
    //    private int useMatchNum;
    /** 上次比赛时间(毫秒) */
    private long preMatchTime;
    /** 最后一次结算时的排名 */
    private int lastRank;
    //==============  统计信息. 累计获取的, 累计消耗的
    /** 总比赛场数 */
    private long totalMatchCount;
    /** 总胜利场数 */
    private long totalWinCount;

    //临时信息
    /** 对手 (临时). map[tid, rank] */
    private Map<Long, Integer> targets = Collections.emptyMap();
    /** 上次刷新对手时间 */
    private long tempPreRefreshOpponentTime;
    /** 和排名比自己高的比赛的时间 */
    private long tempGtRankTargetMatchTime;

    public Arena() {
    }

    public Arena(long teamId, int rank, int maxRank) {
        this.teamId = teamId;
        this.rank = rank;
        this.maxRank = maxRank;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getRank() {
        return rank;
    }

    public int getRankOrLast() {
        return rank > 0 ? rank : Max_Rank_Size;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getMaxRank() {
        return maxRank;
    }

    public int getMaxRankOrLast() {
        return maxRank > 0 ? maxRank : Max_Rank_Size;
    }

    public void setMaxRank(int maxRank) {
        this.maxRank = maxRank;
    }

    public long getPreMatchTime() {
        return preMatchTime;
    }

    public void setPreMatchTime(long preMatchTime) {
        this.preMatchTime = preMatchTime;
    }

    public long getTotalMatchCount() {
        return totalMatchCount;
    }

    public void setTotalMatchCount(long totalMatchCount) {
        this.totalMatchCount = totalMatchCount;
    }

    public long getTotalWinCount() {
        return totalWinCount;
    }

    public void setTotalWinCount(long totalWinCount) {
        this.totalWinCount = totalWinCount;
    }

    public Map<Long, Integer> getTargets() {
        return targets;
    }

    public void setTargets(Map<Long, Integer> targets) {
        this.targets = targets;
    }

    public long getTempPreRefreshOpponentTime() {
        return tempPreRefreshOpponentTime;
    }

    public void setTempPreRefreshOpponentTime(long tempPreRefreshOpponentTime) {
        this.tempPreRefreshOpponentTime = tempPreRefreshOpponentTime;
    }

    public long getTempGtRankTargetMatchTime() {
        return tempGtRankTargetMatchTime;
    }

    public void setTempGtRankTargetMatchTime(long tempGtRankTargetMatchTime) {
        this.tempGtRankTargetMatchTime = tempGtRankTargetMatchTime;
    }

    public int getLastRank() {
        return lastRank;
    }

    public void setLastRank(int lastRank) {
        this.lastRank = lastRank;
    }

    //    /** 是否是有效排名索引 */
    //    public static boolean isNormalRankIdx(int rankIdx) {
    //        return rankIdx >= 0 && rankIdx < Arena.Max_Rank_Size;
    //    }
    //
    //    /** 是否是有效排名 */
    //    public boolean isNormalRank() {
    //        return rank > 0 && rank <= Arena.Max_Rank_Size;
    //    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(teamId, rank, maxRank, preMatchTime, lastRank, totalMatchCount, totalWinCount);
    }

    @Override
    public String getRowNames() {
        return "team_id, rank, max_rank, match_time, last_rank, t_m_c, t_w_c";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(teamId, rank, maxRank, preMatchTime, lastRank, totalMatchCount, totalWinCount);
    }

    @Override
    public String getTableName() {
        return "t_u_r_arena";
    }

    @Override
    public void save() {
        super.save();
    }

    @Override
    public void del() {
    }

    public static void main(String[] args) {
        Arena a = new Arena();
        System.err.println(a.getSource());
    }

}
