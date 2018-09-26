package com.ftkj.console;

import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.cfg.battle.AIBean.AIGroupBuilder;
import com.ftkj.cfg.battle.AIBean.AIPlayerRuleBean;
import com.ftkj.cfg.battle.AIBean.AIPlayerRuleBuilder;
import com.ftkj.cfg.battle.AIBean.AIPlayerRuleResp;
import com.ftkj.cfg.battle.AIBean.AIRuleGroupBean;
import com.ftkj.cfg.battle.AIBean.AIRuleResp;
import com.ftkj.cfg.battle.AIBean.CoachRuleBean;
import com.ftkj.cfg.battle.AIBean.CoachRuleBuilder;
import com.ftkj.cfg.battle.AIBean.SubsituteRuleBean;
import com.ftkj.cfg.battle.AIBean.TacticRuleBean;
import com.ftkj.cfg.battle.AIBean.TacticRuleBuilder;
import com.ftkj.cfg.battle.AIRuleActionConditionBean.AIActCondBean;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * AI配置
 */
public class AIConsole extends AbstractConsole implements ValidateBean {
    private static final Logger log = LoggerFactory.getLogger(AIConsole.class);
    /** map[groupId, Bean] */
    private static Map<Integer, AIRuleGroupBean> aiGroups = Collections.emptyMap();

    public static void init() {
        Map<Integer, AIGroupBuilder> groupBuilders = toMap(CM.aiGroups, b -> b.getId(), b -> b);
        Map<Integer, AIPlayerRuleBuilder> skillRules = toMap(CM.aiSkillRules, b -> b.getId(), b -> b);
        Map<Integer, AIPlayerRuleResp> skillPosResps = toMap(CM.aiSkillResps, b -> b.getId(), b -> b.build());
        Map<Integer, CoachRuleBuilder> coachRules = toMap(CM.aiCoachRules, b -> b.getId(), b -> b);
        Map<Integer, AIActCondBean> actConds = toMap(CM.aiCoachActConds, b -> b.getId(), b -> b.build());//当前版本不支持
        Map<Integer, CoachRuleBean.Resp> coachResps = toMap(CM.aiCoachResps, b -> b.getId(), b -> b.build());
        Map<Integer, TacticRuleBuilder> tacticRules = toMap(CM.aiTacticRules, b -> b.getId(), b -> b);
        Map<Integer, TacticRuleBean.Resp> tacticResps = toMap(CM.aiTacticResps, b -> b.getId(), b -> b.build());
        Map<Integer, SubsituteRuleBean> subRules = toMap(CM.aiSubRules, b -> b.getId(), b -> b.build());

        ImmutableMap.Builder<Integer, AIRuleGroupBean> aiGroups = ImmutableMap.builder();
        for (AIGroupBuilder gb : groupBuilders.values()) {
            AIRuleGroupBean agb = new AIRuleGroupBean(gb.getId(),
                    gb.getAiLev(),
                    gb.getSkillLev(),
                    buildAIPlayerSkillRules(gb, skillRules, skillPosResps),
                    gb.getCoachIds(),
                    buildAICoachRules(gb, coachRules, actConds, coachResps),
                    buildAITacticRules(gb, tacticRules, tacticResps),
                    buildAITacticRules(gb, subRules));

            aiGroups.put(agb.getId(), agb);
        }

        AIConsole.aiGroups = aiGroups.build();
    }

    /** 生成当前难度的换人规则 */
    private static ImmutableList<SubsituteRuleBean> buildAITacticRules(AIGroupBuilder gb,
                                                                       Map<Integer, SubsituteRuleBean> rulebs) {
        ImmutableList.Builder<SubsituteRuleBean> rulesByLev = ImmutableList.builder();
        for (Integer ruleId : gb.getSubRules()) {
            SubsituteRuleBean rb = rulebs.get(ruleId);
            if (rb == null) {
                continue;
            }
            if (rb.getResp().containsKey(gb.getAiLev())) {
                rulesByLev.add(rb);
            }
        }
        return rulesByLev.build();
    }

