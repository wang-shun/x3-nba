package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ParseListColumnUtil;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple3;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple3;
import com.ftkj.cfg.battle.AIRuleActionConditionBean.AIActCondBean;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.ftkj.util.table.DefaultWeightList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * AI 配置
 */
public class AIBean3 {

    /** AI 分组 */
    public static final class AIRuleGroupBean implements Serializable {
        private static final long serialVersionUID = 8191560643699406780L;
        /** 分组id */
        private final int id;
        /** ai 难度 */
        private final int lev;
        /** 技能等级 */
        private final int skillLev;
        /** 所有球员技能规则 */
        private final ImmutableList<AIPlayerRuleBean> skillRules;
        /** 被动 球员技能规则.  map[对方技能id, 回应规则] map[skillId, Bean] */
        private final ImmutableMap<Integer, AIPlayerRuleBean> initiativePlayerRules;
        //        private final //TODO 技能规则分组
        /** 教练及权重 */
        private final DefaultWeightList coachs;
        private final ImmutableList<CoachRuleBean> coachRules;
        /** 被动 教练技能规则.  map[对方技能id, 回应规则] map[skillId, Bean] */
        private final ImmutableMap<Integer, CoachRuleBean> initiativeCoachRules;
        /** 战术规则 */
        private final ImmutableList<TacticRuleBean> tacticRules;
        /** 换人规则 */
        private final ImmutableList<SubsituteRuleBean> subsituteRules;
        /** 所有主动规则 */
        private final ImmutableList<AIRuleBean> passiveRules;

        public AIRuleGroupBean(int id, int lev,
                               int skillLev,
                               ImmutableList<AIPlayerRuleBean> playerRules,
                               DefaultWeightList coachs,
                               ImmutableList<CoachRuleBean> coachRules,
                               ImmutableList<TacticRuleBean> tacticRules,
                               ImmutableList<SubsituteRuleBean> subsituteRules) {
            this.id = id;
            this.lev = lev;
            this.skillLev = skillLev;
            this.skillRules = playerRules;

            Builder<AIRuleBean> passiveRules = ImmutableList.builder();
            Map<Integer, AIPlayerRuleBean> initiativePlayerRules = new HashMap<>();
            Map<Integer, CoachRuleBean> initiativeCoachRules = new HashMap<>();

            init(playerRules, passiveRules, initiativePlayerRules);
            init(coachRules, passiveRules, initiativeCoachRules);

            this.initiativePlayerRules = ImmutableMap.copyOf(initiativePlayerRules);
            this.coachs = coachs;
            this.coachRules = coachRules;
            this.initiativeCoachRules = ImmutableMap.copyOf(initiativeCoachRules);

            this.tacticRules = tacticRules;
            this.subsituteRules = subsituteRules;

            passiveRules.addAll(coachRules);
            passiveRules.addAll(tacticRules);
            passiveRules.addAll(subsituteRules);
            this.passiveRules = passiveRules.build();
        }

        private <T extends AIRuleBean> void init(ImmutableList<T> skillRules,
                                                 Builder<AIRuleBean> passiveRules,
                                                 Map<Integer, T> initiativeRules) {
            for (T sb : skillRules) {
                if (sb.getType() == AIRuleType.initiative) {
                    for (Integer skillId : sb.getOpponentIds()) {
                        initiativeRules.put(skillId, sb);
                    }
                } else if (sb.getType() == AIRuleType.passive) {
                    passiveRules.add(sb);
                }
            }
        }

        public int getId() {
            return id;
        }

        public int getLev() {
            return lev;
        }

        public int getSkillLev() {
            return skillLev;
        }

        public ImmutableList<AIPlayerRuleBean> getSkillRules() {
            return skillRules;
        }

        public ImmutableMap<Integer, AIPlayerRuleBean> getInitiativePlayerRules() {
            return initiativePlayerRules;
        }

        public DefaultWeightList getCoachs() {
            return coachs;
        }

        public ImmutableMap<Integer, CoachRuleBean> getInitiativeCoachRules() {
            return initiativeCoachRules;
        }

        public ImmutableList<AIRuleBean> getPassiveRules() {
            return passiveRules;
        }

        public ImmutableList<AIRuleBean> getRules() {
            return passiveRules;
        }

        public ImmutableList<TacticRuleBean> getTacticRules() {
            return tacticRules;
        }

