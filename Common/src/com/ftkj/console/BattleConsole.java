package com.ftkj.console;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.ActionCondition;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.cfg.battle.BaseBattleActionBean;
import com.ftkj.cfg.battle.BaseSubActionBean;
import com.ftkj.cfg.battle.BattleActionBean;
import com.ftkj.cfg.battle.BattleActionsBean;
import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.cfg.battle.BattleBean.Builder;
import com.ftkj.cfg.battle.BattleHintBean;
import com.ftkj.cfg.battle.BattlePlayerPowerBean;
import com.ftkj.cfg.battle.BattleSkillPowerBean;
import com.ftkj.cfg.battle.GroupBean;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleAction;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.enums.battle.TacticZone;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 通用比赛配置.
 */
public class BattleConsole extends AbstractConsole implements ValidateBean {
    private static final Logger log = LoggerFactory.getLogger(BattleConsole.class);
    /** 赛季首次奖励配置. map[id, Bean] */
    private static Map<EBattleType, BattleBean> battles = Collections.emptyMap();
    /** 回合技能能量变化配置 */
    private static Map<Integer, BattleSkillPowerBean> skillPowers = Collections.emptyMap();
    /** 回合体力变化配置 */
    private static Map<Integer, BattlePlayerPowerBean> playerRoundPowers = Collections.emptyMap();
    /** 提示配置. map[id, Bean] */
    private static Map<Integer, BattleHintBean> hints = Collections.emptyMap();
    private static BattleBean defaultBattleBean;

    public static void init() {
        Map<Integer, GroupBean> actionGroups = toMap(CM.battleActionGroups, b -> b.getGroupId(), b -> b.build());
        Map<Integer, BaseSubActionBean> subActions = toMap(CM.battleSubActions, b -> b.getId(), b -> b.build());
        Map<Integer, BattleSkillPowerBean> skillPowers = toMap(CM.battleSkillPowers, b -> b.getGroupId(), b -> b.build());
        Map<Integer, BattlePlayerPowerBean> playerPowers = toMap(CM.battlePlayerPowers, b -> b.getGroupId(), b -> b.build());
        Map<Integer, BattleHintBean> hints = toMap(CM.battleHints, b -> b.getId(), b -> b.build());
        Map<Integer, GroupBean> hintGroups = toMap(CM.battleHintGroups, b -> b.getGroupId(), b -> b.build());//提示分组配置. map[id, Bean]
        ImmutableMap<Integer, BaseBattleActionBean> actions = buildActionBeans(subActions);

        Map<EBattleType, BattleBean> battles = new EnumMap<>(EBattleType.class);
        for (BattleBean.Builder builder : CM.battles) {
            BattleActionsBean bab = buildBattleActionsBean(builder, actionGroups, actions);
            GroupBean hintGroupBean = hintGroups.get(builder.getHintGroup());
            ImmutableMap<EActionType, ImmutableSet<Integer>> sts = buildHints(hintGroupBean, hints);
            BattleBean bb = builder.build(bab, hintGroupBean, sts);
            if (bb.getType() == null) {
                log.warn("battle config. null battle type {}", builder.getType());
                continue;
            }
            battles.put(bb.getType(), bb);

            //children
            for (Integer childType : builder.getChildren()) {
                EBattleType ct = EBattleType.getBattleType(childType);
                if (ct == null) {
                    log.warn("battle config. null battle type {}", childType);
                    continue;
                }
                battles.put(ct, bb);
            }
        }

        BattleConsole.battles = ImmutableMap.copyOf(battles);
        BattleConsole.playerRoundPowers = ImmutableMap.copyOf(playerPowers);
        BattleConsole.skillPowers = ImmutableMap.copyOf(skillPowers);
        BattleConsole.defaultBattleBean = battles.get(EBattleType.普通比赛);
        BattleConsole.hints = ImmutableMap.copyOf(hints);
        log.debug("bb {} hints {}", battles.size(), hints.size());
    }

