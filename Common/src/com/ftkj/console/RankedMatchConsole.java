package com.ftkj.console;

import com.ftkj.cfg.RankedMatchBean.FirstAwardBean;
import com.ftkj.cfg.RankedMatchBean.RankedMatchSeasonBean;
import com.ftkj.cfg.RankedMatchBean.RatingConvertBean;
import com.ftkj.cfg.RankedMatchBean.WinningStreakBean;
import com.ftkj.cfg.RankedMatchMedalBean;
import com.ftkj.cfg.RankedMatchTierBean;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.util.BinarySearch;
import com.ftkj.util.IntervalInt;
import com.ftkj.util.ListsUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 天梯赛.
 */
public class RankedMatchConsole extends AbstractConsole implements ValidateBean {
    private static final Logger log = LoggerFactory.getLogger(RankedMatchConsole.class);
    /** 赛季首次奖励配置id(1至64) */
    public static final int First_Award_Id_Min = 1;
    /** 赛季首次奖励配置id(1至64) */
    public static final int First_Award_Id_Max = 64;
    /** 段位配置. map[段位id, Bean] */
    private static Map<Integer, RankedMatchMedalBean> medals = Collections.emptyMap();
    /** 层级配置. map[层级id, Bean] */
    private static Map<Integer, RankedMatchTierBean> tiers = Collections.emptyMap();
    /** 赛季配置. map[赛季id, Bean] */
    private static Map<Integer, RankedMatchSeasonBean> seasons = Collections.emptyMap();
    private static List<RankedMatchSeasonBean> seasonArrays = Collections.emptyList();
    /** 赛季首次奖励配置. map[id, Bean] */
    private static Map<Integer, FirstAwardBean> firstAwards = Collections.emptyMap();
    /** 赛季起始积分转换规则 */
    private static List<RatingConvertBean> ratingConverts = Collections.emptyList();
    /** 连胜系数 */
    private static List<WinningStreakBean> winningStreaks = Collections.emptyList();

    public static void init() {
        Map<Integer, RankedMatchMedalBean> medals = new LinkedHashMap<>();
        Map<Integer, RankedMatchTierBean> tiers = new LinkedHashMap<>();
        Map<Integer, RankedMatchSeasonBean> seasons = new LinkedHashMap<>();
        List<RankedMatchSeasonBean> seasonArrays = new ArrayList<>();
        Map<Integer, FirstAwardBean> firstAwards = new LinkedHashMap<>();

        for (RankedMatchMedalBean medal : CM.rMatchMedals) {
            medals.put(medal.getId(), medal);
        }
        for (RankedMatchTierBean tier : CM.rMatchTiers) {
            tiers.put(tier.getId(), tier);
        }
        for (RankedMatchSeasonBean season : CM.rMatchSeasons) {
            seasons.put(season.getId(), season);
            seasonArrays.add(season);
        }
        for (FirstAwardBean fa : CM.rMatchFirstAwards) {
            firstAwards.put(fa.getId(), fa);
        }
        //
        for (RankedMatchTierBean tier : tiers.values()) {
            if (tier.getNextId() > 0) {
                tiers.get(tier.getNextId())
                        .setPreId(tier.getId());
            }
        }
        for (RankedMatchMedalBean medal : medals.values()) {
            if (medal.getNextId() > 0) {
                medals.get(medal.getNextId())
                        .setPreId(medal.getId());
            }
        }
        for (RankedMatchSeasonBean season : seasons.values()) {
            if (season.getNextId() > 0) {
                seasons.get(season.getNextId())
                        .setPreId(season.getId());
            }
        }

        CM.rMatchRatingConverts.sort(Comparator.comparing(RatingConvertBean::getRating));
        CM.rMatchWinningStreaks.sort(Comparator.comparing(WinningStreakBean::getNum));
        seasonArrays.sort((o1, o2) -> IntervalInt.compare(o1.getStart(), o1.getEnd(), o2.getEnd()));

        RankedMatchConsole.medals = ImmutableMap.copyOf(medals);
        RankedMatchConsole.tiers = ImmutableMap.copyOf(tiers);
        RankedMatchConsole.seasons = ImmutableMap.copyOf(seasons);
        RankedMatchConsole.seasonArrays = ImmutableList.copyOf(seasonArrays);
        RankedMatchConsole.firstAwards = ImmutableMap.copyOf(firstAwards);
        RankedMatchConsole.ratingConverts = ImmutableList.copyOf(CM.rMatchRatingConverts);
        RankedMatchConsole.winningStreaks = ImmutableList.copyOf(CM.rMatchWinningStreaks);
        log.debug("medals {} tiers {} seasons {} fa {} rc {} ws {}", medals.size(), tiers.size(), seasons.size(),
                firstAwards.size(), ratingConverts.size(), winningStreaks.size());
    }