        public ImmutableList<SubsituteRuleBean> getSubsituteRules() {
            return subsituteRules;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    ", \"ailev\":" + lev +
                    ", \"coachWeight\":" + coachs +
                    ", \"coach\":" + coachRules +
                    ", \"tactic\":" + tacticRules +
                    ", \"subsitute\":" + subsituteRules +
                    ", \"passiveRules\":" + passiveRules +
                    '}';
        }
    }

    /** 规则类型. 主动或被动 */
    public enum AIRuleType {
        /** 主动 */
        initiative,
        /** 被动 */
        passive;

        public static AIRuleType convert(String name) {
            if (name == null) {
                return null;
            }
            return valueOf(name);
        }
    }

    /** AI 行为类型 */
    public enum AIRuleAction {
        /** 球员技能 */
        Player,
        /** 教练技能 */
        Coach,
        /** 战术 */
        Tactic,
        /** 换人 */
        Subsitute
    }

    /** 通用规则 */
    public static class AIRuleBean implements Serializable {
        private static final long serialVersionUID = -5871113511076416335L;
        /** 触发次数. 无限次 */
        public static final int TRIGGER_UNLIMITED = -1;
        /** 规则id */
        private final int id;
        /** 主动或被动 */
        private final AIRuleType type;
        /** 触发次数. -1 : 无限次 */
        private final int triggerNum;
        /** 起始回合 */
        private final int roundStart;
        /** 最小回合周期 */
        private final int roundPeriodMin;
        /** 最大回合周期 */
        private final int roundPeriodMax;
        /** 触发概率 */
        private final float chance;

        private final boolean alwayTrigger;
        /** 忽略回应者的cd. 0或不填:不忽略; 1 : 忽略 */
        private final boolean ignoreCD;

        public AIRuleBean(int id,
                          AIRuleType type,
                          int triggerNum,
                          int roundStart,
                          int roundPeriodMin,
                          int roundPeriodMax,
                          float chance,
                          boolean ignoreCD) {
            this.id = id;
            this.type = type;
            this.triggerNum = triggerNum;
            this.roundStart = roundStart;
            this.roundPeriodMin = roundPeriodMin;
            this.roundPeriodMax = roundPeriodMax;
            this.chance = chance;
            this.alwayTrigger = Float.compare(chance, 1f) == 0;
            this.ignoreCD = ignoreCD;
        }

        public int getId() {
            return id;
        }

        public AIRuleType getType() {
            return type;
        }

        public int getTriggerNum() {
            return triggerNum;
        }

        public int getRoundStart() {
            return roundStart;
        }

        public int getRoundPeriodMin() {
            return roundPeriodMin;
        }

        public int getRoundPeriodMax() {
            return roundPeriodMax;
        }

        public float getChance() {
            return chance;
        }

        public boolean isIgnoreCD() {
            return ignoreCD;
        }

        /** 对手触发的技能id或者战术id */
        public Collection<Integer> getOpponentIds() {
            return Collections.emptyList();
        }

        public boolean isRoundPeriodFixed() {
            return roundPeriodMin == roundPeriodMax;
        }

        public boolean isAlwayTrigger() {
            return alwayTrigger;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    //                    ", \"num\":" + triggerNum +
                    ", \"rs\":" + roundStart +
                    ", \"rmin\":" + roundPeriodMin +
                    ", \"rmax\":" + roundPeriodMax +
                    ", \"chance\":" + chance +
                    ", \"ignore\":" + ignoreCD +
                    '}';
        }
    }

    /** 球员技能规则及回应 */
    public static final class AIPlayerRuleBean extends AIRuleBean {
        private static final long serialVersionUID = -9040140280453699466L;
        /** 对方释放的技能. map[SkillBean.id] */
        private final ImmutableSet<Integer> opponentSkills;
        /** 球员技能回应, 按球员位置回应 */
        private final AIPlayerRuleResp resp;

        public AIPlayerRuleBean(int id, AIRuleType type,
                                int triggerNum, int roundStart, int roundPeriodMin, int roundPeriodMax,
                                float chance, boolean ignoreCD,
                                ImmutableSet<Integer> opponentSkills,
                                AIPlayerRuleResp resp) {
            super(id, type, triggerNum, roundStart, roundPeriodMin, roundPeriodMax, chance, ignoreCD);
            this.opponentSkills = opponentSkills;
            this.resp = resp;
        }

        @Override
        public ImmutableSet<Integer> getOpponentIds() {
            return opponentSkills;
        }

