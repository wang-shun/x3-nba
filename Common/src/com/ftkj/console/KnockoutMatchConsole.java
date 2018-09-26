package com.ftkj.console;

import com.ftkj.cfg.KnockoutMatchBean;
import com.ftkj.cfg.MatchRankAwardBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 多人赛配置
 *
 * @author Jay
 * @time:2017年5月17日 下午7:38:26
 */
public class KnockoutMatchConsole {
    public static final String Thread_Name = "kkmatch";
    public static final int Thread_Num = 20;
    /** 赛前提醒时间：分钟 */
    public static final int Match_Start_Tip_Min = 5;
    /** 比赛列表间隔推送比分回合数 */
    public static final int Match_Round_Push_Num = 20;

    /** 匹配完对手延迟开始比赛时间，秒 */
    public static final int Match_Ready_Time = 1;
    /** 下一轮延迟 秒 */
    public static final int Round_Delay = 1;
    /** 开始延迟 秒 */
    public static final int Start_Delay = 1;
    //    /** 匹配完对手延迟开始比赛时间，秒 */
    //    public static final int Match_Ready_Time = 20;
    //    /** 下一轮延迟 秒 */
    //    public static final int Round_Delay = 10;
    //    /** 开始延迟 秒 */
    //    public static final int Start_Delay = 5;

    /**
     * 调试开关
     */
    public static boolean debug = false;

    /**
     * NPC范围
     */
    public static final int NPC_Min_ID = 30001;
    /**
     * NPC范围
     */
    public static final int NPC_Max_ID = 30300;

    public static List<KnockoutMatchBean> matchList;
    public static Map<Integer, List<MatchRankAwardBean>> rankAwardMap;

    public static void init(List<KnockoutMatchBean> list, List<MatchRankAwardBean> matchRankAwardList) {
        matchList = list;
        rankAwardMap = Maps.newHashMap();
        for (MatchRankAwardBean award : matchRankAwardList) {
            if (!rankAwardMap.containsKey(award.getId())) {
                rankAwardMap.put(award.getId(), Lists.newArrayList());
            }
            rankAwardMap.get(award.getId()).add(award);
        }
    }

    /**
     * 更加比赛类型和排名,取多人赛奖励
     *
     * @param matchId
     * @param rank
     * @return
     */
    public static int getMatchDropByRank(int matchId, int rank) {
        if (!rankAwardMap.containsKey(matchId)) {
            return 0;
        }
        for (MatchRankAwardBean drop : rankAwardMap.get(matchId)) {
            if (rank >= drop.getMin() && rank <= drop.getMax()) {
                return drop.getDrop();
            }
        }
        return 0;
    }

    public static List<KnockoutMatchBean> getMatchList() {
        return matchList;
    }

    /**
     * 只取超快赛类型
     *
     * @return
     */
    public static List<KnockoutMatchBean> getFastMatchList() {
        return matchList.stream().filter(m -> m.getType() == 2).collect(Collectors.toList());
    }

    /**
     * 通过比赛ID查找配置
     *
     * @param id
     * @return
     */
    public static KnockoutMatchBean getMatchById(int id) {
        return matchList.stream().filter(m -> m.getMatchId() == id).findFirst().orElse(null);
    }

    public static String getName(int id) {
        return getMatchById(id).getName();
    }

    public static int getTid(int matchId) {
        return getMatchById(matchId).getTid();
    }

    /**
     * 取比赛的最大轮数
     *
     * @param id
     * @return
     */
    public static int getMatchMaxRound(int id) {
        KnockoutMatchBean match = getMatchById(id);
        if (match == null) {
            return 1;
        }
        return m(match.getMaxTeam());
    }

    /**
     * 取 2 的n次方
     *
     * @param n
     * @return
     */
    private static int m(int n) {
        if (n <= 0 || n > Math.pow(2, 50)) {
            return 1;
        } else {
            int i = 0;
            while ((int) Math.pow(2, i) != n) {
                i++;
            }
            return i;
        }
    }

    /**
     * 是否满足参数等级条件
     *
     * @param matchId
     * @param lv
     * @return
     */
    public static boolean checkTeamLevel(int matchId, int lv) {
        KnockoutMatchBean bean = getMatchById(matchId);
        if (lv >= bean.getNeedLv() && lv <= bean.getNeedMaxLv()) {
            return true;
        }
        return false;
    }
    
    /**
     * 是否满级参数战力条件
     * @param matchId
     * @param combat
     * @return
     */
    public static boolean checkTeamCombat(int matchId, int combat) {
        KnockoutMatchBean bean = getMatchById(matchId);
        if (combat >= bean.getNeedCombat() && combat <= bean.getNeedMaxCombat()) {
            return true;
        }
        return false;
    }

}