    /** 生成父类型配置 */
    private static ImmutableMap<Integer, BaseBattleActionBean> buildActionBeans(Map<Integer, BaseSubActionBean> subActions) {
        ImmutableMap.Builder<Integer, BaseBattleActionBean> map = ImmutableMap.builder();
        for (BaseBattleActionBean.Builder builder : CM.battleActions) {
            ImmutableList<BaseSubActionBean> subactions = builder.getSubActions().stream()
                    .map(subActions::get).collect(toImmutableList());
            BaseBattleActionBean acb = builder.build(subactions);
            map.put(acb.getId(), acb);
        }
        return map.build();
    }

    /** 生成父类型配置 */
    private static BattleActionsBean buildBattleActionsBean(Builder btb, Map<Integer, GroupBean> actionGroups,
                                                            ImmutableMap<Integer, BaseBattleActionBean> actions) {
        GroupBean actionGroup = actionGroups.get(btb.getActionGroup());
        if (actionGroup == null) {
            throw exception("比赛配置. 比赛 %s 的行为分组 %s 没有配置", btb.getType(), btb.getActionGroup());
        }
        ImmutableMap<EBattleAction, BattleActionBean> acts = actionGroup.getElements().stream()
                .map(actions::get)
                .collect(toImmutableMap(b -> b.getAction(), b -> b));
        return new BattleActionsBean(acts);
    }

    /** 生成 提示次数 配置 */
    private static ImmutableMap<EActionType, ImmutableSet<Integer>>
    buildHints(GroupBean hintGroupBean, Map<Integer, BattleHintBean> hints) {
        Map<EActionType, Set<Integer>> actAndIds = new EnumMap<>(EActionType.class);
        for (Integer hid : hintGroupBean.getElements()) {
            BattleHintBean hb = hints.get(hid);
            if (hb == null) {
                continue;
            }
            for (Map.Entry<EActionType, ActionCondition> e : hb.getConditions().entrySet()) {
                actAndIds.computeIfAbsent(e.getKey(), act -> new HashSet<>())
                        .add(hb.getId());
            }
        }
        ImmutableMap.Builder<EActionType, ImmutableSet<Integer>> map = ImmutableMap.builder();
        actAndIds.forEach((k, v) -> map.put(k, ImmutableSet.copyOf(v)));
        return map.build();
    }

    @Override
    public void validate() {
        BattleBean dbb = BattleConsole.defaultBattleBean;
        int did = EBattleType.普通比赛.getId();
        if (dbb == null) {
            throw exception("比赛配置. 通用比赛类型 %s 没有配置", did);
        }
        if (BattleConsole.getSkillPowers(dbb.getBaseBean().getSkillPowerBean()) == null) {
            throw exception("比赛配置. 通用比赛类型 %s 回合技能能量变化 没有配置", did);
        }
        if (BattleConsole.getPlayerRoundPowers(dbb.getBaseBean().getPlayerPowerBean()) == null) {
            throw exception("比赛配置. 通用比赛类型 %s 球员回合体力变化 没有配置", did);
        }
        for (BattleBean info : battles.values()) {
            validate(info);
        }
    }

    private void validate(BattleBean info) {
        int id = info.getType().getId();
        for (Integer hid : info.getHintGroup().getElements()) {
            if (hints.get(hid) == null) {
                throw exception("比赛配置. 比赛 %s 的提示 %s 没有配置", id, hid);
            }
        }
        DropConsole.validate(BattleBean.getDropIds(info.getWinDrop()), "比赛配置. 比赛 %s 胜利奖励", id);
        DropConsole.validate(BattleBean.getDropIds(info.getLoseDrop()), "比赛配置. 比赛 %s 失败奖励", id);
        PropConsole.validate(info.getWinProps(), "比赛配置. 比赛 %s 胜利奖励", id);
        PropConsole.validate(info.getLoseProps(), "比赛配置. 比赛 %s 失败奖励", id);
    }

    public static BattleBean getDefault() {
        return BattleConsole.defaultBattleBean;
    }

    public static BattleBean getBattle(EBattleType type) {
        return battles.get(type);
    }

    public static BattleBean getBattleOrDefault(EBattleType type) {
        BattleBean bb = battles.get(type);
        return bb != null ? bb : BattleConsole.defaultBattleBean;
    }