        public AIPlayerRuleResp getResp() {
            return resp;
        }
    }

    /** 回应 */
    public static class AIRuleResp implements Serializable {
        private static final long serialVersionUID = 656875454411934300L;
        /** 回应规则. id */
        private final int id;
        /** 难度 */
        private final ImmutableSet<Integer> aiLevs;

        public AIRuleResp(int id, ImmutableSet<Integer> aiLevs) {
            this.id = id;
            this.aiLevs = aiLevs;
        }

        public int getId() {
            return id;
        }

        public ImmutableSet<Integer> getAiLevs() {
            return aiLevs;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    ", \"aiLevs\":" + aiLevs +
                    '}';
        }
    }

    /** 球员技能回应 */
    public static final class AIPlayerRuleResp extends AIRuleResp {
        private static final long serialVersionUID = 3447866724106654619L;
        /** 位置和技能 */
        private final ImmutableMap<EPlayerPosition, AIPlayerRuleRespPos> poss;

        public AIPlayerRuleResp(int id, ImmutableSet<Integer> aiLevs, ImmutableMap<EPlayerPosition, AIPlayerRuleRespPos> poss) {
            super(id, aiLevs);
            this.poss = poss;
        }

        public ImmutableMap<EPlayerPosition, AIPlayerRuleRespPos> getPoss() {
            return poss;
        }
    }

    /** 球员技能回应 位置和技能 */
    public static final class AIPlayerRuleRespPos implements Serializable {
        private static final long serialVersionUID = 8331785797367806552L;
        /** 球员位置 */
        private final EPlayerPosition pos;
        /** 进攻技能 */
        private final int offensiveSkill;
        /** 放手技能 */
        private final int defenseSkill;

        public AIPlayerRuleRespPos(EPlayerPosition pos, int offensiveSkill, int defenseSkill) {
            this.pos = pos;
            this.offensiveSkill = offensiveSkill;
            this.defenseSkill = defenseSkill;
        }

        public EPlayerPosition getPos() {
            return pos;
        }

        public int getOffensiveSkill() {
            return offensiveSkill;
        }

        public int getDefenseSkill() {
            return defenseSkill;
        }
    }

    /** 教练技能规则及回应 */
    public static final class CoachRuleBean extends AIRuleBean {
        private static final long serialVersionUID = -4562532367771262455L;
        /** 己方球队行为要求. 表 CoachCond 的 id 列表 */
        private final ImmutableSet<AIActCondBean> selfActCond;
        /** 对方球队行为要求. 表 CoachCond 的 id 列表 */
        private final ImmutableSet<AIActCondBean> opponentActCond;
        /** 对方释放的技能 */
        private final ImmutableSet<Integer> opponentSkills;
        /** 响应 */
        private final Resp resp;

        public CoachRuleBean(int id, AIRuleType type,
                             int triggerNum, int roundStart, int roundPeriodMin, int roundPeriodMax,
                             float chance, boolean ignoreCD,
                             ImmutableSet<AIActCondBean> selfActCond,
                             ImmutableSet<AIActCondBean> opponentActCond,
                             ImmutableSet<Integer> opponentSkills,
                             Resp resp) {
            super(id, type, triggerNum, roundStart, roundPeriodMin, roundPeriodMax, chance, ignoreCD);

            this.selfActCond = selfActCond;
            this.opponentActCond = opponentActCond;

            this.opponentSkills = opponentSkills;
            this.resp = resp;
        }

        public ImmutableSet<AIActCondBean> getSelfActCond() {
            return selfActCond;
        }

        public ImmutableSet<AIActCondBean> getOpponentActCond() {
            return opponentActCond;
        }

        @Override
        public ImmutableSet<Integer> getOpponentIds() {
            return opponentSkills;
        }

        public Resp getResp() {
            return resp;
        }

        /** 教练技能回应 */
        public static final class Resp extends AIRuleResp {
            private static final long serialVersionUID = 723281321956886985L;
            /** 回应的教练技能列表 */
            private final ImmutableSet<Integer> skills;

            public Resp(int id, ImmutableSet<Integer> aiLevs, ImmutableSet<Integer> skills) {
                super(id, aiLevs);
                this.skills = skills;
            }

            public ImmutableSet<Integer> getSkills() {
                return skills;
            }

            @Override
            public String toString() {
                return "{" +
                        "\"parent\":" + super.toString() +
                        "\"skills\":" + skills +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "{" +
                    "\"parent\":" + super.toString() +
                    ", \"resp\":" + resp +
                    '}';
        }
    }

