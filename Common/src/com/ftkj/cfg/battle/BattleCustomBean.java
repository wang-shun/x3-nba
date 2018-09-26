package com.ftkj.cfg.battle;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.cfg.base.ParseListColumnUtil;
import com.ftkj.cfg.base.ParseListColumnUtil.IDListTuple2;
import com.ftkj.cfg.base.ParseListColumnUtil.IDTuple2;
import com.ftkj.cfg.battle.BaseBattleActionBean.RoundBuilder;
import com.ftkj.cfg.battle.BaseBattleBean.BattleBuilder;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleAction;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.ftkj.util.lambda.LBFloat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 策划自定义比赛配置
 *
 * @author luch
 */
public final class BattleCustomBean {

    /** 策划自定义比赛配置 */
    public static final class CustomBean {
        private final BaseBattleBean baseBean;
        /** 自定义比赛. id */
        private final int id;
        /** 比赛从第几回合开始 */
        private final int startRound;
        /** 比赛开始时的延迟 */
        private final int startDelay;
        /** 比赛开始时主场状态 */
        private final TeamStatBean homeInit;
        /** 比赛开始时主场状态 */
        private final TeamStatBean homeEnd;
        /** 比赛结束时客场状态 */
        private final TeamStatBean awayInit;
        /** 比赛结束时客场状态 */
        private final TeamStatBean awayEnd;
        /** 回合规则列表. “;”分隔. map[round, Bean] */
        private final Map<Integer, CustomRoundBean> roundActs;

        public CustomBean(BaseBattleBean baseBean,
                          int id,
                          int startRound,
                          int startDelay,
                          TeamStatBean homeInit,
                          TeamStatBean homeEnd,
                          TeamStatBean awayInit,
                          TeamStatBean awayEnd,
                          Map<Integer, CustomRoundBean> roundActs) {

            this.baseBean = baseBean;
            this.id = id;
            this.startRound = startRound;
            this.startDelay = startDelay;
            this.homeInit = homeInit;
            this.homeEnd = homeEnd;
            this.awayInit = awayInit;
            this.awayEnd = awayEnd;
            this.roundActs = roundActs;
        }

        public BaseBattleBean getBase() {
            return baseBean;
        }

        public int getId() {
            return id;
        }

        public int getStartRound() {
            return startRound;
        }

        public int getStartDelay() {
            return startDelay;
        }

        public TeamStatBean getHomeInit() {
            return homeInit;
        }

        public TeamStatBean getHomeEnd() {
            return homeEnd;
        }

        public TeamStatBean getAwayInit() {
            return awayInit;
        }

        public TeamStatBean getAwayEnd() {
            return awayEnd;
        }

        public Map<Integer, CustomRoundBean> getRoundActs() {
            return roundActs;
        }

        public CustomRoundBean getRoundAct(int round) {
            return roundActs.get(round);
        }

        @Override
        public String toString() {
            return "CustomBean{" +
                    "\"id\":" + id +
                    ", \"startRound\":" + startRound +
                    ", \"startDelay\":" + startDelay +
                    ", \"homeInit\":" + homeInit +
                    ", \"homeEnd\":" + homeEnd +
                    ", \"awayInit\":" + awayInit +
                    ", \"awayEnd\":" + awayEnd +
                    ", \"roundActs\":" + roundActs +
                    '}';
        }
    }

    public static final class CustomBuilder extends BattleBuilder {
        /** 自定义比赛. id */
        private int id;
        /** 比赛从第几回合开始 */
        private int startRound;
        /** 比赛开始时的延迟 */
        private int startDelay;
        /** 比赛开始时主场状态 */
        private int homeInit;
        /** 比赛开始时主场状态 */
        private int homeEnd;
        /** 比赛结束时客场状态 */
        private int awayInit;
        /** 比赛结束时客场状态 */
        private int awayEnd;
        /** 回合规则列表. “;”分隔 */
        private ImmutableSet<Integer> roundActs;

