package com.ftkj.console;

import com.ftkj.cfg.ArenaBean.NpcRankBean;
import com.ftkj.cfg.ArenaBean.RankAwardBean;
import com.ftkj.cfg.ArenaBean.RankAwardBean.Builder;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.manager.arena.Arena;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.BinarySearch;
import com.ftkj.util.IntervalInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 竞技场. 个人排名竞技.
 *
 * @author luch
 */
public class ArenaConsole extends AbstractConsole implements ValidateBean {
    private static final Logger log = LoggerFactory.getLogger(ArenaConsole.class);
    private static List<RankAwardBean> ranks = Collections.emptyList();
    private static Map<Integer, NpcRankBean> npcs = Collections.emptyMap();

    public static void init() {
        List<RankAwardBean> ranks = CM.arenaRanks.stream().map(Builder::build)
                .sorted((o1, o2) ->
                        IntervalInt.compare(o1.getRank(), o2.getRank().getLower())).collect(toImmutableList());
        Map<Integer, NpcRankBean> npcs = toMap(CM.arenaNpcs, b -> b.getRank(), b -> b.build());

        ArenaConsole.ranks = ranks;
        ArenaConsole.npcs = npcs;
    }

    /** 根据排名获取npc配置id */
    public static Long getNpcByRank(Integer rank) {
        NpcRankBean npc = npcs.get(rank);
        return npc != null ? npc.getNpcId() : null;
    }

    /** 获取最高排名变化奖励 */
    public static Map<Integer, PropSimple> getChangeRankReward(final int oldMaxRank, final int newMaxRank, final int maxRankCurrItemId) {
        if (oldMaxRank <= 0 || newMaxRank <= 0 || oldMaxRank < newMaxRank) {
            return Collections.emptyMap();
        }
        final Map<Integer, PropSimple> props = new HashMap<>();
        float curr = 0;
        int startRank = oldMaxRank;
        log.debug("old {} new {} start {}", oldMaxRank, newMaxRank, startRank);
        while (startRank >= newMaxRank) {
            RankAwardBean award = getAwardByRank(startRank);
            if (award == null) {
                break;
            }
            int min = Math.max(newMaxRank, award.getRank().getLower());
            int max = Math.min(startRank, award.getRank().getUpper());
            if (log.isDebugEnabled()) {
                log.debug("start {} from {} to {} {}", startRank, max, min, max - min);
            }
            for (int i = max; i >= min; i--) {
                PropSimple.mergeProps(award.getMaxRankAward(), props);
            }
            curr += award.getMaxRankCurr() * Math.max(0, max - min);
            startRank = award.getRank().getLower() - 1;
        }
        PropSimple.mergeProps(new PropSimple(maxRankCurrItemId, Math.round(curr)), props);
        return props;
    }

    public static RankAwardBean getAwardByRank(int rank) {
        if (rank < 0) {
            return null;
        }
        int index = BinarySearch.binarySearch(ranks, rank, RankAwardBean.comparator);
        if (index < 0) {
            return null;
        }
        return ranks.get(index);
    }

    /** 根据排名获取比赛胜利奖励 */
    public static List<PropSimple> getWinAwardByRank(int rank) {
        RankAwardBean award = getAwardByRank(rank);
        return award != null ? award.getMatchWinAward() : Collections.emptyList();
    }

    /** 根据排名获取比赛失败奖励 */
    public static List<PropSimple> getLoseAwardByRank(int rank) {
        RankAwardBean award = getAwardByRank(rank);
        return award != null ? award.getMatchLoseAward() : Collections.emptyList();
    }

    @Override
    public void validate() {
        for (int i = 1; i <= Arena.Max_Rank_Size; i++) {
            NpcRankBean npc = npcs.get(i);
            if (npc == null) {
                throw exception("竞技场. 排名 %s 的机器人npc没有配置", i);
            }
            if (NPCConsole.getNPC(npc.getNpcId()) == null) {
                throw exception("竞技场. 排名 %s 的机器人npc %s 没有配置", i, npc.getNpcId());
            }
        }

        int maxrank = ranks.get(0).getMax();
        for (int i = 0; i < ranks.size(); i++) {
            RankAwardBean award = ranks.get(i);
            validate(award);
            if (i > 0 && maxrank + 1 != award.getMin()) {
                throw exception("竞技场. 排名 %s - %s 和上一个区间不连续", award.getMin(), award.getMax());
            }
            maxrank = award.getMax();
        }
    }

    private void validate(RankAwardBean ab) {
        int min = ab.getMin();
        int max = ab.getMax();
        PropConsole.validate(ab.getMaxRankAward(), "竞技场. 排名 %s - %s. 最高排名固定奖励 ", min, max);
        PropConsole.validate(ab.getRankAward(), "竞技场. 排名 %s - %s. 每日排名奖励 ", min, max);
        PropConsole.validate(ab.getMatchWinAward(), "竞技场. 排名 %s - %s. 比赛胜利奖励 ", min, max);
        PropConsole.validate(ab.getMatchLoseAward(), "竞技场. 排名 %s - %s. 比赛失败奖励 ", min, max);
    }
}