    @Override
    public void validate() {
        for (RankedMatchMedalBean medal : medals.values()) {
            validate(medal);
        }
        for (RankedMatchTierBean tiers : tiers.values()) {
            validate(tiers);
        }
        for (RankedMatchSeasonBean season : seasons.values()) {
            validate(season);
        }
        for (FirstAwardBean fa : firstAwards.values()) {
            validate(fa);
        }
        for (int i = 0; i < ratingConverts.size() - 1; i++) {
            RatingConvertBean rc = ratingConverts.get(i);
            RatingConvertBean next = ratingConverts.get(i + 1);
            IntervalInt currII = rc.getRating();
            IntervalInt nextII = next.getRating();
            if (currII.getUpper() + 1 != nextII.getLower()) {
                throw exception("天梯赛. 积分转换 %s - %s 和下一个 %s - %s 之间不连续",
                        currII.getLower(), currII.getUpper(), nextII.getLower(), nextII.getUpper());
            }
        }
        for (int i = 0; i < winningStreaks.size() - 1; i++) {
            WinningStreakBean rc = winningStreaks.get(i);
            WinningStreakBean next = winningStreaks.get(i + 1);
            IntervalInt currII = rc.getNum();
            IntervalInt nextII = next.getNum();
            if (currII.getUpper() + 1 != nextII.getLower()) {
                throw exception("天梯赛. 连胜系数 %s - %s 和下一个 %s - %s 之间不连续",
                        currII.getLower(), currII.getUpper(), nextII.getLower(), nextII.getUpper());
            }
        }
    }

    private void validate(RankedMatchMedalBean medal) {
        int id = medal.getId();
        DropConsole.validate(medal.getSeasonDrop(), "天梯赛. 段位 %s 赛季段位奖励", id);
        DropConsole.validate(medal.getDailyDrop(), "天梯赛. 段位 %s 每日段位奖励", id);
        if (medal.getNextId() > 0 && getMedal(medal.getNextId()) == null) {
            throw exception("天梯赛. 段位 %s 下一个段位 %s 没有配置", id, medal.getNextId());
        }
    }

    private void validate(RankedMatchTierBean tiers) {
        int id = tiers.getId();
        //        DropConsole.validate(tiers.getSeasonDrop(), "天梯赛. 段位 %s 赛季段位奖励", id);
        //        DropConsole.validate(tiers.getDailyDrop(), "天梯赛. 段位 %s 每日段位奖励", id);
        if (tiers.getNextId() > 0 && getTier(tiers.getNextId()) == null) {
            throw exception("天梯赛. 层级 %s 下一层级 %s 没有配置", id, tiers.getNextId());
        }
    }

    private void validate(RankedMatchSeasonBean season) {
        int id = season.getId();
        if (season.getNextId() > 0) {
            RankedMatchSeasonBean next = getSeason(season.getNextId());
            if (next == null) {
                throw exception("天梯赛. 赛季 %s 下一个赛季 %s 没有配置", id, season.getNextId());
            }
            if (season.getEndLdt().isAfter(next.getStartLdt())) {
                throw exception("天梯赛. 赛季 %s 结束时间 %s 配置错误, 在下个赛季的开始时间之后", id, season.getEndLdt());
            }
        }
    }

    private void validate(FirstAwardBean fa) {
        int id = fa.getId();
        if (id < First_Award_Id_Min || id > First_Award_Id_Max) {
            throw exception("天梯赛. 赛季首次奖励id %s 必须在 1 - 64", id);
        }
        DropConsole.validate(fa.getDrop(), "天梯赛. 赛季首次奖励 %s 的奖励", id);
    }

