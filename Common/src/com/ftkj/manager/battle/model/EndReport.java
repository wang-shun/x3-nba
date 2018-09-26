package com.ftkj.manager.battle.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.tuple.Tuple2;

/**
 * @author tim.huang 2017年2月24日 比赛结束报表
 */
public class EndReport implements Serializable {
    private static final long serialVersionUID = 2L;
    private final long battleId;
    private final EBattleType battleType;
    private TeamReport home;
    private TeamReport away;
    //结束奖励，本服发奖
    private Map<Integer, PropSimple> winAwards;
    private Map<Integer, PropSimple> loseAwards;
    private long winTeamId;
    /** 附加信息 */
    private Map<EBattleAttribute, Serializable> additional = new HashMap<>();

    public EndReport(long battleId,
                     EBattleType battleType,
                     BattleTeam home,
                     BattleTeam away,
                     List<PropSimple> winAwards,
                     List<PropSimple> loseAwards,
                     long winTeam) {
        this.battleId = battleId;
        this.battleType = battleType;
        this.home = new TeamReport(home);
        this.away = new TeamReport(away);
        this.winTeamId = winTeam;
        //同类物品，数量叠加
        this.winAwards = PropSimple.mergeProps(winAwards);
        this.loseAwards = PropSimple.mergeProps(loseAwards);
    }

    public static final class TeamReport implements Serializable {
        private static final long serialVersionUID = -3539068073395274071L;
        private final long teamId;
        private final String name;
        private final String logo;
        private int score;
        private final Map<Integer, PlayerActStat> sources;
        private final List<PlayerActStat> sourceLists;
        private final int mvpPlayerId;
        /** mvp评分 */
        private final float mvpScore;

        public TeamReport(BattleTeam bt) {
            this.teamId = bt.getTeamId();
            this.name = bt.getName();
            this.logo = bt.getLogo();
            this.score = bt.getScore();
            this.sources = getPlayerPks(bt);
            this.sourceLists = bt.getPlayers().stream()
                .map(BattlePlayer::getRealTimeActionStats)
                .collect(Collectors.toList());
            Tuple2<Integer, Float> mvp = calcMvp(sources.values());
            mvpPlayerId = mvp._1();
            mvpScore = mvp._2();
        }

        public TeamReport(TeamReport tt) {
            this(tt, tt.getScore());
        }

        public TeamReport(TeamReport tt, int score) {
            this.teamId = tt.getTeamId();
            this.name = tt.getName();
            this.logo = tt.getLogo();
            this.score = score;
            this.sources = tt.getSources();
            this.sourceLists = tt.getSourceLists();
            this.mvpPlayerId = tt.getMvpPlayerId();
            this.mvpScore = tt.getMvpScore();
        }

        public static Map<Integer, PlayerActStat> getPlayerPks(BattleTeam bt) {
            Map<Integer, PlayerActStat> map = new HashMap<>();
            for (BattlePlayer battlePlayer : bt.getPlayers()) {
                PlayerActStat realTimeActionStats = battlePlayer.getRealTimeActionStats();
                map.put(realTimeActionStats.getPlayerRid(), realTimeActionStats);
            }
            return map;
        }

        public long getTeamId() {
            return teamId;
        }

        public String getName() {
            return name;
        }

        public String getLogo() {
            return logo;
        }

        public int getScore() {
            return score;
        }
        
        public void setScore(int score){
        	this.score = score;
        }

        public Map<Integer, PlayerActStat> getSources() {
            return sources;
        }

        public List<PlayerActStat> getSourceLists() {
            return sourceLists;
        }

        public int getMvpPlayerId() {
            return mvpPlayerId;
        }

        public float getMvpScore() {
            return mvpScore;
        }
    }

    public long getBattleId() {
        return battleId;
    }

    public EBattleType getBattleType() {
        return battleType;
    }

    public long getHomeTeamId() {
        return home.teamId;
    }

    public long getAwayTeamId() {
        return away.teamId;
    }

    public int getHomeScore() {
        return home.score;
    }

    public int getAwayScore() {
        return away.score;
    }

    public long getWinTeamId() {
        return winTeamId;
    }

    public TeamReport getHome() {
        return home;
    }

    public void setHome(TeamReport home) {
        this.home = home;
    }

    public TeamReport getAway() {
        return away;
    }

    public void setAway(TeamReport away) {
        this.away = away;
    }

    public boolean isHomeWin() {
        return winTeamId == home.teamId;
    }

    public boolean isAwayWin() {
        return winTeamId == away.teamId;
    }

    public void setWinTeamId(long winTeamId) {
        this.winTeamId = winTeamId;
    }

    public List<PropSimple> getAwardList(long teamId) {
        if (teamId != home.teamId && teamId != away.teamId) {
            return Collections.emptyList();
        }
        if (teamId == winTeamId) {
            return getWinAwardList();
        } else {
            return getLoseAwards();
        }
    }

