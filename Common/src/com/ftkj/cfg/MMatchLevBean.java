package com.ftkj.cfg;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ParseListColumnUtil;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple2;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple4;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple2;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple4;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 主线赛程关卡配置.
 */
public class MMatchLevBean extends AbstractExcelBean {
    /** 关卡id */
    private int id;
    /** 赛区id */
    private int divId;
    /** 类型(1:常规赛, 3:锦标赛) */
    private LevType type;
    /** 开启本关卡需要的上一个关卡id */
    private int enablePreId;
    /** 开启本关卡需要的上级关卡星级 */
    private int enablePreStar;
    /** 装备经验/秒(只有常规赛有效) */
    private int equipExp;
    /** 星级配置 */
    private Map<Integer, Star> stars = Collections.emptyMap();
    /** 常规赛关卡的NPC */
    private int npcId;
    /** 球队等级限制 */
    private int teamLevel;
    /** 本关卡(常规赛)进入锦标赛排行榜的球队数 */
    private int championRankNum;
    /** 锦标赛时在本关卡(常规赛)排行榜选择的对手数量(计算时按关卡顺序排除自己和已经存在的对手) */
    private int championTargetNum;
    /** 常规赛关联的锦标赛关卡 */
    private int assocChampionLev;
    /** 锦标赛关卡的替代npc */
    private List<Integer> championNpc = Collections.emptyList();
    /** 锦标赛关卡关联的常规赛关卡 */
    private Set<Integer> associateRegularLev = Collections.emptySet();
    /** 特定次数比赛特殊处理 */
    private Map<Integer, SpecialHandleMatch> specialHandle = Collections.emptyMap();
    /** 后续关卡 */
    private Set<Integer> nextLevs = new LinkedHashSet<>();

    public MMatchLevBean() {
    }

    public MMatchLevBean(int id, int csTargetNum) {
        this.id = id;
        this.championTargetNum = csTargetNum;
    }

    public String toSimpleString() {
        return "{" +
                "\"id\":" + id +
                ", \"divId\":" + divId +
                ", \"type\":" + type +
                ", \"preId\":" + enablePreId +
                ", \"preStar\":" + enablePreStar +
                '}';
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"divId\":" + divId +
                ", \"type\":" + type +
                ", \"preId\":" + enablePreId +
                ", \"preStar\":" + enablePreStar +
                ", \"equipExp\":" + equipExp +
                ", \"stars\":" + stars +
                ", \"npcId\":" + npcId +
                ", \"teamLevel\":" + teamLevel +
                ", \"csRNum\":" + championRankNum +
                ", \"csTNum\":" + championTargetNum +
                ", \"assocCsLev\":" + assocChampionLev +
                ", \"csNpc\":" + championNpc +
                ", \"assocRegularLev\":" + associateRegularLev +
                ", \"nextLevs\":" + nextLevs +
                '}';
    }