    public static RankedMatchTierBean getTier(int rid) {
        return tiers.get(rid);
    }

    public static RankedMatchTierBean getTierByRating(int rating) {
        RankedMatchTierBean target = null;
        for (RankedMatchTierBean tb : tiers.values()) {
            if (tb.getMinRating() > rating) {
                break;
            }
            target = tb;
        }
        return target;
    }

    public static Map<Integer, RankedMatchTierBean> getTiers() {
        return tiers;
    }

    /** 根据目标评分获取比当前段位低(<=)的段位 */
    public static RankedMatchTierBean getLowerTier(int currTier, int targetRating) {
        RankedMatchTierBean rmmb = getTier(currTier);
        if (rmmb == null) {
            return null;
        }
        while (rmmb.getMinRating() > targetRating) {
            RankedMatchTierBean pre = getTier(rmmb.getPreId());
            if (pre == null) {
                break;
            }
            rmmb = pre;
        }
        return rmmb;
    }

    /** 根据目标评分获取比当前段位高(>=)的段位 */
    public static RankedMatchTierBean getHigherTier(int currTier, int targetRating) {
        RankedMatchTierBean rmmb = getTier(currTier);
        if (rmmb == null) {
            return null;
        }
        while (rmmb.getMinRating() < targetRating) {
            RankedMatchTierBean next = getTier(rmmb.getNextId());
            if (next == null || next.getMinRating() > targetRating) {
                break;
            }
            rmmb = next;
        }
        return rmmb;
    }

    public static Set<Integer> getMedalIds() {
        return medals.keySet();
    }

    public static Map<Integer, RankedMatchMedalBean> getMedals() {
        return medals;
    }

    public static RankedMatchMedalBean getMedal(int mid) {
        return medals.get(mid);
    }

    public static RankedMatchSeasonBean getSeason(int rid) {
        return seasons.get(rid);
    }

    public static Map<Integer, RankedMatchSeasonBean> getSeasons() {
        return seasons;
    }

    public static Map<Integer, FirstAwardBean> getFirstAwards() {
        return firstAwards;
    }

    public static FirstAwardBean getFirstAward(int rid) {
        return firstAwards.get(rid);
    }

    public static List<RatingConvertBean> getRatingConverts() {
        return ratingConverts;
    }

    public static RatingConvertBean getRatingConverts(int rating) {
        int idx = BinarySearch.binarySearch(ratingConverts, rating, (tp, key) -> IntervalInt.compare(tp.getRating(), key));
        if (idx < 0) {
            return null;
        }
        return ListsUtil.get(ratingConverts, idx);
    }

    public static List<WinningStreakBean> getWinningStreaks() {
        return winningStreaks;
    }

    /** 获取连胜系数 */
    public static float getWinningStreak(int winningStreak, float defaultValue) {
        int idx = BinarySearch.binarySearch(winningStreaks, winningStreak, (e, key) -> IntervalInt.compare(e.getNum(), key));
        if (idx < 0) {
            return defaultValue;
        }
        WinningStreakBean wsb = ListsUtil.get(winningStreaks, idx);
        return wsb != null ? wsb.getFactor() : defaultValue;
    }

    /** 获取当前赛季 */
    public static RankedMatchSeasonBean getCurrSeason(long currMillis) {
        int idx = BinarySearch.binarySearch(seasonArrays, currMillis, (tp, key) -> {
            int ret = IntervalInt.compare(tp.getStart(), tp.getEnd(), key);
            //            System.out.println("key " + key + " tp " + tp + " ret " + ret);
            return ret;
        });
        if (idx < 0) {
            return null;
        }
        return ListsUtil.get(seasonArrays, idx);
    }

    /** 获取当前或者最后一个赛季 */
    public static RankedMatchSeasonBean getCurrOrNextSeason(long curr) {
        int idx = BinarySearch.binarySearch(seasonArrays, curr, (tp, key) -> IntervalInt.compare(tp.getStart(), tp.getEnd(), key));
        final int insertIdx = idx < 0 ? -idx - 1 : idx;
        return ListsUtil.get(seasonArrays, insertIdx);
    }

    public static List<RankedMatchSeasonBean> getSeasonArrays() {
        return seasonArrays;
    }
}
