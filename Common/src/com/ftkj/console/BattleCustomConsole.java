package com.ftkj.console;

import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.cfg.battle.BattleCustomBean.CustomBean;
import com.ftkj.cfg.battle.BattleCustomBean.CustomBuilder;
import com.ftkj.cfg.battle.BattleCustomBean.CustomRoundBean;
import com.ftkj.cfg.battle.BattleCustomBean.CustomRoundBuilder;
import com.ftkj.cfg.battle.BattleCustomBean.CustomSubActionBean;
import com.ftkj.cfg.battle.BattleCustomBean.PlayerStatBean;
import com.ftkj.cfg.battle.BattleCustomBean.TeamStatBean;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 策划自定义比赛配置
 */
public class BattleCustomConsole extends AbstractConsole implements ValidateBean {
    private static final Logger log = LoggerFactory.getLogger(BattleCustomConsole.class);
    private static Map<Integer, CustomBean> customs = Collections.emptyMap();

    public static void init() {
        Map<Integer, CustomSubActionBean> subActs = toMap(CM.bcSubActs, b -> b.getId(), b -> b.build());
        Map<Integer, TeamStatBean> teamStats = toMap(CM.bcTeamStats, b -> b.getId(), b -> b.build());
        Map<Integer, PlayerStatBean> prStats = toMap(CM.bcPrStats, b -> b.getId(), b -> b.build());

        ImmutableMap<Integer, CustomRoundBean> roundss = buildRounds(CM.bcRounds, subActs, teamStats, prStats);
        BattleCustomConsole.customs = buildCustoms(CM.battleCustoms, roundss, teamStats);
    }

    /** 构建回合规则 */
    private static ImmutableMap<Integer, CustomRoundBean> buildRounds(List<CustomRoundBuilder> rbs,
                                                                      Map<Integer, CustomSubActionBean> subActs,
                                                                      Map<Integer, TeamStatBean> teamStats,
                                                                      Map<Integer, PlayerStatBean> prStats) {
        Builder<Integer, CustomRoundBean> rounds = ImmutableMap.builder();
        for (CustomRoundBuilder rb : rbs) {
            int id = rb.getId();
            ImmutableList<CustomSubActionBean> subActions = rb.getSubActions().stream()
                    .map(check(subActs, "自定义比赛 回合 %s 的子行为", id)).collect(toImmutableList());
            TeamStatBean homeEnd = teamStats.get(rb.getHomeTeamStat());
            TeamStatBean awayEnd = teamStats.get(rb.getAwayTeamStat());
            ImmutableList<PlayerStatBean> homePlayerStat = rb.getHomePlayerStats().stream()
                    .map(check(prStats, "自定义比赛 回合 %s 的主场球员状态", id)).collect(toImmutableList());
            ImmutableList<PlayerStatBean> awayPlayerStat = rb.getAwayPlayerStats().stream()
                    .map(check(prStats, "自定义比赛 回合 %s 的客场球员状态", id)).collect(toImmutableList());
            CustomRoundBean r = rb.build(subActions, homeEnd, awayEnd, homePlayerStat, awayPlayerStat);
            rounds.put(r.getId(), r);
        }
        return rounds.build();
    }

    private static <T, R> Function<T, R> check(Map<T, R> map, String msg, Object... args) {
        return id -> {
            R r = map.get(id);
            if (r == null) {
                String str = String.format(msg, args);
                throw exception(str + " %s 没有配置", id);
            }
            return r;
        };
    }

    /** 构建自定义规则 */
    private static ImmutableMap<Integer, CustomBean> buildCustoms(List<CustomBuilder> cbs,
                                                                  ImmutableMap<Integer, CustomRoundBean> rounds,
                                                                  Map<Integer, TeamStatBean> teamStats) {
        Builder<Integer, CustomBean> customs = ImmutableMap.builder();
        for (CustomBuilder b : cbs) {
            int id = b.getId();
            TeamStatBean homeInit = teamStats.get(b.getHomeInit());
            TeamStatBean homeEnd = teamStats.get(b.getHomeEnd());
            TeamStatBean awayInit = teamStats.get(b.getAwayInit());
            TeamStatBean awayEnd = teamStats.get(b.getAwayEnd());
            ImmutableMap<Integer, CustomRoundBean> roundActs = b.getRoundActs().stream()
                    .map(check(rounds, "自定义比赛 %s 回合", id))
                    .collect(ImmutableMap.toImmutableMap(r -> r.getRound(), r -> r));

            CustomBean c = b.build(homeInit, homeEnd, awayInit, awayEnd, roundActs);
            customs.put(c.getId(), c);
        }
        return customs.build();
    }

    public static CustomBean getBean(int id) {
        return customs.get(id);
    }

    @Override
    public void validate() {
        for (CustomBean b : customs.values()) {
            validate(b);
        }
    }

    private void validate(CustomBean b) {
        int id = b.getId();
        if (b.getStartRound() > 0 && b.getBase().getSteps().getStepByRound(b.getStartRound()) == null) {
            throw exception("自定义比赛 %s 的起始回合 %s 超出小节的回合配置", id, b.getStartRound());
        }

    }
}