    /** 战术规则及回应 */
    public static final class TacticRuleBean extends AIRuleBean {
        private static final long serialVersionUID = 6052439146913284030L;
        /** 对方使用的战术 */
        private final ImmutableSet<Integer> opponentTactic;
        /** 响应 */
        private final Resp resp;

        public TacticRuleBean(int id, AIRuleType type,
                              int triggerNum, int roundStart, int roundPeriodMin, int roundPeriodMax,
                              float chance, boolean ignoreCD,
                              ImmutableSet<Integer> opponentTactic,
                              Resp resp) {
            super(id, type, triggerNum, roundStart, roundPeriodMin, roundPeriodMax, chance, ignoreCD);
            this.opponentTactic = opponentTactic;
            this.resp = resp;
        }

        @Override
        public ImmutableSet<Integer> getOpponentIds() {
            return opponentTactic;
        }

        public Resp getResp() {
            return resp;
        }

        /** 战术回应 */
        public static final class Resp extends AIRuleResp {
            private static final long serialVersionUID = -2850064248429515633L;
            /** 回应双克权重 */
            private int dualWeight;
            /** 回应单克权重 */
            private int singleWeight;
            /** 随机回应的权重 */
            private int randomWeight;
            /** 权重 */
            private int totalWeight;

            public Resp(int id, ImmutableSet<Integer> aiLevs, int dualWeight, int singleWeight, int randomWeight) {
                super(id, aiLevs);
                this.dualWeight = dualWeight;
                this.singleWeight = singleWeight;
                this.randomWeight = randomWeight;
                this.totalWeight = dualWeight + singleWeight + randomWeight;
            }

            public int getDualWeight() {
                return dualWeight;
            }

            public int getSingleWeight() {
                return singleWeight;
            }

            public int getRandomWeight() {
                return randomWeight;
            }

            public int getTotalWeight() {
                return totalWeight;
            }

            @Override
            public String toString() {
                return "{" +
                        "\"parent\":" + super.toString() +
                        ", \"dualWeight\":" + dualWeight +
                        ", \"singleWeight\":" + singleWeight +
                        ", \"randomWeight\":" + randomWeight +
                        ", \"totalWeight\":" + totalWeight +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "{" +
                    "\"parent\":" + super.toString() +
                    ", \"resp\":" + resp +
                    '}';
        }
    }

    /** 换人规则及回应 */
    public static final class SubsituteRuleBean extends AIRuleBean {
        private static final long serialVersionUID = 8144130590277051400L;
        /** 自己场上球员体力小于 */
        private int lpPower;
        /** 自己替补球员体力大于 */
        private int subPower;
        /** 最多替换几个替补席上来. 格式:AI难度等级,换人数量; */
        private ImmutableMap<Integer, Integer> resp;

        public SubsituteRuleBean(int id, AIRuleType type,
                                 int triggerNum, int roundStart, int roundPeriodMin, int roundPeriodMax,
                                 float chance, boolean ignoreCD,
                                 int lpPower, int subPower,
                                 ImmutableMap<Integer, Integer> resp) {
            super(id, type, triggerNum, roundStart, roundPeriodMin, roundPeriodMax, chance, ignoreCD);
            this.lpPower = lpPower;
            this.subPower = subPower;
            this.resp = resp;
        }

        public int getLpPower() {
            return lpPower;
        }

        public int getSubPower() {
            return subPower;
        }

        public ImmutableMap<Integer, Integer> getResp() {
            return resp;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"parent\":" + super.toString() +
                    ", \"lpPower\":" + lpPower +
                    ", \"subPower\":" + subPower +
                    ", \"resp\":" + resp +
                    '}';
        }
    }

    /** AI 分组 */
    public static final class AIGroupBuilder extends AbstractExcelBean {
        /** AI分组id */
        private int id;
        /** 难度等级 */
        private int aiLev;
        /** 球员技能规则. “;”号分隔多个 */
        private ImmutableSet<Integer> skillRules;
        /** 球员技能等级 */
        private int skillLev;
        /** 教练列表. 格式:教练id,权重; */
        private DefaultWeightList coachIds;
        /** 教练技能规则. “;”号分隔多个 */
        private ImmutableSet<Integer> coachRules;
        /** 换人规则. “;”号分隔多个 */
        private ImmutableSet<Integer> subRules;
        /** 战术规则. “;”号分隔多个 */
        private ImmutableSet<Integer> tacticRules;