    @Override
    public void initExec(RowData row) {
        this.divId = getInt(row, "divId");
        this.teamLevel = getInt(row, "teamLev");
        this.assocChampionLev = getInt(row, "csLevId");
        this.championRankNum = getInt(row, "csTopNum");
        this.championTargetNum = getInt(row, "csTargetNum");
        this.type = LevType.convert(getInt(row, "type_"));
        this.championNpc = ImmutableList.copyOf(StringUtil.toIntegerArray(row.get("csNpc"), StringUtil.ST));

        //starNum2  starMatchData2  starItem2  starDrop2
        //星级2  星级2需要的比赛数据id  星级2的奖励(道具)  星级2的奖励(掉落)
        //int  string  string  int
        IDListTuple4<Integer, Integer, String, String, String> starIlt =
                ParseListColumnUtil.parse(row, toInt,
                        "starNum", toInt, "starCond", toStr, "starItem", toStr, "starDrop", toStr);
        Map<Integer, Star> ss = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));
        for (IDTuple4<Integer, Integer, String, String, String> tp : starIlt.getTuples().values()) {
            if (tp.getE1() <= 0) {
                continue;
            }
            ss.put(tp.getE1(),
                    new Star(tp.getE1(),
                            ImmutableList.copyOf(StringUtil.toIntegerArray(tp.getE2(), StringUtil.COMMA)),
                            PropSimple.parseItems(tp.getE3()),
                            ImmutableList.copyOf(StringUtil.toIntegerArray(tp.getE4(), StringUtil.COMMA))));
        }

        // shMatchNum1	shMatchStep1	shMatchSkillPower1	shMatchRoundPower1
        // 比赛特殊处理. 第几次比赛时特殊处理.	比赛特殊处理, 回合配置.在前面的配置的次数时使用此配置, 留空则使用默认配置.
        // 比赛特殊处理, 技能能量比率.在前面的配置的次数时使用此配置, 留空则使用默认配置.
        // 比赛特殊处理, 体力下降比率.在前面的配置的次数时使用此配置, 留空则使用默认配置.
        // int	string	float	float
        IDListTuple2<Integer, Integer, Integer> shIlt =
                ParseListColumnUtil.parse(row, toInt, "battleCustomNum", toInt, "battleCustomId", toInt);
        Map<Integer, SpecialHandleMatch> specialHandle = new HashMap<>();
        for (IDTuple2<Integer, Integer, Integer> tp : shIlt.getTuples().values()) {
            if (tp.getE1() <= 0) {
                continue;
            }
            specialHandle.put(tp.getE1(), new SpecialHandleMatch(tp.getE1(), tp.getE2()));
        }

        this.stars = ss;
        this.specialHandle = ImmutableMap.copyOf(specialHandle);
    }

    public int getId() {
        return id;
    }

    public int getDivId() {
        return divId;
    }

    public LevType getType() {
        return type;
    }

    public int getEnablePreId() {
        return enablePreId;
    }

    public int getEnablePreStar() {
        return enablePreStar;
    }

    public int getEquipExp() {
        return equipExp;
    }

    public Map<Integer, Star> getStars() {
        return stars;
    }

    public Star getStar(int star) {
        return stars.get(star);
    }

    public int getNpcId() {
        return npcId;
    }

    public int getTeamLevel() {
        return teamLevel;
    }

    public boolean isRegular() {
        return LevType.Regular.equals(type);
    }

    public boolean isChampionship() {
        return LevType.Championship.equals(type);
    }

    //    public boolean isElite() {
    //        return NodeType.Elite.equals(type);
    //    }

    public int getAssocChampionLev() {
        return assocChampionLev;
    }

    public int getChampionRankNum() {
        return championRankNum;
    }

    public int getChampionTargetNum() {
        return championTargetNum;
    }

    public Set<Integer> getAssociateRegularLev() {
        return associateRegularLev;
    }

    public void addAssociateRegularLev(int regularLev) {
        if (associateRegularLev == Collections.<Integer>emptySet()) {
            associateRegularLev = new HashSet<>();
        }
        associateRegularLev.add(regularLev);
    }

    public List<Integer> getChampionNpc() {
        return championNpc;
    }

    public Set<Integer> getNextLevs() {
        return nextLevs;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDivId(int divId) {
        this.divId = divId;
    }

    public void setType(LevType type) {
        this.type = type;
    }

    public void setEnablePreId(int enablePreId) {
        this.enablePreId = enablePreId;
    }

    public void setEnablePreStar(int enablePreStar) {
        this.enablePreStar = enablePreStar;
    }

    public void setChampionRankNum(int championRankNum) {
        this.championRankNum = championRankNum;
    }

    public void setAssocChampionLev(int assocChampionLev) {
        this.assocChampionLev = assocChampionLev;
    }

    public void setChampionNpc(List<Integer> championNpc) {
        this.championNpc = championNpc;
    }

    public void setAssociateRegularLev(Set<Integer> associateRegularLev) {
        this.associateRegularLev = associateRegularLev;
    }

    public void setNextLevs(Set<Integer> nextLevs) {
        this.nextLevs = nextLevs;
    }

    public Map<Integer, SpecialHandleMatch> getSpecialHandle() {
        return specialHandle;
    }

    public SpecialHandleMatch getSpecialHandle(int matchCount) {
        return specialHandle.get(matchCount);
    }

    /** 第几次比赛时对比赛进行特殊处理 */
    public static final class SpecialHandleMatch implements Serializable {
        private static final long serialVersionUID = 8761058475967926271L;
        /** 第几次 */
        private final int num;
        /** 自定义比赛配置 */
        private final int battleCustomId;

        public SpecialHandleMatch(int num, int battleCustomId) {
            this.num = num;
            this.battleCustomId = battleCustomId;
        }

        public int getNum() {
            return num;
        }

        public int getBattleCustomId() {
            return battleCustomId;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"num\":" + num +
                    ", \"battleCustomId\":" + battleCustomId +
                    '}';
        }
    }

    /** 关卡类型 */
    public enum LevType {
        /** 常规赛 */
        Regular(1),
        /** 2 精英 */
        //        Elite(2),
        /** 3. 锦标赛 */
        Championship(3);

        private final int type;

        LevType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static LevType convert(int type) {
            switch (type) {
                case 1: return Regular;
                //                case 2: return Elite;
                case 3: return Championship;
            }
            return null;
        }
    }

    /** 星级配置 */
    public static final class Star {
        /** 星级 */
        private final int star;
        /** 获得本星级需要的条件 */
        private final List<Integer> winConditionId;
        /** 星级的奖励(道具) */
        private final List<PropSimple> props;
        /** 星级的奖励(掉落) */
        private final ImmutableList<Integer> drops;

        public Star(int star, List<Integer> winConditionId, List<PropSimple> props, ImmutableList<Integer> drops) {
            this.star = star;
            this.winConditionId = winConditionId;
            this.props = props;
            this.drops = drops;
        }

        public int getStar() {
            return star;
        }

        public List<Integer> getWinConditionId() {
            return winConditionId;
        }

        public List<PropSimple> getProps() {
            return props;
        }

        public ImmutableList<Integer> getDrops() {
            return drops;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"star\":" + star +
                    ", \"cid\":" + winConditionId +
                    ", \"props\":" + props +
                    ", \"drop\":" + drops +
                    '}';
        }
    }

}
