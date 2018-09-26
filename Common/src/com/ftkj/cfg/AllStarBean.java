package com.ftkj.cfg;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ParseListColumnUtil;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple2;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple2;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.BinarySearch;
import com.ftkj.util.IntervalInt;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * 挑战全明星
 */
public class AllStarBean implements Serializable {
    private static final long serialVersionUID = -3386873873578444352L;
    public static final int Npc_Lev_Start = 1;
    /** id */
    private final int id;
    /** 球队等级要求 */
    private final int teamLev;
//    /** 开始时间. 系统启动时初始化, 动态修改无效 */
//    private final LocalTime startTime;
//    /** 结束时间. 在开始时间添加定时器, 可以动态修改. 结束时间<=开始时间不触发任何动作 */
//    private final LocalTime endTime;
//    /** 每日奖励发放时间. 在开始时间添加定时器, 可以动态修改 */
//    private final LocalTime awardTime;
//    /** 行为及扣血 */
//    private final ImmutableMap<EActionType, Integer> actHps;
    /** npc和难度. map[lev, NpcBean] */
    private final ImmutableMap<Integer, NpcBean> npcs;
    /** 排名奖励 */
    private final ImmutableList<AwardBean> awards;

    public AllStarBean(int id,
                       int teamLev,
                       ImmutableMap<Integer, NpcBean> npcs,
                       ImmutableList<AwardBean> awards) {
        this.id = id;
        this.teamLev = teamLev;
        this.npcs = npcs;
        this.awards = awards;
    }
//    public AllStarBean(int id,
//        int teamLev,
//        LocalTime startTime,
//        LocalTime endTime,
//        LocalTime awardTime,
//        ImmutableMap<EActionType, Integer> actHps,
//        ImmutableMap<Integer, NpcBean> npcs,
//        ImmutableList<AwardBean> awards) {
//      this.id = id;
//      this.teamLev = teamLev;
//      this.startTime = startTime;
//      this.endTime = endTime;
//      this.awardTime = awardTime;
//      this.actHps = actHps;
//      this.npcs = npcs;
//      this.awards = awards;
//    }

    public NpcBean getNpc(int npcLev) {
        return npcs.get(npcLev);
    }

//    public boolean canMatch(LocalTime now) {
//        return now.isAfter(startTime) && now.isBefore(endTime);
//    }
//
//    public int getActHp(EActionType act) {
//        return actHps.getOrDefault(act, 0);
//    }

    /** npc配置 */
    public static final class NpcBean implements Serializable {
        private static final long serialVersionUID = 8904157729328103211L;
        /** 关联的全明星id */
        private final int allStarId;
        /** 全明星npc配置. npc id */
        private final int npcId;
        /** 难度级别 */
        private final int lev;
        /** 最大血量 */
        private final int maxHp;
        /** 当前难度比赛结束时跨难度扣除的血量削减比例 */
        private final float xLevRate;
        /** 击杀奖励*/
        private final String killReward;
        /** 本服击杀奖励*/
        private final String killServerReward;
        
        public NpcBean(int allStarId, int npcId, int lev, int maxHp, float xLevRate,String killReward,String killServerReward) {
            this.allStarId = allStarId;
            this.npcId = npcId;
            this.lev = lev;
            this.maxHp = maxHp;
            this.xLevRate = xLevRate;
            this.killReward = killReward;
            this.killServerReward = killServerReward;
        }

        public int getAllStarId() {
            return allStarId;
        }

        public int getNpcId() {
            return npcId;
        }

        public int getLev() {
            return lev;
        }

        public int getMaxHp() {
            return maxHp;
        }

        public float getxLevRate() {
            return xLevRate;
        }

        public String getKillReward() {
          return killReward;
        }

        public String getKillServerReward() {
          return killServerReward;
        }
        
        
        
    }

    public static final class ScorePersonalAward implements Serializable {

      /**
       * 
       */
      private static final long serialVersionUID = -74057860427354586L;
      
      private final int score;
      private final List<PropSimple> rewards;
      
      public ScorePersonalAward(int score,List<PropSimple> rewards){
        this.score = score;
        this.rewards = rewards;
      }

      public int getScore() {
        return score;
      }

      public List<PropSimple> getRewards() {
        return rewards;
      }
      
    }
    
    /** 奖励配置 */
    public static final class AwardBean implements Serializable {
        private static final long serialVersionUID = 5252857954476222272L;
        /** 关联的全明星id */
        private final int allStarId;
        /** 排名区间 */
        private final IntervalInt rank;
        /** 奖励(道具) */
        private final List<PropSimple> props;
        /** 奖励(掉落) */
        private final int drop;

        AwardBean(int allStarId, IntervalInt rank, List<PropSimple> props, int drop) {
            this.allStarId = allStarId;
            this.rank = rank;
            this.props = props;
            this.drop = drop;
        }

        public int getAllStarId() {
            return allStarId;
        }

        public IntervalInt getRank() {
            return rank;
        }