    /** 生成当前难度的战术规则 */
    private static ImmutableList<TacticRuleBean> buildAITacticRules(AIGroupBuilder gb,
                                                                    Map<Integer, TacticRuleBuilder> rulebs,
                                                                    Map<Integer, TacticRuleBean.Resp> resps) {
        ImmutableList.Builder<TacticRuleBean> rulesByLev = ImmutableList.builder();
        for (Integer ruleId : gb.getTacticRules()) {
            TacticRuleBuilder rb = rulebs.get(ruleId);
            if (rb == null) {
                continue;
            }
            TacticRuleBean.Resp ruleResps = getRuleRespByAiLev(gb.getAiLev(), rb.getResps(), resps);
            if (ruleResps == null) {
                throw exception("ai配置, 组 %s 战术规则 %s 难度 %s 的响应没有配置", gb.getId(),
                        ruleId, gb.getAiLev());
            }
            rulesByLev.add(rb.build(ruleResps));
        }
        return rulesByLev.build();
    }

    /** 生成当前难度的教练规则 */
    private static ImmutableList<CoachRuleBean> buildAICoachRules(AIGroupBuilder gb,
                                                                  Map<Integer, CoachRuleBuilder> coachRules,
                                                                  Map<Integer, AIActCondBean> actConds,
                                                                  Map<Integer, CoachRuleBean.Resp> coachResps) {
        ImmutableList.Builder<CoachRuleBean> coachRulesByLev = ImmutableList.builder();
        for (Integer ruleId : gb.getCoachRules()) {
            CoachRuleBuilder rb = coachRules.get(ruleId);
            if (rb == null) {
                continue;
            }
            CoachRuleBean.Resp ruleResps = getRuleRespByAiLev(gb.getAiLev(), rb.getResps(), coachResps);
            if (ruleResps == null) {
                throw exception("ai配置, 组 %s 技能规则 %s 难度 %s 的响应没有配置", gb.getId(),
                        ruleId, gb.getAiLev());
            }
            ImmutableSet<AIActCondBean> selfActCond = getActConds(actConds, rb.getSelfActCond());
            ImmutableSet<AIActCondBean> opponentActCond = getActConds(actConds, rb.getOpponentActCond());
            coachRulesByLev.add(rb.build(selfActCond, opponentActCond, ruleResps));
        }
        return coachRulesByLev.build();
    }

    private static ImmutableSet<AIActCondBean> getActConds(Map<Integer, AIActCondBean> actConds, ImmutableSet<Integer> condIds) {
        return condIds.stream().map(actConds::get)
                .filter(Objects::nonNull).collect(ImmutableSet.toImmutableSet());
    }

    /** 生成当前难度的技能规则 */
    private static ImmutableList<AIPlayerRuleBean> buildAIPlayerSkillRules(AIGroupBuilder gb,
                                                                           Map<Integer, AIPlayerRuleBuilder> skillRules,
                                                                           Map<Integer, AIPlayerRuleResp> skillPosResps) {
        ImmutableList.Builder<AIPlayerRuleBean> skillRulesByLev = ImmutableList.builder();
        for (Integer ruleId : gb.getSkillRules()) {
            AIPlayerRuleBuilder rb = skillRules.get(ruleId);
            if (rb == null) {
                continue;
            }
            AIPlayerRuleResp rr = getRuleRespByAiLev(gb.getAiLev(), rb.getResp(), skillPosResps);
            if (rr == null) {
                throw exception("ai配置, 组 %s 技能规则 %s 难度 %s 的响应没有配置", gb.getId(),
                        ruleId, gb.getAiLev());
            }
            skillRulesByLev.add(rb.build(rr));
        }
        return skillRulesByLev.build();
    }

    /** 根据 AI 难度获取规则回应 */
    private static <T extends AIRuleResp> T getRuleRespByAiLev(int aiLev, Collection<Integer> respIds, Map<Integer, T> ruleResps) {
        for (Integer respId : respIds) {
            T rr = ruleResps.get(respId);
            if (rr == null) {
                continue;
            }
            if (rr.getAiLevs().contains(aiLev)) {
                return rr;
            }
        }
        return null;
    }

    public static AIRuleGroupBean getBean(int aiGroupId) {
        return aiGroups.get(aiGroupId);
    }

    @Override
    public void validate() {
    }
}