    public Map<Integer, PropSimple> getAwards(long teamId) {
        if (teamId != home.teamId && teamId != away.teamId) {
            return Collections.emptyMap();
        }
        if (teamId == winTeamId) {
            return winAwards;
        } else {
            return loseAwards;
        }
    }

    public List<PropSimple> getWinAwardList() {
        return new ArrayList<>(winAwards.values());
    }

    public Map<Integer, PropSimple> getWinAwards() {
        return winAwards;
    }

    public List<PropSimple> getLoseAwards() {
        return new ArrayList<>(loseAwards.values());
    }

    public void appendHomeAwardList(List<PropSimple> props) {
        if (isHomeWin()) {
            appendWinAwardList(props);
        } else {
            appendLossAwardList(props);
        }
    }

    public void appendAwayAwardList(List<PropSimple> props) {
        if (isAwayWin()) {
            appendWinAwardList(props);
        } else {
            appendLossAwardList(props);
        }
    }

    public void appendWinAwardList(List<PropSimple> props) {
        for (PropSimple ps : props) {
            appendWinAwardList(ps);
        }
    }

    private void appendWinAwardList(PropSimple ps) {
        PropSimple.mergeProps(ps, winAwards);
    }

    public void appendLossAwardList(List<PropSimple> props) {
        for (PropSimple ps : props) {
            appendLossAwardList(ps);
        }
    }

    private void appendLossAwardList(PropSimple ps) {
        PropSimple.mergeProps(ps, loseAwards);
    }

    private List<PlayerActStat> getSourcesList(boolean home) {
        return home ? this.home.sourceLists : this.away.sourceLists;
    }

    public PlayerActStat sortAndGetFirst(boolean home, EActionType act) {
        return sortAndGetFirst(getSourcesList(home), act);
    }

    private PlayerActStat sortAndGetFirst(List<PlayerActStat> sources, EActionType act) {
        return sortAndGetFirst(sources, act, (o1, o2) -> Float.compare(o2, o1));
    }

    private PlayerActStat sortAndGetFirst(List<PlayerActStat> sources,
                                          EActionType act,
                                          Comparator<Float> comparator) {
        sources.sort((a, b) ->
            comparator.compare(a.getValue(act), b.getValue(act)));
        return sources.get(0);
    }

    public PlayerActStat getMVPPlayer(boolean home) {
        return home ?
            this.home.sources.get(this.home.mvpPlayerId) :
            this.away.sources.get(this.away.mvpPlayerId);
    }

    /** 计算 mvp 球员. Tuple[playerRid, calcedScore] */
    public static Tuple2<Integer, Float> calcMvp(Collection<PlayerActStat> sources) {
        List<Tuple2<Integer, Float>> scores = sources.stream()
            .map(as -> new Tuple2<>(as.getPlayerRid(), mvpScore(as)))
            .sorted((o1, o2) -> Float.compare(o2._2(), o1._2())) //降序
            .collect(Collectors.toList());
        return scores.get(0);
    }

    /** 计算mvp评分 */
    private static float mvpScore(PlayerActStat a) {
        float amvp = a.getValue(EActionType.pts) + a.getValue(EActionType.reb) + a.getValue(EActionType.ast) +
            a.getValue(EActionType.blk) * 2f + a.getValue(EActionType.stl) * 2f +
            a.getValue(EActionType.ocap);
        return amvp;
    }