        public List<PropSimple> getProps() {
            return props;
        }

        public int getDrop() {
            return drop;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"rid\":" + allStarId +
                    ", \"rank\":" + rank +
                    ", \"props\":" + props +
                    ", \"drop\":" + drop +
                    '}';
        }
    }

    public int getId() {
        return id;
    }

    public int getTeamLev() {
        return teamLev;
    }

//    public LocalTime getStartTime() {
//        return startTime;
//    }
//
//    public LocalTime getEndTime() {
//        return endTime;
//    }
//
//    public LocalTime getAwardTime() {
//        return awardTime;
//    }
//
//    public ImmutableMap<EActionType, Integer> getActHps() {
//        return actHps;
//    }

    public ImmutableMap<Integer, NpcBean> getNpcs() {
        return npcs;
    }

    public ImmutableList<AwardBean> getAwards() {
        return awards;
    }

    /** 根据排名获取奖励 */
    public AwardBean getAward(int rank) {
        int index = BinarySearch.binarySearch(awards, rank, (nc, key) ->
                IntervalInt.compare(nc.getRank(), key));
        if (index < 0) {
            return null;
        }
        return awards.get(index);
    }

    /** 全明星配置 */
    public static final class AllStarBuilder extends AbstractExcelBean {
        /** id */
        private int id;
        /** 球队等级要求 */
        private int teamLev;
        /** 开始时间 */
        private String startTime;
        /** 结束时间. 结束时间<=开始时间不触发任何动作 */
        private String endTime;
        /** 每日奖励发放时间 */
        private String awardTime;
        /** 行为及扣血 */
        private Map<EActionType, Integer> actHps;

        @Override
        public void initExec(RowData row) {
            IDListTuple2<Integer, String, Integer> ltp =
                    ParseListColumnUtil.parse(row, toInt, "actName", toStr, "actHp", toInt);
            Map<EActionType, Integer> actHps = new EnumMap<>(EActionType.class);
            for (IDTuple2<Integer, String, Integer> tp : ltp.getTuples().values()) {
                actHps.put(EActionType.convertByName(tp.getE1()), tp.getE2());
            }
            this.actHps = actHps;
        }

        public AllStarBean build(Collection<NpcBean> npcsc, Collection<AwardBean> awardsc) {
            ImmutableMap<Integer, NpcBean> npcs = convertNpcs(npcsc);
            ImmutableList<AwardBean> awards = convertAward(awardsc);
//            return new AllStarBean(id,
//                    teamLev,
//                    LocalTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_TIME),
//                    LocalTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_TIME),
//                    LocalTime.parse(awardTime, DateTimeFormatter.ISO_LOCAL_TIME),
//                    ImmutableMap.copyOf(actHps),
//                    npcs,
//                    awards);
            return new AllStarBean(id,
                    teamLev,
                    npcs,
                    awards);
        }

        public int getId() {
            return id;
        }

        private ImmutableList<AwardBean> convertAward(Collection<AwardBean> awardsc) {
            return awardsc.stream()
                    .sorted(Comparator.comparing(o -> o.rank)).collect(ImmutableList.toImmutableList());
        }

        private ImmutableMap<Integer, NpcBean> convertNpcs(Collection<NpcBean> npcsc) {
            return npcsc.stream().collect(ImmutableMap.toImmutableMap(NpcBean::getLev, Function.identity()));
        }
    }

    /** npc配置 */
    public static final class AllStarNpcBuilder extends AbstractExcelBean {
        /** 关联的全明星id */
        private int id;
        /** 全明星npc配置. npc id */
        private int npcId;
        /** 难度级别 */
        private int lev;
        /** 最大血量 */
        private int maxHp;
        /** 当前难度比赛结束时跨难度扣除的血量削减比例 */
        private float xLevRate;
        /** 击杀奖励*/
        private String PersonAward;
        /** 本服击杀奖励*/
        private String currAward;

        @Override
        public void initExec(RowData row) {
        }

        public NpcBean build() {
            return new NpcBean(id, npcId, lev, maxHp, xLevRate,PersonAward,currAward);
        }

        public int getId() {
            return id;
        }
    }
    
    public static final class PersonalAwardBuilder extends AbstractExcelBean {
      
      private int Score;
      private String  Award;
      
      @Override
      public void initExec(RowData row) {
        
      }

      public int getScore() {
        return Score;
      }

      public String getAward() {
        return Award;
      }

    }

    /** 奖励配置 */
    public static final class AllStarAwardBuilder extends AbstractExcelBean {
        /** 关联的全明星id */
        private int id;
        /** 排名区间 */
        private int min;
        private int max;
        /** 奖励(道具) */
        private String props;
        /** 奖励(掉落) */
        private int drop;

        @Override
        public void initExec(RowData row) {
        }

        public AwardBean build() {
            return new AwardBean(id,
                    new IntervalInt(min, max),
                    PropSimple.parseItems(props),
                    drop);
        }

        public int getId() {
            return id;
        }
    }

}