        @Override
        public void initExec(RowData row) {
            super.initExec(row);
            roundActs = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "roundAct")));
        }

        public int getId() {
            return id;
        }

        public CustomBean build(TeamStatBean homeInit,
                                TeamStatBean homeEnd,
                                TeamStatBean awayInit,
                                TeamStatBean awayEnd,
                                ImmutableMap<Integer, CustomRoundBean> roundActs) {
            return new CustomBean(buildBase(),
                    id, startRound, startDelay,
                    homeInit, homeEnd, awayInit, awayEnd, roundActs);
        }

        public int getHomeInit() {
            return homeInit;
        }

        public int getHomeEnd() {
            return homeEnd;
        }

        public int getAwayInit() {
            return awayInit;
        }

        public int getAwayEnd() {
            return awayEnd;
        }

        public ImmutableSet<Integer> getRoundActs() {
            return roundActs;
        }
    }

    public static final class CustomRoundBean implements RoundBean {
        private static final long serialVersionUID = 3581442474717999897L;
        /** 回合规则id */
        private final int id;
        /** 回合 */
        private final int round;
        /** 回合父行为类型 */
        private final EBattleAction mainAction;
        /** 本行为执行后增加的动画轮数. 时长 = 轮数 * 200ms */
        private final int postRoundDelay;
        /** 执行技能后增加的动画轮数. 时长 = 轮数 * 200ms */
        private final int skillRoundDelay;
        /** 触发此行为的球队. home : 主场, away : 客场 */
        private final HomeAway homeAway;
        /** 子行为列表. 多个 “;”分隔 */
        private final ImmutableList<CustomSubActionBean> subActions;
        /** 回合结束时主场球队状态 */
        private final TeamStatBean homeEnd;
        /** 回合结束时客场球队状态 */
        private final TeamStatBean awayEnd;
        /** 回合结束时主场球员状态 */
        private final ImmutableList<PlayerStatBean> homePlayerStat;
        /** 回合结束时技能值 */
        private final boolean homeSkillPower;
        /** 回合结束时体力值 */
        private final boolean homePlayerPower;
        /** 回合结束时客场球员状态 */
        private final ImmutableList<PlayerStatBean> awayPlayerStat;
        /** 回合结束时技能值 */
        private final boolean awaySkillPower;
        /** 回合结束时体力值 */
        private final boolean awayPlayerPower;

        public CustomRoundBean(int id, int round,
                               EBattleAction mainAction,
                               int postRoundDelay,
                               int skillRoundDelay,
                               HomeAway homeAway,
                               ImmutableList<CustomSubActionBean> subActions,
                               TeamStatBean homeEnd,
                               TeamStatBean awayEnd,
                               ImmutableList<PlayerStatBean> homePlayerStat,
                               ImmutableList<PlayerStatBean> awayPlayerStat) {
            this.id = id;
            this.round = round;
            this.mainAction = mainAction;
            this.postRoundDelay = postRoundDelay;
            this.skillRoundDelay = skillRoundDelay;
            this.homeAway = homeAway;
            this.subActions = subActions;
            this.homeEnd = homeEnd;
            this.awayEnd = awayEnd;
            this.homePlayerStat = homePlayerStat;
            this.awayPlayerStat = awayPlayerStat;

            this.homeSkillPower = homePlayerStat.stream().anyMatch(ps -> ps.getSkillPower() != 0);
            this.homePlayerPower = homePlayerStat.stream().anyMatch(ps -> ps.getPlayerPower() != 0);

            this.awaySkillPower = awayPlayerStat.stream().anyMatch(ps -> ps.getSkillPower() != 0);
            this.awayPlayerPower = awayPlayerStat.stream().anyMatch(ps -> ps.getPlayerPower() != 0);
        }

        @Override
        public int getId() {
            return id;
        }

        public int getRound() {
            return round;
        }

        public EBattleAction getMainAction() {
            return mainAction;
        }

        @Override
        public int getPostRoundDelay() {
            return postRoundDelay;
        }

        @Override
        public int getSkillRoundDelay() {
            return skillRoundDelay;
        }

        public HomeAway getHomeAway() {
            return homeAway;
        }

        public List<CustomSubActionBean> getSubActions() {
            return subActions;
        }

        public TeamStatBean getHomeEnd() {
            return homeEnd;
        }

        public TeamStatBean getAwayEnd() {
            return awayEnd;
        }

        public ImmutableList<PlayerStatBean> getHomePlayerStat() {
            return homePlayerStat;
        }

        public ImmutableList<PlayerStatBean> getAwayPlayerStat() {
            return awayPlayerStat;
        }

        public boolean isHomeSkillPower() {
            return homeSkillPower;
        }

        public boolean isHomePlayerPower() {
            return homePlayerPower;
        }

        public boolean isAwaySkillPower() {
            return awaySkillPower;
        }

        public boolean isAwayPlayerPower() {
            return awayPlayerPower;
        }

        @Override
        public String toString() {
            return "RoundBean{" +
                    "\"id\":" + id +
                    ", \"round\":" + round +
                    ", \"mainAction\":" + mainAction +
                    ", \"homeAway\":" + homeAway +
                    ", \"subActions\":" + subActions +
                    ", \"homeEnd\":" + homeEnd +
                    ", \"awayEnd\":" + awayEnd +
                    ", \"homePlayerStat\":" + homePlayerStat +
                    ", \"homeSkillPower\":" + homeSkillPower +
                    ", \"homePlayerPower\":" + homePlayerPower +
                    ", \"awayPlayerStat\":" + awayPlayerStat +
                    ", \"awaySkillPower\":" + awaySkillPower +
                    ", \"awayPlayerPower\":" + awayPlayerPower +
                    '}';
        }
    }

    /** 回合规则 */
    public static final class CustomRoundBuilder extends RoundBuilder {
        /** 回合 */
        private int round;
        /** 回合父行为类型 */
        private String mainAction;
        /** 触发此行为的球队. home : 主场, away : 客场 */
        private String team;
        /** 回合结束时主场球队状态 */
        private int homeTeamStat;
        /** 回合结束时客场球队状态 */
        private int awayTeamStat;
        /** 回合结束时主场球员状态 */
        private ImmutableList<Integer> homePlayerStats;
        /** 回合结束时客场球员状态 */
        private ImmutableList<Integer> awayPlayerStats;

        @Override
        public void initExec(RowData row) {
            super.initExec(row);
            homePlayerStats = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "homePlayerStat"))).asList();
            awayPlayerStats = ImmutableSet.copyOf(StringUtil.toIntArrBySemicolon(getStr(row, "awayPlayerStat"))).asList();
        }

        @Override
        protected String getSubActName() {
            return "subAction";
        }

        public int getId() {
            return id;
        }

        public CustomRoundBean build(ImmutableList<CustomSubActionBean> subActions,
                                     TeamStatBean homeEnd, TeamStatBean awayEnd,
                                     ImmutableList<PlayerStatBean> homePlayerStat, ImmutableList<PlayerStatBean> awayPlayerStat) {
            return new CustomRoundBean(id,
                    round,
                    EBattleAction.convertByName(mainAction),
                    postDelay,
                    skillDelay,
                    HomeAway.convert(team),
                    subActions,
                    homeEnd,
                    awayEnd,
                    homePlayerStat,
                    awayPlayerStat);
        }

        public int getRound() {
            return round;
        }

        public ImmutableList<Integer> getSubActions() {
            return subActions;
        }

        public int getHomeTeamStat() {
            return homeTeamStat;
        }

        public int getAwayTeamStat() {
            return awayTeamStat;
        }

        public ImmutableList<Integer> getHomePlayerStats() {
            return homePlayerStats;
        }

        public ImmutableList<Integer> getAwayPlayerStats() {
            return awayPlayerStats;
        }
    }

    public static final class FindPlayerRule {
        /** 触发此行为的球员位置(和球员无关则不填) */
        private final EPlayerPosition pos;
        /** 触发此行为的场上球员的品质(品质优先,匹配场上第一个,找不到则使用位置)(和球员无关则不填) */
        private final EPlayerGrade quality;
        /** 触发此行为的场下球员的品质(对应的场上位置) */
        private final EPlayerGrade qualityReverse;

        public FindPlayerRule(EPlayerPosition pos, EPlayerGrade quality, EPlayerGrade qualityReverse) {
            this.pos = pos;
            this.quality = quality;
            this.qualityReverse = qualityReverse;
        }

        public EPlayerPosition getPos() {
            return pos;
        }

        public EPlayerGrade getQuality() {
            return quality;
        }

        public EPlayerGrade getQualityReverse() {
            return qualityReverse;
        }

        public boolean isAllNull() {
            return pos == null && quality == null && qualityReverse == null;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"pos\":" + pos +
                    ", \"quality\":" + quality +
                    ", \"qualityR\":" + qualityReverse +
                    '}';
        }
    }

    public static final class CustomSubActionBean extends BaseSubActionBean {
        private final FindPlayerRule findPlayerRule;

        public CustomSubActionBean(int id,
                                   EActionType subActionType,
                                   HomeAway homeAway,
                                   EPlayerPosition pos,
                                   EPlayerGrade quality,
                                   EPlayerGrade qualityReverse,
                                   int chance,
                                   int vi1,
                                   int vi2) {
            super(id, subActionType, chance, vi1, vi2, homeAway);
            findPlayerRule = (pos == null && quality == null && qualityReverse == null) ? null :
                    new FindPlayerRule(pos, quality, qualityReverse);
        }

        public FindPlayerRule getFindPlayerRule() {
            return findPlayerRule;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"rule\":" + findPlayerRule +
                    ", \"base\":" + super.toString() +
                    '}';
        }

        public static final class Builder extends BaseSubActionBean.Builder {
            /** 触发此行为的球员位置(和球员无关则不填) */
            private String pos;
            /** 触发此行为的场上球员的品质(品质优先,匹配场上第一个,找不到则使用位置)(和球员无关则不填) */
            private String quality;
            /** 触发此行为的场下球员的品质(对应的场上位置) */
            private String qualityReverse;

            @Override
            public void initExec(RowData row) {
                super.initExec(row);
            }

            @Override
            protected String getActionName() {
                return "subActionType";
            }

            @Override
            protected String getHomeAwayName() {
                return "team";
            }

            @Override
            public CustomSubActionBean build() {
                return new CustomSubActionBean(id,
                        action,
                        ha,
                        EPlayerPosition.convertByName(pos),
                        EPlayerGrade.convertByName(quality),
                        EPlayerGrade.convertByName(qualityReverse),
                        chance,
                        vi1,
                        vi2);
            }

            @Override
            public String toString() {
                return "{" +
                        "\"base\":\"" + super.toString() + "\"" +
                        ", \"pos\":\"" + pos + "\"" +
                        ", \"quality\":\"" + quality + "\"" +
                        ", \"qualityReverse\":\"" + qualityReverse + "\"" +
                        '}';
            }
        }
    }

    public static final class TeamStatBean {
        /** 球队状态 id */
        private final int id;
        /** 得分(>0时生效) */
        private final int score;
        /** 攻击(>0时有效) */
        private final int ocap;
        /** 防守(>0时有效) */
        private final int dcap;

        public TeamStatBean(int id, int score, int ocap, int dcap) {
            this.id = id;
            this.score = score;
            this.ocap = ocap;
            this.dcap = dcap;
        }

        public int getId() {
            return id;
        }

        public int getScore() {
            return score;
        }

        public int getOcap() {
            return ocap;
        }

        public int getDcap() {
            return dcap;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"id\":" + id +
                    ", \"score\":" + score +
                    ", \"ocap\":" + ocap +
                    ", \"dcap\":" + dcap +
                    '}';
        }
    }

    public static final class TeamStatBuilder extends AbstractExcelBean {
        /** 球队状态 id */
        private int id;
        /** 得分(>0时生效) */
        private int score;
        /** 攻击(>0时有效) */
        private int ocap;
        /** 防守(>0时有效) */
        private int dcap;

        @Override
        public void initExec(RowData row) {

        }

        public int getId() {
            return id;
        }

        public TeamStatBean build() {
            return new TeamStatBean(id, score, ocap, dcap);
        }
    }

    public static final class PlayerStatBean {
        /** 球员状态 id */
        private final int id;
        private final FindPlayerRule findPlayerRule;
        /** 回合结束时技能值 */
        private final int skillPower;
        /** 回合结束时体力值 */
        private final int playerPower;
        /** 行为数据 */
        private final Map<EActionType, LBFloat> actStats;

        public PlayerStatBean(int id,
                              EPlayerPosition pos,
                              EPlayerGrade quality,
                              EPlayerGrade qualityReverse,
                              int skillPower,
                              int playerPower,
                              Map<EActionType, LBFloat> actStats) {
            this.id = id;
            findPlayerRule = new FindPlayerRule(pos, quality, qualityReverse);
            this.skillPower = skillPower;
            this.playerPower = playerPower;
            this.actStats = actStats;
        }

        public int getId() {
            return id;
        }

        public FindPlayerRule getFindPlayerRule() {
            return findPlayerRule;
        }

        public int getSkillPower() {
            return skillPower;
        }

        public int getPlayerPower() {
            return playerPower;
        }

        public Map<EActionType, LBFloat> getActStats() {
            return actStats;
        }

        @Override
        public String toString() {
            return "PlayerStatBean{" +
                    "\"id\":" + id +
                    ", \"findPlayerRule\":" + findPlayerRule +
                    ", \"skillPower\":" + skillPower +
                    ", \"playerPower\":" + playerPower +
                    ", \"actStats\":" + actStats +
                    '}';
        }
    }

    public static final class PlayerStatBuilder extends AbstractExcelBean {
        /** 球员状态 id */
        private int id;
        /** 球员位置 */
        private String pos;
        /** 球员品质(品质优先,匹配场上第一个,找不到则使用位置)(可选) */
        private String lineupQuality;
        /** 触发此行为的场下球员的品质(对应的场上位置)(可选) */
        private String qualityReverse;
        /** 回合结束时技能值 */
        private int skillPower;
        /** 回合结束时体力值 */
        private int power;
        /** 行为数据 */
        private Map<EActionType, LBFloat> actStats = Collections.emptyMap();

        public int getId() {
            return id;
        }

        @Override
        public void initExec(RowData row) {
            //            actName1	actVal1
            //            行为名称	行为值
            //            string	float
            IDListTuple2<String, String, Float> ltp =
                    ParseListColumnUtil.parse(row, toStr, "actName", toStr, "actVal", toFloat);
            Map<EActionType, LBFloat> actStats = new EnumMap<>(EActionType.class);
            for (IDTuple2<String, String, Float> tp : ltp.getTuples().values()) {
                if (tp.getE1() == null || tp.getE1().isEmpty()) {
                    continue;
                }
                EActionType act = EActionType.convertByName(tp.getE1().toLowerCase());
                Objects.requireNonNull(act, "act name : " + tp.getE1());
                actStats.put(act, new LBFloat(tp.getE2()));
            }
            this.actStats = ImmutableMap.copyOf(actStats);
        }

        public PlayerStatBean build() {
            return new PlayerStatBean(id,
                    EPlayerPosition.convertByName(pos),
                    EPlayerGrade.convertByName(lineupQuality),
                    EPlayerGrade.convertByName(qualityReverse),
                    skillPower,
                    power,
                    actStats);
        }
    }

}