    public void setAdditional(Map<EBattleAttribute, Serializable> additional) {
        this.additional = additional;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getAdditional(EBattleAttribute key) {
        Object val = additional.get(key);
        return val != null ? (T) val : null;
    }

    public <T extends Serializable> void addAdditional(EBattleAttribute key, T val) {
        additional.put(key, val);
    }

    public boolean hasAdditional(EBattleAttribute attr) {
        return additional.containsKey(attr);
    }

    public Map<EBattleAttribute, Serializable> getAdditional() {
        return additional;
    }

    /** 全明星. 比赛结束结算信息 */
    public static final class AllStarMatchEnd implements Serializable {
        private static final long serialVersionUID = -8078518039835307971L;
        /** 比赛开始时的npc难度 */
        private final int srcLev;
        /** 比赛结束时的npc难度 */
        private final int currLev;
        /** 按比赛开始时难度造成的总伤害 */
        private final int srcTotalHp;
        /** 比赛结束时的npc难度造成的总伤害(难度发生变化, 伤害根据配置可能打折扣) */
        private final int subTotalHp;

        public AllStarMatchEnd(int srcLev, int currLev, int srcTotalHp, int subTotalHp) {
            this.srcLev = srcLev;
            this.currLev = currLev;
            this.srcTotalHp = srcTotalHp;
            this.subTotalHp = subTotalHp;
        }

        public int getSrcLev() {
            return srcLev;
        }

        public int getCurrLev() {
            return currLev;
        }

        public int getSrcTotalHp() {
            return srcTotalHp;
        }

        public int getSubTotalHp() {
            return subTotalHp;
        }

        @Override
        public String toString() {
            return "{" +
                "\"srcLev\":" + srcLev +
                ", \"currLev\":" + currLev +
                ", \"srcTotalHp\":" + srcTotalHp +
                ", \"subTotalHp\":" + subTotalHp +
                '}';
        }
    }

    /** 天梯赛. 单场比赛结束结算信息 */
    public static final class RankedMatchEnd implements Serializable {
        private static final long serialVersionUID = 6050326181500881211L;
        private final RankedTeam home = new RankedTeam();
        private final RankedTeam away = new RankedTeam();

        public RankedTeam getHome() {
            return home;
        }

        public RankedTeam getAway() {
            return away;
        }

        @Override
        public String toString() {
            return "{" +
                "\"home\":" + home +
                ", \"away\":" + away +
                '}';
        }

        /** 天梯赛比赛结束信息 */
        public static final class RankedTeam implements Serializable {
            private static final long serialVersionUID = 7726740885522181573L;
            /** 原来的层级 */
            private int oldTier;
            /** 新的层级 */
            private int newTier;
            /** 原来的评分 */
            private int srcRating;
            /** 最终评分 */
            private int finalRating;
            /** 本场比赛评分变化 */
            private int changeRating;

            public void setSrc(int oldTier, int srcRating) {
                this.oldTier = oldTier;
                this.srcRating = srcRating;
            }

            public void setAfter(int newTier, int finalRating, int changeRating) {
                this.newTier = newTier;
                this.finalRating = finalRating;
                this.changeRating = changeRating;
            }

            public int getOldTier() {
                return oldTier;
            }

            public void setOldTier(int oldTier) {
                this.oldTier = oldTier;
            }

            public int getNewTier() {
                return newTier;
            }

            public void setNewTier(int newTier) {
                this.newTier = newTier;
            }

            public int getFinalRating() {
                return finalRating;
            }

            public void setFinalRating(int finalRating) {
                this.finalRating = finalRating;
            }

            public int getChangeRating() {
                return changeRating;
            }

            public void setChangeRating(int changeRating) {
                this.changeRating = changeRating;
            }

            public int getSrcRating() {
                return srcRating;
            }

            public void setSrcRating(int srcRating) {
                this.srcRating = srcRating;
            }

            @Override
            public String toString() {
                return "{" +
                    "\"ot\":" + oldTier +
                    ", \"nt\":" + newTier +
                    ", \"sr\":" + srcRating +
                    ", \"fr\":" + finalRating +
                    ", \"cr\":" + changeRating +
                    '}';
            }
        }

    }

    /** 竞技场. 比赛结束信息 */
    public static final class ArenaMatchEnd implements Serializable {
        private static final long serialVersionUID = 3518103529266617860L;
        //比赛结束时 自己的排名. 如果比赛胜利, 并且自己的排名比对方的低, 则排名对调
        private int selfRank;
        //比赛结束时 对方的排名
        private int targetRank;
        //历史最高排名. 旧最高排名 >0时有效
        private int oldMaxRank;
        //历史最高排名. 新最高排名 >0时有效
        private int newMaxRank;
        //比赛结束时 最高排名发生变化时获得的奖励
        private int maxRankAward;

        public int getSelfRank() {
            return selfRank;
        }

        public void setSelfRank(int selfRank) {
            this.selfRank = selfRank;
        }

        public int getTargetRank() {
            return targetRank;
        }

        public void setTargetRank(int targetRank) {
            this.targetRank = targetRank;
        }

        public int getOldMaxRank() {
            return oldMaxRank;
        }

        public void setOldMaxRank(int oldMaxRank) {
            this.oldMaxRank = oldMaxRank;
        }

        public int getNewMaxRank() {
            return newMaxRank;
        }

        public void setNewMaxRank(int newMaxRank) {
            this.newMaxRank = newMaxRank;
        }

        public int getMaxRankAward() {
            return maxRankAward;
        }

        public void setMaxRankAward(int maxRankAward) {
            this.maxRankAward = maxRankAward;
        }
    }
    
    
    /** 新秀对抗赛. 比赛结束结算信息 */
    public static final class StarletMatchEnd implements Serializable {
        private static final long serialVersionUID = -8078518039835307971L;
        /** 卡组类型 */
        private final int cardType;
        /** 卡组基数 */
        private final int redixNum;

        public StarletMatchEnd(int cardType, int redixNum) {
            this.cardType = cardType;
            this.redixNum = redixNum;
        }

        @Override
        public String toString() {
            return "{" +
                "\"cardType\":" + cardType +
                ", \"redixNum\":" + redixNum +               
                '}';
        }

        public int getCardType() {
            return cardType;
        }

        public int getRedixNum() {
            return redixNum;
        }
    }
}