    public static Map<EBattleType, BattleBean> getBattles() {
        return battles;
    }

    public static BattleSkillPowerBean getSkillPowers(int skillPowerGroup) {
        return skillPowers.get(skillPowerGroup);
    }

    public static BattlePlayerPowerBean getPlayerRoundPowers(int playerRoundGroup) {
        return playerRoundPowers.get(playerRoundGroup);
    }

    public static Map<Integer, BattleHintBean> getHints() {
        return hints;
    }

    public static BattleHintBean getHint(int id) {
        return hints.get(id);
    }

    public static BattleActionBean getActionBean(BattleActionsBean actions, TacticZone type, boolean sucessAction, Random random) {
        List<BattleActionBean> beans = sucessAction ? actions.getSuccessActions() : actions.getFailActions();
        Map<TacticZone, Integer> weights = sucessAction ? actions.getSuccessWeight() : actions.getSuccessWeight();
        final int weight = weights.get(type);
        final int ran = random.nextInt(weight);
        BattleActionBean bean;
        if (type == TacticZone.inside) {
            bean = findBean(beans, ran, b -> b.getInsideTacticsWeight());
        } else if (type == TacticZone.outside) {
            bean = findBean(beans, ran, b -> b.getOuterTacticsWeight());
        } else {
            bean = findBean(beans, ran, b -> b.getNormalTacticsWeight());
        }
        if (bean == null) {
            log.debug("btconsole getActionBean null. type {} succstat {} random {} weight {}", type, sucessAction, ran, weight);
            bean = beans.get(0);
        }
        log.debug("btconsole getActionBean. type {} succstat {} random {} weight {}", type, sucessAction, ran, weight);
        return bean;
    }

    private static BattleActionBean findBean(List<BattleActionBean> beans, int ran,
                                             Function<BattleActionBean, Integer> getWeight) {
        int start = 0;
        int end = 0;
        for (BattleActionBean bean : beans) {
            end += getWeight.apply(bean);
            if (ran >= start && ran < end) {
                return bean;
            }
            start = end;
        }
        return null;
    }

    //    private static Map<EBattleAction, EBattleStep> actionAndSteps = new EnumMap<>(EBattleAction.class);
    private static Map<EBattleStep, EBattleAction> stepsAndAction = new EnumMap<>(EBattleStep.class);

    static {
        //        actionAndSteps.put(EBattleAction.A_开场, EBattleStep.Start);
        //        actionAndSteps.put(EBattleAction.A_第一小节, EBattleStep.First_Period);
        //        actionAndSteps.put(EBattleAction.A_第二小节, EBattleStep.Second_Period);
        //        actionAndSteps.put(EBattleAction.A_第三小节, EBattleStep.Thrid_Period);
        //        actionAndSteps.put(EBattleAction.A_第四小节, EBattleStep.Fourth_Period);
        //        actionAndSteps.put(EBattleAction.A_加时, EBattleStep.Overtime);
        //        actionAndSteps.put(EBattleAction.A_结束, EBattleStep.End);
        //        actionAndSteps.put(EBattleAction.A_关闭, EBattleStep.Closed);

        stepsAndAction.put(EBattleStep.Start, EBattleAction.A_开场);
        stepsAndAction.put(EBattleStep.First_Period, EBattleAction.A_第一小节);
        stepsAndAction.put(EBattleStep.Second_Period, EBattleAction.A_第二小节);
        stepsAndAction.put(EBattleStep.Thrid_Period, EBattleAction.A_第三小节);
        stepsAndAction.put(EBattleStep.Fourth_Period, EBattleAction.A_第四小节);
        stepsAndAction.put(EBattleStep.Overtime, EBattleAction.A_加时);
        stepsAndAction.put(EBattleStep.End, EBattleAction.A_结束);
        stepsAndAction.put(EBattleStep.Closed, EBattleAction.A_关闭);
    }

    public static EBattleAction getActionBySteps(EBattleStep step) {
        return stepsAndAction.get(step);
    }
}
