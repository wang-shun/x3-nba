package com.ftkj.cfg;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.BinarySearch;
import com.ftkj.util.IntervalInt;
import com.ftkj.util.excel.RowData;

import java.util.List;

/**
 * 竞技场. 个人排名竞技.
 *
 * @author luch
 */
public class ArenaBean {
    public static final class RankAwardBean {
        private final IntervalInt rank;
        /** 起始排名 */
        private final int min;
        /** 结束排名 */
        private final int max;
        /** 刷新对手时 刷新向上浮动区间 */
        private final int targetRankMin;
        /** 刷新对手时 刷新向下浮动区间 */
        private final int targetRankMax;
        /** 最高排名固定奖励 */
        private final List<PropSimple> maxRankAward;
        /** 最高排名每提升1名对应奖励多少道具数量(道具id在全局配置) */
        private final float maxRankCurr;
        /** 每日排名奖励 */
        private final List<PropSimple> rankAward;
        /** 单场比赛胜利额外排名奖励(按自己和对方大的排名算) */
        private final List<PropSimple> matchWinAward;
        /** 单场比赛失败额外奖励(按自己的排名算) */
        private final List<PropSimple> matchLoseAward;

        public RankAwardBean(int min,
                             int max,
                             int targetRankMin, int targetRankMax, List<PropSimple> maxRankAward,
                             float maxRankCurr,
                             List<PropSimple> rankAward,
                             List<PropSimple> matchWinAward,
                             List<PropSimple> matchLoseAward) {
            this.targetRankMin = targetRankMin;
            this.targetRankMax = targetRankMax;
            this.matchWinAward = matchWinAward;
            this.matchLoseAward = matchLoseAward;
            this.rank = new IntervalInt(min, max);
            this.min = min;
            this.max = max;
            this.maxRankAward = maxRankAward;
            this.maxRankCurr = maxRankCurr;
            this.rankAward = rankAward;
        }

        /** 比较器 */
        public static final BinarySearch.Comparator<RankAwardBean, Integer> comparator =
                (o, key) -> IntervalInt.compare(o.getRank(), key);

        public IntervalInt getRank() {
            return rank;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public int getTargetRankMin() {
            return targetRankMin;
        }

        public int getTargetRankMax() {
            return targetRankMax;
        }

        public List<PropSimple> getMaxRankAward() {
            return maxRankAward;
        }

        public float getMaxRankCurr() {
            return maxRankCurr;
        }

        public List<PropSimple> getRankAward() {
            return rankAward;
        }

        public List<PropSimple> getMatchWinAward() {
            return matchWinAward;
        }

        public List<PropSimple> getMatchLoseAward() {
            return matchLoseAward;
        }

        public static final class Builder extends AbstractExcelBean {
            /** 起始排名 */
            private int min;
            /** 结束排名 */
            private int max;
            /** 刷新对手时 刷新向上浮动区间 */
            private int targetRankMin;
            /** 刷新对手时 刷新向下浮动区间 */
            private int targetRankMax;
            /** 最高排名固定奖励 */
            private String maxRankAward;
            /** 最高排名每提升1名对应奖励多少道具数量(道具id在全局配置) */
            private float maxRankCurr;
            /** 每日排名奖励 */
            private String rankAward;
            /** 单场比赛胜利额外排名奖励(按自己和对方大的排名算) */
            private String matchWinAward;
            /** 单场比赛失败额外奖励(按自己的排名算) */
            private String matchLoseAward;

            @Override
            public void initExec(RowData row) {
            }

            public RankAwardBean build() {
                return new RankAwardBean(min, max,
                        targetRankMin, targetRankMax,
                        PropSimple.parseItems(maxRankAward),
                        maxRankCurr,
                        PropSimple.parseItems(rankAward),
                        PropSimple.parseItems(matchWinAward),
                        PropSimple.parseItems(matchLoseAward));
            }
        }
    }

    public static final class NpcRankBean {
        private final int rank;
        private final long npcId;

        public NpcRankBean(int rank, long npcId) {
            this.rank = rank;
            this.npcId = npcId;
        }

        public int getRank() {
            return rank;
        }

        public long getNpcId() {
            return npcId;
        }

        public static final class Builder extends AbstractExcelBean {
            private int rank;
            private int npcId;

            @Override
            public void initExec(RowData row) {

            }

            public int getRank() {
                return rank;
            }

            public NpcRankBean build() {
                return new NpcRankBean(rank, npcId);
            }
        }
    }
}