        @Override
        public void initExec(RowData row) {
            this.skillRules = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "skillRules_")));
            this.coachIds = DefaultWeightList.parse(getStr(row, "coachIds_"));
            this.coachRules = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "coachRules_")));
            this.subRules = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "subRules_")));
            this.tacticRules = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "tacticRules_")));
        }

        public int getId() {
            return id;
        }

        public int getAiLev() {
            return aiLev;
        }

        public ImmutableSet<Integer> getSkillRules() {
            return skillRules;
        }

        public int getSkillLev() {
            return skillLev;
        }

        public DefaultWeightList getCoachIds() {
            return coachIds;
        }

        public ImmutableSet<Integer> getCoachRules() {
            return coachRules;
        }

        public ImmutableSet<Integer> getSubRules() {
            return subRules;
        }

        public ImmutableSet<Integer> getTacticRules() {
            return tacticRules;
        }
    }

    /** 规则 */
    public static abstract class AIRuleBuilder extends AbstractExcelBean {
        /** 规则id */
        protected int id;
        /** 主动或被动 */
        protected String type;
        /** 触发次数.     -1 : 无限次 */
        int triggerNum;
        /** 起始回合 */
        int roundStart;
        /** 最小回合周期 */
        int roundPeriodMin;
        /** 最大回合周期 */
        int roundPeriodMax;
        /** 触发概率 */
        float chance;
        /** 忽略回应者的cd.  0或不填:不忽略; 1 : 忽略 */
        int ignoreCD;

        public int getId() {
            return id;
        }

        boolean isIgnoreCD() {
            return ignoreCD == 1;
        }

    }

    /** 球员技能规则 */
    public static final class AIPlayerRuleBuilder extends AIRuleBuilder {
        /** 对方释放的技能. map[SkillBean.id] */
        private ImmutableSet<Integer> opponentSkills;
        /** 球员技能回应 */
        private ImmutableSet<Integer> resp;

        @Override
        public void initExec(RowData row) {
            this.opponentSkills = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "opponent")));
            this.resp = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "resp")));
        }

        public ImmutableSet<Integer> getResp() {
            return resp;
        }

        public AIPlayerRuleBean build(AIPlayerRuleResp resp) {
            return new AIPlayerRuleBean(id, AIRuleType.convert(type), triggerNum,
                    roundStart, roundPeriodMin, roundPeriodMax, chance, isIgnoreCD(),
                    opponentSkills, resp);
        }
    }

    /** 球员技能回应 */
    public static final class AIPlayerRuleRespBuilder extends AbstractExcelBean {
        /** 球员技能回应规则. id */
        private int id;
        /** 难度 */
        private ImmutableSet<Integer> levs;
        /** 位置和技能 */
        private ImmutableMap<EPlayerPosition, AIPlayerRuleRespPos> posAndSkills;

        public int getId() {
            return id;
        }

        @Override
        public void initExec(RowData row) {
            this.levs = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "lev")));

            //            pos1	oId1	dId1
            //            球员位置	进攻技能id	防守技能id
            //            string	int	int
            IDListTuple3<String, String, Integer, Integer> ltp =
                    ParseListColumnUtil.parse(row, toStr, "pos", toStr, "oId", toInt, "dId", toInt);
            Map<EPlayerPosition, AIPlayerRuleRespPos> posResps = new EnumMap<>(EPlayerPosition.class);
            for (IDTuple3<String, String, Integer, Integer> tp : ltp.getTuples().values()) {
                if (tp.getE1() == null || tp.getE1().isEmpty()) {
                    continue;
                }
                EPlayerPosition pos = EPlayerPosition.valueOf(tp.getE1().toUpperCase());
                Objects.requireNonNull(pos, "pos name : " + tp.getE1());
                posResps.put(pos, new AIPlayerRuleRespPos(pos, tp.getE2(), tp.getE3()));
            }
            this.posAndSkills = ImmutableMap.copyOf(posResps);
        }

        public AIPlayerRuleResp build() {
            return new AIPlayerRuleResp(id, levs, posAndSkills);
        }
    }

    /** 教练规则 */
    public static final class CoachRuleBuilder extends AIRuleBuilder {
        //        /** 比赛行为要求. 表 CoachCond 的 id 列表 */
        //        private ImmutableSet<Integer> matchActCond;
        /** 己方球队行为要求. 表 CoachCond 的 id 列表 */
        private ImmutableSet<Integer> selfActCond;
        /** 对方球队行为要求. 表 CoachCond 的 id 列表 */
        private ImmutableSet<Integer> opponentActCond;
        /** 对方技能. “;”号分隔多个(留空表示没有限制) */
        private ImmutableSet<Integer> opponentSkills;
        /** 回应列表. “;”号分隔多个 */
        private ImmutableSet<Integer> resps;

        @Override
        public void initExec(RowData row) {
            //            this.matchActCond = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "matchActCond")));
            this.selfActCond = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "selfActCond")));
            this.opponentActCond = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "oActCond")));
            this.opponentSkills = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "opponent")));
            this.resps = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "resp")));
        }

        public ImmutableSet<Integer> getResps() {
            return resps;
        }

        public ImmutableSet<Integer> getSelfActCond() {
            return selfActCond;
        }

        public ImmutableSet<Integer> getOpponentActCond() {
            return opponentActCond;
        }

        public CoachRuleBean build(ImmutableSet<AIActCondBean> selfActCond,
                                   ImmutableSet<AIActCondBean> opponentActCond,
                                   CoachRuleBean.Resp resp) {
            return new CoachRuleBean(id, AIRuleType.convert(type), triggerNum,
                    roundStart, roundPeriodMin, roundPeriodMax, chance, isIgnoreCD(),
                    selfActCond,
                    opponentActCond,
                    opponentSkills,
                    resp);
        }

        /** 教练规则回应 */
        public static final class RespBuilder extends AbstractExcelBean {
            /** 回应规则. id */
            private int id;
            /** 难度 */
            private ImmutableSet<Integer> levs;
            /** 回应技能. “;”号分隔多个 */
            private ImmutableSet<Integer> skillIds;

            public int getId() {
                return id;
            }

            @Override
            public void initExec(RowData row) {
                this.levs = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "aiLev")));
                this.skillIds = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "skills")));
            }

            public CoachRuleBean.Resp build() {
                return new CoachRuleBean.Resp(id, levs, skillIds);
            }
        }
    }

    /** 战术规则 */
    public static final class TacticRuleBuilder extends AIRuleBuilder {
        /** 对方技能. “;”号分隔多个(留空表示没有限制) */
        private ImmutableSet<Integer> opponentSkills;
        /** 回应列表. “;”号分隔多个 */
        private ImmutableSet<Integer> resps;

        @Override
        public void initExec(RowData row) {
            this.opponentSkills = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "opponent")));
            this.resps = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "resp")));
        }

        public ImmutableSet<Integer> getResps() {
            return resps;
        }

        public TacticRuleBean build(TacticRuleBean.Resp resp) {
            return new TacticRuleBean(id, AIRuleType.convert(type), triggerNum,
                    roundStart, roundPeriodMin, roundPeriodMax, chance, isIgnoreCD(),
                    opponentSkills, resp);
        }

        /** 教练规则回应 */
        public static final class RespBuilder extends AbstractExcelBean {
            /** 回应规则. id */
            private int id;
            /** 难度 */
            private ImmutableSet<Integer> levs;
            /** 回应双克权重 */
            private int dual;
            /** 回应单克权重 */
            private int single;
            /** 随机回应的权重 */
            private int random;

            public int getId() {
                return id;
            }

            @Override
            public void initExec(RowData row) {
                this.levs = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "aiLev")));
            }

            public TacticRuleBean.Resp build() {
                return new TacticRuleBean.Resp(id, levs, dual, single, random);
            }
        }
    }

    /** 换人规则 */
    public static final class SubsituteRuleBuilder extends AIRuleBuilder {
        /** 自己场上球员体力小于 */
        private int lpPower;
        /** 自己替补球员体力大于 */
        private int subPower;
        /** 最多替换几个替补席上来. 格式:AI难度等级,换人数量; */
        private ImmutableMap<Integer, Integer> resps;

        @Override
        public void initExec(RowData row) {
            this.resps = ImmutableMap.copyOf(splitToIntMap(getStr(row, "resp")));
        }

        public SubsituteRuleBean build() {
            return new SubsituteRuleBean(id, AIRuleType.convert(type), triggerNum,
                    roundStart, roundPeriodMin, roundPeriodMax, chance, isIgnoreCD(),
                    lpPower, subPower, resps);
        }

    }

}
